package uy.gub.agesic.pdi.pys.backoffice.utiles.spring;

import org.springframework.context.ApplicationContext;

public class PYSApplicationContext {

    private static ApplicationContext context;

    public static ApplicationContext getContext() {
        return context;
    }

    public static void setContext(ApplicationContext context) {
        PYSApplicationContext.context = context;
    }

    private PYSApplicationContext() {

    }

}
