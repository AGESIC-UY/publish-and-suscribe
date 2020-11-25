package uy.gub.agesic.pdi.pys.backend.service;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroAlertaConsultaDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.domain.Alerta;
import uy.gub.agesic.pdi.pys.domain.Productor;
import uy.gub.agesic.pdi.pys.domain.Suscriptor;
import uy.gub.agesic.pdi.pys.domain.Topico;

import java.util.ArrayList;
import java.util.List;

@Service
public class AlertaServiceImpl implements AlertaService {

    private MongoTemplate mongoTemplate;

    @Autowired
    public AlertaServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void crear(Alerta item) throws PSException {
        mongoTemplate.save(item);
    }

    @Override
    public void modificar(Alerta item) throws PSException {
        mongoTemplate.save(item);
    }

    @Override
    public ResultadoPaginadoDTO<Alerta> buscarAlertaFiltro(FiltroAlertaConsultaDTO filtro) throws PSException {

        Productor producer = filtro.getProductor();
        Topico topic = filtro.getTopico();
        Suscriptor suscriptor = filtro.getSuscriptor();

        Query query = QueryUtil.applyConsultaFilterQuery(filtro);

        if (topic != null) {
            query.addCriteria(Criteria.where(BackendServiceUtil.TOPICOID_PARAM).is(new ObjectId(topic.getId())));
        }

        if (producer != null) {
            query.addCriteria(Criteria.where(BackendServiceUtil.PRODUCTORID_PARAM).is(new ObjectId(producer.getId())));
        }

        if (suscriptor != null) {
            query.addCriteria(Criteria.where(BackendServiceUtil.SUSCRIPTORID_PARAM).is(new ObjectId(suscriptor.getId())));
        }

        if (filtro.getNovedadId() != null) {
            query.addCriteria(Criteria.where(BackendServiceUtil.UUID_PARAM).is(filtro.getNovedadId()));
        }

        List<String> props = new ArrayList<>();
        props.add("fecha");
        Sort sort = new Sort(Sort.Direction.DESC, props);
        query.with(sort);

        ResultadoPaginadoDTO<Alerta> resultado = new ResultadoPaginadoDTO<>();
        resultado.setTotalTuplas(mongoTemplate.count(query, Alerta.class));

        QueryUtil.applySizeLimit(query, filtro);

        List<Alerta> listAlertas = mongoTemplate.find(query, Alerta.class);
        resultado.setResultado(listAlertas);

        return resultado;

    }

    @Override
    public Alerta buscarAlerta(String id) throws PSException {
        return mongoTemplate.findById(id, Alerta.class);
    }

}
