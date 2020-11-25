package uy.gub.agesic.pdi.pys.push.send;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import uy.gub.agesic.pdi.common.git.GitManager;
import uy.gub.agesic.pdi.common.logging.Loggable;
import uy.gub.agesic.pdi.pys.domain.Entrega;
import uy.gub.agesic.pdi.pys.push.config.PYSPushProperties;
import uy.gub.agesic.pdi.pys.push.send.handler.LoggingHandler;
import uy.gub.agesic.pdi.services.httpproxy.business.ResourceResolver;
import uy.gub.agesic.pge.beans.ClientCredential;
import uy.gub.agesic.pge.beans.RSTBean;
import uy.gub.agesic.pge.beans.StoreBean;
import uy.gub.agesic.pge.core.config.PGEConfiguration;
import uy.gub.agesic.pge.core.security.KeyStoreUtil;
import uy.gub.agesic.pge.core.ssl.SSLContextInitializer;
import uy.gub.agesic.pge.core.sts.client.PGESTSClient;
import uy.gub.agesic.pge.core.ws.addressing.AddressingBuilder;
import uy.gub.agesic.pge.core.ws.addressing.AddressingProperties;
import uy.gub.agesic.pge.exceptions.ConfigurationException;
import uy.gub.agesic.pge.exceptions.PGEContextException;
import uy.gub.agesic.pge.handler.SAMLHandler;
import uy.gub.agesic.pge.handler.WSAddressingHandler;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.*;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Response;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.soap.SOAPBinding;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@SuppressWarnings("squid:S1068")
public class PDIContext {

    private static final Logger log = Logger.getLogger(PDIContext.class);

    private static final String ADDRESSING_CONTEXT = "uy.gub.pge.ws.addressing.context";

    private PYSPushProperties pysPushProperties;

    private ResourceResolver resourceResolver;

    private GitManager gitManager;

    private PGESTSClient stsClient = new PGESTSClient();

    private List<Handler> handlers;

    private PGEConfiguration config;

    private Service service;

    private Map<String, Dispatch<SOAPMessage>> dispatchers = new HashMap<>();

    private StoreBean keyStoreSSL;

    private StoreBean trustStore;

    private ClientCredential clientCredential;

    private SSLContextInitializer sslContextInitializer;

    public PDIContext(PYSPushProperties pysPushProperties, ResourceResolver resourceResolver, GitManager gitManager) {
        this.pysPushProperties = pysPushProperties;
        this.resourceResolver = resourceResolver;
        this.gitManager = gitManager;
    }

    public void init(PGEConfiguration config) throws ConfigurationException, PGEContextException {
        this.handlers = new ArrayList();
        this.handlers.add(new LoggingHandler());
        if (this.pysPushProperties.getAccessPDIEnabled()) {
            this.handlers.add(new SAMLHandler());
        }
        this.handlers.add(new WSAddressingHandler());
        this.handlers.add(new LoggingHandler());
        if (config == null) {
            try {
                this.config = new PGEConfiguration(this.pysPushProperties.getConfigFile());
            } catch (Exception e) {
                log.warn(e.getLocalizedMessage(), e);
            }
        } else {
            this.config = config;
        }

        initKeyStoreBean();

        try {
            if (this.config != null) {
                String sslInitClazz = this.config.getSTSPropValue("SSLContextInitializer");
                if (sslInitClazz != null) {
                    Class clazz = SecurityActions.loadClass(getClass(), sslInitClazz);
                    this.sslContextInitializer = ((SSLContextInitializer) clazz.newInstance());
                }
            }
            this.service = Service.create(new QName("http://uy.gub.agesic.pdi/pys/", "PUSH"));
        } catch (Exception e) {
            throw new PGEContextException(e.getMessage(), e);
        }
    }

