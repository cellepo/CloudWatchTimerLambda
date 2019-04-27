# CloudWatchTimerLambda
<i>CloudWatchTimerLambda is an AWS-Lambda Java base class that <string>disables its CloudWatch Rule (the rule that triggers your Lambda with a ScheduledEvent) after a timed expiration</strong>:<br>
  If for no other reason, <strong>use CloudWatchTimerLambda to lower billed AWS resources</strong>.<br>
</i>
### Detailed Usage: https://github.com/cellepo/CloudWatchTimerLambda/wiki
<a href="https://github.com/cellepo/ConcreteCloudWatchTimerExample">Extend CloudWatchTimerLambda by implementing its abstract <i>#process</i> method</a> (to do your concrete Lambda's job).<br>
Your Lambda will then have these benefits when it is run (all occur after processing):<br>
<br>
## Features
<ul>
  <li>Stores (if not already stored) an expiration time for the triggering CloudWatch Rule to expire and be disabled
    <ul>
      <li>The expiration is the sum of the time that storage of the expiration occurs, plus a duration:
        <ul>
          <li>Duration is configured by Lambda Environment Variable: "SCHEDULED_EVENT_RULE_EXPIRY_DURATION_MILLIS"
        </ul>
      <li>The expiration is stored in S3
        <ul>
          <li>Name of its S3 bucket is configured by Lambda Environment Variable: "S3_BUCKET_NAME"
        </ul>
    </ul>
  <li>If expiration is already stored, and current time is after it, then the triggering CloudWatch Rule expires:
    <ul>
      <li><strong>CloudWatch Rule is disabled</strong>
      <li>Stored expiration is deleted from S3
    </ul>
</ul>
