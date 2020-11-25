package uy.gub.agesic.pdi.pys.backoffice.config;

import com.giffing.wicket.spring.boot.starter.app.WicketBootSecuredWebApplication;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.core.util.crypt.KeyInSessionSunJceCryptFactory;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uy.gub.agesic.pdi.pys.backoffice.services.UserService;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.seguridad.BackofficeCryptoMapper;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.sesion.BackofficeAuthenticationSession;
import uy.gub.agesic.pdi.pys.backoffice.views.*;
import uy.gub.agesic.pdi.pys.backoffice.views.Error;

@Component
public class WicketBackofficeApplication extends WicketBootSecuredWebApplication {

    private UserService usuarioService;

    @Autowired
    public WicketBackofficeApplication(UserService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    protected void init() {
        super.init();

        // Montamos las paginas de la solucion
        this.mountPage("/index", Index.class);
        this.mountPage("/login", Login.class);
        this.mountPage("/usuarios", Usuarios.class);
        this.mountPage("/password", Password.class);
        this.mountPage("/productores", Productores.class);
        this.mountPage("/suscriptores", Suscriptores.class);
        this.mountPage("/topicos", Topicos.class);
        this.mountPage("/novedades", Novedades.class);
        this.mountPage("/alertas", Alertas.class);
        this.mountPage("/entregas", Entregas.class);
        this.mountPage("/colecciones", Colecciones.class);
        this.mountPage("/filters", PageFilterList.class);
        this.mountPage("/buildxpath", PageBuilderXpath.class);
        // Montamos las paginas especiales
        this.mountPage("/error", Error.class);

        // Configuramos el encriptador en la aplicacion
        getSecuritySettings().setCryptFactory(new KeyInSessionSunJceCryptFactory());

        // Mandamos encriptar las URLs
        this.setRootRequestMapper(new BackofficeCryptoMapper(getRootRequestMapper(), this, LoggerFactory.getLogger(WicketBackofficeApplication.class)));
    }

    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
        return BackofficeAuthenticationSession.class;
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return Login.class;
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return Index.class;
    }

    @Override
    public Session newSession(final Request request, final Response response)  {
        // Creamos la sesion explicitamente para setearle los objetos necesarios para autenticar usuarios
        return new BackofficeAuthenticationSession(request, this.usuarioService);
    }
}