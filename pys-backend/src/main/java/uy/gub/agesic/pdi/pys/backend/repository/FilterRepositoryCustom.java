package uy.gub.agesic.pdi.pys.backend.repository;

import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FilterFilterDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.domain.Filter;

public interface FilterRepositoryCustom  {
    ResultadoPaginadoDTO<Filter> searchFilters(FilterFilterDTO filter) throws PSException;
}
