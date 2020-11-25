package fm.weigl.reimburser

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Created by fweigl on 01.11.16.
 */
class SubscriptionCanceller(publisherProvider: PublisherProvider,
        private val dataFileReader: SubscriptionDataFileReader,
        private val logger: Logger) {

    val TAG = javaClass.simpleName

    val apiService = publisherProvider.getPublisher(Values.APP_NAME, Values.SERVICE_ACCOUNT_EMAIL)


    fun cancelSubscriptions() {

        logger.log(TAG, "Cancelling SUBSCRIPTIONS")

        val customerData: List<CustomerData> = getCustomerDataSet()
        var successCount = 0
        var failCount = 0
        var totalCount = 0
        val start = System.currentTimeMillis()

        customerData.forEachIndexed { index, customer ->

            GlobalScope.launch(Dispatchers.IO) {
                logger.log(TAG, "cancelling abo for customerData $customer")
                ++totalCount
                try {
                    apiService.purchases().subscriptions().cancel(Values.PACKAGE_NAME,
                        customer.subscriptionId, customer.token).execute()
                    logger.log(TAG, "$index,OK,${customer.toCsv()}", true)
                    ++successCount
                } catch (e: Exception) {
                    logger.log("ERROR", "Error cancelling customer $customer at index $index, $e")
                    logger.log(TAG, "$index,ERROR,${customer.toCsv()}", true)
                    ++failCount
                }

                if (index == customerData.lastIndex) {
                    val end = System.currentTimeMillis()
                    val deltaSeconds = ((end - start) / 1000f)

                    // sleep 15 seconds to allow for trailing responses
                    Thread.sleep(15000)

                    logger.log("COMPLETE", "Seconds:$deltaSeconds,Total:$totalCount," +
                        "Success:$successCount,Fail:$failCount", true)
                }
            }
        }

        while (true) {
            // wait for results to come in
        }
    }

    private fun getCustomerDataSet(): List<CustomerData> {
        return dataFileReader.readCustomerDataFromFile()
    }
}