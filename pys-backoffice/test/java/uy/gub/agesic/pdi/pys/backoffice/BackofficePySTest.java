package uy.gub.agesic.pdi.pys.backoffice;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import uy.gub.agesic.pdi.pys.backend.dtos.FilterFilterDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.backend.repository.FilterRepository;
import uy.gub.agesic.pdi.pys.backend.repository.TopicoRepository;
import uy.gub.agesic.pdi.pys.backend.service.FilterService;
import uy.gub.agesic.pdi.pys.backend.service.FilterTopicService;
import uy.gub.agesic.pdi.pys.domain.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BackofficePySTest {
    @Autowired
    private FilterService filterService;
    @Autowired
    private FilterTopicService filterTopicService;
    @Autowired
    private TopicoRepository topicRepository;
    @Autowired
    private FilterRepository filterRepository;

    private List<Topico> topics;

    private List<Filter> filters;

    @Test
    public void workingWithFilters() throws PSException {
        Filter filter1 = Filter.builder()
                .name("Filter 1")
                .operator(Filter.Operator.AND)
                .documentType(Filter.DocumentType.XML)
                .build();

        Filter filter2 = Filter.builder()
                .name("Filter 2")
                .operator(Filter.Operator.AND)
                .documentType(Filter.DocumentType.XML)
                .build();

        Filter filter3 = Filter.builder()
                .name("Filter 3")
                .operator(Filter.Operator.OR)
                .documentType(Filter.DocumentType.JSON)
                .build();

        Filter filter4 = Filter.builder()
                .name("Filter 4")
                .operator(Filter.Operator.OR)
                .documentType(Filter.DocumentType.XML)
                .build();

        Filter filter5 = Filter.builder()
                .name("Filter 5")
                .operator(Filter.Operator.OR)
                .documentType(Filter.DocumentType.XML)
                .build();

        Filter filter6 = Filter.builder()
                .name("Filter 6")
                .operator(Filter.Operator.OR)
                .documentType(Filter.DocumentType.XML)
                .build();

        Assert.assertNull(filter1.getId());
        this.filterService.saveFilter(filter1);
        Assert.assertNotNull(filter1.getId());

        Assert.assertNull(filter2.getId());
        this.filterService.saveFilter(filter2);
        Assert.assertNotNull(filter2.getId());

        Assert.assertNull(filter3.getId());
        this.filterService.saveFilter(filter3);
        Assert.assertNotNull(filter3.getId());

        Assert.assertNull(filter4.getId());
        this.filterService.saveFilter(filter4);
        Assert.assertNotNull(filter4.getId());

        Assert.assertNull(filter5.getId());
        this.filterService.saveFilter(filter5);
        Assert.assertNotNull(filter5.getId());

        Assert.assertNull(filter6.getId());
        this.filterService.saveFilter(filter6);
        Assert.assertNotNull(filter6.getId());

        Assert.assertEquals(6, this.filterService.getAllFilters().size());

        Assert.assertTrue(this.filterService.getFilter("Filter 1").isPresent());
        Assert.assertFalse(this.filterService.getFilter("Filter n").isPresent());

        this.filterService.deleteFilter(this.filterService.getFilter("Filter 1").get());
        Assert.assertEquals(5, this.filterService.getAllFilters().size());

        FilterFilterDTO filterFilterDTO1 = new FilterFilterDTO(0, 3);
        filterFilterDTO1.setName("Filter");
        Assert.assertEquals(3, this.filterService.searchFilters(filterFilterDTO1).getResultado().size());

        FilterFilterDTO filterFilterDTO2 = new FilterFilterDTO(1, 3);
        filterFilterDTO2.setName("Filter");
        Assert.assertEquals(2, this.filterService.searchFilters(filterFilterDTO2).getResultado().size());
    }

    @Test
    public void workingWithFiltersTopics() {
        this.createTopics();
        this.createFilters();

        FilterTopic filterTopic11 = FilterTopic.builder()
                .filter(this.filters.get(0))
                .topic(this.topics.get(0))
                .maximumOccurrences(0)
                .type(FilterTopic.Type.SUBSCRIBER)
                .build();

        FilterTopic filterTopic12 = FilterTopic.builder()
                .filter(this.filters.get(1))
                .topic(this.topics.get(0))
                .maximumOccurrences(0)
                .type(FilterTopic.Type.SUBSCRIBER)
                .build();

        FilterTopic filterTopic23 = FilterTopic.builder()
                .filter(this.filters.get(2))
                .topic(this.topics.get(1))
                .maximumOccurrences(0)
                .type(FilterTopic.Type.MESSAGE)
                .build();

        Assert.assertNull(filterTopic11.getId());
        this.filterTopicService.saveFilterTopic(filterTopic11);
        Assert.assertNotNull(filterTopic11.getId());

        Assert.assertNull(filterTopic12.getId());
        this.filterTopicService.saveFilterTopic(filterTopic12);
        Assert.assertNotNull(filterTopic12.getId());

        Assert.assertNull(filterTopic23.getId());
        this.filterTopicService.saveFilterTopic(filterTopic23);
        Assert.assertNotNull(filterTopic23.getId());

        Assert.assertEquals(2, this.filterTopicService.searchFilterTopicsByTopic(this.topics.get(0)).size());
        Assert.assertEquals(1, this.filterTopicService.searchFilterTopicsByTopic(this.topics.get(1)).size());

        Assert.assertNotNull(this.filterTopicService.getFilterTopic(filterTopic23.getId()));

        Assert.assertNotNull(this.filterTopicService.findFirstByFilterAndTopic(this.filters.get(0), this.topics.get(0)));
        Assert.assertNull(this.filterTopicService.findFirstByFilterAndTopic(this.filters.get(2), this.topics.get(0)));

        this.filterTopicService.deleteByFilter(this.filters.get(0));
        Assert.assertEquals(1, this.filterTopicService.searchFilterTopicsByTopic(this.topics.get(0)).size());
        this.filterTopicService.deleteFilterTopic(filterTopic12.getId());
        Assert.assertEquals(0, this.filterTopicService.searchFilterTopicsByTopic(this.topics.get(0)).size());
    }

    private void createTopics() {
        this.topics = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            Topico topic = new Topico();
            topic.setNombre("Topic " + (i + 1));
            topic.setFechaCreacion(new Date());
            topic.setHabilitado(true);
            topic.setElementoRaiz("ProvideAndRegisterDocumentSetRequest");
            topic.setNamespace("urn:ihe:iti:xds-b:2007");
            topic.setSoapAction("http://novedades.agesic.gub.uy/soap-server#nuevaNovedad");

            this.topicRepository.save(topic);
            topics.add(topic);
        }
    }

    private void createFilters() {
        this.filters = new ArrayList<>();
        List<List<FilterRule>> filterRules = this.createFilterRules();

        for (int i = 0; i < 3; i++) {
            Filter filter = new Filter();
            filter.setName("Filter " + (i + 1));
            filter.setDocumentType(Filter.DocumentType.XML);
            filter.setOperator(Filter.Operator.AND);
            filter.setRules(filterRules.get(i));
            this.filterRepository.save(filter);
            this.filters.add(filter);
        }
    }

    private List<List<FilterRule>> createFilterRules() {
        List<List<FilterRule>> filterRules = new ArrayList<>();

        List<FilterRule> filter1Rules = new ArrayList<>();
        FilterRule filter1Rule1 = FilterRule.builder()
                .name("sourcePatientId")
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
        filterRules.add(filter1Rules);

        List<FilterRule> filter2Rules = new ArrayList<>();
        FilterRule filter2Rule1 = FilterRule.builder()
                .name("sourcePatientAge")
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
                .name("sourcePatientAge")
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
                .operator(FilterRule.Operator.EQUAL)
                .build();
        filter3Rules.add(filter3Rule1);
        filterRules.add(filter3Rules);

        return filterRules;
    }
}
