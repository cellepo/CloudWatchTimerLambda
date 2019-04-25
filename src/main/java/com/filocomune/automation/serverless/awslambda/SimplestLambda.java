package com.filocomune.automation.serverless.awslambda;

/**
 * Action "logs:*" (in i.e: Policy "CloudWatchLogsFullAccess")\n
 */
// https://blog.symphonia.io/learning-lambda-1f25af64161c
public class SimplestLambda {

    public void handle(String s) {
        System.out.println("Hello, " + s);
    }

}
