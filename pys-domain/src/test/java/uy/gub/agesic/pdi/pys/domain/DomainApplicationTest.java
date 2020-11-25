package uy.gub.agesic.pdi.pys.domain;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.UUID;

@SpringBootTest(classes = {Alerta.class})
@ComponentScan(basePackages = {"uy.gub.agesic.pdi.pys.domain"})
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class DomainApplicationTest {

    public static String topicoName = "TopicoTest";

    public static String topicoNameNoHab = "TopicoTestNoHab";

    public static String productorName = "ProductorTestPub";

    public static String productorNameNoHab = "ProductorTestNoHab";

    public static String productorSinTopicoAsociado = "ProductorSinTopico";

    public static String suscriptorNameNoHab = "SuscriptorNoHab";

    public static String suscriptorPullName = "SuscriptorPull";

    public static String suscriptorPushName = "SuscriptorPush";

    @Test
    public void createDataBase() {

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

        Suscriptor suscriptorTest = suscriptorPull;

        //Validar atributos
        Assert.assertNull(suscriptorTest.getId());
        Assert.assertNotNull(suscriptorTest.getNombre());
        Assert.assertEquals(suscriptorTest.getNombre(), suscriptorPullName);
        Assert.assertEquals("{rol}", suscriptorTest.getDn());
        Assert.assertEquals(Boolean.TRUE, suscriptorTest.getHabilitado());

        //------------------ Novedad
        Novedad novedad = new Novedad();
        novedad.setFecha(new Date());
        novedad.setContenido("<xml/>");
        novedad.setProductor(productorPub);
        novedad.setUuid("" + UUID.randomUUID());
        novedad.setWsaMessageId("testMessageID");
        novedad.setWsaRelatesTo("testRelatesTo");
        novedad.setTopico(topicoTest);

        //------------------ Entrega
        Entrega entrega = new Entrega();
        entrega.setEstado(EstadoEntrega.PENDIENTE.name());
        entrega.setFechaCreado(new Date());
        entrega.setNovedad(novedad);
        entrega.setUuid(novedad.getUuid());
        entrega.setSuscriptor(suscriptorTest);

    }

}
