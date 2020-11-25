package uy.gub.agesic.pdi.pys.backoffice.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uy.gub.agesic.pdi.common.exceptions.PDIException;
import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroEntregaConsultaDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.backend.service.EntregaService;
import uy.gub.agesic.pdi.pys.backoffice.integration.PushService;
import uy.gub.agesic.pdi.pys.domain.Entrega;
import uy.gub.agesic.pdi.pys.domain.EstadoEntrega;
import uy.gub.agesic.pdi.pys.domain.ReporteEntregas;
import uy.gub.agesic.pdi.pys.domain.Suscriptor;
import uy.gub.agesic.pdi.pys.domain.Topico;

import java.util.List;

@Service
public class EntregaRepositoryImpl implements EntregaRepository {

    private static Logger logger = LoggerFactory.getLogger(EntregaRepositoryImpl.class);

    private static final String CANCEL_MESSAGE = "Sólo se pueden cancelar entregas en estado PENDIENTE";
    private EntregaService entregaService;

    private PushService pushService;

    @Autowired
    public EntregaRepositoryImpl(EntregaService entregaService, PushService pushService) {
        this.entregaService = entregaService;
        this.pushService = pushService;
    }

    @Override
    public void cancelar(String idEntrega, String suscriptor, String topico) throws PSException {
        try {
            Entrega entrega = entregaService.buscarEntrega(idEntrega, suscriptor, topico);
            if (!entrega.getEstado().equals(EstadoEntrega.PENDIENTE.name())) {
                throw new PDIException(CANCEL_MESSAGE);
            }

            int result = entregaService.cancelar(entrega);
            if (result == 0) {
                throw new PDIException(CANCEL_MESSAGE);
            }
        } catch (Exception e) {
            throw new PSException(e);
        }
        try {
            pushService.cancelarTS(suscriptor, topico);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }

    }

    @Override
    public void reenviar(String idEntrega, String suscriptor, String topico) throws PSException {
        try {
            pushService.pushTS(suscriptor, topico);
        } catch (PDIException e) {
            throw new PSException(e);
        }
    }

    @Override
    public Entrega buscarEntrega(String idEntrega, String suscriptor, String topico) throws PSException {
        return entregaService.buscarEntrega(idEntrega, suscriptor, topico);
    }

    @Override
    public List<Entrega> buscarEntregasNovedad(String uuidNovedad) throws PSException {
        return entregaService.buscarEntregasNovedad(uuidNovedad);
    }

    @Override
    public ResultadoPaginadoDTO<Entrega> buscarEntregaFiltro(FiltroEntregaConsultaDTO filtro) throws PSException {
        return entregaService.buscarEntregaFiltro(filtro);
    }

    @Override
    public ResultadoPaginadoDTO<ReporteEntregas> reporteEntregas(FiltroEntregaConsultaDTO filtro) throws PSException {
        return entregaService.reporteEntregas(filtro);
    }

    @Override
    public void cancelarEntregasTopicoSuscriptor(Suscriptor suscriptor, Topico topico) throws PSException {
        entregaService.cancelarEntregasTopicoSuscriptor(suscriptor, topico);
    }

    @Override
    public void crearColeccion(String topico, String suscriptor) throws PSException {
        entregaService.crearColeccion(topico, suscriptor);
    }

}
