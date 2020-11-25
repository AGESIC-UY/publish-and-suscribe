package uy.gub.agesic.pdi.pys.backend.service;

import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FilterFilterDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.domain.Filter;

import java.util.List;
import java.util.Optional;

public interface FilterService {
    Optional<Filter> getFilter(String name);
    ResultadoPaginadoDTO<Filter> searchFilters(FilterFilterDTO filter) throws PSException;
    Filter saveFilter (Filter filter);
    void deleteFilter(Filter filter);
    List<Filter> getAllFilters();
}
