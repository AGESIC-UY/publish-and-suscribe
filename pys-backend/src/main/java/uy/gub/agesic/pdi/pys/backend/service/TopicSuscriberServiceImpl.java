package uy.gub.agesic.pdi.pys.backend.service;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.domain.Suscriptor;
import uy.gub.agesic.pdi.pys.domain.Topico;
import uy.gub.agesic.pdi.pys.domain.TopicoSuscriptor;

import java.util.ArrayList;
import java.util.List;

@Service
public class TopicSuscriberServiceImpl implements TopicoSuscriptorService {

    private MongoTemplate mongoTemplate;

    @Autowired
    public TopicSuscriberServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void crear(TopicoSuscriptor item) throws PSException {
        this.mongoTemplate.save(item);
    }

    @Override
    public void modificar(TopicoSuscriptor item) throws PSException {
        this.mongoTemplate.save(item);
    }

    @Override
    public void eliminar(String topico, String suscriptor) throws PSException {
        TopicoSuscriptor topicoSuscriptor = buscarTopicoSuscriptor(suscriptor, topico);

        if(topicoSuscriptor != null){
            this.mongoTemplate.remove(topicoSuscriptor);
        }

    }

    @Override
    public List<TopicoSuscriptor> buscar(Topico topico) throws PSException {
        Query query = new Query();
        query.addCriteria(Criteria.where(BackendServiceUtil.TOPICOID_PARAM).is(new ObjectId(topico.getId())));

        return this.mongoTemplate.find(query, TopicoSuscriptor.class);
    }

    @Override
    public List<TopicoSuscriptor> buscarTodos() throws PSException {
        return this.mongoTemplate.findAll(TopicoSuscriptor.class);
    }

    @Override
    public List<TopicoSuscriptor> buscarTopicosSuscriptor(Suscriptor suscriptor) {
        Query query = new Query();
        query.addCriteria(Criteria.where(BackendServiceUtil.SUSCRIPTORID_PARAM).is(new ObjectId(suscriptor.getId())));
        return this.mongoTemplate.find(query, TopicoSuscriptor.class);
    }

    @Override
    public List<TopicoSuscriptor> buscarTopicosSuscriptor(String topico) throws PSException {
        List<TopicoSuscriptor> topicoSuscriptor = null;

        Query queryT = new Query();
        queryT.addCriteria(Criteria.where(BackendServiceUtil.NOMBRE_PARAM).is(topico));
        Topico topic = this.mongoTemplate.findOne(queryT, Topico.class);

        if (topic != null) {
            Query query = new Query();
            query.addCriteria(Criteria.where(BackendServiceUtil.TOPICOID_PARAM).is(new ObjectId(topic.getId())));
            topicoSuscriptor = this.mongoTemplate.find(query, TopicoSuscriptor.class);
        }
        return topicoSuscriptor;
    }

    @Override
    public List<Suscriptor> buscarSuscriptoresPorTopico(String topico) throws PSException {
        List<TopicoSuscriptor> topicoSuscriptor = buscarTopicosSuscriptor(topico);
        List<Suscriptor> suscriptores = new ArrayList<>();

        if(topicoSuscriptor != null){
            for(TopicoSuscriptor ts : topicoSuscriptor){
                suscriptores.add(ts.getSuscriptor());
            }
        }

        return suscriptores;
    }

    @Override
    public TopicoSuscriptor buscarTopicoSuscriptor(String suscriptor, String topico) throws PSException {
        TopicoSuscriptor topicoSuscriptor = null;

        Query queryT = new Query();
        queryT.addCriteria(Criteria.where(BackendServiceUtil.NOMBRE_PARAM).is(topico));
        Topico topic = this.mongoTemplate.findOne(queryT, Topico.class);

        Query queryS = new Query();
        queryS.addCriteria(Criteria.where(BackendServiceUtil.NOMBRE_PARAM).is(suscriptor));
        Suscriptor suscriber = this.mongoTemplate.findOne(queryS, Suscriptor.class);

        if(topic != null && suscriber != null){
            Query query = new Query();
            query.addCriteria(Criteria.where(BackendServiceUtil.SUSCRIPTORID_PARAM).is(new ObjectId(suscriber.getId())).and(BackendServiceUtil.TOPICOID_PARAM).is(new ObjectId(topic.getId())));
            topicoSuscriptor = this.mongoTemplate.findOne(query, TopicoSuscriptor.class);
        }

        return topicoSuscriptor;
    }

}
