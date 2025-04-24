package API;

import static API.ValidateAPITest.apiConfiguration;

public class Environment {

    // Type of the environment
    static String environment_Type = apiConfiguration.getEnvironment_Type();

    // Base URL for the API
    static String base_URL = apiConfiguration.getBase_URL();

    // AAA credentials
    static String username_AAA = apiConfiguration.getUsername_AAA();
    static String password_AAA = apiConfiguration.getPassword_AAA();
    static String client_id_AAA = apiConfiguration.getClient_id_AAA();

    // PING credentials
    static String client_id_PING = apiConfiguration.getClient_id_PING();
    static String client_secret_PING = apiConfiguration.getClient_secret_PING();

}