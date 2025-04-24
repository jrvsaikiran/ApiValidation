package API;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Class to read test data from an Excel file.
 */
public class TestDataReader {

    /**
     * Reads data from an Excel file and returns it as a list of LinkedHashMaps.
     *
     * @return List of LinkedHashMaps containing the test data
     */
    public List<LinkedHashMap<String, String>> readTestData() {
        List<LinkedHashMap<String, String>> list = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream("src/test/java/API/TestData.xlsx");
             XSSFWorkbook wb = new XSSFWorkbook(fis)) {
            XSSFSheet sheet = wb.getSheet("run");
            int totalRows = sheet.getPhysicalNumberOfRows();
            System.out.println("Total rows in testdata:- " + (totalRows-1));

            List<String> keys = new ArrayList<>();
            DataFormatter df = new DataFormatter();

            for (int i = 0; i < totalRows; i++) {
                LinkedHashMap<String, String> hashmap = new LinkedHashMap<>();
                if (i == 0) {
                    // Read the header row
                    int physicalNumberOfCells = sheet.getRow(0).getPhysicalNumberOfCells();
                    for (int colNum = 0; colNum < physicalNumberOfCells; colNum++) {
                        String header = sheet.getRow(0).getCell(colNum).getStringCellValue();
                        keys.add(header);
                    }
                } else {
                    // Read the data rows
                    for (int colNum = 0; colNum < keys.size(); colNum++) {
                        String cell = df.formatCellValue(sheet.getRow(i).getCell(colNum));
                        hashmap.put(keys.get(colNum), cell);
                    }
                    list.add(hashmap);
                }
            }
            System.out.println("TestData successfully read");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}