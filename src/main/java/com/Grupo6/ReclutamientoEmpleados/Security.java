
package com.Grupo6.ReclutamientoEmpleados;

import com.Grupo6.ReclutamientoEmpleados.Servicios.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.support.MultipartFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class Security extends WebSecurityConfigurerAdapter{
    
    @Autowired
    private UsuarioServicio usuarioServicio;
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception{
        auth.userDetailsService(usuarioServicio).passwordEncoder(new BCryptPasswordEncoder());
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", "/error", "/css/**", "/imgs/**", "/js/**", "/registrar-empleador",
                        "/registro/registro-empresa", "/registro/registro-empleado",
                        "/registro/registrar-empresa", "/registro/registrar-empleado").permitAll()
                .antMatchers("/interfaz-empleado/**").hasRole("CANDIDATO")
                .antMatchers("/editar-empleador/**", "/busqueda/**").hasRole("EMPRESA")
                .antMatchers("/registro/modificar-usuario", "/registro/save-usuario",
                        "/registro/cambiar-contrasenha").hasAnyRole("CANDIDATO", "EMPRESA", "ADMIN")
                .anyRequest().authenticated()
                .and().formLogin()
                      .loginPage("/login")
                      .usernameParameter("username")
                      .passwordParameter("password")
                      .defaultSuccessUrl("/")
                      .loginProcessingUrl("/logincheck")
                      .failureUrl("/login?error=error")
                      .permitAll()
                .and().logout()
                      .logoutUrl("/logout")
                      .logoutSuccessUrl("/login?logout");
    }

    @Bean
    public FilterRegistrationBean<MultipartFilter> multipartForCsrf() {
        FilterRegistrationBean<MultipartFilter> filter = new FilterRegistrationBean<>(new MultipartFilter());
        filter.setOrder(SecurityProperties.DEFAULT_FILTER_ORDER - 1);
        filter.addUrlPatterns("/registro/registrar-empleado", "/interfaz-empleado/save-empleado");
        return filter;
    }
}
