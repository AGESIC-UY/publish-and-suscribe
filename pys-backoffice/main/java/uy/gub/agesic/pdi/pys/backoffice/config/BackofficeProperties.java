package uy.gub.agesic.pdi.pys.backoffice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@RefreshScope
public class BackofficeProperties implements Serializable {

    @Value("${application.version}")
    private String appVersion;

    @Value("${application.filasPorPagina}")
    private String filasPorPagina;

    @Value("${application.mensajeConfirmacion}")
    private String mensajeConfirmacion;

    @Value("${application.mensajeReconfirmacion}")
    private String mensajeReconfirmacion;

    @Value("${application.direccionBasePorDefecto}")
    private String direccionBasePorDefecto;

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getFilasPorPagina() {
        return filasPorPagina;
    }

    public void setFilasPorPagina(String filasPorPagina) {
        this.filasPorPagina = filasPorPagina;
    }

    public String getMensajeConfirmacion() {
        return mensajeConfirmacion;
    }

    public void setMensajeConfirmacion(String mensajeConfirmacion) {
        this.mensajeConfirmacion = mensajeConfirmacion;
    }

    public String getMensajeReconfirmacion() {
        return mensajeReconfirmacion;
    }

    public void setMensajeReconfirmacion(String mensajeReconfirmacion) {
        this.mensajeReconfirmacion = mensajeReconfirmacion;
    }

    public String getDireccionBasePorDefecto() {
        return direccionBasePorDefecto;
    }

    public void setDireccionBasePorDefecto(String direccionBasePorDefecto) {
        this.direccionBasePorDefecto = direccionBasePorDefecto;
    }
}
