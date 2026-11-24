package com.Grupo6.ReclutamientoEmpleados.Controladores;

import com.Grupo6.ReclutamientoEmpleados.Entidades.Empleador;
import com.Grupo6.ReclutamientoEmpleados.Errores.ErrorWeb;
import com.Grupo6.ReclutamientoEmpleados.Servicios.CuentaPropia;
import java.security.Principal;
import com.Grupo6.ReclutamientoEmpleados.Servicios.EmpleadorServicio;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/editar-empleador")
public class EmpleadorControlador {
    
    @Autowired
    private EmpleadorServicio empleadorServicio;
    
    @Autowired
    private CuentaPropia cuentaPropia;

    @GetMapping("")
    public String editarEmpleador(Model model, Principal principal, @RequestParam(required = false) String id) {
        model.addAttribute("nombreEmpresa", cuentaPropia.require(principal.getName(), id, Empleador.class).getNombreEmpresa());
        return "editar-empleador";
    }

    @PostMapping("/save-empleador")
    public String saveEmpleador(Principal principal, @RequestParam(required = false) String id,
            @RequestParam String nombreEmpresa, RedirectAttributes attributes) {
        try {
            empleadorServicio.actualizarPerfil(principal.getName(), id, nombreEmpresa);
            attributes.addFlashAttribute("success", "Perfil actualizado");
            return "redirect:/";
        } catch (ErrorWeb e) {
            attributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/editar-empleador";
        }
    }
}
