package uy.gub.agesic.pdi.pys.backoffice.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.backend.service.TopicoProductorService;
import uy.gub.agesic.pdi.pys.domain.Productor;
import uy.gub.agesic.pdi.pys.domain.TopicoProductor;

import java.util.List;

@Service
public class TopicoProductorRepositoryImpl implements TopicoProductorRepository {

    private TopicoProductorService topicoProductorService;

    @Autowired
    public TopicoProductorRepositoryImpl(TopicoProductorService topicoProductorService){ this.topicoProductorService = topicoProductorService; }

    @Override
    public void crear(TopicoProductor item) throws PSException {
        topicoProductorService.crear(item);
    }

    @Override
    public void eliminar(String topico, String productor) throws PSException {
        topicoProductorService.eliminar(topico, productor);
    }

    @Override
    public List<TopicoProductor> buscarTopicoProductor(String topico) throws PSException {
        return topicoProductorService.buscarTopicoProductor(topico);
    }

    @Override
    public List<Productor> buscarProductoresPorTopico(String topico) throws PSException {
        return topicoProductorService.buscarProductoresPorTopico(topico);
    }

    @Override
    public TopicoProductor buscarTopicoProductor(String topico, String productor) throws PSException {
        return topicoProductorService.buscarTopicoProductor(topico, productor);
    }
}
