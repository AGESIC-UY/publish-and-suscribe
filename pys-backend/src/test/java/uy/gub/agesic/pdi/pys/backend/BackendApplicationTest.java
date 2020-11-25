package uy.gub.agesic.pdi.pys.backend;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;
import uy.gub.agesic.pdi.common.exceptions.PDIException;
import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroAlertaConsultaDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroEntregaConsultaDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroNovedadConsultaDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroProductorDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroSuscriptorDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroTopicoDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.backend.service.AlertaService;
import uy.gub.agesic.pdi.pys.backend.service.EntregaService;
import uy.gub.agesic.pdi.pys.backend.service.NovedadService;
import uy.gub.agesic.pdi.pys.backend.service.ProductorService;
import uy.gub.agesic.pdi.pys.backend.service.SuscriptorService;
import uy.gub.agesic.pdi.pys.backend.service.TopicoProductorService;
import uy.gub.agesic.pdi.pys.backend.service.TopicoService;
import uy.gub.agesic.pdi.pys.backend.service.TopicoSuscriptorService;
import uy.gub.agesic.pdi.pys.domain.Alerta;
import uy.gub.agesic.pdi.pys.domain.Entrega;
import uy.gub.agesic.pdi.pys.domain.EstadoEntrega;
import uy.gub.agesic.pdi.pys.domain.Novedad;
import uy.gub.agesic.pdi.pys.domain.Productor;
import uy.gub.agesic.pdi.pys.domain.ReporteEntregas;
import uy.gub.agesic.pdi.pys.domain.Suscriptor;
import uy.gub.agesic.pdi.pys.domain.Topico;
import uy.gub.agesic.pdi.pys.domain.TopicoProductor;
import uy.gub.agesic.pdi.pys.domain.TopicoSuscriptor;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@SpringBootTest(classes = {ProductorService.class})
@ComponentScan(basePackages = {"uy.gub.agesic.pdi.pys", "uy.gub.agesic.pdi.common", "uy.gub.agesic.pdi.services.httpproxy"})
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class BackendApplicationTest {

    public static String topicoName = "TopicoTest";

    public static String topicoNameNoHab = "TopicoTestNoHab";

    public static String productorName = "ProductorTestPub";

    public static String productorNameNoHab = "ProductorTestNoHab";

    public static String productorSinTopicoAsociado = "ProductorSinTopico";

    public static String suscriptorNameNoHab = "SuscriptorNoHab";

    public static String suscriptorPullName = "SuscriptorPull";

    public static String suscriptorPushName = "SuscriptorPush";

    @Autowired
    private AlertaService alertasService;

    @Autowired
    private EntregaService entregaService;

    @Autowired
    private NovedadService novedadService;

    @Autowired
    private ProductorService productorService;

    @Autowired
    private SuscriptorService suscriptorService;

    @Autowired
    private TopicoProductorService topicoProductorService;

    @Autowired
    private TopicoSuscriptorService topicoSuscriptorService;

    @Autowired
    private TopicoService topicoService;

    @Test
    public void createDataBase() throws PSException {

        //Alta Productor Pub
        Productor productorPub = new Productor();
        productorPub.setNombre(productorName);
        productorPub.setHabilitado(true);
        productorPub.setDn("{rol}");

        //Alta Productor No Habilitado
        Productor productorNoHab = new Productor();
        productorNoHab.setNombre(productorNameNoHab);
        productorNoHab.setHabilitado(false);
        productorNoHab.setDn("{rol}");

        //Alta Productor Sin Tópico Asociado
        Productor productorSinTopico = new Productor();
        productorSinTopico.setNombre(productorSinTopicoAsociado);
        productorSinTopico.setHabilitado(true);
        productorSinTopico.setDn("{rol}");

        //Alta Tópico
        Topico topicoTest = new Topico();
        topicoTest.setNamespace("http://novedades.agesic.gub.uy/");
        topicoTest.setHabilitado(true);
        topicoTest.setSoapAction("http://novedades.agesic.gub.uy/");
        topicoTest.setElementoRaiz("publicar");
        topicoTest.setNombre(topicoName);

        //Alta Tópico No Habilitado
        Topico topicoTestNoHab = new Topico();
        topicoTestNoHab.setNamespace("http://novedades.agesic.gub.uy/");
        topicoTestNoHab.setHabilitado(false);
        topicoTestNoHab.setSoapAction("http://novedades.agesic.gub.uy/");
        topicoTestNoHab.setElementoRaiz("publicar");
        topicoTestNoHab.setNombre(topicoNameNoHab);

        //Alta topicoProductor
        TopicoProductor topicoProductor = new TopicoProductor();
        topicoProductor.setProductor(productorPub);
        topicoProductor.setTopico(topicoTest);

        //Alta Suscriptor No Habilitado
        Suscriptor suscriptorNoHab = new Suscriptor();
        suscriptorNoHab.setNombre(suscriptorNameNoHab);
        suscriptorNoHab.setHabilitado(false);
        suscriptorNoHab.setDn("{rol}");

        //Alta Suscriptor PULL
        Suscriptor suscriptorPull = new Suscriptor();
        suscriptorPull.setNombre(suscriptorPullName);
        suscriptorPull.setHabilitado(true);
        suscriptorPull.setDn("{rol}");

        //Alta Suscriptor PUSH
        Suscriptor suscriptorPush = new Suscriptor();
        suscriptorPush.setNombre(suscriptorPushName);
        suscriptorPush.setHabilitado(true);
        suscriptorPush.setDn("{rol}");

        //Alta topicoSuscriptor PULL
        TopicoSuscriptor topicoSuscriptorPull = new TopicoSuscriptor();
        topicoSuscriptorPull.setTopico(topicoTest);
        topicoSuscriptorPull.setSuscriptor(suscriptorPull);
        topicoSuscriptorPull.setDeliveryMode("PULL");

        //Alta topicoSuscriptor PUSH
        TopicoSuscriptor topicoSuscriptorPush = new TopicoSuscriptor();
        topicoSuscriptorPush.setTopico(topicoTest);
        topicoSuscriptorPush.setSuscriptor(suscriptorPush);
        topicoSuscriptorPush.setDeliveryAddr("http://localhost:23000/push");
        topicoSuscriptorPush.setDeliveryMode("PUSH");
        topicoSuscriptorPush.setDeliveryWsaTo("http://deliverywsato");

        if (productorService.buscar(productorNoHab.getNombre()) == null) {
            productorService.crear(productorNoHab);
        }
        productorNoHab = productorService.buscar(productorNoHab.getNombre());

        if (productorService.buscar(productorSinTopico.getNombre()) == null) {
            productorService.crear(productorSinTopico);
        }
        productorSinTopico = productorService.buscar(productorSinTopico.getNombre());

        if (productorService.buscar(productorPub.getNombre()) == null) {
            productorService.crear(productorPub);
        }
        productorPub = productorService.buscar(productorPub.getNombre());

        if (topicoService.buscar(topicoTest.getNombre()) == null) {
            topicoService.crear(topicoTest);
        }
        topicoTest = topicoService.buscar(topicoTest.getNombre());

        if (topicoService.buscar(topicoTestNoHab.getNombre()) == null) {
            topicoService.crear(topicoTestNoHab);
        }
        topicoTestNoHab = topicoService.buscar(topicoTestNoHab.getNombre());

        topicoProductor.setTopico(topicoTest);
        topicoProductor.setProductor(productorPub);
        if (topicoProductorService.buscarTopicoProductor(topicoProductor.getTopico().getNombre(),
                topicoProductor.getProductor().getNombre()) == null) {
            topicoProductorService.crear(topicoProductor);
        }
        topicoProductor = topicoProductorService.buscarTopicoProductor(topicoProductor.getTopico().getNombre(),
                topicoProductor.getProductor().getNombre());

        if (suscriptorService.buscar(suscriptorNoHab.getNombre()) == null) {
            suscriptorService.crear(suscriptorNoHab);
        }
        suscriptorNoHab = suscriptorService.buscar(suscriptorNoHab.getNombre());

        if (suscriptorService.buscar(suscriptorPull.getNombre()) == null) {
            suscriptorService.crear(suscriptorPull);
        }
        suscriptorPull = suscriptorService.buscar(suscriptorPull.getNombre());

        if (suscriptorService.buscar(suscriptorPush.getNombre()) == null) {
            suscriptorService.crear(suscriptorPush);
        }
        suscriptorPush = suscriptorService.buscar(suscriptorPush.getNombre());

        topicoSuscriptorPull.setTopico(topicoTest);
        topicoSuscriptorPull.setSuscriptor(suscriptorPull);

        topicoSuscriptorPush.setTopico(topicoTest);
        topicoSuscriptorPush.setSuscriptor(suscriptorPush);

        if (topicoSuscriptorService.buscarTopicoSuscriptor(topicoSuscriptorPull.getSuscriptor().getNombre(),
                topicoSuscriptorPull.getTopico().getNombre()) == null) {
            topicoSuscriptorService.crear(topicoSuscriptorPull);
        }
        topicoSuscriptorPull = topicoSuscriptorService.buscarTopicoSuscriptor(topicoSuscriptorPull.getSuscriptor().getNombre(),
                topicoSuscriptorPull.getTopico().getNombre());

        if (topicoSuscriptorService.buscarTopicoSuscriptor(topicoSuscriptorPush.getSuscriptor().getNombre(),
                topicoSuscriptorPush.getTopico().getNombre()) == null) {
            topicoSuscriptorService.crear(topicoSuscriptorPush);
        }
        topicoSuscriptorPush = topicoSuscriptorService.buscarTopicoSuscriptor(topicoSuscriptorPush.getSuscriptor().getNombre(),
                topicoSuscriptorPush.getTopico().getNombre());

        productorService.deshabilitarProductor(productorName);
        Productor productorTest = productorService.buscar(productorName);
        Assert.assertEquals(Boolean.FALSE, productorTest.getHabilitado());

        productorService.habilitarProductor(productorName);
        productorTest = productorService.buscar(productorName);
        Assert.assertEquals(Boolean.TRUE, productorTest.getHabilitado());

        //Buscar suscriptor creado
        Suscriptor suscriptorTest = this.suscriptorService.buscar(suscriptorPullName);

        //Validar atributos
        Assert.assertNotNull(suscriptorTest.getId());
        Assert.assertNotNull(suscriptorTest.getFechaCreacion());
        Assert.assertEquals(suscriptorTest.getNombre(), suscriptorPullName);
        Assert.assertEquals("{rol}", suscriptorTest.getDn());
        Assert.assertEquals(Boolean.TRUE, suscriptorTest.getHabilitado());

        suscriptorService.deshabilitarSuscriptor(suscriptorPullName);
        suscriptorTest = suscriptorService.buscar(suscriptorPullName);
        Assert.assertEquals(Boolean.FALSE, suscriptorTest.getHabilitado());

        suscriptorService.habilitarSuscriptor(suscriptorPullName);
        suscriptorTest = suscriptorService.buscar(suscriptorPullName);
        Assert.assertEquals(Boolean.TRUE, suscriptorTest.getHabilitado());

    }

    @Test
    public void testDTO() throws PSException, PDIException, ParseException {
        //------------------ Suscriptor
        FiltroSuscriptorDTO filtro = new FiltroSuscriptorDTO(0, 100);
        ResultadoPaginadoDTO<Suscriptor> resultado = this.suscriptorService.buscarSuscriptoresFiltro(filtro);
        Assert.assertNotNull(resultado);

        filtro.setNombre(suscriptorPushName);
        resultado = this.suscriptorService.buscarSuscriptoresFiltro(filtro);
        Assert.assertNotNull(resultado);
        Assert.assertEquals(1, resultado.getTotalTuplas().longValue());

        Suscriptor susc = this.suscriptorService.buscar(suscriptorPushName);
        Assert.assertNotNull(susc);
        Assert.assertEquals(suscriptorPushName, susc.getNombre());

        List<Suscriptor> suscriptores = suscriptorService.getAll();
        Assert.assertNotNull(suscriptores);

        suscriptores = suscriptorService.obtenerHabilitados();
        Assert.assertNotNull(suscriptores);

        //------------------ Productor
        FiltroProductorDTO filtroP = new FiltroProductorDTO(0, 100);
        ResultadoPaginadoDTO<Productor> resultadoP = productorService.buscarProductoresFiltro(filtroP);
        Assert.assertNotNull(resultadoP);

        filtroP.setNombre(productorName);
        resultadoP = productorService.buscarProductoresFiltro(filtroP);
        Assert.assertNotNull(resultadoP);
        Assert.assertEquals(1, resultadoP.getTotalTuplas().longValue());

        Productor productor = productorService.buscar(productorName);
        Assert.assertNotNull(productor);
        Assert.assertEquals(productorName, productor.getNombre());

        List<Productor> productores = productorService.getAll();
        Assert.assertNotNull(productores);

        productores = productorService.obtenerHabilitados();
        Assert.assertNotNull(productores);

        //------------------ Topico
        FiltroTopicoDTO filtroT = new FiltroTopicoDTO(0, 100);
        ResultadoPaginadoDTO<Topico> resultadoT = topicoService.buscarTopicosFiltro(filtroT);
        Assert.assertNotNull(resultadoT);

        filtroT.setNombre(topicoName);
        filtroT.setHabilitado(true);
        resultadoT = topicoService.buscarTopicosFiltro(filtroT);
        Assert.assertNotNull(resultadoT);
        Assert.assertEquals(1, resultadoT.getTotalTuplas().longValue());

        Topico topico = topicoService.buscar(topicoName);
        Assert.assertNotNull(topico);
        Assert.assertEquals(topicoName, topico.getNombre());

        List<Topico> topicos = topicoService.getAll();
        Assert.assertNotNull(topicos);

        topicoService.habilitar(topicoName);
        //------------------ Alerta
        FiltroAlertaConsultaDTO filtroAlertaConsultaDTO = new FiltroAlertaConsultaDTO(0, 100);
        ResultadoPaginadoDTO<Alerta> resultadoAlertas = alertasService.buscarAlertaFiltro(filtroAlertaConsultaDTO);
        Assert.assertNotNull(resultadoAlertas);

        //------------------ Novedad
        Novedad novedad = new Novedad();
        novedad.setFecha(new Date());
        novedad.setContenido("<xml/>");
        novedad.setProductor(productor);
        novedad.setUuid("" + UUID.randomUUID());
        novedad.setWsaMessageId("testMessageID");
        novedad.setWsaRelatesTo("testRelatesTo");
        novedad.setTopico(topico);
        novedadService.registrar(novedad);

        novedad = novedadService.buscarNovedad(novedad.getUuid());
        Assert.assertNotNull(novedad);

        FiltroNovedadConsultaDTO filtroN = new FiltroNovedadConsultaDTO(0, 100);
        ResultadoPaginadoDTO<Novedad> resultadoN = novedadService.buscarNovedadesFiltro(filtroN);
        Assert.assertNotNull(resultadoN);

        //------------------ Entrega
        entregaService.crearColeccion(topicoName, suscriptorPullName);

        Entrega entrega = new Entrega();
        entrega.setEstado(EstadoEntrega.PENDIENTE.name());
        entrega.setFechaCreado(new Date());
        entrega.setNovedad(novedad);
        entrega.setUuid(novedad.getUuid());
        entrega.setSuscriptor(susc);
        entregaService.upsert(entrega);

        List<Entrega> entregas = entregaService.buscarEntregasNovedad(novedad.getUuid());
        Assert.assertNotNull(entregas);
        Assert.assertEquals(1, entregas.size());

        entrega = entregaService.buscarPrimera(susc.getNombre(), topico.getNombre());
        Assert.assertNotNull(entrega);

        FiltroEntregaConsultaDTO filtroE = new FiltroEntregaConsultaDTO(0, 100);
        filtroE.setTopico(topico);
        filtroE.setEstado(EstadoEntrega.PENDIENTE.name());
        filtroE.setSuscriptor(susc);
        ResultadoPaginadoDTO<ReporteEntregas> reporteEntregas= entregaService.reporteEntregas(filtroE);
        Assert.assertNotNull(reporteEntregas);
        Assert.assertEquals(1, reporteEntregas.getTotalTuplas().longValue());

        ResultadoPaginadoDTO<Entrega> resultadoEntrega = entregaService.buscarEntregaFiltro(filtroE);
        Assert.assertNotNull(resultadoEntrega);
    }

}
