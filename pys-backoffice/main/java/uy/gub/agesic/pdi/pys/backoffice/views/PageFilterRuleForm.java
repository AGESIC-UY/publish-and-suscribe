package uy.gub.agesic.pdi.pys.backoffice.views;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.views.forms.FilterRuleEntityForm;

public class PageFilterRuleForm extends PaginaBase {
    private static final long serialVersionUID = 1L;

    private FilterRuleEntityForm form;

    public PageFilterRuleForm() {
        this.initPage();
    }

    public PageFilterRuleForm(final PageParameters parameters) {
        super(parameters);

        this.initPage();
        this.form.setParameters(parameters);
    }

    private void initPage() {
        this.form = new FilterRuleEntityForm();
        this.add(this.form);
        this.form.initForm();

        this.setPageTitle("AGESIC - Backoffice de P&S - Reglas del filtro");
    }

    @Override
    public BackofficeForm getCurrentForm() {
        return this.form;
    }
}
