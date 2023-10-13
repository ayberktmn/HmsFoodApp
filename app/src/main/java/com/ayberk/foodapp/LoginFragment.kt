package com.ayberk.foodapp

import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.ayberk.foodapp.databinding.FragmentLoginBinding
import com.huawei.hmf.tasks.Task
import com.huawei.hms.aaid.HmsInstanceId
import com.huawei.hms.common.ApiException
import com.huawei.hms.support.account.AccountAuthManager
import com.huawei.hms.support.account.request.AccountAuthParams
import com.huawei.hms.support.account.request.AccountAuthParamsHelper
import com.huawei.hms.support.account.result.AuthAccount
import com.huawei.hms.support.account.service.AccountAuthService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getToken()

        val authParams : AccountAuthParams = AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setIdToken().createParams()
        val service : AccountAuthService = AccountAuthManager.getService(requireContext(), authParams)

        val authorizationParams : AccountAuthParams =  AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setAuthorizationCode().createParams()
        val serviceAuth : AccountAuthService = AccountAuthManager.getService(requireContext(), authorizationParams)

        binding.huaweiIdAuthorizationButton.setOnClickListener {
            startActivityForResult(service.signInIntent, 8888)
        }
        binding.accountSilentSignin.setOnClickListener {
            silentsignin()
        }
        binding.accountSignInCode.setOnClickListener {
            startActivityForResult(serviceAuth.signInIntent, 8888)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    private fun getToken() {
        // Create a thread.
        object : Thread() {
            override fun run() {
                try {
                    // Obtain the app ID from the agconnect-services.json file.
                    val appId = "109290863"

                    // Set tokenScope to HCM.
                    val tokenScope = "HCM"
                    val token = HmsInstanceId.getInstance(requireContext()).getToken(appId, tokenScope)
                    Log.i(ContentValues.TAG, "get token:$token")
//                    Toast.makeText(this@MainActivity,"Token:",Toast.LENGTH_SHORT).show()

                    // Check whether the token is null.
                    if (!TextUtils.isEmpty(token)) {
                        sendRegTokenToServer(token)
                    }
                } catch (e: ApiException) {
                    Log.e(ContentValues.TAG, "get token failed, $e")
                }
            }
        }.start()
    }
    private fun sendRegTokenToServer(token: String) {
        Log.i(ContentValues.TAG, "sending token to server. token:$token")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Process the authorization result and obtain an ID to**AuthAccount**thAccount.
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 8888) {
            val authAccountTask = AccountAuthManager.parseAuthResultFromIntent(data)
            if (authAccountTask.isSuccessful) {

                val authAccount = authAccountTask.result
                   // Toast.makeText(requireContext(),"Id Token:"+ authAccount.idToken, Toast.LENGTH_SHORT).show() // id ile giris yapilinca gelecek id Token
                //    Toast.makeText(this,"serverAuthCode:" + authAccount.authorizationCode, Toast.LENGTH_SHORT).show() // authorizationCode ile giris yapilinca gelecek serverAuthCode

                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            } else {

                Toast.makeText(requireContext(),"Yanlis id:", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun silentsignin(){
        val authParams : AccountAuthParams = AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).createParams()
        val service : AccountAuthService = AccountAuthManager.getService(requireContext(), authParams)
        val task : Task<AuthAccount> = service.silentSignIn()

        task.addOnSuccessListener { authAccount ->
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        }

        task.addOnFailureListener { e ->

            if (e is ApiException) {
                Toast.makeText(requireContext(),"LOGIN FAILED", Toast.LENGTH_SHORT).show() //eger daha once id veya code ile giris yapilmadiysa giris islemi olmayacagini ileten mesaj

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}