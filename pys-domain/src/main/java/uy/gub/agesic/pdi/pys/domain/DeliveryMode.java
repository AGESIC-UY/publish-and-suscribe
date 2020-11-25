package uy.gub.agesic.pdi.pys.domain;

public class DeliveryMode {

    public static final String PULL = "PULL";

    public static final String PUSH = "PUSH";

    private DeliveryMode() {
        throw new IllegalStateException("Utility class");
    }
}
