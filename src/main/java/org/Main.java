package org;

import java.io.*;
import java.util.*;

public class Main {
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        String propertiesFilepath="D:\\crud\\src\\main\\resources\\applicationProperties.properties";
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(propertiesFilepath)) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String value = properties.getProperty("OutputFileType");
        switch (value) {
            case "XML":
                executeOperations(new XMLParser(properties.getProperty("xmlFilePath")));
                break;
            case "JSON":
                executeOperations(new JSONParser(properties.getProperty("jsonFilePath")));
                break;
            case "Memory":
                executeOperations(new MemoryParser());
                break;
            case "Database":
                executeOperations(new DataBaseParser(properties.getProperty("jdbcDriver"),properties.getProperty("jdbcUrl"),properties.getProperty("jdbcUserName"),properties.getProperty("jdbcPassword")));
                break;
            default:
                System.out.println("No such type of file available as specified in properties File :" + value);
                break;
        }
    }

    public static void executeOperations(CRUD object) throws Exception {
        boolean flag=true;
        while (flag) {
            System.out.println("-------------------- select operation to be performed ---------------------");
            System.out.println("1) Insert");
            System.out.println("2) ReadAll");
            System.out.println("3) ReadById");
            System.out.println("4) Update by ID");
            System.out.println("5) Delete Employee record by ID");
            System.out.println("6) Exit");
            int choice=0;
            try {
                 choice = sc.nextInt();
                 sc.nextLine();
            }catch (InputMismatchException e){
                System.out.println("Input Should be a Number");
                sc.nextLine();
                continue;
            }

            switch (choice) {
                case 1:
                case 4:
                    getEmployeeDetails(choice, object);
                    break;
                case 2:
                    System.out.println(String.format(" %70s", "___").replaceAll(" ", "_"));
                    object.readAll();
                    break;
                case 3:
                    System.out.print("Employee ID : ");
                    Employee e = object.readById(sc.nextInt());
                    sc.nextLine();
                    System.out.println(String.format(" %70s", "___").replaceAll(" ", "_"));
                    for (Map.Entry<String, String> entry : e.getEmployee().entrySet()) {
                        System.out.println((String.format("| %-25s|   ", entry.getKey())) + (String.format("%-40s|", entry.getValue())));
                    }
                    System.out.println(String.format("| %70s|", "___").replaceAll(" ", "_"));
                    break;
                case 5:
                    System.out.print("Employee ID : ");
                    System.out.println(object.deleteById(sc.nextInt()));
                    sc.nextLine();
                    break;
                case 6:
                    flag=false;
                    break;
                default:
                    System.out.print("Enter Valid Option !");
                    Thread.sleep(3000);
                    break;
            }
        }

    }

    public static void getEmployeeDetails(int choice, CRUD object){
        ArrayList<String> keys=new ArrayList<>(Arrays.asList("employeeName", "employeeEmail", "employeeSalary", "employeeDesignation", "employeeAddress"));
        HashMap<String, String> employeeDetails = new HashMap<>();
        System.out.println(String.format(" %70s","___").replaceAll(" ","_"));
        if (choice == 4) {
            System.out.print(String.format("| %-25s|   ","Employee Id"));
            employeeDetails.put("employeeId", sc.nextInt() + "");
            sc.nextLine();
        }
        for (int i = 0; i <keys.size() ; i++) {
            System.out.print(String.format("| %-25s|   ", keys.get(i)));
            employeeDetails.put(keys.get(i),sc.nextLine());
        }
        System.out.println(String.format("| %70s|","___").replaceAll(" ","_"));
        if (choice == 1) {
            System.out.println(object.insert(employeeDetails));
        }
        if (choice == 4) {
            System.out.println(object.updateById(employeeDetails));
        }
    }
}