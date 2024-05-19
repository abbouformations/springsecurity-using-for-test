package ma.formations;

import lombok.AllArgsConstructor;
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

import java.util.Arrays;
import java.util.Collections;

@SpringBootApplication

@AllArgsConstructor
public class MainApplication {
    private IUserService userService;
    private IEmpService empService;

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder;
    }

    @Bean
    public CommandLineRunner run() throws Exception {
        return args -> {
            userService.cleanDataBase();
            userService.save(new RoleVo("ADMIN"));
            userService.save(new RoleVo("CLIENT"));

            RoleVo roleAdmin = userService.getRoleByName("ADMIN");
            RoleVo roleClient = userService.getRoleByName("CLIENT");

            UserVo admin1 = new UserVo("admin1", "admin1", Collections.singletonList(roleAdmin), true, true, true, true);
            UserVo admin2 = new UserVo("admin2", "admin2", Collections.singletonList(roleAdmin), true, true, true, true);
            UserVo client1 = new UserVo("client1", "client1", Collections.singletonList(roleClient), true, true, true, true);
            UserVo client2 = new UserVo("client2", "client2", Collections.singletonList(roleClient), true, true, true, true);

            userService.save(admin1);
            userService.save(client1);
            userService.save(client2);
            userService.save(admin2);

            empService.save(new EmpVo("emp1", 10000d, "Fonction1"));
            empService.save(new EmpVo("emp2", 20000d, "Fonction3"));
            empService.save(new EmpVo("emp3", 30000d, "Fonction4"));
            empService.save(new EmpVo("emp4", 40000d, "Fonction5"));
            empService.save(new EmpVo("emp5", 50000d, "Fonction6"));

        };

    }

}
