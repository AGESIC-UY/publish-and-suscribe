package uy.gub.agesic.pdi.pys.backoffice.views;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.views.forms.ErrorForm;

public class Error extends PaginaBase {

	private static final long serialVersionUID = 1L;

	private ErrorForm form;

    public Error() {
    	this.initPage();
    }

    public Error(final PageParameters parameters) {
    	super(parameters);

    	this.initPage();
    	this.form.setParameters(parameters);
    }

    private void initPage() {
    	this.form = new ErrorForm();
    	this.add(this.form);
    	this.form.initForm();
    	this.setPageTitle("AGESIC - Backoffice de P&S - error no esperado");
    	this.setErrorPage(true);
        
        // Generamos el error en este punto
        this.form.showError("Ha ocurrido un error no esperado");
    }

	@Override
	public BackofficeForm getCurrentForm() {
		return this.form;
	}

}