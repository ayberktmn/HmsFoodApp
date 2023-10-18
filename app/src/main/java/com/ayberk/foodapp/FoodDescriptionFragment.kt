package com.ayberk.foodapp

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils.indexOf
import android.text.TextUtils.lastIndexOf
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.compose.runtime.currentCompositeKeyHash
import androidx.core.graphics.green
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.ayberk.foodapp.Adapter.RandomFoodAdapter
import com.ayberk.foodapp.Models.Food.Meal
import com.ayberk.foodapp.Models.Food.RandomFood
import com.ayberk.foodapp.ViewModel.RandomFoodVM
import com.ayberk.foodapp.databinding.FragmentFoodDescriptionBinding
import com.bumptech.glide.Glide
import com.huawei.hms.ads.AdListener
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.InterstitialAd
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

@AndroidEntryPoint
class FoodDescriptionFragment : Fragment() {


    private var _binding: FragmentFoodDescriptionBinding? = null
    private val binding get() = _binding!!
    lateinit var resultList : Meal
    private val viewModel : RandomFoodVM by viewModels()
    private var interstitialAd: InterstitialAd? = null
    private var isBackPressed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isProductPurchased = sharedPref.getBoolean("isProductPurchased", false)
// isProductPurchased, SharedPreferences'ten okunan değeri içerecektir

        if (isProductPurchased){
            Toast.makeText(requireContext(),"Premium account",Toast.LENGTH_SHORT).show()
        }else{
            interstitialAd = InterstitialAd(requireContext())
            // "testb4znbuh3n2" is a dedicated test ad unit ID. Before releasing your app, replace the test ad unit ID with the formal one.
            interstitialAd!!.adId = "testb4znbuh3n2"
            loadInterstitialAd()
            interstitialAd!!.adListener = adListener
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFoodDescriptionBinding.inflate(inflater,container,false)
        val view = binding.root
        val gelen = FoodDescriptionFragmentArgs.fromBundle(requireArguments()).foodId
        fetchRandomFood(foodId = gelen)
        val loading = LoadingDialog(this)
        loading.startLoading()
        val handler = Handler()
        handler.postDelayed(object :Runnable{
            override fun run() {
                loading.isDismiss()
            }
        },1200)


        return view
    }

    private fun loadInterstitialAd() {
        // Load an interstitial ad.
        val adParam = AdParam.Builder().build()
        interstitialAd!!.loadAd(adParam)
    }

    private fun showInterstitialAd() {
        // Display the ad.
        if (interstitialAd != null && interstitialAd!!.isLoaded) {
            interstitialAd!!.show(requireActivity())
        } else {
            Toast.makeText(requireContext(), "Ad did not load", Toast.LENGTH_SHORT).show()
        }
    }



    private val adListener: AdListener = object : AdListener() {
        override fun onAdLoaded() {
            // Called when an ad is loaded successfully.
            showInterstitialAd()
        }
        override fun onAdFailed(errorCode: Int) {
            // Called when an ad fails to be loaded.

        }
        override fun onAdClosed() {
            // Called when an ad is closed.

        }
        override fun onAdClicked() {
            // Called when an ad is clicked.

        }
        override fun onAdLeave() {
            // Called when an ad leaves an app.

        }
        override fun onAdOpened() {
            // Called when an ad is opened.

        }
        override fun onAdImpression() {
            // Called when an ad impression occurs.

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getPopulerDescLiveData().observe(viewLifecycleOwner, object :
            Observer<RandomFood> {
            override fun onChanged(t: RandomFood?) {

                if (t != null) {

                   // println("if e girdi ${t}")
              arguments?.let {
                 val gelen = FoodDescriptionFragmentArgs.fromBundle(it).foodId
               //  println(gelen)
                 resultList = t.meals[listOf(gelen).size - 1]
                 // println(resultList)
                   binding.txtCategory.text = resultList.strCategory
                 binding.txtFoodName.text = resultList.strMeal
                     binding.txtArea.text = resultList.strArea
                     binding.txtDescription.text =resultList.strInstructions
                   Glide.with(binding.imgFoodDesc)
                       .load(resultList.strMealThumb)
                        .into(binding.imgFoodDesc)

                   }
                }
            }
        })
    }

    fun fetchRandomFood(foodId : String){
        CoroutineScope(Dispatchers.Main).async {
            viewModel.loadPopulerDesc(foodId)
        }
    }
}