package uy.gub.agesic.pdi.pys.backoffice.utiles.ui;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.StringValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uy.gub.agesic.pdi.pys.backend.service.FilterService;
import uy.gub.agesic.pdi.pys.backend.service.FilterTopicService;
import uy.gub.agesic.pdi.pys.backoffice.repository.*;
import uy.gub.agesic.pdi.pys.backoffice.services.UserService;
import uy.gub.agesic.pdi.pys.backoffice.utiles.enumerados.ModoOperacion;
import uy.gub.agesic.pdi.pys.backoffice.utiles.exceptions.BackofficeException;
import uy.gub.agesic.pdi.pys.backoffice.utiles.spring.ApplicationContextProvider;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.ComponentCustomFeedbackPanel;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.sesion.BackofficeAuthenticationSession;
import uy.gub.agesic.pdi.pys.domain.BaseNamedEntity;
import uy.gub.agesic.pdi.pys.domain.DeliveryMode;

import java.util.Arrays;
import java.util.List;
import java.util.MissingResourceException;

@java.lang.SuppressWarnings("squid:S2068")
public abstract class BackofficeForm extends StatelessForm<CompoundPropertyModel<BackofficeForm>> {

    protected static final Logger logger = LoggerFactory.getLogger(BackofficeForm.class);

    public static final String PARAM_MSJ_EXITO = "msjExito";

    public static final String PARAM_MSJ_ERROR = "msjError";

    public static final String PARAM_MSJ_WARNING = "msjWarning";

    protected static final String MODE_PARAM_NAME = "modo";

    protected static final String LINK_VOLVER_NAME = "linkVolver";

    protected static final String NOMBRE_PARAMETER = "nombreParameter";

    protected static final String NOMBRE_TOPICO = "nombreTopico";

    protected static final String NUEVO_SUSCRIPTOR = "nuevoSuscriptor";

    protected static final String NUEVO_PRODUCTOR = "nuevoProductor";

    protected static final String NEW_FILTER_TOPIC = "newTopicFilter";

    protected static final String FECHA_CREACION = "fechaCreacion";

    protected static final String DATA_ROW_LINK = "dataRowLink";

    protected static final String DATA_ROW_SELECTOR = "dataRowSelector";

    protected static final String DATA_ROW_NOMBRE = "dataRowNombre";

    protected static final String DATA_ROW_NAME = "dataRowName";

    protected static final String DATA_ROW = "dataRow";

    protected static final String FILTRO_NRO_PAGINA = "filtro_nroPagina";

    protected static final String FILTRO_PAGE_SIZE = "filtro_pageSize";

    protected static final String FILTRO_NOMBRE = "filtro_nombre";

    protected static final String FILTRO_DN = "filtro_dn";

    protected static final String FILTRO_HABILITADO = "filtro_habilitado";

    protected static final String FILTRO_FECHA_DESDE = "filtro_fechaDesde";

    protected static final String FILTRO_FECHA_HASTA = "filtro_fechaHasta";

    protected static final String FILTRO_UUID_NOVEDAD = "filtro_uuidNovedad";

    protected static final String FILTRO_NOMBRE_TOPICO = "filtro_nombreTopico";

    protected static final String FILTRO_NOMBRE_PRODUCTOR = "filtro_nombrePoductor";

    protected static final String FILTRO_ESTADO = "filtro_estado";

    protected static final String FILTRO_TIPO_ENTREGA = "filtro_tipoEntrega";

    protected static final String FILTRO_NOMBRE_SUSCRIPTOR = "filtro_nombreSuscriptor";

    protected static final String FILTRO_UUID = "filtro_uuid";

    protected static final String HABILITADO_NAME = "habilitado";

    protected static final String HABILITADO_VALUE = "Habilitado";

    protected static final String LABEL_HABILITADO = "labelHabilitado";

    protected static final String DESHABILITADO_VALUE = "Deshabilitado";

