package uy.gub.agesic.pdi.pys.backoffice.views;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.views.forms.TrabajarConSuscriptorForm;

public class TrabajarConSuscriptor extends PaginaBase {

    private TrabajarConSuscriptorForm form;

    public TrabajarConSuscriptor() {
        this.initPage();
    }

    public TrabajarConSuscriptor(final PageParameters parameters) {
        super(parameters);
        this.initPage();
        this.form.setParameters(parameters);
    }

    private void initPage() {
        this.form = new TrabajarConSuscriptorForm();
        this.add(this.form);
        this.form.initForm();
        this.setPageTitle("AGESIC - Backoffice de P&S - Trabajar con Suscriptor");
    }

    @Override
    public BackofficeForm getCurrentForm() {
        return this.form;
    }
}
