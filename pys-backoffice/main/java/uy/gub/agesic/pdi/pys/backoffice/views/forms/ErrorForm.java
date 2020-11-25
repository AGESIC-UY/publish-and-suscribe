package uy.gub.agesic.pdi.pys.backoffice.views.forms;

import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;

public class ErrorForm extends BackofficeForm {

	private static final long serialVersionUID = 1L;

	public ErrorForm() {
		super("errorForm");
	}
	
	@Override
	public void initForm() {
		this.setDefaultModel(new CompoundPropertyModel<ErrorForm>(this));
	}

	@Override
	public void setParametersInner(PageParameters parameters) {
	    //Never used
	}

}

