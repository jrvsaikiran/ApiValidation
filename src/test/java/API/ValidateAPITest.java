package API;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.reflections.Reflections;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static io.restassured.RestAssured.given;

        /**
         * Class to handle API testing for Ping Migration.
         */
        @JsonIgnoreProperties(ignoreUnknown = true)
        public class ValidateAPITest {

            private TestDataReader excelRead = new TestDataReader();
            private File dir;
            public static File finalPath;
            private String api_EndPoint;
            private String fullApi;
            private String api_PayLoad;
            private LinkedHashMap<Object, Object> resultMap;
            public static String startTime;
            public static String endTime;
            private SimpleDateFormat simpleDateFormat;
            private Instant startingTime;
            public static String taskTime;
            public static int passCount = 0;
            public static int failCount = 0;
            static ApiConfiguration apiConfiguration;

            /**
             * Test method to validate AAA and Ping responses.
             *
             */
            @Test
            public void API_Validations() {
                 apiConfiguration = new ApiConfiguration();
                // Read the test data from the Excel file
                List<LinkedHashMap<String, String>> reader = excelRead.readTestData();

                // Initialize start time tracking
                taskStartTime();

                reader.stream().filter(hashMap -> "yes".equalsIgnoreCase(hashMap.get("Flag"))).forEach(hashMap -> {
                    try {

                        // Retrieve and trim from the hashMap
                        String requestType = hashMap.get("RequestType").trim();
                        api_EndPoint = hashMap.get("API Name").trim();
                        String testCase = hashMap.get("TestCase").trim();
                        String testNumber = hashMap.get("TestNumber");
                        api_PayLoad = hashMap.get("Body");
                        String className = hashMap.get("Class Name");

                        // Get tokens for AAA and Ping
                        String AAAtoken = getAAAToken();
                        String pingToken = getPingToken();

                        /**
                         * Perform an HTTP request to the AAA endpoint.
                         *
                         * @param AAAtoken the authorization token for AAA
                         * @param requestType the HTTP method (GET, POST, DELETE)
                         */
                        performHttpRequest(AAAtoken, requestType);

                        // Retrieve the AAA response body from the result map and convert it to a string
                        String AAAResponse = Optional.ofNullable(resultMap.get("responseBody")).map(Object::toString).orElse("");
                        // Retrieve the AAA response code from the result map and convert it to a string
                        String AAAResponse_Code = Optional.ofNullable(resultMap.get("responseCode")).map(Object::toString).orElse("");
                        // If the AAA response is not empty, convert it to a JSON file
                        String AAA_jsonResponse_path = "";
                        if (!AAAResponse.isEmpty()) {
                            String AAA = "AAA_";
                            AAA_jsonResponse_path = convertToJsonFile(testNumber, AAAResponse, AAA);
                        }


                        boolean b = validateSchemaOfResponseBody(className, AAAResponse);
                        String AAAschema="";
                        if(b){
                            AAAschema = "Expected Schema";
                        }else {
                            AAAschema = "Not Expected Schema";
                        }

                        /**
                         * Perform an HTTP request to the Ping endpoint.
                         *
                         * @param pingToken the authorization token for Ping
                         * @param requestType the HTTP method (GET, POST, DELETE)
                         */
                        performHttpRequest(pingToken, requestType);

                        // Retrieve the Ping response body from the result map and convert it to a string
                        String pingresponse = Optional.ofNullable(resultMap.get("responseBody")).map(Object::toString).orElse("");
                        // Retrieve the Ping response code from the result map and convert it to a string
                        String PINGResponse_Code = Optional.ofNullable(resultMap.get("responseCode")).map(Object::toString).orElse("");
                        // If the Ping response is not empty, convert it to a JSON file
                        String PING_jsonResponse_path = "";
                        if (!pingresponse.isEmpty()) {
                            String PING = "PING_";
                            PING_jsonResponse_path = convertToJsonFile(testNumber, pingresponse, PING);
                        }
                        boolean b1 = validateSchemaOfResponseBody(className, pingresponse);
                        String Pingschema="";
                        if(b1){
                            Pingschema = "Expected Schema";
                        }else {
                            Pingschema = "Not Expected Schema";
                        }

                        // End time tracking
                        taskEndTime();

                       // Compare responses and update test status
                       // If the AAA response body matches the Ping response body and both response codes start with "2", set the test status to "PASS".
                       // Otherwise, set the test status to "FAIL".
                        String test_Status = AAAResponse.equals(pingresponse) && AAAResponse_Code.startsWith("2")
                                && PINGResponse_Code.startsWith("2") ? "PASS" : "FAIL";

                       // Compare the AAA and Ping response codes and set the result status code
                       // If the AAA response code matches the Ping response code and both start with "2", set the result status code to "Expected Status code".
                       // Otherwise, set the result status code to "Not Expected Status Code".
                        String result_statusCode = AAAResponse_Code.equalsIgnoreCase(PINGResponse_Code) && AAAResponse_Code.startsWith("2")
                                && PINGResponse_Code.startsWith("2") ? "Expected Status code" : "Not Expected Status Code";

                        // Increment the pass count if the test status is "PASS", otherwise increment the fail count
                        if ("PASS".equals(test_Status)) {
                            passCount++;
                        } else {
                            failCount++;
                        }

                       // Create a ReportConstructor object with the test details and results
                        ReportTemplate reportTemplate = new ReportTemplate(testCase, requestType, fullApi, AAA_jsonResponse_path,
                                PING_jsonResponse_path, test_Status, testNumber, AAAResponse_Code, PINGResponse_Code, result_statusCode, passCount, failCount,AAAschema,Pingschema);

                        // Create a Report object and write the test results to an Excel file
                        ReportGenerator excel = new ReportGenerator();
                        excel.excelData(reportTemplate);

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            private static boolean validateSchemaOfResponseBody(String className, String responceBody) {
                // Specify the package to scan
                String packageName = "jsonschemas";

                // Use Reflections to scan the package
                Reflections reflections = new Reflections(packageName, new org.reflections.scanners.SubTypesScanner(false));

                // Get all classes in the package
                Set<Class<?>> classes = reflections.getSubTypesOf(Object.class);

//                classes.forEach(clazz -> System.out.println(clazz.getName()));
                String expectedClass = packageName + "." + className;
                // Filter classes based on the class name
                Optional<Class<?>> matchingClass = classes.stream()
                        .filter(clazz -> clazz.getName().equals(expectedClass))
                        .findFirst();
                boolean flag = false;
                if (matchingClass.isPresent()) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    try {
                        Object deserializedObject = objectMapper.readValue(responceBody, matchingClass.get());
                        System.out.println("Deserialized object: " + deserializedObject);
                         flag = true;
                    } catch (JsonProcessingException e) {
                        System.out.println("Error deserializing JSON response: " + e.getMessage());
//                        throw new RuntimeException("Error deserializing JSON response: " + e.getMessage(), e);
                    }
                } else {
                    System.out.println("No matching class found for: " + className);
                }
                return flag;
            }

            /**
             * Method to initialize start time tracking.
             */
            private void taskStartTime() {
                simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                startTime = simpleDateFormat.format(new Date());
                startingTime = Instant.now();
            }

            /**
             * Method to initialize end time tracking.
             */
            private void taskEndTime() {
                simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                endTime = simpleDateFormat.format(new Date());

                Instant endTime = Instant.now();
                Duration duration = Duration.between(startingTime, endTime);
                long hours = duration.toHours();
                long minutes = duration.toMinutes() % 60;
                long seconds = duration.getSeconds() % 60;
                taskTime = String.join(" ", hours + " hrs", minutes + " min", seconds + " sec");
            }

           /**
            * Method to perform an HTTP request.
            *
            * @param token the authorization token
            * @param method the HTTP method (GET, POST, DELETE)
            * @return a map containing the response body and response code
            */
           private LinkedHashMap<Object, Object> performHttpRequest(String token, String method) {
               fullApi = Environment.base_URL + api_EndPoint;
               endpointContainsPercentage();
               Response response;

               switch (method.toLowerCase()) {
                   case "post":
                       response = given()
                               .header("Authorization", "Bearer " + token)
                               .header("Content-Type", "application/json")
                               .header("accept-language", "en")
                               .body(api_PayLoad)
                               .when()
                               .post(fullApi);
                       break;
                   case "delete":
                       response = given()
                               .header("Authorization", "Bearer " + token)
                               .header("Content-Type", "application/json")
                               .header("accept-language", "en")
                               .when()
                               .delete(fullApi);
                       break;
                   case "get":
                       response = given()
                               .header("Authorization", "Bearer " + token)
                               .header("Content-Type", "application/json")
                               .when()
                               .get(fullApi);
                       break;
                   default:
                          throw new IllegalArgumentException("Invalid HTTP method:------> " + method);
               }

               resultMap = new LinkedHashMap<>();
               resultMap.put("responseBody", response.getBody().prettyPrint().toString());
               resultMap.put("responseCode", String.valueOf(response.getStatusCode()));
               return resultMap;
           }

            /**
             * Method to encode the API endpoint if it contains a percentage sign.
             */
            private void endpointContainsPercentage() {
                if (api_EndPoint.contains("%")) {
                    String[] split = fullApi.split("\\?");
                    String encode;
                    try {
                        encode = URLEncoder.encode(split[1], "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                    fullApi = split[0] + "?" + encode;
                }
            }

            /**
             * Method to convert a JSON response to a file.
             *
             * @param testNumber   the test number
             * @param jsonResponse the JSON response
             * @param AuthType     the authorization type
             * @return the path to the JSON file
             * @throws IOException if an I/O error occurs
             */
            private String convertToJsonFile(String testNumber, String jsonResponse, String AuthType) throws IOException {
                ObjectMapper obj = new ObjectMapper();
                JsonNode node = obj.readTree(jsonResponse);
                String apiPath = System.getProperty("user.dir");
                String[] s = ValidateAPITest.startTime.split(" ");
                String s1 = "_" + s[0];
                String p = apiPath + "/target" + File.separator + Environment.environment_Type + s1;

                if (dir == null) {
                    dir = new File(p);
                    dir.mkdir();
                }
                String jsonPath = p + File.separator + AuthType + testNumber + ".json";
                try {
                    finalPath = new File(jsonPath);
                    obj.writerWithDefaultPrettyPrinter().writeValue(new File(jsonPath), node);
                    System.out.println("Successfully json response file created");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return finalPath.toString();
            }

            /**
             * Method to get the AAA token.
             *
             * @return the AAA token
             */
            private String getAAAToken() {
                Response response = null;
                try {
                    response = given()
                            .header("Accept", "application/json")
                            .contentType("application/x-www-form-urlencoded")
                            .formParam("grant_type", "password")
                            .formParam("username", Environment.username_AAA)
                            .formParam("password", Environment.password_AAA)
                            .formParam("scope", "trapi")
                            .formParam("takeExclusiveSignOnControl", "true")
                            .formParam("client_id", Environment.client_id_AAA)
                            .when()
                            .post(Environment.base_URL + "/auth/oauth2/v1/token");
                    String token = response.jsonPath().get("access_token").toString();
                    Assert.assertEquals(response.getStatusCode(), 200);
                    return token;
                } catch (Exception e) {
                    System.out.println(response.getBody().prettyPrint());
                    throw new RuntimeException("Error in getting AAA token: " + e.getLocalizedMessage());
                }
            }

            /**
             * Method to get the Ping token.
             *
             * @return the Ping token
             */
            private String getPingToken() {
                Response response = null;
                try {
                    response = given()
                            .header("Accept", "application/json")
                            .contentType("application/x-www-form-urlencoded")
                            .formParam("grant_type", "client_credentials")
                            .formParam("password", "Welcome2")
                            .formParam("scope", "trapi")
                            .formParam("takeExclusiveSignOnControl", "true")
                            .formParam("client_id", Environment.client_id_PING)
                            .formParam("username", "wealthtest1@lseg.com")
                            .formParam("client_secret", Environment.client_secret_PING)
                            .when()
                            .post(Environment.base_URL + "/auth/oauth2/v2/token");
                    String token = response.jsonPath().get("access_token").toString();
                    Assert.assertEquals(response.getStatusCode(), 200);
                    return token;
                } catch (Exception e) {
                    System.out.println(response.getBody().prettyPrint());
                    throw new RuntimeException("Error in getting Ping token: " + e.getLocalizedMessage());
                }
            }
        }