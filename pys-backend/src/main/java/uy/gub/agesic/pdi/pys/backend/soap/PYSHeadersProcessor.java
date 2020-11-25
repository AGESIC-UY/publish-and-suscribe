package uy.gub.agesic.pdi.pys.backend.soap;

import org.jaxen.saxpath.SAXPathException;
import org.springframework.stereotype.Component;
import uy.gub.agesic.pdi.common.logging.Loggable;
import uy.gub.agesic.pdi.common.message.canonical.Canonical;
import uy.gub.agesic.pdi.common.message.soap.SoapPayload;
import uy.gub.agesic.pdi.common.utiles.XPathXmlDogUtil;

import javax.xml.xpath.XPathException;
import java.util.*;

@Component
public class PYSHeadersProcessor {

    private static final String SAML_IDENTIFIER_NAME = "samlNameIdentifier";

    private static final String TO_ACK_NAME = "toAck";

    private static final String PS_TOPIC_NAME = "psTopic";

    private static Map<String, String> namespaces = new HashMap<>();

    private static List<String> xpaths = new LinkedList<>();

    static {
        xpaths.add("/soapenv:Envelope/soapenv:Header/wsse:Security/saml:Assertion/saml:AuthenticationStatement/saml:Subject/saml:NameIdentifier/text()");
        xpaths.add("/soapenv:Envelope/soapenv:Header/ps:producer/text()");
        xpaths.add("/soapenv:Envelope/soapenv:Header/ps:topic/text()");
        xpaths.add("/soapenv:Envelope/soapenv:Body/ps:NotificationRequest/subscriber/text()");
        xpaths.add("/soapenv:Envelope/soapenv:Body/ps:NotificationRequest/topic/text()");
        xpaths.add("/soapenv:Envelope/soapenv:Body/ps:NotificationRequest/ackNotificationId/text()");

        namespaces.put("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        namespaces.put("wsa", "http://www.w3.org/2005/08/addressing");
        namespaces.put("ps", "http://ps.agesic.gub.uy");
        namespaces.put("wsse",  "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
        namespaces.put("saml", "urn:oasis:names:tc:SAML:1.0:assertion");
    }

    @Loggable
    public void processHeadersPublisher(Canonical<SoapPayload> message, String xml) throws XPathException, SAXPathException {
        SoapPayload payload = message.getPayload();

        List<Object> listResultsXPath = XPathXmlDogUtil.executeMultipleXPath(xml, xpaths, namespaces);

        String samlNameIdentifier;
        String psProducer;
        String psTopic;

        if ((listResultsXPath.get(0) instanceof Collection)) {
            samlNameIdentifier = (String) ((List) listResultsXPath.get(0)).get(0);
        } else {
            samlNameIdentifier = (String) listResultsXPath.get(0);
        }
        payload.getWsaHeaders().put(SAML_IDENTIFIER_NAME, (samlNameIdentifier != null) ? samlNameIdentifier.trim() : samlNameIdentifier);

        if ((listResultsXPath.get(1) instanceof Collection)) {
            psProducer = (String) ((List) listResultsXPath.get(1)).get(0);
        } else {
            psProducer = (String) listResultsXPath.get(1);
        }
        payload.getWsaHeaders().put("psProducer", (psProducer != null) ? psProducer.trim() : psProducer);

        if ((listResultsXPath.get(2) instanceof Collection)) {
            psTopic = (String) ((List) listResultsXPath.get(2)).get(0);
        } else {
            psTopic = (String) listResultsXPath.get(2);
        }
        payload.getWsaHeaders().put(PS_TOPIC_NAME,  (psTopic != null) ? psTopic.trim() : psTopic);

    }

    @Loggable
    public void processHeadersPull(Canonical<SoapPayload> message, String xml) throws Exception {
        SoapPayload payload = message.getPayload();

        List<Object> listResultsXPath = XPathXmlDogUtil.executeMultipleXPath(xml, xpaths, namespaces);

        String samlNameIdentifier;
        String psSuscriber;
        String psTopic;
        String toAck;

        if ((listResultsXPath.get(0) instanceof Collection)) {
            samlNameIdentifier = (String) ((List) listResultsXPath.get(0)).get(0);
        } else {
            samlNameIdentifier = (String) listResultsXPath.get(0);
        }
        payload.getWsaHeaders().put(SAML_IDENTIFIER_NAME, (samlNameIdentifier != null) ? samlNameIdentifier.trim() : samlNameIdentifier);

        if ((listResultsXPath.get(3) instanceof Collection)) {
            psSuscriber = (String) ((List) listResultsXPath.get(3)).get(0);
        } else {
            psSuscriber = (String) listResultsXPath.get(3);
        }
        payload.getWsaHeaders().put("psSuscriber",  (psSuscriber != null) ? psSuscriber.trim() : psSuscriber);

        if ((listResultsXPath.get(4) instanceof Collection)) {
            psTopic = (String) ((List) listResultsXPath.get(4)).get(0);
        } else {
            psTopic = (String) listResultsXPath.get(4);
        }
        payload.getWsaHeaders().put(PS_TOPIC_NAME, (psTopic != null) ? psTopic.trim() : psTopic);

        if ((listResultsXPath.get(5) instanceof Collection)) {
            toAck = (String) ((List) listResultsXPath.get(5)).get(0);
        } else {
            toAck = (String) listResultsXPath.get(5);
        }
        payload.getWsaHeaders().put(TO_ACK_NAME, (toAck != null) ? toAck.trim() : toAck);

    }

    public String getDN (Canonical<SoapPayload> message) {
        return message.getPayload().getWsaHeaders().get(SAML_IDENTIFIER_NAME);
    }

    public String getSuscriber (Canonical<SoapPayload> message) {
        return message.getPayload().getWsaHeaders().get("psSuscriber");
    }

    public String getProducer (Canonical<SoapPayload> message) {
        return message.getPayload().getWsaHeaders().get("psProducer");
    }

    public String getTopic(Canonical<SoapPayload> message) {
        return message.getPayload().getWsaHeaders().get(PS_TOPIC_NAME);
    }

    public String getToAck (Canonical<SoapPayload> message) {
        return message.getPayload().getWsaHeaders().get(TO_ACK_NAME);
    }

}
