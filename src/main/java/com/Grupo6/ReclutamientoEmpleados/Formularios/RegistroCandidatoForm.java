package com.Grupo6.ReclutamientoEmpleados.Formularios;

public class RegistroCandidatoForm extends PerfilCandidatoForm {
    private String nombre_usuario;
    private String contrasenha;

    public String getNombre_usuario() { return nombre_usuario; }
    public void setNombre_usuario(String nombre_usuario) { this.nombre_usuario = nombre_usuario; }

    public String getContrasenha() { return contrasenha; }
    public void setContrasenha(String contrasenha) { this.contrasenha = contrasenha; }
}
