package uy.gub.agesic.pdi.pys.push.send;

import org.infinispan.health.CacheHealth;
import org.infinispan.health.ClusterHealth;
import org.infinispan.health.HealthStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uy.gub.agesic.pdi.pys.push.cache.TSCacheManager;
import uy.gub.agesic.pdi.pys.push.config.PYSPushProperties;

import java.util.List;

@Component
public class PushHAController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PushHAController.class);

    private PYSPushProperties pysPushProperties;

    private TSCacheManager tsCacheManager;

    @Autowired
    public PushHAController(TSCacheManager tsCacheManager, PYSPushProperties pysPushProperties) {
        this.tsCacheManager = tsCacheManager;
        this.pysPushProperties = pysPushProperties;
    }

    public boolean grantPushAccess() {
        if (!this.pysPushProperties.isMaster()) {
            List<CacheHealth> cacheHealthList = this.tsCacheManager.getContainer().getHealth().getCacheHealth();
            for (CacheHealth cacheHealth : cacheHealthList) {
                if (!cacheHealth.getStatus().equals(HealthStatus.HEALTHY)) {
                    String msg = String.format("La cache no se encuentra saludable. cacheHealth=%s", cacheHealth.getStatus().name());
                    LOGGER.warn(msg);
                    return false;
                }
            }

            ClusterHealth clusterHealth = this.tsCacheManager.getContainer().getHealth().getClusterHealth();
            HealthStatus clusterHealthStatus = clusterHealth.getHealthStatus();
            if (!clusterHealthStatus.equals(HealthStatus.HEALTHY)) {
                String msg = String.format("El cluster no se encuentra saludable. clusterHealth=%s", clusterHealthStatus.name());
                LOGGER.warn(msg);
                return false;
            }
            int numberOfNodes = clusterHealth.getNumberOfNodes();

            if (numberOfNodes != this.pysPushProperties.getClusterMembers()) {
                String msg = String.format("Cambió la topologia del cluster. NodosVivos=%d, NodosEsperados=%d.", numberOfNodes, this.pysPushProperties.getClusterMembers());
                LOGGER.warn(msg);
                return false;
            }
        }

        return true;
    }

}
