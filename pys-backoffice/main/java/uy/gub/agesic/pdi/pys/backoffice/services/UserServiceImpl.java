package uy.gub.agesic.pdi.pys.backoffice.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uy.gub.agesic.pdi.common.logging.Loggable;
import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroUsuarioDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.backoffice.repository.UsuarioRepository;
import uy.gub.agesic.pdi.pys.backoffice.utiles.crypto.JasyptUtil;
import uy.gub.agesic.pdi.pys.backoffice.utiles.exceptions.BackofficeException;
import uy.gub.agesic.pdi.pys.domain.Usuario;

import java.util.List;

@Service
@SuppressWarnings("squid:S1948")
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private UsuarioRepository usuarioRepository;

    @Autowired
    public UserServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Loggable
    public boolean authenticate(String username, String password) throws BackofficeException {
        try {
            Usuario usuario = usuarioRepository.getUsuario(username);

            return JasyptUtil.matchesPassword(password, usuario.getPassword());
        } catch (Exception ex) {
            logger.error("Ha ocurrido un error autenticando al usuario: " + username, ex);
            throw new BackofficeException("Credenciales invalidas");
        }
    }

    @Override
    @Loggable
    public ResultadoPaginadoDTO<Usuario> buscarUsuarios(FiltroUsuarioDTO filtro) throws BackofficeException {
        try {
            return usuarioRepository.getUsuarios(filtro);
        } catch (PSException ex) {
            throw new BackofficeException(ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new BackofficeException("No ha sido posible recuperar los usuarios indicados", ex);
        }
    }


    @Override
    @Loggable
    public Usuario obtenerUsuario(String login) throws BackofficeException {
        try {
            return usuarioRepository.getUsuario(login);
        } catch (Exception ex) {
            throw new BackofficeException("No ha sido posible obtener el usuario con login: " + login);
        }
    }

    @Override
    @Loggable
    public void crearUsuario(Usuario usuario) throws BackofficeException {
        try {
            String hashPassword = JasyptUtil.hashPassword(usuario.getPassword());
            usuario.setPassword(hashPassword);

            usuarioRepository.insertarUsuario(usuario);
        } catch (Exception ex) {
            throw new BackofficeException("No ha sido posible crear el usuario: " + usuario.getLogin(), ex);
        }
    }

    @Override
    @Loggable
    public void modificarUsuario(Usuario usuario) throws BackofficeException {
        try {
            usuarioRepository.insertarUsuario(usuario);
        } catch (Exception ex) {
            throw new BackofficeException("No ha sido posible actualizar el usuario: " + usuario.getLogin());
        }
    }

    @Override
    @Loggable
    public boolean cambiarContrasena(String login, String oldPassword, String newPassword) throws BackofficeException {
        boolean success = false;

        try {
            Usuario usuario = usuarioRepository.getUsuario(login);

            if (JasyptUtil.matchesPassword(oldPassword, usuario.getPassword())){
                String hashPassword = JasyptUtil.hashPassword(newPassword);
                usuario.setPassword(hashPassword);
                usuarioRepository.insertarUsuario(usuario);
                success = true;
            }
        } catch (Exception ex) {
            logger.error("Ha ocurrido un error al modificar la contraseña del usuario: " + login, ex);
            throw new BackofficeException("Error al modificar conraseña");
        }

        return success;
    }

    @Override
    @Loggable
    public String permisoUsuario(String login) throws BackofficeException {
        try {
            Usuario usuario = usuarioRepository.getUsuario(login);
            return usuario.getPermiso();
        } catch (Exception ex) {
            logger.error("Ha ocurrido un error al obtener el nivel de permiso del usuario: " + login, ex);
            throw new BackofficeException("Error al obtener permiso");
        }
    }

    @Override
    @Loggable
    public void eliminarUsuarios (List<String> logins) throws BackofficeException {
        for (String login : logins) {
            try {
                usuarioRepository.eliminarUsuario(login);
            } catch (Exception ex) {
                throw new BackofficeException("No ha sido posible eliminar el usuario: " + login);
            }
        }
    }

}