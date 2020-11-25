package uy.gub.agesic.pdi.pys.push.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import uy.gub.agesic.pdi.common.utiles.XmlTransformer;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.backend.service.AlertaService;
import uy.gub.agesic.pdi.pys.backend.service.EntregaService;
import uy.gub.agesic.pdi.pys.backend.service.TopicoSuscriptorService;
import uy.gub.agesic.pdi.pys.common.util.Constants;
import uy.gub.agesic.pdi.pys.domain.Alerta;
import uy.gub.agesic.pdi.pys.domain.DeliveryMode;
import uy.gub.agesic.pdi.pys.domain.Entrega;
import uy.gub.agesic.pdi.pys.domain.EstadoEntrega;
import uy.gub.agesic.pdi.pys.domain.TopicoSuscriptor;
import uy.gub.agesic.pdi.pys.push.config.PYSPushProperties;
import uy.gub.agesic.pdi.pys.push.exceptions.SleepTimeException;
import uy.gub.agesic.pdi.pys.push.send.AccessControl;
import uy.gub.agesic.pdi.pys.push.send.BusinessLogicExecutor;
import uy.gub.agesic.pdi.pys.push.send.PDIContext;
import uy.gub.agesic.pdi.pys.push.send.PushHAController;
import uy.gub.agesic.pge.exceptions.ConfigurationException;

import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.UUID;

public class TopicoSuscriptorSender implements BusinessLogicExecutor {

    private static final Logger logger = LoggerFactory.getLogger(TopicoSuscriptorSender.class);

    private static final String DURATION = "duration";

    private static final long MILLIS_DAY = 86400000;

    private AccessControl accessControl;

    private PushHAController haController;

    private String tsName;

    private String suscriptor;

    private String topico;

    private long tsDefaultSleepTime;

    private int pushCount;

    private long threadSleepTime;

    private boolean stop;

    private boolean checkTimeout = true;

    private PushThreadsController pushController;

    private EntregaService entregaService;

    private AlertaService alertaService;

    private TopicoSuscriptorService topicoSuscriptorService;

    private PDIContext pdiContext;

    private PYSPushProperties pysPushProperties;

    private boolean cacheEnabled;

    private String hostName;

    public TopicoSuscriptorSender(PushThreadsController pushController, EntregaService entregaService, PDIContext pdiContext,
                                  TopicoSuscriptorService topicoSuscriptorService, AlertaService alertaService, PYSPushProperties pysPushProperties, String hostName) {
        this.entregaService = entregaService;
        this.pdiContext = pdiContext;
        this.pushController = pushController;
        this.topicoSuscriptorService = topicoSuscriptorService;
        this.alertaService = alertaService;
        this.pysPushProperties = pysPushProperties;
        this.hostName = hostName;
    }

    public void destroy() {
        accessControl = null;
        pushController.removeTS(this.tsName);
        pushController = null;
        haController = null;
    }

    public void setAccessControl(AccessControl accessControl) {
        this.accessControl = accessControl;
    }

    public void setHaController(PushHAController haController) {
        this.haController = haController;
    }

    public void setTsName(String tsName) {
        this.tsName = tsName;
    }

    public void setTsDefaultSleepTime(long tsDefaultSleepTime) {
        this.tsDefaultSleepTime = tsDefaultSleepTime;
    }

    public void setPushCount(int pushCount) {
        this.pushCount = pushCount;
    }

    public boolean doSendOnlyOne() {
        initCache();

        ExecuteLogic.initLog(hostName, topico, suscriptor);

        long start = System.currentTimeMillis();
        boolean ok = accessControl.accessResource(this);

        ExecuteLogic.endLog(start);

        String msg = "Procesamiento del PUSH";
        logger.debug(msg);
        return ok;
    }

    public void doSend() throws InterruptedException {
        int count = 1;
        while (!stop) {
            boolean ok;
            synchronized (this) {
                ok = doSendOnlyOne();
            }
            if (!ok || count == pushCount) {
                count = 1;
                if (ok || !haController.grantPushAccess()) {
                    this.threadSleepTime = this.tsDefaultSleepTime;
                } else {
                    //Esto es para que el thread siga revisando cada cierto tiempo y no quede dormido ante una cancelacion o reenvio en otro nodo.
                    long sleepTime = sleepTimePolicy();
                    this.threadSleepTime = sleepTime > this.tsDefaultSleepTime ? this.tsDefaultSleepTime : sleepTime;
                }
                String msg = String.format("Thread Sleep. threadSleepTime %d", this.threadSleepTime);
                logger.trace(msg);

                Thread.sleep(this.threadSleepTime);
            } else {
                count++;
            }
        }
    }

