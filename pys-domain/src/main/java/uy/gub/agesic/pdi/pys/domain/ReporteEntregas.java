package uy.gub.agesic.pdi.pys.domain;


import java.io.Serializable;

public class ReporteEntregas implements Serializable {

    private static final long serialVersionUID = 2405172041950251807L;

    private String topico;
    private String suscriptor;
    private String estado;
    private String total;

    public ReporteEntregas(String estado, String total) {
        this.estado = estado;
        this.total = total;
    }

    public ReporteEntregas() { }

        public String getTopico() {
        return topico;
    }

    public void setTopico(String topico) {
        this.topico = topico;
    }

    public String getSuscriptor() {
        return suscriptor;
    }

    public void setSuscriptor(String suscriptor) {
        this.suscriptor = suscriptor;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
