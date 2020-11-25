package uy.gub.agesic.pdi.pys.backoffice.utiles.ui;

import org.apache.wicket.Application;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import uy.gub.agesic.pdi.pys.backoffice.config.BackofficeProperties;
import uy.gub.agesic.pdi.pys.backoffice.utiles.exceptions.BackofficeException;
import uy.gub.agesic.pdi.pys.backoffice.utiles.spring.ApplicationContextProvider;

import javax.servlet.http.HttpSession;

public abstract class BackofficePage extends WebPage {

    private BackofficeProperties properties;

    public BackofficePage() {
        this.errorPage = false;
        this.properties = ApplicationContextProvider.getBean("backofficeProperties", BackofficeProperties.class);
    }

    public BackofficePage(final PageParameters parameters) {
		super(parameters);
    	this.errorPage = false;
        this.properties = ApplicationContextProvider.getBean("backofficeProperties", BackofficeProperties.class);
	}
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Funciones requeridas por las paginas concretas
    
    public abstract BackofficeForm getCurrentForm();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Aspectos de la pagina

    private boolean errorPage;
    
    public void setErrorPage(boolean value) {
    	this.errorPage = value;
    }

    @Override
    public boolean isErrorPage() {
    	return this.errorPage;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Seguridad

    public void logout() throws BackofficeException {
        try {
            AuthenticatedWebSession.get().invalidate();

            HttpSession session = ((ServletWebRequest) RequestCycle.get().getRequest()).getContainerRequest().getSession();
            session.invalidate();

            setResponsePage(getApplication().getHomePage());
        } catch (Exception e) {
            throw new BackofficeException(e);
        }
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        AuthenticatedWebApplication app = (AuthenticatedWebApplication) Application.get();

        // Si el usuario no esta autenticado, lo forzamos a que vaya a la pagina de login
        if (!AuthenticatedWebSession.get().isSignedIn()) {
            app.restartResponseAtSignInPage();
        } else {
            HttpSession session = ((ServletWebRequest)RequestCycle.get().getRequest()).getContainerRequest().getSession();
            session.setMaxInactiveInterval(300);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Utilidades
    public BackofficeProperties getProperties() {
        return this.properties;
    }


}
