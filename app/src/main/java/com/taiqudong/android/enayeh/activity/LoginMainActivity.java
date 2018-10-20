package com.taiqudong.android.enayeh.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.gson.Gson;
import com.taiqudong.android.ad.SPUtil;
import com.taiqudong.android.ad.stats.EventConsts;
import com.taiqudong.android.ad.stats.EventLogger;
import com.taiqudong.android.enayeh.R;
import com.taiqudong.android.enayeh.application.AppLogic;
import com.taiqudong.android.enayeh.application.ClientSideFactory;
import com.taiqudong.android.enayeh.application.SysApplication;
import com.taiqudong.android.enayeh.application.retrofit.bean.MenstrualLogs;
import com.taiqudong.android.enayeh.application.retrofit.bean.MenstrualUserInfo;
import com.taiqudong.android.enayeh.bean.LoginResp;
import com.taiqudong.android.enayeh.utils.ApiUtil;
import com.taiqudong.android.enayeh.utils.Constants;
import com.taiqudong.android.enayeh.utils.Log;
import com.taiqudong.android.enayeh.utils.StatusBarUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by taiqudong on 2017/7/6.
 * 第三方登录facebook google phone true
 */

public class LoginMainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private CallbackManager mCallbackManager;
    private GoogleApiClient mGoogleApiClient;

    private static final int RC_GOOGLE_SIGN_IN = 9001;
    private static final int RC_FACEBOOK_SIGN_IN = 9002;

    private static final String TAG = "LoginMainAvt";

    private ProgressDialog mProgressDialog;
    private TextView Skip;


    private String source;

    private int mLoginSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);
        source = getIntent().getStringExtra(EventConsts.source);
        SysApplication.getInstance().addActivit(this);
        StatusBarUtils.statusbar(this);
        initListener();
        initGoogleSignIn();
        initFaceBookLogin();
        mProgressDialog = new ProgressDialog(this);
        Skip = (TextView) findViewById(R.id.Skip);
        Skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里是什么意思？
                //AppLogic.getInstance().setToken("");
                //                startActivity(new Intent(LoginMainActivity.this, MainActivity.class));
                LoginMainActivity.this.finish();
            }
        });
        Skip.setVisibility(View.VISIBLE);
    }

    private void initGoogleSignIn() {
        String serverClientId = getString(R.string.gg_server_client_id);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PROFILE))
                .requestServerAuthCode(serverClientId, false)
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void initFaceBookLogin() {
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook login onSuccess");
                AccessToken accessToken = loginResult.getAccessToken();
                getLoginInfo(accessToken);
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook login onCancel");
                onLoginError("");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook login onError");
                LoginMainActivity.this.onLoginError("");
            }
        });
    }

    private void initListener() {
        findViewById(R.id.btn_boginmain_google).setOnClickListener(this);
        findViewById(R.id.btn_fb).setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);

        final WeakReference<ProgressDialog> theProgressDialog =
                new WeakReference<ProgressDialog>(mProgressDialog);
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressDialog dg = theProgressDialog.get();
                if (dg != null) {
                    dg.show();
                }
            }
        });
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Log.e(TAG, "google sign in callback");
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        } else {
            Log.i(TAG, "facebook sign in callback");
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_fb:
                loginFacebook();
                break;
            case R.id.btn_boginmain_google:
                loginGoogle();
                break;
            default:
        }
    }

    private void loginFacebook() {
        Log.e(TAG, "call facebook sign in");
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
    }

    //google 登录
    private void loginGoogle() {
        Log.e(TAG, "call google sign in");
        //startActivity(MainActivity.class);
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }


    /**
     * 获取登录fb信息
     *
     * @param accessToken
     */
    public void getLoginInfo(AccessToken accessToken) {
        Log.d(TAG, "login_by_fb " + accessToken.getToken());
        ClientSideFactory.getClient().loginByFB(accessToken.getToken(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "login_by_fb failed " + e.getMessage());
                onLoginError("");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handleOnResponse(call, response);
            }
        });
    }

    private void handleOnResponse(Call call, Response response) throws IOException {
        if (response.code() == 200) {
            String json = response.body().string();
            Gson gson = new Gson();
            LoginResp resp = gson.fromJson(json, LoginResp.class);
            if (resp.isOk()) {
                onLoginSuccess(resp, LoginMainActivity.this);
                return;
            } else {
                Log.e(TAG, String.format("onResponse failed, code:%d, desc:%s",
                        resp.getCode(), resp.getDesc()));
            }
        }
        Log.d(TAG, String.format("onResponse failed, status %d, url %s",
                response.code(), call.request().url()));
        onLoginError("");
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "google login onConnectionFailed");
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            final String authCode = acct.getServerAuthCode();
            Log.d(TAG, "authCode " + authCode);
            ClientSideFactory.getClient().loginByGG(authCode, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "login_by_gg failure");
                    onLoginError("");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.e(TAG, "login_by_gg onResponse");
                    handleOnResponse(call, response);
                }
            });
            Log.d(TAG, "google authCode " + authCode);
        } else {
            Log.d(TAG, "google authCode failed " + result.getStatus().getStatusCode());
            onLoginError("");
        }
    }

    private void onLoginError(String msg) {
        final String errorMsg = String.format("%s. %s", getString(R.string.loginFailed), msg);
        Log.d(TAG, errorMsg);
        mProgressDialog.dismiss();
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginMainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 当用户登录成功，我们则会从服务器下载数据, 实现同步.
     *
     * @param loginResp
     * @param context
     */
    private void onLoginSuccess(final LoginResp loginResp, Context context) {
        Log.d(TAG, "onLoginSuccess");
        AppLogic.getInstance().setUserInfo(loginResp.getData().getUser());
        AppLogic.getInstance().setToken(loginResp.getData().getToken());
        SPUtil.put(context, Constants.SP_TOKEN, loginResp.getData().getToken());
        android.util.Log.d("onLoginSuccess", "onLoginSuccess: " + loginResp.getData().getToken());
        SPUtil.put(context, Constants.SP_LOGIN_TYPE, Constants.LOGIN_TYPE.REAL_USER);
        mLoginSteps++;
        ApiUtil.menstrualLogGet(this, new ApiUtil.ApiCallback<MenstrualLogs>() {
            @Override
            public void run(boolean isSuccess, MenstrualLogs resp) {
                mLoginSteps++;
                if (mLoginSteps >= 3) {
                    finishLogin();
                }
            }
        });
        ApiUtil.userinfoGet(this, new ApiUtil.ApiCallback<MenstrualUserInfo>() {
            @Override
            public void run(boolean isSuccess, MenstrualUserInfo resp) {
                mLoginSteps++;
                if (mLoginSteps >= 3) {
                    finishLogin();
                }
            }
        });
    }

    private void finishLogin() {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressDialog = null;
                //                Intent intent = null;
                //                if (!AppLogic.getInstance().isInitialized()) {
                //                    intent = new Intent(LoginMainActivity.this, SelectStatusActivity.class);
                //                } else {
                //                    intent = new Intent(LoginMainActivity.this, MainActivity.class);
                //                }
                Log.i("*********", "mGoogleApiClient" + mGoogleApiClient.isConnected());
                //                startActivity(new Intent(LoginMainActivity.this, MainActivity.class));
                //成功登录 登录弹窗、个人中心登录按钮 LoginSuccessfully(source=dialog&personalCenter)
                EventLogger.logEvent(EventConsts.LoginSuccessfully, EventConsts.source, source);
                startActivity(MainActivity.newInstance(LoginMainActivity.this,null));
                SPUtil.put(LoginMainActivity.this, Constants.SP_NEED_REFRESH_MAINDATA, true);

            }
        });
    }

    //返回无效，不登录，不能用
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SysApplication.getInstance().removeActivity(this);
    }
}
