package uy.gub.agesic.pdi.pys.push.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class PushController {

    private PushThreadsController pushThreadsController;

    @Autowired
    public PushController(PushThreadsController pushThreadsController) {
        this.pushThreadsController = pushThreadsController;
    }

    @RequestMapping(value = "/cancelarTS/{topico}/{suscriptor}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public void cancelarTS(@PathVariable("suscriptor") String suscriptor, @PathVariable("topico") String topico) {
        pushThreadsController.interruptTS(suscriptor, topico);
    }

    @RequestMapping(value = "/pushTS/{topico}/{suscriptor}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public void pushTS(@PathVariable("suscriptor") String suscriptor, @PathVariable("topico") String topico) {
        pushThreadsController.pushTS(suscriptor, topico);
    }

    @RequestMapping(value = "/cancelarEntrega/{topico}/{suscriptor}/{idEntrega}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    void cancelarEntrega(@PathVariable("suscriptor") String suscriptor, @PathVariable("topico") String topico, @PathVariable("idEntrega") String idEntrega) {
        pushThreadsController.cancelTS(suscriptor, topico, idEntrega);
    }

}
