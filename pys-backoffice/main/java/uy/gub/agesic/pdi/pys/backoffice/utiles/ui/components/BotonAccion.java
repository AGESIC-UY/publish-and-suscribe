package uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;

@SuppressWarnings({"rawtypes","unchecked"})
public abstract class BotonAccion extends Button {

    private static final String ON_CLICK = "onclick";
	
	private static final long serialVersionUID = 1L;

	private Class paginaPermisosInsuficientes;

    public BotonAccion(String id) {
        super(id);
    }

    public BotonAccion(String id, Class paginaPermisosInsuficientes) {
        super(id);
        this.paginaPermisosInsuficientes = paginaPermisosInsuficientes;
    }

    public BotonAccion(String id, boolean requiereConfirmacion, String mensajeConfirmacion) {
        super(id);

        if (requiereConfirmacion) {
            mensajeConfirmacion = StringEscapeUtils.unescapeHtml(mensajeConfirmacion);
            this.add(new AttributeModifier(ON_CLICK, "return confirm('" + mensajeConfirmacion + "');"));
        }
    }

    public BotonAccion(String id, boolean requiereConfirmacion, String mensajeConfirmacion1, String mensajeConfirmacion2) {
        super(id);

        if (requiereConfirmacion) {
            mensajeConfirmacion1 = StringEscapeUtils.unescapeHtml(mensajeConfirmacion1);
            mensajeConfirmacion2 = StringEscapeUtils.unescapeHtml(mensajeConfirmacion2);

            this.add(new AttributeModifier(ON_CLICK, "if (confirm('" + mensajeConfirmacion1 + "')) { return confirm('" + mensajeConfirmacion2 + "'); } else { return false; };"));
        }
    }

    public BotonAccion(String id, Class paginaPermisosInsuficientes, boolean requiereConfirmacion, String mensajeConfirmacion) {
        super(id);
        
        this.paginaPermisosInsuficientes = paginaPermisosInsuficientes;
        
        if (requiereConfirmacion) {
            mensajeConfirmacion = StringEscapeUtils.unescapeHtml(mensajeConfirmacion);
            this.add(new AttributeModifier(ON_CLICK, "return confirm('" + mensajeConfirmacion + "');"));
        }
    }

    public BotonAccion(String id, Class paginaPermisosInsuficientes, boolean requiereConfirmacion, String mensajeConfirmacion1, String mensajeConfirmacion2) {
        super(id);
        
        this.paginaPermisosInsuficientes = paginaPermisosInsuficientes;
        
        if (requiereConfirmacion) {
            mensajeConfirmacion1 = StringEscapeUtils.unescapeHtml(mensajeConfirmacion1);
            mensajeConfirmacion2 = StringEscapeUtils.unescapeHtml(mensajeConfirmacion2);
            
            this.add(new AttributeModifier(ON_CLICK, "if (confirm('" + mensajeConfirmacion1 + "')) { return confirm('" + mensajeConfirmacion2 + "'); } else { return false; };"));
        }
    }

	@Override
    public final void onSubmit() {
        if (!this.poseePermisoEjecucion()) {
            PageParameters parameters = new PageParameters();
            parameters.add(BackofficeForm.PARAM_MSJ_ERROR, "No est&aacute; autorizado para acceder a la p&aacute;gina/acci&oacute;n seleccionada");

            throw new RestartResponseException(this.paginaPermisosInsuficientes, parameters);
        }

        this.ejecutar();
    }

    public abstract boolean poseePermisoEjecucion();
    
    public abstract void ejecutar();
}
