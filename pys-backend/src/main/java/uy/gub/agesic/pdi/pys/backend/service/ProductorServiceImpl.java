package uy.gub.agesic.pdi.pys.backend.service;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroProductorDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.domain.Productor;
import uy.gub.agesic.pdi.pys.domain.Topico;
import uy.gub.agesic.pdi.pys.domain.TopicoProductor;

import java.util.List;

@Service
public class ProductorServiceImpl implements ProductorService {

    private MongoTemplate mongoTemplate;

    @Autowired
    public ProductorServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void crear(Productor item) throws PSException {
        mongoTemplate.save(item);
    }

    @Override
    public void modificar(Productor item) throws PSException {
        mongoTemplate.save(item);
    }

    @Override
    public void eliminar(String nombre) throws PSException {
        Productor productor = buscar(nombre);

        if (productor != null) {
            mongoTemplate.remove(productor);
        }
    }

    @Override
    public void habilitarProductor(String nombre) throws PSException {
        Productor productor = buscar(nombre);
        if (productor != null) {
            productor.setHabilitado(true);
            mongoTemplate.save(productor);
        }

    }

    @Override
    public void deshabilitarProductor(String nombre) throws PSException {
        Productor productor = buscar(nombre);
        if (productor != null) {
            productor.setHabilitado(false);
            mongoTemplate.save(productor);
        }
    }

    @Override
    public Productor buscar(String nombre) throws PSException {
        Query query = new Query();
        query.addCriteria(Criteria.where(BackendServiceUtil.NOMBRE_PARAM).is(nombre));
        List<Productor> productorList = mongoTemplate.find(query,Productor.class);
        return (productorList != null && productorList.size() == 1) ? productorList.get(0) : null;

    }

    @Override
    public Boolean existeTopicoProductor(Topico topico, Productor productor) throws PSException {
        Query query = new Query();
        query.addCriteria(Criteria.where(BackendServiceUtil.TOPICOID_PARAM).is(new ObjectId(topico.getId())).and(BackendServiceUtil.PRODUCTORID_PARAM).is(new ObjectId(productor.getId())));
        List<TopicoProductor> topicoProductorList = mongoTemplate.find(query,TopicoProductor.class);
        return topicoProductorList != null && topicoProductorList.size() == 1;
    }

    @Override
    public ResultadoPaginadoDTO<Productor> buscarProductoresFiltro(FiltroProductorDTO filtro) throws PSException {
        Query query = QueryUtil.applyFilterQuery(filtro);

        ResultadoPaginadoDTO<Productor> resultado = new ResultadoPaginadoDTO<>();
        resultado.setTotalTuplas(mongoTemplate.count(query, Productor.class));

        QueryUtil.applySizeLimit(query, filtro);

        List<Productor> listProductores = mongoTemplate.find(query, Productor.class);
        resultado.setResultado(listProductores);

        return resultado;
    }

    @Override
    public List<Productor> getAll() throws PSException {
        return mongoTemplate.findAll(Productor.class);
    }

    @Override
    public List<Productor> obtenerHabilitados() throws PSException {
        Query query = new Query();
        query.addCriteria(Criteria.where(BackendServiceUtil.HABILITADO_PARAM).is(true));
        return  mongoTemplate.find(query, Productor.class);

    }
}
