package uy.gub.agesic.pdi.pys.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uy.gub.agesic.pdi.pys.domain.Filter;
import uy.gub.agesic.pdi.pys.domain.FilterTopic;
import uy.gub.agesic.pdi.pys.domain.Topico;

import java.util.List;

public interface FilterTopicRepository extends MongoRepository<FilterTopic, String> {
    List<FilterTopic> findAllByTopic(Topico topico);
    List<FilterTopic> findAllByFilter(Filter filter);
    FilterTopic findFirstByFilterAndTopic(Filter filter, Topico topico);
    void deleteAllByFilter(Filter filter);
    List<FilterTopic> findAllByTopicId(String id);
}