    @Loggable
    public SOAPMessage invokeService(String serviceKey, String wsaTo, String wsaAction, String wsaMessageID, String xml, String url, Entrega entrega) throws Exception {
        Dispatch<SOAPMessage> dispatch;
        serviceKey = serviceKey.replaceAll("\\s+", "");
        synchronized (dispatchers) {
            if (!dispatchers.containsKey(serviceKey)) {
                QName portQName = new QName(wsaTo, serviceKey);
                service.addPort(portQName, SOAPBinding.SOAP11HTTP_BINDING, url);
                dispatch = service.createDispatch(portQName, SOAPMessage.class, Service.Mode.MESSAGE);
                dispatchers.put(serviceKey, dispatch);
            }
            dispatch = dispatchers.get(serviceKey);
            initBindingProvider(dispatch);
        }

        dispatch.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);

        String soapAction = !wsaAction.startsWith("\"") ? "\"" + wsaAction + "\"" : wsaAction;
        dispatch.getRequestContext().put(BindingProvider.SOAPACTION_URI_PROPERTY, soapAction);
        dispatch.getRequestContext().put(BindingProvider.SOAPACTION_USE_PROPERTY, Boolean.TRUE);

        initAddressingProperties(dispatch, wsaTo, wsaAction, wsaMessageID);
        //Create SOAP payload
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage requestMessage = messageFactory.createMessage();
        SOAPPart part = requestMessage.getSOAPPart();
        SOAPEnvelope envelope = part.getEnvelope();
        envelope.addNamespaceDeclaration("wsa", "http://www.w3.org/2005/08/addressing");

        SOAPFactory soapFactory = SOAPFactory.newInstance();
        String prefix = "ps";
        String uri = "http://ps.agesic.gub.uy";
        SOAPElement element = soapFactory.createElement("notificationId", prefix, uri);
        element.addTextNode(entrega.getUuid());
        SOAPHeader header = envelope.getHeader();
        header.addChildElement(element);

        element = soapFactory.createElement("producer", prefix, uri);
        element.addTextNode(entrega.getNovedad().getProductor().getNombre());
        header.addChildElement(element);

        element = soapFactory.createElement("createTS", prefix, uri);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        element.addTextNode(dateFormat.format(entrega.getFechaCreado()));
        header.addChildElement(element);

        if (entrega.getReason() != null) {
            element = soapFactory.createElement("reason", prefix, uri);
            element.addTextNode(entrega.getReason());
            header.addChildElement(element);
        }

        //String xml to document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder db = factory.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml));
        Document doc = db.parse(is);

        //injectXMLInSOAP
        SOAPBody soapBody = envelope.getBody();
        soapBody.addDocument(doc);
        requestMessage.saveChanges();

