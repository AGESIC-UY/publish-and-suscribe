package uy.gub.agesic.pdi.pys.backoffice.views;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.views.forms.AddSuscriptorForm;

public class AddSuscriptor extends PaginaBase {

    private static final long serialVersionUID = 1L;

    private AddSuscriptorForm form;

    public AddSuscriptor() {
        this.initPage();
    }

    public AddSuscriptor(final PageParameters parameters) {
        super(parameters);

        this.initPage();
        this.form.setParameters(parameters);
    }

    private void initPage() {
        this.form = new AddSuscriptorForm();
        this.add(this.form);
        this.form.initForm();

        this.setPageTitle("AGESIC - Backoffice de P&S - Suscriptores del tópico");
    }

    @Override
    public BackofficeForm getCurrentForm() {
        return this.form;
    }

}
