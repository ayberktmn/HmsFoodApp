package com.ayberk.foodapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.ayberk.foodapp.databinding.ActivityMainBinding
import com.ayberk.foodapp.databinding.ActivityPremiumBinding
import com.huawei.hmf.tasks.OnSuccessListener
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.BannerAdSize
import com.huawei.hms.iap.Iap
import com.huawei.hms.iap.IapApiException
import com.huawei.hms.iap.IapClient
import com.huawei.hms.iap.entity.ConsumeOwnedPurchaseReq
import com.huawei.hms.iap.entity.InAppPurchaseData
import com.huawei.hms.iap.entity.OrderStatusCode
import com.huawei.hms.iap.entity.ProductInfoReq
import com.huawei.hms.iap.entity.PurchaseIntentReq
import org.json.JSONException

class premiumActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPremiumBinding
    private var isProductPurchased: Boolean = false

    companion object {
        private val REQ_CODE_BUY = 4002
        val TAG = "PremiumAccountFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPremiumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadProduct()

        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        isProductPurchased = sharedPref.getBoolean("isProductPurchased", false)
        // Set the ad unit ID and ad dimensions. "testw6vs28auh3" is a dedicated test ad unit ID.

        if (isProductPurchased) {
            // Ürün satın alındı, reklamları gizle veya premium özelliklere erişim sağla
            binding.bannerView.visibility = View.GONE
            binding.btnPremium.isEnabled = false
            binding.textView6.text = "Premium uyesiniz!"
        } else {
            // Ürün satın alınmadı, reklamları göster ve premium özelliklere erişimi kapat
            binding.bannerView.visibility = View.VISIBLE
            binding.btnPremium.isEnabled = true
            binding.textView6.text = "Reklamlardan kurtulmak icin premium satin alin"
            binding.btnPremium.setOnClickListener {
                gotoPay(this@premiumActivity, "premium7", IapClient.PriceType.IN_APP_NONCONSUMABLE)
            }
            binding.bannerView.adId = "testw6vs28auh3"
            binding.bannerView.bannerAdSize = BannerAdSize.BANNER_SIZE_360_57
            // Set the refresh interval to 60 seconds.
            binding.bannerView.setBannerRefresh(60)
            // Create an ad request to load an ad.
            val adParam = AdParam.Builder().build()
            binding.bannerView.loadAd(adParam)
        }
    }


    private fun loadProduct() {
        // Obtain in-app product details configured in AppGallery Connect, and then show the products.
        val iapClient = Iap.getIapClient(this)
        val task =iapClient.obtainProductInfo(createProductInfoReq())
        task.addOnSuccessListener { result ->
            if (result != null && !result.productInfoList.isEmpty()) {
                val  product1 = result.productInfoList[0]
                binding.txtPrice.text = product1.price

            }
        }.addOnFailureListener { e ->
            //    Log.e("IAP", e.message)
            if (e is IapApiException) {
                val returnCode = e.statusCode
                if (returnCode == OrderStatusCode.ORDER_HWID_NOT_LOGIN) {
                    Toast.makeText(this, "Please sign in to the app with a HUAWEI ID.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createProductInfoReq(): ProductInfoReq? {
        // In-app product type contains:
        // 0: consumable.
        // 1: non-consumable.
        // 2: auto-renewable subscription.
        val req = ProductInfoReq()
        req?.let { productDetails ->
            productDetails.priceType = IapClient.PriceType.IN_APP_NONCONSUMABLE
            val productIds = ArrayList<String>()
            // Pass in the item_productId list of products to be queried.
            // The product ID is the same as that you set during product information configuration in AppGallery Connect.
            //   productIds.add("notads")
            productIds.add("premium7")
            productDetails.productIds = productIds
        }
        return req
    }
    private fun gotoPay(activty: Activity, productId: String?, type: Int) {

        Log.i("IAP", "call createPurchaseIntent")
        val mClient = Iap.getIapClient(activty)
        val task = mClient.createPurchaseIntent(createPurchaseIntentReq(type, "premium7"))
        task.addOnSuccessListener(OnSuccessListener { result ->
            Log.i("IAP", "createPurchaseIntent, onSuccess")
            if (result == null) {
                Log.e("IAP", "result is null")
                return@OnSuccessListener
            }
            val status = result.status
            if (status == null) {
                Log.e("IAP", "status is null")
                return@OnSuccessListener
            }
            // Bring up the screen to complete the payment process.
            if (status.hasResolution()) {
                try {
                    status.startResolutionForResult(activty,REQ_CODE_BUY)
                } catch (exp: IntentSender.SendIntentException) {
                    exp.message?.let { Log.e("IAP", it) }
                }
            } else {
                Log.e("IAP", "intent is null")
            }
        }).addOnFailureListener { e ->
            e.message?.let { Log.e("IAP", it) }
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            if (e is IapApiException) {
                val returnCode = e.statusCode
                Log.e("IAP", "createPurchaseIntent, returnCode: $returnCode")
                // Handle errors.
            } else {
                // Other external errors.
            }
        }
    }

    private fun createPurchaseIntentReq(type: Int, productId: String?): PurchaseIntentReq? {
        val req = PurchaseIntentReq()
        req?.let {  productDetails ->
            productDetails.productId = "premium7"
            productDetails.priceType=type
            productDetails.developerPayload="test"
        }
        return req
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        println("activty result")
        if (requestCode == REQ_CODE_BUY) {
            if (data == null) {
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
                return
            }
            val purchaseResultInfo = Iap.getIapClient(this).parsePurchaseResultInfoFromIntent(data)
            when (purchaseResultInfo.returnCode) {
                OrderStatusCode.ORDER_STATE_SUCCESS -> {
                    // Verify signature of payment results.
                    // CipherUtil is a custom class that contains the method for verifying signatures. You can find it in the demo project package provided in the "Reference" section.
                    val success: Boolean = CipherUtil.doCheck(purchaseResultInfo.inAppPurchaseData, purchaseResultInfo.inAppDataSignature,resources.getString(R.string.publickey))

                    if (success) {
                        // Call consumeOwnedPurchase to consume the product after it is successfully delivered to the user.
                        consumeOwnedPurchase(this, purchaseResultInfo.inAppPurchaseData)
                        Toast.makeText(this, "Satin alindi!", Toast.LENGTH_SHORT).show()
                        // Satın alma işlemi başarılı olduğunda isProductPurchased değerini true olarak ayarlayın
                        isProductPurchased = true

                        // SharedPreferences kullanarak değeri saklayın
                        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                        val editor = sharedPref.edit()
                        editor.putBoolean("isProductPurchased", isProductPurchased)
                        editor.apply()
                    } else {
                        Toast.makeText(this, "Pay successful, sign failed", Toast.LENGTH_SHORT).show()

                        isProductPurchased = true
                        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                        val editor = sharedPref.edit()
                        editor.putBoolean("isProductPurchased", isProductPurchased)
                        editor.apply()

                        binding.bannerView.visibility = View.GONE
                        binding.btnPremium.isEnabled = false
                        binding.textView6.text = "Premium uyesiniz!"
                    }
                    return
                }
                OrderStatusCode.ORDER_STATE_CANCEL -> {
                    // The user cancels payment.
                    Toast.makeText(this, "user cancel", Toast.LENGTH_SHORT).show()
                    return
                }
                OrderStatusCode.ORDER_PRODUCT_OWNED -> {
                    // The user has already owned the product.
                    isProductPurchased = true
                    binding.bannerView.visibility = View.GONE
                    binding.btnPremium.isEnabled = false
                    binding.textView6.text = "Premium uyesiniz!"
                    Toast.makeText(this, "you have owned the product", Toast.LENGTH_SHORT).show()
                    // You can check if the user has purchased the product and decide whether to deliver the product.
                    // If the product is a consumable product, call consumeOwnedPurchase to consume it after it is successfully delivered to the user.
                    return
                }
                else -> Toast.makeText(this, "Pay failed", Toast.LENGTH_SHORT).show()
            }
            return
        }
    }
    private fun consumeOwnedPurchase(context: Context, inAppPurchaseData: String) {
        Log.i(TAG, "call consumeOwnedPurchase")
        val mClient = Iap.getIapClient(context)
        val task = mClient.consumeOwnedPurchase(createConsumeOwnedPurchaseReq(inAppPurchaseData))
        task.addOnSuccessListener { // Consume success
            Log.i(TAG, "consumeOwnedPurchase success")
            Toast.makeText(
                context,
                "Pay success, and the product has been delivered",
                Toast.LENGTH_SHORT
            ).show()

        }.addOnFailureListener { e ->
            Log.e(TAG, e.message.toString())
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            if (e is IapApiException) {
                val apiException = e
                val returnCode = apiException.statusCode
                Log.e(TAG, "consumeOwnedPurchase fail,returnCode: $returnCode")
            } else {
                // Other external errors
            }
        }
    }
    private fun createConsumeOwnedPurchaseReq(purchaseData: String): ConsumeOwnedPurchaseReq? {
        val req = ConsumeOwnedPurchaseReq()
        // Parse purchaseToken from InAppPurchaseData in JSON format.
        try {
            val inAppPurchaseData = InAppPurchaseData(purchaseData)
            req.purchaseToken = inAppPurchaseData.purchaseToken
        } catch (e: JSONException) {
            Log.e(TAG, "createConsumeOwnedPurchaseReq JSONExeption")
        }
        return req
    }
}