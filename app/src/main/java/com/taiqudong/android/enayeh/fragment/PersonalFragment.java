package com.taiqudong.android.enayeh.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.taiqudong.android.ad.SPUtil;
import com.taiqudong.android.ad.stats.EventConsts;
import com.taiqudong.android.enayeh.R;
import com.taiqudong.android.enayeh.activity.CollectionActivity;
import com.taiqudong.android.enayeh.activity.CommentActivity;
import com.taiqudong.android.enayeh.activity.ConditionActivity;
import com.taiqudong.android.enayeh.activity.HealthReminderActivity;
import com.taiqudong.android.enayeh.activity.LoginMainActivity;
import com.taiqudong.android.enayeh.activity.MainActivity;
import com.taiqudong.android.enayeh.activity.SelectStatusActivity;
import com.taiqudong.android.enayeh.activity.SetCycleActivity;
import com.taiqudong.android.enayeh.activity.WebActivity;
import com.taiqudong.android.enayeh.activity.WeightLogActivity;
import com.taiqudong.android.enayeh.application.AppLogic;
import com.taiqudong.android.enayeh.application.ClientSideFactory;
import com.taiqudong.android.enayeh.application.retrofit.ServiceGenerator;
import com.taiqudong.android.enayeh.application.retrofit.api.StartService;
import com.taiqudong.android.enayeh.application.retrofit.bean.MenstrualLogs;
import com.taiqudong.android.enayeh.application.retrofit.bean.MenstrualUserInfo;
import com.taiqudong.android.enayeh.application.retrofit.bean.RegisterAnon;
import com.taiqudong.android.enayeh.bean.UserInfo;
import com.taiqudong.android.enayeh.utils.ApiUtil;
import com.taiqudong.android.enayeh.utils.Constants;
import com.taiqudong.android.enayeh.utils.LanguageUtil;
import com.taiqudong.android.enayeh.utils.LoadingDialog;
import com.taiqudong.android.enayeh.view.CircleImageView;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 个人中心
 * Created by taiqudong on 2017/8/16.
 */

