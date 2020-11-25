package uy.gub.agesic.pdi.pys.backoffice.integration;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uy.gub.agesic.pdi.common.exceptions.PDIException;

@FeignClient("push-service")
public interface PushService {

    @RequestMapping(value = "/pushTS/{topico}/{suscriptor}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    void pushTS(@PathVariable("suscriptor") String suscriptor, @PathVariable("topico") String topico) throws PDIException;

    @RequestMapping(value = "/cancelarTS/{topico}/{suscriptor}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    void cancelarTS(@PathVariable("suscriptor") String suscriptor, @PathVariable("topico") String topico) throws PDIException;

    @RequestMapping(value = "/cancelarEntrega/{topico}/{suscriptor}/{idEntrega}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    void cancelarEntrega(@PathVariable("suscriptor") String suscriptor, @PathVariable("topico") String topico, @PathVariable("idEntrega") String idEntrega) throws PDIException;
}
