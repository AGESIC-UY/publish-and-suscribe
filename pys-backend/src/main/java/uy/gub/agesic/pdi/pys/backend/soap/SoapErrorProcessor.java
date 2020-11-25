package uy.gub.agesic.pdi.pys.backend.soap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uy.gub.agesic.pdi.common.message.canonical.Canonical;
import uy.gub.agesic.pdi.common.message.canonical.Error;
import uy.gub.agesic.pdi.common.message.soap.SoapPayload;
import uy.gub.agesic.pdi.common.utiles.CanonicalProcessor;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.common.util.Constants;
import uy.gub.agesic.pdi.services.httpproxy.business.ErrorProcessor;


import java.util.Map;

@Component
public class SoapErrorProcessor {

    private static final Logger logger = LoggerFactory.getLogger(SoapErrorProcessor.class);

    private PYSSoapTransformer pysSoapTransformer;

    private ErrorProcessor errorProcessor;

    private WsaInspector wsaInspector;

    @Autowired
    public SoapErrorProcessor(PYSSoapTransformer pysSoapTransformer, ErrorProcessor errorProcessor, WsaInspector wsaInspector) {
        this.pysSoapTransformer = pysSoapTransformer;
        this.errorProcessor = errorProcessor;
        this.wsaInspector = wsaInspector;
    }

    public void processErrors(Canonical<SoapPayload> canonical, Exception e) {
        Error error = createError(e instanceof PSException ? (PSException) e : new PSException(e));
        canonical.getHeaders().put(Constants.ERROR_HEADER_NAME, error);
        processErrors(canonical);
    }

    public Error createError(PSException e) {
        return errorProcessor.createError(e.getMessage(), e.getCode(), e.getDescription());
    }

    public void processErrors(Canonical<SoapPayload> message) {
        Object errorObj = message.getHeaders().get(Constants.ERROR_HEADER_NAME);
        if (errorObj != null) {
            Error error;
            if (errorObj instanceof Error) {
                error = (Error) errorObj;
                String desc = errorProcessor.getDescriptionError(error);
                error.setDescription(desc != null ? desc : error.getDescription());
            } else {
                Map map = (Map) errorObj;
                error = new Error();
                error.setMessage((String) map.get("message"));
                error.setCode((String) map.get("code"));
                String desc = errorProcessor.getDescriptionError(error);
                error.setDescription(desc != null ? desc : (String) map.get("description"));

                message.getHeaders().put(Constants.ERROR_HEADER_NAME, error);
            }

            message.getPayload().setResponseStatusCode("500");

            try {
                String cs = Constants.DEFAULT_ENCODING;
                String ct = Constants.DEFAULT_CONTENTTYPE;
                error.setAction(this.wsaInspector.getWsaAction(message));
                error.setRelatesTo(this.wsaInspector.getWsaMessageID(message));
                error.setMessageId(message.getHeaders().get(Constants.TRANSACTIONID_HEADER_NAME).toString());
                String xml = pysSoapTransformer.soapFaultTemplate(error, cs);
                message.getPayload().setBase64Data(CanonicalProcessor.encodeData(xml, cs));
                message.getPayload().setContentType(ct);
            } catch (Exception e) {
                logger.error(Constants.ERRORCODESOAP + " " + errorProcessor.getDescriptionByCode(Constants.ERRORCODESOAP), e);
            }
        }
    }
}

