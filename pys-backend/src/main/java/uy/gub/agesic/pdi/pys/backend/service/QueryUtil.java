package uy.gub.agesic.pdi.pys.backend.service;

import com.mongodb.DBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import uy.gub.agesic.pdi.common.utiles.dtos.FiltroDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroBaseConsultaDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroBaseEntidadesDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QueryUtil {

    private static final Logger logger = LoggerFactory.getLogger(QueryUtil.class);


    private QueryUtil() {
        //Privado
    }

    static Query applyFilterQuery(FiltroBaseEntidadesDTO filtro) {
        Query query = new Query();

        if (filtro.getHabilitado() != null) {
            query.addCriteria(Criteria.where(BackendServiceUtil.HABILITADO_PARAM).is(filtro.getHabilitado()));
        }

        if (filtro.getNombre() != null) {
            query.addCriteria(Criteria.where(BackendServiceUtil.NOMBRE_PARAM).regex(filtro.getNombre(), BackendServiceUtil.NO_CASE_SENSITIVE));
        }

        if (filtro.getDn() != null) {
            query.addCriteria(Criteria.where(BackendServiceUtil.DN_PARAM).regex(filtro.getDn(), BackendServiceUtil.NO_CASE_SENSITIVE));
        }

        List<String> props = new ArrayList<>();
        props.add(BackendServiceUtil.FECHA_CREACION_PARAM);
        Sort sort = new Sort(Sort.Direction.DESC, props);
        query.with(sort);

        return query;
    }

    static void applySizeLimit(Query query, FiltroDTO filtro) {
        int currentPage = filtro.getCurrentPage();
        int pageSize = filtro.getPageSize();

        query.skip(currentPage * pageSize);
        query.limit(pageSize);
    }

    static Query applyConsultaFilterQuery(FiltroBaseConsultaDTO filtro) {
        Query query = new Query();

        if (filtro.getFechaDesde() != null && filtro.getFechaHasta() != null) {
            final Date from = filtro.getFechaDesde();
            final Date to = filtro.getFechaHasta();
            query.addCriteria(Criteria.where(BackendServiceUtil.FECHA_PARAM).gte(from).lte(to));

        } else {
            if (filtro.getFechaHasta() != null) {
                final Date to = filtro.getFechaHasta();
                query.addCriteria(Criteria.where(BackendServiceUtil.FECHA_PARAM).lte(to));
            }

            if (filtro.getFechaDesde() != null) {
                final Date from = filtro.getFechaDesde();
                query.addCriteria(Criteria.where(BackendServiceUtil.FECHA_PARAM).gte(from));
            }
        }

        return query;
    }

    static void printQueryExplain(MongoTemplate mongoTemplate, String collection, Query query) {
        String str = "-------------------------------------------";
        logger.debug(str);
        DBObject dbObject = mongoTemplate.getCollection(collection).find(query.getQueryObject()).explain();
        String explainData = dbObject == null ? "" : dbObject.toString();
        logger.debug(explainData);
        logger.debug(str);
    }

}
