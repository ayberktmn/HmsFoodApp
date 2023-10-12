package com.ayberk.foodapp

import android.app.Activity
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

class PremiumAccountFragment : Fragment() {

    private var _binding: FragmentPremiumAccountBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPremiumAccountBinding.inflate(inflater, container, false)
        val view = binding.root
        loadProduct()
        binding.btnPremium.setOnClickListener {
           // Toast.makeText(requireContext(), "Premium Hesaba Yukseltildi", Toast.LENGTH_SHORT).show()
           // binding.bannerView.visibility=View.GONE
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


}