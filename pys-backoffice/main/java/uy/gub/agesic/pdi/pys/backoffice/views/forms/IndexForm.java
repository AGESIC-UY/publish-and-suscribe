package uy.gub.agesic.pdi.pys.backoffice.views.forms;

import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;

public class IndexForm extends BackofficeForm {

	private static final long serialVersionUID = 1L;

	public IndexForm() { 
		super("indexForm"); 
	}
	
	@Override
	public void initForm() {
		this.setDefaultModel(new CompoundPropertyModel<IndexForm>(this));
	}

	@Override
	public void setParametersInner(PageParameters parameters) {
	    //Never used
	}

}

