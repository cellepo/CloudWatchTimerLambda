package com.filocomune.automation.serverless.awslambda;

import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;

import static com.filocomune.automation.serverless.awslambda.util.LambdaRuntimeUtil.log;

/**
 * Action "logs:*" (in i.e: Policy "CloudWatchLogsFullAccess")\n
 */
// https://blog.symphonia.io/learning-lambda-1f25af64161c
public class ScheduledLambda {

    // https://docs.aws.amazon.com/lambda/latest/dg/with-scheduledevents-example.html
    public void handle(ScheduledEvent scheduledEvent) {
        log("ScheduledLambda invoked by ScheduledEvent {id: " + scheduledEvent.getId() + "}");
    }

}
