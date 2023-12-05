package org;

import java.io.*;
import java.util.*;
import org.json.simple.*;
public class JSONParser implements CRUD {
     org.json.simple.parser.JSONParser jsonParser = new org.json.simple.parser.JSONParser();
     String jsonFilePath;
     JSONArray employees;
    ArrayList<String> keys = new ArrayList<>(Arrays.asList("employeeId", "employeeName", "employeeEmail", "employeeSalary", "employeeDesignation", "employeeAddress"));

    JSONParser(String path){
        jsonFilePath=path;
        try {
            employees = (JSONArray) jsonParser.parse(new FileReader(path));
        } catch (Exception e) {
            System.out.println("Error :  " + e);
        }
    }

    @Override
    public void readAll(){
        for (int i = 0; i < employees.size(); i++) {
            JSONObject employee = (JSONObject) employees.get(i);
            for (int j = 0; j < keys.size(); j++) {
                System.out.println((String.format("| %-25s|   ", keys.get(j)))+(String.format("%-40s|",employee.get(keys.get(j)))));
            }
            System.out.println(String.format("| %70s|","___").replaceAll(" ","_"));
        }
    }

    @Override
    public Employee readById(int id){
        HashMap<String, String> employeeDetails = new HashMap<>();
        for (int i = 0; i < employees.size(); i++) {
            JSONObject employee = (JSONObject) employees.get(i);
            if (employee.get("employeeId").toString().equals(id + "")) {
                for (int j = 0; j < keys.size(); j++) {
                    employeeDetails.put(keys.get(j),  employee.get(keys.get(j))+"");
                }
            }
        }
        Employee empObj = new Employee(employeeDetails);
        return empObj;
    }

    @Override
    public boolean insert(HashMap<String, String> employeeDetails){
        try {
            long currentId = getNewId();
            JSONObject employee = new JSONObject();
            employee.put("employeeId",currentId);
            for (Map.Entry<String, String> entry : employeeDetails.entrySet()) {
                employee.put(entry.getKey(), entry.getValue());
            }
            employees.add(employee);
            saveChanges(employees);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateById(HashMap<String, String> employeeDetails){
        int c = 0;
        try {
            int employeeId = Integer.parseInt(employeeDetails.get("employeeId"));
            for (int i = 0; i < employees.size(); i++) {
                JSONObject employee = (JSONObject) employees.get(i);
                if (employee.get("employeeId").toString().equals(employeeId + "")) {
                    for (Map.Entry<String, String> entry : employeeDetails.entrySet()) {
                        if (!entry.getKey().equals("employeeId")) {
                            employee.put(entry.getKey(), entry.getValue());
                        }
                    }
                    c++;
                    saveChanges(employees);
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return c == 0 ? false : true;
    }

    @Override
    public boolean deleteById(int id) {
        int c = 0;
        try {
            for (int i = 0; i < employees.size(); i++) {
                JSONObject employee = (JSONObject) employees.get(i);
                if ((long) employee.get("employeeId") == id) {
                    employees.remove(i);
                    c++;
                    saveChanges(employees);
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return c == 0 ? false : true;
    }

    public  void saveChanges(JSONArray employeeDetails) throws Exception {
        FileWriter jsonFile = new FileWriter(jsonFilePath);
        jsonFile.write(employeeDetails.toString());
        jsonFile.close();
    }

    public  long getNewId() {
        long newId = 1;
        for (int i = 0; i < employees.size(); i++) {
            JSONObject employee = (JSONObject) employees.get(i);
            long val = (long) employee.get("employeeId");
            if (newId < val) {
                newId = val;
            }
        }
        return employees.size() == 0 ? 1 : newId + 1;
    }
}
