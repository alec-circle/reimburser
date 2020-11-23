# google-abo-revoker

### Small Kotlin program to 'cancel' active subscriptions on the Google PlayStore

Subscriptions means subscriptions made via Google Play In App Billing

Cancel here means cancel as described in the [method documentation](https://developers.google.com/android-publisher/api-ref/purchases/subscriptions/cancel).

#### How-To:

1. Install Kotlin-plugin for IntelliJ

2. Provide data in the `customer_data.csv` file as a CSV containing (per line): `subscriptionId,token`

 `subscriptionId`: The ID of the product the user purchased (ex. 'com.example.subscription.monthly001')

 `token`: Provided to the customer's device via the Google Billing API. Most likely stored in the back-end as well.

3. Provide the `key.p12` key file needed for authentication with the [Google Play service account](https://developers.google.com/android-publisher/getting_started#using_a_service_account). This file can be downloaded from the Google Api console. Has to be put in the
resources folder alongside `customer_data.csv`

4. Provide `app name`, `app package name`, `service account email` (can be found in your Google API console) in `Values.kt`

5. Run `Main.main()`. The script will read from `customer_data.csv` and revoke them one by one. Logs can be found in `revoker_logs.txt` in the project folder.
