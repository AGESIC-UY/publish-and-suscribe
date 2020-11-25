package uy.gub.agesic.pdi.pys.push.send;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import uy.gub.agesic.pdi.pys.push.config.PYSPushProperties;
import uy.gub.agesic.pdi.pys.push.controller.PushThreadsController;

@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

    private PushThreadsController threadsController;

    private PYSPushProperties pysPushProperties;

    @Autowired
    public ApplicationStartup(PushThreadsController threadsController, PYSPushProperties pysPushProperties) {
        this.threadsController = threadsController;
        this.pysPushProperties = pysPushProperties;

    }

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {

        int port = this.pysPushProperties.getMcastPort();
        String ip = this.pysPushProperties.getMcastAddress();
        System.setProperty("jgroups.udp.mcast_port", String.format("%d", port));
        System.setProperty("jgroups.udp.mcast_addr", ip);

        this.threadsController.createEnvironment();

        this.threadsController.startPushServices();
    }
}