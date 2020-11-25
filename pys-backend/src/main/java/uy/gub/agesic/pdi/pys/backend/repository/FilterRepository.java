package uy.gub.agesic.pdi.pys.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uy.gub.agesic.pdi.pys.domain.Filter;

import java.util.Optional;

@Repository
public interface FilterRepository extends MongoRepository<Filter, String>, FilterRepositoryCustom {
    Optional<Filter> findOneByName(String name);
}
