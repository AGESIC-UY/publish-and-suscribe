package uy.gub.agesic.pdi.pys.backoffice.repository;

import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroProductorDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.domain.Productor;

import java.util.List;

public interface ProductorRepository {

    ResultadoPaginadoDTO<Productor> buscarProductores(FiltroProductorDTO filtro) throws PSException;

    Productor buscarProductor(String nombre) throws PSException;

    void crearProductor(Productor productor) throws PSException;

    void eliminarProductores(List<String> nombres) throws PSException;

    void modificarProductor(Productor productor) throws PSException;

    List<Productor> getAll() throws PSException;

    List<Productor> obtenerHabilitados() throws PSException;
}
