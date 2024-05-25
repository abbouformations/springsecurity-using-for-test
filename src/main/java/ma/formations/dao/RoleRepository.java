package ma.formations.dao;

import ma.formations.service.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    List<Role> findByAuthority(String role);

    List<Role> findAll();

}