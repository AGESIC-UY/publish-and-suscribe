package uy.gub.agesic.pdi.pys.backend.dtos;

import uy.gub.agesic.pdi.common.utiles.dtos.FiltroDTO;

public class FiltroBaseEntidadesDTO extends FiltroDTO {

    private String nombre;
    private String dn;
    private String fechaCreacion;
    private Boolean habilitado;

    public FiltroBaseEntidadesDTO() {}

    public FiltroBaseEntidadesDTO(Integer currentPage, Integer pageSize) {
        super(currentPage, pageSize);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Boolean getHabilitado() {
        return habilitado;
    }

    public void setHabilitado(Boolean habilitado) {
        this.habilitado = habilitado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FiltroBaseEntidadesDTO)) return false;
        if (!super.equals(o)) return false;

        FiltroBaseEntidadesDTO that = (FiltroBaseEntidadesDTO) o;

        if (!nombre.equals(that.nombre)) return false;
        if (dn != null ? !dn.equals(that.dn) : that.dn != null) return false;
        if (fechaCreacion != null ? !fechaCreacion.equals(that.fechaCreacion) : that.fechaCreacion != null)
            return false;
        return habilitado != null ? habilitado.equals(that.habilitado) : that.habilitado == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + nombre.hashCode();
        result = 31 * result + (dn != null ? dn.hashCode() : 0);
        result = 31 * result + (fechaCreacion != null ? fechaCreacion.hashCode() : 0);
        result = 31 * result + (habilitado != null ? habilitado.hashCode() : 0);
        return result;
    }

}
