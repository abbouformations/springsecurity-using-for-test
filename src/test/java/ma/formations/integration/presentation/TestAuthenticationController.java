package ma.formations.integration.presentation;

import ma.formations.domaine.RoleVo;
import ma.formations.domaine.TokenVo;
import ma.formations.domaine.UserVo;
import ma.formations.service.IUserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TestAuthenticationController {
    @Autowired
    private IUserService userService;
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    public void init() {
        userService.save(RoleVo.builder().authority("ADMIN").build());
        userService.save(RoleVo.builder().authority("CLIENT").build());
        RoleVo roleAdmin = userService.getRoleByName("ADMIN");
        RoleVo roleClient = userService.getRoleByName("CLIENT");
        UserVo admin1 = UserVo.builder().
                username("admin1").
                password("admin1").
                authorities(Collections.singletonList(roleAdmin)).build();

        UserVo admin2 = UserVo.builder().
                username("admin2").
                password("admin2").
                authorities(Collections.singletonList(roleAdmin)).build();

        UserVo client1 = UserVo.builder().
                username("client1").
                password("client1").
                authorities(Collections.singletonList(roleClient)).build();

        UserVo client2 = UserVo.builder().
                username("client2").
                password("client2").
                authorities(Collections.singletonList(roleClient)).build();

        userService.save(admin1);
        userService.save(client1);
        userService.save(client2);
        userService.save(admin2);
    }

    @AfterEach
    public void clean() {
        userService.cleanDataBase();
    }

    @Test
    public void testauthenticateUserIsNotNull() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
// Request to return JSON format
        headers.setContentType(MediaType.APPLICATION_JSON);
        UserVo user = new UserVo();
        user.setUsername("admin1");
        user.setPassword("admin1");
        HttpEntity<UserVo> entity = new HttpEntity<UserVo>(user, headers);
// TokenVo tokenTest=new TokenVo()
        assertThat(this.restTemplate.exchange("http://localhost:" + port + "/auth/signin", HttpMethod.POST,
                entity,
                TokenVo.class)).isNotNull();
    }

    @Test
    public void testauthenticateUserHasToken() throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
// Request to return JSON format
        headers.setContentType(MediaType.APPLICATION_JSON);
        UserVo user = new UserVo();
        user.setUsername("admin1");
        user.setPassword("admin1");
        HttpEntity<UserVo> entity = new HttpEntity<UserVo>(user, headers);
// TokenVo tokenTest=new TokenVo()
        ResponseEntity<TokenVo> response = this.restTemplate.exchange("http://localhost:" + port +
                        "/auth/signin",
                HttpMethod.POST, entity, TokenVo.class);
        TokenVo dto = response.getBody();
        assertThat(dto).isNotNull();
        assertThat(dto.getJwttoken()).isNotNull();
        assertThat(dto.getRoles()).isNotEmpty();
    }

    //
    @Test
    public void testauthenticateUserHasRole() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
// Request to return JSON format
        headers.setContentType(MediaType.APPLICATION_JSON);
        UserVo user = new UserVo();
        user.setUsername("admin1");
        user.setPassword("admin1");
        HttpEntity<UserVo> entity = new HttpEntity<UserVo>(user, headers);
// TokenVo tokenTest=new TokenVo()
        ResponseEntity<TokenVo> response = this.restTemplate.exchange("http://localhost:" + port +
                        "/auth/signin",
                HttpMethod.POST, entity, TokenVo.class);
        assertThat(response.getBody()).isNotNull();
        TokenVo t = response.getBody();
        List<String> roles = t.getRoles();
//assertThat(roles).isNotEmpty();
        assertThat(t).isNotNull();
        assertThat(roles).isNotNull();
        assertThat(roles.get(0)).isEqualTo("ADMIN");
    }
}
