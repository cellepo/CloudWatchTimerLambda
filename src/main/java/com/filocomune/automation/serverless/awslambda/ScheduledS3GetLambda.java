package com.filocomune.automation.serverless.awslambda;

import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;

import static com.filocomune.automation.serverless.awslambda.util.LambdaRuntimeUtil.log;

/**
 * AWS Lambda triggered by CloudWatch Rule, that gets {@link S3Object} keyed by <s3Key> in <s3BucketName>.\n
 * \n
 * Action "logs:*" (in i.e: Policy "CloudWatchLogsFullAccess")\n
 * https://docs.aws.amazon.com/IAM/latest/UserGuide/list_amazons3.html\n
 * Action "s3:List*" (in i.e: Policy "AmazonS3ReadOnlyAccess")\n
 * Action "s3:Get*" (in i.e: Policy "AmazonS3ReadOnlyAccess")\n
 */
// https://blog.symphonia.io/learning-lambda-1f25af64161c
public class ScheduledS3GetLambda {

    protected final String s3BucketName = "<s3BucketName>";
    protected final String s3Key = "<s3Key>";

    // https://docs.aws.amazon.com/lambda/latest/dg/with-scheduledevents-example.html
    public void handle(ScheduledEvent scheduledEvent) {
        final S3Object s3Object
                = AmazonS3ClientBuilder.defaultClient().getObject(s3BucketName, s3Key);
        if(s3Object != null) {
            log("Retrieved S3Object {eTag: " + s3Object.getObjectMetadata().getETag() + "}");

        } else {
            log("null S3Object retrieved");
        }
    }

}