public class PersonalFragment extends Fragment implements View.OnClickListener {
    protected View contentView = null;
    LoadingDialog.Builder builder;
    LoadingDialog dialog;
    private static final String TAG = "PersonalFragment";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.fragment_personal, container, false);
        Log.d(TAG, "onCreateView");
        builder = new LoadingDialog.Builder(getContext(), getString(R.string.loading));
        dialog = builder.create();
        initView();
        initListener();
        return contentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        initData();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    private CircleImageView civ_icon;//头像
    private TextView tv_id;//id
    private TextView tv_login;//login
    private TextView tv_name;//名字和登录
    private TextView tv_state;//阶段处于什么状态
    private TextView tv_cycle;//周期,点击事件有LinearLayout控制
    private ImageView iv_state;//阶段箭头
    private ImageView iv_cycle;//周期箭头
    private LinearLayout menu_log_out;
    private LinearLayout ll_nologin;
    private LinearLayout ll_yeslogin;


    private int mLogoutSteps;


    public void initView() {
        ll_nologin = (LinearLayout) contentView.findViewById(R.id.ll_nologin);
        ll_yeslogin = (LinearLayout) contentView.findViewById(R.id.ll_yeslogin);
        civ_icon = (CircleImageView) contentView.findViewById(R.id.civ_icon);

        tv_id = (TextView) contentView.findViewById(R.id.tv_id);
        tv_login = (TextView) contentView.findViewById(R.id.tv_login);

        tv_name = (TextView) contentView.findViewById(R.id.tv_name);

        tv_state = (TextView) contentView.findViewById(R.id.tv_state);
        tv_cycle = (TextView) contentView.findViewById(R.id.tv_cycle);

        iv_state = (ImageView) contentView.findViewById(R.id.iv_state);
        iv_cycle = (ImageView) contentView.findViewById(R.id.iv_cycle);

        menu_log_out = (LinearLayout) contentView.findViewById(R.id.menu_log_out);
    }

    public void initListener() {
        tv_login.setOnClickListener(this);

        contentView.findViewById(R.id.menu_state).setOnClickListener(this);//人生阶段
        contentView.findViewById(R.id.menu_cycle).setOnClickListener(this);//周期
        contentView.findViewById(R.id.menu_collect).setOnClickListener(this);//收藏
        contentView.findViewById(R.id.menu_comment).setOnClickListener(this);//评论

        contentView.findViewById(R.id.menu_health_reminder).setOnClickListener(this);//提醒
        contentView.findViewById(R.id.menu_health_statistics).setOnClickListener(this);//统计
        contentView.findViewById(R.id.menu_contact_us).setOnClickListener(this);//联系我们
        contentView.findViewById(R.id.menu_about_us).setOnClickListener(this);//关于我们
        menu_log_out.setOnClickListener(this);//退出
    }

    public void initData() {
        int loginType = (int) SPUtil.get(getContext(), Constants.SP_LOGIN_TYPE, Constants.LOGIN_TYPE.NON_USER);
        UserInfo userInfo = (UserInfo) AppLogic.getInstance().getUserInfo();
        if (loginType == Constants.LOGIN_TYPE.NON_USER) {//没fb,gg登录
            ll_nologin.setVisibility(View.VISIBLE);
            ll_yeslogin.setVisibility(View.GONE);
            menu_log_out.setVisibility(View.GONE);//隐藏退出
            tv_id.setVisibility(View.VISIBLE);
            tv_login.setVisibility(View.VISIBLE);
            tv_name.setVisibility(View.INVISIBLE);
            // TODO: 2017/8/16 默认ID
            if (userInfo == null) {
                tv_id.setText(AppLogic.getInstance().getDeviceId());
            } else {
                tv_id.setText(userInfo.getName());
            }
            tv_login.setText(getString(R.string.PleaseLogin));//请登录
            Log.d(TAG, "initData: NON_USER");
        } else {
            ll_nologin.setVisibility(View.GONE);
            ll_yeslogin.setVisibility(View.VISIBLE);
            menu_log_out.setVisibility(View.VISIBLE);//显示退出
            tv_id.setVisibility(View.INVISIBLE);
            tv_login.setVisibility(View.INVISIBLE);
            tv_name.setVisibility(View.VISIBLE);
            if (userInfo != null) {
                Log.d("nameuser", "initData: " + userInfo.getName());
                Glide.with(this).load(userInfo.getAvatar()).diskCacheStrategy(DiskCacheStrategy.RESULT).error(R.drawable.avatar_default).into(civ_icon);//头像
                tv_name.setText(userInfo.getName());//名字
            }
            Log.d(TAG, "initData: REAL_USER");
        }
        if (AppLogic.getInstance().getStatus() == AppLogic.USER_STATUS_TO_PREGNANT) {//怀孕
            tv_state.setText(getString(R.string.state1));
            if (LanguageUtil.isALB()) {
                iv_state.setImageResource(R.drawable.ic_chevron_left_24dp);
            } else {
                iv_state.setImageResource(R.drawable.ic_chevron_right_24dp);
            }
        } else if (AppLogic.getInstance().getStatus() == AppLogic.USER_STATUS_TO_TRACK) {//追踪
            tv_state.setText(getString(R.string.state2));
            if (LanguageUtil.isALB()) {
                iv_state.setImageResource(R.drawable.ic_chevron_left_24dp);
            } else {
                iv_state.setImageResource(R.drawable.ic_chevron_right_24dp);
            }
        } else {//用户还没有记录
            tv_state.setText("");
            if (LanguageUtil.isALB()) {
                iv_state.setImageResource(R.drawable.ic_chevron_left_24dp_huise);
            } else {
                iv_state.setImageResource(R.drawable.ic_chevron_right_24dp_huise);
            }
        }

        if (AppLogic.getInstance().getMentrualCicle() > 0) {//有周期
            tv_cycle.setText(String.format(getString(R.string.menucycle), "" + AppLogic.getInstance().getMentrualCicle()));//周期
            if (LanguageUtil.isALB()) {
                iv_cycle.setImageResource(R.drawable.ic_chevron_left_24dp);
            } else {
                iv_cycle.setImageResource(R.drawable.ic_chevron_right_24dp);
            }
        } else {//没记录
            tv_cycle.setText("");
            if (LanguageUtil.isALB()) {
                iv_cycle.setImageResource(R.drawable.ic_chevron_left_24dp_huise);
            } else {
                iv_cycle.setImageResource(R.drawable.ic_chevron_right_24dp_huise);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_login:
                Intent intentlogin = new Intent(getActivity(), LoginMainActivity.class);
                intentlogin.putExtra(EventConsts.source, EventConsts.personalCenter);
                startActivity(intentlogin);//到登录页
                break;
            case R.id.menu_state:
                if (AppLogic.getInstance().isInitialized()) {
                    startActivity(new Intent(getActivity(), ConditionActivity.class)); //到修改阶段
                } else {
                    startActivity(new Intent(getActivity(), SelectStatusActivity.class));//没用户数据
                }
                break;
            case R.id.menu_cycle:
                if (AppLogic.getInstance().isInitialized()) {
                    startActivity(new Intent(getActivity(), SetCycleActivity.class)); //到修改周期
                } else {
                    startActivity(new Intent(getActivity(), SelectStatusActivity.class));//没用户数据
                }
                break;
            case R.id.menu_collect:
                //到收藏
                startActivity(new Intent(getActivity(), CollectionActivity.class));
                break;
            case R.id.menu_comment:
                //到评论
                startActivity(new Intent(getActivity(), CommentActivity.class));
                break;
            case R.id.menu_health_reminder:
                startActivity(new Intent(getActivity(), HealthReminderActivity.class));
                break;
            case R.id.menu_health_statistics:
                startActivity(new Intent(getActivity(), WeightLogActivity.class));
                break;
            case R.id.menu_contact_us:
                String[] email = {ClientSideFactory.EMAIL_RUL}; // 需要注意，email必须以数组形式传入
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822"); // 设置邮件格式
                intent.putExtra(Intent.EXTRA_EMAIL, email); // 接收人
                intent.putExtra(Intent.EXTRA_CC, email);
                startActivity(Intent.createChooser(intent, getString(R.string.app_name)));
                break;
            case R.id.menu_about_us:
                Intent intent1 = new Intent(getActivity(), WebActivity.class);
                intent1.putExtra("from", WebActivity.FROM_ABOUT_US);
                startActivity(intent1);
                break;
            case R.id.menu_log_out:
                registerAnon(new CallBack() {
                    @Override
                    public void call() {
                        AppLogic.getInstance().clearBasic();
                        mLogoutSteps++;
                        //获取经期数据
                        ApiUtil.menstrualLogGet(getContext(), new ApiUtil.ApiCallback<MenstrualLogs>() {
                            @Override
                            public void run(boolean isSuccess, MenstrualLogs resp) {
                                mLogoutSteps++;
                                refreshUser();
                            }
                        });
                        //获取用户信息
                        ApiUtil.userinfoGet(getContext(), new ApiUtil.ApiCallback<MenstrualUserInfo>() {
                            @Override
                            public void run(boolean isSuccess, MenstrualUserInfo resp) {
                                mLogoutSteps++;
                                refreshUser();
                            }
                        });
                    }
                });
                // TODO: 2017/8/16 退出逻辑，到哪里？？？
                //startActivity(new Intent(this,LoginMainActivity.class));
                break;
            default:
        }
    }

    /**
     * 用户数据更新后重载activity
     */
    private void refreshUser() {
        if (mLogoutSteps >= 3) {
            mLogoutSteps = 0;
            getActivity().startActivity(MainActivity.newInstance(getContext(),null));
            SPUtil.put(getContext(), Constants.SP_NEED_REFRESH_MAINDATA, true);
        }
    }

    /**
     * 匿名注册
     */
    void registerAnon(final CallBack callBack) {
        dialog.show();
        StartService startService = ServiceGenerator.createServcieWithoutAccess(StartService.class, getContext());
        String deviceID = AppLogic.getInstance().getDeviceId();
        Log.d("MainActivity", "registerAnon: " + deviceID);
        HashMap<String, String> map = new HashMap<>();
        map.put("did", deviceID);
        startService.registerAnon(map).enqueue(new Callback<RegisterAnon>() {
            @Override
            public void onResponse(Call<RegisterAnon> call, Response<RegisterAnon> response) {
                RegisterAnon data = response.body();
                dialog.dismiss();
                if (data == null) {
                    return;
                }
                if (data.getCode() == Constants.REQUEST_SUCCESS) {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setAvatar(data.getData().getUser().getAvatar());
                    userInfo.setIntro(data.getData().getUser().getIntro());
                    userInfo.setName(data.getData().getUser().getName());
                    userInfo.setUid(data.getData().getUser().getUid());
                    AppLogic.getInstance().setUserInfo(userInfo);
                    SPUtil.put(getContext(), Constants.SP_REGISTER_ANNO, true);
                    AppLogic.getInstance().setToken(data.getData().getToken());
                    SPUtil.put(getContext(), Constants.SP_TOKEN, data.getData().getToken());
                    SPUtil.put(getContext(), Constants.SP_LOGIN_TYPE, Constants.LOGIN_TYPE.NON_USER);
                    Log.d("MainActivity", "onResponse: " + data.getData().getToken());
                    if (callBack != null) {
                        callBack.call();
                    }
                    //getActivity().finish();
                }
            }

            @Override
            public void onFailure(Call<RegisterAnon> call, Throwable t) {
                Log.d("MainActivity", "onResponse: " + t.getMessage());
            }
        });
    }

    private interface CallBack {
        void call();
    }

}
