package uy.gub.agesic.pdi.pys.backend.soap;

import org.springframework.stereotype.Component;
import uy.gub.agesic.pdi.common.logging.Loggable;
import uy.gub.agesic.pdi.common.message.canonical.Canonical;
import uy.gub.agesic.pdi.common.message.soap.SoapPayload;
import uy.gub.agesic.pdi.common.soap.WsaHeadersProcessor;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.common.util.Constants;

@Component
public class WsaInspector {

    @Loggable
    public void processHeaders(Canonical<SoapPayload> message, String xml) throws PSException {
        try {
            WsaHeadersProcessor.processHeaders(message, xml);
        } catch (Exception e) {
            throw new PSException("Error al procesar el cabezal, inv\u00E1lido", null, Constants.ERRORPROCHEADER, e);
        }
    }

    public String getWsaAction(Canonical<SoapPayload> message) {
        return WsaHeadersProcessor.getWsaAction(message);
    }

    public String getWsaTo(Canonical<SoapPayload> message) {
        return WsaHeadersProcessor.getWsaTo(message);
    }

    public String getWsaMessageID(Canonical<SoapPayload> message) {
        return WsaHeadersProcessor.getWsaMessageID(message);
    }

    public String getWsaRelatesTo(Canonical<SoapPayload> message) {
        return WsaHeadersProcessor.getWsaRelatesTo(message);
    }

}
