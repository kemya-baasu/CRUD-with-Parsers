package org;

import java.util.HashMap;

public interface CRUD {
void readAll() throws Exception;
Employee readById(int id) throws Exception;
boolean insert(HashMap<String,String> employeeDetails);
boolean updateById(HashMap<String,String> employeeDetails);
boolean deleteById(int id);
}
