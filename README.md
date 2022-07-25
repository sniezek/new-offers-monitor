# new-offers-monitor

## Description
A program that periodically fetches online marketplaces for the latest offers matching the search criteria defined in the program configuration and sends email notifications about new offers.

Many online marketplaces provide notification systems but usually they are very limited. The goal of this program is to notify a user about a new offer as soon as possible.

The program supports 10 popular Polish websites with real estate sale & rental offers:
* `Otodom.pl`
* `OLX.pl` (a marketplace for everything, including real estates)
* `Adresowo.pl`
* `Morizon.pl`
* `Domiporta.pl`
* `Nieruchomosci-online.pl`
* `Dominium.pl`
* `Szybko.pl`
* `Sprzedajemy.pl`
* `Gratka.pl`

and a popular Polish marketplace with car sale offers: `Otomoto.pl`.

## How to run
The program can be run either locally, as a Java 11 program run on a computer, or on AWS, as a Lambda function (Java 11 Corretto).

Either way, the program requires two things to be done before running:
1. For the email notifications to be sent, a Gmail email address will be needed to send them. It is also required to go to the Google account Security settings, enable two-step verification and create an application-dedicated password that will be used by this program. Lastly, three environment variables must be added for the program to work: `SMTP_EMAIL_ENV_VAR` (an email address), `SMTP_PASSWORD_ENV_VAR` (the email password) and `ADMIN_EMAIL_ENV_VAR` (the email address that will receive notifications about any issues). The program can also be used without email notifications (e.g. to only create a database of offers) by replacing the `GmailEmailSender` object with a `NoopNotificationSender` object in `LocalUseMain`/`AwsLambdaRequestHandler`.
2. Defining search criteria configurations - the program can work for one or more users, and each user can have their own configuration. A configuration is a list of URLs of pages the program should monitor for new offers. Each such page must be a list of offers sorted from newest and can also be marked as active (to be currently monitored) or not. A configuration must have a unique name, can also be set as active or inactive and can be provided with a set of keywords that, if any of them is present in an offer URL, will result in the program ignoring that particular offer and not send a notification. Additionally, a list of keywords that are more desired in offers can be provided, and the program will highlight in the notification that the offer has one of those keywords. The configurations are defined in the `src/main/resources/configurations/configurations.json` file.

### Local
The program can be started by running `LocalUseMain`. A database of seen offers will be filled in the `filedb_offers.json` file. It can be controlled how often the program should run by changing the `RUN_MONITOR_EVERY_MILLIS` constant.

### AWS
Instructions to make the program work on AWS:
1. Create a DynamoDB table `Offers` with `configurationName` partition key and `urlAndPrice` sort key.
2. Create a Java 11 Corretto Lambda function and grant permissions for the Lambda to read and write to DynamoDB.
3. Add a scheduled EventBridge trigger that will periodically trigger the Lambda.
4. Generate a Java zip using a `buildZip` task in `build.gradle`.
5. Deploy the zip into the Lambda function.
6. Make sure the timeout value of the lambda is not too low for the defined configurations and pages to be processed.

## Extension
The program can be easily extended:
* any new offers site can be added to the `OffersSite` enum (it requires to implement three scraping methods from `OffersSiteInterface`), the enum name must match the site name in the URL
* `ConfigurationsLoader` interface can have different implementations (e.g. to load the configurations from an external service)
* `OffersRepository` interface can have different implementations (e.g. to use another database type)
* `NotificationSender` can have different implementations (e.g. for push notifications)
