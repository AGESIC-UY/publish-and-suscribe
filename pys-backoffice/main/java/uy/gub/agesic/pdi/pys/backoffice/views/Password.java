package uy.gub.agesic.pdi.pys.backoffice.views;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.views.forms.PasswordForm;

public class Password extends PaginaBase {

    private static final long serialVersionUID = 1L;

    private PasswordForm form;

    public Password() {
        this.initPage();
    }

    public Password(final PageParameters parameters) {
        super(parameters);

        this.initPage();
        this.form.setParameters(parameters);
    }

    private void initPage() {
        this.form = new PasswordForm();
        this.add(this.form);
        this.form.initForm();

        this.setPageTitle("AGESIC - Backoffice de la PDI - Password");
    }

    @Override
    public BackofficeForm getCurrentForm() {
        return this.form;
    }

}