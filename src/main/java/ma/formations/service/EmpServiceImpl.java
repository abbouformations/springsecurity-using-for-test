package ma.formations.service;

import lombok.AllArgsConstructor;
import ma.formations.dao.EmpRepository;
import ma.formations.domaine.EmpVo;
import ma.formations.service.model.Emp;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class EmpServiceImpl implements IEmpService {
    private final EmpRepository empRepository;
    private ModelMapper modelMapper;

    @Override
    public List<EmpVo> getEmployees() {
        return empRepository.findAll().
                stream().
                map(bo -> modelMapper.map(bo, EmpVo.class)).
                collect(Collectors.toList());

    }

    @Override
    public void save(EmpVo emp) {
        empRepository.save(modelMapper.map(emp, Emp.class));
    }

    @Override
    public EmpVo getEmpById(Long id) {
        boolean trouve = empRepository.existsById(id);
        if (!trouve)
            return null;
        return modelMapper.map(empRepository.getById(id), EmpVo.class);
    }

    @Override
    public void delete(Long id) {
        empRepository.deleteById(id);
    }

    @Override
    public List<EmpVo> findBySalary(Double salary) {
        return empRepository.findBySalary(salary).
                stream().
                map(bo -> modelMapper.map(bo, EmpVo.class)).
                collect(Collectors.toList());
    }

    @Override
    public List<EmpVo> findByFonction(String fonction) {
        return empRepository.findByFonction(fonction).
                stream().
                map(bo -> modelMapper.map(bo, EmpVo.class)).
                collect(Collectors.toList());
    }

    @Override
    public List<EmpVo> findBySalaryAndFonction(Double salary, String fonction) {
        return empRepository.findBySalaryAndFonction(salary, fonction).
                stream().
                map(bo -> modelMapper.map(bo, EmpVo.class)).
                collect(Collectors.toList());
    }

    @Override
    public EmpVo getEmpHavaingMaxSalary() {
        return modelMapper.map(empRepository.getEmpHavaingMaxSalary(), EmpVo.class);
    }

    @Override
    public List<EmpVo> findAll(int pageId, int size) {
        return empRepository.findAll(PageRequest.of(pageId, size, Direction.ASC, "name")).
                getContent().
                stream().
                map(bo -> modelMapper.map(bo, EmpVo.class)).
                collect(Collectors.toList());
    }

    @Override
    public List<EmpVo> sortBy(String fieldName) {
        return empRepository.findAll(Sort.by(fieldName)).
                stream().
                map(bo -> modelMapper.map(bo, EmpVo.class)).
                collect(Collectors.toList());
    }
}
