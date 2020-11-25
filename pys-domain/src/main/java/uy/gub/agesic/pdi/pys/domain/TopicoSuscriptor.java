package uy.gub.agesic.pdi.pys.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document(collection = "topico_suscriptor")
public class TopicoSuscriptor implements Serializable {

    private static final long serialVersionUID = 2405172041950251807L;

    @Id
    private String id;

    @DBRef
    private Topico topico;

    @DBRef
    private Suscriptor suscriptor;

    private String deliveryMode;

    private String deliveryAddr;

    private String deliveryWsaTo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Topico getTopico() {
        return topico;
    }

    public void setTopico(Topico topico) {
        this.topico = topico;
    }

    public Suscriptor getSuscriptor() {
        return suscriptor;
    }

    public void setSuscriptor(Suscriptor suscriptor) {
        this.suscriptor = suscriptor;
    }

    public String getDeliveryMode() {
        return deliveryMode;
    }

    public void setDeliveryMode(String deliveryMode) {
        this.deliveryMode = deliveryMode;
    }

    public String getDeliveryAddr() {
        return deliveryAddr;
    }

    public void setDeliveryAddr(String deliveryAddr) {
        this.deliveryAddr = deliveryAddr;
    }

    public String getDeliveryWsaTo() {
        return deliveryWsaTo;
    }

    public void setDeliveryWsaTo(String deliveryWsaTo) {
        this.deliveryWsaTo = deliveryWsaTo;
    }


    @Override
    public String toString() {
        return "TopicoSuscriptor{" +
                "topico='" + topico.getNombre() + '\'' +
                ", suscriptor='" + suscriptor.getNombre() + '\'' +
                '}';
    }
}
