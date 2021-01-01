package com.duke.elliot.biblereadinghabits.billing

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails
import com.duke.elliot.biblereadinghabits.R
import com.duke.elliot.biblereadinghabits.databinding.ActivityBillingBinding

class BillingActivity: AppCompatActivity(),
    BillingProcessor.IBillingHandler {

    private lateinit var binding: ActivityBillingBinding
    private lateinit var billingProcessor: BillingProcessor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_billing)

        billingProcessor = BillingProcessor(this, Base64, this);
        billingProcessor.initialize()

        binding.donate.setOnClickListener {
            billingProcessor.purchase(this, Sku.donate)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (billingProcessor.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        if (::billingProcessor.isInitialized)
            billingProcessor.release()

        super.onDestroy()
    }

    override fun onProductPurchased(productId: String, details: TransactionDetails?) {
        if (productId == Sku.donate)
            Toast.makeText(this, "후원해주셔서 감사합니다.", Toast.LENGTH_LONG).show()
    }

    override fun onPurchaseHistoryRestored() {

    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {
        Toast.makeText(
                this,
                "결제가 취소되었습니다.: ${error?.message}",
                Toast.LENGTH_LONG
        ).show()
    }

    override fun onBillingInitialized() {

    }

    companion object {
        private const val Base64 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmsN75auB2tq4XF0SN2mtOs" +
                "yNBlW3u0TbqISVWKprxwt6IhSkZLKRCrXpnu9CAigifWZJy7oKyBIhwEDWdg1AXpCd" +
                "iqf9PXo+3yvwcYTSTijEUP+MgYuzRZYBhZn8Z4TPXaNhhTLChm7EIDXMqneegGJo6S" +
                "7t6vhueyVHUFePGhCyc0dBErpkzM7rZv0DqpvkMuv5rZ4bhu6wW6sT2Ry1NDEnEqfV2" +
                "7BQuxd8amfclUIlQCJQYQvot4/9/3WgxgzdjRf4Xk4GacAR2yRnpS0AWqyeVUXIST" +
                "NQIEtRcfQWOJmHBZxbP4E80dgPRjJ3TjMlsBFbSnCNkhvGacpVP1mq7wIDAQAB"
    }

    object Sku {
        val donate = "donate"
    }
}