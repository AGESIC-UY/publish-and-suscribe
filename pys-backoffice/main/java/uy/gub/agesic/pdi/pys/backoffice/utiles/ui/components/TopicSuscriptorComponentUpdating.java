package uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.DropDownChoice;
import uy.gub.agesic.pdi.pys.domain.Suscriptor;

import java.io.Serializable;

public interface TopicSuscriptorComponentUpdating extends Serializable {

    void onUpdate(AjaxRequestTarget target, DropDownChoice<Suscriptor> component);

}
