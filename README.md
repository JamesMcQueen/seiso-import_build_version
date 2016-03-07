# Seiso Update Build Version
(seiso-import_build_version)


## Goals
BuildVersion data for Seiso should be continuously updated.

## Application Details
1. EOS Publishes a VersionChanged AWS SNS Notification.
2. A corresponding VersionChanged AWS SQS Queue is filled with messages delivered by subscribing to the SNS Notifications.
3. The Seiso Build Version Updater uses a scheduled cron to poll the SQS VersionChanged queue at a specified rate.
4. As the SQS Queue is polled verification and resolution is performed on the node id and message contents.
5. If the update is valid the messages are removed from the SQS Queue and the Seiso API is updated via HTTP PATCH.
6. The PATCH payload is a JSON Object in the form of {'buildVersion':'VERSION'}

## Notes
Using the SQS allows us to update securely, where previous attempts to use Lambda and HTTP Post Controller listening directly
to SNS Notifications fell short. Due to security concerns it is advised to use this technique for API Patches until
AWS has a solution for posting SNS to private IPs. Additionally this technique allows for data redundancy as the queue
retains the messages until polled where listening via POST Controller may fail if the controller is down.

## Extensibility
Other Seiso API attributes can easily be updated by changing the SQSRetriever input parameters and / or @autowiring additional
services accordingly.

## Author
James McQueen - <jmcqueen@expedia.com>