    public void interrupt() {
        synchronized (this) {
            // Esto es para evitar que se interrumpa mientras se está ejecutando un push.
        }
    }

    private long sleepTimePolicy() {
        try {
            Entrega entrega = entregaService.buscarPrimera(suscriptor, topico);
            if (entrega != null) {
                int cantReintentos = entrega.getCantidadReintentos();
                Date fechaUltimoIntento = entrega.getFechaUltimoIntento();
                if (cantReintentos > 0 || fechaUltimoIntento != null) {
                    long sleepTime = sleepTime(cantReintentos);
                    long realSleepTime = System.currentTimeMillis() - fechaUltimoIntento.getTime();
                    String msg = String.format("SleepTimePolicy. Sleeptime %d, reintentos %d, Ultimo Intento %s", sleepTime, cantReintentos, fechaUltimoIntento.toString());
                    logger.trace(msg);
                    return (realSleepTime >= sleepTime) ? 0 : (sleepTime - realSleepTime);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        String msg = "SleepTimePolicy. Default sleepTime";
        logger.trace(msg);
        return tsDefaultSleepTime;
    }

    private long sleepTime(int cantReintentos) {
        if (cantReintentos <= this.pysPushProperties.getMaxReintentosExponencial()) {
            return (long) Math.pow(2, cantReintentos) * 1000l;
        } else {
            return this.pysPushProperties.getDiasMultiplicador() * MILLIS_DAY;
        }
    }

    @java.lang.SuppressWarnings("squid:S3776")
    @Override
    public void executeLogic() throws Throwable {
        String url = null;
        TopicoSuscriptor ts = null;
        Entrega entrega = null;
        long startInvoke = 0;
        try {
            ts = this.topicoSuscriptorService.buscarTopicoSuscriptor(suscriptor, topico);
            if (ts != null) {
                if (ts.getDeliveryMode().equalsIgnoreCase(DeliveryMode.PULL) || ts.getSuscriptor() == null
                        || !ts.getSuscriptor().getHabilitado() || !ts.getTopico().getHabilitado()) {
                    stop = true;
                    pushController.removeTS(tsName);
                } else {
                    entrega = entregaService.buscarPrimera(suscriptor, topico);
                    if (entrega != null) {
                        MDC.put(Constants.MESSAGEID_HEADER_NAME, entrega.getUuid());
                        int cantReintentos = entrega.getCantidadReintentos();
                        Date fechaUltimoIntento = entrega.getFechaUltimoIntento();
                        if (checkTimeout && (cantReintentos > 0 || fechaUltimoIntento != null)) {
                            long sleepTime = sleepTime(cantReintentos);
                            if (fechaUltimoIntento != null && fechaUltimoIntento.getTime() + sleepTime >= System.currentTimeMillis()) {
                                String msg = String.format("No transcurrio el tiempo entre reintentos. Sleeptime %d, reintentos %d, Ultimo Intento %s", sleepTime, cantReintentos, fechaUltimoIntento.toString());
                                logger.trace(msg);
                                throw new SleepTimeException(msg);
                            }
                        }
                        String wsaTo = ts.getDeliveryWsaTo();
                        String wsaAction = ts.getTopico().getSoapAction();
                        String wsaMessageID = "uuid:" + UUID.randomUUID().toString();
                        url = ts.getDeliveryAddr();
                        String xml = entrega.getNovedad().getContenido();
                        startInvoke = System.currentTimeMillis();
                        SOAPMessage response = pdiContext.invokeService(tsName, wsaTo, wsaAction, wsaMessageID, xml, url, entrega);
                        String mdcMsg = String.format("%d", System.currentTimeMillis() - startInvoke);
                        MDC.put(DURATION, mdcMsg);
                        if (response != null && response.getSOAPBody() != null) {
                            String msg = "PUSH ejecutado correctamente";
                            logger.debug(msg);
                            entrega.setEstado(EstadoEntrega.ENVIADO.name());
                            entrega.setTipoEntrega(DeliveryMode.PUSH);
                            entrega.setFechaEnviado(new Date());
                            entregaService.upsert(entrega);
                        } else {
                            String msg = "Error en la entrega. La respuestas no es valida o no es del tipo valido";
                            logger.error(msg);
                            generateAlerta(msg, msg, null, entrega, ts, startInvoke);
                        }
                    }
                }
            } else {
                stop = true;
                pushController.removeTS(tsName);
            }
        } catch (PSException | ConfigurationException e) {
            generateAlerta(e.getMessage(), null, e, entrega, ts, startInvoke);
            throw e;
        } catch (SleepTimeException e) {
            String msg = "Recalculo sleepTime";
            logger.trace(msg);
            throw e;
        } catch (SOAPFaultException e) {
            handleSoapFaultException(e, entrega, ts, startInvoke);
        } catch (WebServiceException e) {
            handleWebServiceException(e, entrega, ts, url, startInvoke);
        } catch (Throwable e) {
            String msg = String.format("Error desconocido. URL: %s", url);
            logger.error(msg, e);
            generateAlerta(msg, msg, e, entrega, ts, startInvoke);
            throw e;
        }
    }

    private void handleWebServiceException(WebServiceException e, Entrega entrega, TopicoSuscriptor ts, String url, long startInvoke) {
        Throwable t = e.getCause();
        String msg = String.format("La informaci\u00F3n suministrada para la conexi\u00F3n no es correcta. URL: %s", url);
        if (t instanceof java.net.SocketTimeoutException) {
            msg = String.format("Se detectó un timeout para la URL: %s", url);
        }
        logger.error(msg);
        generateAlerta(msg, msg, e, entrega, ts, startInvoke);
        throw e;
    }

    private void handleSoapFaultException(SOAPFaultException e, Entrega entrega, TopicoSuscriptor ts, long startInvoke) {
        String msg = "Se respondió con un SOAPFault";
        logger.trace(msg, e);
        SOAPFault fault = e.getFault();
        String error = null;
        if (fault != null) {
            error = msg + " - " + fault.getFaultString();
            try {
                msg = XmlTransformer.domToString(fault);
                if (logger.isTraceEnabled()) {
                    logger.trace(msg);
                }
            } catch (Exception e1) {
                logger.error(e1.getMessage(), e1);
            }
        }
        generateAlerta(error, msg, null, entrega, ts, startInvoke);
        throw e;
    }

    private void initCache() {
        if (!cacheEnabled) {
            accessControl.init();
            cacheEnabled = true;
        }
    }

    private void generateAlerta(String msgError, String str, Throwable e, Entrega entrega, TopicoSuscriptor ts, long startInvoke) {
        StringBuilder stringBuilder = new StringBuilder();
        if (str != null) {
            stringBuilder.append(str);
            stringBuilder.append("\r\n");
        }

        if (e != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            stringBuilder.append(sw.toString());
        }

        if (entrega != null && ts != null) {
            try {
                Alerta alerta = new Alerta();
                alerta.setError(msgError);
                alerta.setDescripcion(stringBuilder.toString());
                alerta.setEntrega(entrega);
                alerta.setFecha(new Date());
                alerta.setProductor(entrega.getNovedad().getProductor());
                alerta.setSuscriptor(ts.getSuscriptor());
                alerta.setTopico(ts.getTopico());
                alerta.setUuid(entrega.getUuid());
                alertaService.crear(alerta);

                entrega = entregaService.buscarEntrega(entrega.getId(), suscriptor, topico);
                if (entrega.getFechaUltimoIntento() != null) {
                    int reintentos = entrega.getCantidadReintentos();
                    reintentos = reintentos + 1;
                    entrega.setCantidadReintentos(reintentos);
                }
                entrega.setFechaUltimoIntento(startInvoke > 0 ? new Date(startInvoke) : new Date());
                entregaService.upsert(entrega);
            } catch (Exception ex) {
                logger.error("Error al crear la alerta", ex);
            }
        } else {
            String msg = "Error al crear la alerta. Entrega o Topico inexistente";
            logger.error(msg);
        }
    }

    public void setSuscriptor(String suscriptor) {
        this.suscriptor = suscriptor;
    }

    public void setTopico(String topico) {
        this.topico = topico;
    }

    public long getThreadSleepTime() {
        return this.threadSleepTime;
    }

    public void setCheckTimeout(boolean checkTimeout) {
        this.checkTimeout = checkTimeout;
    }

}
