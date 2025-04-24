package API;

import lombok.Data;
//ApiConfiguration
/**
 * Class representing API configuration parameters.
 */
@Data
public class ApiConfiguration {
    public static final String PPE = "https://api.ppe.refinitiv.com";
    public static final String PROD = "https://api.refinitiv.com";
    private String environment_Type;
    private String base_URL;
    private String username_AAA;
    private String password_AAA;
    private String client_id_AAA;
    private String client_id_PING;
    private String client_secret_PING;

    /**
     * Default constructor that initializes the API configuration parameters
     * from system properties and prints them to the console.
     */
    public ApiConfiguration() {
        setEnvironment_Type(retrieveCredential("environment_Type"));
        setBase_URL(getBaseURL(getEnvironment_Type()));
        setUsername_AAA(retrieveCredential("username_AAA"));
        setPassword_AAA(retrieveCredential("password_AAA"));
        setClient_id_AAA(retrieveCredential("client_id_AAA"));
        setClient_id_PING(retrieveCredential("client_id_PING"));
        setClient_secret_PING(retrieveCredential("client_secret_PING"));

        System.out.println("Environment_Type: " + getEnvironment_Type());
        System.out.println("Base_URL: " + getBase_URL());
        System.out.println("Username_AAA: " + getUsername_AAA());
        System.out.println("Password_AAA: " + getPassword_AAA());
        System.out.println("Client_id_AAA: " + getClient_id_AAA());
        System.out.println("Client_id_PING: " + getClient_id_PING());
        System.out.println("Client_secret_PING: " + getClient_secret_PING());
    }

    /**
     * Returns the base URL based on the provided environment type.
     *
     * @param environmentType the type of environment (e.g., PPE, PROD)
     * @return the base URL corresponding to the environment type
     * @throws RuntimeException if the environment type is invalid
     */
    private String getBaseURL(String environmentType) {
        if (environmentType.contains("PPE") || environmentType.contains("PPE_Environment")
                || environmentType.equalsIgnoreCase("ppe_environment")) {
            return PPE;
        } else if (environmentType.contains("PROD") || environmentType.contains("PROD_Environment")
                || environmentType.equalsIgnoreCase("prod_environment")) {
            return PROD;
        } else {
            throw new RuntimeException("Invalid environment type--------->" + environmentType);
        }
    }

    /**
     * Retrieves the value of a system property and trims any leading or trailing whitespace.
     * @param credential the name of the system property to retrieve
     * @return the trimmed value of the system property
     * @throws RuntimeException if the system property is not found or an error occurs
     */
    private static String retrieveCredential(String credential) {
        try {
            return System.getProperty(credential).trim();
        } catch (Exception e) {
            throw new RuntimeException("Please provide the parameter " + credential + " in the command line");
        }
    }
}