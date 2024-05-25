package ma.formations.service;

import lombok.AllArgsConstructor;
import ma.formations.dao.EmpRepository;
import ma.formations.dao.RoleRepository;
import ma.formations.dao.UserRepository;
import ma.formations.domaine.RoleVo;
import ma.formations.domaine.UserVo;
import ma.formations.service.exception.BusinessException;
import ma.formations.service.model.Role;
import ma.formations.service.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements IUserService, UserDetailsService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private EmpRepository empRepository;
    private ModelMapper modelMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return modelMapper.map(userRepository.findByUsername(username), UserVo.class);
    }

    @Override
    public void save(UserVo userVo) {
        User user = modelMapper.map(userVo, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        List<Role> rolesPersist = new ArrayList<>();
        for (Role role : user.getAuthorities()) {
            Role userRole = roleRepository.findByAuthority(role.getAuthority()).get(0);
            rolesPersist.add(userRole);
        }
        user.setAuthorities(rolesPersist);
        userRepository.save(user);
    }

    @Override
    public void save(RoleVo roleVo) {
        roleRepository.save(modelMapper.map(roleVo, Role.class));
    }

    @Override
    public List<UserVo> getAllUsers() {

        return userRepository.findAll().
                stream().
                map(bo -> modelMapper.map(bo, UserVo.class)).
                collect(Collectors.toList());
    }

    @Override
    public List<RoleVo> getAllRoles() {
        return roleRepository.findAll().
                stream().
                map(bo -> modelMapper.map(bo, RoleVo.class)).
                collect(Collectors.toList());
    }

    @Override
    public RoleVo getRoleByName(String role) {
        return modelMapper.map(roleRepository.findByAuthority(role).get(0), RoleVo.class);
    }

    @Override
    public void cleanDataBase() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
        empRepository.deleteAll();
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public UserVo findByUsername(String username) {
        if (username == null || username.trim().equals(""))
            throw new BusinessException("login is empty !!");

        User bo = userRepository.findByUsername(username);

        if (bo == null)
            throw new BusinessException("No user with this login");

        UserVo vo = modelMapper.map(bo, UserVo.class);
        return vo;
    }

}
