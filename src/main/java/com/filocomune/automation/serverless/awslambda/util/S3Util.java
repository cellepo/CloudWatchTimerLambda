package com.filocomune.automation.serverless.awslambda.util;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;

import static com.filocomune.automation.serverless.awslambda.util.LambdaRuntimeUtil.log;

/**
 * Action "logs:*" (in i.e: Policy "AWSLambdaExecute")\n
 * https://docs.aws.amazon.com/IAM/latest/UserGuide/list_amazons3.html\n
 * Action "s3:PutObject" (in i.e: Policy "AWSLambdaExecute")\n
 * Action "s3:List*" (in i.e: Policy "AmazonS3ReadOnlyAccess")\n
 * Action "s3:Get*" (in i.e: Policy "AmazonS3ReadOnlyAccess")\n
 */
public class S3Util {

    private static final AmazonS3 amazonS3 = AmazonS3ClientBuilder.defaultClient();

    /**
     * Action "logs:*" (in i.e: Policy "AWSLambdaExecute")\n
     * Action "s3:PutObject" (in i.e: Policy "AWSLambdaExecute")\n
     *
     * @param string to put:  Becomes encoded UTF-8.
     * @param bucketName
     * @param key
     * @return {@link PutObjectResult}
     * @throws SdkClientException
     */
    public static PutObjectResult putString(String string, String bucketName, String key) throws SdkClientException {
        final PutObjectResult putObjectResult = amazonS3.putObject(bucketName, key, string);
        log("Stored S3Object {eTag: " + putObjectResult.getETag() + "}");

        return putObjectResult;
    }

    /**
     * Action "logs:*" (in i.e: Policy "AWSLambdaExecute")\n
     * Action "s3:List*" (in i.e: Policy "AmazonS3ReadOnlyAccess")\n
     * Action "s3:Get*" (in i.e: Policy "AmazonS3ReadOnlyAccess")\n
     *
     * @param bucketName
     * @param key
     * @return {@link S3Object}
     * @throws SdkClientException
     */
    public static S3Object getObject(String bucketName, String key) throws SdkClientException {
        final S3Object s3Object = amazonS3.getObject(bucketName, key);
        if (s3Object != null) {
            log("Retrieved S3Object {eTag: " + s3Object.getObjectMetadata().getETag() + "}");

        } else {
            log("null S3Object retrieved");
        }

        return s3Object;
    }

    /**
     * Action "logs:*" (in i.e: Policy "AWSLambdaExecute")\n
     * Action "s3:List*" (in i.e: Policy "AmazonS3ReadOnlyAccess")\n
     * Action "s3:Get*" (in i.e: Policy "AmazonS3ReadOnlyAccess")\n
     *
     * @param bucketName
     * @param key
     * @return String
     * @throws SdkClientException
     */
    public static String getString(String bucketName, String key) throws SdkClientException {
        // Get metadata before string
        final String stringETag = amazonS3.getObjectMetadata(bucketName, key).getETag();
        final String string = amazonS3.getObjectAsString(bucketName, key);
        if(string != null){
            log("Retrieved [as String] S3Object {eTag: " + stringETag + "}");

        } else {
            log("null S3Object retrieved");
        }

        return string;
    }

}
