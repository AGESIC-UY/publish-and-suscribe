package uy.gub.agesic.pdi.pys.fil.web.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uy.gub.agesic.pdi.pys.domain.FilterSubscriber;
import uy.gub.agesic.pdi.pys.fil.FilterService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("rest")
public class EvaluatorResource {
    private FilterService filterService;

    public EvaluatorResource(FilterService filterService) {
        this.filterService = filterService;
    }

    @PostMapping("/filtersubscribelist/{topicId}/{noveltyId}")
    public ResponseEntity<List<FilterSubscriber>> getFilterSubscribeList(@PathVariable String topicId, @PathVariable String noveltyId) {
        log.debug("REST request to retrieve filter subscriber list by topic: {%s} and novelty: {%s}", topicId, noveltyId);
        List<FilterSubscriber> filterSubscriberList = this.filterService.evaluate(topicId, noveltyId);
        if (filterSubscriberList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(filterSubscriberList);
    }
}
