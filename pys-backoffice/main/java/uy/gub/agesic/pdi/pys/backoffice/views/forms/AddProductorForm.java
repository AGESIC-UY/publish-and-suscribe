package uy.gub.agesic.pdi.pys.backoffice.views.forms;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.backoffice.config.BackofficeProperties;
import uy.gub.agesic.pdi.pys.backoffice.utiles.enumerados.ModoOperacion;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficePage;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.BotonAccion;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.ComponentCustomFeedbackPanel;
import uy.gub.agesic.pdi.pys.backoffice.views.Error;
import uy.gub.agesic.pdi.pys.backoffice.views.TrabajarConTopico;
import uy.gub.agesic.pdi.pys.domain.Productor;
import uy.gub.agesic.pdi.pys.domain.Topico;
import uy.gub.agesic.pdi.pys.domain.TopicoProductor;

import java.util.ArrayList;
import java.util.List;

public class AddProductorForm extends BackofficeForm {

    private static final long serialVersionUID = 1L;

    private ModoOperacion modoProd;

    private String nombreTopico;

    private Productor productor;

    private String nombreProductor;

    private ArrayList<Productor> listProductores;

    public AddProductorForm () {
        super("addProductorForm");
    }

    @Override
    public void initForm() {

        this.setDefaultModel(new CompoundPropertyModel<>(this));

        super.initForm();

        BackofficeProperties properties = ((BackofficePage) this.getPage()).getProperties();

        this.add(new AddProductorForm.AgregarProductorButton(this.getFinalMessage(properties.getMensajeConfirmacion())));

        this.nombreTopico = RequestCycle.get().getRequest().getRequestParameters().getParameterValue(NOMBRE_TOPICO_PARAM_NAME).toString();

        listProductores = new ArrayList<>();
        buscarProductores();

        final DropDownChoice<Productor> productorChoice = new DropDownChoice<>(PRODUCTOR_NAME, new PropertyModel<>(AddProductorForm.this, PRODUCTOR_NAME), listProductores);
        productorChoice.setNullValid(true);
        productorChoice.setRequired(true);
        productorChoice.setOutputMarkupId(true);
        productorChoice.setOutputMarkupPlaceholderTag(true);
        productorChoice.setLabel(new Model<>("Productor"));
        this.add(productorChoice);

        ComponentCustomFeedbackPanel productorFeedback = new ComponentCustomFeedbackPanel("deliveryProductorFeedback", new ComponentFeedbackMessageFilter(productorChoice));
        productorFeedback.setOutputMarkupId(true);
        productorFeedback.setOutputMarkupPlaceholderTag(true);
        this.add(productorFeedback);

        Label labelProductor = new Label("labelProductor", "Productor *");
        labelProductor.setOutputMarkupId(true);
        labelProductor.setOutputMarkupPlaceholderTag(true);
        labelProductor.setVisible(true);
        this.add(labelProductor);


        // Se agrega el link de volver
        this.add(new AddProductorForm.LinkVolver());

    }

    private class LinkVolver extends StatelessLink {

        private PageParameters parametersCallback;

        public LinkVolver() {
            super(LINK_VOLVER_NAME);
        }

        @Override
        public void onClick() {

            parametersCallback.set(MODE_PARAM_NAME, modo);
            parametersCallback.set(MODE_PROD_PARAM_NAME, modoProd);
            parametersCallback.set(NOMBRE_TOPICO_PARAM_NAME, nombreTopico);
            parametersCallback.set(NOMBRE_PROD_PARAM_NAME, nombreProductor);
            setResponsePage(TrabajarConTopico.class, parametersCallback);
        }

        public PageParameters getParametersCallback() {
            return parametersCallback;
        }

        public void setParametersCallback(PageParameters parametersCallback) {
            this.parametersCallback = parametersCallback;
        }
    }

