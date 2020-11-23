package fm.weigl.reimburser

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

        customerData.forEachIndexed { index, customerData ->

            logger.log(TAG, "cancelling abo for customerData $customerData")
            ++totalCount
            try {
                apiService.purchases().subscriptions().cancel(Values.PACKAGE_NAME,
                    customerData.subscriptionId, customerData.token).execute()
                logger.log(TAG, "$index,OK,${customerData.toCsv()}")
                ++successCount
            } catch (e: Exception) {
                logger.log("ERROR", "Error cancelling customer $customerData at index $index, $e")
                logger.log(TAG, "$index,ERROR,${customerData.toCsv()}", true)
                ++failCount
            }
        }

        val end = System.currentTimeMillis()
        val deltaSeconds = (end - start) / 1000f

        logger.log("COMPLETE", "Seconds:$deltaSeconds,Total:$totalCount," +
            "Success:$successCount,Fail:$failCount", true)
    }

    private fun getCustomerDataSet(): List<CustomerData> {
        return dataFileReader.readCustomerDataFromFile()
    }
}