package uy.gub.agesic.pdi.pys.backend.dtos;

public class FiltroEntregaConsultaDTO extends FiltroBaseConsultaDTO {

    private String estado;
    private String tipoEntrega;

    public FiltroEntregaConsultaDTO() {}

    public FiltroEntregaConsultaDTO(Integer currentPage, Integer pageSize) {
        super(currentPage, pageSize);
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTipoEntrega() {
        return tipoEntrega;
    }

    public void setTipoEntrega(String tipoEntrega) {
        this.tipoEntrega = tipoEntrega;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FiltroEntregaConsultaDTO)) return false;
        if (!super.equals(o)) return false;

        FiltroEntregaConsultaDTO that = (FiltroEntregaConsultaDTO) o;

        if (estado != null ? !estado.equals(that.estado) : that.estado != null) return false;
        return tipoEntrega != null ? tipoEntrega.equals(that.tipoEntrega) : that.tipoEntrega == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (estado != null ? estado.hashCode() : 0);
        result = 31 * result + (tipoEntrega != null ? tipoEntrega.hashCode() : 0);
        return result;
    }
}
