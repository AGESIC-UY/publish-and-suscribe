package uy.gub.agesic.pdi.pys.backend.repository;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FilterFilterDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.domain.Filter;

import java.util.List;
import java.util.regex.PatternSyntaxException;

@Repository
public class FilterRepositoryImpl implements FilterRepositoryCustom {
    private MongoTemplate mongoTemplate;

    public FilterRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public ResultadoPaginadoDTO<Filter> searchFilters(FilterFilterDTO filter) throws PSException {
        try {
            Query query = new Query();
            if (filter.getName() != null) {
                query.addCriteria(Criteria.where("name").regex(filter.getName(), "i"));
            }

            ResultadoPaginadoDTO<Filter> result = new ResultadoPaginadoDTO<>();
            result.setTotalTuplas(mongoTemplate.count(query, Filter.class));

            this.applySizeLimit(query, filter);

            List<Filter> listFilters = mongoTemplate.find(query, Filter.class);
            result.setResultado(listFilters);

            return result;
        } catch (PatternSyntaxException e) {
            throw new PSException("Fueron ingresados datos invalidos", null, "INVALIDFILTERDATA", e);
        }
    }

    private void applySizeLimit(Query query, FilterFilterDTO filter) {
        int currentPage = filter.getCurrentPage();
        int pageSize = filter.getPageSize();

        query.skip(currentPage * pageSize);
        query.limit(pageSize);
    }
}
