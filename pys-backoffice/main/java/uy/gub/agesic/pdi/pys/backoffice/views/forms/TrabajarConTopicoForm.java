package uy.gub.agesic.pdi.pys.backoffice.views.forms;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.StringValidator;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.backoffice.config.BackofficeProperties;
import uy.gub.agesic.pdi.pys.backoffice.repository.TopicoRepository;
import uy.gub.agesic.pdi.pys.backoffice.utiles.enumerados.ModoOperacion;
import uy.gub.agesic.pdi.pys.backoffice.utiles.enumerados.PermisoUsuario;
import uy.gub.agesic.pdi.pys.backoffice.utiles.exceptions.BackofficeException;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficePage;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.ComponentCustomFeedbackPanel;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.GuardarButton;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.sesion.BackofficeAuthenticationSession;
import uy.gub.agesic.pdi.pys.backoffice.views.*;
import uy.gub.agesic.pdi.pys.common.util.Constants;
import uy.gub.agesic.pdi.pys.domain.FilterTopic;
import uy.gub.agesic.pdi.pys.domain.Topico;
import uy.gub.agesic.pdi.pys.domain.TopicoProductor;
import uy.gub.agesic.pdi.pys.domain.TopicoSuscriptor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@java.lang.SuppressWarnings({"squid:S1068", "squid:S1450"})
public class TrabajarConTopicoForm extends BackofficeForm {

    // Propiedades del backoffice
    private BackofficeProperties properties;

    // Atributos del Tópico
    private String nombreTopico;
    private String elementoRaiz;
    private String namespaceField;
    private String soapAction;
    private String fechaCreacion;
    private Boolean habilitado;

    private String nombreSuscEliminar;
    private String nombreProdEliminar;
    private String currentFilterTopicId;

    private String nombreSuscEditar;

    //Productor asociado al tópico
    private ArrayList<TopicoProductor> topicoProductor;

    //Suscriptores asociados al tópico
    private ArrayList<TopicoSuscriptor> topicoSuscriptor;

    private ArrayList<FilterTopic> filterTopics;

    private static final List<String> estados = Arrays.asList(HABILITADO_VALUE, DESHABILITADO_VALUE);
    private String estado = HABILITADO_VALUE;

    public TrabajarConTopicoForm() {
        super("trabajarConTopicoForm");
    }

