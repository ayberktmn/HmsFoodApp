package com.ayberk.foodapp

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.ayberk.foodapp.databinding.FragmentPremiumAccountBinding
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
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers.data
import org.json.JSONException

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
        binding.btnPremium.isEnabled = false
        binding.btnPremium.setOnClickListener {
            gotoPay(this, "foodapp", IapClient.PriceType.IN_APP_CONSUMABLE)
        }
        binding.btnUye.setOnClickListener {
            gotoPay(this,"premium1",IapClient.PriceType.IN_APP_CONSUMABLE)
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
                val productInfo1 = result.productInfoList[1]

                binding.txtPrice.text = productInfo.price
                binding.txtUye.text = productInfo1.price

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
            productIds.add("premium1")
            productDetails.productIds = productIds

        }
        return req
    }
    private fun gotoPay(fragment: Fragment, productId: String?, type: Int) {

        Log.i("IAP", "call createPurchaseIntent")
        val mClient = Iap.getIapClient(fragment.requireContext())
        val task = mClient.createPurchaseIntent(createPurchaseIntentReq(type, productId))
        task.addOnSuccessListener(OnSuccessListener { result ->
            Log.i("IAP", "createPurchaseIntent, onSuccess")
            Toast.makeText(requireContext(),"${productId}",Toast.LENGTH_SHORT).show()
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
                Toast.makeText(requireContext(),"basarisiz",Toast.LENGTH_SHORT).show()
                // Handle errors.
            } else {
                // Other external errors.
            }
        }
    }

    private fun createPurchaseIntentReq(type: Int, productId: String?): PurchaseIntentReq? {
        val req = PurchaseIntentReq()
        req?.let {  productDetails ->
            productDetails.productId ="foodapp"
            productDetails.productId ="premium1"
            productDetails.priceType =type
            productDetails.developerPayload ="test"
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

                    val success: Boolean = com.ayberk.foodapp.CipherUtil.doCheck(purchaseResultInfo.inAppPurchaseData, purchaseResultInfo.inAppDataSignature, resources.getString(R.string.publickey))
                    if (success) {
                        // Ürün kullanıcıya başarıyla teslim edildiyse, consumeOwnedPurchase'ı çağırarak ürünü tüketin.
                        consumeOwnedPurchase(requireContext(), purchaseResultInfo.inAppPurchaseData)
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

    private fun consumeOwnedPurchase(context: Context, inAppPurchaseData: String) {
        Log.i(TAG, "call consumeOwnedPurchase")
        val mClient = Iap.getIapClient(context)
        val task = mClient.consumeOwnedPurchase(createConsumeOwnedPurchaseReq(inAppPurchaseData))
        task.addOnSuccessListener {
            Log.i(TAG, "consumeOwnedPurchase success")
            Toast.makeText(
                context,
                "Pay success, and the product has been delivered",
                Toast.LENGTH_SHORT
            ).show()

            // Ödeme başarılı olduğunda kullanıcıyı yönlendirin
            // Örnek olarak, yeni bir aktivite başlatılabilir:
          findNavController().navigate(R.id.action_premiumAccountFragment_to_homeFragment)
        }.addOnFailureListener { e ->
            Log.e(TAG, e.message.toString())
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            if (e is IapApiException) {
                val apiException = e
                val returnCode = apiException.statusCode
                Log.e(TAG, "consumeOwnedPurchase fail, returnCode: $returnCode")
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