package com.taiqudong.android.enayeh.fragment;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.taiqudong.android.ad.stats.EventConsts;
import com.taiqudong.android.ad.stats.EventLogger;
import com.taiqudong.android.enayeh.R;
import com.taiqudong.android.enayeh.activity.EditActivity;
import com.taiqudong.android.enayeh.activity.ExplainActivity;
import com.taiqudong.android.enayeh.activity.HealthReminderActivity;
import com.taiqudong.android.enayeh.activity.MainActivity;
import com.taiqudong.android.enayeh.activity.SelectStatusActivity;
import com.taiqudong.android.enayeh.adapter.FeedAdapter;
import com.taiqudong.android.enayeh.adapter.FeedViewPagerFragmentAdapter;
import com.taiqudong.android.enayeh.application.AppDay;
import com.taiqudong.android.enayeh.application.AppLogic;
import com.taiqudong.android.enayeh.application.SysApplication;
import com.taiqudong.android.enayeh.application.retrofit.ServiceGenerator;
import com.taiqudong.android.enayeh.application.retrofit.api.ApiService;
import com.taiqudong.android.enayeh.application.retrofit.bean.Tab;
import com.taiqudong.android.enayeh.bean.Basic;
import com.taiqudong.android.enayeh.bean.Feed;
import com.taiqudong.android.enayeh.bean.T;
import com.taiqudong.android.enayeh.listener.PostionChange;
import com.taiqudong.android.enayeh.utils.ApiUtil;
import com.taiqudong.android.enayeh.utils.Constants;
import com.taiqudong.android.enayeh.utils.DateUtil;
import com.taiqudong.android.enayeh.utils.DialogUtil;
import com.taiqudong.android.enayeh.utils.LanguageUtil;
import com.taiqudong.android.enayeh.utils.LoadingDialog;
import com.taiqudong.android.enayeh.utils.ReminderUtil;
import com.taiqudong.android.enayeh.view.NestedScrollview;
import com.taiqudong.android.enayeh.view.NestedViewPager;
import com.taiqudong.android.enayeh.view.NumberPickerView;
import com.taiqudong.android.enayeh.view.RadioBtn3;
import com.taiqudong.android.enayeh.view.RadioBtn5;
import com.taiqudong.android.enayeh.view.RadioBtnMain3;
import com.taiqudong.android.enayeh.view.RadioBtnMain5;
import com.taiqudong.android.enayeh.view.calendar.CalendarMonthView;
import com.taiqudong.android.enayeh.view.calendar.UserCalendarView;
import com.taiqudong.android.enayeh.view.miniCalendar.BlurCircleView;
import com.taiqudong.android.enayeh.view.miniCalendar.MiniCalendarAdapter;
import com.taiqudong.android.enayeh.view.miniCalendar.MiniCalendarRecyclerView;
import com.taiqudong.android.enayeh.view.miniCalendar.MiniCalendarViewHelper;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by taiqudong on 2017/8/19.
 * 主页fragment.
 */

public class MainFragment extends Fragment implements View.OnClickListener {
    //public class MainFragment extends YouTubePlayerSupportFragment implements View.OnClickListener,YouTubePlayer.OnInitializedListener {
    private Toolbar tlb_main;
    private TextView tv_main_open;
    private ImageView iv_main_open2;
    private TextView tv_main_today;
    private final String TAG = "MainFragmentffff";
    public static int mScrollViewState = ViewState.CONSUME;
    public static int mXRecycleViewState = ViewState.UNCONSUME;
    public static int mViewPagerState = ViewState.UNCONSUME;
    public static boolean showTab = false;
    ViewPager viewPager;
    RelativeLayout backRl;
    TabLayout tabLayout;
    public LinearLayout llTab;
    ImageView back;
    HorizontalScrollView horizontalScrollView;
    FeedAdapter feedAdapter;
    LinearLayout downLl;
    NestedViewPager myviewPager;
    int currentPosition = 0;
    RelativeLayout netWorkRl;
    LoadingDialog.Builder builder;
    LoadingDialog dialog;
    FrameLayout fl_jisuan;

    public interface ViewState {
        int CONSUME = 0;
        int UNCONSUME = 1;
    }

    private NestedScrollview scrollview;

    private RadioBtn3 rb3_main_up, rb3_main_down;

    private TextView tv_main_weight;
    private ImageView iv_main_weight;
    private ImageView iv_main_move, iv_main_water, iv_main_fruit, iv_main_defecation;

    private TextView tv_main_switch;
    private ImageView iv_main_switch;

    private RadioBtn5 rb5_main;

    private LinearLayout ll_save;
    private LinearLayout ll_today;
    private LinearLayout ll_show;//第二个页面的月经，痛经

    private TextView tv_main_edit;
    private TextView tv_tuisuan;
    private RecyclerView rv_feed;//Feed
    private ArrayList<Feed> rowsBeans = new ArrayList<>();
    private MiniCalendarAdapter mMiniCalendarAdapter;
    private MiniCalendarRecyclerView miniCalendar;
    private ProgressBar pb_main;
    private TextView tv_date;
    private TextView tv_retry;
    List<Tab.DataBean.ArticleTabsBean> articleTabs = new ArrayList<>();//保存当前的tab值

    //记录用户点击圈圈，或者滑动圈圈的日期
    private Calendar mCal = Calendar.getInstance();
    //记录用户点击圈圈，或者滑动圈圈的日期,或者滑动日历，的偏移量，用于打开第二个页面
    private Calendar mPYL = Calendar.getInstance();
    int headHeight = 0;

    RelativeLayout titleRl;

    MainActivity activity;

