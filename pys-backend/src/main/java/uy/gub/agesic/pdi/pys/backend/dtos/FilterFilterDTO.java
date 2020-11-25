package uy.gub.agesic.pdi.pys.backend.dtos;

import uy.gub.agesic.pdi.common.utiles.dtos.FiltroDTO;

public class FilterFilterDTO extends FiltroDTO {
    private String name;

    public FilterFilterDTO(Integer currentPage, Integer pageSize) {
        super(currentPage, pageSize);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
