package uy.gub.agesic.pdi.pys.backend.service;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.domain.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class TopicoProductorServiceImp implements TopicoProductorService {

    private MongoTemplate mongoTemplate;

    @Autowired
    public TopicoProductorServiceImp(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void crear(TopicoProductor item) throws PSException {
        this.mongoTemplate.save(item);
    }

    @Override
    public void modificar(TopicoProductor item) throws PSException {
        this.mongoTemplate.save(item);
    }

    @Override
    public void eliminar(String topico, String productor) throws PSException {
        TopicoProductor topicoProductor = buscarTopicoProductor(topico, productor);

        if (topicoProductor != null) {
            this.mongoTemplate.remove(topicoProductor);
        }
    }

    @Override
    public List<TopicoProductor> buscarTopicoProductor(String topico) throws PSException {
        List<TopicoProductor> topicoProductor = null;

        Query queryT = new Query();
        queryT.addCriteria(Criteria.where(BackendServiceUtil.NOMBRE_PARAM).is(topico));
        Topico topic = this.mongoTemplate.findOne(queryT, Topico.class);

        if (topic != null) {
            Query query = new Query();
            query.addCriteria(Criteria.where(BackendServiceUtil.TOPICOID_PARAM).is(new ObjectId(topic.getId())));
            topicoProductor = this.mongoTemplate.find(query, TopicoProductor.class);
        }

        return topicoProductor;
    }

    @Override
    public List<Productor> buscarProductoresPorTopico(String topico) throws PSException {
        List<TopicoProductor> topicoProductor = buscarTopicoProductor(topico);
        List<Productor> productores = new ArrayList<>();

        if(topicoProductor != null){
            for(TopicoProductor tp : topicoProductor){
                productores.add(tp.getProductor());
            }
        }

        return productores;
    }

    @Override
    public TopicoProductor buscarTopicoProductor(String topico, String productor) throws PSException {
        TopicoProductor topicoProductor = null;

        Query queryT = new Query();
        queryT.addCriteria(Criteria.where(BackendServiceUtil.NOMBRE_PARAM).is(topico));
        Topico topic = this.mongoTemplate.findOne(queryT, Topico.class);

        Query queryS = new Query();
        queryS.addCriteria(Criteria.where(BackendServiceUtil.NOMBRE_PARAM).is(productor));
        Productor producer = this.mongoTemplate.findOne(queryS, Productor.class);

        if (topic != null && producer != null) {
            Query query = new Query();
            query.addCriteria(Criteria.where(BackendServiceUtil.PRODUCTORID_PARAM).is(new ObjectId(producer.getId())).and(BackendServiceUtil.TOPICOID_PARAM).is(new ObjectId(topic.getId())));
            topicoProductor = this.mongoTemplate.findOne(query, TopicoProductor.class);
        }

        return topicoProductor;

    }


}
