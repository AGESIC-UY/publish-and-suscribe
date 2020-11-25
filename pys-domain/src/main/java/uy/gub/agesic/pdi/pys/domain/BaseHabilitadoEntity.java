package uy.gub.agesic.pdi.pys.domain;

import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

public class BaseHabilitadoEntity extends BaseNamedEntity {

    private Boolean habilitado;
    @CreatedDate
    private Date fechaCreacion;

    public Boolean getHabilitado() {
        return habilitado;
    }

    public void setHabilitado(Boolean habilitado) {
        this.habilitado = habilitado;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseHabilitadoEntity)) return false;

        BaseHabilitadoEntity that = (BaseHabilitadoEntity) o;

        if (habilitado != null ? !habilitado.equals(that.habilitado) : that.habilitado != null) return false;
        return fechaCreacion != null ? fechaCreacion.equals(that.fechaCreacion) : that.fechaCreacion == null;
    }

    @Override
    public int hashCode() {
        int result = habilitado != null ? habilitado.hashCode() : 0;
        result = 31 * result + (fechaCreacion != null ? fechaCreacion.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
