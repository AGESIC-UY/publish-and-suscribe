package uy.gub.agesic.pdi.pys.push;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import uy.gub.agesic.pdi.pys.backend.service.EntregaService;
import uy.gub.agesic.pdi.pys.domain.Entrega;
import uy.gub.agesic.pdi.pys.domain.EstadoEntrega;
import uy.gub.agesic.pdi.pys.push.controller.PushEndpoint;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SuppressWarnings("squid:S2925")
public class PushServiceApplicationTest {

    public static String topicoName = "TopicoTest";

    public static String suscriptorPushName = "SuscriptorPush";

    @Autowired
    private EntregaService entregaService;

    @Test
    public void testPush() throws Exception {

        Entrega entrega = entregaService.buscarPrimera(suscriptorPushName, topicoName);
        Assert.assertNotNull(entrega);
        Assert.assertTrue(entrega.getEstado().equals(EstadoEntrega.PENDIENTE.name()));

        entregaService.cancelar(entrega);
        entrega = entregaService.buscarEntrega(entrega.getId(), suscriptorPushName, topicoName);
        Assert.assertNotNull(entrega);
        Assert.assertTrue(entrega.getEstado().equals(EstadoEntrega.CANCELADO.name()));

        entrega = entregaService.buscarPrimera(suscriptorPushName, topicoName);
        Assert.assertNotNull(entrega);
        Assert.assertTrue(entrega.getEstado().equals(EstadoEntrega.PENDIENTE.name()));

        System.setProperty("application.push.ts.whiteList", "TopicoTest_SuscriptorPush");
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        PushEndpoint endpoint = new PushEndpoint();

        String req = "";

        request.setContent(req.getBytes());
        endpoint.doPost(request, response);
        String xml = response.getContentAsString();
        Thread.sleep(60000);

        Entrega entregaEnviada = entregaService.buscarEntrega(entrega.getId(), suscriptorPushName, topicoName);

        Assert.assertNotNull(entregaEnviada);
        Assert.assertTrue(entregaEnviada.getEstado().equals(EstadoEntrega.ENVIADO.name()));

        entregaService.cancelar(entregaEnviada);
        entregaEnviada = entregaService.buscarEntrega(entregaEnviada.getId(), suscriptorPushName, topicoName);

        Assert.assertNotNull(entregaEnviada);
        Assert.assertTrue(entregaEnviada.getEstado().equals(EstadoEntrega.ENVIADO.name()));

    }

}
