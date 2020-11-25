package uy.gub.agesic.pdi.pys.backend.dtos;

public class FiltroNovedadConsultaDTO extends FiltroBaseConsultaDTO {

    private String uuidNovedad;

    public FiltroNovedadConsultaDTO() {}

    public FiltroNovedadConsultaDTO(Integer currentPage, Integer pageSize) {
        super(currentPage, pageSize);
    }

    public String getUuidNovedad() {
        return uuidNovedad;
    }

    public void setUuidNovedad (String uuidNovedad) {
        this.uuidNovedad = uuidNovedad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FiltroNovedadConsultaDTO)) return false;
        if (!super.equals(o)) return false;

        FiltroNovedadConsultaDTO that = (FiltroNovedadConsultaDTO) o;

        return uuidNovedad != null ? uuidNovedad.equals(that.uuidNovedad) : that.uuidNovedad == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (uuidNovedad != null ? uuidNovedad.hashCode() : 0);
        return result;
    }
}
