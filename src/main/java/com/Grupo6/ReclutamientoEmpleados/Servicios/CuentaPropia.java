package com.Grupo6.ReclutamientoEmpleados.Servicios;

import com.Grupo6.ReclutamientoEmpleados.Entidades.Usuario;
import com.Grupo6.ReclutamientoEmpleados.Repositorios.UsuarioRepositorio;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CuentaPropia {
    private final UsuarioRepositorio usuarios;

    public CuentaPropia(UsuarioRepositorio usuarios) {
        this.usuarios = usuarios;
    }

    public Usuario require(String username, String requestedId) {
        Usuario usuario = usuarios.findByUsername(username);
        if (usuario == null || (StringUtils.hasText(requestedId) && !requestedId.equals(usuario.getId()))) {
            throw new AccessDeniedException("Account unavailable");
        }
        return usuario;
    }

    public <T extends Usuario> T require(String username, String requestedId, Class<T> type) {
        Usuario usuario = require(username, requestedId);
        if (!type.isInstance(usuario)) {
            throw new AccessDeniedException("Profile unavailable");
        }
        return type.cast(usuario);
    }
}
