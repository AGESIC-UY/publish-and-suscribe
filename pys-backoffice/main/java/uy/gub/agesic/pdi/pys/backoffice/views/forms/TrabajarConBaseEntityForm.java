package uy.gub.agesic.pdi.pys.backoffice.views.forms;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.StringValidator;
import uy.gub.agesic.pdi.pys.backoffice.config.BackofficeProperties;
import uy.gub.agesic.pdi.pys.backoffice.utiles.enumerados.ModoOperacion;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficePage;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.ComponentCustomFeedbackPanel;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.LinkVolver;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SuppressWarnings({"squid:S1068", "squid:S1450"})
public abstract class TrabajarConBaseEntityForm extends BackofficeForm {

    protected String nombre;

    // Propiedades del backoffice
    protected BackofficeProperties properties;

    protected static final List<String> estados = Arrays.asList(HABILITADO_VALUE, DESHABILITADO_VALUE);

    protected String estado = HABILITADO_VALUE;

    public TrabajarConBaseEntityForm(String id) {
        super(id);
    }

    @Override
    public void initForm() {
        this.properties = ((BackofficePage) this.getPage()).getProperties();

        this.setDefaultModel(new CompoundPropertyModel<>(this));

        final FormComponent<String> nombreComponent = new TextField<String>(NOMBRE_NAME).setRequired(true);
        nombreComponent.add(StringValidator.maximumLength(50));
        this.add(new ComponentCustomFeedbackPanel("nombreFeedback", new ComponentFeedbackMessageFilter(nombreComponent)));
        nombreComponent.setLabel(new Model("Nombre"));
        this.add(nombreComponent);

        final FormComponent<String> dnComponent = new TextField<String>(DN_NAME).setRequired(true);
        dnComponent.add(StringValidator.maximumLength(200));
        this.add(new ComponentCustomFeedbackPanel("dnFeedback", new ComponentFeedbackMessageFilter(dnComponent)));
        dnComponent.setLabel(new Model("DN"));
        this.add(dnComponent);

        Label labelHabilitado = new Label(LABEL_HABILITADO, "Estado *");
        labelHabilitado.setOutputMarkupId(true);
        labelHabilitado.setOutputMarkupPlaceholderTag(true);
        this.add(labelHabilitado);

        Label labelFechaCreacion = new Label(LABEL_FECHA_CREACION, "Fecha de creación");
        labelFechaCreacion.setOutputMarkupId(true);
        labelFechaCreacion.setOutputMarkupPlaceholderTag(true);
        labelFechaCreacion.setVisible(false);
        this.add(labelFechaCreacion);

        DropDownChoice<String> habilitadoChoice = new DropDownChoice<>(
                HABILITADO_NAME, new PropertyModel<>(this, "estado"), estados);
        habilitadoChoice.setLabel(new Model<>("Estado"));
        this.add(habilitadoChoice);

        final FormComponent<Date> fechaCreacionComponent = new DateTextField(FECHA_CREACION).setRequired(false);
        this.add(new ComponentCustomFeedbackPanel("fechaCreacionFeedback", new ComponentFeedbackMessageFilter(fechaCreacionComponent)));
        fechaCreacionComponent.setLabel(new Model("Fecha de creación"));
        fechaCreacionComponent.setVisible(false);
        this.add(fechaCreacionComponent);


    }

    @Override
    public void setParametersInner(PageParameters parameters) {
        // Se cargan los datos del filtro de la pagina anterior en el link de volver, por si se quiere volver al mismo lugar de donde se vino
        ((LinkVolver) this.get(LINK_VOLVER_NAME)).setParametersCallback(this.buscarParametrosDeFiltro(parameters));

        if (parameters.getNamedKeys().contains(MODE_PARAM_NAME)) {
            this.modo = parameters.get(MODE_PARAM_NAME).toEnum(ModoOperacion.class);
        }
        if (parameters.getNamedKeys().contains(NOMBRE_PARAMETER)) {
            this.nombre = parameters.get(NOMBRE_PARAMETER).toString();
        }

        if (!this.modo.equals(ModoOperacion.ALTA)) {
            this.definirValoresIniciales();
        }

        // Si el modo es consulta, definir componentes como Readonly
        if (this.modo.equals(ModoOperacion.CONSULTA)) {
            this.get(NOMBRE_NAME).add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
            this.get(DN_NAME).add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
            this.get(FECHA_CREACION).setVisible(true);
            this.get(FECHA_CREACION).add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
            this.get(LABEL_FECHA_CREACION).setVisible(true);
            this.get(HABILITADO_NAME).setEnabled(false);
        }
    }

    protected abstract void definirValoresIniciales();

}
