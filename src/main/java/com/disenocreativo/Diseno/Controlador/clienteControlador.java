package com.disenocreativo.Diseno.Controlador;

import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.disenocreativo.Diseno.Entidad.usuario;
import com.disenocreativo.Diseno.Servicio.clienteServicio;

@RestController
@RequestMapping("/controladorCliente")
public class clienteControlador {
     @Autowired
private clienteServicio usuarioService;
    @GetMapping("/usuario")
    public List<usuario> getAllUs(){
        return usuarioService.getAllUsuarios();
    }

    @PostMapping("/guardarUs")
    public usuario guardarUsuario(@RequestBody usuario k){
        return usuarioService.save(k);
    }
    @GetMapping("/findbycorreo")
    public usuario findByCorreUsuario(@RequestParam("correo") String correo){
        return usuarioService.findByCorreo(correo);
    }
    @GetMapping("/rol")
    public String obtenerRolPorUsuario() {
        // Obtener el nombre de usuario del objeto Authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        
        // Buscar el usuario en el servicio Usuario_modelos
        usuario user = usuarioService.findByCorreo(name);

        // Verificar si el usuario existe y devolver su rol
        if (user != null) {
            return user.getRol().toString();
        } else {
            return "Usuario no encontrado";
        }
    }
}