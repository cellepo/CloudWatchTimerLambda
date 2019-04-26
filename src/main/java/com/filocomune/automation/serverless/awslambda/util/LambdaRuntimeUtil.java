package com.filocomune.automation.serverless.awslambda.util;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.LambdaRuntime;

/**
 * Action "logs:*" (in i.e: Policy "CloudWatchLogsFullAccess")\n
 */
public class LambdaRuntimeUtil {

    // https://docs.aws.amazon.com/lambda/latest/dg/java-logging.html
    private static final LambdaLogger logger = LambdaRuntime.getLogger();

    public static void log(String msg){
        logger.log(msg);
    }

    public static void log(Exception exception){
        log(exception.getClass().getName() + ": " + exception.getMessage());
    }

}
