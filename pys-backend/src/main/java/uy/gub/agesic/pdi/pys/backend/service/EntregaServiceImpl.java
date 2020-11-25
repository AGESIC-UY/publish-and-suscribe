package uy.gub.agesic.pdi.pys.backend.service;

import com.mongodb.MongoClient;
import com.mongodb.WriteResult;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroEntregaConsultaDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.domain.Entrega;
import uy.gub.agesic.pdi.pys.domain.EstadoEntrega;
import uy.gub.agesic.pdi.pys.domain.Novedad;
import uy.gub.agesic.pdi.pys.domain.ReporteEntregas;
import uy.gub.agesic.pdi.pys.domain.Suscriptor;
import uy.gub.agesic.pdi.pys.domain.Topico;
import uy.gub.agesic.pdi.pys.domain.TopicoSuscriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Repository
public class EntregaServiceImpl implements EntregaService {

    private MongoTemplate mongoTemplate;

    private TopicoSuscriptorService topicoSuscriptorService;
    private NovedadService novedadService;

    @Autowired
    public EntregaServiceImpl(MongoTemplate mongoTemplate, TopicoSuscriptorService topicoSuscriptorService, NovedadService novedadService) {
        this.mongoTemplate = mongoTemplate;
        this.topicoSuscriptorService = topicoSuscriptorService;
        this.novedadService = novedadService;
    }

    @Override
    public void upsert(Entrega item) throws PSException {
        String collection = getCollectionName(item);
        mongoTemplate.save(item, collection);
    }

    @Override
    public void crearColeccion(String topico, String suscriptor) throws PSException {
        String nombreColeccion = getCollectionName(suscriptor, topico);
        mongoTemplate.indexOps(nombreColeccion).ensureIndex(new Index().on(BackendServiceUtil.FECHA_CREADO_PARAM,  Sort.Direction.DESC).named(BackendServiceUtil.FECHA_CREADO_PARAM));
        mongoTemplate.indexOps(nombreColeccion).ensureIndex(new Index().on(BackendServiceUtil.UUID_PARAM,  Sort.Direction.DESC).named(BackendServiceUtil.UUID_PARAM));
        mongoTemplate.indexOps(nombreColeccion).ensureIndex(new Index().on(BackendServiceUtil.FECHA_CREADO_PARAM,  Sort.Direction.DESC).
                on(BackendServiceUtil.UUID_PARAM,  Sort.Direction.DESC).on(BackendServiceUtil.ESTADO_PARAM,  Sort.Direction.DESC).
                on(BackendServiceUtil.TIPO_ENTREGA_PARAM,  Sort.Direction.DESC).named(BackendServiceUtil.FECHA_ESTADO_TE_PARAM));
     }

    @Override
    public void eliminar(Entrega item) throws PSException {
        String collection = getCollectionName(item);
        mongoTemplate.remove(item, collection);
    }

    @Override
    public int cancelar(Entrega item) throws PSException {
        String collection = getCollectionName(item);
        Query query = new Query();
        query.addCriteria(Criteria.where(BackendServiceUtil.UUID_PARAM).in(item.getUuid()));
        query.addCriteria(Criteria.where(BackendServiceUtil.ESTADO_PARAM).in(EstadoEntrega.PENDIENTE.name()));
        WriteResult result = mongoTemplate.updateFirst(query, Update.update(BackendServiceUtil.ESTADO_PARAM, EstadoEntrega.CANCELADO.name()), collection);
        if (result != null) {
            return result.getN();
        }
        return 0;
    }

    @Override
    public void cancelarEntregasSuscriptor(Suscriptor suscriptor) throws PSException {
        List<TopicoSuscriptor> listaTopicosSuscriptor = this.topicoSuscriptorService.buscarTopicosSuscriptor(suscriptor);

        Query query = new Query();
        query.addCriteria(Criteria.where(BackendServiceUtil.ESTADO_PARAM).in(EstadoEntrega.PENDIENTE.name()));

        for (TopicoSuscriptor item : listaTopicosSuscriptor) {
            String collection = getCollectionName(suscriptor.getNombre(), item.getTopico().getNombre());
            mongoTemplate.updateMulti(query, Update.update(BackendServiceUtil.ESTADO_PARAM, EstadoEntrega.CANCELADO.name()), collection);
        }
    }

    @Override
    public void cancelarEntregasTopicoSuscriptor(Suscriptor suscriptor, Topico topico) throws PSException {
        Query query = new Query();
        query.addCriteria(Criteria.where(BackendServiceUtil.ESTADO_PARAM).in(EstadoEntrega.PENDIENTE.name()));

        String collection = getCollectionName(suscriptor.getNombre(), topico.getNombre());
        mongoTemplate.updateMulti(query, Update.update(BackendServiceUtil.ESTADO_PARAM, EstadoEntrega.CANCELADO.name()), collection);

    }

