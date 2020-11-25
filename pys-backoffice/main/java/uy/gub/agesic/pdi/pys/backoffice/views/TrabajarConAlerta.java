package uy.gub.agesic.pdi.pys.backoffice.views;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.views.forms.TrabajarConAlertaForm;

public class TrabajarConAlerta extends PaginaBase {

    private TrabajarConAlertaForm form;

    public TrabajarConAlerta() {
        this.initPage();
    }

    public TrabajarConAlerta(final PageParameters parameters) {
        super(parameters);
        this.initPage();
        this.form.setParameters(parameters);
    }

    private void initPage() {
        this.form = new TrabajarConAlertaForm();
        this.add(this.form);
        this.form.initForm();
        this.setPageTitle("AGESIC - Backoffice de P&S - Trabajar con Alerta");
    }

    @Override
    public BackofficeForm getCurrentForm() {
        return this.form;
    }

}
