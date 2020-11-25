package uy.gub.agesic.pdi.pys.backoffice.views.forms;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.StringValidator;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.backoffice.utiles.enumerados.ModoOperacion;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.ComponentCustomFeedbackPanel;
import uy.gub.agesic.pdi.pys.backoffice.views.Entregas;
import uy.gub.agesic.pdi.pys.common.util.Constants;
import uy.gub.agesic.pdi.pys.domain.Entrega;

import java.text.SimpleDateFormat;
import java.util.Date;

@java.lang.SuppressWarnings({"squid:S1450", "squid:S1068"})
public class TrabajarConEntregaForm extends BackofficeForm {

    private static final String CANCEL_BUTTON_ID = "cancelar";

    private static final String RESEND_BUTTON_ID = "reenviar";

    private static final String TOPICO_PARAMETER = "TopicoParameter";

    private static final String ID_ENTREGA_PARAMETER = "idEntregaParameter";

    private static final String SUSCRIPTOR_PARAMETER = "SuscriptorParameter";

    // Atributos de la novedad
    private String idEntrega;
    private String uuid;
    private String estado;
    private String tipoEntrega;
    private String nombreProductor;
    private String nombreTopico;
    private String nombreSuscriptor;
    private String fechaCreacion;
    private String fechaEnvio;
    private String xmlContenido;
    private String reason;

    public TrabajarConEntregaForm() {
        super("trabajarConEntregaForm");
    }

    @Override
    public void initForm() {
        this.setDefaultModel(new CompoundPropertyModel<>(this));

        final FormComponent<Date> fechaCreacionCmp = new TextField("fechaCreacion").setRequired(false);
        this.add(new ComponentCustomFeedbackPanel("fechaCreacionFeedback", new ComponentFeedbackMessageFilter(fechaCreacionCmp)));
        fechaCreacionCmp.setLabel(new Model("Fecha de creación"));
        fechaCreacionCmp.add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
        this.add(fechaCreacionCmp);

        final FormComponent<Date> fechaEnvioCmp = new TextField("fechaEnvio").setRequired(false);
        this.add(new ComponentCustomFeedbackPanel("fechaEnvioFeedback", new ComponentFeedbackMessageFilter(fechaEnvioCmp)));
        fechaEnvioCmp.setLabel(new Model("Fecha de envío"));
        fechaEnvioCmp.add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
        this.add(fechaEnvioCmp);

        final FormComponent<String> uuidField = new TextField<String>("uuid").setRequired(false);
        uuidField.add(StringValidator.maximumLength(200));
        this.add(new ComponentCustomFeedbackPanel("uuidFeedback", new ComponentFeedbackMessageFilter(uuidField)));
        uuidField.setLabel(new Model("Id Novedad"));
        this.add(uuidField);

        this.add(createComponent("nombreProductor", "nombreProductorFeedback", "Productor"));

        this.add(createComponent("nombreTopico", "nombreTopicoFeedback", "Tópico"));

        this.add(createComponent("nombreSuscriptor", "nombreSuscriptorFeedback", "Suscriptor"));

        final FormComponent<String> estadoField = new TextField<String>("estado").setRequired(false);
        estadoField.add(StringValidator.maximumLength(200));
        this.add(new ComponentCustomFeedbackPanel("estadoFeedback", new ComponentFeedbackMessageFilter(estadoField)));
        estadoField.setLabel(new Model("Estado"));
        this.add(estadoField);

        final FormComponent<String> tipoEntregaField = new TextField<String>("tipoEntrega").setRequired(false);
        tipoEntregaField.add(StringValidator.maximumLength(200));
        this.add(new ComponentCustomFeedbackPanel("tipoEntregaFeedback", new ComponentFeedbackMessageFilter(tipoEntregaField)));
        tipoEntregaField.setLabel(new Model("Tipo de Entrega"));
        this.add(tipoEntregaField);

        final FormComponent<String> reasonField = new TextField<String>("reason").setRequired(false);
        reasonField.add(StringValidator.maximumLength(200));
        this.add(new ComponentCustomFeedbackPanel("reasonFeedback", new ComponentFeedbackMessageFilter(reasonField)));
        reasonField.setLabel(new Model("Motivo"));
        this.add(reasonField);

        final FormComponent<String> xmlContenidoField = new TextArea<String>("xmlContenido").setRequired(false);
        this.add(new ComponentCustomFeedbackPanel("xmlContenidoFeedback", new ComponentFeedbackMessageFilter(xmlContenidoField)));
        xmlContenidoField.setLabel(new Model("Contenido"));
        this.add(xmlContenidoField);

        createEjecutarBoton(CANCEL_BUTTON_ID);
        createEjecutarBoton(RESEND_BUTTON_ID);

        // Se agrega el link de volver
        this.add(new TrabajarConEntregaForm.LinkVolver());

    }

