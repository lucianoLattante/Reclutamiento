

package com.Grupo6.ReclutamientoEmpleados.Controladores;

import com.Grupo6.ReclutamientoEmpleados.Entidades.Empleado;
import com.Grupo6.ReclutamientoEmpleados.Errores.ErrorWeb;
import com.Grupo6.ReclutamientoEmpleados.Repositorios.CategoriaRepositorio;
import com.Grupo6.ReclutamientoEmpleados.Formularios.PerfilCandidatoForm;
import com.Grupo6.ReclutamientoEmpleados.Servicios.CuentaPropia;
import java.security.Principal;
import com.Grupo6.ReclutamientoEmpleados.Servicios.CategoriaServicio;
import com.Grupo6.ReclutamientoEmpleados.Servicios.EmpleadoServicio;
import com.Grupo6.ReclutamientoEmpleados.Servicios.LocalidadServicio;
import java.io.IOException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

/**
 *
 * @author Tomi
 */

@Controller
@RequestMapping("/interfaz-empleado")
public class EmpleadoControlador {
    
    @Autowired
    private EmpleadoServicio empleadoServicio;
    
    @Autowired
    private CuentaPropia cuentaPropia;
    
    @Autowired
    private CategoriaServicio categoriaServicio;
    
    @Autowired
    private LocalidadServicio localidadServicio;
    
    @GetMapping("")
    public String verPerfil(ModelMap model){
        Authentication auth= SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("empleado",empleadoServicio.findByUsername(auth.getName()));
        return "visualizaciones";
    }
    
    @GetMapping("/editar")
    public String editarEmpleado(Model model, Principal principal, @RequestParam(required = false) String id) {
        Empleado empleado = cuentaPropia.require(principal.getName(), id, Empleado.class);
        model.addAttribute("categoria", categoriaServicio.listaCategorias());
        model.addAttribute("localidad", localidadServicio.listarLocalidad());
        model.addAttribute("empleado", PerfilCandidatoForm.from(empleado));
        return "editar-empleado";
    }

    @PostMapping("/save-empleado")
    public String saveEmpleado(Principal principal, @RequestParam(required = false) String id,
            @ModelAttribute("empleado") PerfilCandidatoForm form, MultipartFile imagen,
            RedirectAttributes attributes) throws IOException {
        try {
            empleadoServicio.actualizarPerfil(principal.getName(), id, form, imagen);
            attributes.addFlashAttribute("success", "Perfil actualizado");
            return "redirect:/interfaz-empleado";
        } catch (ErrorWeb e) {
            attributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/interfaz-empleado/editar";
        }
    }
}
