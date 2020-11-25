package uy.gub.agesic.pdi.pys.backend.service;

import uy.gub.agesic.pdi.common.exceptions.PDIException;
import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroSuscriptorDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.domain.Suscriptor;
import uy.gub.agesic.pdi.pys.domain.Topico;

import java.util.List;

public interface SuscriptorService {

    void crear(Suscriptor item) throws PSException;

    void modificar(Suscriptor item) throws PSException;

    void eliminar(String nombre) throws PSException;

    void habilitarSuscriptor (String nombre) throws PSException;

    void deshabilitarSuscriptor (String nombre) throws PSException;

    Suscriptor buscar(String nombre) throws PSException;

    Suscriptor searchById(String id);

    List<Suscriptor> getAll() throws PSException;

    List<Suscriptor> obtenerHabilitados() throws PSException;

    Boolean existeTopicoSuscriptorPull(Topico topico, Suscriptor suscriptor) throws PSException;

    ResultadoPaginadoDTO<Suscriptor> buscarSuscriptoresFiltro(FiltroSuscriptorDTO filtro) throws PSException, PDIException;

}
