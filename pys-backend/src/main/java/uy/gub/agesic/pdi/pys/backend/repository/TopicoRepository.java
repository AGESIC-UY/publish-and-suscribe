package uy.gub.agesic.pdi.pys.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uy.gub.agesic.pdi.pys.domain.Topico;

public interface TopicoRepository extends MongoRepository<Topico, String> {

}
