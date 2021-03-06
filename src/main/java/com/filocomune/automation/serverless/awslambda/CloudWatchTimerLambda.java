package com.filocomune.automation.serverless.awslambda;

import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.filocomune.automation.serverless.awslambda.util.CloudWatchUtil;
import com.filocomune.automation.serverless.awslambda.util.S3Util;

import static com.filocomune.automation.serverless.awslambda.util.LambdaRuntimeUtil.log;

/**
 * AWS Lambda triggered by CloudWatch Rule,
 *  that first processes this concrete extension's job (per {@link #process(ScheduledEvent)}),
 *  and then finally keys (by "expiration-<scheduledEventARN>")
 *  current time String into S3_BUCKET_NAME (if not already keyed there to a prior time;
 *  otherwise disables the CloudWatch Rule and deletes the time stored in S3).\n
 * \n
 * Lambda Environment Variable "S3_BUCKET_NAME"\n
 * Lambda Environment Variable "scheduledEventRuleDurationMillis"\n
 * \n
 * Action "logs:*" (in i.e: Policy "AWSLambdaExecute")\n
 * https://docs.aws.amazon.com/IAM/latest/UserGuide/list_amazons3.html\n
 * Action "s3:List*" (in i.e: Policy "AmazonS3ReadOnlyAccess")\n
 * Action "s3:Get*" (in i.e: Policy "AmazonS3ReadOnlyAccess")\n
 * Action "s3:PutObject" (in i.e: Policy "AWSLambdaExecute")\n
 * Action "s3:DeleteObject"\n
 * https://docs.aws.amazon.com/AmazonCloudWatch/latest/events/permissions-reference-cwe.html\n
 * Action "events:DisableRule"\n
 */
// https://blog.symphonia.io/learning-lambda-1f25af64161c
public abstract class CloudWatchTimerLambda {

    protected final String s3BucketName = System.getenv("S3_BUCKET_NAME");

    public final long scheduledEventRuleDurationMillis
            = Long.parseLong(System.getenv("SCHEDULED_EVENT_RULE_EXPIRY_DURATION_MILLIS"));

    /**
     * Implement to process the concrete extension's job.
     *
     * @param scheduledEvent {@link ScheduledEvent}
     */
    abstract protected void process(ScheduledEvent scheduledEvent);

    // https://docs.aws.amazon.com/lambda/latest/dg/with-scheduledevents-example.html
    public void handle(ScheduledEvent scheduledEvent) {
        manageTimer(scheduledEvent);

        // Process after managing timer, so that any processing Exception does not prevent management
        try {
            process(scheduledEvent);

        } catch(Exception e) {
            log(getClass().getName() + "#process throws:");

            throw e;
        }
    }

    /**
     * **Only to be called once per Lambda instantiation,**\n
     * \n
     * Keys (by "expiration-<scheduledEventARN>")
     *  current time String into S3_BUCKET_NAME if not already keyed there to a prior time,
     *  otherwise disables the CloudWatch Rule and deletes the time stored in S3.
     *
     * @param scheduledEvent - {@link ScheduledEvent}
     */
    private void manageTimer(ScheduledEvent scheduledEvent){
        try {
            // https://docs.aws.amazon.com/lambda/latest/dg/with-scheduled-events.html
            final String scheduledEventARN = scheduledEvent.getResources().get(0);
            final String timedExpirationKey = "expiration-" + scheduledEventARN;
            try {
                final long scheduledEventRuleExpirationMillis
                        = Long.parseLong(S3Util.getString(s3BucketName, timedExpirationKey));
                if (scheduledEventRuleExpirationMillis < System.currentTimeMillis()) {
                    CloudWatchUtil.disableRule(scheduledEventARN);
                    log("  (expired at " + scheduledEventRuleExpirationMillis + ")");

                    S3Util.delete(s3BucketName, timedExpirationKey);

                } else {
                    log(timedExpirationKey + " is " + scheduledEventRuleExpirationMillis);
                }

            } catch (AmazonS3Exception as3e) {
                if (as3e.getStatusCode() == 404) {   // nothing already stored in S3 with timedExpirationKey
                    final String scheduleEventRuleExpiryMillis
                            = String.valueOf(System.currentTimeMillis() + scheduledEventRuleDurationMillis);
                    S3Util.putString(scheduleEventRuleExpiryMillis, s3BucketName, timedExpirationKey);
                    log(timedExpirationKey + " is " + scheduleEventRuleExpiryMillis);

                } else {
                    throw as3e;
                }
            }

        } catch(Exception e) {
            log(getClass().getName() + "#manageTimer throws:");

            throw e;
        }
    }

}

