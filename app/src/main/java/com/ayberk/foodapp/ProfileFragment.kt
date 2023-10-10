package com.ayberk.foodapp

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
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
import com.huawei.hms.common.ApiException
import com.huawei.hms.support.account.AccountAuthManager
import com.huawei.hms.support.account.request.AccountAuthParams
import com.huawei.hms.support.account.request.AccountAuthParamsHelper
import com.huawei.hms.support.account.result.AuthAccount
import com.huawei.hms.support.account.service.AccountAuthService


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
                // If your app appears in full screen mode when a user tries to sign in, that is, with no status bar at the top of the device screen, add the following parameter in the intent:
                // intent.putExtra(CommonConstant.RequestParams.IS_FULL_SCREEN, true)
                // Check the details in this FAQ.
                //   signInIntent.putExtra(CommonConstant.RequestParams.IS_FULL_SCREEN, true)
                //  startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN)
            }
        }

        binding.btnSignout.setOnClickListener {
            signOut()
        }
        binding.btnCancel.setOnClickListener {
            showCancelAuthorizationConfirmationDialog()
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