    @Override
    public void eliminarEntregasSuscriptor(Suscriptor suscriptor) throws PSException {
        List<TopicoSuscriptor> listaTopicosSuscriptor = this.topicoSuscriptorService.buscarTopicosSuscriptor(suscriptor);

        Query query = new Query();
        query.addCriteria(Criteria.where(BackendServiceUtil.ESTADO_PARAM).in(EstadoEntrega.PENDIENTE.name()));

        for (TopicoSuscriptor item : listaTopicosSuscriptor) {
            String collection = getCollectionName(suscriptor.getNombre(), item.getTopico().getNombre());
            mongoTemplate.remove(query, collection);
        }
    }

    @Override
    public Entrega buscarPrimera(String suscriptor, String topico) throws PSException {
        Query query = new Query();
        query.addCriteria(Criteria.where(BackendServiceUtil.ESTADO_PARAM).in(EstadoEntrega.PENDIENTE.name()));
        query.with(new Sort(Sort.Direction.ASC, BackendServiceUtil.FECHA_CREADO_PARAM));

        String collection = getCollectionName(suscriptor, topico);
        return this.mongoTemplate.findOne(query, Entrega.class, collection);
    }

    @Override
    public Entrega buscarEntrega(String idEntrega, String suscriptor, String topico) throws PSException {
        String collection = getCollectionName(suscriptor, topico);
        return this.mongoTemplate.findById(idEntrega, Entrega.class, collection);
    }

    @Override
    public List<Entrega> buscarEntregasTopicoSuscriptor(String suscriptor, String topico) throws PSException {
        String collection = getCollectionName(suscriptor, topico);
        return mongoTemplate.findAll(Entrega.class, collection);
    }

    @Override
    public List<Entrega> buscarEntregasNovedad(String uuidNovedad) throws PSException {
        List<Entrega> listEntregas = new ArrayList<>();

        Novedad novedad = novedadService.buscarNovedad(uuidNovedad);
        String topico = novedad.getTopico().getNombre();
        List<TopicoSuscriptor> listTopicosSuscriptor = topicoSuscriptorService.buscarTopicosSuscriptor(topico);

        for (TopicoSuscriptor item : listTopicosSuscriptor) {
            String collection = getCollectionName(item.getSuscriptor().getNombre(), topico);
            Query query = new Query();
            query.addCriteria(Criteria.where(BackendServiceUtil.NOVEDADID_PARAM).is(new ObjectId(novedad.getId())));
            Entrega entrega = this.mongoTemplate.findOne(query, Entrega.class, collection);
            if (entrega != null) {
                listEntregas.add(entrega);
            }

        }

        return listEntregas;
    }

    @Override
    public ResultadoPaginadoDTO<Entrega> buscarEntregaFiltro(FiltroEntregaConsultaDTO filtro) throws PSException {
        Topico topic = filtro.getTopico();
        Suscriptor suscriptor = filtro.getSuscriptor();

        if (suscriptor == null || topic == null) {
            throw new PSException("El t\u00F3pico y el suscriptor no pueden ser vac\u00EDos", null);
        }

        String collection = getCollectionName(suscriptor.getNombre(), topic.getNombre());

        Query query = new Query();

        if (filtro.getFechaDesde() != null && filtro.getFechaHasta() != null) {
            query.addCriteria(Criteria.where(BackendServiceUtil.FECHA_CREADO_PARAM).gte(filtro.getFechaDesde()).lte(filtro.getFechaHasta()));

        } else {
            if (filtro.getFechaHasta() != null) {
                query.addCriteria(Criteria.where(BackendServiceUtil.FECHA_CREADO_PARAM).lte(filtro.getFechaHasta()));
            }

            if (filtro.getFechaDesde() != null) {
                query.addCriteria(Criteria.where(BackendServiceUtil.FECHA_CREADO_PARAM).gte(filtro.getFechaDesde()));
            }
        }

        if (filtro.getNovedadId() != null) {
            query.addCriteria(Criteria.where(BackendServiceUtil.UUID_PARAM).is(filtro.getNovedadId()));
        }

        if (filtro.getEstado() != null) {
            query.addCriteria(Criteria.where(BackendServiceUtil.ESTADO_PARAM).is(filtro.getEstado()));
        }

        if (filtro.getTipoEntrega() != null) {
            query.addCriteria(Criteria.where(BackendServiceUtil.TIPO_ENTREGA_PARAM).is(filtro.getTipoEntrega()));
        }

        List<String> props = new ArrayList<>();
        props.add(BackendServiceUtil.FECHA_CREADO_PARAM);
        Sort sort = new Sort(Sort.Direction.DESC, props);
        query.with(sort);

        ResultadoPaginadoDTO<Entrega> resultado = new ResultadoPaginadoDTO<>();
        resultado.setTotalTuplas(mongoTemplate.count(query, collection));

        QueryUtil.applySizeLimit(query, filtro);

        List<Entrega> listEntregas = mongoTemplate.find(query, Entrega.class, collection);
        resultado.setResultado(listEntregas);

        return resultado;
    }

