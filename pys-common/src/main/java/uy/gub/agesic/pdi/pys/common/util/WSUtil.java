package uy.gub.agesic.pdi.pys.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import uy.gub.agesic.pdi.common.logging.PDIHostName;
import org.apache.commons.io.IOUtils;
import uy.gub.agesic.pdi.common.message.canonical.Canonical;
import uy.gub.agesic.pdi.common.message.soap.SoapPayload;
import uy.gub.agesic.pdi.common.utiles.CanonicalProcessor;
import uy.gub.agesic.pdi.common.utiles.HttpUtil;

public class WSUtil {

    private static final Logger logger = LoggerFactory.getLogger(WSUtil.class);

    private WSUtil() {
    }

    public static Canonical<SoapPayload> initWS(HttpServletRequest req) throws IOException {
        MDC.clear();

        MDC.put("duration", "0");
        MDC.put("host", PDIHostName.HOST_NAME);
        String transactionId = "uuid:" + UUID.randomUUID();
        MDC.put(Constants.TRANSACTIONID_HEADER_NAME, transactionId);

        InputStream is = req.getInputStream();
        byte[] bytes = IOUtils.toByteArray(is);

        long startTime = System.currentTimeMillis();

        if (logger.isTraceEnabled()) {
            String msgPayload = String.format("Request payload - %s", new String(bytes));
            logger.trace(msgPayload);
            String msgHeader = String.format("Request cabezal http - %s", HttpUtil.getHeadersRequest(req).toString());
            logger.trace(msgHeader);
        }

        Canonical<SoapPayload> canonical = CanonicalProcessor.createSoapCanonical(bytes);
        canonical.getHeaders().put("initialTime", startTime);
        canonical.getHeaders().put(Constants.TRANSACTIONID_HEADER_NAME, transactionId);

        String contentType = req.getContentType();
        SoapPayload soapPayload = canonical.getPayload();
        soapPayload.setContentType(contentType);

        return canonical;
    }

    public static void generateResponse(HttpServletResponse resp, Canonical canonical) {
        int statusCode = 200;
        byte[] bytes = CanonicalProcessor.getData(canonical);
        if (canonical.getPayload() instanceof SoapPayload) {
            SoapPayload soapPayload = (SoapPayload) canonical.getPayload();
            if (bytes != null) {
                resp.setContentType(soapPayload.getContentType());
            }
            if (soapPayload.getResponseStatusCode() != null) {
                statusCode = Integer.parseInt(soapPayload.getResponseStatusCode());
            }
            Object header = canonical.getHeaders().get("serviceTimestamp");
            resp.setHeader("pys_serviceTimestamp", header != null ? header.toString() : "");
            header = canonical.getHeaders().get("webProxyTimestamp");
            resp.setHeader("pys_webProxyTimestamp", header != null ? header.toString() : "");
            Long start = (Long) canonical.getHeaders().get("initialTime");
            resp.setHeader("pys_responseTime", Long.toString(System.currentTimeMillis() - start));
        }

        resp.setStatus(statusCode);

        canonical.getHeaders().put("endTime", System.currentTimeMillis());
        if (bytes != null) {
            try {
                if (logger.isTraceEnabled()) {
                    String payloadMsg = String.format("Response payload - %s", new String(bytes));
                    logger.trace(payloadMsg);
                    String headersMsg = String.format("Response cabezal http - %s", HttpUtil.getHeadersResponse(resp).toString());
                    logger.trace(headersMsg);
                }
                resp.getOutputStream().write(bytes);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

}
