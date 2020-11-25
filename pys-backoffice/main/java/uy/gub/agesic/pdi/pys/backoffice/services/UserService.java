package uy.gub.agesic.pdi.pys.backoffice.services;

import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;

import uy.gub.agesic.pdi.pys.backend.dtos.FiltroUsuarioDTO;
import uy.gub.agesic.pdi.pys.backoffice.utiles.exceptions.BackofficeException;
import uy.gub.agesic.pdi.pys.domain.Usuario;

import java.io.Serializable;
import java.util.List;

public interface UserService extends Serializable {

    boolean authenticate(String username, String password) throws BackofficeException;

    ResultadoPaginadoDTO<Usuario> buscarUsuarios(FiltroUsuarioDTO filtro) throws BackofficeException;

    Usuario obtenerUsuario(String login) throws BackofficeException;

    void eliminarUsuarios (List<String> logins) throws BackofficeException;

    void crearUsuario (Usuario usuario) throws BackofficeException;

    void modificarUsuario(Usuario usuario) throws BackofficeException;

    boolean cambiarContrasena(String login, String oldPassword, String newPassword) throws BackofficeException;

    String permisoUsuario(String login) throws BackofficeException;

}
