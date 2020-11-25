package uy.gub.agesic.pdi.pys.backend.soap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uy.gub.agesic.pdi.common.message.canonical.Canonical;
import uy.gub.agesic.pdi.common.message.canonical.Error;
import uy.gub.agesic.pdi.common.message.soap.SoapPayload;
import uy.gub.agesic.pdi.common.utiles.XSLTInformation;
import uy.gub.agesic.pdi.common.utiles.XmlTransformer;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.common.util.Constants;
import uy.gub.agesic.pdi.pys.domain.Novedad;

import java.text.SimpleDateFormat;

@Component
public class PYSSoapTransformer {

    private static final String MESSAGE_ID_NAME = "messageId";

    private static final String RELATES_TO_NAME = "relatesTo";

    private static final String WSA_ACTION_NAME = "wsaAction";

    private static final String EMPTY_XML = "<xml/>";

    private WsaInspector wsaInspector;

    @Autowired
    public PYSSoapTransformer(WsaInspector wsaInspector) {
        this.wsaInspector = wsaInspector;
    }

    public String extractBody(String xml, String cs) throws PSException {
        XSLTInformation xsltInfo = new XSLTInformation();
        xsltInfo.setName("extract-body");
        xsltInfo.setPath("extract-body.xsl");

        try {
            return XmlTransformer.xslt(xml, xsltInfo, cs);
        } catch (Exception e) {
            throw new PSException(e);
        }
    }

    public String soapFaultTemplate(Error error, String cs) throws PSException {
        XSLTInformation xsltInfo = new XSLTInformation();
        xsltInfo.setName("soapFault");
        xsltInfo.setPath(Constants.PATHXSLSOAPFAULT);
        xsltInfo.getParameters().put("errorMessage", error.getMessage() == null ? "" : error.getMessage());
        xsltInfo.getParameters().put("errorDescription", error.getDescription() == null ? "" : error.getDescription());
        xsltInfo.getParameters().put("errorCode", error.getCode() == null ? "" : error.getCode());
        xsltInfo.getParameters().put(MESSAGE_ID_NAME, error.getMessageId() == null ? "" : error.getMessageId());
        xsltInfo.getParameters().put(RELATES_TO_NAME, error.getRelatesTo() == null ? "" : error.getRelatesTo());
        xsltInfo.getParameters().put(WSA_ACTION_NAME, error.getAction() == null ? "" : error.getAction());
        try {
            return XmlTransformer.xslt(EMPTY_XML, xsltInfo, cs);
        } catch (Exception e) {
            throw new PSException("Error interno", null, Constants.ERRORSOAPFAULTTEMPLATE, e);
        }
    }


    public String responseConDatos(Novedad novedad, Canonical<SoapPayload> canonical, String cs, String reason) throws PSException {
        XSLTInformation xsltInfo = new XSLTInformation();

        if (reason != null) {
            xsltInfo.setName("responsePullConDatosRazon");
            xsltInfo.setPath(Constants.PATHXSLPULLRESCONDATOS_RAZON);
        } else {
            xsltInfo.setName("responsePullConDatos");
            xsltInfo.setPath(Constants.PATHXSLPULLRESCONDATOS);
        }

        xsltInfo.getParameters().put("replayProducer", novedad.getProductor() == null ? "" : novedad.getProductor().getNombre());
        xsltInfo.getParameters().put("replayNotificationId", novedad.getUuid() == null ? "" : novedad.getUuid());

        String convertedDate = null;
        if (novedad.getFecha() != null) {
            /*
            GregorianCalendar gc = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
            gc.setTime(novedad.getFecha());
            */
            try {
                //convertedDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc).toXMLFormat();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                convertedDate = dateFormat.format(novedad.getFecha());
            } catch (Exception e) {
                throw new PSException(e);
            }
        }

        xsltInfo.getParameters().put("createTS", convertedDate == null ? "" : convertedDate);
        xsltInfo.getParameters().put("replayXML", novedad.getContenido());
        if (reason != null)
            xsltInfo.getParameters().put("reason", reason);

        String transactionID = (String) canonical.getHeaders().get(Constants.TRANSACTIONID_HEADER_NAME);
        xsltInfo.getParameters().put(MESSAGE_ID_NAME, transactionID == null ? "" : transactionID);
        xsltInfo.getParameters().put(RELATES_TO_NAME, wsaInspector.getWsaMessageID(canonical) == null ? "" : wsaInspector.getWsaMessageID(canonical));
        xsltInfo.getParameters().put(WSA_ACTION_NAME, wsaInspector.getWsaAction(canonical) == null ? "" : wsaInspector.getWsaAction(canonical));
        try {
            return XmlTransformer.xslt(EMPTY_XML, xsltInfo, cs);
        } catch (Exception e) {
            throw new PSException(e);
        }
    }

    public String responseSinDatos(String xml, Canonical<SoapPayload> canonical, String cs) throws PSException {
        XSLTInformation xsltInfo = new XSLTInformation();

        xsltInfo.setName("responsePullSinDatos");
        xsltInfo.setPath(Constants.PATHXSLPULLRESSINDATOS);

        xsltInfo.getParameters().put("replayXML", xml);

        String transactionID = (String) canonical.getHeaders().get(Constants.TRANSACTIONID_HEADER_NAME);
        xsltInfo.getParameters().put(MESSAGE_ID_NAME, transactionID == null ? "" : transactionID);
        xsltInfo.getParameters().put(RELATES_TO_NAME, wsaInspector.getWsaMessageID(canonical) == null ? "" : wsaInspector.getWsaMessageID(canonical));
        xsltInfo.getParameters().put(WSA_ACTION_NAME, wsaInspector.getWsaAction(canonical) == null ? "" : wsaInspector.getWsaAction(canonical));

        try {
            return XmlTransformer.xslt(EMPTY_XML, xsltInfo, cs);
        } catch (Exception e) {
            throw new PSException(e);
        }

    }

    public String pubResponse(String uuid, Canonical<SoapPayload> canonical, String cs) throws PSException {
        XSLTInformation xsltInfo = new XSLTInformation();

        xsltInfo.setName("pubResponse");
        xsltInfo.setPath(Constants.PATHXSLPUBRESPONSE);

        xsltInfo.getParameters().put("uuid", uuid == null ? "" : uuid);
        String transactionID = (String) canonical.getHeaders().get(Constants.TRANSACTIONID_HEADER_NAME);
        xsltInfo.getParameters().put(MESSAGE_ID_NAME, transactionID == null ? "" : transactionID);
        xsltInfo.getParameters().put(RELATES_TO_NAME, wsaInspector.getWsaMessageID(canonical) == null ? "" : wsaInspector.getWsaMessageID(canonical));
        xsltInfo.getParameters().put(WSA_ACTION_NAME, wsaInspector.getWsaAction(canonical) == null ? "" : wsaInspector.getWsaAction(canonical));

        try {
            return XmlTransformer.xslt(EMPTY_XML, xsltInfo, cs);
        } catch (Exception e) {
            throw new PSException(e);
        }

    }


}
