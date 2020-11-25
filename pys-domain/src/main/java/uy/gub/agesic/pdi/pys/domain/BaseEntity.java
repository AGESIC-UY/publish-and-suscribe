package uy.gub.agesic.pdi.pys.domain;

import org.springframework.data.annotation.Id;

public class BaseEntity extends BaseHabilitadoEntity {

    @Id
    private String id;
    private String dn;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity)) return false;
        if (!super.equals(o)) return false;

        BaseEntity that = (BaseEntity) o;

        if (!id.equals(that.id)) return false;
        return dn != null ? dn.equals(that.dn) : that.dn == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + id.hashCode();
        result = 31 * result + (dn != null ? dn.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