    @Override
    public void setParametersInner(PageParameters parameters) {

        ((LinkVolver) this.get(LINK_VOLVER_NAME)).setParametersCallback(this.buscarParametrosDeFiltro(parameters));

        if (parameters.getNamedKeys().contains(MODE_PARAM_NAME)) {
            this.modo = parameters.get(MODE_PARAM_NAME).toEnum(ModoOperacion.class);
        }

        if (parameters.getNamedKeys().contains(MODE_PROD_PARAM_NAME)) {
            this.modoProd = parameters.get(MODE_PROD_PARAM_NAME).toEnum(ModoOperacion.class);
        }

        if (parameters.getNamedKeys().contains(NOMBRE_TOPICO_PARAM_NAME)) {
            this.nombreTopico = parameters.get(NOMBRE_TOPICO_PARAM_NAME).toString();
        }

        if (parameters.getNamedKeys().contains(NOMBRE_PROD_PARAM_NAME)) {
            this.nombreProductor = parameters.get(NOMBRE_PROD_PARAM_NAME).toString();
        }

        // Si el modo es consulta, definir componentes como Readonly
        if (this.modoProd.equals(ModoOperacion.CONSULTA)) {
            this.get(PRODUCTOR_NAME).setEnabled(false);
        }

        if (!this.modoProd.equals(ModoOperacion.ALTA)) {
            this.definirValoresIniciales();
        }

        buscarProductores();

    }

    private void definirValoresIniciales() {
        try {

            TopicoProductor topicoProductor = this.obtenerTopicoProductorService().buscarTopicoProductor(this.nombreTopico, this.nombreProductor);
            this.productor = topicoProductor.getProductor();

            listProductores.clear();
            listProductores.add(this.productor);

            this.get(PRODUCTOR_NAME).add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));


        } catch (PSException ex) {
            this.showError(ex);
        }

    }

    private class AgregarProductorButton extends BotonAccion {

        public AgregarProductorButton(String mensajeConfirmacion) {
            super("btnAgregarProductor", Error.class, false, mensajeConfirmacion);
        }

        @Override
        public boolean poseePermisoEjecucion() {
            // El control en este caso ya fue realizado al ingresar a la pagina, segun el modo apropiado
            return true;
        }

        @Override
        public boolean isVisible() {
            return !AddProductorForm.this.modoProd.equals(ModoOperacion.CONSULTA);
        }

        @Override
        public void ejecutar() {
            guardarTopicoProductor();
        }

    }


    protected void guardarTopicoProductor() {
        try {

            if (obtenerTopicoProductorService().buscarTopicoProductor(this.nombreTopico, this.productor.getNombre()) != null) {
                showError("TopicoProductor.existe.relacion");
            } else {

                TopicoProductor topicoProductor = new TopicoProductor();
                Topico topico = obtenerTopicoService().buscarTopico(this.nombreTopico);
                topicoProductor.setTopico(topico);
                topicoProductor.setProductor(this.productor);

                obtenerTopicoProductorService().crear(topicoProductor);

                PageParameters parameters = new PageParameters();
                parameters.add(NOMBRE_TOPICO_PARAM_NAME, this.nombreTopico);
                parameters.add(MODE_PARAM_NAME, this.modo);

                setResponsePage(TrabajarConTopico.class, parameters);

                this.showSuccess("Operacion.exitosa");
            }

        } catch (PSException e) {
            logger.error(e.getMessage(), e);
            showError("TopicoProductor.error.asociar");
        }
    }

    public void buscarProductores() {
        try {
            this.listProductores.clear();

            if (modoProd != null && !modoProd.equals(ModoOperacion.ALTA)) {
                this.listProductores.addAll(obtenerProductorService().getAll());
            } else {

                List<Productor> listaAuxiliarProductores = obtenerProductorService().obtenerHabilitados();

                for (Productor p : listaAuxiliarProductores) {
                    if (obtenerTopicoProductorService().buscarTopicoProductor(this.nombreTopico, p.getNombre()) == null) {
                        this.listProductores.add(p);
                    }
                }

            }

        } catch (PSException e) {
            logger.error(e.getMessage(), e);
        }
    }

}