    protected static final String LABEL_FECHA_CREACION = "labelFechaCreacion";

    protected static final String NOMBRE_NAME = "nombre";

    protected static final String DN_NAME = "dn";

    protected static final String UUID_NOVEDAD_PARAMETER = "uuidNovedadParameter";

    protected static final String NOMBRE_TOPICO_PARAM_NAME = "nombreTopicoParameter";

    protected static final String NOMBRE_SUSC_PARAM_NAME = "nombreSuscParameter";

    protected static final String MODE_SUC_PARAM_NAME = "modoSusc";

    protected static final String MODE_PROD_PARAM_NAME = "modoProd";

    protected static final String ELEMENTO_RAIZ = "elementoRaiz";

    protected static final String NS_NAME = "namespaceField";

    protected static final String SOAP_ACTION = "soapAction";

    protected static final String LABEL_DELIVERY_ADDR = "labelDeliveryAddr";

    protected static final String LABEL_DELIVERY_WSATO = "labelDeliveryWsaTo";

    protected static final String DELIVERY_ADDR_NAME = "deliveryAddr";

    protected static final String DELIVERY_WSA_TO_NAME = "deliveryWsaTo";

    protected static final String MODO_ENVIO_NAME = "modoEnvio";

    protected static final String SUSCRIPTOR_NAME = "suscriptor";

    protected static final String PRODUCTOR_NAME = "productor";

    protected static final String NOMBRE_PROD_PARAM_NAME = "nombreProdParameter";

    protected static final String LOGIN_CMP = "login";

    protected static final String PASSWORD_CMP = "password";

    protected static final String LABEL_PASSWORD = "labelPassword";

    protected static final String ADMIN_CMP = "admin";

    protected static final String LOGIN_PARAMETER = "loginParameter";

    protected static final String FILTRO_LOGIN = "filtro_login";

    protected static final String FILTRO_APELLIDO = "filtro_apellido";

    protected static final String LIMPIAR_FILTRO_COMPONENT_NAME = "limpiarFiltros";

    protected static final String READONLY_ATTRIBUTE_NAME = "readonly";

    protected static final String DISABLED_ATTRIBUTE_NAME = "disabled";

    protected static final String FILTER_LABEL_NAME_FIELD = "labelName";

    protected static final String FILTER_NAME_FIELD = "name";

    protected static final String FILTER_LABEL_DOCUMENT_TYPE_FIELD = "labelDocumentType";

    protected static final String FILTER_DOCUMENT_TYPE_FIELD = "documentType";

    protected static final String FILTER_LABEL_OPERATOR_FIELD = "labelOperator";

    protected static final String FILTER_OPERATOR_FIELD = "operator";

    protected static final String FILTER_PARAM_NAME = "filterParamName";

    protected static final String FILTER_PARAM_ID = "filterParamId";

    protected static final String FILTER_TOPIC_PARAM_ID = "filterTopicParamId";

    protected static final String FILTER_RULE_PARAM_NAME = "filterRuleParamName";

    protected static final String FILTER_RULE_PARAM_ID = "filterRuleParamId";

    protected static final String FILER_MODE_RULE_PARAM_NAME = "modoRule";

    protected static final String FILER_MODE_FILTER_PARAM_NAME = "modoFilter";

    protected static final String FILTER_BTN_NEW_FILTER_RULE = "newRule";

    protected static final String FILTER_RULE_LABEL_NAME_FIELD = "labelName";

    protected static final String FILTER_RULE_NAME_FIELD = "name";

    protected static final String FILTER_RULE_NAME_FEED_FIELD = "nameFeedback";

    protected static final String FILTER_RULE_LABEL_OPERATOR_FIELD = "labelOperator";

    protected static final String FILTER_RULE_OPERATOR_FIELD = "operator";

    protected static final String FILTER_RULE_LABEL_LEFTFACTORTYPE_FIELD = "labelLeftFactorType";

