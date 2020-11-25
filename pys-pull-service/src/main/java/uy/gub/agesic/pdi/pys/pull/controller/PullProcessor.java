package uy.gub.agesic.pdi.pys.pull.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uy.gub.agesic.pdi.common.message.canonical.Canonical;
import uy.gub.agesic.pdi.common.message.soap.SoapPayload;
import uy.gub.agesic.pdi.common.utiles.CanonicalProcessor;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.backend.service.EntregaService;
import uy.gub.agesic.pdi.pys.backend.service.SuscriptorService;
import uy.gub.agesic.pdi.pys.backend.service.TopicoService;
import uy.gub.agesic.pdi.pys.backend.soap.PYSHeadersProcessor;
import uy.gub.agesic.pdi.pys.backend.soap.PYSSoapTransformer;
import uy.gub.agesic.pdi.pys.backend.soap.SoapErrorProcessor;
import uy.gub.agesic.pdi.pys.backend.soap.WsaInspector;
import uy.gub.agesic.pdi.pys.common.util.Constants;
import uy.gub.agesic.pdi.pys.common.util.WSUtil;
import uy.gub.agesic.pdi.pys.domain.*;
import uy.gub.agesic.pdi.pys.pull.config.PYSPullProperties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Service
public class PullProcessor {

    private static final Logger logger = LoggerFactory.getLogger(PullProcessor.class);

    private final EntregaService entregaService;

    private final TopicoService topicoService;

    private final SuscriptorService suscriptorService;

    private final WsaInspector wsaInspector;

    private final PYSHeadersProcessor pysHeadersProcessor;

    private final PYSSoapTransformer pysSoapTransformer;

    private final PYSPullProperties pysPullProperties;

    private final SoapErrorProcessor soapErrorProcessor;

    @Autowired
    @java.lang.SuppressWarnings("squid:S00107")
    public PullProcessor(WsaInspector wsaInspector, PYSHeadersProcessor pysHeadersProcessor,
                         PYSSoapTransformer pysSoapTransformer, TopicoService topicoService, SuscriptorService suscriptorService,
                         SoapErrorProcessor soapErrorProcessor, PYSPullProperties pysPullProperties, EntregaService entregaService) {
        this.wsaInspector = wsaInspector;
        this.pysHeadersProcessor = pysHeadersProcessor;
        this.pysSoapTransformer = pysSoapTransformer;
        this.topicoService = topicoService;
        this.suscriptorService = suscriptorService;
        this.soapErrorProcessor = soapErrorProcessor;
        this.pysPullProperties = pysPullProperties;
        this.entregaService = entregaService;

    }

    public void pull(HttpServletRequest req, HttpServletResponse resp, boolean httpsOnly) {
        Canonical<SoapPayload> canonical = null;
        try {
            canonical = WSUtil.initWS(req);

            SoapPayload soapPayload = canonical.getPayload();
            String xml = CanonicalProcessor.decodeSoap(soapPayload);

            if (req.getScheme().equalsIgnoreCase("http") && httpsOnly) {
                throw new PSException("HTTPS only", null, Constants.HTTPSONLY, null);
            }

            wsaInspector.processHeaders(canonical, xml);
            String wsaMessageID = wsaInspector.getWsaMessageID(canonical);
            MDC.put(Constants.MESSAGEID_HEADER_NAME, wsaMessageID);

            pysHeadersProcessor.processHeadersPull(canonical, xml);
            String topic = pysHeadersProcessor.getTopic(canonical);
            String dn = pysHeadersProcessor.getDN(canonical);
            String suscriber = pysHeadersProcessor.getSuscriber(canonical);
            String ack = pysHeadersProcessor.getToAck(canonical);

            MDC.put("ackID", ack == null ? "" : ack);

            processData(topic, suscriber, ack, dn, canonical);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            if (canonical == null) {
                canonical = CanonicalProcessor.createSoapCanonical(null);
            }
            soapErrorProcessor.processErrors(canonical, e);
        } finally {
            WSUtil.generateResponse(resp, canonical);
        }
    }

    private void processData(String topic, String suscriber, String ack, String dn, Canonical<SoapPayload> canonical) throws PSException {
        Topico topico = topicoService.buscar(topic);
        if (topico == null) {
            throw new PSException("T\u00F3pico no encontrado", null, Constants.ERRORMATCHTOPIC, null);
        }

        if (!topico.getHabilitado()) {
            throw new PSException("T\u00F3pico no habilitado", null, Constants.ERRORTOPICONOHAB, null);
        }

        MDC.put("topic", topic);

        Suscriptor suscriptor = suscriptorService.buscar(suscriber);
        if (suscriptor == null) {
            throw new PSException("Suscriber no encontrado", null, Constants.ERRORMATCHSUSCRIBER, null);
        }

        if (!suscriptor.getHabilitado()) {
            throw new PSException("Suscriber no habilitado", null, Constants.ERRORSUSCNOHAB, null);
        }

        MDC.put("suscriptor", suscriber);

        //valida DN si esta habilitado el acceso a traves de la pdi
        if (pysPullProperties.getAccessPDIEnabled()) {
            String suscriptorDN = suscriptor.getDn();

            if (!dn.equalsIgnoreCase(suscriptorDN)) {
                throw new PSException("El DN ingresado no corresponde al suscriptor", null, Constants.ERRORMATCHDN, null);
            }
        }

        //Valida que existe la relacion topicoSuscriptor
        if (!this.suscriptorService.existeTopicoSuscriptorPull(topico, suscriptor)) {
            throw new PSException("El suscriptor no tiene el t\u00F3pico asociado", null, Constants.ERRORMATCHSUSCTOPICO, null);
        }

        if (ack != null) {
            Entrega entrega = this.entregaService.buscarPrimera(suscriptor, topico);
            if (entrega != null && entrega.getUuid().equals(ack)) {
                entrega.setEstado(EstadoEntrega.ENVIADO.name());
                entrega.setTipoEntrega(DeliveryMode.PULL);
                entrega.setFechaEnviado(new Date());
                this.entregaService.upsert(entrega);
            }
        }

        Entrega entrega = this.entregaService.buscarPrimera(suscriptor, topico);
        Novedad novedad = entrega != null ? entrega.getNovedad() : null;
        String response;
        if (novedad != null) {
            response = pysSoapTransformer.responseConDatos(novedad, canonical, Constants.DEFAULT_ENCODING, entrega.getReason());
            MDC.put("novedadID", entrega.getUuid());
        } else {
            StringBuilder b = new StringBuilder("<ps:");
            b.append(topico.getElementoRaiz());
            b.append(" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
            b.append(" xmlns:ps=\"");
            b.append(topico.getNamespace());
            b.append("\"/>");

            response = pysSoapTransformer.responseSinDatos(b.toString(), canonical, Constants.DEFAULT_ENCODING);
        }

        canonical.getPayload().setContentType("text/xml;charset=UTF-8");
        canonical.getPayload().setBase64Data(CanonicalProcessor.encodeData(response, Constants.DEFAULT_ENCODING));
        String info = "Novedad entregada";
        logger.info(info);
    }

}
