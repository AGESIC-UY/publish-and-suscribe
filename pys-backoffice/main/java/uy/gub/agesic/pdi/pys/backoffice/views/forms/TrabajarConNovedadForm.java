package uy.gub.agesic.pdi.pys.backoffice.views.forms;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.StringValidator;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.backoffice.utiles.enumerados.ModoOperacion;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.ComponentCustomFeedbackPanel;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.LinkVolver;
import uy.gub.agesic.pdi.pys.backoffice.views.Novedades;
import uy.gub.agesic.pdi.pys.common.util.Constants;
import uy.gub.agesic.pdi.pys.domain.Entrega;
import uy.gub.agesic.pdi.pys.domain.Novedad;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@java.lang.SuppressWarnings({"squid:S1068", "squid:S1450"})
public class TrabajarConNovedadForm extends BackofficeForm {

    // Atributos de la novedad
    private String uuidNovedad;
    private String nombreProductor;
    private String nombreTopico;
    private String xmlContenido;
    private String fechaCreacion;

    private ArrayList<Entrega> listEntregas;

    public TrabajarConNovedadForm() {
        super("trabajarConNovedadForm");
    }

    @Override
    public void initForm() {

        listEntregas = new ArrayList<>();

        this.setDefaultModel(new CompoundPropertyModel<>(this));

        final FormComponent<Date> fechaCreacionCmp = new DateTextField("fechaCreacion").setRequired(false);
        this.add(new ComponentCustomFeedbackPanel("fechaCreacionFeedback", new ComponentFeedbackMessageFilter(fechaCreacionCmp)));
        fechaCreacionCmp.setLabel(new Model("Fecha de creación"));
        fechaCreacionCmp.add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
        this.add(fechaCreacionCmp);

        final FormComponent<String> uuidNovedadCmp = new TextField<String>("uuidNovedad").setRequired(true);
        uuidNovedadCmp.add(StringValidator.maximumLength(200));
        this.add(new ComponentCustomFeedbackPanel("uuidNovedadFeedback", new ComponentFeedbackMessageFilter(uuidNovedadCmp)));
        uuidNovedadCmp.setLabel(new Model("uuid Novedad"));
        this.add(uuidNovedadCmp);

        this.add(createComponent("nombreProductor", "nombreProductorFeedback", "Productor"));

        this.add(createComponent("nombreTopico", "nombreTopicoFeedback", "Tópico"));

        final FormComponent<String> xmlContenidoCmp = new TextArea<String>("xmlContenido").setRequired(true);
        this.add(new ComponentCustomFeedbackPanel("xmlContenidoFeedback", new ComponentFeedbackMessageFilter(xmlContenidoCmp)));
        xmlContenidoCmp.setLabel(new Model("XML Contenido"));
        this.add(xmlContenidoCmp);

        // Se agrega el link de volver
        this.add(new LinkVolver<>(Novedades.class));

        EntregasDataView dataViewEnt = new EntregasDataView ("rowsEnt", new ListDataProvider<>(this.listEntregas));
        this.add(dataViewEnt);

    }

    @Override
    public void setParametersInner(PageParameters parameters) {
        // Se cargan los datos del filtro de la pagina anterior en el link de volver, por si se quiere volver al mismo lugar de donde se vino
        ((LinkVolver) this.get(LINK_VOLVER_NAME)).setParametersCallback(this.buscarParametrosDeFiltro(parameters));

        if (parameters.getNamedKeys().contains(MODE_PARAM_NAME)) {
            this.modo = parameters.get(MODE_PARAM_NAME).toEnum(ModoOperacion.class);
        }
        if (parameters.getNamedKeys().contains(UUID_NOVEDAD_PARAMETER)) {
            this.uuidNovedad = parameters.get(UUID_NOVEDAD_PARAMETER).toString();
        }

        if (parameters.getNamedKeys().contains(NOMBRE_TOPICO_PARAM_NAME)) {
            this.nombreTopico = parameters.get(NOMBRE_TOPICO_PARAM_NAME).toString();
        }

        if (!this.modo.equals(ModoOperacion.ALTA)) {
            this.definirValoresIniciales();
        }

        // Si el modo es consulta, definir componentes como Readonly
        if (this.modo.equals(ModoOperacion.CONSULTA)) {
            this.get("uuidNovedad").add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
            this.get("nombreProductor").add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
            this.get("nombreTopico").add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
            this.get("fechaCreacion").add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
            this.get("xmlContenido").add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
        }
    }

    private void definirValoresIniciales() {
        try {
            Novedad novedad = obtenerNovedadService().buscarNovedad(this.uuidNovedad) ;
            this.uuidNovedad = novedad.getUuid();
            this.nombreProductor = novedad.getProductor().getNombre();
            this.nombreTopico = novedad.getTopico().getNombre();

            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.PATRON_FECHA_HORA);
            this.fechaCreacion  = dateFormat.format(novedad.getFecha());
            this.xmlContenido = novedad.getContenido();

            buscarEntregas();

        } catch(PSException ex) {
            this.showError(ex);
        }

    }

    private void buscarEntregas() {
        try {
            String uuidNovedadStr = RequestCycle.get().getRequest().getRequestParameters().getParameterValue(UUID_NOVEDAD_PARAMETER).toString();

            if (uuidNovedadStr != null) {
                List<Entrega> entregas = obtenerEntregaService().buscarEntregasNovedad(uuidNovedadStr);
                    this.listEntregas.addAll(entregas);
            }

        } catch (PSException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private class EntregasDataView extends DataView<Entrega> {

        public EntregasDataView(String id, ListDataProvider<Entrega> dataProvider) {
            super(id, dataProvider);
        }

        @SuppressWarnings("rawtypes")
        @Override
        protected void populateItem(Item<Entrega> item) {
            final Entrega info = item.getModelObject();

            RepeatingView repeatingView = new RepeatingView("dataRowEnt");
            repeatingView.add(new Label(repeatingView.newChildId(), info.getSuscriptor().getNombre()));
            repeatingView.add(new Label(repeatingView.newChildId(), info.getEstado()));
            item.add(repeatingView);
        }
    }

}
