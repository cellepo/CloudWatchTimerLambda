package com.filocomune.automation.serverless.awslambda.util;

import com.amazonaws.services.cloudwatchevents.AmazonCloudWatchEvents;
import com.amazonaws.services.cloudwatchevents.AmazonCloudWatchEventsClientBuilder;
import com.amazonaws.services.cloudwatchevents.model.AmazonCloudWatchEventsException;
import com.amazonaws.services.cloudwatchevents.model.DisableRuleRequest;

import static com.filocomune.automation.serverless.awslambda.util.LambdaRuntimeUtil.log;

/**
 *  Action "logs:*" (in i.e: Policy "AWSLambdaExecute")\n
 *  https://docs.aws.amazon.com/AmazonCloudWatch/latest/events/permissions-reference-cwe.html\n
 *  Action "events:DisableRule"\n
 */
public class CloudWatchUtil {

    private static final AmazonCloudWatchEvents amazonCloudWatchEvents
            = AmazonCloudWatchEventsClientBuilder.defaultClient();

    // https://docs.aws.amazon.com/lambda/latest/dg/with-scheduled-events.html
    private static final String cloudWatchRuleNameARNPrefix = ":rule/";

    /**
     * Action "events:DisableRule"\n
     * Action "logs:*" (in i.e: Policy "AWSLambdaExecute")\n
     *
     * @param ruleARN String
     * @throws {@link AmazonCloudWatchEventsException}
     */
    public static void disableRule(String ruleARN) throws AmazonCloudWatchEventsException {
        // https://docs.aws.amazon.com/lambda/latest/dg/with-scheduled-events.html
        final String cloudWatchRuleName = ruleARN.substring(
                ruleARN.indexOf(cloudWatchRuleNameARNPrefix) + cloudWatchRuleNameARNPrefix.length());

        amazonCloudWatchEvents.disableRule(new DisableRuleRequest().withName(cloudWatchRuleName));
        log(ruleARN + " disabled");
    }

}
