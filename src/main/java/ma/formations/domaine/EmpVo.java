package ma.formations.domaine;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpVo {
    private Long id;
    //Bean validation (est implémenté par Hibernate Validator
    @NotEmpty(message = "Le nom de l'employé ne peut pas être vide")
    private String name;
    @Max(value = 15000, message = "Le salaire ne doit pas dépasser 15000 ")
    private Double salary;
    @NotEmpty(message = "La fonction de l'employé ne peut pas être vide")
    private String fonction;
}
