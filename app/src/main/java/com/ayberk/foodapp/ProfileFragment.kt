package com.ayberk.foodapp

import android.content.IntentSender
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.ayberk.foodapp.databinding.FragmentPopulerBinding
import com.ayberk.foodapp.databinding.FragmentProfileBinding
import com.bumptech.glide.Glide
import com.huawei.hmf.tasks.Task
import com.huawei.hms.ads.Gender
import com.huawei.hms.common.ApiException
import com.huawei.hms.iap.Iap
import com.huawei.hms.iap.IapApiException
import com.huawei.hms.iap.IapClient
import com.huawei.hms.iap.entity.OrderStatusCode
import com.huawei.hms.iap.entity.ProductInfo
import com.huawei.hms.iap.entity.ProductInfoReq
import com.huawei.hms.support.account.AccountAuthManager
import com.huawei.hms.support.account.request.AccountAuthParams
import com.huawei.hms.support.account.request.AccountAuthParamsHelper
import com.huawei.hms.support.account.result.AuthAccount
import com.huawei.hms.support.account.service.AccountAuthService
import com.huawei.hms.support.api.client.Status


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        val authParams : AccountAuthParams = AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setIdToken().createParams()
        val service : AccountAuthService = AccountAuthManager.getService(requireContext(), authParams)
        val task : Task<AuthAccount> = service.silentSignIn()

        task.addOnSuccessListener { authAccount ->
            val username = authAccount.displayName
            val photo = authAccount.avatarUri

            Glide.with(binding.imgUser)
                .load(photo)
                .circleCrop()
                .into(binding.imgUser)
            binding.txtUserName.text = username


        }
        task.addOnFailureListener { e -> // The silent sign-in fails. Your app will call getSignInIntent() to show the authorization or sign-in screen.
            if (e is ApiException) {
                val apiException = e
                //   val signInIntent = service!!.getSignInIntent()
            }
        }
        binding.btnSignout.setOnClickListener {
            signOut()
        }
        binding.btnCancel.setOnClickListener {
            showCancelAuthorizationConfirmationDialog()
        }
        binding.btnPremiumFragment.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_premiumAccountFragment)
        }
        return view
    }

     fun signOut(){
       val authParams : AccountAuthParams = AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).createParams()
       val service : AccountAuthService = AccountAuthManager.getService(requireContext(), authParams)
       val signOutTask = service.signOut()

       signOutTask.addOnCompleteListener { it ->
           Toast.makeText(requireContext(),"SignOut Complete", Toast.LENGTH_SHORT).show()
           findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
       }
   }

     private fun cancelAuthorization() {
          val authParams : AccountAuthParams = AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).createParams()
          val service : AccountAuthService = AccountAuthManager.getService(requireContext(), authParams)
          val task = service.cancelAuthorization()
          task.addOnSuccessListener {
              Toast.makeText(requireContext(),"CancelAuthorization Success", Toast.LENGTH_SHORT).show()

          }
          task.addOnFailureListener { e ->
              Toast.makeText(requireContext(),"CancelAuthorization Failed", Toast.LENGTH_SHORT).show()

          }
      }

      private fun showCancelAuthorizationConfirmationDialog() {
          val alertDialogBuilder = AlertDialog.Builder(requireContext())
          alertDialogBuilder.setTitle("Cikis")
          alertDialogBuilder.setMessage("Cikis yapmak istediğinize emin misiniz?")
          alertDialogBuilder.setIcon(R.drawable.huawei)

          val NegativeButtonText = "Hayır"
          val NegativeButtonTextSpannable = SpannableString(NegativeButtonText)
          NegativeButtonTextSpannable.setSpan(
              ForegroundColorSpan(Color.RED), // Kırmızı renk
              0,
              NegativeButtonText.length,
              0
          )

          alertDialogBuilder.setPositiveButton("Evet") { _, _ ->
              // İptal işlemi onaylandı
              cancelAuthorization()
              findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
          }

          alertDialogBuilder.setNegativeButton(NegativeButtonTextSpannable) { dialog, _ ->
              // İptal işlemi iptal edildi, dialog'u kapat
              dialog.dismiss()
          }
          val alertDialog = alertDialogBuilder.create()
          alertDialog.show()
      }
}

