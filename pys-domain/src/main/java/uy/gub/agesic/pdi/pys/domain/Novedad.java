package uy.gub.agesic.pdi.pys.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Document(collection = "novedades")
@CompoundIndexes({
        @CompoundIndex(name = "filtro", def = "{'fecha' : 1, 'uuid': 1, 'productor.id': 1, 'topico.id': 1}")
})
public class Novedad implements Serializable, Comparable<Novedad> {

    private static final long serialVersionUID = 2405172041950251807L;

    @Id
    private String id;
    private String contenido;
    @Indexed(direction = IndexDirection.DESCENDING)
    private Date fecha;
    @Indexed
    private String uuid;
    private String wsaRelatesTo;
    private String wsaMessageId;

    @DBRef
    private Productor productor;

    @DBRef
    private Topico topico;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getWsaRelatesTo() {
        return wsaRelatesTo;
    }

    public void setWsaRelatesTo(String wsaRelatesTo) {
        this.wsaRelatesTo = wsaRelatesTo;
    }

    public String getWsaMessageId() {
        return wsaMessageId;
    }

    public void setWsaMessageId(String wsaMessageId) {
        this.wsaMessageId = wsaMessageId;
    }

    @Override
    public int compareTo(Novedad o) {
        if(id.compareTo(o.getId()) < 0)
            return -1;
        else if(id.compareTo(o.getId()) > 0)
            return 1;
        else return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Novedad)) return false;

        Novedad novedad = (Novedad) o;

        if (!id.equals(novedad.id)) return false;
        if (contenido != null ? !contenido.equals(novedad.contenido) : novedad.contenido != null) return false;
        if (fecha != null ? !fecha.equals(novedad.fecha) : novedad.fecha != null) return false;
        if (!uuid.equals(novedad.uuid)) return false;
        if (wsaRelatesTo != null ? !wsaRelatesTo.equals(novedad.wsaRelatesTo) : novedad.wsaRelatesTo != null)
            return false;
        if (wsaMessageId != null ? !wsaMessageId.equals(novedad.wsaMessageId) : novedad.wsaMessageId != null)
            return false;
        if (productor != null ? !productor.equals(novedad.productor) : novedad.productor != null) return false;
        return topico != null ? topico.equals(novedad.topico) : novedad.topico == null;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (contenido != null ? contenido.hashCode() : 0);
        result = 31 * result + (fecha != null ? fecha.hashCode() : 0);
        result = 31 * result + uuid.hashCode();
        result = 31 * result + (wsaRelatesTo != null ? wsaRelatesTo.hashCode() : 0);
        result = 31 * result + (wsaMessageId != null ? wsaMessageId.hashCode() : 0);
        result = 31 * result + (productor != null ? productor.hashCode() : 0);
        result = 31 * result + (topico != null ? topico.hashCode() : 0);
        return result;
    }

}
