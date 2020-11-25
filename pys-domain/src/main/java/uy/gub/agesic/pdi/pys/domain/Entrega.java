package uy.gub.agesic.pdi.pys.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Document
@CompoundIndexes({
        @CompoundIndex(name = "filtro", def = "{'fechaCreado' : 1, 'uuid': 1, 'estado': 1, 'tipoEntrega': 1}")
})
public class Entrega implements Serializable {

    private static final long serialVersionUID = 2405172041950251807L;

    @Id
    private String id;

    private Date fechaEnviado;

    private String estado;

    private String reason;

    @Indexed
    private String uuid;

    private Date fechaCreado;

    private String tipoEntrega;

    private int cantidadReintentos;

    private Date fechaUltimoIntento;

    @DBRef
    private Novedad novedad;

    @DBRef
    private Suscriptor suscriptor;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getFechaEnviado() {
        return fechaEnviado;
    }

    public void setFechaEnviado(Date fechaEnviado) {
        this.fechaEnviado = fechaEnviado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Novedad getNovedad() {
        return novedad;
    }

    public void setNovedad(Novedad novedad) {
        this.novedad = novedad;
    }

    public Suscriptor getSuscriptor() {
        return suscriptor;
    }

    public void setSuscriptor(Suscriptor suscriptor) {
        this.suscriptor = suscriptor;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getFechaCreado() {
        return fechaCreado;
    }

    public void setFechaCreado(Date fechaCreado) {
        this.fechaCreado = fechaCreado;
    }

    public String getTipoEntrega() {
        return tipoEntrega;
    }

    public void setTipoEntrega(String tipoEntrega) {
        this.tipoEntrega = tipoEntrega;
    }

    public int getCantidadReintentos() {
        return cantidadReintentos;
    }

    public void setCantidadReintentos(int cantidadReintentos) {
        this.cantidadReintentos = cantidadReintentos;
    }

    public Date getFechaUltimoIntento() {
        return fechaUltimoIntento;
    }

    public void setFechaUltimoIntento(Date fechaUltimoIntento) {
        this.fechaUltimoIntento = fechaUltimoIntento;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entrega)) return false;

        Entrega entrega = (Entrega) o;

        return id != null ? id.equals(entrega.id) : entrega.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Entrega{" +
                "id='" + id + '\'' +
                ", uuid='" + uuid + '\'' +
                ", cantidadReintentos=" + cantidadReintentos +
                ", fechaUltimoIntento=" + fechaUltimoIntento +
                ", suscriptor=" + suscriptor +
                '}';
    }
}