    protected static final String FILTER_RULE_LEFTFACTORTYPE_FIELD = "leftFactorType";

    protected static final String FILTER_RULE_LABEL_LEFTFACTORVALUE_FIELD = "labelLeftFactorValue";

    protected static final String FILTER_RULE_LEFTFACTORVALUE_FIELD = "leftFactorValue";

    protected static final String FILTER_RULE_LEFTFACTORVALUE_FEED_FIELD = "leftFactorValueFeedback";

    protected static final String FILTER_RULE_LABEL_LEFTFACTORSUBSCRIBER_FIELD = "labelLeftFactorSubscriber";

    protected static final String FILTER_RULE_LEFTFACTORSUBSCRIBER_FIELD = "leftFactorSubscriber";

    protected static final String FILTER_RULE_LABEL_LEFTFACTORVALUETYPE_FIELD = "labelLeftFactorValueType";

    protected static final String FILTER_RULE_LEFTFACTORVALUETYPE_FIELD = "leftFactorValueType";

    protected static final String FILTER_RULE_LABEL_LEFTFACTORVALUEFORMAT_DATETIME_FIELD = "labelLeftFactorValueFormatDatetime";

    protected static final String FILTER_RULE_LEFTFACTORVALUEFORMAT_DATETIME_FIELD = "leftFactorValueFormatDatetime";

    protected static final String FILTER_RULE_LABEL_LEFTFACTORVALUEFORMAT_NUMERIC_FIELD = "labelLeftFactorValueFormatNumeric";

    protected static final String FILTER_RULE_LEFTFACTORVALUEFORMAT_NUMERIC_FIELD = "leftFactorValueFormatNumeric";

    protected static final String FILTER_RULE_LABEL_RIGHTFACTORTYPE_FIELD = "labelRightFactorType";

    protected static final String FILTER_RULE_RIGHTFACTORTYPE_FIELD = "rightFactorType";

    protected static final String FILTER_RULE_LABEL_RIGHTFACTORVALUE_FIELD = "labelRightFactorValue";

    protected static final String FILTER_RULE_RIGHTFACTORVALUE_FIELD = "rightFactorValue";

    protected static final String FILTER_RULE_RIGHTFACTORVALUE_FEED_FIELD = "rightFactorValueFeedback";

    protected static final String FILTER_RULE_LABEL_RIGHTFACTORSUBSCRIBER_FIELD = "labelRightFactorSubscriber";

    protected static final String FILTER_RULE_RIGHTFACTORSUBSCRIBER_FIELD = "rightFactorSubscriber";

    protected static final String FILTER_RULE_LABEL_RIGHTFACTORVALUETYPE_FIELD = "labelRightFactorValueType";

    protected static final String FILTER_RULE_RIGHTFACTORVALUETYPE_FIELD = "rightFactorValueType";

    protected static final String FILTER_RULE_LABEL_RIGHTFACTORVALUEFORMAT_DATETIME_FIELD = "labelRightFactorValueFormatDatetime";

    protected static final String FILTER_RULE_RIGHTFACTORVALUEFORMAT_DATETIME_FIELD = "rightFactorValueFormatDatetime";

    protected static final String FILTER_RULE_LABEL_RIGHTFACTORVALUEFORMAT_NUMERIC_FIELD = "labelRightFactorValueFormatNumeric";

    protected static final String FILTER_RULE_RIGHTFACTORVALUEFORMAT_NUMERIC_FIELD = "rightFactorValueFormatNumeric";

    protected static final String FILTER_RULE_MODAL_LEFT = "modalSubscribersLeft";

    protected static final String FILTER_RULE_MODAL_LEFT_LINK = "showModalSubscribersLeft";

    protected static final String FILTER_RULE_MODAL_RIGHT = "modalSubscribersRight";

    protected static final String FILTER_RULE_MODAL_RIGHT_LINK = "showModalSubscribersRight";

