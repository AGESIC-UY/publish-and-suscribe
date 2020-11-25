package uy.gub.agesic.pdi.pys.backoffice.dtos;

import uy.gub.agesic.pdi.common.utiles.dtos.BaseDTO;

import java.util.Date;

public class NovedadDTO extends BaseDTO {

    private String id;

    private Date fecha;

    private String uuid;

    private String productor;

    private String topico;

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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getProductor() {
        return productor;
    }

    public void setProductor(String productor) {
        this.productor = productor;
    }

    public String getTopico() {
        return topico;
    }

    public void setTopico(String topico) {
        this.topico = topico;
    }
}
