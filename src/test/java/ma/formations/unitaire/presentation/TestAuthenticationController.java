package ma.formations.unitaire.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.formations.controller.AuthenticationController;
import ma.formations.domaine.UserVo;
import ma.formations.jwt.AuthEntryPointJwt;
import ma.formations.jwt.JwtUtils;
import ma.formations.service.IEmpService;
import ma.formations.service.IUserService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)

@WebMvcTest(controllers = AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
//@WithMockUser(authorities = {"ADMIN"}, password = "admin1", username = "admin1")
public class TestAuthenticationController {
    @MockBean
    AuthenticationManager authenticationManager;
    @MockBean
    AuthEntryPointJwt authEntryPointJwt;
    @MockBean
    IEmpService empService;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private IUserService userService;
    @MockBean
    private JwtUtils jwtUtils;

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testauthenticateUser() throws Exception {
        String tokenTest = "AAAA.BBBB.SSSS";
        UserVo userVoTest = new UserVo();
        userVoTest.setUsername("admin");
        userVoTest.setPassword("admin");
        Authentication authenticationResult = new UsernamePasswordAuthenticationToken(userVoTest.getUsername(),
                userVoTest.getPassword(), List.of(new SimpleGrantedAuthority("ADMIN")));
        when(authenticationManager.authenticate(Mockito.any())).thenReturn(authenticationResult);
        when(jwtUtils.generateJwtToken(Mockito.any())).thenReturn(tokenTest);
        mockMvc.perform(post("/auth/signin").
                        content(asJsonString(userVoTest)).
                        contentType(MediaType.APPLICATION_JSON).with(csrf()).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk())
                .andExpect(jsonPath("$.jwttoken").value(tokenTest))
                .andExpect(jsonPath("$.username").value(userVoTest.getUsername()))
                .andExpect(jsonPath("$.roles[0]").value("ADMIN"));
    }

    @Test
    void testregisterUser_ExistDeja() throws Exception {
        UserVo userVoTest = new UserVo();
        userVoTest.setUsername("admin");
        userVoTest.setPassword("admin");
        when(userService.existsByUsername(userVoTest.getUsername())).thenReturn(true);
        mockMvc.perform(post("/auth/signup").content(asJsonString(userVoTest)).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Error: Username is already taken!"));
    }

    @Test
    @Disabled
    void testregisterUser_DoesntExist() throws Exception {
        UserVo userVoTest = new UserVo();
        userVoTest.setUsername("admin");
        userVoTest.setPassword("admin");
        when(userService.existsByUsername(userVoTest.getUsername())).thenReturn(false);
        doNothing().when(userService).save(userVoTest);
        mockMvc.perform(post("/auth/signup").content(asJsonString(userVoTest)).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk())
                .andExpect(jsonPath("$").value("User registered successfully!"));
    }
}


