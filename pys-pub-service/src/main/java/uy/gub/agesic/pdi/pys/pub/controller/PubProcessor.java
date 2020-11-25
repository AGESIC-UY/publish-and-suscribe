package uy.gub.agesic.pdi.pys.pub.controller;

import jlibs.xml.DefaultNamespaceContext;
import jlibs.xml.sax.dog.XMLDog;
import jlibs.xml.sax.dog.XPathResults;
import jlibs.xml.sax.dog.expr.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;
import uy.gub.agesic.pdi.common.message.canonical.Canonical;
import uy.gub.agesic.pdi.common.message.soap.SoapPayload;
import uy.gub.agesic.pdi.common.utiles.CanonicalProcessor;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.backend.service.*;
import uy.gub.agesic.pdi.pys.backend.soap.PYSHeadersProcessor;
import uy.gub.agesic.pdi.pys.backend.soap.PYSSoapTransformer;
import uy.gub.agesic.pdi.pys.backend.soap.SoapErrorProcessor;
import uy.gub.agesic.pdi.pys.backend.soap.WsaInspector;
import uy.gub.agesic.pdi.pys.common.util.Constants;
import uy.gub.agesic.pdi.pys.common.util.WSUtil;
import uy.gub.agesic.pdi.pys.domain.*;
import uy.gub.agesic.pdi.pys.pub.config.PYSProperties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.StringReader;
import java.util.List;

@Service
public class PubProcessor {

    private static final Logger logger = LoggerFactory.getLogger(PubProcessor.class);

    private final NovedadService novedadService;

    private final TopicoService topicoService;

    private final ProductorService productorService;

    private final WsaInspector wsaInspector;

    private final PYSHeadersProcessor pysHeadersProcessor;

    private final PYSSoapTransformer pysSoapTransformer;

    private final PYSProperties pysProperties;

    private final SoapErrorProcessor soapErrorProcessor;

    private final TopicoSuscriptorService topicoSuscriptorService;

    private final EntregaService entregaService;

    @Autowired
    @java.lang.SuppressWarnings("squid:S00107")
    public PubProcessor(NovedadService novedadService, WsaInspector wsaInspector, PYSHeadersProcessor pysHeadersProcessor,
                        PYSSoapTransformer pysSoapTransformer, TopicoService topicoService, ProductorService productorService,
                        PYSProperties pysProperties, SoapErrorProcessor soapErrorProcessor, TopicoSuscriptorService topicoSuscriptorService,
                        EntregaService entregaService) {
        this.novedadService = novedadService;
        this.wsaInspector = wsaInspector;
        this.pysHeadersProcessor = pysHeadersProcessor;
        this.pysSoapTransformer = pysSoapTransformer;
        this.topicoService = topicoService;
        this.productorService = productorService;
        this.pysProperties = pysProperties;
        this.soapErrorProcessor = soapErrorProcessor;
        this.topicoSuscriptorService = topicoSuscriptorService;
        this.entregaService = entregaService;

    }

