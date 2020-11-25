package uy.gub.agesic.pdi.pys.backoffice.views;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.views.forms.TrabajarConNovedadForm;

public class TrabajarConNovedad extends PaginaBase {


    private TrabajarConNovedadForm form;

    public TrabajarConNovedad() {
        this.initPage();
    }

    public TrabajarConNovedad(final PageParameters parameters) {
        super(parameters);
        this.initPage();
        this.form.setParameters(parameters);
    }

    private void initPage() {
        this.form = new TrabajarConNovedadForm();
        this.add(this.form);
        this.form.initForm();
        this.setPageTitle("AGESIC - Backoffice de P&S - Trabajar con Novedad");
    }

    @Override
    public BackofficeForm getCurrentForm() {
        return this.form;
    }

}
