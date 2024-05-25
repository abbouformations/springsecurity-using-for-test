package ma.formations;

import ma.formations.domaine.EmpVo;
import ma.formations.domaine.RoleVo;
import ma.formations.domaine.UserVo;
import ma.formations.service.IEmpService;
import ma.formations.service.IUserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootApplication
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner run(IUserService userService, IEmpService empService) throws Exception {
        return args -> {
            userService.cleanDataBase();
            userService.save(RoleVo.builder().authority("ADMIN").build());
            userService.save(RoleVo.builder().authority("CLIENT").build());

            RoleVo roleAdmin = userService.getRoleByName("ADMIN");
            RoleVo roleClient = userService.getRoleByName("CLIENT");

            UserVo admin1 = UserVo.builder().
                    username("admin1").
                    password("admin1").
                    accountNonExpired(true).
                    credentialsNonExpired(true).
                    accountNonLocked(true).
                    enabled(true).
                    authorities(List.of(roleAdmin)).
                    build();

            UserVo admin2 = UserVo.builder().
                    username("admin2").
                    password("admin2").
                    accountNonExpired(true).
                    credentialsNonExpired(true).
                    accountNonLocked(true).
                    enabled(true).
                    authorities(List.of(roleAdmin)).
                    build();

            UserVo client1 = UserVo.builder().
                    username("client1").
                    password("client1").
                    accountNonExpired(true).
                    credentialsNonExpired(true).
                    accountNonLocked(true).
                    enabled(true).
                    authorities(List.of(roleClient)).
                    build();

            UserVo client2 = UserVo.builder().
                    username("client2").
                    password("client2").
                    accountNonExpired(true).
                    credentialsNonExpired(true).
                    accountNonLocked(true).
                    enabled(true).
                    authorities(List.of(roleClient)).
                    build();

            userService.save(admin1);
            userService.save(client1);
            userService.save(client2);
            userService.save(admin2);

            empService.save(EmpVo.builder().name("emp1").salary(10000d).fonction("Fonction1").build());
            empService.save(EmpVo.builder().name("emp2").salary(20000d).fonction("Fonction2").build());
            empService.save(EmpVo.builder().name("emp3").salary(30000d).fonction("Fonction3").build());
            empService.save(EmpVo.builder().name("emp4").salary(40000d).fonction("Fonction4").build());
            empService.save(EmpVo.builder().name("emp5").salary(50000d).fonction("Fonction5").build());

        };

    }

}
