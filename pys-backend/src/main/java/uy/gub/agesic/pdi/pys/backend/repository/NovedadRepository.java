package uy.gub.agesic.pdi.pys.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uy.gub.agesic.pdi.pys.domain.Novedad;

public interface NovedadRepository extends MongoRepository<Novedad, String> {

}
