package uy.gub.agesic.pdi.pys.backend.service;

import org.springframework.stereotype.Service;
import uy.gub.agesic.pdi.pys.backend.repository.FilterTopicRepository;
import uy.gub.agesic.pdi.pys.domain.Filter;
import uy.gub.agesic.pdi.pys.domain.FilterTopic;
import uy.gub.agesic.pdi.pys.domain.Topico;

import java.util.List;

@Service
public class FilterTopicServiceImpl implements FilterTopicService {
    private final FilterTopicRepository filterTopicRepository;

    public FilterTopicServiceImpl(FilterTopicRepository filterTopicRepository) {
        this.filterTopicRepository = filterTopicRepository;
    }

    public void deleteFilterTopic(String id) {
        this.filterTopicRepository.delete(id);
    }

    @Override
    public List<FilterTopic> searchFilterTopicsByFilter(Filter filter) {
        return this.filterTopicRepository.findAllByFilter(filter);
    }

    @Override
    public List<FilterTopic> searchFilterTopicsByTopic(Topico topic) {
        return this.filterTopicRepository.findAllByTopic(topic);
    }

    @Override
    public FilterTopic findFirstByFilterAndTopic(Filter filter, Topico topic) {
        return this.filterTopicRepository.findFirstByFilterAndTopic(filter, topic);
    }

    @Override
    public FilterTopic getFilterTopic(String id) {
        return this.filterTopicRepository.findOne(id);
    }

    @Override
    public FilterTopic saveFilterTopic(FilterTopic filterTopic) {
        return this.filterTopicRepository.save(filterTopic);
    }

    @Override
    public void deleteByFilter(Filter filter) {
        this.filterTopicRepository.deleteAllByFilter(filter);
    }
}
