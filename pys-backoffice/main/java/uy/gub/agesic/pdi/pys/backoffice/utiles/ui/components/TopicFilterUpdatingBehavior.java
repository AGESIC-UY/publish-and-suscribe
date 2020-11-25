package uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import uy.gub.agesic.pdi.pys.domain.Suscriptor;

public class TopicFilterUpdatingBehavior extends AjaxFormComponentUpdatingBehavior {

    private final DropDownChoice<Suscriptor> suscriptorFiltro;

    private final TopicSuscriptorComponentUpdating componentUpdating;

    public TopicFilterUpdatingBehavior(DropDownChoice<Suscriptor> suscriptorFiltro, TopicSuscriptorComponentUpdating componentUpdating) {
        super("onchange");
        this.suscriptorFiltro = suscriptorFiltro;
        this.componentUpdating = componentUpdating;
    }

    @Override
    protected void onUpdate(AjaxRequestTarget target) {
        componentUpdating.onUpdate(target, suscriptorFiltro);
    }

}

