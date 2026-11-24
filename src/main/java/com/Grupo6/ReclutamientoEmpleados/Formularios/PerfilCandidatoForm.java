package com.Grupo6.ReclutamientoEmpleados.Formularios;

import com.Grupo6.ReclutamientoEmpleados.Entidades.Empleado;
import com.Grupo6.ReclutamientoEmpleados.Entidades.Categoria;
import com.Grupo6.ReclutamientoEmpleados.Enums.*;
import java.util.Date;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;
import org.springframework.format.annotation.DateTimeFormat;

public class PerfilCandidatoForm {
    private String nombre;
    private String apellido;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date fechaNac;
    private List<String> categorias = Collections.emptyList();
    private Sexo sexo;
    private EstudiosAlcanzados estudiosAlcanzados;
    private DisponibilidadHoraria disponibilidadHoraria;
    private MovilidadPropia movilidadPropia;
    private PosibleReubicacion posiblereubicacion;
    private CarnetConducir carnetConducir;
    private String email;
    private String numeroTelefonico;
    private String experienciaLaboral;
    private String localidad;

    public static PerfilCandidatoForm from(Empleado empleado) {
        PerfilCandidatoForm form = new PerfilCandidatoForm();
        form.nombre = empleado.getNombre();
        form.apellido = empleado.getApellido();
        form.fechaNac = empleado.getFechaNac();
        if (empleado.getCategorias() != null) {
            form.categorias = empleado.getCategorias().stream().map(Categoria::getId).collect(Collectors.toList());
        }
        form.sexo = empleado.getSexo();
        form.estudiosAlcanzados = empleado.getEstudiosAlcanzados();
        form.disponibilidadHoraria = empleado.getDisponibilidadHoraria();
        form.movilidadPropia = empleado.getMovilidadPropia();
        form.posiblereubicacion = empleado.getPosiblereubicacion();
        form.carnetConducir = empleado.getCarnetConducir();
        form.email = empleado.getEmail();
        form.numeroTelefonico = empleado.getNumeroTelefonico();
        form.experienciaLaboral = empleado.getExperienciaLaboral();
        form.localidad = empleado.getLocalidad() == null ? null : empleado.getLocalidad().getId();
        return form;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public Date getFechaNac() { return fechaNac; }
    public void setFechaNac(Date fechaNac) { this.fechaNac = fechaNac; }

    public List<String> getCategorias() { return categorias; }
    public void setCategorias(List<String> categorias) { this.categorias = categorias; }

    public Sexo getSexo() { return sexo; }
    public void setSexo(Sexo sexo) { this.sexo = sexo; }

    public EstudiosAlcanzados getEstudiosAlcanzados() { return estudiosAlcanzados; }
    public void setEstudiosAlcanzados(EstudiosAlcanzados estudiosAlcanzados) { this.estudiosAlcanzados = estudiosAlcanzados; }

    public DisponibilidadHoraria getDisponibilidadHoraria() { return disponibilidadHoraria; }
    public void setDisponibilidadHoraria(DisponibilidadHoraria disponibilidadHoraria) { this.disponibilidadHoraria = disponibilidadHoraria; }

    public MovilidadPropia getMovilidadPropia() { return movilidadPropia; }
    public void setMovilidadPropia(MovilidadPropia movilidadPropia) { this.movilidadPropia = movilidadPropia; }

    public PosibleReubicacion getPosiblereubicacion() { return posiblereubicacion; }
    public void setPosiblereubicacion(PosibleReubicacion posiblereubicacion) { this.posiblereubicacion = posiblereubicacion; }

    public CarnetConducir getCarnetConducir() { return carnetConducir; }
    public void setCarnetConducir(CarnetConducir carnetConducir) { this.carnetConducir = carnetConducir; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNumeroTelefonico() { return numeroTelefonico; }
    public void setNumeroTelefonico(String numeroTelefonico) { this.numeroTelefonico = numeroTelefonico; }

    public String getExperienciaLaboral() { return experienciaLaboral; }
    public void setExperienciaLaboral(String experienciaLaboral) { this.experienciaLaboral = experienciaLaboral; }

    public String getLocalidad() { return localidad; }
    public void setLocalidad(String localidad) { this.localidad = localidad; }
}
