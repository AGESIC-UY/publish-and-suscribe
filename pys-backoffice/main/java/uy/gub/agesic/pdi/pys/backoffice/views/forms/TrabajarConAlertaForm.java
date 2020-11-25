package uy.gub.agesic.pdi.pys.backoffice.views.forms;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.StringValidator;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.backoffice.utiles.enumerados.ModoOperacion;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.ComponentCustomFeedbackPanel;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.LinkVolver;
import uy.gub.agesic.pdi.pys.backoffice.views.Alertas;
import uy.gub.agesic.pdi.pys.common.util.Constants;
import uy.gub.agesic.pdi.pys.domain.Alerta;

import java.text.SimpleDateFormat;
import java.util.Date;

@java.lang.SuppressWarnings({"squid:S1068", "squid:S1450"})
public class TrabajarConAlertaForm extends BackofficeForm {

    // Atributos de la novedad
    private String idAlerta;
    private String uuid;
    private String nombreProductor;
    private String nombreTopico;
    private String nombreSuscriptor;
    private String descripcion;
    private String error;
    private String fechaCreacion;

    public TrabajarConAlertaForm() {
        super("trabajarConAlertaForm");
    }

    @Override
    public void initForm() {
        this.setDefaultModel(new CompoundPropertyModel<>(this));

        final FormComponent<Date> fechaCreacionCmp = new DateTextField("fechaCreacion").setRequired(false);
        this.add(new ComponentCustomFeedbackPanel("fechaCreacionFeedback", new ComponentFeedbackMessageFilter(fechaCreacionCmp)));
        fechaCreacionCmp.setLabel(new Model("Fecha de creación"));
        fechaCreacionCmp.add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
        this.add(fechaCreacionCmp);

        final FormComponent<String> uuidField = new TextField<String>("uuid").setRequired(true);
        uuidField.add(StringValidator.maximumLength(200));
        this.add(new ComponentCustomFeedbackPanel("uuidFeedback", new ComponentFeedbackMessageFilter(uuidField)));
        uuidField.setLabel(new Model("Id Novedad"));
        this.add(uuidField);

        final FormComponent<String> errorField = new TextField<String>("error").setRequired(true);
        errorField.add(StringValidator.maximumLength(200));
        this.add(new ComponentCustomFeedbackPanel("errorFeedback", new ComponentFeedbackMessageFilter(errorField)));
        errorField.setLabel(new Model("Error"));
        this.add(errorField);

        this.add(createComponent("nombreProductor", "nombreProductorFeedback", "Productor"));

        this.add(createComponent("nombreTopico", "nombreTopicoFeedback", "Tópico"));

        this.add(createComponent("nombreSuscriptor", "nombreSuscriptorFeedback", "Suscriptor"));

        final FormComponent<String> descripcionCmp = new TextArea<String>("descripcion").setRequired(true);
        this.add(new ComponentCustomFeedbackPanel("descripcionFeedback", new ComponentFeedbackMessageFilter(descripcionCmp)));
        descripcionCmp.setLabel(new Model("Descripcion"));
        this.add(descripcionCmp);

        // Se agrega el link de volver
        this.add(new LinkVolver<>(Alertas.class));

    }

    @Override
    public void setParametersInner(PageParameters parameters) {
        // Se cargan los datos del filtro de la pagina anterior en el link de volver, por si se quiere volver al mismo lugar de donde se vino
        ((LinkVolver) this.get(LINK_VOLVER_NAME)).setParametersCallback(this.buscarParametrosDeFiltro(parameters));

        if (parameters.getNamedKeys().contains(MODE_PARAM_NAME)) {
            this.modo = parameters.get(MODE_PARAM_NAME).toEnum(ModoOperacion.class);
        }
        if (parameters.getNamedKeys().contains("idAlertaParameter")) {
            this.idAlerta = parameters.get("idAlertaParameter").toString();
        }

        this.definirValoresIniciales();

        this.get("uuid").add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
        this.get("nombreProductor").add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
        this.get("nombreTopico").add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
        this.get("nombreSuscriptor").add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
        this.get("fechaCreacion").add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
        this.get("descripcion").add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
        this.get("error").add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
    }

    private void definirValoresIniciales() {
        try {
            Alerta alerta = obtenerAlertaService().buscarAlerta(this.idAlerta);
            this.idAlerta = alerta.getId();
            this.uuid = alerta.getUuid();
            this.nombreProductor = alerta.getProductor().getNombre();
            this.nombreTopico = alerta.getTopico().getNombre();
            this.nombreSuscriptor = alerta.getSuscriptor().getNombre();
            this.error = alerta.getError();

            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.PATRON_FECHA_HORA);
            this.fechaCreacion  = dateFormat.format(alerta.getFecha());
            this.descripcion = alerta.getDescripcion();

        } catch(PSException ex) {
            this.showError(ex);
        }

    }

}
