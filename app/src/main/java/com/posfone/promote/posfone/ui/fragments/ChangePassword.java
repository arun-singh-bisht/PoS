package com.posfone.promote.posfone.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.gson.JsonObject;
import com.posfone.promote.posfone.R;
import com.posfone.promote.posfone.Utils.CustomAlertDialog;
import com.posfone.promote.posfone.Utils.GeneralUtil;
import com.posfone.promote.posfone.data.local.sp.SharedPreferenceHandler;
import com.posfone.promote.posfone.data.remote.rest.ApiClient;
import com.posfone.promote.posfone.data.remote.rest.RESTClient;
import com.posfone.promote.posfone.ui.activities.MainActivity;
import com.posfone.promote.posfone.ui.activities.SignInActivity;
import com.posfone.promote.posfone.ui.activities.SignUpActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;


public class ChangePassword extends Fragment {

@BindView(R.id.input_old_password)
    EditText old_password;
@BindView(R.id.input_password)
EditText input_password;
@BindView(R.id.confirm_input_password)
EditText confirm_password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    private boolean validateData()
    {
        String message = null;
        if (!GeneralUtil.validatePAsswordEditText(getActivity(),R.id.input_old_password))
            message = "Please Enter valid current password";
        else if(!input_password.getText().toString().equals(confirm_password.getText().toString()))
        message="Confirmation Password Does not Match";
       /*else if(!GeneralUtil.validateEditText(this,R.id.input_username))
           message = "Enter Username.";*/
    else if(!GeneralUtil.validatePAsswordEditText(getActivity(),R.id.input_password))
        message = "Enter valid password .";


        if(message != null)
        {
            GeneralUtil.showToast(getContext(),message);
            return false;
        }
        return true;
    }

   @OnClick(R.id.btn_next)
    public  void change_password (){

       if(!validateData())
           return;

       GeneralUtil.showProgressDialog(getActivity(),"Changing password...");
       final SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(getActivity());
       //Header
       HashMap<String,String> header = new HashMap<>();
       header.put("x-api-key", ApiClient.X_API_KEY);
       //RequestBody
       JsonObject jsonObject = new JsonObject();
       jsonObject.addProperty("old",old_password.getText().toString());
       jsonObject.addProperty("new",input_password.getText().toString());
       jsonObject.addProperty("new_confirm",confirm_password.getText().toString());
       jsonObject.addProperty("email",preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_USER_EMAIL));

       String body = "json="+jsonObject.toString();

       Call call = RESTClient.call_POST(RESTClient.CHANGE_PASSWORD, header, body,new okhttp3.Callback() {
       @Override
       public void onFailure(final Call call, IOException e) {
           GeneralUtil.dismissProgressDialog();
       }

       @Override
       public void onResponse(Call call, okhttp3.Response response) {

           GeneralUtil.dismissProgressDialog();
           Log.e("code",String.valueOf(response.code()));
           if (response.isSuccessful()) {
               try {

                   String res = response.body().string();
                   Log.i("onResponse", res);
                   final JSONObject jsonObject = new JSONObject(res);

                   if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("1")) {

                       final String message = jsonObject.getString("message");
                       Log.e("password", message);
                       show_message(message, true);
                   }
               }
                   catch (Exception e) {
                       e.printStackTrace();
                   } finally {
                       //-----
                   }
               } else {
               try {
                   String res = response.body().string();
                   Log.i("onResponse", res);
                   final JSONObject jsonObject = new JSONObject(res);
                   final String message = jsonObject.getString("message");
                   Log.e("password", message);
                   show_message(message, false);
                   //-----------
               } catch (Exception e) {

               }
               }

           }
       });

       }
       // show Dialog Message
       public void show_message(final String message , final boolean isSuccess) {
           getActivity().runOnUiThread(new Runnable() {
               @Override
               public void run() {

                   CustomAlertDialog.showDialogSingleButton(getActivity(), message, new CustomAlertDialog.I_CustomAlertDialog() {
                       @Override
                       public void onPositiveClick() {
                           if (isSuccess) {
                               final SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(getActivity());
                               preferenceHandler.clearSP();
                               Intent intent = new Intent(getActivity(), SignInActivity.class);
                               startActivity(intent);
                               getActivity().finish();
                           }
                       }

                       @Override
                       public void onNegativeClick() {

                       }
                   });
               }
           });
       }
}
