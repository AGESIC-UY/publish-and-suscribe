package uy.gub.agesic.pdi.pys.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroTopicoDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.domain.Topico;

import java.util.List;

@Service
public class TopicoServiceImpl implements TopicoService {

    private MongoTemplate mongoTemplate;

    @Autowired
    public TopicoServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void crear(Topico item) throws PSException {
        mongoTemplate.save(item);
    }

    @Override
    public void modificar(Topico item) throws PSException {
        mongoTemplate.save(item);
    }

    @Override
    public void eliminar(String nombre) throws PSException {
        Topico topico = buscar(nombre);

        if (topico != null) {
            mongoTemplate.remove(topico);
        }
    }

    @Override
    public void habilitar(String nombre) throws PSException {
        Topico topico = buscar(nombre);

        if(topico != null){
            topico.setHabilitado(true);
            mongoTemplate.save(topico);
        }
    }

    @Override
    public void deshabilitar(String nombre) throws PSException {
        Topico topico = buscar(nombre);

        if (topico != null) {
            topico.setHabilitado(false);
            mongoTemplate.save(topico);
        }

    }

    @Override
    public Topico buscar(String nombre) throws PSException {
        Query query = new Query();
        query.addCriteria(Criteria.where(BackendServiceUtil.NOMBRE_PARAM).is(nombre));

        List<Topico> topicoList = mongoTemplate.find(query,Topico.class);

        return (topicoList != null && topicoList.size() == 1) ? topicoList.get(0) : null;
    }

    @Override
    public ResultadoPaginadoDTO<Topico> buscarTopicosFiltro(FiltroTopicoDTO filtro) throws PSException {
        Query query = QueryUtil.applyFilterQuery(filtro);

        ResultadoPaginadoDTO<Topico> resultado = new ResultadoPaginadoDTO<>();
        resultado.setTotalTuplas(mongoTemplate.count(query, Topico.class));

        QueryUtil.applySizeLimit(query, filtro);

        List<Topico> listTopicos = mongoTemplate.find(query, Topico.class);
        resultado.setResultado(listTopicos);

        return resultado;
    }

    @Override
    public List<Topico> getAll() throws PSException {
        return mongoTemplate.findAll(Topico.class);
    }

}
