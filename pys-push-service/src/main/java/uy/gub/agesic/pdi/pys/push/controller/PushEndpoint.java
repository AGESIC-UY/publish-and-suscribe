package uy.gub.agesic.pdi.pys.push.controller;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.Enumeration;

@Service
public class PushEndpoint extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger("push.test");

    private static final String TEXT_XML_CT = "text/xml;charset=UTF-8";

    private static final String EMPTY_SOAP = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">" +
            "<SOAP-ENV:Header xmlns:wsa=\"http://www.w3.org/2005/08/addressing\"></SOAP-ENV:Header><SOAP-ENV:Body>" +
            "</SOAP-ENV:Body></SOAP-ENV:Envelope>";

    private static final String FAULT_SOAP = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
            "<SOAP-ENV:Header/><SOAP-ENV:Body>\n" +
            "<SOAP-ENV:Fault><faultcode>SOAP-ENV:Client</faultcode>\n" +
            "<faultstring>Message does not have necessary info</faultstring>\n" +
            "<faultactor>http://gizmos.com/order</faultactor>\n" +
            "<detail>\n" +
            "<PO:order xmlns:PO=\"http://gizmos.com/orders/\">\n" +
            "Quantity element does not have a value</PO:order>\n" +
            "<PO:confirmation xmlns:PO=\"http://gizmos.com/confirm\">\n" +
            "Incomplete address: no zip code</PO:confirmation>\n" +
            "</detail></SOAP-ENV:Fault>\n" +
            "</SOAP-ENV:Body></SOAP-ENV:Envelope>";

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String path = req.getRequestURI();
            if (path.endsWith("fault")) {
                responseFault(req, resp);
            } else if (path.endsWith("timeout")) {
                responseTimeout(req, resp);
            } else {
                responseOK(req, resp);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    public void responseOK(HttpServletRequest req, HttpServletResponse resp) {
        try {
            processMessage(req);

            resp.setContentType(TEXT_XML_CT);
            resp.setHeader("Content-Type", TEXT_XML_CT);

            resp.setStatus(200);

            resp.getOutputStream().write(EMPTY_SOAP.getBytes());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void responseTimeout(HttpServletRequest req, HttpServletResponse resp) {
        try {
            processMessage(req);

            resp.setContentType(TEXT_XML_CT);
            resp.setHeader("Content-Type", TEXT_XML_CT);

            resp.setStatus(200);

            Thread.sleep(40000);

            resp.getOutputStream().write(EMPTY_SOAP.getBytes());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void responseFault(HttpServletRequest req, HttpServletResponse resp) {
        try {
            processMessage(req);

            resp.setContentType(req.getContentType());
            resp.setStatus(500);

            resp.getOutputStream().write(FAULT_SOAP.getBytes());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void processMessage(HttpServletRequest req) {
        try {
            InputStream is = req.getInputStream();
            byte[] bytes = IOUtils.toByteArray(is);

            StringBuilder stringBuilder = new StringBuilder();
            if (bytes != null) {
                stringBuilder.append("Request - [");
                stringBuilder.append(new String(bytes));
                stringBuilder.append("]\n");
            }

            stringBuilder.append("Headers - [");
            Enumeration<String> headers = req.getHeaderNames();
            while (headers.hasMoreElements()) {
                String headerName = headers.nextElement();
                String headerValue = req.getHeader(headerName);
                stringBuilder.append(headerName);
                stringBuilder.append("=");
                stringBuilder.append(headerValue);
                stringBuilder.append("; ");
            }
            stringBuilder.append("] ");

            String msg = stringBuilder.toString();
            logger.debug(msg);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

}
