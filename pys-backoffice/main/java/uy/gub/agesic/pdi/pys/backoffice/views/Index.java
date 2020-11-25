package uy.gub.agesic.pdi.pys.backoffice.views;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.views.forms.IndexForm;

public class Index extends PaginaBase {
	 
	private static final long serialVersionUID = 1L;
	
	private IndexForm form;
 
    public Index() {
    	this.initPage();
    }

    public Index(final PageParameters parameters) {
    	super(parameters);

    	this.initPage();
    	this.form.setParameters(parameters);

	}

    private void initPage() {

    	this.form = new IndexForm();
    	this.add(this.form);
    	this.form.initForm();
    	this.setPageTitle("AGESIC - Backoffice P&S");
    }

	@Override
	public BackofficeForm getCurrentForm() {
		return this.form;
	}

}