package uy.gub.agesic.pdi.pys.backend.service;

import uy.gub.agesic.pdi.pys.domain.Filter;
import uy.gub.agesic.pdi.pys.domain.FilterTopic;
import uy.gub.agesic.pdi.pys.domain.Topico;

import java.util.List;

public interface FilterTopicService {
    void deleteFilterTopic(String id);
    List<FilterTopic> searchFilterTopicsByTopic(Topico topic);
    List<FilterTopic> searchFilterTopicsByFilter(Filter filter);
    FilterTopic findFirstByFilterAndTopic(Filter filter, Topico topic);
    FilterTopic getFilterTopic(String id);
    FilterTopic saveFilterTopic(FilterTopic filterTopic);
    void deleteByFilter(Filter filter);
}
