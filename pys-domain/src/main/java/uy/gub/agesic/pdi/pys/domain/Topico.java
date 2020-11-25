package uy.gub.agesic.pdi.pys.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "topicos")
public class Topico extends BaseHabilitadoEntity {

    private static final long serialVersionUID = 2405172041950251807L;

    @Id
    private String id;
    private String elementoRaiz;
    private String namespace;
    private String soapAction;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getElementoRaiz() {
        return elementoRaiz;
    }

    public void setElementoRaiz(String elementoRaiz) {
        this.elementoRaiz = elementoRaiz;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getSoapAction() {
        return soapAction;
    }

    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Topico)) return false;
        if (!super.equals(o)) return false;

        Topico topico = (Topico) o;

        if (!id.equals(topico.id)) return false;
        if (elementoRaiz != null ? !elementoRaiz.equals(topico.elementoRaiz) : topico.elementoRaiz != null)
            return false;
        if (namespace != null ? !namespace.equals(topico.namespace) : topico.namespace != null) return false;
        return soapAction != null ? soapAction.equals(topico.soapAction) : topico.soapAction == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + id.hashCode();
        result = 31 * result + (elementoRaiz != null ? elementoRaiz.hashCode() : 0);
        result = 31 * result + (namespace != null ? namespace.hashCode() : 0);
        result = 31 * result + (soapAction != null ? soapAction.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return nombre;
    }

}
