package uy.gub.agesic.pdi.pys.backoffice.views;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.views.forms.SuscriptoresForm;

public class Suscriptores extends PaginaBase  {

    private static final long serialVersionUID = 1L;

    private SuscriptoresForm form;

    public Suscriptores() {
        this.initPage();
    }

    public Suscriptores(final PageParameters parameters) {
        super(parameters);

        this.initPage();
        this.form.setParameters(parameters);
    }

    private void initPage() {
        this.form = new SuscriptoresForm();
        this.add(this.form);
        this.form.initForm();

        this.setPageTitle("AGESIC - Backoffice de P&S - Suscriptores");
    }

    @Override
    public BackofficeForm getCurrentForm() {
        return this.form;
    }
}
