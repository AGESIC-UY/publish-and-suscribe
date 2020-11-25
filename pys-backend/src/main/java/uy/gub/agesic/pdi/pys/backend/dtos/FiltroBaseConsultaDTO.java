package uy.gub.agesic.pdi.pys.backend.dtos;

import uy.gub.agesic.pdi.common.utiles.dtos.FiltroDTO;
import uy.gub.agesic.pdi.pys.domain.Productor;
import uy.gub.agesic.pdi.pys.domain.Suscriptor;
import uy.gub.agesic.pdi.pys.domain.Topico;

import java.util.Date;

public class FiltroBaseConsultaDTO extends FiltroDTO {

    private String novedadId;

    private Date fechaDesde;

    private Date fechaHasta;

    private Topico topico;

    private Productor productor;

    private Suscriptor suscriptor;

    public FiltroBaseConsultaDTO() {}

    public FiltroBaseConsultaDTO(Integer currentPage, Integer pageSize) {
        super(currentPage, pageSize);
    }

    public String getNovedadId() {
        return novedadId;
    }

    public void setNovedadId(String novedadId) {
        this.novedadId = novedadId;
    }

    public Date getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(Date fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public Date getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
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

    public Suscriptor getSuscriptor() {
        return suscriptor;
    }

    public void setSuscriptor(Suscriptor suscriptor) {
        this.suscriptor = suscriptor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FiltroBaseConsultaDTO)) return false;
        if (!super.equals(o)) return false;

        FiltroBaseConsultaDTO that = (FiltroBaseConsultaDTO) o;

        if (novedadId != null ? !novedadId.equals(that.novedadId) : that.novedadId != null) return false;
        if (fechaDesde != null ? !fechaDesde.equals(that.fechaDesde) : that.fechaDesde != null) return false;
        if (fechaHasta != null ? !fechaHasta.equals(that.fechaHasta) : that.fechaHasta != null) return false;
        if (topico != null ? !topico.equals(that.topico) : that.topico != null) return false;
        if (productor != null ? !productor.equals(that.productor) : that.productor != null) return false;
        return suscriptor != null ? suscriptor.equals(that.suscriptor) : that.suscriptor == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (novedadId != null ? novedadId.hashCode() : 0);
        result = 31 * result + (fechaDesde != null ? fechaDesde.hashCode() : 0);
        result = 31 * result + (fechaHasta != null ? fechaHasta.hashCode() : 0);
        result = 31 * result + (topico != null ? topico.hashCode() : 0);
        result = 31 * result + (productor != null ? productor.hashCode() : 0);
        result = 31 * result + (suscriptor != null ? suscriptor.hashCode() : 0);
        return result;
    }
}
