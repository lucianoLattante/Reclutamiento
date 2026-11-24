
package com.Grupo6.ReclutamientoEmpleados.Servicios;

import com.Grupo6.ReclutamientoEmpleados.Entidades.Empleador;
import com.Grupo6.ReclutamientoEmpleados.Enums.Rol;
import com.Grupo6.ReclutamientoEmpleados.Errores.ErrorWeb;
import com.Grupo6.ReclutamientoEmpleados.Repositorios.EmpleadorRepositorio;
import com.Grupo6.ReclutamientoEmpleados.Repositorios.UsuarioRepositorio;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class EmpleadorServicio{
    
    @Autowired
    private EmpleadorRepositorio empleadorRepositorio;
    
    @Autowired 
    private UsuarioServicio usuarioServicio;
    
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    
    @Autowired
    private CuentaPropia cuentaPropia;

    @Transactional
    public void actualizarPerfil(String principal, String id, String nombreEmpresa) throws ErrorWeb {
        Empleador empleador = cuentaPropia.require(principal, id, Empleador.class);
        if (nombreEmpresa == null || nombreEmpresa.trim().isEmpty() || nombreEmpresa.length() > 150) {
            throw new ErrorWeb("Ingrese un nombre de empresa válido");
        }
        empleador.setNombreEmpresa(nombreEmpresa.trim());
    }

    public Empleador findByName(String nombre){
        return empleadorRepositorio.findByUsername(nombre);
    }
}
