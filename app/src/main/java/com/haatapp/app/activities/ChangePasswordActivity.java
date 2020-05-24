package com.haatapp.app.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.haatapp.app.R;
import com.haatapp.app.build.api.ApiClient;
import com.haatapp.app.build.api.ApiInterface;
import com.haatapp.app.helper.CustomDialog;
import com.haatapp.app.models.ChangePassword;
import com.haatapp.app.utils.TextUtils;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ChangePasswordActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_top)
    Toolbar toolbarTop;
    @BindView(R.id.confirm)
    Button confirm;
    @BindView(R.id.old_password)
    EditText oldPassword;
    @BindView(R.id.old_password_eye_img)
    ImageView oldPasswordEyeImg;
    @BindView(R.id.new_password)
    EditText newPassword;
    @BindView(R.id.password_eye_img)
    ImageView passwordEyeImg;
    @BindView(R.id.confirm_password)
    EditText confirmPassword;
    @BindView(R.id.confirm_password_eye_img)
    ImageView confirmPasswordEyeImg;

    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    String strConfirmPassword, strNewPassword, strOldPassword;
    CustomDialog customDialog;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);

        context = ChangePasswordActivity.this;
        passwordEyeImg.setTag(1);
        confirmPasswordEyeImg.setTag(1);
        oldPasswordEyeImg.setTag(1);

        setSupportActionBar(toolbarTop);
        toolbarTop.setNavigationIcon(R.drawable.ic_back);
        toolbarTop.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private void initValues() {
        strOldPassword = oldPassword.getText().toString();
        strConfirmPassword = confirmPassword.getText().toString();
        strNewPassword = newPassword.getText().toString();

        if (TextUtils.isEmpty(strOldPassword)) {
            Toast.makeText(this, "Please enter old password", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(strNewPassword)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(strConfirmPassword)) {
            Toast.makeText(this, "Please confirm password", Toast.LENGTH_SHORT).show();
        } else if (!strConfirmPassword.equalsIgnoreCase(strNewPassword)) {
            Toast.makeText(this, "Password and confirm password doesn't match", Toast.LENGTH_SHORT).show();
        } else {
            HashMap<String, String> map = new HashMap<>();
            map.put("password_old", strOldPassword);
            map.put("password", strNewPassword);
            map.put("password_confirmation", strConfirmPassword);
            changePassword(map);
        }

    }

    private void changePassword(HashMap<String, String> map) {
        customDialog = new CustomDialog(context);
        customDialog.show();
        Call<ChangePassword> call = apiInterface.changePassword(map);
        call.enqueue(new Callback<ChangePassword>() {
            @Override
            public void onResponse(@NonNull Call<ChangePassword> call, @NonNull Response<ChangePassword> response) {
                customDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, jObjError.optString("error"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChangePassword> call, @NonNull Throwable t) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_nothing, R.anim.slide_out_right);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @OnClick({R.id.old_password_eye_img, R.id.password_eye_img, R.id.confirm_password_eye_img, R.id.confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.old_password_eye_img:
                if (oldPasswordEyeImg.getTag().equals(1)) {
                    oldPassword.setTransformationMethod(null);
                    oldPasswordEyeImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_eye_close));
                    oldPasswordEyeImg.setTag(0);
                } else {
                    oldPasswordEyeImg.setTag(1);
                    oldPassword.setTransformationMethod(new PasswordTransformationMethod());
                    oldPasswordEyeImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_eye_open));
                }
                break;
            case R.id.password_eye_img:
                if (passwordEyeImg.getTag().equals(1)) {
                    newPassword.setTransformationMethod(null);
                    passwordEyeImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_eye_close));
                    passwordEyeImg.setTag(0);
                } else {
                    passwordEyeImg.setTag(1);
                    newPassword.setTransformationMethod(new PasswordTransformationMethod());
                    passwordEyeImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_eye_open));
                }
                break;
            case R.id.confirm_password_eye_img:
                if (confirmPasswordEyeImg.getTag().equals(1)) {
                    confirmPassword.setTransformationMethod(null);
                    confirmPasswordEyeImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_eye_close));
                    confirmPasswordEyeImg.setTag(0);
                } else {
                    confirmPasswordEyeImg.setTag(1);
                    confirmPassword.setTransformationMethod(new PasswordTransformationMethod());
                    confirmPasswordEyeImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_eye_open));
                }
                break;
            case R.id.confirm:
                initValues();
                break;
        }
    }
}