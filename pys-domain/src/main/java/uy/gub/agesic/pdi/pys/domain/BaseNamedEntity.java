package uy.gub.agesic.pdi.pys.domain;

import java.io.Serializable;

public abstract class BaseNamedEntity implements Serializable, Comparable<BaseNamedEntity> {

    private static final long serialVersionUID = 2405172041950251807L;

    protected String nombre;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public int compareTo(BaseNamedEntity o) {
        if (nombre.compareTo(o.getNombre()) < 0) {
            return -1;
        } else if(nombre.compareTo(o.getNombre()) > 0) {
            return 1;
        } else {
            return 0;
        }
    }

    public abstract boolean equals(Object o);

    public abstract int hashCode();

}
