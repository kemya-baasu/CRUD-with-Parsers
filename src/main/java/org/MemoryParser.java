package org;

import java.util.*;

public class MemoryParser implements CRUD {
     HashSet<Employee> employees;

    MemoryParser() {
        employees = new HashSet<>();
    }

    @Override
    public void readAll() {
        Employee element;
        Iterator iterator= employees.iterator();
        while (iterator.hasNext()) {
            element = (Employee) iterator.next();
            for (Map.Entry<String, String> entry : element.getEmployee().entrySet()) {
                System.out.println((String.format("| %-25s|   ", entry.getKey()))+(String.format("%-40s|",entry.getValue())));
            }
            System.out.println(String.format("| %70s|","___").replaceAll(" ","_"));
        }
    }

    @Override
    public Employee readById(int id) {
        Employee element = null;
        Iterator iterator= employees.iterator();
        while (iterator.hasNext()) {
             element = (Employee) iterator.next();
            if (Integer.parseInt(element.getEmployee().get("employeeId") )== id) {
                break;
            }
        }
        return element;
    }

    @Override
    public boolean insert(HashMap<String, String> employeeDetails) {
        try {
            long newId = getNewId();
            employeeDetails.put("employeeId",newId+"");
            Employee employee = new Employee(employeeDetails);
            employees.add(employee);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateById(HashMap<String, String> employeeDetails) {
        int c = 0;
        try {
            Employee element = null;
            Iterator iterator= employees.iterator();
            while (iterator.hasNext()) {
                element = (Employee) iterator.next();
                if (element.getEmployee().get("employeeId").equals(employeeDetails.get("employeeId"))) {
                    element.updateEmployeeDetails(employeeDetails);
                    break;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteById(int id) {
        int c = 0;
        try {
            Employee element = null;
            Iterator iterator= employees.iterator();
            while (iterator.hasNext()) {
                element = (Employee) iterator.next();
                if (Integer.parseInt(element.getEmployee().get("employeeId") )== id) {
                    employees.remove(element);
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return c == 0 ? false : true;
    }

    public long getNewId() {
        long currentId = 1;
        Iterator iterator= employees.iterator();
        while (iterator.hasNext()) {
            Employee element = (Employee) iterator.next();
            if (Integer.parseInt(element.getEmployee().get("employeeId") )> currentId) {
                currentId=Integer.parseInt(element.getEmployee().get("employeeId") );
            }
        }
        return currentId == 1 && employees.size() == 0 ? 1 : currentId + 1;
    }
}

class Employee {
    HashMap<String, String> employee = new HashMap<>();

    public Employee(HashMap<String, String> employeeDetails) {
        updateEmployeeDetails(employeeDetails);
    }
    public void updateEmployeeDetails(HashMap<String, String> employeeDetails){
        for (Map.Entry<String, String> entry : employeeDetails.entrySet()) {
            employee.put(entry.getKey(), entry.getValue());
        }
    }

    public HashMap<String, String> getEmployee() {
        return employee;
    }

    @Override
    public int hashCode() {
        return Integer.parseInt(employee.get("employeeId"));
    }
}
