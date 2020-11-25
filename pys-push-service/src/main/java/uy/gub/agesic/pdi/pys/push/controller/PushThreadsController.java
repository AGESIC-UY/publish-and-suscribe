package uy.gub.agesic.pdi.pys.push.controller;

import org.infinispan.AdvancedCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;
import uy.gub.agesic.pdi.common.logging.PDIHostName;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.backend.service.AlertaService;
import uy.gub.agesic.pdi.pys.backend.service.EntregaService;
import uy.gub.agesic.pdi.pys.backend.service.TopicoSuscriptorService;
import uy.gub.agesic.pdi.pys.domain.DeliveryMode;
import uy.gub.agesic.pdi.pys.domain.TopicoSuscriptor;
import uy.gub.agesic.pdi.pys.push.cache.TSCacheManager;
import uy.gub.agesic.pdi.pys.push.config.PYSPushProperties;
import uy.gub.agesic.pdi.pys.push.send.AccessControl;
import uy.gub.agesic.pdi.pys.push.send.PDIContext;
import uy.gub.agesic.pdi.pys.push.send.PushHAController;

import javax.transaction.TransactionManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;

@Service
@ManagedResource
public class PushThreadsController {

    private static final Logger logger = LoggerFactory.getLogger(PushThreadsController.class);

    private PYSPushProperties pysPushProperties;

    private PushHAController haController;

    private TopicoSuscriptorService topicoSuscriptorService;

    private EntregaService entregaService;

    private AlertaService alertaService;

    private Map<String, TSThread> tsThreads = new HashMap<>();

    private Map<String, TopicoSuscriptorSender> senders = new HashMap<>();

    private TSCacheManager tsCacheManager;

    private PDIContext pdiContext;

    private List<String> whiteList = new ArrayList<>();

    private List<String> blackList = new ArrayList<>();

    private String hostName;

    @Autowired
    public PushThreadsController(TopicoSuscriptorService topicoSuscriptorService, TSCacheManager tsCacheManager, EntregaService entregaService,
                                 PDIContext pdiContext, AlertaService alertaService, PYSPushProperties pysPushProperties, PushHAController haController) {
        this.topicoSuscriptorService = topicoSuscriptorService;
        this.tsCacheManager = tsCacheManager;
        this.entregaService = entregaService;
        this.alertaService = alertaService;
        this.pdiContext = pdiContext;
        this.pysPushProperties = pysPushProperties;
        this.haController = haController;
    }

