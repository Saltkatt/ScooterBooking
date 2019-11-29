package com.fabrikam.functions;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    /**
     * This function listens at endpoint "/api/HttpTrigger-Java". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpTrigger-Java&code={your function key}
     * 2. curl "{your host}/api/HttpTrigger-Java?name=HTTP%20Query&code={your function key}"
     * Function Key is not needed when running locally, it is used to invoke function deployed to Azure.
     * More details: https://aka.ms/functions_authorization_keys
     */
    @FunctionName("HttpTrigger-Java")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.FUNCTION) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        String userName = System.getenv("userName");
        String password = System.getenv("password");

        TestBot testBot = new TestBot();
        Map<String, Boolean> ret = new HashMap<>();
        boolean testBotResult = testBot.performTesting(userName, password);
        ret.put("Result", testBotResult);

        context.getLogger().info("The TestBot is up and running: " + testBotResult);

        return (HttpResponseMessage) request.createResponseBuilder(HttpStatus.OK).body(ret);


    }

}
