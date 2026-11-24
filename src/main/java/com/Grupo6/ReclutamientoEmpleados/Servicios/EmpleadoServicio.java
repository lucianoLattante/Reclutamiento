package com.Grupo6.ReclutamientoEmpleados.Servicios;

import com.Grupo6.ReclutamientoEmpleados.Entidades.Categoria;
import com.Grupo6.ReclutamientoEmpleados.Entidades.Foto;
import com.Grupo6.ReclutamientoEmpleados.Entidades.Empleado;
import com.Grupo6.ReclutamientoEmpleados.Entidades.Localidad;
import com.Grupo6.ReclutamientoEmpleados.Enums.CarnetConducir;
import com.Grupo6.ReclutamientoEmpleados.Enums.DisponibilidadHoraria;
import com.Grupo6.ReclutamientoEmpleados.Enums.EstudiosAlcanzados;
import com.Grupo6.ReclutamientoEmpleados.Enums.MovilidadPropia;
import com.Grupo6.ReclutamientoEmpleados.Enums.PosibleReubicacion;
import com.Grupo6.ReclutamientoEmpleados.Enums.Rol;
import com.Grupo6.ReclutamientoEmpleados.Enums.Sexo;
import com.Grupo6.ReclutamientoEmpleados.Errores.ErrorWeb;
import com.Grupo6.ReclutamientoEmpleados.Repositorios.EmpleadoRepositorio;
import com.Grupo6.ReclutamientoEmpleados.Repositorios.LocalidadRepositorio;
import com.Grupo6.ReclutamientoEmpleados.Repositorios.UsuarioRepositorio;
import java.io.IOException;
import com.Grupo6.ReclutamientoEmpleados.Formularios.PerfilCandidatoForm;
import com.Grupo6.ReclutamientoEmpleados.Repositorios.CategoriaRepositorio;
import java.util.LinkedHashSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Tomi
 */
@Service
public class EmpleadoServicio {

    @Autowired
    private EmpleadoRepositorio empleadoRepositorio;
    @Autowired
    private FotoServicio fotoServicio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private LocalidadRepositorio localidadRepositorio;
    @Autowired
    private CategoriaRepositorio categoriaRepositorio;
    @Autowired
    private CuentaPropia cuentaPropia;

    public List<Empleado> listAll() {

        return empleadoRepositorio.findAll();

    }

    @Transactional(rollbackFor = { ErrorWeb.class, IOException.class })
    public void actualizarPerfil(String principal, String id, PerfilCandidatoForm form, MultipartFile imagen)
            throws IOException, ErrorWeb {
        Empleado empleado = cuentaPropia.require(principal, id, Empleado.class);
        aplicarPerfil(empleado, form);
        validarPerfil(empleado.getNombre(), empleado.getApellido(), empleado.getFechaNac(), empleado.getEmail(),
                empleado.getSexo(), empleado.getEstudiosAlcanzados(), empleado.getPosiblereubicacion(),
                empleado.getNumeroTelefonico(), empleado.getCategorias(), empleado.getDisponibilidadHoraria(),
                empleado.getCarnetConducir());
        if (imagen != null && !imagen.isEmpty()) {
            empleado.setFoto(fotoServicio.guardar(imagen));
        }
    }

    void aplicarPerfil(Empleado empleado, PerfilCandidatoForm form) throws ErrorWeb {
        if (form.getLocalidad() == null || form.getCategorias() == null || form.getCategorias().isEmpty()) {
            throw new ErrorWeb("Seleccione una localidad y al menos una categoría");
        }
        Localidad localidad = localidadRepositorio.findById(form.getLocalidad())
                .orElseThrow(() -> new ErrorWeb("Ingrese una localidad válida"));
        LinkedHashSet<String> ids = new LinkedHashSet<>(form.getCategorias());
        List<Categoria> categorias = categoriaRepositorio.findAllById(ids);
        if (categorias.size() != ids.size()) {
            throw new ErrorWeb("Seleccione categorías válidas");
        }
        empleado.setNombre(form.getNombre());
        empleado.setApellido(form.getApellido());
        empleado.setFechaNac(form.getFechaNac());
        empleado.setCategorias(categorias);
        empleado.setSexo(form.getSexo());
        empleado.setEstudiosAlcanzados(form.getEstudiosAlcanzados());
        empleado.setDisponibilidadHoraria(form.getDisponibilidadHoraria());
        empleado.setMovilidadPropia(form.getMovilidadPropia());
        empleado.setPosiblereubicacion(form.getPosiblereubicacion());
        empleado.setCarnetConducir(form.getCarnetConducir());
        empleado.setEmail(form.getEmail());
        empleado.setNumeroTelefonico(form.getNumeroTelefonico());
        empleado.setExperienciaLaboral(form.getExperienciaLaboral());
        empleado.setLocalidad(localidad);
    }

