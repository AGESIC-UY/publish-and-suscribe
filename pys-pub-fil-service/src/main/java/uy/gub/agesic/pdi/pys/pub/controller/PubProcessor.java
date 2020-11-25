package uy.gub.agesic.pdi.pys.pub.controller;

import jlibs.xml.DefaultNamespaceContext;
import jlibs.xml.sax.dog.XMLDog;
import jlibs.xml.sax.dog.XPathResults;
import jlibs.xml.sax.dog.expr.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.InputSource;
import uy.gub.agesic.pdi.common.logging.PDIHostName;
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
import java.util.Date;
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

    private final SuscriptorService suscriptorService;

    private final FilterTopicService filterTopicService;

    private final RestTemplate restTemplate;

    @Value("${application.fil-service.url}")
    private String filServiceUrl;

    @Autowired
    @java.lang.SuppressWarnings("squid:S00107")
    public PubProcessor(NovedadService novedadService, WsaInspector wsaInspector, PYSHeadersProcessor pysHeadersProcessor,
                        PYSSoapTransformer pysSoapTransformer, TopicoService topicoService, ProductorService productorService,
                        PYSProperties pysProperties, SoapErrorProcessor soapErrorProcessor, TopicoSuscriptorService topicoSuscriptorService,
                        EntregaService entregaService, SuscriptorService suscriptorService, FilterTopicService filterTopicService, RestTemplate restTemplate) {
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
        this.suscriptorService = suscriptorService;
        this.filterTopicService = filterTopicService;
        this.restTemplate = restTemplate;
    }

    public void publicar(HttpServletRequest req, HttpServletResponse resp, boolean https) {
        Canonical<SoapPayload> canonical = null;
        try {
            MDC.put("host", PDIHostName.HOST_NAME);
            logger.info("Se inicia el proceso: Publicando novedad.");
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
            logger.debug(String.format(
                    "Extrayendo datos de la peticion: Topico: %s, Productor: %s, DN: %s",
                    topic,
                    producer,
                    dn
            ));

            xml = pysSoapTransformer.extractBody(xml, cs);

            logger.debug(String.format("Buscando topico '%s' en el listado de topicos.", topic));
            Topico topico = topicoService.buscar(topic);
            logger.debug("Chequeando el topico.");
            checkTopic(topico);

            MDC.put("topic", topic);

            logger.debug(String.format("Buscando productor '%s' en el listado de productores.", producer));
            Productor productor = productorService.buscar(producer);
            logger.debug("Chequeando el productor.");
            checkProducer(productor);

            //valida DN si esta habilitado el acceso a traves de la pdi
            if (pysProperties.getAccessPDIEnabled()) {
                String producerDN = productor.getDn();
                if (!dn.equalsIgnoreCase(producerDN)) {
                    logger.info("El DN ingresado no corresponde al productor.");
                    throw new PSException("El DN ingresado no corresponde al productor", null, Constants.ERRORMATCHDN, null);
                }
            }

            //Valida que existe la relacion topicoProductor
            if (!this.productorService.existeTopicoProductor(topico, productor)) {
                logger.info("El productor no tiene topico asociado.");
                throw new PSException("El productor no tiene el t\u00F3pico asociado", null, Constants.ERRORMATCHPRODTOPICO, null);
            }

            //Valida que el root element corresponda con el definido en el topico
            DefaultNamespaceContext nsContext = new DefaultNamespaceContext();
            nsContext.declarePrefix("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
            nsContext.declarePrefix("ps", "http://ps.agesic.gub.uy");
            nsContext.declarePrefix("topicNs", topico.getNamespace());

            String rootElement = "topicNs:" + topico.getElementoRaiz();
            logger.debug(String.format("Validando que el root element: '%s' corresponda con el definido en el topico.", rootElement));
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
                logger.debug("El elemento raiz de la novedad no corresponde al del topico.");
                throw new PSException("El elemento ra\u00EDz de la novedad no corresponde al del t\u00F3pico", null, Constants.ERRORMATCHROOTELEMENT, null);
            } else {
                logger.debug("El elemento raiz de la novedad corresponde al del topico.");
            }

            logger.info(" - Creando novedad.");
            Novedad novedad = new Novedad();
            novedad.setContenido(xml);
            novedad.setProductor(productor);
            novedad.setTopico(topico);
            novedad.setWsaMessageId(wsaMessageID);
            novedad.setWsaRelatesTo(wsaRelatesTo);
            logger.info(" - Registrando novedad.");
            novedad = this.novedadService.registrar(novedad);
            MDC.put("novedadID", novedad.getUuid());
            logger.info(String.format(" - Novedad registrada con ID '%s'.", novedad.getUuid()));

            String response = pysSoapTransformer.pubResponse(novedad.getUuid(), canonical, cs);
            logger.debug(response);

            canonical.getPayload().setContentType("text/xml;charset=UTF-8");
            canonical.getPayload().setBase64Data(CanonicalProcessor.encodeData(response, "UTF-8"));

            logger.info("Se inicia el proceso de creacion de entrega.");
            processDelivery(novedad, topico);
        } catch (Exception e) {
            logger.error("Error intentando publicar una novedad.", e);
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
            logger.debug("Topico no encontrado.");
            throw new PSException("T\u00F3pico no encontrado", null, Constants.ERRORMATCHTOPIC, null);
        }
        if (!topico.getHabilitado()) {
            logger.debug("Topico no habilitado.");
            throw new PSException("T\u00F3pico no habilitado", null, Constants.ERRORTOPICONOHAB, null);
        }
        logger.debug("Topico encontrado.");
    }

    private void checkProducer(Productor productor) throws PSException {
        if (productor == null) {
            logger.debug("Productor no encontrado.");
            throw new PSException("Publisher no encontrado", null, Constants.ERRORMATCHPUBLISHER, null);
        }
        if (!productor.getHabilitado()) {
            logger.debug("Productor no habilitado.");
            throw new PSException("Publisher no habilitado", null, Constants.ERRORPRODNOHAB, null);
        }
        logger.debug("Topico encontrado.");
    }

    private void processDelivery(Novedad novedad, Topico topico) throws PSException {
        if (this.containFilters(topico)) {
            String resourceUrl = String.format("%s/%s/%s", filServiceUrl, topico.getId(), novedad.getId());
            logger.debug("Url para consumir servicio: " + resourceUrl);

            try {
                logger.debug("Consumiendo servicio.");
                ResponseEntity<List<FilterSubscriber>> response = restTemplate.exchange(
                        resourceUrl,
                        HttpMethod.POST,
                        null,
                        new ParameterizedTypeReference<List<FilterSubscriber>>() {
                        }
                );

                logger.debug(String.format("Codigo de respuesta del servicio: %s", response.getStatusCode().toString()));
                if (response.getStatusCode().equals(HttpStatus.OK)) {
                    List<FilterSubscriber> filterSubscriberList = response.getBody();
                    logger.debug(String.format("Se obtienen un total de: %d suscriptores", filterSubscriberList.size()));

                    for (FilterSubscriber filterSubscriber : filterSubscriberList) {
                        logger.debug(String.format("Buscando suscriptor por ID: %s", filterSubscriber.getSubscriberId()));
                        Suscriptor subscriber = this.suscriptorService.searchById(filterSubscriber.getSubscriberId());
                        if (subscriber != null) {
                            createDelivery(subscriber, novedad, EstadoEntrega.PENDIENTE.name(), novedad.getUuid(), novedad.getFecha(), filterSubscriber.getReason());
                        } else {
                            logger.debug(String.format("El suscriptor con ID: %s no fue encontrado.", filterSubscriber.getSubscriberId()));
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Error procesando la creacion de la entrega", e);
            }
        } else {
            logger.info(String.format("El topico %s no tiene filtros asociados, se envia a cada uno de los subcriptores asociados al topico.", topico.getNombre()));

            List<TopicoSuscriptor> list = topicoSuscriptorService.buscar(topico);
            for (TopicoSuscriptor topicoSuscriptor : list) {
                if (topicoSuscriptor.getSuscriptor().getHabilitado()) {
                    createDelivery(topicoSuscriptor.getSuscriptor(), novedad, EstadoEntrega.PENDIENTE.name(), novedad.getUuid(), novedad.getFecha(), "");
                }
            }
        }
    }

    private void createDelivery(Suscriptor suscriptor, Novedad novedad, String estado, String uuid, Date fechaCreado, String reason) {
        logger.info(String.format("Creando entrega para el suscriptor: %s", suscriptor.getNombre()));
        Entrega entrega = new Entrega();
        entrega.setSuscriptor(suscriptor);
        entrega.setNovedad(novedad);
        entrega.setEstado(estado);
        entrega.setUuid(uuid);
        entrega.setFechaCreado(fechaCreado);
        entrega.setReason(reason);

        logger.debug(
                String.format(
                        "Datos de la entrega: Suscriptor: %s, Novedad: %s, Estado: %s, Uuid: %s, Reason: %s",
                        suscriptor.getNombre(),
                        novedad.getUuid(),
                        estado,
                        uuid,
                        reason
                )
        );

        try {
            entregaService.upsert(entrega);
            logger.info("Entrega realizada.");
        } catch (PSException e) {
            logger.error("Error creando entrega.", e);
        }
    }

    private boolean containFilters(Topico topic) {
        logger.info(String.format("Evaluando si tiene filtros asociados el topico: %s", topic.getNombre()));
        List<FilterTopic> filterTopicList = filterTopicService.searchFilterTopicsByTopic(topic);
        return filterTopicList != null && !filterTopicList.isEmpty();
    }
}
