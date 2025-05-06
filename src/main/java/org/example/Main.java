package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {




        ObjectMapper mapper = new ObjectMapper();
        File file = new File("C:\\Users\\rajavenkatasaikiran_\\Downloads\\Universal_API_Validation\\src\\main\\java\\org\\example\\file.json");
        if (file.exists()) {
            System.out.println("File found: " + file.getAbsolutePath());
        } else {
            System.out.println("File not found: " + file.getAbsolutePath());
        }
        JsonNode root = mapper.readTree(file);
        parseJson(root, "");
        System.out.println("Hello world!");
    }

    public static void parseJson(JsonNode node, String path) throws Exception {
        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String currentPath = path.isEmpty() ? entry.getKey() : path + "." + entry.getKey();
                parseJson(entry.getValue(), currentPath);  // Recursive call
            }
        } else if (node.isArray()) {
            int index = 0;
            for (JsonNode item : node) {
                parseJson(item, path + "[" + index + "]");  // Recursive call
                index++;
            }
        } else {
            System.out.println(path + " : " + node.asText());  // Leaf value
            if(node.asText().isEmpty() || node.asText()==null){
//                throw new Exception("--------->>>>>>>>>");
            }
        }
    }
}