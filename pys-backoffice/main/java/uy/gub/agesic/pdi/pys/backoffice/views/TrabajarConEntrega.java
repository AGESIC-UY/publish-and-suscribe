package uy.gub.agesic.pdi.pys.backoffice.views;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.views.forms.TrabajarConEntregaForm;

public class TrabajarConEntrega extends PaginaBase {

    private TrabajarConEntregaForm form;

    public TrabajarConEntrega() {
        this.initPage();
    }

    public TrabajarConEntrega(final PageParameters parameters) {
        super(parameters);
        this.initPage();
        this.form.setParameters(parameters);
    }

    private void initPage() {
        this.form = new TrabajarConEntregaForm();
        this.add(this.form);
        this.form.initForm();
        this.setPageTitle("AGESIC - Backoffice de P&S - Trabajar con Entrega");
    }

    @Override
    public BackofficeForm getCurrentForm() {
        return this.form;
    }

}
