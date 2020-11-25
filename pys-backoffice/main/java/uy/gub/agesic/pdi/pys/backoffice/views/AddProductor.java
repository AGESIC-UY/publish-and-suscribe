package uy.gub.agesic.pdi.pys.backoffice.views;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.views.forms.AddProductorForm;

public class AddProductor extends PaginaBase {

    private static final long serialVersionUID = 1L;

    private AddProductorForm form;

    public AddProductor() {
        this.initPage();
    }

    public AddProductor(final PageParameters parameters) {
        super(parameters);

        this.initPage();
        this.form.setParameters(parameters);
    }

    private void initPage() {
        this.form = new AddProductorForm();
        this.add(this.form);
        this.form.initForm();

        this.setPageTitle("AGESIC - Backoffice de P&S - Productores del tópico");
    }

    @Override
    public BackofficeForm getCurrentForm() {
        return this.form;
    }


}