    public static MainFragment newInstance() {
        Bundle args = new Bundle();
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        //重置变量
        mScrollViewState = ViewState.CONSUME;
        mXRecycleViewState = ViewState.UNCONSUME;
        mViewPagerState = ViewState.UNCONSUME;
        builder = new LoadingDialog.Builder(getContext(), getString(R.string.loading));
        dialog = builder.create();
        dialog.show();
        netWorkRl = (RelativeLayout) getActivity().findViewById(R.id.rl_network_error_main);
        titleRl = (RelativeLayout) getActivity().findViewById(R.id.rl_title);
        tv_retry = (TextView) getActivity().findViewById(R.id.tv_retry);
        tv_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initTab();
            }
        });
        titleRl.post(new Runnable() {
            @Override
            public void run() {
                int height = titleRl.getBottom() - titleRl.getTop();
                SysApplication.getInstance().setTitleHeight(height);
                SysApplication.getInstance().setStateHeight(getStatusBarHeight());
                Log.d("height", "height: " + height + ";statusHeight:" + getStatusBarHeight());
            }
        });
        horizontalScrollView = (HorizontalScrollView) getActivity().findViewById(R.id.hs);
        onCreate();
        final View view = getActivity().findViewById(R.id.my_ll);
        view.post(new Runnable() {
            @Override
            public void run() {
                viewBottom = view.getBottom();
            }
        });

    }

    int viewBottom;

    void onCreate() {
        myviewPager = (NestedViewPager) getActivity().findViewById(R.id.myviewpager);
        final int pos[] = new int[2];
        myviewPager.post(new Runnable() {
            @Override
            public void run() {
                myviewPager.getLocationOnScreen(pos);
                headHeight = pos[1] - SysApplication.getInstance().getTitleHeight();
                Log.d("headHeight", "run: " + SysApplication.getInstance().getTitleHeight());
            }
        });
        viewPager = (ViewPager) getActivity().findViewById(R.id.viewPager);
        tv_date = (TextView) getActivity().findViewById(R.id.tv_date);
        llTab = (LinearLayout) getActivity().findViewById(R.id.ll_tab);
        tabLayout = (TabLayout) getActivity().findViewById(R.id.tab);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentPosition = tab.getPosition();
                if (articleTabs.size() > 0 && articleTabs.get(currentPosition) != null) {
                    EventLogger.logEvent(EventConsts.HomeFeedShow, EventConsts.tid, articleTabs.get(currentPosition).getTid());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        downLl = (LinearLayout) getActivity().findViewById(R.id.ll_down);
        downLl.setOnClickListener(this);
        Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
        setTvDate(c);
        //首页日历
        miniCalendar = (MiniCalendarRecyclerView) getActivity().findViewById(R.id.miniCalendar);
        MiniCalendarViewHelper.MiniCalendarViewWrapper miniCalendarViewWrapper =
                MiniCalendarViewHelper.build(getContext(), miniCalendar, AppLogic.getInstance(),
                        (BlurCircleView) getActivity().findViewById(R.id.miniCalendarBlurView));
        mMiniCalendarAdapter = (MiniCalendarAdapter) miniCalendar.getAdapter();
        miniCalendarViewWrapper.setDayChangeListener(new MiniCalendarViewHelper.DayChangeListener() {
            @Override
            public void onDayChange(Calendar cal) {
                setToolbarString(cal);
                userCalendarView.setCurrentItem(cal);//滑动到当前月
                mCal = cal;
                mPYL = cal;
                setTvDate(cal);
                if (DateUtil.dayEqual(Calendar.getInstance(), cal)) {
                    updateTodayTip();
                } else {
                    setTipEmpty();
                }
            }
        });
        String language = Locale.getDefault().getLanguage();
        if (language.equals("ar") | language.equals("fa")) {
            miniCalendar.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            Log.d(TAG, "language is ar!");
        }
        miniCalendarViewWrapper.setCenterDayClickListener(new MiniCalendarViewHelper.CenterDayClickListener() {
            @Override
            public void onDayClick(AppDay cal) {
                if (!AppLogic.getInstance().isInitialized()) {//沒有初始化
                    startActivity(new Intent(getContext(), SelectStatusActivity.class));
                } else {
                    //大圈圈的点击事件
                    mCal = cal.getDay();
                    mPYL = cal.getDay();
                    openMiniClick(cal);
                    Calendar c = cal.getDay();
                    T.setYY(c.get(Calendar.YEAR));
                    T.setMM(c.get(Calendar.MONTH) + 1);
                    T.setDD(c.get(Calendar.DAY_OF_MONTH));
                    EventLogger.logEvent(EventConsts.e_RiLiJieMianZhanShi, EventConsts.p_DianJiQiPao);
                }
            }
        });
        initView();
        initListener();
        initDate();

        updateTodayTip();

        loves = new String[]{getString(R.string.Noaction), getString(R.string.protecte), getString(R.string.Noprotection), getString(R.string.Strongsexualdesire)};

        initViewPager();

        initHealthReminder();

        initTab();
    }

    /**
     * 初始化feed数据，配置其滑动事件。
     */
    void initRecycleViewPager(List<Tab.DataBean.ArticleTabsBean> articleTabsBeens) {
        //通过Tablayout设置viewpager
        final FeedViewPagerFragmentAdapter adapter = new FeedViewPagerFragmentAdapter(getChildFragmentManager(), articleTabsBeens, null);
        adapter.setViewpagerType(FeedViewPagerFragmentAdapter.VIEWPAGER_TYPE.MAIN_VIEWPAGER);
        myviewPager.setAdapter(adapter);
        tabLayout = (TabLayout) getActivity().findViewById(R.id.tab);
        llTab = (LinearLayout) getActivity().findViewById(R.id.ll_tab);
        back = (ImageView) getActivity().findViewById(R.id.iv_back);
        tabLayout.setupWithViewPager(myviewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        //如果是阿拉伯语，则显示最后一个
        backRl = (RelativeLayout) getActivity().findViewById(R.id.rl_back);
        backRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (articleTabs.size() > 0 && articleTabs.get(currentPosition) != null) {
                    EventLogger.logEvent(EventConsts.HomeFeedShow, EventConsts.tid, articleTabs.get(currentPosition).getTid());
                }
                scrollview.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollview.smoothScrollTo(0, 0);
                        showTab = false;
                        FeedViewPagerFragmentAdapter feedViewPagerFragmentAdapter = (FeedViewPagerFragmentAdapter) myviewPager.getAdapter();
                        //                        MainFeedFragment fragment = (MainFeedFragment) feedViewPagerFragmentAdapter.getItem(currentPosition);
                        MainFeedFragment fragment = (MainFeedFragment) feedViewPagerFragmentAdapter.instantiateItem(myviewPager, currentPosition);
                        if (fragment != null) {
                            fragment.Ry();
                            fragment.playReset();
                        }
                    }
                });

            }
        });

        scrollview.setPostionChange(new PostionChange() {
            @Override
            public void change() {
                int[] location = new int[2];
                fl_jisuan.getLocationOnScreen(location);
                jisuan = location[1];
                Log.i("*******", "change: location[1]" + location[1]);
                if (scrollview.getScrollY() >= viewBottom) {
                    tlb_main.setVisibility(View.GONE);
                    llTab.setVisibility(View.VISIBLE);
                    MainActivity.b = 0;//不是回弹
                    mScrollViewState = ViewState.UNCONSUME;
                    mXRecycleViewState = ViewState.CONSUME;
                    mViewPagerState = ViewState.CONSUME;
                    showTab = true;
                    scrollview.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollview.scrollTo(0, viewBottom);
                        }
                    });
                } else {
                    showTab = false;
                    mScrollViewState = ViewState.CONSUME;
                    mXRecycleViewState = ViewState.UNCONSUME;
                    mViewPagerState = ViewState.UNCONSUME;
                    tlb_main.setVisibility(View.VISIBLE);
                    llTab.setVisibility(View.GONE);
                }
            }
        });

        //如果是阿拉伯语，则显示最后一个
        if (LanguageUtil.isALB()) {
            myviewPager.setCurrentItem(tabLayout.getTabCount());
        }
    }

    static int jisuan = 0;

    private UserCalendarView userCalendarView;

    private void initViewPager() {
        userCalendarView = (UserCalendarView) getActivity().findViewById(R.id.viewPager);
        userCalendarView.init();
        userCalendarView.setCurrentItem(Calendar.getInstance());//默认打开当月
        userCalendarView.setOnCalendarClickListener(new CalendarMonthView.OnCalendarClickListener() {
            @Override
            public void onClick(View view, int position, AppDay appDay) {
                if (!appDay.isCurrentMonth()) {
                    userCalendarView.setCurrentItem(appDay.getDay());
                }
                userCalendarView.setHightlight(appDay.getDay());
                mPYL = appDay.getDay();//用户滑动的偏移量
                showUI(appDay);//是否显示ui
                uiSQLite(AppLogic.getInstance().queryBasic(getBasicString(appDay)));
                Calendar c = appDay.getDay();
                T.setYY(c.get(Calendar.YEAR));
                T.setMM(c.get(Calendar.MONTH) + 1);
                T.setDD(c.get(Calendar.DAY_OF_MONTH));
            }
        });

        userCalendarView.setOnCalendarChangeListener(new UserCalendarView.OnCalendarChangeListener() {
            @Override
            public void OnSelectedChange(Calendar cal) {
                T.setYY(cal.get(Calendar.YEAR));
                T.setMM(cal.get(Calendar.MONTH) + 1);
                T.setDD(cal.get(Calendar.DAY_OF_MONTH));
                mPYL = cal;//用户滑动的偏移量
                setToolbarString(cal);
                tv_main_today.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * 更新今天的tip
     */
    private void updateTodayTip() {
        if (AppLogic.getInstance().isInitialized()) {
            //今天的tip
            AppDay today = AppLogic.getInstance().getTodayAppDay();

            TextView textView = (TextView) getActivity().findViewById(R.id.today_tip);
            if (today.getDayType() == AppDay.DAY_TYPE_MENSTRUAL) {

                int time = AppLogic.getInstance().getMenstrualTime();//大姨妈要来几天

                int last = time - today.getDayCount();
                if (last <= 2) {//少于两天则提示要走了.
                    textView.setText(getString(R.string.tip3));
                } else {
                    textView.setText(getString(R.string.tip2));
                }

            } else {
                String tip1 = getString(R.string.tip1);
                textView.setText(MessageFormat.format(tip1, AppLogic.getInstance().findNextMemstrualCount()));

            }
            Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
            setTvDate(c);

        } else {
            TextView textView = (TextView) getActivity().findViewById(R.id.today_tip);
            textView.setText("");
        }

        if (ll_one.getVisibility() == View.VISIBLE) {
            tv_main_today.setVisibility(View.INVISIBLE);
        }
    }

    public void setTipEmpty() {
        TextView textView = (TextView) getActivity().findViewById(R.id.today_tip);
        textView.setText("");
        tv_main_today.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isReply) {
            scrollview.scrollTo(0, 0);
            isReply = false;
        }
        //使用这个来刷新横向日历
        // goToday();
        mMiniCalendarAdapter.refreshData();

        //刷新数据
        userCalendarView.totalRefresh();

        showUI(AppLogic.getInstance().getAppDay(mPYL));
        uiSQLite(AppLogic.getInstance().queryBasic(AppLogic.getInstance().getAppDay(mPYL)));
    }

    public static boolean isReply = false;

    private void initView() {
        scrollview = (NestedScrollview) getActivity().findViewById(R.id.scrollview);
        rv_feed = (RecyclerView) getActivity().findViewById(R.id.rv_feed);
        tlb_main = (Toolbar) getActivity().findViewById(R.id.tlb_main);
        iv_main_open2 = (ImageView) getActivity().findViewById(R.id.iv_main_open2);
        tv_main_today = (TextView) getActivity().findViewById(R.id.tv_main_today);
        ll_one = (LinearLayout) getActivity().findViewById(R.id.ll_one);
        ll_two = (LinearLayout) getActivity().findViewById(R.id.ll_two);
        rb3_main_up = (RadioBtn3) getActivity().findViewById(R.id.rb3_main_up);
        rb3_main_down = (RadioBtn3) getActivity().findViewById(R.id.rb3_main_down);

        tv_main_weight = (TextView) getActivity().findViewById(R.id.tv_main_weight);
        iv_main_weight = (ImageView) getActivity().findViewById(R.id.iv_main_weight);
        iv_main_move = (ImageView) getActivity().findViewById(R.id.iv_main_move);
        iv_main_water = (ImageView) getActivity().findViewById(R.id.iv_main_water);
        iv_main_fruit = (ImageView) getActivity().findViewById(R.id.iv_main_fruit);
        iv_main_defecation = (ImageView) getActivity().findViewById(R.id.iv_main_defecation);

        tv_main_switch = (TextView) getActivity().findViewById(R.id.tv_main_switch);
        iv_main_switch = (ImageView) getActivity().findViewById(R.id.iv_main_switch);

        rb5_main = (RadioBtn5) getActivity().findViewById(R.id.rb5_main);

        tv_main_open = (TextView) getActivity().findViewById(R.id.tv_main_open);
        tv_main_edit = (TextView) getActivity().findViewById(R.id.tv_main_edit);

        ll_save = (LinearLayout) getActivity().findViewById(R.id.ll_save);
        ll_today = (LinearLayout) getActivity().findViewById(R.id.ll_today);
        ll_show = (LinearLayout) getActivity().findViewById(R.id.ll_show);
        pb_main = (ProgressBar) getActivity().findViewById(R.id.pb_main);
        tv_tuisuan = (TextView) getActivity().findViewById(R.id.tv_tuisuan);
        fl_jisuan = (FrameLayout) getActivity().findViewById(R.id.fl_jisuan);
    }


    private void initListener() {
        tv_main_today.setOnClickListener(this);
        getActivity().findViewById(R.id.ll_main_function1).setOnClickListener(this);
        getActivity().findViewById(R.id.ll_main_function2).setOnClickListener(this);
        getActivity().findViewById(R.id.ll_main_function3).setOnClickListener(this);
        getActivity().findViewById(R.id.ll_main_function4).setOnClickListener(this);
        getActivity().findViewById(R.id.ll_main_function5).setOnClickListener(this);
        getActivity().findViewById(R.id.tv_main_gotoday).setOnClickListener(this);
        tv_main_weight.setOnClickListener(this);
        iv_main_weight.setOnClickListener(this);
        getActivity().findViewById(R.id.ll_explain).setOnClickListener(this);
        iv_main_move.setOnClickListener(this);
        iv_main_water.setOnClickListener(this);
        iv_main_fruit.setOnClickListener(this);
        iv_main_defecation.setOnClickListener(this);

        iv_main_switch.setOnClickListener(this);
        tv_main_edit.setOnClickListener(this);

        rb5_main.setOnClickRadioBtn5ItemListener(new RadioBtn5.OnClickRadioBtn5ItemListener() {
            @Override
            public void onClickRadioBtn5Item(int i) {
                ContentValues values = new ContentValues();
                values.put("mood", i);
                AppLogic.getInstance().updateBasic(T.getString(), values);
                EventLogger.logEvent(EventConsts.e_XinQiMianBanBianJi, EventConsts.p_RiLiYeMianJingXingBianJi);
            }
        });

        rb3_main_up.setOnClickRadioBtn3ItemListener(new RadioBtn3.OnClickRadioBtn3ItemListener() {
            @Override
            public void onClickRadioBtn3Item(int i) {
                ContentValues values = new ContentValues();
                values.put("menstruation", i);
                AppLogic.getInstance().updateBasic(T.getString(), values);
                EventLogger.logEvent(EventConsts.e_JingQiGuanLiBianJi, EventConsts.p_RiLiYeMianJingXingBianJi);
            }
        });
        rb3_main_down.setOnClickRadioBtn3ItemListener(new RadioBtn3.OnClickRadioBtn3ItemListener() {
            @Override
            public void onClickRadioBtn3Item(int i) {
                ContentValues values = new ContentValues();
                values.put("dysmenorrhea", i);
                AppLogic.getInstance().updateBasic(T.getString(), values);
                EventLogger.logEvent(EventConsts.e_JingQiGuanLiBianJi, EventConsts.p_RiLiYeMianJingXingBianJi);
            }
        });
    }

    private void initDate() {
        activity.setSupportActionBar(tlb_main);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
        setToolbarString(Calendar.getInstance());
        rv_feed.setLayoutManager(new LinearLayoutManager(getContext()));

        rb3_main_up.setDefaultResIds(
                new int[]{R.mipmap.ic_less_d, R.mipmap.ic_medium_d, R.mipmap.ic_more_d}
        );//默认图片
        rb3_main_up.setClickResIds(
                new int[]{R.mipmap.ic_less_s, R.mipmap.ic_medium_s, R.mipmap.ic_more_s}
        );//点击图片
        rb3_main_down.setDefaultResIds(
                new int[]{R.mipmap.ic_mildly_d, R.mipmap.ic_moderate_d, R.mipmap.ic_severe_d}
        );//默认图片
        rb3_main_down.setClickResIds(
                new int[]{R.mipmap.ic_mildly_s, R.mipmap.ic_moderate_s, R.mipmap.ic_severe_s}
        );//点击图片
        rb5_main.setDefaultResIds(
                new int[]{R.mipmap.ic_sohappy_d, R.mipmap.ic_happy_d, R.mipmap.ic_common_d, R.mipmap.ic_unhappy_d, R.mipmap.ic_sad_d}
        );//默认图片
        rb5_main.setClickResIds(
                new int[]{R.mipmap.ic_sohappy_s, R.mipmap.ic_happy_s, R.mipmap.ic_common_s, R.mipmap.ic_unhappy_s, R.mipmap.ic_sad_s}
        );//点击图片
        Basic basic = AppLogic.getInstance().queryBasic(AppLogic.getInstance().getTodayStr());
        if (basic == null) {
            basic = new Basic();
        }
        rb3_main_up.setItem(basic.getMenstruation());//第getMood()个选中
        rb3_main_down.setItem(basic.getDysmenorrhea());
        rb5_main.setItem(basic.getMood());
    }

    public static boolean isCalendar = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_main_gotoday:
                //回到今天
                userCalendarView.setCurrentItem(Calendar.getInstance());
                userCalendarView.setHightlight(Calendar.getInstance());
                showUI(AppLogic.getInstance().getTodayAppDay());
                uiSQLite(AppLogic.getInstance().queryBasic(AppLogic.getInstance().getTodayStr()));
                break;
            case R.id.ll_down:
                if (AppLogic.getInstance().isInitialized()) {
                    //用户信息上传
                    ApiUtil.postUserinfo(getContext());
                    //经期数据上传
                    ApiUtil.postMenstrualLog(getContext());
                }
                openCalendar();
                EventLogger.logEvent(EventConsts.e_RiLiJieMianZhanShi, EventConsts.p_DianJiRiLiRuKou);
                break;
            case R.id.tv_main_today:
                userCalendarView.setCurrentItem(Calendar.getInstance());
                userCalendarView.setHightlight(Calendar.getInstance());
                showUI(AppLogic.getInstance().getTodayAppDay());
                uiSQLite(AppLogic.getInstance().queryBasic(AppLogic.getInstance().getTodayStr()));
                goToday();
                break;
            case R.id.ll_main_function1:
                if (!AppLogic.getInstance().isInitialized()) {//沒有初始化
                    showInitializedDialog();
                } else {
                    goToday();
                    showFunction1();
                    EventLogger.logEvent(EventConsts.e_JingQiGuanLiMianBanZhanShi, EventConsts.p_JingQiGuanLiAnNiuDianJi);
                }
                break;
            case R.id.ll_main_function2:
                goToday();
                showFunction2();
                EventLogger.logEvent(EventConsts.e_ZuoAiJiLuMianBanZhanShi, EventConsts.p_ZuoAiJiLuAnNiuDianJi);
                break;
            case R.id.ll_main_function3:
                goToday();
                showFunction3();
                EventLogger.logEvent(EventConsts.e_XinQingMianBanZhanShi, EventConsts.p_XinQingAnNiuDianJi);
                break;
            case R.id.ll_main_function4:
                goToday();
                showFunction4();
                EventLogger.logEvent(EventConsts.e_JianKangJiLuZhanShi, EventConsts.p_JianKangJiLuAnNiuDinaJi);
                break;
            case R.id.ll_main_function5:
                startActivity(HealthReminderActivity.class);
                EventLogger.logEvent(EventConsts.e_TiXingJieMianZhanShi, EventConsts.p_ShouYeTiXingAnNiuDianJi);
                break;
            case R.id.iv_main_weight:
                weight2();
                break;
            case R.id.ll_explain:
                startActivity(ExplainActivity.class);
                break;
            case R.id.tv_main_weight:
                weight2();
                Bundle bundle = new Bundle();
                bundle.putString("desc", EventConsts.p_DianJiCiShu);
                bundle.putString("note", EventConsts.bz_ShouYeJianKangAnNiu);
                EventLogger.logEvent(EventConsts.e_TiZhongJiLu, EventConsts.p_DianJiCiShu);
                break;
            case R.id.iv_main_move:
                isMainMove = !isMainMove;
                iv_main_move.setImageResource(isMainMove ? R.mipmap.ic_move_s : R.mipmap.ic_move_d);
                ContentValues values7 = new ContentValues();
                values7.put("running", isMainMove);
                AppLogic.getInstance().updateBasic(T.getString(), values7);
                EventLogger.logEvent(EventConsts.e_JianKangJiLuBianJi, EventConsts.p_RiLiYeMianJingXingBianJi);
                break;
            case R.id.iv_main_water:
                isMainWater = !isMainWater;
                iv_main_water.setImageResource(isMainWater ? R.mipmap.ic_water_s : R.mipmap.ic_water_d);
                ContentValues values8 = new ContentValues();
                values8.put("drink", isMainWater);
                AppLogic.getInstance().updateBasic(T.getString(), values8);
                EventLogger.logEvent(EventConsts.e_JianKangJiLuBianJi, EventConsts.p_RiLiYeMianJingXingBianJi);
                break;
            case R.id.iv_main_fruit:
                isMainFruit = !isMainFruit;
                iv_main_fruit.setImageResource(isMainFruit ? R.mipmap.ic_fruit_s : R.mipmap.ic_fruit_d);
                ContentValues values9 = new ContentValues();
                values9.put("fruit", isMainFruit);
                AppLogic.getInstance().updateBasic(T.getString(), values9);
                EventLogger.logEvent(EventConsts.e_JianKangJiLuBianJi, EventConsts.p_RiLiYeMianJingXingBianJi);
                break;
            case R.id.iv_main_defecation:
                isMainDefecation = !isMainDefecation;
                iv_main_defecation.setImageResource(isMainDefecation ? R.mipmap.ic_defecation_s : R.mipmap.ic_defecation_d);
                ContentValues values10 = new ContentValues();
                values10.put("defecation", isMainDefecation);
                AppLogic.getInstance().updateBasic(T.getString(), values10);
                EventLogger.logEvent(EventConsts.e_JianKangJiLuBianJi, EventConsts.p_RiLiYeMianJingXingBianJi);
                break;
            case R.id.iv_main_switch:
                if (isMainSwitch) {
                    iv_main_switch.setImageResource(R.mipmap.ic_switch_d);
                    tv_main_switch.setVisibility(View.INVISIBLE);
                    isMainSwitch = false;
                    ContentValues values = new ContentValues();
                    values.put("sex", 0);
                    AppLogic.getInstance().updateBasic(T.getString(), values);
                } else {
                    iv_main_switch.setImageResource(R.mipmap.ic_switch_s);
                    showLove();
                }
                EventLogger.logEvent(EventConsts.e_ZuoAiJiLuBianJi, EventConsts.p_RiLiYeMianJingXingBianJi);
                break;
            case R.id.tv_main_edit:
                if (!AppLogic.getInstance().isInitialized()) {//沒有初始化
                    showInitializedDialog();
                } else {
                    Intent intent2 = new Intent(getContext(), EditActivity.class);
                    intent2.putExtra("scroll", mPYL.getTime().getTime());
                    startActivity(intent2);
                    EventLogger.logEvent(EventConsts.e_JingQiBianJi, EventConsts.p_DianJiBianJiAnNiu);
                }
                break;
            default:
        }
    }

    private boolean isMainMove, isMainWater, isMainFruit, isMainDefecation;
    private boolean isMainSwitch;

    private LinearLayout ll_one;
    private LinearLayout ll_two;


    public void openCalendar() {//打开另一个页面/日历
        if (isCalendar) {
            Log.e(TAG, "onKeyDown: " + 5);
            //关闭日历，打开首页
            isCalendar = false;
            iv_main_open2.setImageResource(R.drawable.ic_arrow_drop_down_white_24dp);
            ll_one.setVisibility(View.VISIBLE);
            TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                    0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    -1.0f);
            translateAnimation.setDuration(500);
            ll_two.setAnimation(translateAnimation);
            ll_two.setVisibility(View.INVISIBLE);
            goToday();
            setToolbarString(Calendar.getInstance());
        } else {
            MainActivity.b = 1;
            //打开日历，关闭首页
            isCalendar = true;
            iv_main_open2.setImageResource(R.drawable.ic_arrow_drop_up_white_24dp);
            ll_one.setVisibility(View.INVISIBLE);
            TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
            translateAnimation.setDuration(500);
            ll_two.setAnimation(translateAnimation);
            ll_two.setVisibility(View.VISIBLE);
            userCalendarView.setCurrentItem(mCal);
            userCalendarView.setHightlight(mCal);
            scrollview.scrollTo(0, 0);
            showUI(AppLogic.getInstance().getAppDay(mCal));
            uiSQLite(AppLogic.getInstance().queryBasic(T.getString()));
            tv_main_today.setVisibility(View.VISIBLE);
        }
        setToolbarString(mCal);
        activity.mBottomLl.setVisibility(activity.mBottomLl.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    public int onBackPressed() {
        if (isCalendar) {//回主界面
            openCalendar();
            return 1;//表示没有执行下面的代码
        }
        //返回到首次的加载画面
        if (articleTabs.size() > 0 && articleTabs.get(currentPosition) != null) {
            EventLogger.logEvent(EventConsts.HomeFeedShow, EventConsts.tid, articleTabs.get(currentPosition).getTid());
        }
        scrollview.post(new Runnable() {
            @Override
            public void run() {
                scrollview.smoothScrollTo(0, 0);
                showTab = false;
                FeedViewPagerFragmentAdapter feedViewPagerFragmentAdapter = (FeedViewPagerFragmentAdapter) myviewPager.getAdapter();
                //                        MainFeedFragment fragment = (MainFeedFragment) feedViewPagerFragmentAdapter.getItem(currentPosition);
                MainFeedFragment fragment = (MainFeedFragment) feedViewPagerFragmentAdapter.instantiateItem(myviewPager, currentPosition);
                if (fragment != null) {
                    fragment.Ry();
                }
            }
        });
        return 2;//表示执行上面的代码
    }

    private void startActivity(Class<?> cls) {
        Intent intent = new Intent(getContext(), cls);
        startActivity(intent);
    }

    private boolean isComing;//下面的来大姨妈了？
    private Dialog mFunction1Dialog;
    private RadioBtnMain3 rb3_f1_up, rb3_f1_down;
    private ImageView iv_f1_coming;
    private LinearLayout ll;
    private int menstruation;//数据库存储用123
    private int dysmenorrhea;//数据库存储用123

    private void showFunction1() {
        mFunction1Dialog = new Dialog(getContext(), R.style.MyDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(getContext()).inflate(
                R.layout.dialog_function1, null);
        root.findViewById(R.id.iv_dialog_function1_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFunction1Dialog.cancel();
            }
        });
        root.findViewById(R.id.tv_dialog_function1_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put("menstruation", menstruation);
                values.put("dysmenorrhea", dysmenorrhea);
                values.put("coming", isComing);
                AppLogic.getInstance().updateBasic(AppLogic.getInstance().getTodayStr(), values);
                mFunction1Dialog.cancel();
                EventLogger.logEvent(EventConsts.e_JingQiGuanLiBianJi, EventConsts.p_ShouYeMianBanJingXingBianJi);
                if (AppLogic.getInstance().isInitialized()) {
                    //用户信息上传
                    ApiUtil.postUserinfo(getContext());
                    //经期数据上传
                    ApiUtil.postMenstrualLog(getContext());
                }
            }
        });
        rb3_f1_up = (RadioBtnMain3) root.findViewById(R.id.rb3_f1_up);
        rb3_f1_up.setStringResIds(
                new String[]{getString(R.string.less), getString(R.string.medium), getString(R.string.more)}
        );//字
        rb3_f1_up.setDefaultResIds(
                new int[]{R.mipmap.ic_less_dm, R.mipmap.ic_medium_dm, R.mipmap.ic_more_dm}
        );//默认图片
        rb3_f1_up.setClickResIds(
                new int[]{R.mipmap.ic_less_sm, R.mipmap.ic_medium_sm, R.mipmap.ic_more_sm}
        );//点击图片
        rb3_f1_down = (RadioBtnMain3) root.findViewById(R.id.rb3_f1_down);
        rb3_f1_down.setStringResIds(
                new String[]{getString(R.string.Mildly), getString(R.string.Moderate), getString(R.string.Severe)}
        );//字
        rb3_f1_down.setDefaultResIds(
                new int[]{R.mipmap.ic_mildly_dm, R.mipmap.ic_moderate_dm, R.mipmap.ic_severe_dm}
        );//默认图片
        rb3_f1_down.setClickResIds(
                new int[]{R.mipmap.ic_mildly_sm, R.mipmap.ic_moderate_sm, R.mipmap.ic_severe_sm}
        );//点击图片
        iv_f1_coming = (ImageView) root.findViewById(R.id.iv_f1_coming);
        ll = (LinearLayout) root.findViewById(R.id.ll);
        Basic todayBasic = AppLogic.getInstance().queryBasic(AppLogic.getInstance().getTodayStr());
        if (todayBasic == null) {
            //获得日期
            AppDay today = AppLogic.getInstance().getTodayAppDay();
            todayBasic = new Basic();
            if (today.getDayType() == AppDay.DAY_TYPE_MENSTRUAL) {
                todayBasic.setComing(true);
                ContentValues contentValues = new ContentValues();
                contentValues.put("coming", true);
                contentValues.put("menstruation", 0);
                contentValues.put("dysmenorrhea", 0);
                AppLogic.getInstance().updateBasic(AppLogic.getInstance().getTodayStr(), contentValues);
            }
        }
        menstruation = todayBasic.getMenstruation();
        dysmenorrhea = todayBasic.getDysmenorrhea();
        rb3_f1_up.setItem(menstruation);
        rb3_f1_down.setItem(dysmenorrhea);
        if (todayBasic.isComing()) {
            iv_f1_coming.setImageResource(R.mipmap.ic_switch_s);
            isComing = true;
        } else {
            iv_f1_coming.setImageResource(R.mipmap.ic_switch_d);
            isComing = false;
        }
        if (isComing) {
            ll.setVisibility(View.VISIBLE);
        } else {
            ll.setVisibility(View.GONE);
        }

        iv_f1_coming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isComing) {
                    iv_f1_coming.setImageResource(R.mipmap.ic_switch_d);
                    ContentValues values = new ContentValues();
                    values.put("coming", false);
                    values.put("menstruation", 0);
                    values.put("dysmenorrhea", 0);
                    AppLogic.getInstance().updateBasic(AppLogic.getInstance().getTodayStr(), values);
                    mMiniCalendarAdapter.refreshData();
                } else {
                    iv_f1_coming.setImageResource(R.mipmap.ic_switch_s);
                    ContentValues values = new ContentValues();
                    values.put("coming", true);
                    values.put("menstruation", 0);
                    values.put("dysmenorrhea", 0);
                    AppLogic.getInstance().updateBasic(AppLogic.getInstance().getTodayStr(), values);
                    mMiniCalendarAdapter.refreshData(AppDay.DAY_TYPE_MENSTRUAL);
                }
                userCalendarView.totalRefresh();
                mFunction1Dialog.cancel();
                updateTodayTip();
                showFunction1();
                EventLogger.logEvent(EventConsts.e_JingQiBianJi, EventConsts.p_BianJiJingQi);
                if (AppLogic.getInstance().isInitialized()) {
                    //用户信息上传
                    ApiUtil.postUserinfo(getContext());
                    //经期数据上传
                    ApiUtil.postMenstrualLog(getContext());
                }
            }
        });
        rb3_f1_up.setOnClickRadioBtn3ItemListener(new RadioBtnMain3.OnClickRadioBtn3ItemListener() {
            @Override
            public void onClickRadioBtn3Item(int i) {
                menstruation = i;
            }
        });
        rb3_f1_down.setOnClickRadioBtn3ItemListener(new RadioBtnMain3.OnClickRadioBtn3ItemListener() {
            @Override
            public void onClickRadioBtn3Item(int i) {
                dysmenorrhea = i;
            }
        });
        mFunction1Dialog.setContentView(root);
        mFunction1Dialog.show();
    }

    String[] loves;
    private NumberPickerView npv_function2;
    private Dialog mFunction2Dialog;

    private void showFunction2() {
        mFunction2Dialog = new Dialog(getContext(), R.style.MyDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(getContext()).inflate(
                R.layout.dialog_function2, null);

        root.findViewById(R.id.iv_dialog_function2_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFunction2Dialog.cancel();
            }
        });
        root.findViewById(R.id.tv_dialog_function2_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put("sex", npv_function2.getValue() + 1);
                AppLogic.getInstance().updateBasic(AppLogic.getInstance().getTodayStr(), values);
                mFunction2Dialog.cancel();
                EventLogger.logEvent(EventConsts.e_ZuoAiJiLuBianJi, EventConsts.p_ShouYeMianBanJingXingBianJi);
                if (AppLogic.getInstance().isInitialized()) {
                    //用户信息上传
                    ApiUtil.postUserinfo(getContext());
                    //经期数据上传
                    ApiUtil.postMenstrualLog(getContext());
                }
            }
        });

        npv_function2 = (NumberPickerView) root.findViewById(R.id.npv_function2);
        npv_function2.setDisplayedValues(loves);
        npv_function2.setMinValue(0);
        npv_function2.setMaxValue(loves.length - 1);
        npv_function2.setWrapSelectorWheel(false);
        Basic todayBasic = AppLogic.getInstance().queryBasic(AppLogic.getInstance().getTodayStr());
        if (todayBasic == null) {
            todayBasic = new Basic();
        }
        if (todayBasic.getSex() > 0) {
            npv_function2.setValue(todayBasic.getSex() - 1);
        } else {
            npv_function2.setValue(0);
        }
        mFunction2Dialog.setContentView(root);
        mFunction2Dialog.show();
    }

    private void showLove() {
        mFunction2Dialog = new Dialog(getContext(), R.style.MyDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(getContext()).inflate(
                R.layout.dialog_function2, null);

        root.findViewById(R.id.iv_dialog_function2_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFunction2Dialog.cancel();
                iv_main_switch.setImageResource(R.mipmap.ic_switch_d);
            }
        });
        root.findViewById(R.id.tv_dialog_function2_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_main_switch.setVisibility(View.VISIBLE);
                tv_main_switch.setText(loves[npv_function2.getValue()]);
                isMainSwitch = true;
                ContentValues values = new ContentValues();
                values.put("sex", npv_function2.getValue() + 1);
                AppLogic.getInstance().updateBasic(T.getString(), values);
                mFunction2Dialog.cancel();
            }
        });

        npv_function2 = (NumberPickerView) root.findViewById(R.id.npv_function2);
        npv_function2.setDisplayedValues(loves);
        npv_function2.setMinValue(0);
        npv_function2.setMaxValue(loves.length - 1);
        npv_function2.setWrapSelectorWheel(false);
        npv_function2.setValue(0);

        mFunction2Dialog.setContentView(root);
        mFunction2Dialog.show();
    }

    private Dialog mFunction3Dialog;
    private ImageView iv_f3_move, iv_f3_water, iv_f3_fruit, iv_f3_defecation;
    private TextView tv_f3_move, tv_f3_water, tv_f3_fruit, tv_f3_defecation;
    private boolean isMove, isWater, isFruit, isDefecation;

    private void showFunction3() {
        mFunction3Dialog = new Dialog(getContext(), R.style.MyDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(getContext()).inflate(
                R.layout.dialog_function3, null);
        root.findViewById(R.id.iv_dialog_function3_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFunction3Dialog.cancel();
            }
        });
        root.findViewById(R.id.tv_dialog_function3_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put("running", isMove);
                values.put("drink", isWater);
                values.put("fruit", isFruit);
                values.put("defecation", isDefecation);
                AppLogic.getInstance().updateBasic(AppLogic.getInstance().getTodayStr(), values);
                mFunction3Dialog.cancel();
                EventLogger.logEvent(EventConsts.e_JianKangJiLuBianJi, EventConsts.p_ShouYeMianBanJingXingBianJi);
                if (AppLogic.getInstance().isInitialized()) {
                    //用户信息上传
                    ApiUtil.postUserinfo(getContext());
                    //经期数据上传
                    ApiUtil.postMenstrualLog(getContext());
                }
            }
        });
        iv_f3_move = (ImageView) root.findViewById(R.id.iv_f3_move);
        iv_f3_water = (ImageView) root.findViewById(R.id.iv_f3_water);
        iv_f3_fruit = (ImageView) root.findViewById(R.id.iv_f3_fruit);
        iv_f3_defecation = (ImageView) root.findViewById(R.id.iv_f3_defecation);
        tv_f3_move = (TextView) root.findViewById(R.id.tv_f3_move);
        tv_f3_water = (TextView) root.findViewById(R.id.tv_f3_water);
        tv_f3_fruit = (TextView) root.findViewById(R.id.tv_f3_fruit);
        tv_f3_defecation = (TextView) root.findViewById(R.id.tv_f3_defecation);
        Basic todayBasic = AppLogic.getInstance().queryBasic(AppLogic.getInstance().getTodayStr());
        if (todayBasic == null) {
            todayBasic = new Basic();
        }
        if (todayBasic.isRunning()) {
            isMove = true;
            iv_f3_move.setImageResource(R.mipmap.ic_move_sm);
            tv_f3_move.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        }
        if (todayBasic.isDrink()) {
            isWater = true;
            iv_f3_water.setImageResource(R.mipmap.ic_water_sm);
            tv_f3_water.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        }
        if (todayBasic.isFruit()) {
            isFruit = true;
            iv_f3_fruit.setImageResource(R.mipmap.ic_fruit_sm);
            tv_f3_fruit.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        }
        if (todayBasic.isDefecation()) {
            isDefecation = true;
            iv_f3_defecation.setImageResource(R.mipmap.ic_defecation_sm);
            tv_f3_defecation.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        }
        root.findViewById(R.id.ll_f3_move).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMove = !isMove;
                iv_f3_move.setImageResource(isMove ? R.mipmap.ic_move_sm : R.mipmap.ic_move_dm);
                tv_f3_move.setTextColor(isMove ? ContextCompat.getColor(getContext(), R.color.colorPrimary) : ContextCompat.getColor(getContext(), R.color.colorTextDialog));
            }
        });
        root.findViewById(R.id.ll_f3_water).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isWater = !isWater;
                iv_f3_water.setImageResource(isWater ? R.mipmap.ic_water_sm : R.mipmap.ic_water_dm);
                tv_f3_water.setTextColor(isWater ? ContextCompat.getColor(getContext(), R.color.colorPrimary) : ContextCompat.getColor(getContext(), R.color.colorTextDialog));
            }
        });
        root.findViewById(R.id.ll_f3_fruit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFruit = !isFruit;
                iv_f3_fruit.setImageResource(isFruit ? R.mipmap.ic_fruit_sm : R.mipmap.ic_fruit_dm);
                tv_f3_fruit.setTextColor(isFruit ? ContextCompat.getColor(getContext(), R.color.colorPrimary) : ContextCompat.getColor(getContext(), R.color.colorTextDialog));
            }
        });
        root.findViewById(R.id.ll_f3_defecation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDefecation = !isDefecation;
                iv_f3_defecation.setImageResource(isDefecation ? R.mipmap.ic_defecation_sm : R.mipmap.ic_defecation_dm);
                tv_f3_defecation.setTextColor(isDefecation ? ContextCompat.getColor(getContext(), R.color.colorPrimary) : ContextCompat.getColor(getContext(), R.color.colorTextDialog));
            }
        });
        mFunction3Dialog.setContentView(root);
        mFunction3Dialog.show();
    }

    private Dialog mFunction4Dialog;
    private int mood;
    private RadioBtnMain5 re5main_f4;

    private void showFunction4() {
        Basic todayBasic = AppLogic.getInstance().queryBasic(AppLogic.getInstance().getTodayStr());
        if (todayBasic == null) {
            todayBasic = new Basic();
        }

        mFunction4Dialog = new Dialog(getContext(), R.style.MyDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(getContext()).inflate(
                R.layout.dialog_function4, null);

        root.findViewById(R.id.iv_dialog_function4_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFunction4Dialog.cancel();
            }
        });
        root.findViewById(R.id.tv_dialog_function4_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put("mood", mood);
                AppLogic.getInstance().updateBasic(AppLogic.getInstance().getTodayStr(), values);
                mFunction4Dialog.cancel();
                EventLogger.logEvent(EventConsts.e_XinQiMianBanBianJi, EventConsts.p_ShouYeMianBanJingXingBianJi);
                if (AppLogic.getInstance().isInitialized()) {
                    //用户信息上传
                    ApiUtil.postUserinfo(getContext());
                    //经期数据上传
                    ApiUtil.postMenstrualLog(getContext());
                }
            }
        });

        re5main_f4 = (RadioBtnMain5) root.findViewById(R.id.re5main_f4);

        re5main_f4.setStringResIds(
                new String[]{getString(R.string.Sohappy), getString(R.string.happy), getString(R.string.common), getString(R.string.unhappy), getString(R.string.sad)}
        );//字
        re5main_f4.setDefaultResIds(
                new int[]{R.mipmap.ic_sohappy_dm, R.mipmap.ic_happy_dm, R.mipmap.ic_common_dm, R.mipmap.ic_unhappy_dm, R.mipmap.ic_sad_dm}
        );//默认图片
        re5main_f4.setClickResIds(
                new int[]{R.mipmap.ic_sohappy_sm, R.mipmap.ic_happy_sm, R.mipmap.ic_common_sm, R.mipmap.ic_unhappy_sm, R.mipmap.ic_sad_sm}
        );//点击图片

        re5main_f4.setItem(todayBasic.getMood());//第几个选中
        mood = todayBasic.getMood();

        re5main_f4.setOnClickRadioBtn5ItemListener(new RadioBtnMain5.OnClickRadioBtn5ItemListener() {
            @Override
            public void onClickRadioBtn5Item(int i) {
                mood = i;
            }
        });
        mFunction4Dialog.setContentView(root);
        mFunction4Dialog.show();
    }

    // 点击gridview的item， 更新下面的ui
    public void uiSQLite(Basic basic) {
        if (basic == null) {
            basic = new Basic(0, 0, false, 0, "", false, false, false, false, 0);
        }
        rb3_main_up.setItem(basic.getMenstruation());
        rb3_main_down.setItem(basic.getDysmenorrhea());

        if (TextUtils.isEmpty(basic.getWeight()) || Float.parseFloat(basic.getWeight()) < 1) {
            tv_main_weight.setText("");
        } else {
            tv_main_weight.setText(basic.getWeight());
        }
        isMainMove = basic.isRunning();
        iv_main_move.setImageResource(isMainMove ? R.mipmap.ic_move_s : R.mipmap.ic_move_d);
        isMainWater = basic.isDrink();
        iv_main_water.setImageResource(isMainWater ? R.mipmap.ic_water_s : R.mipmap.ic_water_d);
        isMainFruit = basic.isFruit();
        iv_main_fruit.setImageResource(isMainFruit ? R.mipmap.ic_fruit_s : R.mipmap.ic_fruit_d);
        isMainDefecation = basic.isDefecation();
        iv_main_defecation.setImageResource(isMainDefecation ? R.mipmap.ic_defecation_s : R.mipmap.ic_defecation_d);

        isMainSwitch = basic.getSex() > 0;
        tv_main_switch.setVisibility(basic.getSex() > 0 ? View.VISIBLE : View.INVISIBLE);
        tv_main_switch.setText(basic.getSex() > 0 ? loves[basic.getSex() - 1] : "");
        iv_main_switch.setImageResource(basic.getSex() > 0 ? R.mipmap.ic_switch_s : R.mipmap.ic_switch_d);

        rb5_main.setItem(basic.getMood());
    }

    private NumberPickerView npv_weight_positive;
    private NumberPickerView npv_weight_decimal;
    private Dialog mWeightDialog;
    final String[] positive = {"20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35",
            "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55",
            "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75",
            "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95",
            "96", "97", "98", "99", "100"};
    final String[] decimals = {".0", ".1", ".2", ".3", ".4", ".5", ".6", ".7", ".8", ".9"};

    //第二界面弹出
    private void weight2() {
        mWeightDialog = new Dialog(getContext(), R.style.MyDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(getContext()).inflate(
                R.layout.dialog_weight, null);
        root.findViewById(R.id.iv_dialog_weight_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWeightDialog.cancel();
            }
        });
        root.findViewById(R.id.tv_dialog_weight_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_main_weight.setText("" + positive[npv_weight_positive.getValue()] + decimals[npv_weight_decimal.getValue()]);
                ContentValues values = new ContentValues();
                values.put("weight", "" + positive[npv_weight_positive.getValue()] + decimals[npv_weight_decimal.getValue()]);
                AppLogic.getInstance().updateBasic(T.getString(), values);
                mWeightDialog.cancel();
                EventLogger.logEvent(EventConsts.e_TiZhongJiLu, EventConsts.p_JiLuChengGongCiShu);
            }
        });
        npv_weight_positive = (NumberPickerView) root.findViewById(R.id.npv_weight_positive);
        npv_weight_positive.setDisplayedValues(positive);
        npv_weight_positive.setMinValue(0);
        npv_weight_positive.setMaxValue(positive.length - 1);
        npv_weight_positive.setWrapSelectorWheel(false);
        npv_weight_positive.setValue(30);

        npv_weight_decimal = (NumberPickerView) root.findViewById(R.id.npv_weight_decimal);
        npv_weight_decimal.setDisplayedValues(decimals);
        npv_weight_decimal.setMinValue(0);
        npv_weight_decimal.setMaxValue(decimals.length - 1);
        npv_weight_decimal.setWrapSelectorWheel(false);
        npv_weight_decimal.setValue(0);

        mWeightDialog.setContentView(root);
        Window dialogWindow = DialogUtil.getDialogWindow(getContext(), mWeightDialog);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();
        //        lp.alpha = 0.7f; // 透明度
        dialogWindow.setAttributes(lp);
        mWeightDialog.show();
    }


    //点击返回时，不被销毁
    public void finish() {
        if (AppLogic.getInstance().getToken() == null) {
            getActivity().finish();
        } else if (!AppLogic.getInstance().isInitialized()) {
            getActivity().finish();
        } else {
            getActivity().moveTaskToBack(true);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        finish();
    }

    private void openMiniClick(AppDay appDay) {//圈圈的点击事件
        isCalendar = true;
        iv_main_open2.setImageResource(R.drawable.ic_arrow_drop_up_white_24dp);
        ll_one.setVisibility(View.INVISIBLE);
        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        translateAnimation.setDuration(500);
        ll_two.setAnimation(translateAnimation);
        ll_two.setVisibility(View.VISIBLE);
        MainActivity.b = 1;
        userCalendarView.setCurrentItem(appDay.getDay());
        userCalendarView.setHightlight(appDay.getDay());
        showUI(appDay);
        uiSQLite(AppLogic.getInstance().queryBasic(getBasicString(appDay)));
        tv_main_today.setVisibility(View.VISIBLE);
        activity.mBottomLl.setVisibility(activity.mBottomLl.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private void initHealthReminder() {
        sharedPreferences = AppLogic.getInstance().getPrefs();//只执行一次
        editor = sharedPreferences.edit();
        if (sharedPreferences.getBoolean("isFirstReminder", true)) {
            editor.putBoolean("isFirstReminder", false);//只执行一次
            editor.putString("isBeginningHour", "10");
            editor.putString("isBeginningMinute", "00");
            editor.putString("isBeginningAMPM", "" + getString(R.string.am));
            editor.putString("isEndHour", "08");
            editor.putString("isEndMinute", "00");
            editor.putString("isEndAMPM", "" + getString(R.string.pm));
            editor.putString("isMedicationHour", "08");
            editor.putString("isMedicationMinute", "30");
            editor.putString("isMedicationAMPM", "" + getString(R.string.am));
            editor.apply();
            editor.commit();
            AppLogic.getInstance().setReminderBEGINNING(true);
            AppLogic.getInstance().setReminderEND(true);
            AppLogic.getInstance().setReminderMEDICATION(true);
            AppLogic.getInstance().setReminderDRINK(true);
        }
        ReminderUtil.setReminder(getContext());
    }

    public void showUI(AppDay appDay) {//用户点击或滑动日历，根据item的AppDay显示ui布局
        if (appDay.isFuture()) {//如果是以后
            //显示回到今天
            ll_save.setVisibility(View.INVISIBLE);
            ll_today.setVisibility(View.VISIBLE);
            tv_main_edit.setVisibility(View.GONE);
            switch (appDay.getDayType()) {
                case AppDay.DAY_TYPE_MENSTRUAL:
                    tv_tuisuan.setText(getString(R.string.tuisuandangrichuyuyuejingqi));
                    break;
                case AppDay.DAY_TYPE_OVULATION:
                    tv_tuisuan.setText(getString(R.string.tuisuandangrichuyupailuanqi));
                    break;
                case AppDay.DAY_TYPE_OVULATION_DAY:
                    tv_tuisuan.setText(getString(R.string.tuisuandangrichuyupailuanri));
                    break;
                default:
                    tv_tuisuan.setText("");
            }
        } else {
            //显示编辑保存的一系列icon
            ll_save.setVisibility(View.VISIBLE);
            ll_today.setVisibility(View.INVISIBLE);
            tv_main_edit.setVisibility(View.VISIBLE);
            Basic basic = AppLogic.getInstance().queryBasic(getBasicString(appDay));//coming显示痛经和月经量
            if (basic == null) {
                basic = new Basic();
            }
            if (appDay.getDayType() == AppDay.DAY_TYPE_MENSTRUAL || basic.isComing()) {//月经期显示痛经和月经量,coming也显示
                ll_show.setVisibility(View.VISIBLE);
            } else {
                ll_show.setVisibility(View.GONE);
            }
        }
    }

    private String getBasicString(AppDay appDay) {
        Calendar calendar = appDay.getDay();
        int YY = calendar.get(Calendar.YEAR);
        int MM = calendar.get(Calendar.MONTH) + 1;
        String M = "" + MM;
        if (MM < 10) {
            M = "0" + MM;
        }
        int DD = calendar.get(Calendar.DATE);
        String D = "" + DD;
        if (DD < 10) {
            D = "0" + DD;
        }
        String yyyyMMdd = YY + "" + M + "" + D;
        return yyyyMMdd;
    }

    private void setToolbarString(Calendar cal) {
        int y = Calendar.getInstance().get(Calendar.YEAR);
        int yy = cal.get(Calendar.YEAR);
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        if (y == yy) {
            tv_main_open.setText(sdf.format(cal.getTime()));
        } else {
            tv_main_open.setText("" + yy + "" + sdf.format(cal.getTime()));
        }
    }

    //气泡日历回到今天
    @Deprecated
    private void goToday() {
        //气泡刷新数据的时候，会影响左右的数据，再次滑动的时候会出现滑动不准确的情况
        userCalendarView.totalRefresh();
        mMiniCalendarAdapter.refreshData();
        setToolbarString(Calendar.getInstance());//设置toolbar內容
        updateTodayTip();
        mCal = Calendar.getInstance();
        setTvDate(mCal);
    }

    private void setTvDate(Calendar Cal) {
        int m = Cal.get(Calendar.MONTH) + 1;
        int d = Cal.get(Calendar.DAY_OF_MONTH);
        String mm = "" + m;
        String dd = "" + d;
        if (m < 10) {
            mm = "0" + m;
        }
        if (d < 10) {
            dd = "0" + d;
        }
        tv_date.setText(mm + "." + dd);
    }

    private void showInitializedDialog() {
        DialogUtil.showDialog(getContext(), getString(R.string.messageBianji), new DialogUtil.OnDialogUtilListener() {
            @Override
            public void onDialogUtil_YES() {
                startActivity(new Intent(getContext(), SelectStatusActivity.class));
            }

            @Override
            public void onDialogUtil_CANCEL() {
            }
        });
    }

    /**
     * 网络初始化tab标签
     */
    void initTab() {
        ServiceGenerator.createServcie(getContext(), ApiService.class).AppCfg().enqueue(new Callback<Tab>() {
            @Override
            public void onResponse(Call<Tab> call, Response<Tab> response) {
                Tab tab = response.body();
                if (tab == null) {
                    dialog.dismiss();
                    return;
                }
                if (tab.getCode() == Constants.REQUEST_SUCCESS) {
                    articleTabs = tab.getData().getArticle_tabs();
                    if (LanguageUtil.isALB()) {//如果是阿拉伯，反转list数据
                        Collections.reverse(articleTabs);
                    }
                    initRecycleViewPager(articleTabs);
                    netWorkRl.setVisibility(View.GONE);
                } else {
                    netWorkRl.setVisibility(View.VISIBLE);
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<Tab> call, Throwable t) {
                netWorkRl.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });
    }

    protected int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 38;//38大部分是这样的
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }

}
