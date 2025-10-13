package tqsdemo.employeemngr.service;

import tqsdemo.employeemngr.data.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {

    public Employee getEmployeeById(Long id);

    public Optional<Employee> getEmployeeByName(String name);

    public List<Employee> getAllEmployees();

    public boolean exists(String email);

    public Employee save(Employee employee);
}
