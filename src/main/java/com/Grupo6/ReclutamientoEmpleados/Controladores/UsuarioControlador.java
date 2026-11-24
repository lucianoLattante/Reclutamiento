package com.Grupo6.ReclutamientoEmpleados.Controladores;

import com.Grupo6.ReclutamientoEmpleados.Entidades.Categoria;
import com.Grupo6.ReclutamientoEmpleados.Formularios.RegistroCandidatoForm;
import com.Grupo6.ReclutamientoEmpleados.Servicios.CuentaPropia;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import com.Grupo6.ReclutamientoEmpleados.Entidades.Usuario;
import com.Grupo6.ReclutamientoEmpleados.Enums.CarnetConducir;
import com.Grupo6.ReclutamientoEmpleados.Enums.DisponibilidadHoraria;
import com.Grupo6.ReclutamientoEmpleados.Enums.EstudiosAlcanzados;
import com.Grupo6.ReclutamientoEmpleados.Enums.MovilidadPropia;
import com.Grupo6.ReclutamientoEmpleados.Enums.PosibleReubicacion;
import com.Grupo6.ReclutamientoEmpleados.Enums.Sexo;
import com.Grupo6.ReclutamientoEmpleados.Errores.ErrorWeb;
import com.Grupo6.ReclutamientoEmpleados.Repositorios.CategoriaRepositorio;
import com.Grupo6.ReclutamientoEmpleados.Servicios.CategoriaServicio;
import com.Grupo6.ReclutamientoEmpleados.Servicios.LocalidadServicio;
import com.Grupo6.ReclutamientoEmpleados.Servicios.UsuarioServicio;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/registro")
public class UsuarioControlador {

    @Autowired
    private CuentaPropia cuentaPropia;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private CategoriaServicio categoriaServicio;

    @Autowired
    private LocalidadServicio localidadServicio;

    @GetMapping("/registro-empresa")
    public String registro() {
        return "registroEmpleador";
    }

    @GetMapping("/registro-empleado")
    public String registroEmpleado(ModelMap model) {
        model.addAttribute("categoria", categoriaServicio.listaCategorias());
        model.addAttribute("localidad", localidadServicio.listarLocalidad());
        model.addAttribute("empleado", new RegistroCandidatoForm());
        return "registroEmpleado";
    }

    @PostMapping("/registrar-empresa")
    public String registrarNuevoEmpleador(Model model, RedirectAttributes redirectAttributes, @RequestParam String username, @RequestParam String password, @RequestParam String password2, @RequestParam String nombre_empresa) {
        try {
            usuarioServicio.crearUsuarioEmpresa(username, password, password2, nombre_empresa);

            redirectAttributes.addFlashAttribute("success", "Usuario creado con éxito");

            return "redirect:/";
        } catch (ErrorWeb e) {
            model.addAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            model.addAttribute("username", username);
            return "redirect:/registro/registro-empresa";
        }
    }

    @PostMapping("/registrar-empleado")
    public String registrarNuevoEmpleado(Model model, RedirectAttributes redirectAttributes, @ModelAttribute("empleado") RegistroCandidatoForm empleado, @RequestParam String password2, MultipartFile imagen) throws IOException {
        try {

            usuarioServicio.crearUsuarioEmpleado(password2, empleado, imagen);

            redirectAttributes.addFlashAttribute("success", "Usuario creado con éxito");

            return "redirect:/";
        } catch (ErrorWeb e) {
            model.addAttribute("error", e.getMessage());
            empleado.setContrasenha(null);
            model.addAttribute("empleado", empleado);
            model.addAttribute("categoria", categoriaServicio.listaCategorias());
            model.addAttribute("localidad", localidadServicio.listarLocalidad());
            model.addAttribute("nombre_usuario", empleado.getNombre_usuario());
            return "registroEmpleado";
        }
    }

    @GetMapping("/modificar-usuario")
    public String modificarUsuario(Model model, Principal principal, @RequestParam(required = false) String id) {
        model.addAttribute("nombreUsuario", cuentaPropia.require(principal.getName(), id).getNombre_usuario());
        return "modificar-usuario";
    }

    @PostMapping("/save-usuario")
    public String saveUsuario(Principal principal, @RequestParam(required = false) String id,
            @RequestParam String nombre, RedirectAttributes attributes, HttpServletRequest request) {
        try {
            usuarioServicio.actualizarNombre(principal.getName(), id, nombre);
            request.getSession().invalidate();
            return "redirect:/login";
        } catch (ErrorWeb e) {
            attributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/registro/modificar-usuario";
        }
    }

    @PostMapping("/cambiar-contrasenha")
    public String cambiarContrasenha(Principal principal, @RequestParam(required = false) String id,
            @RequestParam String actual, @RequestParam String nueva, @RequestParam String confirmacion,
            RedirectAttributes attributes) {
        try {
            usuarioServicio.cambiarContrasenha(principal.getName(), id, actual, nueva, confirmacion);
            attributes.addFlashAttribute("success", "Contraseña actualizada");
        } catch (ErrorWeb e) {
            attributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/registro/modificar-usuario";
    }
}
