package API;

     import lombok.Data;

     /**
      * Class representing the API constructor with various fields related to API testing.
      */
     @Data
     public class ReportTemplate {
         String testCase; // Description of the test case
         String requestType; // Type of the request (e.g., GET, POST)
         String apiName; // Name of the API endpoint
         String AAA_jsonResponse; // JSON response from AAA
         String PING_jsonResponse; // JSON response from PING
         String test_Status; // Status of the test (e.g., PASS, FAIL)
         String testNumber; // Number of the test case
         String AAAResponse_Code; // Response code from AAA
         String PINGResponse_Code; // Response code from PING
         String result_statusCode; // Result status code after comparison
         int passCount; // Count of passed tests
         int failCount; // Count of failed tests
         String AAAschema; // JSON schema for AAA
         String Pingschema;  // JSON schema for PING

         /**
          * Constructor to initialize the API_Constructor object with the given parameters.
          *
          * @param testCase Description of the test case
          * @param requestType Type of the request (e.g., GET, POST)
          * @param apiName Name of the API endpoint
          * @param AAA_jsonResponse JSON response from AAA
          * @param PING_jsonResponse JSON response from PING
          * @param test_Status Status of the test (e.g., PASS, FAIL)
          * @param testNumber Number of the test case
          * @param AAAResponse_Code Response code from AAA
          * @param PINGResponse_Code Response code from PING
          * @param result_statusCode Result status code after comparison
          * @param passCount Count of passed tests
          * @param failCount Count of failed tests
          * @param AAAschema JSON schema for AAA
          * @param Pingschema JSON schema for PING
          */
         ReportTemplate(String testCase, String requestType, String apiName, String AAA_jsonResponse, String PING_jsonResponse, String test_Status,
                        String testNumber, String AAAResponse_Code, String PINGResponse_Code, String result_statusCode, int passCount, int failCount,String AAAschema,
                        String Pingschema) {
             this.testCase = testCase;
             this.requestType = requestType;
             this.apiName = apiName;
             this.AAA_jsonResponse = AAA_jsonResponse;
             this.PING_jsonResponse = PING_jsonResponse;
             this.test_Status = test_Status;
             this.testNumber = testNumber;
             this.AAAResponse_Code = AAAResponse_Code;
             this.PINGResponse_Code = PINGResponse_Code;
             this.result_statusCode = result_statusCode;
             this.passCount = passCount;
             this.failCount = failCount;
             this.AAAschema = AAAschema;
             this.Pingschema = Pingschema;
         }
     }