    public void validarEmpleado(String username, String password, String password2, String nombre, String apellido,
            Date fechaNac, String email, Sexo sexo, EstudiosAlcanzados estudiosAlcanzados,
            MultipartFile foto, PosibleReubicacion posibleReubicacion, String numeroTelefonico, MovilidadPropia movilidadPropia,
            List<Categoria> categorias, DisponibilidadHoraria disponibilidadHoraria, CarnetConducir carnetConducir
    ) throws ErrorWeb {

        if (username == null) {
            throw new ErrorWeb("El nombre de usuario no debe estar vacío");
        }

        if (password == null || password.isEmpty()) {
            throw new ErrorWeb("La contraseña no debe estar vacía");
        }
        if (password2 == null || password2.isEmpty()) {
            throw new ErrorWeb("Debe completar el campo de validación de contraseña");
        }
        if (password.length() < 6) {

            throw new ErrorWeb("La contraseña debe tener al menos seis caracteres");
        }

        if (foto == null) {
            throw new ErrorWeb("La foto no puede estar vacía.");
        }

        validarPerfil(nombre, apellido, fechaNac, email, sexo, estudiosAlcanzados, posibleReubicacion,
                numeroTelefonico, categorias, disponibilidadHoraria, carnetConducir);
    }

    private void validarPerfil(String nombre, String apellido, Date fechaNac, String email, Sexo sexo,
            EstudiosAlcanzados estudiosAlcanzados, PosibleReubicacion posibleReubicacion,
            String numeroTelefonico, List<Categoria> categorias, DisponibilidadHoraria disponibilidadHoraria,
            CarnetConducir carnetConducir) throws ErrorWeb {
        if (nombre == null || nombre.length() < 3) {
            throw new ErrorWeb("El nombre debe contener al menos 3 caracteres");
        }
        if (apellido == null || apellido.length() < 2) {
            throw new ErrorWeb("El apellido debe contener al menos 2 caracteres");
        }
        if (fechaNac == null) {
            throw new ErrorWeb("Ingrese su fecha de nacimiento");
        }

        Date hoy = new Date();

        if (hoy.getYear() - fechaNac.getYear() <= 18) {
            throw new ErrorWeb("Debe ser mayor de edad");
        }

        if (email == null) {
            throw new ErrorWeb("Ingrese su email");
        }
        if (sexo == null) {
            throw new ErrorWeb("Ingrese su sexo");
        }
        if (estudiosAlcanzados == null) {
            throw new ErrorWeb("Ingrese el nivel de estudios alcanzado");
        }
        
        if (posibleReubicacion == null) {
            throw new ErrorWeb("Ingrese si está dispuesto a reubicarse");
        }
        if (numeroTelefonico == null || numeroTelefonico.length() != 10) {
            throw new ErrorWeb("Ingrese su número telefónico con diez dígitos, sin 0 y sin 15 ej: 3417123123");
        }
        if (categorias == null || categorias.isEmpty()) {
            throw new ErrorWeb("Ingrese al menos una categoría");
        }
        if (disponibilidadHoraria == null) {
            throw new ErrorWeb("Ingrese su disponibilidad horaria");
        }
        if (carnetConducir == null) {
            throw new ErrorWeb("Ingrese su tipo de carnet de conducir, si posee, o no");
        }
    }

    public List<Empleado> listAllByCategoriaYLocalidad(String categoria, String localidad) {
        List<Empleado> lista = new ArrayList<>();

        List<Empleado> lista1 = empleadoRepositorio.findByLocalidad(localidad);

        for (Empleado empleado : lista1) {
            for (Categoria empleado1 : empleado.getCategorias()) {
                if (empleado1.getNombre().equals(categoria)) {
                    lista.add(empleado);
                }
            }
        }

        return lista;
    }

    public Empleado findById(String id) {
        return empleadoRepositorio.findById(id).get();
    }

    public Empleado findByUsername(String username) {
        Empleado empleado = (Empleado) usuarioRepositorio.findByUsername(username);
        return empleado;
    }
}