    protected static final String FILTER_TOPIC_LABEL_FILTER_NAME_FIELD = "labelFilterTopicFilter";

    protected static final String FILTER_TOPIC_FILTER_NAME_FIELD = "filterTopicFilter";

    protected static final String FILTER_TOPIC_LABEL_TYPE_NAME_FIELD = "labelFilterTopicType";

    protected static final String FILTER_TOPIC_TYPE_NAME_FIELD = "filterTopicType";

    protected static final String XPATH_XML_NAME_FIELD = "dataXml";

    protected static final String XPATH_XMLPATHS_NAME_FIELD = "xmlPaths";

    protected static final String XPATH_LABEL_EVALUATION_NAME_FIELD = "labelXpathEvaluation";

    protected static final String XPATH_LABEL_BUILD_NAME_FIELD = "labelXpathBuild";

    protected static final String XPATH_BUILD_NAME_FIELD = "xpathBuildField";

    protected static final String XPATH_BUILD_FEED_NAME_FIELD = "xpathBuildFeed";

    protected static final String FILTER_TOPIC_LABEL_MAXIMUM_OCCURRENCES_NAME_FIELD = "labelFilterTopicMaximumOccurrences";

    protected static final String FILTER_TOPIC_MAXIMUM_OCCURRENCES_NAME_FIELD = "filterTopicMaximumOccurrences";

    protected static final String FILTER_TOPIC_MAXIMUM_OCCURRENCES_NAME_FEED_FIELD = "filterTopicMaximumOccurrencesFeedback";

    protected static final String FILTER_TOPIC_MESSAGE_WARNING = "filterTopicMessageWarning";

    protected static final String FILTER_TOPIC_MESSAGE_SUCCESS = "filterTopicMessageSuccess";

    protected static final List<String> modosEnvio = Arrays.asList(DeliveryMode.PULL, DeliveryMode.PUSH);

    protected String permiso;

    protected String logedUser;

    // Parametros
    protected ModoOperacion modo;

    public BackofficeForm(String id) {
        super(id);
        this.validateComponents();
    }

    /**
     * Esta funcion inicializa el formulario. Debe ser llamada manualmente por quien instancie este formulario, LUEGO de que el mismo
     * fue agregado al a pagina que lo contiene. Si esto no se hace asi, se generan errores con el Localizer de recursos al invocar a 
     * la funcion getFinalMessage(key)
     */
	protected void initForm() {

        logedUser = ((BackofficeAuthenticationSession) AuthenticatedWebSession.get()).getUsername();

        try {
            if (logedUser != null) {
                permiso = this.obtenerUsuarioService().permisoUsuario(logedUser);
            }
        } catch (BackofficeException e) {
            showError("Error.General");
        }
    }

    protected void logout() throws BackofficeException {
    	BackofficePage ownerPage = (BackofficePage)this.getPage();
    	ownerPage.logout();
    }

    public final void setParameters(PageParameters parameters) {
        if (parameters.getNamedKeys().contains(BackofficeForm.PARAM_MSJ_EXITO)) {
            showSuccess(parameters.get(BackofficeForm.PARAM_MSJ_EXITO).toString());
        } else if (parameters.getNamedKeys().contains(BackofficeForm.PARAM_MSJ_ERROR)) {
            showError(parameters.get(BackofficeForm.PARAM_MSJ_ERROR).toString());
        } else if (parameters.getNamedKeys().contains(BackofficeForm.PARAM_MSJ_WARNING)) {
            showWarning(parameters.get(BackofficeForm.PARAM_MSJ_WARNING).toString());
        }

        try {
            this.setParametersInner(parameters);
        } catch (Exception e) {
            logger.error("Error al inicializar los parametros del formulario", e);
        }
    }

    public abstract void setParametersInner(PageParameters parameters);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Mensajeria

    public void showSuccess(String message) {
        message = this.getFinalMessage(message);
        this.success(message);
    }
    
