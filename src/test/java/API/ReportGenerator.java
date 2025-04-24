package API;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

/**
 * Class to generate an Excel report for API test results.
 */
public class ReportGenerator {

    public static final int HEADER = 4;
    public static final int TESTCASE_DESCRIPTION = 0;
    public static final int REQUEST_TYPE = 1;
    public static final int API_ENDPOINT = 2;
    public static final int AAA_RESPONSE_BODY = 3;
    public static final int PING_RESPONSE_BODY = 4;
    public static final int RESPONSE_BODY_COMPARISION = 5;
    public static final int AAA_RESPONSE_STATUS_CODE = 6;
    public static final int PING_RESPONSE_STATUS_CODE = 7;
    public static final int STATUS_CODE_COMPARISION = 8;
    private static final int AA_SCHEMA = 9;
    private static final int PING_SCHEMA = 10;


    private File reportDir;
    private static XSSFWorkbook wb;
    private static XSSFSheet sheet;
    private static  int IteratorRow = 5;
    private static ReportTemplate profileData;

    /**
     * Method to generate Excel report data.
     * @param reportTemplate
     */
    public void excelData(ReportTemplate reportTemplate){

         profileData = reportTemplate;

        try {
            if (wb == null) {
                wb = new XSSFWorkbook();
                sheet = wb.createSheet(Environment.environment_Type);
            }

            generateTitle();

            // Create the first row (header row)
            XSSFRow headerRow = sheet.createRow(HEADER);
            // Create header cells with light blue background
            String[] headers = {"TestCase_Description", "Request_Type", "API_Endpoint","AAA_Response_body","PING_Response_body","Status(Pass/Fail)",
                    "AAA_Response_Code","Ping_Response_Code","Status_Code_Comparison","AAA_Schema","Ping_Schema"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell1 = headerRow.createCell(i);
                cell1.setCellValue(headers[i]);
                // Apply style to header cells
                CellStyle headerStyle = wb.createCellStyle();
                headerStyle.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerStyle.setAlignment(HorizontalAlignment.CENTER);
                Font headerFont = wb.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);
                cell1.setCellStyle(headerStyle);
            }
            sheet.createFreezePane(0,5);    //TO freeze excel 1st row

            //create row data
            XSSFRow row = sheet.createRow(IteratorRow);
            IteratorRow++;
            row.createCell(TESTCASE_DESCRIPTION).setCellValue(profileData.getTestCase());
            row.createCell(REQUEST_TYPE).setCellValue(profileData.getRequestType());
            row.createCell(API_ENDPOINT).setCellValue(profileData.getApiName());
            row.createCell(AAA_RESPONSE_STATUS_CODE).setCellValue(profileData.getAAAResponse_Code());
            row.createCell(PING_RESPONSE_STATUS_CODE).setCellValue(profileData.getPINGResponse_Code());

            // Set color based on test status
            XSSFCell cell = row.createCell(RESPONSE_BODY_COMPARISION);
                    cell.setCellValue(profileData.getTest_Status());

            if(profileData.getTest_Status().equalsIgnoreCase("fail")){
                addColourToCell(IndexedColors.RED, cell);
            } else  {
                addColourToCell(IndexedColors.LIGHT_GREEN, cell);
            }

            // Set color based on result status code
            XSSFCell cell8 = row.createCell(STATUS_CODE_COMPARISION);
            cell8.setCellValue(profileData.getResult_statusCode());

            if(profileData.getResult_statusCode().equalsIgnoreCase("Not Expected Status Code")){
                addColourToCell(IndexedColors.RED, cell8);
            } else  {
                addColourToCell(IndexedColors.LIGHT_GREEN, cell8);
            }

            XSSFCell cell9 = row.createCell(AA_SCHEMA);
            cell9.setCellValue(profileData.getAAAschema());
            if(profileData.getAAAschema().equalsIgnoreCase("Not Expected Schema")){
                addColourToCell(IndexedColors.RED, cell9);
            } else  {
                addColourToCell(IndexedColors.LIGHT_GREEN, cell9);
            }

            XSSFCell cell10 = row.createCell(PING_SCHEMA);
            cell10.setCellValue(profileData.getPingschema());
            if(profileData.getPingschema().equalsIgnoreCase("Not Expected Schema")){
                addColourToCell(IndexedColors.RED, cell10);
            } else  {
                addColourToCell(IndexedColors.LIGHT_GREEN, cell10);
            }

            // Create hyperlinks for JSON response files
            int to_Createcell = AAA_RESPONSE_BODY;
            if(profileData.AAA_jsonResponse!=null){
                hyperLinkCreation(row, to_Createcell,profileData.AAA_jsonResponse);
            }

            int to_Createcell2 = PING_RESPONSE_BODY;
            if(profileData.PING_jsonResponse!=null){
                hyperLinkCreation(row, to_Createcell2,profileData.PING_jsonResponse);
            }

            // Apply filter to the first row
            sheet.setAutoFilter(CellRangeAddress.valueOf("A5:K5"));
            // Adjust column width for better visibility
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String[] s = ValidateAPITest.startTime.split(" ");
        String s1 = "_"+s[0];

        try {
            String apiPath = System.getProperty("user.dir");
            String p = apiPath+"/target"+File.separator+ Environment.environment_Type+s1+File.separator+"Report";
            if (reportDir==null) {
                reportDir = new File(p);
                reportDir.mkdir();
            }
            String finalPath = reportDir + File.separator + Environment.environment_Type+ ".xlsx";
            FileOutputStream fis = new FileOutputStream(finalPath);
            wb.write(fis);
            System.out.println("Report Generated Successfully");

        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFoundException---->>>>" + e);
        } catch (IOException e) {
            throw new RuntimeException("IOException----->>>" + e);
        }


    }

