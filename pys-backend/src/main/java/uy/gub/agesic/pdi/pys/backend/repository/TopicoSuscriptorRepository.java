package uy.gub.agesic.pdi.pys.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uy.gub.agesic.pdi.pys.domain.TopicoSuscriptor;

import java.util.List;

public interface TopicoSuscriptorRepository extends MongoRepository<TopicoSuscriptor, String> {

    List<TopicoSuscriptor> findAllByTopicoId(String topicId);

}
