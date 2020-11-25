package uy.gub.agesic.pdi.pys.backoffice.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;
import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroUsuarioDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.backend.service.UsuarioService;
import uy.gub.agesic.pdi.pys.domain.Usuario;


@Service
@ManagedResource
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private static Logger logger = LoggerFactory.getLogger(UsuarioRepositoryImpl.class);

    private UsuarioService usuarioService;

    @Autowired
    public UsuarioRepositoryImpl(UsuarioService usuarioService) {
        this.usuarioService =  usuarioService;
    }

    @Override
    public ResultadoPaginadoDTO<Usuario> getUsuarios(FiltroUsuarioDTO filtro) throws PSException {
        return usuarioService.getUsuarios(filtro);
    }

    @Override
    public Usuario getUsuario(String login) throws PSException {
            return usuarioService.getUsuario(login);

    }

    @Override
    public void insertarUsuario(Usuario usuario) throws PSException {
        try {
            usuarioService.insertarUsuario(usuario);
        } catch (PSException e) {
            String msg = String.format("Ha ocurrido un error insertando un nuevo usuario: %s", usuario.toString());
            logger.error(msg, e);
            throw e;
        } catch (Exception e) {
            String msg = String.format("Ha ocurrido un error insertando un nuevo usuario: %s", usuario.toString());
            logger.error(msg, e);
            throw new PSException(msg, e.toString());
        }
    }

    @Override
    public void eliminarUsuario(String login) throws PSException {
        try {
            usuarioService.eliminarUsuario(login);
        } catch (PSException e) {
            String msg = String.format("Ha ocurrido un error eliminando el usuario con login: %s", login);
            logger.error(msg, e);
            throw e;
        } catch (Exception e) {
            String msg = String.format("Ha ocurrido un error eliminando el usuario con login: %s", login);
            logger.error(msg, e);
            throw new PSException(msg, e.toString());
        }
    }

}