    @Override
    public ResultadoPaginadoDTO<ReporteEntregas> reporteEntregas(FiltroEntregaConsultaDTO filtro) throws PSException {

        Topico topic = filtro.getTopico();

        if (topic == null) {
            throw new PSException("El t\u00F3pico no puede ser vac\u00EDo", null);
        }

        if (filtro.getEstado() == null) {
            throw new PSException("El estado no puede ser vac\u00EDo", null);
        }

        Suscriptor suscriptor = filtro.getSuscriptor();
        Map<String,String> listCollections = new HashMap<>();

        if (suscriptor != null) {
            listCollections.put(getCollectionName(suscriptor.getNombre(), topic.getNombre()),suscriptor.getNombre());
        } else {

           List<TopicoSuscriptor> topicoSuscriptors = topicoSuscriptorService.buscarTopicosSuscriptor(topic.getNombre());
           for (TopicoSuscriptor item : topicoSuscriptors) {
               listCollections.put(getCollectionName(item.getSuscriptor().getNombre(), item.getTopico().getNombre()), item.getSuscriptor().getNombre());
           }
        }

        MongoClient client = (MongoClient) mongoTemplate.getDb().getMongo();
        try (MongoClient client2 = new MongoClient(client.getServerAddressList(), client.getCredentialsList());) {
            return executeQueryEntregas(client2, filtro, topic, listCollections);
        }
    }

    private ResultadoPaginadoDTO<ReporteEntregas> executeQueryEntregas(MongoClient client, FiltroEntregaConsultaDTO filtro, Topico topic, Map<String,String> listCollections) {
        MongoDatabase db = client.getDatabase(mongoTemplate.getDb().getName());

        long size = 0;
        long contador = 0;
        int currentPage = filtro.getCurrentPage();
        int pageSize = filtro.getPageSize();

        int limit = pageSize;
        int skip = currentPage * pageSize;

        Document group = new Document(BackendServiceUtil.GROUP_QUERY, new Document("_id", "$estado").append(BackendServiceUtil.COUNT_PARAM, new Document(BackendServiceUtil.SUM_QUERY, 1)));
        Document match = new Document(BackendServiceUtil.MATCH_QUERY, new Document(BackendServiceUtil.ESTADO_PARAM, filtro.getEstado()));

        Document matchFecha = processFiltroFecha(filtro);

        List<ReporteEntregas> listReporteEntregas = new ArrayList<>();
        for (Map.Entry<String, String> collection : listCollections.entrySet()) {
            MongoCollection<Document> col = db.getCollection(collection.getKey());
            String nomSuscriptor = collection.getValue();

            AggregateIterable<Document> output;
            if (matchFecha == null) {
                output = col.aggregate(Arrays.asList(match, group));
            } else {
                output = col.aggregate(Arrays.asList(match, matchFecha, group));
            }

            for (Document dbObject : output) {

                if (contador >= skip && size < limit) {
                    ReporteEntregas item = new ReporteEntregas();
                    item.setTopico(topic.getNombre());
                    item.setSuscriptor(nomSuscriptor);
                    item.setEstado(dbObject.get("_id").toString());
                    item.setTotal(dbObject.get(BackendServiceUtil.COUNT_PARAM).toString());
                    listReporteEntregas.add(item);
                    size ++;
                }
                contador ++;

            }
        }

        ResultadoPaginadoDTO<ReporteEntregas> resultado = new ResultadoPaginadoDTO<>();
        resultado.setTotalTuplas(contador);
        resultado.setResultado(listReporteEntregas);

        return resultado;
    }

    private Document processFiltroFecha(FiltroEntregaConsultaDTO filtro) {
        Document matchFecha = null;

        if (filtro.getFechaDesde() != null && filtro.getFechaHasta() != null) {
            final Date from = filtro.getFechaDesde();
            final Date to = filtro.getFechaHasta();
            matchFecha = new Document(BackendServiceUtil.MATCH_QUERY, new Document(BackendServiceUtil.FECHA_CREADO_PARAM, new Document(BackendServiceUtil.GTE_QUERY, from).append(BackendServiceUtil.LTE_QUERY, to)));

        } else {
            if (filtro.getFechaHasta() != null) {
                final Date to = filtro.getFechaHasta();
                matchFecha = new Document(BackendServiceUtil.MATCH_QUERY, new Document(BackendServiceUtil.FECHA_CREADO_PARAM, new Document(BackendServiceUtil.LTE_QUERY, to)));
            }

            if (filtro.getFechaDesde() != null) {
                final Date from = filtro.getFechaDesde();
                matchFecha = new Document(BackendServiceUtil.MATCH_QUERY, new Document(BackendServiceUtil.FECHA_CREADO_PARAM, new Document(BackendServiceUtil.GTE_QUERY, from)));
            }
        }
        return matchFecha;
    }

    @Override
    public Entrega buscarPrimera(Suscriptor suscriptor, Topico topico) throws PSException {
        return this.buscarPrimera(suscriptor.getNombre(), topico.getNombre());
    }

    private String getCollectionName(Entrega entrega) {
        return getCollectionName(entrega.getSuscriptor().getNombre(), entrega.getNovedad().getTopico().getNombre());
    }

    private String getCollectionName(String suscriptorName, String topicoName) {
        return suscriptorName + "_" + topicoName;
    }
}
