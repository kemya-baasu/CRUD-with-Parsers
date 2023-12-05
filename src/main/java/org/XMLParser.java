package org;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.*;

public class XMLParser implements CRUD{

     Document document;
     Element root;
    String xmlFilePath;
    ArrayList<String> keys = new ArrayList<>(Arrays.asList("employeeName", "employeeEmail", "employeeSalary", "employeeDesignation", "employeeAddress"));

    XMLParser(String path) {
        xmlFilePath=path;
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentFactory.newDocumentBuilder();
             document = documentBuilder.parse(xmlFilePath);
            document.getDocumentElement().normalize();
             root= document.getDocumentElement();
        }
        catch (Exception e) {
            System.out.println("Exception Thrown : "+e);
        }
    }

    @Override
    public void readAll() {
        NodeList nodeList = root.getElementsByTagName("employee");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            for (Map.Entry<String,String> entry :printElements(node).getEmployee().entrySet()) {
                System.out.println((String.format("| %-25s|   ", entry.getKey()))+(String.format("%-40s|",entry.getValue())));
            }
            System.out.println(String.format("| %70s|","___").replaceAll(" ","_"));
        }
    }

    @Override
    public Employee readById(int id) {
        Node node=getElementById(id);
        return printElements(node);
    }

    @Override
    public boolean insert(HashMap<String,String> employeeDetails) {
        try {
            long id = nextId();
            Element employee = document.createElement("employee");
            root.appendChild(employee);
            employee.setAttribute("EMPLOYEE", id+"");
            for (Map.Entry<String, String> entry : employeeDetails.entrySet()) {
                Element element = document.createElement(entry.getKey());
                element.appendChild(document.createTextNode(entry.getValue()));
                employee.appendChild(element);
            }
            printToXMLFile();
            return true;
        } catch (Exception e) {
            throw  new RuntimeException(e);
        }
    }

    @Override
   public boolean updateById(HashMap<String,String> employeeDetails){
        try {
            int employeeId= Integer.parseInt(employeeDetails.get("employeeId"));
            Element element = getElementById(employeeId);
            for (Map.Entry<String, String> entry : employeeDetails.entrySet()) {
                if(!entry.getKey().equals("employeeId")) {
                    Node nameNode = element.getElementsByTagName(entry.getKey()).item(0);
                    nameNode.setTextContent(entry.getValue());
                }
            }
            printToXMLFile();
            return true;
        }catch (Exception e){
            System.out.println(e.fillInStackTrace());
            return false;
        }
    }

    @Override
    public boolean deleteById(int id) {
        Element childNode = getElementById(id);
        if(childNode==null){
           return false;
        }
        else {
            try {
                Node parentNode = childNode.getParentNode();
                parentNode.removeChild(childNode);
                printToXMLFile();
                return true;
            }
            catch (Exception e){
                return false;
            }
        }
    }

    public Employee printElements(Node node) {
        HashMap<String,String> employeeObj=new HashMap<>();
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            employeeObj.put("employeeId",element.getAttribute("EMPLOYEE"));
            for (String l:keys) {
                employeeObj.put(l,element.getElementsByTagName(l).item(0).getTextContent());
            }
        }
        Employee emp_Obj=new Employee(employeeObj);
        return emp_Obj;
    }

    public void printToXMLFile() throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource domSource = new DOMSource(document);
        StreamResult result = new StreamResult(new File(xmlFilePath));
        transformer.transform(domSource, result);
    }

    public Element getElementById(int id) {
        Element element = null;
        NodeList nodeList = root.getElementsByTagName("employee");
        for (int i = 0; i < nodeList.getLength(); i++) {
            element = (Element) nodeList.item(i);
            if (String.valueOf(id).equals(element.getAttribute("EMPLOYEE"))) {
                return element;
            }
        }
        element=null;
        return element;

    }
    public long nextId(){
        long newId=1;
        NodeList nodeList = root.getElementsByTagName("employee");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            if(Integer.parseInt(element.getAttribute("EMPLOYEE"))>newId)
                newId = Integer.parseInt(element.getAttribute("EMPLOYEE"));
        }
        return nodeList.getLength()==0?1:newId+1;
    }
}
