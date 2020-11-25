package uy.gub.agesic.pdi.pys.backoffice.utiles.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextProvider implements ApplicationContextAware {

    private ApplicationContextProvider() {

    }

    public static ApplicationContext getApplicationContext() {
        return PYSApplicationContext.getContext();
    }

    public static <T> T getBean(String name, Class<T> aClass) {
        return PYSApplicationContext.getContext().getBean(name, aClass);
    }

    @SuppressWarnings("squid:RedundantThrowsDeclarationCheck")
    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        PYSApplicationContext.setContext(ctx);
    }
}