package uy.gub.agesic.pdi.pys.push.send;

import org.infinispan.AdvancedCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uy.gub.agesic.pdi.common.exceptions.PDIException;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.push.cache.TSCacheManager;
import uy.gub.agesic.pdi.pys.push.exceptions.SleepTimeException;

import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

public class AccessControl {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessControl.class);

    private PushHAController haController;

    private TSCacheManager tsCacheManager;

    private AdvancedCache<String,String> lockCache;

    private TransactionManager txMgr;

    private String tsName;

    public void init() {
        this.lockCache = this.tsCacheManager.getLockCache();
        txMgr = this.lockCache.getTransactionManager();

        try {
            txMgr.begin();
            // Cargamos valores en el cache, que pueden llegar a estar porque otro los cargo
            this.lockCache.putIfAbsent(this.tsName, this.tsName);

            txMgr.commit();
        } catch (Throwable e1) {
            LOGGER.error("Ha ocurrido un error al inicializar la cache para TS:" + tsName, e1);
            try {
                txMgr.rollback();
            } catch (SystemException e2) {
                LOGGER.error("Ha ocurrido un error cerrando la transaccion actual para TS:" + tsName, e2);
            }
        }

    }

    public void setTSCacheManager(TSCacheManager tsCacheManager) {
        this.tsCacheManager = tsCacheManager;
    }

    public void setTsName(String tsName) {
        this.tsName = tsName;
    }

    public void setHaController(PushHAController haController) {
        this.haController = haController;
    }

    public boolean accessResource(BusinessLogicExecutor logic) {
        try {
            txMgr.begin();

            // Bloqueamos el acceso a la entrega que nos interesa
            this.lockCache.put(this.tsName, this.tsName);

            if (!this.haController.grantPushAccess()) {
                String msg = "La cantidad de nodos en el cluster no es suficiente para procesar pedidos PUSH";
                throw new PDIException(msg);
            }

            logic.executeLogic();

            // Se liberan los bloqueos aca
            txMgr.commit();

            return true;
        } catch (SleepTimeException | PSException e) {
            LOGGER.trace(e.getMessage(), e);
            rollback();
        } catch (Throwable e1) {
            String msg = String.format("Se capturó una exception al procesar el TS: %s. Se libera la memoria compartida", tsName);
            LOGGER.warn(msg, e1);
            rollback();
        }
        return false;
    }

    private void rollback() {
        try {
            txMgr.rollback();
        } catch (SystemException e2) {
            LOGGER.error("Ha ocurrido un error cerrando la transaccion actual para TS:" + tsName, e2);
        }
    }

}