    public void showWarning(String message) {
        message = this.getFinalMessage(message);
        this.warn(message);
    }
    
    public void showError(String message) {
        message = this.getFinalMessage(message);
        this.error(message);
    }

    public void showError(Throwable exception) {
        String message = this.getFinalMessage(exception.getMessage());
        this.error(message);
    }

    protected String getFinalMessage(String messageOrKey) {
        if (messageOrKey == null) {
            // Si la clave o el mensaje es nulo, ha ocurrido un error general.
            messageOrKey = "Error.General";
        }

        String message = null;
    	try {
    		message = this.getString(messageOrKey.replace("\n", ""));
    	} catch (MissingResourceException ex) {
    	    logger.error(ex.getMessage(), ex);
        }
    	
        if (message != null) {
            return message;
        } else {
            return messageOrKey;
        }
    }

    protected PageParameters buscarParametrosDeFiltro(PageParameters parameters) {
        PageParameters parametrosFiltro = null;

        if (parameters != null) {
            parametrosFiltro = new PageParameters();

            for (String param : parameters.getNamedKeys()) {
                if (param.startsWith("filtro_")) {
                    parametrosFiltro.add(param, parameters.get(param));
                }
            }
        }

        return (parametrosFiltro);
    }

    protected FormComponent<String> createComponent(String fieldName, String fieldFeedbackName, String title) {
        final FormComponent<String> cmp = new TextField<String>(fieldName).setRequired(true);
        cmp.add(StringValidator.maximumLength(200));
        this.add(new ComponentCustomFeedbackPanel(fieldFeedbackName, new ComponentFeedbackMessageFilter(cmp)));
        cmp.setLabel(new Model(title));
        return cmp;
    }

    public ModoOperacion getModo() {
        return modo;
    }

    protected <T extends BaseNamedEntity> T getEntity(List<T> listEntities, String name) {
        if (listEntities != null && !listEntities.isEmpty()) {
            for (T entity : listEntities) {
                if (entity.getNombre().equals(name)) {
                    return entity;
                }
            }
        }
        return null;
    }

    protected AlertaRepository obtenerAlertaService() {
        return ApplicationContextProvider.getBean("alertaRepositoryImpl", AlertaRepository.class);
    }

    protected ProductorRepository obtenerProductorService() {
        return ApplicationContextProvider.getBean("productorRepositoryImpl", ProductorRepository.class);
    }

    protected NovedadRepository obtenerNovedadService() {
        return ApplicationContextProvider.getBean("novedadRepositoryImpl", NovedadRepository.class);
    }

    protected TopicoProductorRepository obtenerTopicoProductorService() {
        return ApplicationContextProvider.getBean("topicoProductorRepositoryImpl", TopicoProductorRepository.class);
    }

    protected EntregaRepository obtenerEntregaService() {
        return ApplicationContextProvider.getBean("entregaRepositoryImpl", EntregaRepository.class);
    }

    protected TopicoRepository obtenerTopicoService() {
        return ApplicationContextProvider.getBean("topicoRepositoryImpl", TopicoRepository.class);
    }

    protected SuscriptorRepository obtenerSuscriptorService() {
        return ApplicationContextProvider.getBean("suscriptorRepositoryImpl", SuscriptorRepository.class);
    }

    protected TopicoSuscriptorRepository obtenerTopicoSuscriptorService() {
        return ApplicationContextProvider.getBean("topicoSuscriptorRepositoryImpl", TopicoSuscriptorRepository.class);
    }

    protected UserService obtenerUsuarioService() {
        return ApplicationContextProvider.getBean("userServiceImpl", UserService.class);
    }

    protected FilterService getFilterService() {
        return ApplicationContextProvider.getBean("filterServiceImpl", FilterService.class);
    }

    protected FilterTopicService getFilterTopicService() {
        return ApplicationContextProvider.getBean("filterTopicServiceImpl", FilterTopicService.class);
    }
}
