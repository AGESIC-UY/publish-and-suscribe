package uy.gub.agesic.pdi.pys.backoffice.repository;

import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroSuscriptorDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.domain.Suscriptor;

import java.util.List;

public interface SuscriptorRepository {

    ResultadoPaginadoDTO<Suscriptor> buscarSuscriptores(FiltroSuscriptorDTO filtro) throws PSException;

    Suscriptor buscarSuscriptor(String nombre) throws PSException;

    void crearSuscriptor(Suscriptor suscriptor) throws PSException;

    void eliminarSuscriptores(List<String> nombres) throws PSException;

    void modificarSuscriptor(Suscriptor suscriptor) throws PSException;

    List<Suscriptor> getAll() throws PSException;

    List<Suscriptor> obtenerHabilitados() throws PSException;
}
