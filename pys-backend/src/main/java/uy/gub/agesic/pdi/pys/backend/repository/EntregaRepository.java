package uy.gub.agesic.pdi.pys.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uy.gub.agesic.pdi.pys.domain.Entrega;

public interface EntregaRepository extends MongoRepository<Entrega, String> {

}