    @Override
    public void initForm() {
        this.topicoProductor = new ArrayList<>();
        this.topicoSuscriptor = new ArrayList<>();
        this.filterTopics = new ArrayList<>();
        this.properties = ((BackofficePage) this.getPage()).getProperties();

        this.setDefaultModel(new CompoundPropertyModel<>(this));

        String logedUser = ((BackofficeAuthenticationSession) AuthenticatedWebSession.get()).getUsername();

        try {
            if (logedUser != null) {
                permiso = this.obtenerUsuarioService().permisoUsuario(logedUser);
            }
        } catch (BackofficeException e) {
            showError("Error.General");
        }

        final FormComponent<String> nombre = new TextField<String>(NOMBRE_TOPICO).setRequired(true);
        nombre.add(StringValidator.maximumLength(50));
        this.add(new ComponentCustomFeedbackPanel("nombreFeedback", new ComponentFeedbackMessageFilter(nombre)));
        nombre.setLabel(new Model("Nombre"));
        this.add(nombre);

        final FormComponent<String> elementoRaizCmp = new TextField<String>("elementoRaiz").setRequired(true);
        elementoRaizCmp.add(StringValidator.maximumLength(200));
        this.add(new ComponentCustomFeedbackPanel("elementoRaizFeedback", new ComponentFeedbackMessageFilter(elementoRaizCmp)));
        elementoRaizCmp.setLabel(new Model("Elemento Raíz"));
        this.add(elementoRaizCmp);

        final FormComponent<String> namespaceComponent = new TextField<String>("namespaceField").setRequired(true);
        namespaceComponent.add(StringValidator.maximumLength(200));
        this.add(new ComponentCustomFeedbackPanel("namespaceFeedback", new ComponentFeedbackMessageFilter(namespaceComponent)));
        namespaceComponent.setLabel(new Model("Namespace"));
        this.add(namespaceComponent);

        final FormComponent<String> soapActionCmp = new TextField<String>("soapAction").setRequired(true);
        soapActionCmp.add(StringValidator.maximumLength(200));
        this.add(new ComponentCustomFeedbackPanel("soapActionFeedback", new ComponentFeedbackMessageFilter(soapActionCmp)));
        soapActionCmp.setLabel(new Model("Soap Action"));
        this.add(soapActionCmp);

        Label labelHabilitado = new Label("labelHabilitado", "Estado *");
        labelHabilitado.setOutputMarkupId(true);
        labelHabilitado.setOutputMarkupPlaceholderTag(true);
        this.add(labelHabilitado);

        Label labelFechaCreacion = new Label("labelFechaCreacion","Fecha de creación");
        labelFechaCreacion.setOutputMarkupId(true);
        labelFechaCreacion.setOutputMarkupPlaceholderTag(true);
        labelFechaCreacion.setVisible(false);
        this.add(labelFechaCreacion);

        DropDownChoice<String> habilitadoChoice = new DropDownChoice<>(
                HABILITADO_NAME, new PropertyModel<>(this, "estado"), estados);
        habilitadoChoice.setLabel(new Model<>("Estado"));
        this.add(habilitadoChoice);

        final FormComponent<Date> fechaCreacionCmp = new DateTextField(FECHA_CREACION).setRequired(false);
        this.add(new ComponentCustomFeedbackPanel("fechaCreacionFeedback", new ComponentFeedbackMessageFilter(fechaCreacionCmp)));
        fechaCreacionCmp.setLabel(new Model("Fecha de creación"));
        fechaCreacionCmp.setVisible(false);
        this.add(fechaCreacionCmp);

        // Guardamos cambios
        this.add(new TrabajarConTopicoForm.TrbGuardarButton(this.getFinalMessage(properties.getMensajeConfirmacion())));


        Button botonNuevoSusc = ( new Button(NUEVO_SUSCRIPTOR) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                vistaAddSuscriptor();
            }
        });

        this.add (botonNuevoSusc);

        Button botonNuevoProd = ( new Button(NUEVO_PRODUCTOR) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                vistaAddProductor();
            }
        });

        this.add(botonNuevoProd);

        Button buttonNewTopicFilter = ( new Button(NEW_FILTER_TOPIC) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                viewAddTopicFilter();
            }
        });
        this.add(buttonNewTopicFilter);

        buscarTopicoProductor();
        buscarTopicoSuscriptor();
        searchFilterTopics();

        //Tabla Productores
        TopicosProductorDataView dataViewProd = new TopicosProductorDataView("rowsProd", new ListDataProvider<>(this.topicoProductor));
        this.add(dataViewProd);

        //Tabla Suscriptores
        TopicosSuscriptorDataView dataViewSusc = new TopicosSuscriptorDataView("rowsSusc", new ListDataProvider<>(this.topicoSuscriptor));
        this.add(dataViewSusc);

        FilterTopicDataView filterTopicDataView = new FilterTopicDataView("rowsFilterTopic", new ListDataProvider<>(this.filterTopics));
        this.add(filterTopicDataView);

        // Se agrega el link de volver
        this.add(new TrabajarConTopicoForm.LinkVolver());

        if (permiso != null && permiso.equals(PermisoUsuario.LECTURA.name())) {
            this.get(NUEVO_PRODUCTOR).setVisible(false);
            this.get(NUEVO_SUSCRIPTOR).setVisible(false);
            this.get(NEW_FILTER_TOPIC).setVisible(false);
        }
    }

    @Override
    public void setParametersInner(PageParameters parameters) {
        // Se cargan los datos del filtro de la pagina anterior en el link de volver, por si se quiere volver al mismo lugar de donde se vino
        ((LinkVolver) this.get(LINK_VOLVER_NAME)).setParametersCallback(this.buscarParametrosDeFiltro(parameters));

        if (parameters.getNamedKeys().contains(MODE_PARAM_NAME)) {
            this.modo = parameters.get(MODE_PARAM_NAME).toEnum(ModoOperacion.class);
        }

        if (parameters.getNamedKeys().contains(NOMBRE_TOPICO_PARAM_NAME)) {
            this.nombreTopico = parameters.get(NOMBRE_TOPICO_PARAM_NAME).toString();
        }

        if (parameters.getNamedKeys().contains(FILTER_TOPIC_MESSAGE_WARNING)) {
            String messageWarning = parameters.get(FILTER_TOPIC_MESSAGE_WARNING).toString();
            this.showWarning(messageWarning);
        }

        if (parameters.getNamedKeys().contains(FILTER_TOPIC_MESSAGE_SUCCESS)) {
            String messageSuccess = parameters.get(FILTER_TOPIC_MESSAGE_SUCCESS).toString();
            this.showSuccess(messageSuccess);
        }

        if (!this.modo.equals(ModoOperacion.ALTA)) {
            this.definirValoresIniciales();
        }

        if (this.modo.equals(ModoOperacion.ALTA)) {
            this.get(NUEVO_PRODUCTOR).setVisible(false);
            this.get(NUEVO_SUSCRIPTOR).setVisible(false);
            this.get(NEW_FILTER_TOPIC).setVisible(false);
        }

        // Si el modo es consulta, definir componentes como Readonly
        if (this.modo.equals(ModoOperacion.CONSULTA)) {
            this.get(NOMBRE_TOPICO).add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
            this.get(ELEMENTO_RAIZ).add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
            this.get(NS_NAME).add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
            this.get(SOAP_ACTION).add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
            this.get(FECHA_CREACION).setVisible(true);
            this.get(FECHA_CREACION).add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
            this.get(LABEL_FECHA_CREACION).setVisible(true);
            this.get(HABILITADO_NAME).setEnabled(false);
            this.get(NUEVO_SUSCRIPTOR).setVisible(false);
            this.get(NUEVO_PRODUCTOR).setVisible(false);
            this.get(NEW_FILTER_TOPIC).setVisible(false);
        }
    }

    @java.lang.SuppressWarnings("squid:S3776")
    protected void guardarTopico() {
        try {

            TopicoRepository topicoService = this.obtenerTopicoService();

            if (ModoOperacion.ALTA.equals(this.modo) || ModoOperacion.MODIFICACION.equals(this.modo)) {

                String regex = Constants.PATRON_ROOTELEMENT;

                if (!Pattern.matches(regex, elementoRaiz)) {
                    showError("Datos inv\u00E1lidos, verifique el campo Elemento ra\u00EDz");
                    return;
                }

                if (!Pattern.matches(Constants.PATRON_NOMBRE, this.nombreTopico)) {
                    showError("Datos inv\u00E1lidos, verifique el campo Nombre");
                    return;
                }

                Topico topico = getTopico(topicoService);
                updateTopico(topico);

                if (ModoOperacion.ALTA.equals(this.modo)) {

                    if (topicoService.buscarTopico(this.nombreTopico) != null) {
                        this.showError("Ya existe el T\u00F3pico: " + this.nombreTopico);
                        return;
                    }

                    topicoService.crearTopico(topico);
                    getSession().success("T\u00F3pico creado exitosamente");

                } else {
                    topicoService.modificarTopico(topico);
                    getSession().success("T\u00F3pico modificado exitosamente");
                }
                // Cambio al modo a edicion
                this.modo = ModoOperacion.MODIFICACION;
                this.nombreTopico = topico.getNombre();
                this.definirValoresIniciales();

                PageParameters parameters = new PageParameters();
                parameters.add(MODE_PARAM_NAME, ModoOperacion.MODIFICACION);
                parameters.add(NOMBRE_TOPICO_PARAM_NAME, nombreTopico);

                setResponsePage(TrabajarConTopico.class, parameters);

            }

        } catch (PSException ex) {
            this.showError(ex);
        }
    }

    private void updateTopico(Topico topico) {
        topico.setNombre(this.nombreTopico);
        topico.setElementoRaiz(this.elementoRaiz);
        topico.setNamespace(this.namespaceField);
        topico.setSoapAction(this.soapAction);
        topico.setHabilitado(true);

        try {
            if (this.fechaCreacion != null) {
                topico.setFechaCreacion(new SimpleDateFormat(Constants.PATRON_FECHA_HORA).parse(this.fechaCreacion));
            }
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
        }

        topico.setHabilitado(this.estado.equals(HABILITADO_VALUE));
    }

    private Topico getTopico(TopicoRepository topicoService) throws PSException {
        return ModoOperacion.ALTA.equals(this.modo) ? new Topico() : topicoService.buscarTopico(this.nombreTopico);
    }

    private class TrbGuardarButton extends GuardarButton {

        public TrbGuardarButton(String mensajeConfirmacion) {
            super(mensajeConfirmacion, TrabajarConTopicoForm.this);
        }

        @Override
        public void ejecutar() {
            guardarTopico();
        }
    }

    private class LinkVolver extends StatelessLink {

        private PageParameters parametersCallback;

        public LinkVolver() {
            super(LINK_VOLVER_NAME);
        }

        @Override
        public void onClick() {
            setResponsePage(Topicos.class, parametersCallback);
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

            Topico topico = obtenerTopicoService().buscarTopico(this.nombreTopico);
            this.nombreTopico = topico.getNombre();

            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.PATRON_FECHA_HORA);
            this.fechaCreacion = topico.getFechaCreacion() != null ? dateFormat.format(topico.getFechaCreacion()) : "";

            this.habilitado = topico.getHabilitado();
            this.elementoRaiz = topico.getElementoRaiz();
            this.namespaceField = topico.getNamespace();
            this.soapAction = topico.getSoapAction();

            this.get(NOMBRE_TOPICO).add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
            this.get(FECHA_CREACION).setVisible(false);
            this.get(LABEL_FECHA_CREACION).setVisible(false);

            if (this.habilitado) {
                this.get(HABILITADO_NAME).add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
            }

            this.estado = topico.getHabilitado() ? HABILITADO_VALUE : DESHABILITADO_VALUE;

            this.get(NUEVO_PRODUCTOR).setVisible(true);
            this.get(NUEVO_SUSCRIPTOR).setVisible(true);
            this.get(NEW_FILTER_TOPIC).setVisible(true);


        } catch (PSException ex) {
            this.showError(ex);
        }

    }

    private class TopicosProductorDataView extends DataView<TopicoProductor> {

        public TopicosProductorDataView(String id, ListDataProvider<TopicoProductor> dataProvider) {
            super(id, dataProvider);
        }

        @SuppressWarnings("rawtypes")
        @Override
        protected void populateItem(Item<TopicoProductor> item) {
            final TopicoProductor info = item.getModelObject();

            //Link para eliminar productor
            Link linkEliminar = new Link("linkEliminarProductor") {
                private static final long serialVersionUID = 1L;


                @Override
                public void onClick() {
                    nombreProdEliminar = info.getProductor().getNombre();
                    eliminarProductor();
                }
            };

            linkEliminar.add(new Label("eliminarProd", "eliminar"));

            if (permiso != null) {
                if (permiso.equals(PermisoUsuario.LECTURA.name()) || modo.equals(ModoOperacion.CONSULTA)) {
                    linkEliminar.setVisible(false);
                } else {
                    linkEliminar.add(new AttributeModifier("onclick", "return confirm('¿Está seguro que desea confirmar la operación?');"));
                }
            }

            item.add(linkEliminar);

            String nombreProductor = null;

            if (info != null) {
                nombreProductor = (info.getProductor() != null ? info.getProductor().getNombre() : null);
            }

            Link dataRowLink = new Link("dataRowLinkProd") {
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick() {
                    PageParameters parameters = new PageParameters();
                    parameters.add(NOMBRE_PROD_PARAM_NAME, info.getProductor().getNombre());
                    parameters.add(NOMBRE_TOPICO_PARAM_NAME, nombreTopico);
                    parameters.add(MODE_PARAM_NAME, modo);
                    parameters.add(MODE_PROD_PARAM_NAME , ModoOperacion.CONSULTA);

                    setResponsePage(AddProductor.class, parameters);
                }
            };
            dataRowLink.add(new Label("dataRowProdNombre", nombreProductor));
            item.add(dataRowLink);

        }
    }

    private class TopicosSuscriptorDataView extends DataView<TopicoSuscriptor> {

        public TopicosSuscriptorDataView(String id, ListDataProvider<TopicoSuscriptor> dataProvider) {
            super(id, dataProvider);
        }

        @SuppressWarnings("rawtypes")
        @Override
        protected void populateItem(Item<TopicoSuscriptor> item) {
            final TopicoSuscriptor info = item.getModelObject();

            // Datos puramente visuales
            RepeatingView repeatingView = new RepeatingView("dataRowSusc");

            //Link para eliminar suscriptor
            Link linkEliminar = new Link("linkEliminarSuscriptor") {
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick() {
                    nombreSuscEliminar = info.getSuscriptor().getNombre();
                    eliminarSuscriptor();
                }
            };

            linkEliminar.add(new Label("eliminarSusc", "eliminar"));

            if (permiso != null) {
                if (permiso.equals(PermisoUsuario.LECTURA.name()) || modo.equals(ModoOperacion.CONSULTA)) {
                    linkEliminar.setVisible(false);
                } else {
                    linkEliminar.add(new AttributeModifier("onclick", "return confirm('¿Está seguro que desea confirmar la operación?');"));
                }
            }

            item.add(linkEliminar);

            //Link para editar suscriptor
            Link linkEditar = new Link("linkEditarSuscriptor") {
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick() {
                    nombreSuscEditar = info.getSuscriptor().getNombre();
                    editarSuscriptor();
                }
            };
            linkEditar.add(new Label("editarSusc", "editar"));

            if (permiso != null && (permiso.equals(PermisoUsuario.LECTURA.name()) || modo.equals(ModoOperacion.CONSULTA))) {
                linkEditar.setVisible(false);
            }

            item.add(linkEditar);

            Link dataRowLink = new Link("dataRowLinkSusc") {
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick() {
                    PageParameters parameters = new PageParameters();
                    parameters.add(NOMBRE_SUSC_PARAM_NAME, info.getSuscriptor().getNombre());
                    parameters.add(NOMBRE_TOPICO_PARAM_NAME, nombreTopico);
                    parameters.add(MODE_PARAM_NAME, modo);
                    parameters.add(MODE_SUC_PARAM_NAME, ModoOperacion.CONSULTA);

                    setResponsePage(AddSuscriptor.class, parameters);
                }
            };
            dataRowLink.add(new Label("dataRowNombre", info.getSuscriptor().getNombre()));
            item.add(dataRowLink);

            repeatingView.add(new Label(repeatingView.newChildId(), info.getDeliveryMode()));
            item.add(repeatingView);
        }
    }

    private class FilterTopicDataView extends DataView<FilterTopic> {
        public FilterTopicDataView(String id, ListDataProvider<FilterTopic> dataProvider) {
            super(id, dataProvider);
        }

        @SuppressWarnings("rawtypes")
        @Override
        protected void populateItem(Item<FilterTopic> item) {
            final FilterTopic info = item.getModelObject();

            RepeatingView repeatingView = new RepeatingView("dataRowFT");

            Link linkDelete = new Link("linkDeleteFilter") {
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick() {
                    currentFilterTopicId = info.getId();
                    deleteFilter();
                }
            };
            linkDelete.add(new Label("deleteFilter", "eliminar"));
            if (permiso != null) {
                if (permiso.equals(PermisoUsuario.LECTURA.name()) || modo.equals(ModoOperacion.CONSULTA)) {
                    linkDelete.setVisible(false);
                } else {
                    linkDelete.add(new AttributeModifier("onclick", "return confirm('¿Está seguro que desea confirmar la operación?');"));
                }
            }
            item.add(linkDelete);

            Link linkUpdate = new Link("linkUpdateFilter") {
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick() {
                    currentFilterTopicId = info.getId();
                    updateFilter();
                }
            };
            linkUpdate.add(new Label("updateFilter", "editar"));
            if (permiso != null && (permiso.equals(PermisoUsuario.LECTURA.name()) || modo.equals(ModoOperacion.CONSULTA))) {
                linkUpdate.setVisible(false);
            }
            item.add(linkUpdate);

            Link dataRowLink = new Link("dataRowLinkFilter") {
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick() {
                    PageParameters parameters = new PageParameters();
                    parameters.add(FILTER_TOPIC_PARAM_ID, info.getId());
                    parameters.add(NOMBRE_TOPICO_PARAM_NAME, nombreTopico);
                    parameters.add(MODE_PARAM_NAME, modo);
                    parameters.add(FILER_MODE_FILTER_PARAM_NAME, ModoOperacion.CONSULTA);

                    setResponsePage(PageFilterTopic.class, parameters);
                }
            };
            dataRowLink.add(new Label("dataRowName", info.getFilter().getName()));
            item.add(dataRowLink);

            String type;
            if (info.getType() == FilterTopic.Type.MESSAGE) {
                type = "Mensaje";
            } else {
                type = "Suscriptor";
            }

            repeatingView.add(new Label(repeatingView.newChildId(), type));
            item.add(repeatingView);
        }
    }

    public void buscarTopicoProductor() {
        try {
            this.topicoProductor.clear();
            List<TopicoProductor> topicoProductorList = obtenerTopicoProductorService().buscarTopicoProductor(RequestCycle.get().getRequest().getRequestParameters().getParameterValue(NOMBRE_TOPICO_PARAM_NAME).toString());
            if (topicoProductorList != null && !topicoProductorList.isEmpty()) {
                this.topicoProductor.addAll(topicoProductorList);
            }

        } catch (PSException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void buscarTopicoSuscriptor() {
        try {
            this.topicoSuscriptor.clear();
            List<TopicoSuscriptor> topicoSuscriptorList = obtenerTopicoSuscriptorService().buscarTopicosSuscriptor(RequestCycle.get().getRequest().getRequestParameters().getParameterValue(NOMBRE_TOPICO_PARAM_NAME).toString());

            if (topicoSuscriptorList != null && !topicoSuscriptorList.isEmpty()) {
                this.topicoSuscriptor.addAll(topicoSuscriptorList);
            }

        } catch (PSException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void searchFilterTopics() {
        try {
            this.filterTopics.clear();
            String nameTopic = RequestCycle.get().getRequest().getRequestParameters().getParameterValue(NOMBRE_TOPICO_PARAM_NAME).toString();
            Topico topic = obtenerTopicoService().buscarTopico(nameTopic);
            List<FilterTopic> filterTopicList = getFilterTopicService().searchFilterTopicsByTopic(topic);

            if (filterTopicList != null && !filterTopicList.isEmpty()) {
                this.filterTopics.addAll(filterTopicList);
            }
        } catch (PSException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void vistaAddSuscriptor() {
        PageParameters parameters = new PageParameters();
        parameters.add(NOMBRE_TOPICO_PARAM_NAME, this.nombreTopico);
        parameters.add(MODE_SUC_PARAM_NAME, ModoOperacion.ALTA);
        parameters.add(MODE_PARAM_NAME, modo);
        setResponsePage(AddSuscriptor.class, parameters);
    }

    public void viewAddTopicFilter() {
        PageParameters parameters = new PageParameters();
        parameters.add(NOMBRE_TOPICO_PARAM_NAME, nombreTopico);
        parameters.add(FILER_MODE_FILTER_PARAM_NAME, ModoOperacion.ALTA);
        parameters.add(MODE_PARAM_NAME, modo);
        setResponsePage(PageFilterTopic.class, parameters);
    }

    public void vistaAddProductor() {
        PageParameters parameters = new PageParameters();
        parameters.add(NOMBRE_TOPICO_PARAM_NAME, this.nombreTopico);
        parameters.add(MODE_PARAM_NAME, this.modo);
        parameters.add(MODE_PROD_PARAM_NAME, ModoOperacion.ALTA);
        setResponsePage(AddProductor.class, parameters);
    }

    public void eliminarProductor() {
        try {
            if (this.nombreProdEliminar == null) {
                this.showError("Debe seleccionar un productor");
            } else {
                obtenerTopicoProductorService().eliminar(nombreTopico, nombreProdEliminar);
                buscarTopicoProductor();
                this.showSuccess("Operacion.exitosa");
            }

        } catch (PSException e) {
            showError("Productores.error.eliminar");
            logger.error(e.getMessage(), e);
        }

    }

    public void eliminarSuscriptor() {
        try {
            if (nombreSuscEliminar == null) {
                this.showError("Debe seleccionar un suscriptor");
            } else {
                //Se cancelan las entregas pendientes del suscriptor para el tópico y se elimina la asociación
                TopicoSuscriptor ts = obtenerTopicoSuscriptorService().buscarTopicoSuscriptor(nombreSuscEliminar, nombreTopico);
                obtenerEntregaService().cancelarEntregasTopicoSuscriptor(ts.getSuscriptor(), ts.getTopico());
                obtenerTopicoSuscriptorService().eliminar(nombreTopico, nombreSuscEliminar);
                buscarTopicoSuscriptor();
                this.showSuccess("Operacion.exitosa");
            }

        } catch (PSException e) {
            showError("Suscriptores.error.eliminar");
            logger.error(e.getMessage(), e);
        }

    }

    public void editarSuscriptor() {
        PageParameters parameters = new PageParameters();
        parameters.add(NOMBRE_SUSC_PARAM_NAME, this.nombreSuscEditar);
        parameters.add(NOMBRE_TOPICO_PARAM_NAME, this.nombreTopico);
        parameters.add(MODE_PARAM_NAME, modo);
        parameters.add(MODE_SUC_PARAM_NAME, ModoOperacion.MODIFICACION);
        setResponsePage(AddSuscriptor.class, parameters);
    }

    public void deleteFilter() {
        if (this.currentFilterTopicId == null) {
            this.showError("Debe seleccionar un filtro");
        } else {
            getFilterTopicService().deleteFilterTopic(this.currentFilterTopicId);
            this.searchFilterTopics();
            this.showSuccess("Operacion.exitosa");
        }
    }

    public void updateFilter() {
        PageParameters parameters = new PageParameters();
        parameters.add(FILTER_TOPIC_PARAM_ID, this.currentFilterTopicId);
        parameters.add(NOMBRE_TOPICO_PARAM_NAME, this.nombreTopico);
        parameters.add(MODE_PARAM_NAME, modo);
        parameters.add(FILER_MODE_FILTER_PARAM_NAME, ModoOperacion.MODIFICACION);
        setResponsePage(PageFilterTopic.class, parameters);
    }
}
