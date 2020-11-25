package uy.gub.agesic.pdi.pys.push.controller;

import org.slf4j.MDC;

import java.util.UUID;

public class ExecuteLogic {

    private static final String DURATION = "duration";

    private ExecuteLogic() {
    }

    public static void initLog(String hostName, String topico, String suscriptor) {
        MDC.clear();
        MDC.put(DURATION, "0");
        MDC.put("host", hostName);

        String transactionId = "uuid:" + UUID.randomUUID();
        MDC.put("transactionId", transactionId);
        MDC.put("topic", topico);
        MDC.put("suscriptor", suscriptor);

    }

    public static void endLog(long start) {
        String mdcMsg = String.format("%s", System.currentTimeMillis() - start);
        MDC.put(DURATION, mdcMsg);
    }

}
