package uy.gub.agesic.pdi.pys.backend.service;

import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroNovedadConsultaDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.domain.Novedad;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class NovedadServiceImpl implements NovedadService {

    private static final String SERVER_STATUS = "serverStatus";

    private static final String LOCAL_TIME = "localTime";

    private MongoTemplate mongoTemplate;

    @Autowired
    public NovedadServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Novedad registrar(Novedad item) throws PSException {
        DBObject command = new BasicDBObject();
        command.put(SERVER_STATUS, 1);
        CommandResult commandResult = mongoTemplate.getDb().command(command);
        Date date = commandResult.getDate(LOCAL_TIME);
        item.setFecha(date);

        String uuid = UUID.randomUUID().toString();
        item.setUuid(uuid);
        this.mongoTemplate.save(item);

        Query query = new Query();
        query.addCriteria(Criteria.where(BackendServiceUtil.UUID_PARAM).is(uuid));
        return mongoTemplate.findOne(query, Novedad.class);

    }

    @Override
    public ResultadoPaginadoDTO<Novedad> buscarNovedadesFiltro(FiltroNovedadConsultaDTO filtro) throws PSException, ParseException {
        Query query = QueryUtil.applyConsultaFilterQuery(filtro);

        if (filtro.getTopico() != null) {
            query.addCriteria(Criteria.where(BackendServiceUtil.TOPICOID_PARAM).is(new ObjectId(filtro.getTopico().getId())));
        }

        if (filtro.getProductor() != null) {
            query.addCriteria(Criteria.where(BackendServiceUtil.PRODUCTORID_PARAM).is(new ObjectId(filtro.getProductor().getId())));
        }

        if (filtro.getUuidNovedad() != null) {
            query.addCriteria(Criteria.where(BackendServiceUtil.UUID_PARAM).regex(filtro.getUuidNovedad()));
        }

        List<String> props = new ArrayList<>();
        props.add(BackendServiceUtil.FECHA_PARAM);
        Sort sort = new Sort(Sort.Direction.DESC, props);
        query.with(sort);

        ResultadoPaginadoDTO<Novedad> resultado = new ResultadoPaginadoDTO<>();
        resultado.setTotalTuplas(mongoTemplate.count(query, Novedad.class));

        QueryUtil.applySizeLimit(query, filtro);

        query.fields().include("fecha");
        query.fields().include("id");
        query.fields().include("productor");
        query.fields().include("topico");
        query.fields().include("uuid");

        List<Novedad> listNovedades = mongoTemplate.find(query, Novedad.class);
        resultado.setResultado(listNovedades);

        return resultado;
    }

    @Override
    public Novedad buscarNovedad(String uuidNovedad) throws PSException {
        Query query = new Query();
        query.addCriteria(Criteria.where(BackendServiceUtil.UUID_PARAM).is(uuidNovedad));
        return mongoTemplate.findOne(query, Novedad.class);
    }

}
