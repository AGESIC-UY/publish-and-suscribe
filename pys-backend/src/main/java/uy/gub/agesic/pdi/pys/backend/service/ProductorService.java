package uy.gub.agesic.pdi.pys.backend.service;

import uy.gub.agesic.pdi.common.exceptions.PDIException;
import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;
import uy.gub.agesic.pdi.pys.domain.Productor;
import uy.gub.agesic.pdi.pys.domain.Topico;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroProductorDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;

import java.util.List;

public interface ProductorService {

    void crear(Productor item) throws PSException;

    void modificar(Productor item) throws PSException;

    void eliminar(String nombre) throws PSException;

    void habilitarProductor(String nombre) throws PSException;

    void deshabilitarProductor(String nombre) throws PSException;

    Productor buscar(String nombre) throws PSException;

    Boolean existeTopicoProductor (Topico topico, Productor productor) throws PSException;

    ResultadoPaginadoDTO<Productor> buscarProductoresFiltro(FiltroProductorDTO filtro) throws PSException, PDIException;

    List<Productor> getAll() throws PSException;

    List<Productor> obtenerHabilitados() throws PSException;

}
