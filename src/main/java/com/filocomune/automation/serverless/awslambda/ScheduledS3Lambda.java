package com.filocomune.automation.serverless.awslambda;

import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;

import static com.filocomune.automation.serverless.awslambda.util.LambdaRuntimeUtil.log;

/**
 * AWS Lambda triggered by CloudWatch Rule,
 *  that keys (by S3_KEY) current time String into S3_BUCKET_NAME if not already keyed there to a prior time.\n
 * \n
 * Lambda Environment Variable "S3_BUCKET_NAME"\n
 * Lambda Environment Variable "S3_KEY"\n
 * \n
 * Action "logs:*" (in i.e: Policy "AWSLambdaExecute")\n
 * https://docs.aws.amazon.com/IAM/latest/UserGuide/list_amazons3.html\n
 * Action "s3:List*" (in i.e: Policy "AmazonS3ReadOnlyAccess")\n
 * Action "s3:Get*" (in i.e: Policy "AmazonS3ReadOnlyAccess")\n
 * Action "s3:PutObject" (in i.e: Policy "AWSLambdaExecute")\n
 */
// https://blog.symphonia.io/learning-lambda-1f25af64161c
public class ScheduledS3Lambda {

    protected final String s3BucketName = System.getenv("S3_BUCKET_NAME");
    protected final String s3Key = System.getenv("S3_KEY");

    protected static final AmazonS3 amazonS3 = AmazonS3ClientBuilder.defaultClient();

    // https://docs.aws.amazon.com/lambda/latest/dg/with-scheduledevents-example.html
    public void handle(ScheduledEvent scheduledEvent) {
        try {
            final S3Object s3Object = amazonS3.getObject(s3BucketName, s3Key);
            if (s3Object != null) {
                log("Retrieved S3Object {eTag: " + s3Object.getObjectMetadata().getETag() + "}");

            } else {
                log("null S3Object retrieved");
            }

        } catch(AmazonS3Exception as3e){
            if(as3e.getStatusCode() == 404) {   // nothing already stored in S3 with timedExpirationKey
                final PutObjectResult putObjectResult = amazonS3.putObject(
                        s3BucketName, s3Key, String.valueOf(System.currentTimeMillis()));
                log("Stored S3Object {eTag: " + putObjectResult.getETag() + "}");

            } else {
                throw as3e;
            }
        }
    }

}

