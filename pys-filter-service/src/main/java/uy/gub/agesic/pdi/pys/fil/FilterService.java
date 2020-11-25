package uy.gub.agesic.pdi.pys.fil;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import uy.gub.agesic.pdi.common.logging.PDIHostName;
import uy.gub.agesic.pdi.pys.backend.repository.FilterTopicRepository;
import uy.gub.agesic.pdi.pys.backend.repository.NovedadRepository;
import uy.gub.agesic.pdi.pys.backend.repository.TopicoSuscriptorRepository;
import uy.gub.agesic.pdi.pys.domain.*;
import uy.gub.agesic.pdi.pys.fil.error.EvaluationContinueException;
import uy.gub.agesic.pdi.pys.fil.error.EvaluationFailureException;
import uy.gub.agesic.pdi.pys.fil.evaluator.FactorEvaluator;
import uy.gub.agesic.pdi.pys.fil.evaluator.ProcessEvaluator;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilterService {
    private final FilterTopicRepository filterTopicRepository;
    private final TopicoSuscriptorRepository topicoSuscriptorRepository;
    private final NovedadRepository novedadRepository;
    private final Map<Factor.Type, FactorEvaluator> map;
    private ProcessEvaluator processEvaluator;

    public FilterService(FilterTopicRepository filterTopicRepository, List<FactorEvaluator> factorEvaluators,
                         TopicoSuscriptorRepository topicoSuscriptorRepository, NovedadRepository novedadRepository,
                         ProcessEvaluator processEvaluator) {
        this.filterTopicRepository = filterTopicRepository;
        this.topicoSuscriptorRepository = topicoSuscriptorRepository;
        this.novedadRepository = novedadRepository;
        this.processEvaluator = processEvaluator;
        map = new EnumMap<>(Factor.Type.class);
        for (FactorEvaluator factorEvaluator : factorEvaluators) {
            map.put(factorEvaluator.getType(), factorEvaluator);
        }
    }

    public List<FilterSubscriber> evaluate(String topicId, String noveltyId) {
        MDC.put("host", PDIHostName.HOST_NAME);
        log.info(String.format("Evaluando topicoId: %s, novedadId: %s", topicId, noveltyId));
        List<FilterSubscriber> result = new ArrayList<>();
        List<FilterTopic> filters = filterTopicRepository.findAllByTopicId(topicId);
        log.debug(String.format("Cantidad de filtros definidos en el topico: %d", filters.size()));

        if (!filters.isEmpty()) {
            Topico topico = filters.get(0).getTopic();
            log.debug(String.format("Obteniendo topico: %s", topico.getNombre()));

            Novedad novedad = novedadRepository.findOne(noveltyId);
            log.debug(String.format("Obteniendo novedad: %s", novedad.getUuid()));

            List<Suscriptor> suscriptors = topicoSuscriptorRepository
                    .findAllByTopicoId(topico.getId())
                    .stream()
                    .map(TopicoSuscriptor::getSuscriptor)
                    .collect(Collectors.toList());
            log.debug(String.format("Cantidad de suscriptores asociados al topico: %d", suscriptors.size()));

            log.info("Evaluando filtros:");
            for (FilterTopic topic : filters) {
                log.debug(
                        String.format(
                                "Datos del la relacion filtro - topico: Topico: %s, Filtro: %s, Tipo: %s",
                                topic.getTopic().getNombre(),
                                topic.getFilter().getName(),
                                topic.getType().toString()
                        )
                );
                if (topic.getFilter().getRules().size() == 0) {
                    log.info(String.format("No hay reglas definidas para el filtro %s", topic.getFilter().getName()));
                    continue;
                }
                boolean needEvaluate = true;
                int sequence = 0;
                if (topic.getType().equals(FilterTopic.Type.SUBSCRIBER)) {
                    log.info("Evaluando relacion de tipo SUSCRIPTOR");
                    int count = 0;
                    for (Suscriptor suscriptor : suscriptors) {
                        try {
                            if (!needEvaluate || evaluateFilter(novedad, topic, suscriptor, sequence)) {
                                log.info("Evaluacion satisfactoria.");
                                result.add(FilterSubscriber.builder()
                                        .reason(topic.getFilter().getName())
                                        .subscriberId(suscriptor.getId())
                                        .build());
                                count++;
                            } else {
                                log.info("Evaluacion no satisfactoria.");
                            }
                        } catch (EvaluationFailureException exception) {
                            log.info(exception.getMessage());
                            break;
                        } catch (EvaluationContinueException exception) {
                            needEvaluate = false;
                            log.info(exception.getMessage());
                            log.info("Evaluacion satisfactoria.");
                            result.add(FilterSubscriber.builder()
                                    .reason(topic.getFilter().getName())
                                    .subscriberId(suscriptor.getId())
                                    .build());
                            count++;
                        }
                        if (count == topic.getMaximumOccurrences() && topic.getMaximumOccurrences() > 0)
                            break;
                        sequence++;
                    }
                } else {
                    log.info("Evaluando relacion de tipo MENSAJE");
                    try {
                        if (evaluateFilter(novedad, topic, null, sequence)) {
                            log.info("Evaluacion satisfactoria.");
                            result.addAll(suscriptors.stream().map(s -> FilterSubscriber.builder()
                                    .reason(topic.getFilter().getName())
                                    .subscriberId(s.getId())
                                    .build()).collect(Collectors.toList()));
                        } else {
                            log.info("Evaluacion no satisfactoria.");
                        }
                    } catch (Exception exception) {
                        log.info(exception.getMessage());
                    }
                }
            }
        }
        return result;
    }

    private boolean evaluateFilter(Novedad novedad, FilterTopic filterTopic, Suscriptor suscriptor, int sequence) throws RuntimeException {
        boolean passed = false;
        if (filterTopic.getFilter().getOperator().equals(Filter.Operator.OR)) {
            passed = this.processEvaluator.evaluate(novedad.getContenido(), filterTopic.getFilter(), suscriptor, Boolean::logicalOr, sequence);
        }
        if (filterTopic.getFilter().getOperator().equals(Filter.Operator.AND)) {
            passed = this.processEvaluator.evaluate(novedad.getContenido(), filterTopic.getFilter(), suscriptor, Boolean::logicalAnd, sequence);
        }
        log.info(String.format("%s [type=%s,occurrences=%d] = %s", filterTopic.getFilter().getName(), filterTopic.getType(), filterTopic.getMaximumOccurrences(), passed));
        return passed;
    }
}