    /**
     * Method to generate the title for the Excel report.
     */
    private void generateTitle() {
        if(sheet.getMergedRegions().isEmpty()){
            sheet.addMergedRegion(new CellRangeAddress(0,3,0,10));
        }
        XSSFRow titleRow = sheet.createRow(0);
        XSSFCell title = titleRow.createCell(0);
        XSSFRichTextString rich = new XSSFRichTextString();
        XSSFRichTextString title1 = textColour(wb, IndexedColors.BLUE.getIndex(), "AUTOMATED TEST REPORT "+" \n", rich);
        XSSFRichTextString startTime = textColour(wb, IndexedColors.BROWN.getIndex(),  "Test Start Time:- "+ ValidateAPITest.startTime+"\n", rich);
        XSSFRichTextString endTime = textColour(wb, IndexedColors.SEA_GREEN.getIndex(),  "Test Duration:- "+ ValidateAPITest.taskTime+"\n", rich);
        XSSFRichTextString env = textColour(wb, IndexedColors.ROYAL_BLUE.getIndex(),  "Test Environment:- "+ Environment.environment_Type+"\n", rich);
        XSSFRichTextString pass= textColour(wb,IndexedColors.GREEN.getIndex(),"Total Passed:- "+profileData.getPassCount()+"\n",rich );
        XSSFRichTextString fail= textColour(wb,IndexedColors.RED.getIndex(),"Total Failed:- "+profileData.getFailCount()+"\n",rich );

        addColourToCell(IndexedColors.WHITE1,title);
        title.setCellValue(title1);
        title.setCellValue(startTime);
        title.setCellValue(endTime);
        title.setCellValue(env);
        title.setCellValue(pass);
        title.setCellValue(fail);
    }

    /**
     * Method to set text color for a given string.
     *
     * @param wb    The workbook
     * @param index The color index
     * @param title The title text
     * @param rich  The rich text string
     * @return The formatted rich text string
     */
    private XSSFRichTextString textColour(XSSFWorkbook wb, short index, String title, XSSFRichTextString rich) {
        XSSFFont font = wb.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 11);
        font.setColor(index);
        font.setBold(true);
        rich.append(title,font);
        return rich;
    }

    /**
     * Method to add color to a cell.
     *
     * @param colour The color to be added
     * @param cell   The cell to be colored
     */
    private static void addColourToCell(IndexedColors colour, XSSFCell cell) {
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFillForegroundColor(colour.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cell.setCellStyle(cellStyle);
    }

    /**
     * Method to create a hyperlink in a cell.
     *
     * @param row          The row where the cell is located
     * @param to_Createcell The cell index
     * @param jsonLocation The location of the JSON file
     */
    private static void hyperLinkCreation(XSSFRow row, int to_Createcell, String jsonLocation) {
        XSSFCell cell = row.createCell(to_Createcell);
        if(jsonLocation.contains("AAA")){
            cell.setCellValue("AAA_"+profileData.getTestNumber());
        } else if (jsonLocation.contains("PING")) {
            cell.setCellValue("PING_"+profileData.getTestNumber());
        }
        CreationHelper createHelper = wb.getCreationHelper();
        Hyperlink link = createHelper.createHyperlink(HyperlinkType.URL);
        String replace = null;
        try {
            replace = jsonLocation.replace("\\", "/");
            URI uri = new File(replace).toURI();
            link.setAddress(uri.toString());
            System.out.println(replace);
        } catch (Exception e) {
            throw new RuntimeException("Invalid URI for hyperlink: " + replace, e);
        }
        cell.setHyperlink(link);
        cell.setCellStyle(createHyperlinkStyle(wb));
    }

    /**
     * Method to create a hyperlink style.
     *
     * @param workbook The workbook
     * @return The hyperlink style
     */
    private static CellStyle createHyperlinkStyle(Workbook workbook) {
        Font font = workbook.createFont();
        font.setUnderline(Font.U_SINGLE);
        font.setColor(IndexedColors.BLUE.getIndex());
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        return style;
    }
}
