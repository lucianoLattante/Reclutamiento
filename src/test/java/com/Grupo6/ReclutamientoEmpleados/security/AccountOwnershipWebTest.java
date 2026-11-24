package com.Grupo6.ReclutamientoEmpleados.security;

import com.Grupo6.ReclutamientoEmpleados.Controladores.UsuarioControlador;
import com.Grupo6.ReclutamientoEmpleados.Controladores.EmpleadoControlador;
import com.Grupo6.ReclutamientoEmpleados.Controladores.EmpleadorControlador;
import com.Grupo6.ReclutamientoEmpleados.Entidades.Empleado;
import com.Grupo6.ReclutamientoEmpleados.Entidades.Usuario;
import com.Grupo6.ReclutamientoEmpleados.Enums.Rol;
import com.Grupo6.ReclutamientoEmpleados.Repositorios.EmpleadoRepositorio;
import com.Grupo6.ReclutamientoEmpleados.Repositorios.EmpleadorRepositorio;
import com.Grupo6.ReclutamientoEmpleados.Repositorios.UsuarioRepositorio;
import com.Grupo6.ReclutamientoEmpleados.Security;
import com.Grupo6.ReclutamientoEmpleados.Servicios.CategoriaServicio;
import com.Grupo6.ReclutamientoEmpleados.Servicios.CuentaPropia;
import com.Grupo6.ReclutamientoEmpleados.Servicios.EmpleadoServicio;
import com.Grupo6.ReclutamientoEmpleados.Servicios.EmpleadorServicio;
import com.Grupo6.ReclutamientoEmpleados.Servicios.FotoServicio;
import com.Grupo6.ReclutamientoEmpleados.Servicios.LocalidadServicio;
import com.Grupo6.ReclutamientoEmpleados.Servicios.UsuarioServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;
import com.Grupo6.ReclutamientoEmpleados.Formularios.PerfilCandidatoForm;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.support.MultipartFilter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({UsuarioControlador.class, EmpleadoControlador.class, EmpleadorControlador.class})
@Import({Security.class, UsuarioServicio.class, CuentaPropia.class})
class AccountOwnershipWebTest {
    @Autowired MockMvc mvc;
    @Autowired WebApplicationContext webContext;
    @Autowired FilterRegistrationBean<MultipartFilter> multipartForCsrf;
    @MockBean UsuarioRepositorio usuarios;
    @MockBean EmpleadoRepositorio empleados;
    @MockBean EmpleadorRepositorio empleadores;
    @MockBean EmpleadoServicio empleadoServicio;
    @MockBean EmpleadorServicio empleadorServicio;
    @MockBean LocalidadServicio localidadServicio;
    @MockBean CategoriaServicio categoriaServicio;
    @MockBean FotoServicio fotoServicio;

    private final BCryptPasswordEncoder passwords = new BCryptPasswordEncoder();
    private Usuario owner;
    private Usuario admin;

    @BeforeEach
    void seed() throws Exception {
        multipartForCsrf.getFilter().init(new MockFilterConfig(webContext.getServletContext(), "multipartForCsrf"));
        owner = new Empleado();
        owner.setId("owner-id");
        owner.setNombre_usuario("owner");
        owner.setRol(Rol.CANDIDATO);
        owner.setContrasenha(passwords.encode("synthetic-initial"));
        admin = new Usuario();
        admin.setId("admin-id");
        admin.setNombre_usuario("admin");
        admin.setRol(Rol.ADMIN);
        when(usuarios.findByUsername("owner")).thenReturn(owner);
        when(usuarios.findByUsername("admin")).thenReturn(admin);
    }

    @Test
    void ownerCanChangeNameWithoutChangingPasswordOrRole() throws Exception {
        String hash = owner.getContrasenha();
        mvc.perform(post("/registro/save-usuario").with(user("owner").roles("CANDIDATO"))
                .with(csrf()).param("id", "owner-id").param("nombre", "renamed")
                .param("rol", "ADMIN").param("contrasenha", "unbound-value"))
                .andExpect(redirectedUrl("/login"));
        assertEquals("renamed", owner.getNombre_usuario());
        assertEquals(Rol.CANDIDATO, owner.getRol());
        assertEquals(hash, owner.getContrasenha());
    }

    @Test
    void anotherUserAndAdminCannotEditAnUnownedAccount() throws Exception {
        mvc.perform(get("/registro/modificar-usuario").with(user("owner").roles("CANDIDATO"))
                .param("id", "admin-id")).andExpect(status().isForbidden());
        mvc.perform(post("/registro/save-usuario").with(user("admin").roles("ADMIN"))
                .with(csrf()).param("id", "owner-id").param("nombre", "blocked"))
                .andExpect(status().isForbidden());
        assertEquals("owner", owner.getNombre_usuario());
    }

    @Test
    void missingCsrfCannotModifyTheOwnersAccount() throws Exception {
        mvc.perform(post("/registro/save-usuario").with(user("owner").roles("CANDIDATO"))
                .param("nombre", "blocked")).andExpect(status().isForbidden());
        verifyNoInteractions(usuarios);
    }

    @Test
    void profileEditsDenyOtherAccountsAndWrongRoles() throws Exception {
        mvc.perform(get("/interfaz-empleado/editar").with(user("owner").roles("CANDIDATO"))
                .param("id", "admin-id")).andExpect(status().isForbidden());
        mvc.perform(get("/editar-empleador").with(user("owner").roles("CANDIDATO")))
                .andExpect(status().isForbidden());
        mvc.perform(multipart("/interfaz-empleado/save-empleado").with(user("owner").roles("CANDIDATO")))
                .andExpect(status().isForbidden());
    }

    @Test
    void profileServicesResolveTheTargetFromTheAuthenticatedAccount() {
        CuentaPropia ownership = new CuentaPropia(usuarios);
        EmpleadoServicio candidate = new EmpleadoServicio();
        EmpleadorServicio employer = new EmpleadorServicio();
        ReflectionTestUtils.setField(candidate, "cuentaPropia", ownership);
        ReflectionTestUtils.setField(employer, "cuentaPropia", ownership);
        assertThrows(AccessDeniedException.class,
                () -> candidate.actualizarPerfil("owner", "admin-id", new PerfilCandidatoForm(), null));
        assertThrows(AccessDeniedException.class,
                () -> employer.actualizarPerfil("owner", "owner-id", "Synthetic employer"));
    }

    @Test
    void dedicatedPasswordActionChecksCurrentPasswordAndStoresABcryptHash() throws Exception {
        String old = owner.getContrasenha();
        mvc.perform(post("/registro/cambiar-contrasenha").with(user("owner").roles("CANDIDATO"))
                .with(csrf()).param("actual", "wrong-synthetic-pass").param("nueva", "synthetic-replacement")
                .param("confirmacion", "synthetic-replacement"))
                .andExpect(flash().attributeExists("error"));
        assertEquals(old, owner.getContrasenha());
        mvc.perform(post("/registro/cambiar-contrasenha").with(user("owner").roles("CANDIDATO"))
                .with(csrf()).param("actual", "synthetic-initial").param("nueva", "synthetic-replacement")
                .param("confirmacion", "synthetic-replacement"))
                .andExpect(flash().attributeExists("success"));
        assertTrue(passwords.matches("synthetic-replacement", owner.getContrasenha()));
        assertNotEquals("synthetic-replacement", owner.getContrasenha());
    }
}
