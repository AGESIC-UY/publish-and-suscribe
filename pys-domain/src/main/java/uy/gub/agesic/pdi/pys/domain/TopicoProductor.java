package uy.gub.agesic.pdi.pys.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document(collection = "topico_productor")
public class TopicoProductor implements Serializable {

    private static final long serialVersionUID = 2405172041950251807L;

    @Id
    private String id;

    @DBRef
    private Topico topico;

    @DBRef
    private Productor productor;

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

    public Productor getProductor() {
        return productor;
    }

    public void setProductor(Productor productor) {
        this.productor = productor;
    }

    @Override
    public String toString() {
        return "TopicoProductor {" +
                "topico='" + topico.getNombre() + '\'' +
                ", productor='" + productor.getNombre() + '\'' +
                '}';
    }
}
