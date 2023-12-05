package org;

import java.sql.*;
import java.util.*;

public class DataBaseParser implements CRUD {
    Connection c = null;
    Statement s = null;
    HashMap<String, String> keys = new HashMap<String, String>() {
        {
            put("employeeId", "employee_id");
            put("employeeName", "employee_name");
            put("employeeEmail", "employee_email");
            put("employeeSalary", "employee_salary");
            put("employeeDesignation", "employee_designation");
            put("employeeAddress", "employee_address");
        }
    };

    DataBaseParser(String jdbcDriver, String jdbcUrl, String jdbcUserName, String jdbcPassword) {
        try {
            Class.forName(jdbcDriver);
            c = DriverManager.getConnection(jdbcUrl, jdbcUserName, jdbcPassword);
            s = c.createStatement();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void readAll() throws Exception {
        ResultSet resultSet = s.executeQuery("select * from employees");
        while (resultSet.next()) {
            for (Map.Entry<String, String> entry : keys.entrySet()) {
                System.out.println((String.format("| %-25s|   ", entry.getKey())) + (String.format("%-40s|",  resultSet.getString(entry.getValue()))));
            }
            System.out.println(String.format("| %70s|", "___").replaceAll(" ", "_"));
        }
    }

    @Override
    public Employee readById(int id) throws Exception {
        HashMap<String, String> employee_details = new HashMap<>();
        PreparedStatement preparedStatement = c.prepareStatement("select * from employees where employee_id=?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            for (Map.Entry<String, String> entry : keys.entrySet()) {
                employee_details.put(entry.getKey(), resultSet.getString(entry.getValue()));
            }
        }
        Employee empObj = new Employee(employee_details);
        return empObj;
    }

    @Override
    public boolean insert(HashMap<String, String> employeeDetails) {
        try {
            PreparedStatement preparedStatement = c.prepareStatement("INSERT INTO  employees(employee_name,employee_email,employee_salary,employee_designation,employee_address) VALUES(?,?,?,?,?)");
            preparedStatement.setString(1, employeeDetails.get("employeeName"));
            preparedStatement.setString(2, employeeDetails.get("employeeEmail"));
            preparedStatement.setInt(3, Integer.parseInt(employeeDetails.get("employeeSalary")));
            preparedStatement.setString(4, employeeDetails.get("employeeDesignation"));
            preparedStatement.setString(5, employeeDetails.get("employeeAddress"));
            preparedStatement.execute();
            return true;
        } catch (NumberFormatException n) {
            System.out.println("Salary should be Number");
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateById(HashMap<String, String> employeeDetails) {
        try {
            int employee_id = Integer.parseInt(employeeDetails.get("employeeId"));
            PreparedStatement preparedStatement = c.prepareStatement("update employees set employee_name=?,employee_email=?,employee_salary=?,employee_designation=?,employee_address =? where employee_id =?");
            preparedStatement.setString(1, employeeDetails.get("employeeName"));
            preparedStatement.setString(2, employeeDetails.get("employeeEmail"));
            preparedStatement.setInt(3, Integer.parseInt(employeeDetails.get("employeeSalary")));
            preparedStatement.setString(4, employeeDetails.get("employeeDesignation"));
            preparedStatement.setString(5, employeeDetails.get("employeeAddress"));
            preparedStatement.setInt(6, employee_id);
            preparedStatement.execute();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteById(int id) {
        try {
            PreparedStatement preparedStatement = c.prepareStatement("delete from employees where employee_id =?");
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public int getNewId() throws SQLException {
        int newId = 1;
        ResultSet resultSet = s.executeQuery("select * from employees");
        while (resultSet.next()) {
            if (resultSet.getInt("employee_id") > newId) {
                newId = resultSet.getInt("employee_id");
            }
        }
        if (newId == 1) {
            return newId;
        } else {
            return newId + 1;
        }
    }
}