    @java.lang.SuppressWarnings("squid:S3776")
    public void createEnvironment() {
        this.tsCacheManager.init();

        String wList = System.getProperty("application.push.ts.whiteList");
        if (wList != null) {
            StringTokenizer tokenizer = new StringTokenizer(wList, ",");
            while (tokenizer.hasMoreTokens()) {
                whiteList.add(tokenizer.nextToken().toLowerCase());
            }
        }
        String bList = System.getProperty("application.push.ts.blackList");
        if (bList != null) {
            StringTokenizer tokenizer = new StringTokenizer(bList, ",");
            while (tokenizer.hasMoreTokens()) {
                blackList.add(tokenizer.nextToken().toLowerCase());
            }
        }
        try {
            this.pdiContext.init(null);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        TransactionManager txMgr = this.tsCacheManager.getLockCache().getTransactionManager();
        String msg = String.format("TX MANAGER CONFIGURADO >>>>> %s", txMgr.getClass().getName());
        logger.info(msg);

        this.hostName = PDIHostName.HOST_NAME;

        try {
            List<TopicoSuscriptor> tsList = topicoSuscriptorService.buscarTodos();
            if (tsList != null) {
                for (TopicoSuscriptor ts : tsList) {
                    if ((ts.getTopico() != null) && (ts.getSuscriptor() != null)) {
                        String tsName =  getTSName(ts.getTopico().getNombre(), ts.getSuscriptor().getNombre());
                        if (ts.getDeliveryMode().equalsIgnoreCase(DeliveryMode.PUSH) && isAcceptedTS(tsName)
                                && ts.getSuscriptor() != null && ts.getSuscriptor().getHabilitado()) {
                            TopicoSuscriptorSender tsSender = createSender(ts.getSuscriptor().getNombre(), ts.getTopico().getNombre());
                            senders.put(tsName, tsSender);
                            TSThread tsThread = new TSThread(tsName, tsSender);
                            tsThreads.put(tsName, tsThread);
                        }
                    }

                }
            }
        } catch (PSException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void startPushServices() {
        if (tsThreads != null) {
            Iterator<String> iter = tsThreads.keySet().iterator();
            while (iter.hasNext()) {
                TSThread tsThread = tsThreads.get(iter.next());
                tsThread.start();
            }
        }
        ControllerThread ct = new ControllerThread("PushThreadsController", this);
        ct.start();
    }

    public void pushTS(String suscriptor, String topico) {
        TopicoSuscriptorSender sender = createSender(suscriptor, topico);
        sender.setCheckTimeout(false);
        if (sender.doSendOnlyOne()) {
            String tsName = getTSName(topico, suscriptor);
            interruptTS(tsName);
        }
    }

    @ManagedOperation
    public void interruptTS(String suscriptor, String topico) {
        String tsName = getTSName(topico, suscriptor);
        interruptTS(tsName);
    }

    public void cancelTS(String suscriptor, String topico, String idEntrega) {
        String tsName = getTSName(topico, suscriptor);

        AccessControl accessControl = new AccessControl();
        accessControl.setTSCacheManager(tsCacheManager);
        accessControl.setTsName(tsName);
        accessControl.setHaController(haController);

        CancelDelivery cancelDelivery = new CancelDelivery(entregaService, topicoSuscriptorService, hostName);
        cancelDelivery.setAccessControl(accessControl);
        cancelDelivery.setTopico(topico);
        cancelDelivery.setSuscriptor(suscriptor);
        cancelDelivery.setIdEntrega(idEntrega);

        cancelDelivery.cancel();

        interruptTS(tsName);
    }

    private void interruptTS(String tsName) {
        TSThread tsThread = tsThreads.get(tsName);
        if (tsThread != null) {
            try {
                tsThread.interrupt();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public void looking() throws InterruptedException {
        MDC.clear();
        MDC.put("duration", "0");
        MDC.put("host", hostName);

        while (!Thread.currentThread().isInterrupted()) {
            String transactionId = "uuid:" + UUID.randomUUID();
            MDC.put("transactionId", transactionId);
            try {
                List<TopicoSuscriptor> tsList = topicoSuscriptorService.buscarTodos();
                if (tsList != null && haController.grantPushAccess()) {
                    for (TopicoSuscriptor ts : tsList) {
                        checkTSForThreadCreation(ts);
                    }
                }

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            Thread.sleep(pysPushProperties.getControllerSleepTime());
        }

    }

    private void checkTSForThreadCreation(TopicoSuscriptor ts) {
        if ((ts.getTopico() != null) && (ts.getSuscriptor() != null)) {
            String tsName = getTSName(ts.getTopico().getNombre(), ts.getSuscriptor().getNombre());
            if (ts.getDeliveryMode().equalsIgnoreCase(DeliveryMode.PUSH) && ts.getTopico().getHabilitado() && ts.getSuscriptor().getHabilitado() && tsThreads.get(tsName) == null && isAcceptedTS(tsName)) {
                String msg = String.format("Nuevo Topico Suscriptor PUSH detectado: %s", tsName);
                logger.debug(msg);
                TopicoSuscriptorSender tsSender = createSender(ts.getSuscriptor().getNombre(), ts.getTopico().getNombre());
                senders.put(tsName, tsSender);
                TSThread tsThread = new TSThread(tsName, tsSender);
                tsThreads.put(tsName, tsThread);
                tsThread.start();
            }
        } else {
            String msg = String.format("No fue posible crear el Thread, topico o suscriptor nulo. Id topico_suscriptor : %s", ts.getId());
            logger.warn(msg);
        }

    }

    public void removeTS(String tsName) {
        TSThread tsThread = tsThreads.get(tsName);
        if (tsThread != null) {
            tsThreads.remove(tsName);
        }
        TopicoSuscriptorSender sender = senders.get(tsName);
        if (sender != null) {
            senders.remove(tsName);
        }
    }

    @ManagedAttribute
    public List<String> getWhiteList() {
        return whiteList;
    }

    @ManagedAttribute
    public List<String> getBlackList() {
        return blackList;
    }

    @ManagedOperation
    public String getCacheStatus() {
        AdvancedCache<String,String> lockCache = this.tsCacheManager.getLockCache();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("STATUS DEL CLUSTER >>>>> ");
        stringBuilder.append(this.tsCacheManager.getContainer().getMembers());
        stringBuilder.append("STATUS DE LOS LOCKS >>>>> ");
        stringBuilder.append(lockCache.getLockManager().printLockInfo());

        Iterator<String> iter = senders.keySet().iterator();
        while (iter.hasNext()) {
            String tsName = iter.next();
            stringBuilder.append("OWNER DEL LOCK ");
            stringBuilder.append(tsName);
            stringBuilder.append(" >>>>> ");
            stringBuilder.append(lockCache.getLockManager().getOwner(tsName));
            stringBuilder.append("STATUS DEL LOCK ");
            stringBuilder.append(tsName);
            stringBuilder.append(" >>>>> ");
            stringBuilder.append(lockCache.getLockManager().isLocked(tsName));
        }

        String cacheStatus = stringBuilder.toString();
        logger.info(cacheStatus);
        return cacheStatus;

    }

    @ManagedOperation
    public String showSleepTime() {
        Iterator<String> iter = senders.keySet().iterator();
        StringBuilder strBuilder = new StringBuilder();
        while (iter.hasNext()) {
            String key = iter.next();
            strBuilder.append("{key=");
            strBuilder.append(key);
            strBuilder.append(", sleepTime=");
            strBuilder.append(senders.get(key).getThreadSleepTime());
            strBuilder.append("},");
        }
        return strBuilder.toString();
    }

    private String getTSName(String topico, String suscriptor) {
        return suscriptor + "_" + topico;
    }

    private boolean isAcceptedTS(String tsName) {
        if (whiteList.contains(tsName.toLowerCase())) {
            return true;
        }

        if (blackList.contains(tsName.toLowerCase())) {
            return false;
        }

        return whiteList.isEmpty();
    }

    private TopicoSuscriptorSender createSender(String suscriptor, String topico) {
        String tsName = getTSName(topico, suscriptor);
        AccessControl accessControl = new AccessControl();
        accessControl.setTSCacheManager(tsCacheManager);
        accessControl.setTsName(tsName);
        accessControl.setHaController(haController);

        TopicoSuscriptorSender tsSender = new TopicoSuscriptorSender(this, entregaService, pdiContext, topicoSuscriptorService,
                alertaService, pysPushProperties, hostName);
        tsSender.setTsName(tsName);
        tsSender.setTsDefaultSleepTime(pysPushProperties.getTsSleepTime());
        tsSender.setPushCount(pysPushProperties.getPushCount());
        tsSender.setSuscriptor(suscriptor);
        tsSender.setTopico(topico);
        tsSender.setAccessControl(accessControl);
        tsSender.setHaController(haController);

        return tsSender;
    }

    class TSThread extends Thread {

        private TopicoSuscriptorSender tsSender;

        public TSThread(String s, TopicoSuscriptorSender tsSender) {
            super(s);
            this.tsSender = tsSender;
        }

        @Override
        public void run() {
            try {
                this.tsSender.doSend();
            } catch (InterruptedException e) {
                logger.warn("Se interrumpe el Thread al ejecutar el push manualmente", e);
                tsSender.destroy();
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                tsSender.destroy();
            }
        }

        @Override
        public void interrupt() {
            this.tsSender.interrupt();
            super.interrupt();
        }
    }

    class ControllerThread extends Thread {

        private PushThreadsController controller;

        public ControllerThread(String s, PushThreadsController controller) {
            super(s);
            this.controller = controller;
        }

        @Override
        public void run() {
            try {
                this.controller.looking();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                Thread.currentThread().interrupt();
            }
        }
    }

}
