package uy.gub.agesic.pdi.pys.push.cache;

import org.infinispan.AdvancedCache;
import org.infinispan.Cache;
import org.infinispan.context.Flag;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TSCacheManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(TSCacheManager.class);

    private static final String CACHE_NAME = "lock-cache";

    // Container infinispan de los diferentes caches del sistema
    private EmbeddedCacheManager container;

    // Caches
    private AdvancedCache<String, String> lockCache;

    public void init() {
        try {
            container = new DefaultCacheManager(TSCacheManager.class.getResourceAsStream("/infinispan/pdi-infinispan.xml"));

            String msg = String.format("Comienza inicializacion del cache manager. Datos del cluster: %s - %s", container.getClusterName(), container.getAddress().toString());
            LOGGER.info(msg);

            // Forzamos la generacion de los caches. Utilizamos advanced caches para alterar el comportamiento de los metodos
            this.lockCache = this.getAdvancedCache(container.getCache(CACHE_NAME));

            msg = String.format("Finaliza inicializacion del cache manager. Datos del cluster: %s - %s", container.getClusterName(), container.getAddress().toString());
            LOGGER.info(msg);

        } catch (Exception ex) {
            LOGGER.error("Error inicializando el cache manager de infinispan", ex);
        }
    }

    /**
     * Construimos un cache avanzado para cada cache usado en el datagrid
     * Alteramos el comportamiento del cache para mejorar performace y generar estadisticas entre otros
     */
    private <K,V> AdvancedCache<K,V> getAdvancedCache(Cache<K,V> cache) {
        AdvancedCache<K, V> acache = cache.getAdvancedCache();

        // Habilitamos estadisticas
        acache.getStats().setStatisticsEnabled(true);

        // Flags de control del cache
        acache = acache.withFlags(Flag.IGNORE_RETURN_VALUES, Flag.FORCE_SYNCHRONOUS, Flag.FORCE_WRITE_LOCK);

        return acache;
    }

    public AdvancedCache<String, String> getLockCache() {
        return this.lockCache;
    }

    public EmbeddedCacheManager getContainer() {
        return container;
    }

}
