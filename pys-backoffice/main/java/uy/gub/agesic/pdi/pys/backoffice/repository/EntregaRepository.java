package uy.gub.agesic.pdi.pys.backoffice.repository;

import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroEntregaConsultaDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.domain.Entrega;
import uy.gub.agesic.pdi.pys.domain.ReporteEntregas;
import uy.gub.agesic.pdi.pys.domain.Suscriptor;
import uy.gub.agesic.pdi.pys.domain.Topico;

import java.util.List;

public interface EntregaRepository {

    void cancelar(String idEntrega, String suscriptor, String topico) throws PSException;

    void reenviar(String idEntrega, String suscriptor, String topico) throws PSException;

    Entrega buscarEntrega(String idEntrega, String suscriptor, String topico) throws PSException;

    List<Entrega> buscarEntregasNovedad(String uuidNovedad) throws PSException;

    ResultadoPaginadoDTO<Entrega> buscarEntregaFiltro(FiltroEntregaConsultaDTO filtro) throws PSException;

    ResultadoPaginadoDTO<ReporteEntregas> reporteEntregas(FiltroEntregaConsultaDTO filtro) throws PSException;

    void cancelarEntregasTopicoSuscriptor(Suscriptor suscriptor, Topico topico) throws PSException;

    void crearColeccion(String topico, String suscriptor) throws PSException;

}