        if (this.pysPushProperties.getAccessPDIEnabled()) {
            //queryForIssueToken
            Response<SOAPMessage> token = issueToken(wsaTo);
            if (log.isTraceEnabled() && token != null) {
                SOAPMessage msg = token.get();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                msg.writeTo(out);
                String strMsg = new String(out.toByteArray());
                log.trace("STS Response - " + strMsg);
            }

            //injectIssueTokenInSOAP injected by handler
            Map<String, Object> requestContextProperties = dispatch.getRequestContext();
            requestContextProperties.put("uy.gub.agesic.security.saml", token);

        }
        return dispatch.invoke(requestMessage);
    }

    @Loggable
    private Response<SOAPMessage> issueToken(String wsaTo) throws Exception {
        log.debug("Request STS...");
        RSTBean bean = createRSTBean();
        bean.setService(wsaTo);

        Response<SOAPMessage> stsResponse = this.stsClient.requestSecurityToken(bean, this.clientCredential, this.keyStoreSSL, this.trustStore, this.config, this.sslContextInitializer);
        //NO SACAR. Esto hay que hacerlo para el caso de que falle el STS tire la exception sino queda encapsulada en el response.
        if (stsResponse != null) {
            stsResponse.get();
        }

        return stsResponse;
    }

    public void addHandlerChain(Handler handler) {
        this.handlers.add(handler);
    }

    public Service getService() {
        return this.service;
    }

    private RSTBean createRSTBean() throws PGEContextException {
        if (config != null) {
            String issuer = this.config.getSTSPropValue("Issuer");
            String policyName = this.config.getSTSPropValue("Policy");
            String role = this.config.getSTSPropValue("Role");
            String userName = this.config.getSTSPropValue("Username");

            RSTBean bean = new RSTBean();
            bean.setIssuer(issuer);
            bean.setPolicyName(policyName);
            bean.setRole(role);
            bean.setUserName(userName);

            return bean;
        } else {
            throw new PGEContextException("El push no está configurado adecuadamente. Revise el valor de la property \"application.accessPDI.enabled\" y si existe el archivo de configuracion pdi-config.xml");
        }
    }

    private void initBindingProvider(Dispatch<SOAPMessage> dispatcher) {
        Map<String, Object> requestContextProperties = dispatcher.getRequestContext();

        if (this.config != null) {
            requestContextProperties.put("STSTimeOut", this.config.getSTSLongPropValue("STSTimeOut"));
        }

        if (this.sslContextInitializer != null && this.pysPushProperties.getAccessPDIEnabled()) {
            this.sslContextInitializer.initSSLContext(dispatcher, this.config);
        }

        requestContextProperties.put("javax.xml.ws.client.receiveTimeout", this.pysPushProperties.getWsRequestTimeout()); // Timeout in millis
        requestContextProperties.put("javax.xml.ws.client.connectionTimeout", this.pysPushProperties.getWsConnectTimeout()); // Timeout in millis

        List<Handler> handlerChain = new ArrayList();
        handlerChain.addAll(this.handlers);
        dispatcher.getBinding().setHandlerChain(handlerChain);
    }

    private void initAddressingProperties(Dispatch<SOAPMessage> dispatcher, String wsaTo, String wsaAction, String wsaMessageID) throws ConfigurationException {
        Map<String, Object> requestContextProperties = dispatcher.getRequestContext();
        try {
            AddressingProperties addrProps = buildAddressingProperties(wsaTo, wsaAction, wsaMessageID);
            requestContextProperties.put(ADDRESSING_CONTEXT, addrProps);
        } catch (URISyntaxException e) {
            String msg = String.format("Error al procesar wsaTo=%s. %s", wsaTo, e.getMessage());
            throw new ConfigurationException(msg, e);
        }

    }

    private AddressingProperties buildAddressingProperties(String wsaTo, String wsaAction, String wsaMessageID) throws URISyntaxException {
        AddressingBuilder builder = AddressingBuilder.getAddressingBuilder();

        AddressingProperties outProps = builder.newAddressingProperties();
        outProps.setTo(builder.newURI(wsaTo));
        outProps.setAction(builder.newURI(wsaAction));
        outProps.setMessageID(builder.newURI(wsaMessageID));
        return outProps;
    }

    private void initKeyStoreBean() throws ConfigurationException {
        if (this.config != null) {
            String alias = this.config.getKeyStoreAuthValue("KeyStoreAlias");
            String keyStoreUrl = this.config.getKeyStoreAuthValue("KeyStoreURL");
            String keyStorePwd = this.config.getKeyStoreAuthValue("KeyStorePass");
            String trustStoreUrl = this.config.getKeyStoreAuthValue("TrustStoreURL");
            String trustStorePwd = this.config.getKeyStoreAuthValue("TrustStorePass");
            String sslKeyStoreUrl = this.config.getKeyStoreAuthValue("SSLKeyStoreURL");
            String sslKeyStorePwd = this.config.getKeyStoreAuthValue("SSLKeyStorePass");

            try {
                this.keyStoreSSL = new StoreBean();
                this.keyStoreSSL.setAlias(alias);
                this.keyStoreSSL.setStoreFilePath(KeyStoreUtil.getKeystorePath(sslKeyStoreUrl));
                this.keyStoreSSL.setStorePwd(sslKeyStorePwd);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }

            try {
                this.trustStore = new StoreBean();
                this.trustStore.setStoreFilePath(KeyStoreUtil.getKeystorePath(trustStoreUrl));
                this.trustStore.setStorePwd(trustStorePwd);

            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }

            try {
                KeyStore keyStore = KeyStoreUtil.loadKeyStore(keyStoreUrl, keyStorePwd);
                X509Certificate x509Certificate = (X509Certificate) keyStore.getCertificate(alias);
                Key key = keyStore.getKey(alias, keyStorePwd.toCharArray());
                this.clientCredential = new ClientCredential();
                this.clientCredential.setCertificate(x509Certificate);
                this.clientCredential.setPrivateKey((PrivateKey) key);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

}