    @Override
    public void setParametersInner(PageParameters parameters) {
        // Se cargan los datos del filtro de la pagina anterior en el link de volver, por si se quiere volver al mismo lugar de donde se vino
        ((LinkVolver) this.get(LINK_VOLVER_NAME)).setParametersCallback(this.buscarParametrosDeFiltro(parameters));

        if (parameters.getNamedKeys().contains(MODE_PARAM_NAME)) {
            this.modo = parameters.get(MODE_PARAM_NAME).toEnum(ModoOperacion.class);
        }

        if (parameters.getNamedKeys().contains(ID_ENTREGA_PARAMETER)) {
            this.idEntrega = parameters.get(ID_ENTREGA_PARAMETER).toString();
        }

        if (parameters.getNamedKeys().contains(TOPICO_PARAMETER)) {
            this.nombreTopico = parameters.get(TOPICO_PARAMETER).toString();
        }

        if (parameters.getNamedKeys().contains(SUSCRIPTOR_PARAMETER)) {
            this.nombreSuscriptor = parameters.get(SUSCRIPTOR_PARAMETER).toString();
        }

        EjecutarButton ejButton = (EjecutarButton) this.get(CANCEL_BUTTON_ID);
        ejButton.setParametersCallback(this.buscarParametrosDeFiltro(parameters));

        ejButton = (EjecutarButton) this.get(RESEND_BUTTON_ID);
        ejButton.setParametersCallback(this.buscarParametrosDeFiltro(parameters));

        this.definirValoresIniciales();

        this.get("uuid").add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
        this.get("estado").add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
        this.get("nombreProductor").add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
        this.get("nombreTopico").add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
        this.get("nombreSuscriptor").add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
        this.get("fechaCreacion").add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
        this.get("fechaEnvio").add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
        this.get("tipoEntrega").add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
        this.get("xmlContenido").add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
        this.get("reason").add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));

    }

    private void createEjecutarBoton(String id) {
        EjecutarButton ejButon = new TrabajarConEntregaForm.EjecutarButton(id);
        ejButon.setOutputMarkupId(true);
        ejButon.setOutputMarkupPlaceholderTag(true);

        this.add(ejButon);
    }

    private class EjecutarButton extends Button {

        private PageParameters parametersCallback;

        public EjecutarButton(String id) {
            super(id);
        }

        @Override
        public boolean isVisible() {
            return (getId().equals(CANCEL_BUTTON_ID) && TrabajarConEntregaForm.this.modo.equals(ModoOperacion.CANCELAR)) ||
                    (getId().equals(RESEND_BUTTON_ID) && TrabajarConEntregaForm.this.modo.equals(ModoOperacion.REENVIAR));
        }

        @Override
        public void onSubmit() {
            boolean ok = false;
            if (TrabajarConEntregaForm.this.modo.equals(ModoOperacion.CANCELAR)) {
                try {
                    obtenerEntregaService().cancelar(idEntrega, nombreSuscriptor, nombreTopico);
                    ok = true;
                } catch (PSException e) {
                    String msg = e.getMessage();
                    logger.error(msg, e);
                    showError(msg);
                }
            } else if (TrabajarConEntregaForm.this.modo.equals(ModoOperacion.REENVIAR)) {
                try {
                    if (!obtenerTopicoService().buscarTopico(nombreTopico).getHabilitado()) {
                        showError("Entrega.reenviar.topico.deshabilitado");
                    } else if (!obtenerSuscriptorService().buscarSuscriptor(nombreSuscriptor).getHabilitado()) {
                        showError("Entrega.reenviar.suscriptor.deshabilitado");
                    } else {
                        obtenerEntregaService().reenviar(idEntrega, nombreSuscriptor, nombreTopico);
                        ok = true;
                    }

                } catch (RuntimeException ex) {
                    processExceptions(ex);
                }
                catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    showError(e.getMessage());
                }
            }
            if (ok) {
                setResponsePage(Entregas.class, parametersCallback);
            }
        }

        private void processExceptions(Throwable ex) {
            logger.error(ex.getMessage(), ex);
            if (ex.getMessage().contains("ClientException")) {
                showError("Entrega.reenviar.excepcion.servicio");
            } else if (ex.getMessage().contains("Read timed out")) {
                showError("Entrega.reenviar.excepcion.timeout");
            } else {
                if (ex.getCause() != null) {
                    processExceptions(ex.getCause());
                } else {
                    showError(ex.getMessage());
                }
            }
        }

        public void setParametersCallback(PageParameters parametersCallback) {
            this.parametersCallback = parametersCallback;
        }
    }

    private class LinkVolver extends StatelessLink {

        private PageParameters parametersCallback;

        public LinkVolver() {
            super(LINK_VOLVER_NAME);
        }

        @Override
        public void onClick() {

            parametersCallback.set(MODE_PARAM_NAME, ModoOperacion.MODIFICACION);
            parametersCallback.set(ID_ENTREGA_PARAMETER, idEntrega);
            parametersCallback.set(SUSCRIPTOR_PARAMETER, nombreSuscriptor);
            parametersCallback.set(TOPICO_PARAMETER, nombreTopico);
            parametersCallback.set("estadoParameter", estado);

            setResponsePage(Entregas.class, parametersCallback);
        }

        public PageParameters getParametersCallback() {
            return parametersCallback;
        }

        public void setParametersCallback(PageParameters parametersCallback) {
            this.parametersCallback = parametersCallback;
        }
    }

    private void definirValoresIniciales() {
        try {
            Entrega entrega = obtenerEntregaService().buscarEntrega(this.idEntrega, this.nombreSuscriptor, this.nombreTopico);
            this.idEntrega = entrega.getId();
            this.uuid = entrega.getUuid();
            this.nombreProductor = entrega.getNovedad().getProductor().getNombre();
            this.nombreTopico = entrega.getNovedad().getTopico().getNombre();
            this.nombreSuscriptor = entrega.getSuscriptor().getNombre();
            this.estado = entrega.getEstado();
            this.tipoEntrega = entrega.getTipoEntrega();
            this.xmlContenido = entrega.getNovedad().getContenido();
            this.reason = entrega.getReason();

            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.PATRON_FECHA_HORA);
            this.fechaCreacion = dateFormat.format(entrega.getFechaCreado());
            if (entrega.getFechaEnviado() != null) {
                this.fechaEnvio = dateFormat.format(entrega.getFechaEnviado());
            }
        } catch(PSException ex) {
            this.showError(ex);
        }

    }

}