    public void publicar(HttpServletRequest req, HttpServletResponse resp, boolean https) {
        Canonical<SoapPayload> canonical = null;
        try {
            canonical = WSUtil.initWS(req);

            SoapPayload soapPayload = canonical.getPayload();
            String xml = CanonicalProcessor.decodeSoap(soapPayload);
            String cs = CanonicalProcessor.getCharSet(canonical);

            if (req.getScheme().equalsIgnoreCase("http") && https) {
                throw new PSException("HTTPS only", null, Constants.HTTPSONLY, null);
            }

            wsaInspector.processHeaders(canonical, xml);
            String wsaMessageID = wsaInspector.getWsaMessageID(canonical);
            MDC.put(Constants.MESSAGEID_HEADER_NAME, wsaMessageID);
            String wsaRelatesTo = wsaInspector.getWsaRelatesTo(canonical);
            MDC.put("wsaRelatesTo", wsaRelatesTo);

            pysHeadersProcessor.processHeadersPublisher(canonical, xml);
            String topic = pysHeadersProcessor.getTopic(canonical);
            String producer = pysHeadersProcessor.getProducer(canonical);
            String dn = pysHeadersProcessor.getDN(canonical);

            xml = pysSoapTransformer.extractBody(xml, cs);

            Topico topico = topicoService.buscar(topic);

            checkTopic(topico);

            MDC.put("topic", topic);

            Productor productor = productorService.buscar(producer);

            checkProducer(productor);

            //valida DN si esta habilitado el acceso a traves de la pdi
            if (pysProperties.getAccessPDIEnabled()) {
                String producerDN = productor.getDn();

                if (!dn.equalsIgnoreCase(producerDN)) {
                    throw new PSException("El DN ingresado no corresponde al productor", null, Constants.ERRORMATCHDN, null);
                }
            }

            //Valida que existe la relacion topicoProductor
            if (!this.productorService.existeTopicoProductor(topico, productor)) {
                throw new PSException("El productor no tiene el t\u00F3pico asociado", null, Constants.ERRORMATCHPRODTOPICO, null);
            }

            //Valida que el root element corresponda con el definido en el topico
            DefaultNamespaceContext nsContext = new DefaultNamespaceContext();
            nsContext.declarePrefix("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
            nsContext.declarePrefix("ps", "http://ps.agesic.gub.uy");
            nsContext.declarePrefix("topicNs", topico.getNamespace());

            String rootElement = "topicNs:" + topico.getElementoRaiz();

            XMLDog dog = new XMLDog(nsContext, null, null);
            String xpath = "count(/" + rootElement + ") = 1";
            Expression xpathExpr = dog.addXPath(xpath);

            Boolean correctRootElement = false;
            try (StringReader xmlStrReader = new StringReader(xml)) {
                InputSource source = new InputSource(xmlStrReader);
                XPathResults results = dog.sniff(source);
                correctRootElement = (Boolean) results.getResult(xpathExpr);
            }
            if (!correctRootElement) {
                throw new PSException("El elemento ra\u00EDz de la novedad no corresponde al del t\u00F3pico", null, Constants.ERRORMATCHROOTELEMENT, null);
            }

            Novedad novedad = new Novedad();
            novedad.setContenido(xml);
            novedad.setProductor(productor);
            novedad.setTopico(topico);
            novedad.setWsaMessageId(wsaMessageID);
            novedad.setWsaRelatesTo(wsaRelatesTo);

            novedad = this.novedadService.registrar(novedad);
            MDC.put("novedadID", novedad.getUuid());

            String response = pysSoapTransformer.pubResponse(novedad.getUuid(), canonical, cs);


            canonical.getPayload().setContentType("text/xml;charset=UTF-8");
            canonical.getPayload().setBase64Data(CanonicalProcessor.encodeData(response, "UTF-8"));
            String info = "Novedad publicada";
            logger.info(info);

            createDelivery(novedad, topico);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            if (canonical == null) {
                canonical = CanonicalProcessor.createSoapCanonical(null);
            }
            soapErrorProcessor.processErrors(canonical, e);
        } finally {
            WSUtil.generateResponse(resp, canonical);
        }
    }

    private void checkTopic(Topico topico) throws PSException {
        if (topico == null) {
            throw new PSException("T\u00F3pico no encontrado", null, Constants.ERRORMATCHTOPIC, null);
        }

        if (!topico.getHabilitado()) {
            throw new PSException("T\u00F3pico no habilitado", null, Constants.ERRORTOPICONOHAB, null);
        }
    }

    private void checkProducer(Productor productor) throws PSException {
        if (productor == null) {
            throw new PSException("Publisher no encontrado", null, Constants.ERRORMATCHPUBLISHER, null);
        }

        if (!productor.getHabilitado()) {
            throw new PSException("Publisher no habilitado", null, Constants.ERRORPRODNOHAB, null);
        }
    }

    private void createDelivery(Novedad novedad, Topico topico) throws PSException {
        List<TopicoSuscriptor> list = topicoSuscriptorService.buscar(topico);
        for (TopicoSuscriptor topicoSuscriptor : list) {
            if (topicoSuscriptor.getSuscriptor().getHabilitado()) {
                Entrega entrega = new Entrega();
                entrega.setSuscriptor(topicoSuscriptor.getSuscriptor());
                entrega.setNovedad(novedad);
                entrega.setEstado(EstadoEntrega.PENDIENTE.name());
                entrega.setUuid(novedad.getUuid());
                entrega.setFechaCreado(novedad.getFecha());
                entregaService.upsert(entrega);
            }
        }
    }

}
