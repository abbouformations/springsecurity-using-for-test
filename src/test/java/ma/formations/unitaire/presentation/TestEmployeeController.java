package ma.formations.unitaire.presentation;

import ma.formations.controller.EmpController;
import ma.formations.domaine.EmpVo;
import ma.formations.jwt.AuthEntryPointJwt;
import ma.formations.jwt.JwtUtils;
import ma.formations.service.IEmpService;
import ma.formations.service.IUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = EmpController.class,
        useDefaultFilters = false,
        includeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value =
                EmpController.class)})
@WithMockUser(authorities = {"ADMIN"}, password = "admin1", username = "admin1")

public class TestEmployeeController {
    @MockBean
    AuthenticationManager authenticationManager;
    @MockBean
    AuthEntryPointJwt authEntryPointJwt;
    @MockBean

    private IEmpService service;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private IUserService userService;
    @MockBean
    private JwtUtils jwtUtils;

    @Test
    void testgetAll() throws Exception {
        List<EmpVo> employees = Arrays.asList(
                EmpVo.builder().name("emp1").fonction("Fonction1").salary(10000d).build(),
                EmpVo.builder().name("emp2").fonction("Fonction2").salary(20000d).build(),
                EmpVo.builder().name("emp3.").fonction("Fonction3").salary(30000d).build());
        when(service.getEmployees()).thenReturn(employees);
        mvc.perform(get("/employees").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("emp1"))
                .andExpect(jsonPath("$[1].fonction").value("Fonction2"))
                .andExpect(jsonPath("$[1].salary").value(20000d))
                .andExpect(jsonPath("$[2].salary").value(30000d));
    }

    @Test
    void testgetAll_empty() throws Exception {
        when(service.getEmployees()).thenReturn(null);
        mvc.perform(get("/employees").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void testgetEmpByIdEmployeeExist() throws Exception {
        EmpVo empTest = new EmpVo();
        empTest.setId(1L);
        empTest.setFonction("INGENIEUR");
        empTest.setSalary(10000d);
        empTest.setName("Foulane");
        when(service.getEmpById(Mockito.any())).thenReturn(empTest);
        mvc.perform(get("/employees/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(empTest.getId()))
                .andExpect(jsonPath("$.name").value(empTest.getName()))
                .andExpect(jsonPath("$.fonction").value(empTest.getFonction()))
                .andExpect(jsonPath("$.salary").value(empTest.getSalary()));
    }

    @Test
    void testgetEmpByIdEmployeeDoesntExist() throws Exception {
        when(service.getEmpById(Mockito.any())).thenReturn(null);
        mvc.perform(get("/employees/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("employee doen't exist"));
    }
}
