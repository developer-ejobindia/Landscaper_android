package com.seazoned.landscaper.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.seazoned.landscaper.R;
import com.seazoned.landscaper.app.AppData;
import com.seazoned.landscaper.databinding.ActivityLoginBinding;
import com.seazoned.landscaper.model.User;
import com.seazoned.landscaper.other.MyCustomProgressDialog;
import com.seazoned.landscaper.service.api.Api;
import com.seazoned.landscaper.service.preference.SharedPreferenceHelper;
import com.seazoned.landscaper.service.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN = 200;
    ActivityLoginBinding mBinding;
    private ProgressDialog dialog;
    private CallbackManager callbackManager;
    private GoogleSignInOptions gso;
    private GoogleApiClient mGoogleApiClient;

    private void showpDialog() {
        if (!dialog.isShowing())
            dialog.show();
    }

    private void hidepDialog() {
        if (dialog.isShowing())
            dialog.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_login);
        FacebookSdk.sdkInitialize(getApplicationContext());

        //Initializing google signin option
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        //Util.setPadding(this, mBinding.mainLayout);
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            Window window = getWindow(); // in Activity's onCreate() for instance
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);

            tintManager.setTintColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        mBinding.tvClickhere.setOnClickListener(this);
        mBinding.tvSignIn.setOnClickListener(this);
        mBinding.ivShow.setOnClickListener(this);
        mBinding.ivHide.setOnClickListener(this);
        mBinding.tvForgotPassword.setOnClickListener(this);
        callbackManager = CallbackManager.Factory.create();
        facebookLogin();

        mBinding.signInButton.setSize(SignInButton.SIZE_WIDE);
        mBinding.signInButton.setScopes(gso.getScopeArray());

        //Initializing google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mBinding.signInButton.setOnClickListener(this);
    }

    private void googleSignIn() {
//Creating an intent
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);

        //Starting intent for result
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void facebookLogin() {
        //Facebook button aftrt login click
        mBinding.mFacebookLoginButton.setReadPermissions(Arrays.asList(
                "public_profile", "user_photos", "user_hometown", "user_posts", "user_about_me", "user_events", "email", "user_birthday", "user_friends", "user_location"));

        //FOR HASH KEY CODE FACEBOOK.............
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));

                //MyUtils.onMyAlertDialog(this, "hashKey", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                //Release Key=2jmj7l5rSw0yVb/vlWAYkK/YBwk=
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        //Hit Facebook Login
        mBinding.mFacebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                if (response == null)
                                    return;
                                try {
                                    String t = AccessToken.getCurrentAccessToken().getToken();

                                    String responseStr = response.getRawResponse();
                                    JSONObject json = new JSONObject(responseStr);

                                    // String authtoken= AccessToken.getCurrentAccessToken().getToken();
                                    AccessToken token = loginResult.getAccessToken();

                                    String szFullName = "";
                                    String szUserName = "";
                                    String facebookid = "";
                                    String szEmail = "";
                                    String lastname = "", location = "";
                                    String szGender = "", currentCity = "";
                                    String birthday = "";
                                    String photourl = "";
                                    int gender = 1;

                                    try {
                                        szFullName = json.getString("name");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        szUserName = json.getString("first_name");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        lastname = json.getString("last_name");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        facebookid = json.getString("id");
                                        Log.d("Facebook id", facebookid);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        JSONObject jsonPicture = json.getJSONObject("picture");
                                        if (jsonPicture != null) {
                                            JSONObject jsonPictureData = jsonPicture.getJSONObject("data");
                                            if (jsonPictureData != null) {
                                                photourl = jsonPictureData.getString("url");
                                                //photourl = URLEncoder.encode(photourl, "UTF-8");
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    try {

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        if (object.has("email"))
                                            szEmail = object.getString("email");
                                        else {
                                            if (szEmail.equals(""))
                                                szEmail = facebookid + "@gmail.com";
                                        }
                                        if (object.has("birthday"))
                                            birthday = json.getString("birthday");
                                        if (!birthday.equals("")) {
                                            //age = ""+Util.getAge(birthday);
                                            //birthday = Util.changeDobFormate(birthday);
                                        }
                                        //location=object.getJSONObject("location").getString("id");

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        if (object.has("gender"))
                                            szGender = json.getString("gender");
                                        if (szGender != null)
                                            gender = (szGender.equals("male") == true ? 1 : 2);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    //call webservice
                                    HashMap<String, String> params = new HashMap<>();
                                    params.put("email", szEmail);
                                    params.put("first_name", szUserName);
                                    params.put("last_name", lastname);
                                    params.put("facebook_id", facebookid);
                                    /*params.put("dob",birthday);
                                    params.put("profile_image",photourl);*/
                                    getUserLoginInfo(Api.sFaceBookLogin, params, "facebook");

                                } catch (JSONException e) {
                                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                    //hidepDialog();
                                } catch (Exception e) {
                                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                    // hidepDialog();
                                }
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, first_name, last_name, email, picture.type(large), gender,birthday,location,photos{link}");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

                Toast.makeText(getApplicationContext(), "Facebook Login attempt canceled.", Toast.LENGTH_SHORT).show();


                //Toast.makeText(SignIn.this, "Facebook Login attempt canceled.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException e) {

                Toast.makeText(getApplicationContext(), "Facebook Login attempt failed." + e.getMessage(), Toast.LENGTH_SHORT).show();

                //Toast.makeText(SignIn.this, "Facebook Login attempt failed." + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //Calling a new function to handle signin
            handleSignInResult(result);
        }
    }

    //After the signing we are calling this function
    private void handleSignInResult(GoogleSignInResult result) {
        //If the login succeed
        if (result.isSuccess()) {
            //Getting google account
            GoogleSignInAccount acct = result.getSignInAccount();
            try {
                String fullName = acct.getDisplayName();
                String fname = fullName.split(" ")[0];
                String lname = fullName.split(" ")[1];
                String email = acct.getEmail();
                String googleId = acct.getId();

                HashMap<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("first_name", fname);
                params.put("last_name", lname);
                params.put("google_id", googleId);
                getUserLoginInfo(Api.sGooglePlusLogin, params, "google");
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            //If login fails

            Toast.makeText(getApplicationContext(), "Login Failed.", Toast.LENGTH_SHORT).show();


            //Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onClick(View view) {
        if (view == mBinding.tvClickhere) {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (view == mBinding.tvSignIn) {
            Util.closeKeyBoard(LoginActivity.this);
            String mEmail = mBinding.etEmail.getText().toString();
            String mPassword = mBinding.etPassword.getText().toString();
            if (TextUtils.isEmpty(mEmail)) {
                Toast.makeText(this, "Input your email id.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(mPassword)) {
                Toast.makeText(this, "Input your password", Toast.LENGTH_SHORT).show();
                return;
            }
            HashMap<String, String> params = new HashMap<>();
            params.put("username", mEmail);
            params.put("password", mPassword);
            params.put("profile_id", "3");
            getUserLoginInfo(Api.sUserLogin, params, "appLogin");

        } else if (view == mBinding.signInButton) {
            googleSignIn();
        }
        else if (view==mBinding.ivHide){
            mBinding.ivHide.setVisibility(View.GONE);
            mBinding.ivShow.setVisibility(View.VISIBLE);
            mBinding.etPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            mBinding.etPassword.setSelection(mBinding.etPassword.getText().length());
        }
        else if (view==mBinding.ivShow){
            mBinding.ivShow.setVisibility(View.GONE);
            mBinding.ivHide.setVisibility(View.VISIBLE);
            mBinding.etPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mBinding.etPassword.setSelection(mBinding.etPassword.getText().length());
        }else if (view == mBinding.tvForgotPassword) {

            startActivity(new Intent(LoginActivity.this, ForgotPassWordActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    private void getUserLoginInfo(String api, final HashMap<String, String> params, final String loginType) {
        if (!Util.isConnected(LoginActivity.this)) {
            Util.showSnakBar(LoginActivity.this, getResources().getString(R.string.internectconnectionerror), mBinding.tvSignIn);
            return;
        }
        dialog = MyCustomProgressDialog.ctor(LoginActivity.this);
        dialog.setCancelable(false);
        dialog.setMessage("Please wait...");
        showpDialog();

        StringRequest postRequest = new StringRequest(Request.Method.POST, api,
                new Response.Listener<String>() {
                    public String mStatus;
                    public String mSuccess;

                    @Override
                    public void onResponse(String data) {
                        try {
                            //Util util = new Util();
                            JSONObject response = Util.getjsonobject(data);
                            mSuccess = response.optString("success");
                            mStatus = response.optString("msg");
                            if (mSuccess.equalsIgnoreCase("1")) {

                                String token = response.optString("token");
                                JSONObject userData = response.getJSONObject("data");
                                String userId = userData.optString("user_id");
                                String address = userData.optString("address");
                                String firstName = userData.optString("first_name");
                                String lastName = userData.optString("last_name");
                                int service_count = userData.optInt("service_count");
                                int provider_status = userData.optInt("provider_status");
                                AppData.sToken = token;
                                AppData.sUserId = userId;
                                User user = new User(userId, token, firstName, lastName, loginType);
                                //getLatLang(address);
                                if (service_count <= 0 || provider_status <= 0) {
                                    finish();
                                    AppData.user = user;
                                    AppData.address=address;
                                    AppData.type="login";
                                    startActivity(new Intent(LoginActivity.this, RegistrationStep2Activity.class)
                                            .putExtra("name", firstName + " " + lastName)
                                            //.putExtra("address", address)
                                    );
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                } else if (service_count > 0 && provider_status == 1) {

                                    SharedPreferenceHelper helper = SharedPreferenceHelper.getInstance(getApplicationContext());
                                    helper.saveUserInfo(user);

                                    finish();
                                    startActivity(new Intent(LoginActivity.this, DashBoardActivity.class));
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "" + mStatus, Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        hidepDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidepDialog();
                Util.showSnakBar(LoginActivity.this, getResources().getString(R.string.networkerror), mBinding.tvSignIn);
                VolleyLog.d("Error: " + error.getMessage());

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(LoginActivity.this).add(postRequest);


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SplashScreen.class));
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
