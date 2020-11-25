package uy.gub.agesic.pdi.pys.backend.service;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import uy.gub.agesic.pdi.common.exceptions.PDIException;
import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroSuscriptorDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.domain.DeliveryMode;
import uy.gub.agesic.pdi.pys.domain.Suscriptor;
import uy.gub.agesic.pdi.pys.domain.Topico;
import uy.gub.agesic.pdi.pys.domain.TopicoSuscriptor;

import java.util.List;

@Service
public class SuscriptorServiceImpl implements SuscriptorService {

    private MongoTemplate mongoTemplate;

    private EntregaService entregaService;

    @Autowired
    public SuscriptorServiceImpl(MongoTemplate mongoTemplate, EntregaService entregaService) {
        this.mongoTemplate = mongoTemplate;
        this.entregaService = entregaService;
    }

    @Override
    public void crear(Suscriptor item) throws PSException {
        mongoTemplate.save(item);
    }

    @Override
    public void modificar(Suscriptor item) throws PSException {
        mongoTemplate.save(item);
    }

    @Override
    public void eliminar(String nombre) throws PSException {
        Suscriptor suscriptor = buscar(nombre);

        if (suscriptor != null) {
            entregaService.eliminarEntregasSuscriptor(suscriptor);
            mongoTemplate.remove(suscriptor);
        }
    }

    @Override
    public void habilitarSuscriptor(String nombre) throws PSException {
        Suscriptor suscriptor = buscar(nombre);

        if (suscriptor != null) {
            suscriptor.setHabilitado(true);
            mongoTemplate.save(suscriptor);
        }

    }

    @Override
    public void deshabilitarSuscriptor(String nombre) throws PSException {
        Suscriptor suscriptor = buscar(nombre);

        if (suscriptor != null) {
            suscriptor.setHabilitado(false);
            mongoTemplate.save(suscriptor);
            entregaService.cancelarEntregasSuscriptor(suscriptor);
        }

    }

    @Override
    public Suscriptor buscar(String nombre) throws PSException {
        Query query = new Query();
        query.addCriteria(Criteria.where(BackendServiceUtil.NOMBRE_PARAM).is(nombre));
        List<Suscriptor> suscriptorList = mongoTemplate.find(query, Suscriptor.class);
        return (suscriptorList != null && suscriptorList.size() == 1) ? suscriptorList.get(0) : null;
    }

    @Override
    public Suscriptor searchById(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where(BackendServiceUtil.ID_PARAM).is(id));
        return mongoTemplate.findOne(query, Suscriptor.class);
    }

    @Override
    public List<Suscriptor> getAll() throws PSException {
        return mongoTemplate.findAll(Suscriptor.class);
    }

    @Override
    public List<Suscriptor> obtenerHabilitados() throws PSException {
        Query query = new Query();
        query.addCriteria(Criteria.where(BackendServiceUtil.HABILITADO_PARAM).is(true));
        return  mongoTemplate.find(query, Suscriptor.class);
    }

    @Override
    public Boolean existeTopicoSuscriptorPull(Topico topico, Suscriptor suscriptor) throws PSException {
        Query query = new Query();

        query.addCriteria(Criteria.where(BackendServiceUtil.TOPICOID_PARAM).is(new ObjectId(topico.getId())).
                and(BackendServiceUtil.SUSCRIPTORID_PARAM).is(new ObjectId(suscriptor.getId())).
                and(BackendServiceUtil.DELIVERY_MODE_PARAM).in(DeliveryMode.PULL));

        List<TopicoSuscriptor> topicoSuscriptorList = mongoTemplate.find(query, TopicoSuscriptor.class);

        return (topicoSuscriptorList != null && topicoSuscriptorList.size() == 1);
    }

    @Override
    public ResultadoPaginadoDTO<Suscriptor> buscarSuscriptoresFiltro(FiltroSuscriptorDTO filtro) throws PSException, PDIException {
        Query query = QueryUtil.applyFilterQuery(filtro);

        ResultadoPaginadoDTO<Suscriptor> resultado = new ResultadoPaginadoDTO<>();
        resultado.setTotalTuplas(mongoTemplate.count(query, Suscriptor.class));

        QueryUtil.applySizeLimit(query, filtro);

        List<Suscriptor> listSuscriptores = mongoTemplate.find(query, Suscriptor.class);
        resultado.setResultado(listSuscriptores);

        return resultado;
    }

}
