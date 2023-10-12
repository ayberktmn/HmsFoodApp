package com.ayberk.foodapp

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ayberk.foodapp.databinding.FragmentPremiumAccountBinding
import com.ayberk.foodapp.databinding.FragmentProfileBinding
import com.huawei.hmf.tasks.OnSuccessListener
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.BannerAdSize
import com.huawei.hms.iap.Iap
import com.huawei.hms.iap.IapApiException
import com.huawei.hms.iap.IapClient
import com.huawei.hms.iap.entity.OrderStatusCode
import com.huawei.hms.iap.entity.ProductInfoReq
import com.huawei.hms.iap.entity.PurchaseIntentReq
import com.huawei.secure.android.common.encrypt.aes.CipherUtil
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers.data

class PremiumAccountFragment : Fragment() {

    private var _binding: FragmentPremiumAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadProduct()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPremiumAccountBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btnPremium.setOnClickListener {

            val productId: String = data?.id.toString()
            gotoPay(this@PremiumAccountFragment, productId, IapClient.PriceType.IN_APP_CONSUMABLE)
        }

        // Set the ad unit ID and ad dimensions. "testw6vs28auh3" is a dedicated test ad unit ID.
        binding.bannerView.adId = "testw6vs28auh3"
        binding.bannerView.bannerAdSize = BannerAdSize.BANNER_SIZE_360_57
        // Set the refresh interval to 60 seconds.
        binding.bannerView.setBannerRefresh(60)
        // Create an ad request to load an ad.
        val adParam = AdParam.Builder().build()
        binding.bannerView.loadAd(adParam)

        return view
    }

    private fun loadProduct() {
        // Obtain in-app product details configured in AppGallery Connect, and then show the products.
        val iapClient = Iap.getIapClient(requireContext())
        val task =iapClient.obtainProductInfo(createProductInfoReq())
        task.addOnSuccessListener { result ->
            if (result != null && !result.productInfoList.isEmpty()) {
                val productInfo = result.productInfoList[0] // Assuming you have only one product in the list

                val price = productInfo.price

                // Display the price in a TextView or any other UI element
                displayPrice(price)

            }
        }.addOnFailureListener { e ->
            e.message?.let { Log.e("IAP", it) }
            if (e is IapApiException) {
                val returnCode = e.statusCode
                if (returnCode == OrderStatusCode.ORDER_HWID_NOT_LOGIN) {
                    Toast.makeText(requireContext(), "Please sign in to the app with a HUAWEI ID.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "error", Toast.LENGTH_SHORT).show()
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
            productDetails.priceType = IapClient.PriceType.IN_APP_CONSUMABLE

            val productIds = ArrayList<String>()
            // Pass in the item_productId list of products to be queried.
            // The product ID is the same as that you set during product information configuration in AppGallery Connect.
            productIds.add("foodapp")
            productDetails.productIds = productIds


        }
        return req
    }
    private fun displayPrice(price: String) {
        binding.txtPrice.text = price
    }
    private fun gotoPay(fragment: Fragment, productId: String?, type: Int) {

        Log.i("IAP", "call createPurchaseIntent")
        val mClient = Iap.getIapClient(fragment.requireContext())
        val task = mClient.createPurchaseIntent(createPurchaseIntentReq(type, productId))
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
                    status.startResolutionForResult(activity, 6666)
                } catch (exp: IntentSender.SendIntentException) {
                    Log.e("IAP", exp.message.toString())
                }
            } else {
                Log.e("IAP", "intent is null")
            }
        }).addOnFailureListener { e ->
            Log.e("IAP", e.message.toString())
            Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
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
            productDetails.productId=productId
            productDetails.priceType=type
            productDetails.developerPayload="test"
        }
        return req
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 6666) {
            if (data == null) {
                Toast.makeText(requireContext(), "error", Toast.LENGTH_SHORT).show()
                return
            }
            val purchaseResultInfo = Iap.getIapClient(requireContext()).parsePurchaseResultInfoFromIntent(data)
            when (purchaseResultInfo.returnCode) {
                OrderStatusCode.ORDER_STATE_SUCCESS -> {
                    // Ödeme sonuçlarının imzasını doğrulayın.
                    // CipherUtil, imzaları doğrulamak için kullanılan özel bir sınıf


                   // val success: Boolean = CipherUtil.doCheck(purchaseResultInfo.inAppPurchaseData, purchaseResultInfo.inAppDataSignature, resources.getString(R.string.publickey))
                    if (true) {
                        // Ürün kullanıcıya başarıyla teslim edildiyse, consumeOwnedPurchase'ı çağırarak ürünü tüketin.
                         purchaseResultInfo.inAppPurchaseData
                    } else {
                        Toast.makeText(requireContext(), "Ödeme başarılı, imza doğrulaması başarısız", Toast.LENGTH_SHORT).show()
                    }
                    return
                }
                OrderStatusCode.ORDER_STATE_CANCEL -> {
                    // Kullanıcı ödemeyi iptal etti.
                    Toast.makeText(requireContext(), "Iptal edildi", Toast.LENGTH_SHORT).show()
                    return
                }
                OrderStatusCode.ORDER_PRODUCT_OWNED -> {
                    // Kullanıcı ürünü zaten satın almış.
                    Toast.makeText(requireContext(), "Ürünü zaten satın aldınız", Toast.LENGTH_SHORT).show()
                    // Kullanıcının ürünü satın alıp almadığını kontrol edebilir ve ürünü teslim etmeye karar verilebilir
                    // Eğer ürün tüketilebilir bir ürünse, kullanıcıya teslim edildikten sonra consumeOwnedPurchase'ı çağırilabilir.
                    return
                }
                else -> Toast.makeText(requireContext(), "Ödeme başarısız", Toast.LENGTH_SHORT).show()
            }
            return
        }
    }
}