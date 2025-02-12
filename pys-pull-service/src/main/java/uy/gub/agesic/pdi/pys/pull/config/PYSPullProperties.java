package uy.gub.agesic.pdi.pys.pull.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@RefreshScope
public class PYSPullProperties implements Serializable {

    @Value("${application.accessPDI.enabled:true}")
    private Boolean accessPDIEnabled;

    public Boolean getAccessPDIEnabled() {
        return accessPDIEnabled;
    }

    public void setAccessPDIEnabled(Boolean accessPDIEnabled) {
        this.accessPDIEnabled = accessPDIEnabled;
    }

}