package uy.gub.agesic.pdi.pys.push.send.handler;

import org.apache.log4j.Logger;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LoggingHandler implements SOAPHandler<SOAPMessageContext> {
    private static final Logger log = Logger.getLogger(LoggingHandler.class);

    public void close(MessageContext arg0) {
        //No usado
    }

    public boolean handleFault(SOAPMessageContext ctx) {
        return true;
    }

    public boolean handleMessage(SOAPMessageContext ctx) {
        Boolean outbound = (Boolean) ctx.get("javax.xml.ws.handler.message.outbound");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append((outbound == null || outbound) ? "Request - " : "Response - ");
        SOAPMessage msg = ctx.getMessage();
        try {
            if (log.isTraceEnabled()) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                msg.writeTo(out);
                String strMsg = new String(out.toByteArray());
                stringBuilder.append(strMsg);

                Map<String, List<String>> responseHeaders = (Map<String, List<String>>) ctx.get((outbound == null || outbound) ? SOAPMessageContext.HTTP_REQUEST_HEADERS : SOAPMessageContext.HTTP_RESPONSE_HEADERS);
                String headers = getHeaders(responseHeaders);

                stringBuilder.append("\n");
                stringBuilder.append("HTTP Headers - ");
                stringBuilder.append(headers);

                log.trace(stringBuilder.toString());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return true;
    }

    public Set<QName> getHeaders() {
        return Collections.emptySet();
    }

    private String getHeaders(Map<String, List<String>> headers) {
        StringBuffer result = new StringBuffer();
        if (headers != null) {
            for (Map.Entry<String, List<String>> header : headers.entrySet()) {
                if (header.getValue() == null || header.getValue().isEmpty()) {
                    result.append(header.getValue());
                } else {
                    for (String value : header.getValue()) {
                        result.append(header.getKey() + ": " + value);
                    }
                }
                result.append("\n");
            }
        }
        return result.toString();
    }

}