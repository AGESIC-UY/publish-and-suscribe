package uy.gub.agesic.pdi.pys.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uy.gub.agesic.pdi.pys.domain.Productor;

public interface ProductorRepository extends MongoRepository<Productor, String> {

}
