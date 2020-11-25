package uy.gub.agesic.pdi.pys.backoffice.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroTopicoDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.backend.service.TopicoService;
import uy.gub.agesic.pdi.pys.domain.Topico;

import java.util.List;

@Service
public class TopicoRepositoryImpl implements TopicoRepository {

    private static Logger logger = LoggerFactory.getLogger(TopicoRepositoryImpl.class);

    private TopicoService topicoService;

    @Autowired
    public TopicoRepositoryImpl(TopicoService topicoService){ this.topicoService = topicoService; }


    @Override
    public ResultadoPaginadoDTO<Topico> buscarTopicos(FiltroTopicoDTO filtro) throws PSException {
        try {
            return topicoService.buscarTopicosFiltro(filtro);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new PSException("No ha sido posible recuperar los t\u00F3picos", ex.toString());
        }
    }

    @Override
    public Topico buscarTopico(String nombre) throws PSException {
        try {
            return topicoService.buscar(nombre);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new PSException("No ha sido posible recuperar los datos del t\u00F3pico: " + nombre, ex.toString());

        }
    }

    @Override
    public void crearTopico(Topico topico) throws PSException {
        try {
            topicoService.crear(topico);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new PSException("No ha sido posible crear el t\u00F3pico: " + topico.getNombre(), ex.toString());

        }
    }

    @Override
    public void eliminarTopicos(List<String> nombres) throws PSException {
        for (String nombre : nombres) {
            try {
                //La eliminación es lógica, pasa a estado deshabilitado
                topicoService.deshabilitar(nombre);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                throw new PSException("No ha sido posible eliminar el t\u00F3pico: " + nombre, ex.toString());

            }
        }
    }

    @Override
    public void modificarTopico(Topico topico) throws PSException {
        try {
            topicoService.modificar(topico);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new PSException("No ha sido posible modificar el t\u00F3pico: " + topico.getNombre(), ex.toString());

        }
    }

    @Override
    public List<Topico> getAll() throws PSException {
        try {
            return topicoService.getAll();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new PSException("No ha sido posible recuperar los t\u00F3pics", ex.toString());
        }
    }

}
