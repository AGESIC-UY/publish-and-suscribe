package uy.gub.agesic.pdi.pys.fil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import uy.gub.agesic.pdi.pys.backend.repository.*;
import uy.gub.agesic.pdi.pys.domain.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FilterServiceApplicationTests {
    @Autowired
    private TopicoRepository topicoRepository;
    @Autowired
    private ProductorRepository productorRepository;
    @Autowired
    private SuscriptorRepository suscriptorRepository;
    @Autowired
    private TopicoProductorRepository topicoProductorRepository;
    @Autowired
    private TopicoSuscriptorRepository topicoSuscriptorRepository;
    @Autowired
    private FilterRepository filterRepository;
    @Autowired
    private FilterTopicRepository filterTopicRepository;
    @Autowired
    private NovedadRepository novedadRepository;
    @Autowired
    private FilterService service;

    @Value("classpath:request_002.xml")
    Resource request1;

    private List<Productor> publishers;

    private List<Suscriptor> subscribers;

    private List<Topico> topics;

    private List<Filter> filters;

    private List<FilterTopic> filtersTopics;

    private List<Novedad> novelties;

    @Before
    public void setUp() {
        this.createPublishers();
        this.createSubscribers();
        this.createTopics();
        this.createTopicsPublishers();
        this.createTopicsSubscribers();
        this.createFilters();
        this.createTopicsFilters();
        this.createNovelties();
    }

    @Test
    public void contextLoads() {
        Assert.assertEquals(3, service.evaluate(this.topics.get(0).getId(), this.novelties.get(0).getId()).size());
        Assert.assertEquals(5, service.evaluate(this.topics.get(1).getId(), this.novelties.get(0).getId()).size());
        Assert.assertEquals(1, service.evaluate(this.topics.get(2).getId(), this.novelties.get(0).getId()).size());
        Assert.assertEquals(0, service.evaluate(this.topics.get(3).getId(), this.novelties.get(0).getId()).size());
        Assert.assertEquals(6, service.evaluate(this.topics.get(4).getId(), this.novelties.get(0).getId()).size());
    }

    private String getFileContent(InputStream is) throws IOException {
        StringBuilder xml = new StringBuilder();
        String line;

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is))) {
            while ((line = bufferedReader.readLine()) != null) {
                xml.append(line);
            }
        }
        return xml.toString();
    }

    private void createPublishers() {
        this.publishers = new ArrayList<>();

        List<String> publishersDn = Stream.of(new String[] {
                "OU=hospital,O=Sanatorio 1,C=UY",
                "OU=hospital,O=Sanatorio 2,C=UY",
                "OU=hospital,O=Sanatorio 3,C=UY",
                "OU=hospital,O=Sanatorio 4,C=UY",
                "OU=hospital,O=Sanatorio 5,C=UY",
                "OU=hospital,O=Sanatorio 6,C=UY"
        }).collect(Collectors.toList());

        for (int i = 0; i < 3; i++) {
            Productor publisher = new Productor();
            publisher.setNombre("Publisher " + (i + 1));
            publisher.setDn(publishersDn.get(i));
            publisher.setFechaCreacion(new Date());
            publisher.setHabilitado(true);

            this.productorRepository.save(publisher);
            this.publishers.add(publisher);
        }
    }

    private void createSubscribers() {
        this.subscribers = new ArrayList<>();

        List<String> subscribersDn = Stream.of(new String[] {
                "OU=hospital,O=Sanatorio 1,C=UY",
                "OU=hospital,O=Sanatorio 2,C=UY",
                "OU=hospital,O=Sanatorio 3,C=UY",
                "OU=hospital,O=Sanatorio 4,C=UY",
                "OU=hospital,O=Sanatorio 5,C=UY",
                "OU=hospital,O=Sanatorio 6,C=UY"
        }).collect(Collectors.toList());

        for (int i = 0; i < 6; i++) {
            Suscriptor subscriber = new Suscriptor();
            subscriber.setNombre("Subscriber " + (i + 1));
            subscriber.setDn(subscribersDn.get(i));
            subscriber.setFechaCreacion(new Date());
            subscriber.setHabilitado(true);

            this.suscriptorRepository.save(subscriber);
            this.subscribers.add(subscriber);
        }
    }

    private void createTopics() {
        this.topics = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Topico topic = new Topico();
            topic.setNombre("Topic " + (i + 1));
            topic.setFechaCreacion(new Date());
            topic.setHabilitado(true);
            topic.setElementoRaiz("ProvideAndRegisterDocumentSetRequest");
            topic.setNamespace("urn:ihe:iti:xds-b:2007");
            topic.setSoapAction("http://novedades.agesic.gub.uy/soap-server#nuevaNovedad");

            this.topicoRepository.save(topic);
            topics.add(topic);
        }
    }

    private void createTopicsPublishers() {
        for (Topico topic: this.topics) {
            for (Productor publisher: this.publishers) {
                TopicoProductor topicPublisher = new TopicoProductor();
                topicPublisher.setTopico(topic);
                topicPublisher.setProductor(publisher);
                this.topicoProductorRepository.save(topicPublisher);
            }
        }
    }

    private void createTopicsSubscribers() {
        for (Topico topic: this.topics) {
            int counter, cursorDeliveryModes = 0;
            String deliveryMode;

            switch (topic.getNombre()) {
                case "Topic 1": {
                    counter = 0;
                    List<String> deliveryModes = Stream.of(new String[] {
                            "PUSH",
                            "PULL",
                            "PULL",
                            "PULL",
                            "PUSH",
                            "PUSH",
                    }).collect(Collectors.toList());

                    for (Suscriptor subscriber: this.subscribers) {
                        deliveryMode = deliveryModes.get(counter++);

                        TopicoSuscriptor topicSubscriber = new TopicoSuscriptor();
                        topicSubscriber.setTopico(topic);
                        topicSubscriber.setSuscriptor(subscriber);
                        topicSubscriber.setDeliveryMode(deliveryMode);
                        if (deliveryMode.equals("PUSH")) {
                            topicSubscriber.setDeliveryAddr("http://localhost:23000/push");
                            topicSubscriber.setDeliveryWsaTo("http://deliverywsato");
                        }
                        this.topicoSuscriptorRepository.save(topicSubscriber);
                    }
                }break;
                case "Topic 2": {
                    counter = 0;
                    List<String> deliveryModes = Stream.of(new String[] {
                            "PUSH",
                            "PULL",
                            "PULL"
                    }).collect(Collectors.toList());

                    for (Suscriptor subscriber: this.subscribers) {
                        if (counter < 3) {
                            deliveryMode = deliveryModes.get(counter++);

                            TopicoSuscriptor topicSubscriber = new TopicoSuscriptor();
                            topicSubscriber.setTopico(topic);
                            topicSubscriber.setSuscriptor(subscriber);
                            topicSubscriber.setDeliveryMode(deliveryMode);
                            if (deliveryMode.equals("PUSH")) {
                                topicSubscriber.setDeliveryAddr("http://localhost:23000/push");
                                topicSubscriber.setDeliveryWsaTo("http://deliverywsato");
                            }
                            this.topicoSuscriptorRepository.save(topicSubscriber);
                        } else {
                            break;
                        }
                    }
                }break;
                case "Topic 3": {
                    counter = 0;
                    cursorDeliveryModes = 0;
                    List<String> deliveryModes = Stream.of(new String[] {
                            "PUSH",
                            "PULL",
                            "PULL"
                    }).collect(Collectors.toList());

                    for (Suscriptor subscriber: this.subscribers) {
                        if (counter%2 == 0) {
                            deliveryMode = deliveryModes.get(cursorDeliveryModes++);

                            TopicoSuscriptor topicSubscriber = new TopicoSuscriptor();
                            topicSubscriber.setTopico(topic);
                            topicSubscriber.setSuscriptor(subscriber);
                            topicSubscriber.setDeliveryMode(deliveryMode);
                            if (deliveryMode.equals("PUSH")) {
                                topicSubscriber.setDeliveryAddr("http://localhost:23000/push");
                                topicSubscriber.setDeliveryWsaTo("http://deliverywsato");
                            }
                            this.topicoSuscriptorRepository.save(topicSubscriber);
                        }
                        counter++;
                    }
                }break;
                case "Topic 4": {
                    counter = 0;
                    cursorDeliveryModes = 0;
                    List<String> deliveryModes = Stream.of(new String[] {
                            "PUSH",
                            "PULL",
                            "PULL"
                    }).collect(Collectors.toList());

                    for (Suscriptor subscriber: this.subscribers) {
                        if (counter >= 3) {
                            deliveryMode = deliveryModes.get(cursorDeliveryModes++);

                            TopicoSuscriptor topicSubscriber = new TopicoSuscriptor();
                            topicSubscriber.setTopico(topic);
                            topicSubscriber.setSuscriptor(subscriber);
                            topicSubscriber.setDeliveryMode(deliveryMode);
                            if (deliveryMode.equals("PUSH")) {
                                topicSubscriber.setDeliveryAddr("http://localhost:23000/push");
                                topicSubscriber.setDeliveryWsaTo("http://deliverywsato");
                            }
                            this.topicoSuscriptorRepository.save(topicSubscriber);
                        }
                        counter++;
                    }
                }break;
                case "Topic 5": {
                    counter = 0;
                    cursorDeliveryModes = 0;
                    List<String> deliveryModes = Stream.of(new String[] {
                            "PULL",
                            "PUSH",
                            "PULL"
                    }).collect(Collectors.toList());

                    for (Suscriptor subscriber: this.subscribers) {
                        if (counter >= 3) {
                            deliveryMode = deliveryModes.get(cursorDeliveryModes++);

                            TopicoSuscriptor topicSubscriber = new TopicoSuscriptor();
                            topicSubscriber.setTopico(topic);
                            topicSubscriber.setSuscriptor(subscriber);
                            topicSubscriber.setDeliveryMode(deliveryMode);
                            if (deliveryMode.equals("PUSH")) {
                                topicSubscriber.setDeliveryAddr("http://localhost:23000/push");
                                topicSubscriber.setDeliveryWsaTo("http://deliverywsato");
                            }
                            this.topicoSuscriptorRepository.save(topicSubscriber);
                        }
                        counter++;
                    }
                }break;
            }
        }
    }

    private void createFilters() {
        this.filters = new ArrayList<>();

        List<Filter.Operator> operatorsList = Stream.of(
                Filter.Operator.AND,
                Filter.Operator.AND,
                Filter.Operator.AND,
                Filter.Operator.AND,
                Filter.Operator.OR,
                Filter.Operator.AND,
                Filter.Operator.OR,
                Filter.Operator.OR,
                Filter.Operator.OR,
                Filter.Operator.OR,
                Filter.Operator.OR
        ).collect(Collectors.toCollection(ArrayList::new));

        List<List<FilterRule>> filterRules = this.createFilterRules();

        for (int i = 0; i < 11; i++) {
            Filter filter = Filter.builder()
                    .name("Filter " + (i + 1))
                    .documentType(Filter.DocumentType.XML)
                    .operator(operatorsList.get(i))
                    .rules(filterRules.get(i))
                    .build();
            this.filterRepository.save(filter);
            this.filters.add(filter);
        }
    }

    private List<List<FilterRule>> createFilterRules() {
        List<List<FilterRule>> filterRules = new ArrayList<>();

        List<FilterRule> filter1Rules = new ArrayList<>();
        FilterRule filter1Rule1 = FilterRule.builder()
                .name("filter1Rule1")
                .leftFactor(Factor.builder()
                        .type(Factor.Type.XPATH)
                        .valueType(Factor.ValueType.TEXT)
                        .value("//urn1:ExtrinsicObject//descendant::urn1:Slot[@name='sourcePatientId']/urn1:ValueList/urn1:Value/text()")
                        .build())
                .rightFactor(Factor.builder()
                        .type(Factor.Type.FIXEDTEXT)
                        .value("87847883^^^&2.16.858.2.10000005.72768.20&ISO")
                        .build())
                .operator(FilterRule.Operator.EQUAL)
                .build();
        filter1Rules.add(filter1Rule1);

        FilterRule filter1Rule2 = FilterRule.builder()
                .name("filter1Rule2")
                .leftFactor(Factor.builder()
                        .type(Factor.Type.XPATH)
                        .valueType(Factor.ValueType.NUMBER)
                        .value("//urn1:ExtrinsicObject//descendant::urn1:Slot[@name='sourcePatientAge']/urn1:ValueList/urn1:Value/text()")
                        .build())
                .rightFactor(Factor.builder()
                        .type(Factor.Type.FIXEDTEXT)
                        .valueType(Factor.ValueType.NUMBER)
                        .value("50")
                        .build())
                .operator(FilterRule.Operator.GREATER)
                .build();
        filter1Rules.add(filter1Rule2);

        FilterRule filter1Rule3 = FilterRule.builder()
                .name("filter1Rule3")
                .leftFactor(Factor.builder()
                        .type(Factor.Type.FIXEDTEXT)
                        .valueType(Factor.ValueType.TEXT)
                        .value("Subscriber 3")
                        .build())
                .rightFactor(Factor.builder()
                        .type(Factor.Type.SUBSCRIBER)
                        .valueType(Factor.ValueType.TEXT)
                        .value("nombre")
                        .build())
                .operator(FilterRule.Operator.EQUAL)
                .build();
        filter1Rules.add(filter1Rule3);
        filterRules.add(filter1Rules);

        List<FilterRule> filter2Rules = new ArrayList<>();
        FilterRule filter2Rule1 = FilterRule.builder()
                .name("filter2Rule1")
                .leftFactor(Factor.builder()
                        .type(Factor.Type.XPATH)
                        .valueType(Factor.ValueType.NUMBER)
                        .value("//urn1:ExtrinsicObject//descendant::urn1:Slot[@name='sourcePatientAge']/urn1:ValueList/urn1:Value/text()")
                        .build())
                .rightFactor(Factor.builder()
                        .type(Factor.Type.FIXEDTEXT)
                        .valueType(Factor.ValueType.NUMBER)
                        .value("60")
                        .build())
                .operator(FilterRule.Operator.GREATEROREQUAL)
                .build();
        filter2Rules.add(filter2Rule1);
        filterRules.add(filter2Rules);

        List<FilterRule> filter3Rules = new ArrayList<>();
        FilterRule filter3Rule1 = FilterRule.builder()
                .name("filter3Rule1")
                .leftFactor(Factor.builder()
                        .type(Factor.Type.XPATH)
                        .valueType(Factor.ValueType.TEXT)
                        .value("//urn1:ExtrinsicObject//descendant::urn1:Slot[@name='sourcePatientAge']/urn1:ValueList/urn1:Value/text()")
                        .build())
                .rightFactor(Factor.builder()
                        .type(Factor.Type.FIXEDTEXT)
                        .valueType(Factor.ValueType.TEXT)
                        .value("6")
                        .build())
                .operator(FilterRule.Operator.CONTAINS)
                .build();
        filter3Rules.add(filter3Rule1);
        filterRules.add(filter3Rules);

        List<FilterRule> filter4Rules = new ArrayList<>();
        FilterRule filter4Rule1 = FilterRule.builder()
                .name("filter4Rule1")
                .leftFactor(Factor.builder()
                        .type(Factor.Type.FIXEDTEXT)
                        .valueType(Factor.ValueType.TEXT)
                        .value("Subscriber 2")
                        .build())
                .rightFactor(Factor.builder()
                        .type(Factor.Type.SUBSCRIBER)
                        .valueType(Factor.ValueType.TEXT)
                        .value("nombre")
                        .build())
                .operator(FilterRule.Operator.CONTAINS)
                .build();
        //filter4Rules.add(filter4Rule1);

        FilterRule filter4Rule2 = FilterRule.builder()
                .name("filter4Rule2")
                .leftFactor(Factor.builder()
                        .type(Factor.Type.XPATH)
                        .valueType(Factor.ValueType.TEXT)
                        .value("//urn1:ExtrinsicObject//descendant::urn1:Slot[@name='sourceSubscriberName2']/urn1:ValueList/urn1:Value/text()")
                        .build())
                .rightFactor(Factor.builder()
                        .type(Factor.Type.XPATH)
                        .valueType(Factor.ValueType.TEXT)
                        .value("//urn1:ExtrinsicObject//descendant::urn1:Slot[@name='sourceSubscriberName']/urn1:ValueList/urn1:Value/text()")
                        .build())
                .operator(FilterRule.Operator.CONTAINS)
                .build();
        filter4Rules.add(filter4Rule2);
        filterRules.add(filter4Rules);

        List<FilterRule> filter5Rules = new ArrayList<>();
        FilterRule filter5Rule1 = FilterRule.builder()
                .name("filter5Rule1")
                .leftFactor(Factor.builder()
                        .type(Factor.Type.FIXEDTEXT)
                        .valueType(Factor.ValueType.TEXT)
                        .value("Subscriber 2")
                        .build())
                .rightFactor(Factor.builder()
                        .type(Factor.Type.SUBSCRIBER)
                        .valueType(Factor.ValueType.TEXT)
                        .value("nombre")
                        .build())
                .operator(FilterRule.Operator.CONTAINS)
                .build();
        filter5Rules.add(filter5Rule1);

        FilterRule filter5Rule2 = FilterRule.builder()
                .name("filter5Rule2")
                .leftFactor(Factor.builder()
                        .type(Factor.Type.SUBSCRIBER)
                        .valueType(Factor.ValueType.TEXT)
                        .value("nombre")
                        .build())
                .rightFactor(Factor.builder()
                        .type(Factor.Type.XPATH)
                        .valueType(Factor.ValueType.TEXT)
                        .value("//urn1:ExtrinsicObject//descendant::urn1:Slot[@name='sourceSubscriberName2']/urn1:ValueList/urn1:Value/text()")
                        .build())
                .operator(FilterRule.Operator.CONTAINS)
                .build();
        filter5Rules.add(filter5Rule2);
        filterRules.add(filter5Rules);

        List<FilterRule> filter6Rules = new ArrayList<>();
        FilterRule filter6Rule1 = FilterRule.builder()
                .name("filter6Rule1")
                .leftFactor(Factor.builder()
                        .type(Factor.Type.XPATH)
                        .valueType(Factor.ValueType.NUMBER)
                        .valueFormat("Double")
                        .value("//urn1:ExtrinsicObject//descendant::urn1:Slot[@name='sourcePatientSalary']/urn1:ValueList/urn1:Value/text()")
                        .build())
                .rightFactor(Factor.builder()
                        .type(Factor.Type.FIXEDTEXT)
                        .valueType(Factor.ValueType.NUMBER)
                        .valueFormat("Double")
                        .value("100.57")
                        .build())
                .operator(FilterRule.Operator.EQUAL)
                .build();
        filter6Rules.add(filter6Rule1);

        FilterRule filter6Rule2 = FilterRule.builder()
                .name("filter6Rule2")
                .leftFactor(Factor.builder()
                        .type(Factor.Type.SUBSCRIBER)
                        .valueType(Factor.ValueType.TEXT)
                        .value("nombre")
                        .build())
                .rightFactor(Factor.builder()
                        .type(Factor.Type.FIXEDTEXT)
                        .valueType(Factor.ValueType.TEXT)
                        .value("Subscriber 1")
                        .build())
                .operator(FilterRule.Operator.EQUAL)
                .build();
        filter6Rules.add(filter6Rule2);
        filterRules.add(filter6Rules);

        List<FilterRule> filter7Rules = new ArrayList<>();
        FilterRule filter7Rule1 = FilterRule.builder()
                .name("filter7Rule1")
                .leftFactor(Factor.builder()
                        .type(Factor.Type.XPATH)
                        .valueType(Factor.ValueType.TEXT)
                        .value("//urn1:ExtrinsicObject//descendant::urn1:Slot[@name='sourcePatientId']/urn1:ValueList/urn1:Value/text()")
                        .build())
                .rightFactor(Factor.builder()
                        .type(Factor.Type.FIXEDTEXT)
                        .valueType(Factor.ValueType.TEXT)
                        .value("NOEXISTE")
                        .build())
                .operator(FilterRule.Operator.EQUAL)
                .build();
        filter7Rules.add(filter7Rule1);
        filterRules.add(filter7Rules);

        List<FilterRule> filter8Rules = new ArrayList<>();
        FilterRule filter8Rule1 = FilterRule.builder()
                .name("filter8Rule1")
                .leftFactor(Factor.builder()
                        .type(Factor.Type.LIST)
                        .valueType(Factor.ValueType.TEXT)
                        .value("hola;buen;dia")
                        .build())
                .rightFactor(Factor.builder()
                        .type(Factor.Type.FIXEDTEXT)
                        .valueType(Factor.ValueType.TEXT)
                        .value("hola")
                        .build())
                .operator(FilterRule.Operator.CONTAINS)
                .build();
        filter8Rules.add(filter8Rule1);
        filterRules.add(filter8Rules);

        List<FilterRule> filter9Rules = new ArrayList<>();
        FilterRule filter9Rule1 = FilterRule.builder()
                .name("filter9Rule1")
                .leftFactor(Factor.builder()
                        .type(Factor.Type.LIST)
                        .valueType(Factor.ValueType.TEXT)
                        .value("hola;buen;dia")
                        .build())
                .rightFactor(Factor.builder()
                        .type(Factor.Type.FIXEDTEXT)
                        .valueType(Factor.ValueType.TEXT)
                        .value("saludos")
                        .build())
                .operator(FilterRule.Operator.CONTAINS)
                .build();
        filter9Rules.add(filter9Rule1);
        filterRules.add(filter9Rules);

        List<FilterRule> filter10Rules = new ArrayList<>();
        FilterRule filter10Rule1 = FilterRule.builder()
                .name("filter10Rule1")
                .leftFactor(Factor.builder()
                        .type(Factor.Type.FIXEDTEXT)
                        .valueType(Factor.ValueType.TEXT)
                        .value("hola")
                        .build())
                .rightFactor(Factor.builder()
                        .type(Factor.Type.LIST)
                        .valueType(Factor.ValueType.TEXT)
                        .value("hola;buen;dia")
                        .build())
                .operator(FilterRule.Operator.CONTAINS)
                .build();
        filter10Rules.add(filter10Rule1);
        filterRules.add(filter10Rules);

        List<FilterRule> filter11Rules = new ArrayList<>();
        FilterRule filter11Rule1 = FilterRule.builder()
                .name("filter11Rule1")
                .leftFactor(Factor.builder()
                        .type(Factor.Type.LIST)
                        .valueType(Factor.ValueType.TEXT)
                        .value("hola;dia;buen")
                        .build())
                .rightFactor(Factor.builder()
                        .type(Factor.Type.LIST)
                        .valueType(Factor.ValueType.TEXT)
                        .value("hola;buen;dia")
                        .build())
                .operator(FilterRule.Operator.CONTAINS)
                .build();
        filter11Rules.add(filter11Rule1);
        filterRules.add(filter11Rules);

        return filterRules;
    }

    private void createTopicsFilters() {
        this.filtersTopics = new ArrayList<>();

        for (Topico topic: this.topics) {
            switch (topic.getNombre()) {
                case "Topic 1": {
                    FilterTopic filterTopic11 = FilterTopic.builder()
                            .filter(this.filters.get(0))
                            .topic(topic)
                            .maximumOccurrences(0)
                            .type(FilterTopic.Type.SUBSCRIBER)
                            .build();
                    this.filterTopicRepository.save(filterTopic11);
                    this.filtersTopics.add(filterTopic11);

                    FilterTopic filterTopic21 = FilterTopic.builder()
                            .filter(this.filters.get(1))
                            .topic(topic)
                            .maximumOccurrences(2)
                            .type(FilterTopic.Type.SUBSCRIBER)
                            .build();
                    this.filterTopicRepository.save(filterTopic21);
                    this.filtersTopics.add(filterTopic21);
                }break;
                case "Topic 2": {
                    FilterTopic filterTopic32 = FilterTopic.builder()
                            .filter(this.filters.get(2))
                            .topic(topic)
                            .maximumOccurrences(1)
                            .type(FilterTopic.Type.MESSAGE)
                            .build();
                    this.filterTopicRepository.save(filterTopic32);
                    this.filtersTopics.add(filterTopic32);

                    FilterTopic filterTopic42 = FilterTopic.builder()
                            .filter(this.filters.get(3))
                            .topic(topic)
                            .maximumOccurrences(0)
                            .type(FilterTopic.Type.MESSAGE)
                            .build();
                    this.filterTopicRepository.save(filterTopic42);
                    this.filtersTopics.add(filterTopic42);

                    FilterTopic filterTopic52 = FilterTopic.builder()
                            .filter(this.filters.get(4))
                            .topic(topic)
                            .maximumOccurrences(0)
                            .type(FilterTopic.Type.SUBSCRIBER)
                            .build();
                    this.filterTopicRepository.save(filterTopic52);
                    this.filtersTopics.add(filterTopic52);
                }break;
                case "Topic 3": {
                    FilterTopic filterTopic63 = FilterTopic.builder()
                            .filter(this.filters.get(5))
                            .topic(topic)
                            .maximumOccurrences(2)
                            .type(FilterTopic.Type.SUBSCRIBER)
                            .build();
                    this.filterTopicRepository.save(filterTopic63);
                    this.filtersTopics.add(filterTopic63);
                }break;
                case "Topic 4": {
                    FilterTopic filterTopic74 = FilterTopic.builder()
                            .filter(this.filters.get(6))
                            .topic(topic)
                            .maximumOccurrences(10)
                            .type(FilterTopic.Type.SUBSCRIBER)
                            .build();
                    this.filterTopicRepository.save(filterTopic74);
                    this.filtersTopics.add(filterTopic74);
                }break;
                case "Topic 5": {
                    FilterTopic filterTopic85 = FilterTopic.builder()
                            .filter(this.filters.get(7))
                            .topic(topic)
                            .type(FilterTopic.Type.MESSAGE)
                            .build();
                    this.filterTopicRepository.save(filterTopic85);
                    this.filtersTopics.add(filterTopic85);

                    FilterTopic filterTopic95 = FilterTopic.builder()
                            .filter(this.filters.get(8))
                            .topic(topic)
                            .type(FilterTopic.Type.MESSAGE)
                            .build();
                    this.filterTopicRepository.save(filterTopic95);
                    this.filtersTopics.add(filterTopic95);

                    FilterTopic filterTopic105 = FilterTopic.builder()
                            .filter(this.filters.get(9))
                            .topic(topic)
                            .type(FilterTopic.Type.MESSAGE)
                            .build();
                    this.filterTopicRepository.save(filterTopic105);
                    this.filtersTopics.add(filterTopic105);

                    FilterTopic filterTopic115 = FilterTopic.builder()
                            .filter(this.filters.get(10))
                            .topic(topic)
                            .type(FilterTopic.Type.MESSAGE)
                            .build();
                    this.filterTopicRepository.save(filterTopic115);
                    this.filtersTopics.add(filterTopic115);
                }break;
            }
        }
    }

    void createNovelties() {
        this.novelties = new ArrayList<>();

        try {
            for (Topico topic: this.topics) {
                Novedad novelty = new Novedad();
                novelty.setFecha(new Date());
                novelty.setUuid(UUID.randomUUID().toString());
                novelty.setProductor(this.publishers.get(0));
                novelty.setTopico(topic);
                novelty.setWsaMessageId(UUID.randomUUID().toString());
                novelty.setContenido(getFileContent(request1.getInputStream()));
                this.novedadRepository.save(novelty);
                this.novelties.add(novelty);
            }
        } catch (IOException e) { }
    }
}
