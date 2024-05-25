package ma.formations.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import ma.formations.domaine.RoleVo;
import ma.formations.domaine.TokenVo;
import ma.formations.domaine.UserVo;
import ma.formations.jwt.JwtUtils;
import ma.formations.service.IUserService;
import ma.formations.service.exception.BusinessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final IUserService userService;

    @PostMapping("/signin")
    public ResponseEntity<TokenVo> authenticateUser(@Valid @RequestBody UserVo userVo) {

        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(userVo.getUsername(), userVo.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            TokenVo tokenVo = new TokenVo();
            tokenVo.setJwttoken(jwt);
            tokenVo.setUsername(userVo.getUsername());
            Collection<? extends GrantedAuthority> list = authentication.getAuthorities();
            list.forEach(authorite -> tokenVo.getRoles().add(authorite.getAuthority()));
            return ResponseEntity.ok(tokenVo);
        } catch (Exception e) {
            throw new BusinessException("Login ou mot de passe incorrect");
        }

    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserVo userVo) {
        if (userService.existsByUsername(userVo.getUsername())) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }
        // par défaut, le client a le rôle CLIENT
        userVo.getAuthorities().add(RoleVo.builder().authority("CLIENT").build());
        userService.save(userVo);
        return ResponseEntity.ok("User registered successfully!");
    }
}
