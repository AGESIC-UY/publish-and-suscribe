package uy.gub.agesic.pdi.pys.backoffice.views.forms;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.StringValidator;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.backoffice.config.BackofficeProperties;
import uy.gub.agesic.pdi.pys.backoffice.utiles.enumerados.ModoOperacion;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficePage;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.BotonAccion;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.ComponentCustomFeedbackPanel;
import uy.gub.agesic.pdi.pys.backoffice.views.Error;
import uy.gub.agesic.pdi.pys.backoffice.views.TrabajarConTopico;
import uy.gub.agesic.pdi.pys.common.util.Constants;
import uy.gub.agesic.pdi.pys.domain.DeliveryMode;
import uy.gub.agesic.pdi.pys.domain.Suscriptor;
import uy.gub.agesic.pdi.pys.domain.Topico;
import uy.gub.agesic.pdi.pys.domain.TopicoSuscriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class AddSuscriptorForm extends BackofficeForm {

    private ModoOperacion modoSusc;

    private String nombreTopico;

    private String nombreSuscriptor;

    private Suscriptor suscriptor;

    private String deliveryAddr;

    private String deliveryWsaTo;

    private ArrayList<Suscriptor> listSuscriptores;

    private String modoEnvio = DeliveryMode.PULL;

    private static final long serialVersionUID = 1L;

    public AddSuscriptorForm() {
        super("addSuscriptorForm");
    }

    @Override
    public void initForm() {

        this.setDefaultModel(new CompoundPropertyModel<>(this));

        super.initForm();

        BackofficeProperties properties = ((BackofficePage) this.getPage()).getProperties();

        this.add(new AddSuscriptorForm.AgregarSuscriptorButton(this.getFinalMessage(properties.getMensajeConfirmacion())));

        this.nombreTopico = RequestCycle.get().getRequest().getRequestParameters().getParameterValue(NOMBRE_TOPICO_PARAM_NAME).toString();

        if (RequestCycle.get().getRequest().getRequestParameters().getParameterNames().contains (MODE_SUC_PARAM_NAME)) {
            this.modoSusc = RequestCycle.get().getRequest().getRequestParameters().getParameterValue(MODE_SUC_PARAM_NAME).toEnum(ModoOperacion.class);
        }

        listSuscriptores = new ArrayList<>();
        buscarSuscriptores();

        final FormComponent<String> address = new TextField<>(DELIVERY_ADDR_NAME);
        address.setVisible(false);
        address.setRequired(true);
        address.add(StringValidator.maximumLength(200));
        address.setOutputMarkupId(true);
        address.setOutputMarkupPlaceholderTag(true);
        address.setLabel(new Model("Direcci\u00F3n de env\u00EDo"));
        this.add(address);

        ComponentCustomFeedbackPanel addressFeedback = new ComponentCustomFeedbackPanel("deliveryAddrFeedback", new ComponentFeedbackMessageFilter(address));
        addressFeedback.setOutputMarkupId(true);
        addressFeedback.setOutputMarkupPlaceholderTag(true);
        this.add(addressFeedback);

        Label labelAddress = new Label(LABEL_DELIVERY_ADDR, "Direcci\u00F3n de env\u00EDo *");
        labelAddress.setOutputMarkupId(true);
        labelAddress.setOutputMarkupPlaceholderTag(true);
        labelAddress.setVisible(false);
        this.add(labelAddress);

        final FormComponent<String> wsaTo = new TextField<>(DELIVERY_WSA_TO_NAME);
        wsaTo.setVisible(false);
        wsaTo.setRequired(true);
        wsaTo.add(StringValidator.maximumLength(200));
        wsaTo.setOutputMarkupId(true);
        wsaTo.setOutputMarkupPlaceholderTag(true);
        wsaTo.setLabel(new Model("Wsa To"));
        this.add(wsaTo);

        ComponentCustomFeedbackPanel wsaToFeedback = new ComponentCustomFeedbackPanel("deliveryWsaToFeedback", new ComponentFeedbackMessageFilter(wsaTo));
        wsaToFeedback.setOutputMarkupId(true);
        wsaToFeedback.setOutputMarkupPlaceholderTag(true);
        this.add(wsaToFeedback);

        Label labelWsaTo = new Label(LABEL_DELIVERY_WSATO, "Wsa To *");
        labelWsaTo.setOutputMarkupId(true);
        labelWsaTo.setOutputMarkupPlaceholderTag(true);
        labelWsaTo.setVisible(false);
        this.add(labelWsaTo);

        final DropDownChoice<Suscriptor> suscriptorChoice = new DropDownChoice<>(SUSCRIPTOR_NAME, new PropertyModel<>(this, SUSCRIPTOR_NAME), listSuscriptores);
        suscriptorChoice.setNullValid(true);
        suscriptorChoice.setRequired(true);
        suscriptorChoice.setOutputMarkupId(true);
        suscriptorChoice.setOutputMarkupPlaceholderTag(true);
        suscriptorChoice.setLabel(new Model<>("Suscriptor"));
        this.add(suscriptorChoice);

        final DropDownChoice<String> deliveryMode = new DropDownChoice<>(
                MODO_ENVIO_NAME, new PropertyModel<>(this, MODO_ENVIO_NAME), modosEnvio);
        deliveryMode.setOutputMarkupId(true);
        deliveryMode.setOutputMarkupPlaceholderTag(true);
        deliveryMode.setLabel(new Model<>("Modo de env\u00EDo"));
        this.add(deliveryMode);

        deliveryMode.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                if (target != null) {
                    target.add(wsaTo);
                    target.add(address);
                    target.add(labelAddress);
                    target.add(labelWsaTo);
                    target.add(wsaToFeedback);
                    target.add(addressFeedback);

                    if (modoEnvio.equalsIgnoreCase(DeliveryMode.PUSH)) {
                        wsaToFeedback.setVisible(true);
                        addressFeedback.setVisible(true);
                        wsaTo.setVisible(true);
                        address.setVisible(true);
                        labelAddress.setVisible(true);
                        labelWsaTo.setVisible(true);
                        modoEnvio = DeliveryMode.PUSH;
                    } else {
                        wsaToFeedback.setVisible(false);
                        addressFeedback.setVisible(false);
                        wsaTo.setVisible(false);
                        address.setVisible(false);
                        labelAddress.setVisible(false);
                        labelWsaTo.setVisible(false);
                        modoEnvio = DeliveryMode.PULL;
                    }
                }
            }
        });

        // Se agrega el link de volver
        this.add(new AddSuscriptorForm.LinkVolver());

    }

    private class LinkVolver extends StatelessLink {

        private PageParameters parametersCallback;

        public LinkVolver() {
            super(LINK_VOLVER_NAME);
        }

        @Override
        public void onClick() {
            parametersCallback.set(MODE_PARAM_NAME, modo);
            parametersCallback.set(NOMBRE_TOPICO_PARAM_NAME, nombreTopico);
            parametersCallback.set(NOMBRE_SUSC_PARAM_NAME, nombreSuscriptor);
            parametersCallback.set(MODE_SUC_PARAM_NAME, modoSusc);

            setResponsePage(TrabajarConTopico.class, parametersCallback);
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

            TopicoSuscriptor topicoSuscriptor = this.obtenerTopicoSuscriptorService().buscarTopicoSuscriptor(this.nombreSuscriptor, this.nombreTopico);
            this.modoEnvio = topicoSuscriptor.getDeliveryMode();
            this.deliveryAddr = topicoSuscriptor.getDeliveryAddr();
            this.deliveryWsaTo = topicoSuscriptor.getDeliveryWsaTo();
            this.suscriptor = topicoSuscriptor.getSuscriptor();
            this.nombreSuscriptor = topicoSuscriptor.getSuscriptor().getNombre();

            if (modoEnvio.equalsIgnoreCase(DeliveryMode.PUSH)) {
                this.get(LABEL_DELIVERY_ADDR).setVisible(true);
                this.get(LABEL_DELIVERY_WSATO).setVisible(true);
            } else {
                this.get(LABEL_DELIVERY_ADDR).setVisible(false);
                this.get(LABEL_DELIVERY_WSATO).setVisible(false);
            }

            this.get(SUSCRIPTOR_NAME).add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));

        } catch (PSException ex) {
            this.showError(ex);
        }

    }

    @Override
    public void setParametersInner(PageParameters parameters) {

        ((LinkVolver) this.get(LINK_VOLVER_NAME)).setParametersCallback(this.buscarParametrosDeFiltro(parameters));

        if (parameters.getNamedKeys().contains(MODE_PARAM_NAME)) {
            this.modo = parameters.get(MODE_PARAM_NAME).toEnum(ModoOperacion.class);
        }

        if (parameters.getNamedKeys().contains(MODE_SUC_PARAM_NAME)) {
            this.modoSusc = parameters.get(MODE_SUC_PARAM_NAME).toEnum(ModoOperacion.class);
        }

        if (parameters.getNamedKeys().contains(NOMBRE_TOPICO_PARAM_NAME)) {
            this.nombreTopico = parameters.get(NOMBRE_TOPICO_PARAM_NAME).toString();
        }

        if (parameters.getNamedKeys().contains(NOMBRE_SUSC_PARAM_NAME)) {
            this.nombreSuscriptor = parameters.get(NOMBRE_SUSC_PARAM_NAME).toString();
        }

        if (!this.modoSusc.equals(ModoOperacion.ALTA)) {
            this.definirValoresIniciales();
        }

        // Si el modo es consulta, definir componentes como Readonly
        if (this.modoSusc.equals(ModoOperacion.CONSULTA)) {

            if (this.modoEnvio.equalsIgnoreCase(DeliveryMode.PUSH)) {
                this.get(DELIVERY_ADDR_NAME).add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
                this.get(DELIVERY_WSA_TO_NAME).add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
            }

            this.get(SUSCRIPTOR_NAME).setEnabled(false);
            this.get(MODO_ENVIO_NAME).setEnabled(false);

        }

        if (this.modoEnvio.equalsIgnoreCase(DeliveryMode.PUSH)) {
            this.get(DELIVERY_ADDR_NAME).setVisible(true);
            this.get(DELIVERY_WSA_TO_NAME).setVisible(true);
        }
    }

    private class AgregarSuscriptorButton extends BotonAccion {

        public AgregarSuscriptorButton(String mensajeConfirmacion) {
            super("btnAgregarSuscriptor", Error.class, false, mensajeConfirmacion);
        }

        @Override
        public boolean poseePermisoEjecucion() {
            // El control en este caso ya fue realizado al ingresar a la pagina, segun el modo apropiado
            return true;
        }

        @Override
        public boolean isVisible() {
            return !AddSuscriptorForm.this.modoSusc.equals(ModoOperacion.CONSULTA);
        }

        @Override
        public void ejecutar() {
            guardarTopicoSuscriptor();
        }
    }

    protected void guardarTopicoSuscriptor() {
        try {
            if (this.modo.equals(ModoOperacion.ALTA) && obtenerTopicoSuscriptorService().buscarTopicoSuscriptor(this.suscriptor.getNombre(), this.nombreTopico) != null) {
                 showError("TopicoSuscriptor.existe.relacion");
            } else {
                TopicoSuscriptor topicoSuscriptor = obtenerTopicoSuscriptorService().buscarTopicoSuscriptor(this.suscriptor.getNombre(),this.nombreTopico);

                if (topicoSuscriptor == null) {
                    topicoSuscriptor = new TopicoSuscriptor();
                }

                Topico topico = obtenerTopicoService().buscarTopico(this.nombreTopico);
                topicoSuscriptor.setTopico(topico);
                topicoSuscriptor.setSuscriptor(this.suscriptor);

                if (this.modoEnvio != null && this.modoEnvio.equalsIgnoreCase(DeliveryMode.PUSH)) {

                    String regex = Constants.PATRON_URI;

                    if (!Pattern.matches(regex, deliveryAddr) || (!Pattern.matches(regex, deliveryWsaTo))) {
                        showError("Datos inv\u00E1lidos, verifique los campos Direcci\u00F3n y Wsa To");
                        return;
                    }

                    topicoSuscriptor.setDeliveryWsaTo(this.deliveryWsaTo);
                    topicoSuscriptor.setDeliveryAddr(this.deliveryAddr);
                    topicoSuscriptor.setDeliveryMode(DeliveryMode.PUSH);
                } else {
                    topicoSuscriptor.setDeliveryMode(DeliveryMode.PULL);
                }

                obtenerTopicoSuscriptorService().crear(topicoSuscriptor);
                String susName = topicoSuscriptor.getSuscriptor().getNombre();
                String topicName = topicoSuscriptor.getTopico().getNombre();
                obtenerEntregaService().crearColeccion(topicName, susName);

                PageParameters parameters = new PageParameters();
                parameters.add(MODE_PARAM_NAME, modo);
                parameters.add(MODE_SUC_PARAM_NAME, modoSusc);
                parameters.add(NOMBRE_TOPICO_PARAM_NAME, this.nombreTopico);

                setResponsePage(TrabajarConTopico.class, parameters);

                this.showSuccess("Operacion.exitosa");
            }

        } catch (PSException e) {
            logger.error(e.getMessage(), e);
            showError("TopicoSuscriptor.error.asociar");
        }
    }

    public void buscarSuscriptores() {
        try {

            if (modoSusc != null && !modoSusc.equals(ModoOperacion.ALTA)) {
                this.listSuscriptores.addAll(obtenerSuscriptorService().getAll());
            } else {

                List<Suscriptor> listaAuxiliarSuscriptores = obtenerSuscriptorService().obtenerHabilitados();

                for (Suscriptor s : listaAuxiliarSuscriptores) {
                    if (obtenerTopicoSuscriptorService().buscarTopicoSuscriptor(s.getNombre(), this.nombreTopico) == null) {
                        listSuscriptores.add(s);
                    }
                }
           }

        } catch (PSException e) {
            logger.error(e.getMessage(), e);
        }
    }

}
