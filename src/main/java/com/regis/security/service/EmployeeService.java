package com.regis.security.service;

import com.regis.security.employee.Employee;
import com.regis.security.employee.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    public Employee postEmployee(Employee employee){
        return employeeRepository.save(employee);
    }
    public List<Employee> getAllEmployees(){
        return employeeRepository.findAll();
    }
    public List<Employee> getAllEmployeesRead(){ return employeeRepository.findAll(); }

    public void deleteEmployee(Long id){
        if (!employeeRepository.existsById(id)){
            throw new EntityNotFoundException("employee with ID" + id + "not found");
        }
        employeeRepository.deleteById(id);
    }

    public Employee getEmployeeById(Long id){
        return employeeRepository.findById(id).orElse(null);
    }

    public Employee updateEmployee(Long id, Employee employee){
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        if (optionalEmployee.isPresent()){
            Employee existingEmployee = optionalEmployee.get();

            existingEmployee.setEmail(employee.getEmail());
            existingEmployee.setName(employee.getName());
            existingEmployee.setPhone(employee.getPhone());
            existingEmployee.setDepartment(employee.getDepartment());
            existingEmployee.setResponsibility(employee.getResponsibility());
            return  employeeRepository.save(existingEmployee);
        }
        return null;
    }
}
