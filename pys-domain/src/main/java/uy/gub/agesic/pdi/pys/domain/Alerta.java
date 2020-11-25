package uy.gub.agesic.pdi.pys.domain;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Document(collection = "alertas")
@CompoundIndexes({
        @CompoundIndex(name = "filtro", def = "{'topico' : 1, 'productor': 1, 'suscriptor': 1, 'uuid': 1, 'fecha': 1}")
})
public class Alerta implements Serializable, Comparable<Alerta> {

    private static final long serialVersionUID = 2405172041950251807L;

    @Id
    private String id;
    @DBRef
    private Entrega entrega;

    private String uuid;

    private String error;

    private String descripcion;

    @DBRef
    private Suscriptor suscriptor;

    @DBRef
    private Productor productor;

    @DBRef
    private Topico topico;

    @CreatedDate
    @Indexed(direction = IndexDirection.DESCENDING)
    private Date fecha;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Entrega getEntrega() {
        return entrega;
    }

    public void setEntrega(Entrega entrega) {
        this.entrega = entrega;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Suscriptor getSuscriptor() {
        return suscriptor;
    }

    public void setSuscriptor(Suscriptor suscriptor) {
        this.suscriptor = suscriptor;
    }

    public Productor getProductor() {
        return productor;
    }

    public void setProductor(Productor productor) {
        this.productor = productor;
    }

    public Topico getTopico() {
        return topico;
    }

    public void setTopico(Topico topico) {
        this.topico = topico;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public int compareTo(Alerta o) {
        if(id.compareTo(o.getId()) < 0)
            return -1;
        else if(id.compareTo(o.getId()) > 0)
            return 1;
        else return 0;
    }

    @java.lang.SuppressWarnings("squid:S3776")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Alerta)) return false;

        Alerta alerta = (Alerta) o;

        if (!id.equals(alerta.id)) return false;
        if (entrega != null ? !entrega.equals(alerta.entrega) : alerta.entrega != null) return false;
        if (!uuid.equals(alerta.uuid)) return false;
        if (error != null ? !error.equals(alerta.error) : alerta.error != null) return false;
        if (descripcion != null ? !descripcion.equals(alerta.descripcion) : alerta.descripcion != null) return false;
        if (suscriptor != null ? !suscriptor.equals(alerta.suscriptor) : alerta.suscriptor != null) return false;
        if (productor != null ? !productor.equals(alerta.productor) : alerta.productor != null) return false;
        if (topico != null ? !topico.equals(alerta.topico) : alerta.topico != null) return false;
        return fecha != null ? fecha.equals(alerta.fecha) : alerta.fecha == null;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (entrega != null ? entrega.hashCode() : 0);
        result = 31 * result + uuid.hashCode();
        result = 31 * result + (error != null ? error.hashCode() : 0);
        result = 31 * result + (descripcion != null ? descripcion.hashCode() : 0);
        result = 31 * result + (suscriptor != null ? suscriptor.hashCode() : 0);
        result = 31 * result + (productor != null ? productor.hashCode() : 0);
        result = 31 * result + (topico != null ? topico.hashCode() : 0);
        result = 31 * result + (fecha != null ? fecha.hashCode() : 0);
        return result;
    }

}
