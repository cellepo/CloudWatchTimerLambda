package com.filocomune.automation.serverless.awslambda;

import com.amazonaws.services.cloudwatchevents.AmazonCloudWatchEvents;
import com.amazonaws.services.cloudwatchevents.AmazonCloudWatchEventsClientBuilder;
import com.amazonaws.services.cloudwatchevents.model.DisableRuleRequest;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.filocomune.automation.serverless.awslambda.util.CloudWatchUtil;
import com.filocomune.automation.serverless.awslambda.util.S3Util;

import static com.filocomune.automation.serverless.awslambda.util.LambdaRuntimeUtil.log;

/**
 * AWS Lambda triggered by CloudWatch Rule, that keys (by "expiration-<scheduledEventARN>") current time String
 * into S3_BUCKET_NAME if not already keyed there to a prior time, otherwise disables the CloudWatch Rule.\n
 * \n
 * Lambda Environment Variable "S3_BUCKET_NAME"\n
 * \n
 * Action "logs:*" (in i.e: Policy "AWSLambdaExecute")\n
 * https://docs.aws.amazon.com/IAM/latest/UserGuide/list_amazons3.html\n
 * Action "s3:List*" (in i.e: Policy "AmazonS3ReadOnlyAccess")\n
 * Action "s3:Get*" (in i.e: Policy "AmazonS3ReadOnlyAccess")\n
 * Action "s3:PutObject" (in i.e: Policy "AWSLambdaExecute")\n
 * https://docs.aws.amazon.com/AmazonCloudWatch/latest/events/permissions-reference-cwe.html\n
 * Action "events:DisableRule"\n
 */
// https://blog.symphonia.io/learning-lambda-1f25af64161c
public class CloudWatchTimerLambda {

    protected final String s3BucketName = System.getenv("S3_BUCKET_NAME");

    // https://docs.aws.amazon.com/lambda/latest/dg/with-scheduledevents-example.html
    public void handle(ScheduledEvent scheduledEvent) {
        // https://docs.aws.amazon.com/lambda/latest/dg/with-scheduled-events.html
        final String scheduledEventARN = scheduledEvent.getResources().get(0);
        final String timedExpirationKey = "expiration-" + scheduledEventARN;
        try {
            final long scheduledEventRuleExpirationMillis
                    = Long.parseLong(S3Util.getString(s3BucketName, timedExpirationKey));
            if(scheduledEventRuleExpirationMillis < System.currentTimeMillis()){
                CloudWatchUtil.disableRule(scheduledEventARN);
                log( "  (expired at " + scheduledEventRuleExpirationMillis + ")");


            } else {
                log(timedExpirationKey + " is " + scheduledEventRuleExpirationMillis);
            }

        } catch(AmazonS3Exception as3e){
            if(as3e.getStatusCode() == 404) {   // nothing already stored in S3 with timedExpirationKey
                S3Util.putString(String.valueOf(System.currentTimeMillis()), s3BucketName, timedExpirationKey);

            } else {
                throw as3e;
            }
        }
    }

}
