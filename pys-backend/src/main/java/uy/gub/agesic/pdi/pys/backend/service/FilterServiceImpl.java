package uy.gub.agesic.pdi.pys.backend.service;

import org.springframework.stereotype.Service;
import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FilterFilterDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.backend.repository.FilterRepository;
import uy.gub.agesic.pdi.pys.domain.Filter;

import java.util.List;
import java.util.Optional;

@Service
public class FilterServiceImpl implements  FilterService {
    private final FilterRepository filterRepository;

    public FilterServiceImpl(FilterRepository filterRepository) {
        this.filterRepository = filterRepository;
    }

    @Override
    public Optional<Filter> getFilter(String name) {
        return this.filterRepository.findOneByName(name);
    }

    @Override
    public ResultadoPaginadoDTO<Filter> searchFilters(FilterFilterDTO filter) throws PSException {
        return this.filterRepository.searchFilters(filter);
    }

    @Override
    public Filter saveFilter(Filter filter) {
        return this.filterRepository.save(filter);
    }

    @Override
    public void deleteFilter(Filter filter) {
        this.filterRepository.delete(filter);
    }

    @Override
    public List<Filter> getAllFilters() {
        return this.filterRepository.findAll();
    }
}
