package com.youngtechie.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.youngtechie.todoapp.databinding.ActivityLoginBinding;
import com.youngtechie.todoapp.modelResponse.BasicResponse;
import com.youngtechie.todoapp.networkClient.RetrofitService;
import com.youngtechie.todoapp.storage.SharedPreferenceClass;
import com.youngtechie.todoapp.utilsService.UtilsService;
import com.youngtechie.todoapp.R;

import java.util.HashMap;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    private String email, password;
    UtilsService utilsService;
    SharedPreferenceClass sharedPreferenceClass;

    private ActivityLoginBinding binding;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//      binding = ActivityLoginBinding.inflate(getLayoutInflater());
//      setContentView(binding.getRoot());

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        // Set ViewModel to the binding
//        binding.setViewModel(viewModel);
//        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        // Set lifecycle owner to observe LiveData
        binding.setLifecycleOwner(this);

        sharedPreferenceClass = new SharedPreferenceClass(this);

        binding.loginEmailController.setOnFocusChangeListener(this);
        binding.loginPasswordController.setOnFocusChangeListener(this);
//
        binding.goCreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidateField()) {
                    loginUser(v);
                }
            }
        });
    }


    private boolean isValidateField() {
        boolean passwordValid = validatePassword();
        boolean emailValid = validateEmail();
        return passwordValid && emailValid;
    }

    private boolean validatePassword() {
        String errorMsg = null;
        String value = binding.loginPasswordController.getText().toString();
        if (value.isEmpty()) {
            errorMsg = "This field is required";
        }

        if (errorMsg != null) {
            binding.passwordInputLayout.setErrorEnabled(true);
            binding.passwordInputLayout.setErrorIconDrawable(null);
            binding.passwordInputLayout.setError(errorMsg);
        } else {
            binding.passwordInputLayout.setError(null);
        }
        return errorMsg == null;
    }

    private boolean validateEmail() {
        String errorMsg = null;
        String value = binding.loginEmailController.getText().toString();
        if (value.isEmpty()) {
            errorMsg = "This field is required";
        }

        if (errorMsg != null) {
            binding.emailInputLayout.setErrorEnabled(true);
            binding.emailInputLayout.setErrorIconDrawable(null);
            binding.emailInputLayout.setError(errorMsg);
        } else {
            binding.emailInputLayout.setError(null);
        }
        return errorMsg == null;
    }


    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (view != null) {
            switch (view.getId()) {
                case R.id.loginEmailController:
                    if (hasFocus) {
                        if (binding.emailInputLayout.isErrorEnabled()) {
                            binding.emailInputLayout.setErrorEnabled(false);
                        }
                    } else {
                        validateEmail();
                    }
                    break;

                case R.id.loginPasswordController:
                    if (hasFocus) {
                        if (binding.passwordInputLayout.isErrorEnabled()) {
                            binding.passwordInputLayout.setErrorEnabled(false);
                        }
                    } else {
                        validatePassword();
                    }
                    break;
            }
        }
    }


    private void loginUser(View v) {
        binding.loginProgressBar.setVisibility(View.VISIBLE);
        HashMap<String, String> body = new HashMap<>();

        body.put("email", email);
        body.put("password", password);
        Call<BasicResponse> call = RetrofitService.getInstance(this).getApi().userLogin(body);

        call.enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(@NonNull Call<BasicResponse> call, @NonNull Response<BasicResponse> response) {
                binding.loginProgressBar.setVisibility(View.GONE);
                BasicResponse resp = response.body();
                if (response.isSuccessful()) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    assert resp != null;
                    sharedPreferenceClass.setValue_string("token", resp.getData().getToken());
                    Toast.makeText(LoginActivity.this, resp.getMsg(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<BasicResponse> call, @NonNull Throwable t) {
                binding.loginProgressBar.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }


}