package uy.gub.agesic.pdi.pys.push.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import uy.gub.agesic.pdi.pys.backend.service.EntregaService;
import uy.gub.agesic.pdi.pys.backend.service.TopicoSuscriptorService;
import uy.gub.agesic.pdi.pys.common.util.Constants;
import uy.gub.agesic.pdi.pys.domain.DeliveryMode;
import uy.gub.agesic.pdi.pys.domain.Entrega;
import uy.gub.agesic.pdi.pys.domain.TopicoSuscriptor;
import uy.gub.agesic.pdi.pys.push.send.AccessControl;
import uy.gub.agesic.pdi.pys.push.send.BusinessLogicExecutor;

public class CancelDelivery implements BusinessLogicExecutor {

    private static final Logger logger = LoggerFactory.getLogger(CancelDelivery.class);

    private AccessControl accessControl;

    private String suscriptor;

    private String topico;

    private String idEntrega;

    private EntregaService entregaService;

    private TopicoSuscriptorService topicoSuscriptorService;

    private boolean cacheEnabled;

    private String hostName;

    public CancelDelivery(EntregaService entregaService, TopicoSuscriptorService topicoSuscriptorService,
                          String hostName) {
        this.entregaService = entregaService;
        this.topicoSuscriptorService = topicoSuscriptorService;
        this.hostName = hostName;
    }

    public void setAccessControl(AccessControl accessControl) {
        this.accessControl = accessControl;
    }

    public boolean cancel() {
        initCache();

        ExecuteLogic.initLog(hostName, topico, suscriptor);

        long start = System.currentTimeMillis();
        boolean ok = accessControl.accessResource(this);

        ExecuteLogic.endLog(start);

        String msg = "Cancelar Entrega PUSH";
        logger.debug(msg);
        return ok;
    }

    @SuppressWarnings("squid:S3776")
    @Override
    public void executeLogic() throws Throwable {
        try {
            TopicoSuscriptor ts = this.topicoSuscriptorService.buscarTopicoSuscriptor(suscriptor, topico);
            if (ts != null && ts.getDeliveryMode().equalsIgnoreCase(DeliveryMode.PUSH) && ts.getSuscriptor() != null
                    && ts.getSuscriptor().getHabilitado() && ts.getTopico().getHabilitado()) {

                Entrega entrega = entregaService.buscarEntrega(idEntrega, suscriptor, topico);
                if (entrega != null) {
                    MDC.put(Constants.MESSAGEID_HEADER_NAME, entrega.getUuid());
                    entregaService.cancelar(entrega);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    private void initCache() {
        if (!cacheEnabled) {
            accessControl.init();
            cacheEnabled = true;
        }
    }

    public void setSuscriptor(String suscriptor) {
        this.suscriptor = suscriptor;
    }

    public void setTopico(String topico) {
        this.topico = topico;
    }

    public void setIdEntrega(String idEntrega) {
        this.idEntrega = idEntrega;
    }

}
