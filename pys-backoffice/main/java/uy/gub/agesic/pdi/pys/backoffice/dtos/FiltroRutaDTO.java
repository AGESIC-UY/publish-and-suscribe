package uy.gub.agesic.pdi.pys.backoffice.dtos;


import uy.gub.agesic.pdi.common.utiles.dtos.FiltroDTO;

public class FiltroRutaDTO extends FiltroDTO {

    private String logical;

    private String physical;

    private String baseURI;

    private Boolean degraded;

    private Long degradeTimeout;

    private Long degradePermits;

    public FiltroRutaDTO() {}

    public FiltroRutaDTO(Integer currentPage, Integer pageSize) {
        super(currentPage, pageSize);
    }

    public String getLogical() {
        return logical;
    }

    public void setLogical(String logical) {
        this.logical = logical;
    }

    public String getPhysical() {
        return physical;
    }

    public void setPhysical(String physical) {
        this.physical = physical;
    }

    public String getBaseURI() {
        return baseURI;
    }

    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }

    public Boolean getDegraded() { return degraded; }

    public void setDegraded(Boolean degraded) { this.degraded = degraded; }

    public Long getDegradeTimeout() { return degradeTimeout; }

    public void setDegradeTimeout(Long degradeTimeout) { this.degradeTimeout = degradeTimeout; }

    public Long getDegradePermits() { return degradePermits; }

    public void setDegradePermits(Long degradePermits) { this.degradePermits = degradePermits; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FiltroRutaDTO rutaDTO = (FiltroRutaDTO) o;

        if (logical != null ? !logical.equals(rutaDTO.logical) : rutaDTO.logical != null) return false;
        if (physical != null ? !physical.equals(rutaDTO.physical) : rutaDTO.physical != null) return false;
        return baseURI != null ? baseURI.equals(rutaDTO.baseURI) : rutaDTO.baseURI == null;
    }

    @Override
    public String toString() {
        return "RutaDTO{" +
                "logical='" + logical + '\'' +
                ", physical='" + physical + '\'' +
                ", baseURI='" + baseURI + '\'' +
                '}';
    }


    @Override
    public int hashCode() {
        int result = logical != null ? logical.hashCode() : 0;
        result = 31 * result + (physical != null ? physical.hashCode() : 0);
        result = 31 * result + (baseURI != null ? baseURI.hashCode() : 0);
        return result;
    }
}
