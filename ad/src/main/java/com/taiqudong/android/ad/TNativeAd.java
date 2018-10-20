package com.taiqudong.android.ad;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.ads.AdChoicesView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;

import java.util.ArrayList;
import java.util.List;

/**
 * 代表一个广告，正常情况下会与一个view进行绑定
 * 不要直接调用构造方法 ，请使用admanager生成.
 * Created by zhangxiang on 2017/9/4.
 */
public class TNativeAd {
    private AdConfig adConfig;
    private ViewGroup parent;
    private static final String TAG = "TNativeAd";
    private AdContent adContent;

    /**
     * @param adConfig
     * @param parent
     */
    public TNativeAd(AdConfig adConfig, ViewGroup parent) {
        this.adConfig = adConfig;
        this.parent = parent;
    }

    //TODO 确保和服务端同步
    public static String typeBig = "type1";//big
    public static String typeSmall = "type2";//small
    public static String typeSplash = "type3";//splash（不受服务端的配置影响，目前设定就是插屏，插屏没有区分类型。）

    //TODO 配置AdContent.
    public TNativeAd setAdContent(AdContent adContent) {
        this.adContent = adContent;
        return this;
    }


    /**
     * TODO 加载广告到视图
     */
    public void loadAd() {
        AdParallelLoader loader = new AdParallelLoader(adConfig);
        loader.setAdLoadListener(new AdParallelLoader.AdLoadListener() {
            /**
             * 加载广告
             * @param ad
             */
            @Override
            public void onAdLoaded(AdContent ad) {
                adContent = ad;
                bindView();
            }

            /**
             * TODO 处理错误
             * @param error
             */
            @Override
            public void onError(AdError error) {
                Log.e(TAG, error.getMessage());
            }
        });
        //TODO 设置超时时长
        GlobalConfig.getInstance().setChoosedAdTimeout(GlobalConfig.getInstance().getRequestTimeoutMs());
        loader.loadAd();
    }

    /**
     * TODO 加载广告到视图
     */
    public void loadAd(final ContentAdListener contentAdListener) {
        AdParallelLoader loader = new AdParallelLoader(adConfig);
        loader.setAdLoadListener(new AdParallelLoader.AdLoadListener() {
            /**
             * 加载广告
             * @param ad
             */
            @Override
            public void onAdLoaded(AdContent ad) {
                adContent = ad;
                contentAdListener.call(adContent);
                bindView();
            }

            /**
             * TODO 处理错误
             * @param error
             */
            @Override
            public void onError(AdError error) {
                Log.e(TAG, error.getMessage());
                contentAdListener.onError(error);
            }
        });
        //TODO 设置超时时长
        GlobalConfig.getInstance().setChoosedAdTimeout(GlobalConfig.getInstance().getRequestTimeoutMs());
        loader.loadAd();
        Log.i(TAG, "loadAd: ");
    }

    /**
     * TODO 加载并展示插屏广告的入口
     */
    public void loadSplashAd(final ViewListener viewListener) {
        AdParallelLoader loader = new AdParallelLoader(adConfig);
        loader.setAdLoadListener(new AdParallelLoader.AdLoadListener() {
            /**
             * 加载广告
             * @param ad
             */
            @Override
            public void onAdLoaded(AdContent ad) {
                adContent = ad;
                switch (ad.getAdType()) {
                    case AdContent.FB_AD:
                        Log.d(TAG, "start load fb splash to view");
                        displayFBSplashContent(ad.getFbAd(), viewListener);
                        break;
                    case AdContent.GG_APPINSTALL_AD:
                        Log.d(TAG, "load gg_appinstall splash to view");
                        displayGGSplashInstall(ad.getGgAppInstallAd(), viewListener);
                        break;
                    case AdContent.GG_CONTENT_AD:
                        Log.d(TAG, "load gg_content splash to view");
                        displayGGSplashContent(ad.getGgContentAd(), viewListener);
                        break;
                    default:
                        Log.w(TAG, "未知的广告类型");
                        break;
                }
            }

            /**
             * TODO 处理错误
             * @param error
             */
            @Override
            public void onError(AdError error) {
                Log.e(TAG, error.getMessage());
                viewListener.error(error);
            }
        });
        //TODO 设置不同类型的闪屏对应的超时时长
        if (Constants.AD_SPLASH_LOGIN.equals(adConfig.getPlaceId())) {
            GlobalConfig.getInstance().setChoosedAdTimeout(GlobalConfig.getInstance().getLaunchAdTimeout());
        } else if (Constants.AD_SPLASH_BACKHOME.equals(adConfig.getPlaceId())) {
            GlobalConfig.getInstance().setChoosedAdTimeout(GlobalConfig.getInstance().getRequestTimeoutMs());
        }
        loader.loadAd();
    }


    /**
     * 将广告重新绑定view, 应用于recylerview 等场景
     */
    public void bindView() {
        switch (adContent.getAdType()) {
            case AdContent.FB_AD:
                if (typeBig.equals(adConfig.getType())) {
                    displayFBContent_big(adContent.getFbAd(), parent);
                } else if (typeSmall.equals(adConfig.getType())) {
                    displayFBContent_small(adContent.getFbAd(), parent);
                }
                break;
            case AdContent.GG_APPINSTALL_AD:
                if (typeBig.equals(adConfig.getType())) {
                    displayGGAppInstall_big(adContent.getGgAppInstallAd(), parent);
                } else if (typeSmall.equals(adConfig.getType())) {
                    displayGGAppInstall_small(adContent.getGgAppInstallAd(), parent);
                }
                break;
            case AdContent.GG_CONTENT_AD:
                if (typeBig.equals(adConfig.getType())) {
                    displayGGContent_big(adContent.getGgContentAd(), parent);
                } else if (typeSmall.equals(adConfig.getType())) {
                    displayGGContent_small(adContent.getGgContentAd(), parent);
                }
                break;
            default:
                Log.w(TAG, "未知的广告类型");
                break;

        }
    }


    /**
     * TODO 谷歌内容插屏广告
     *
     * @param ggContentAd
     */
    private void displayGGSplashContent(NativeContentAd ggContentAd, final ViewListener viewListener) {
        NativeContentAdView adView = (NativeContentAdView) LayoutInflater.from(GlobalConfig.getInstance().getContext())
                .inflate(R.layout.ad_gg_content_splash, null);
        adView.setHeadlineView(adView.findViewById(R.id.ad_tv_title));
        adView.setImageView(adView.findViewById(R.id.ad_iv_image));
        adView.setBodyView(adView.findViewById(R.id.ad_tv_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_tv_call_to_action));
        adView.setLogoView(adView.findViewById(R.id.ad_iv_logo));
        // Some assets are guaranteed to be in every NativeContentAd.
        ((TextView) adView.getHeadlineView()).setText(ggContentAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(ggContentAd.getBody());
        ((TextView) adView.getCallToActionView()).setText(ggContentAd.getCallToAction());
        List<NativeAd.Image> images = ggContentAd.getImages();
        if (images.size() > 0) {
            ((ImageView) adView.getImageView()).setImageDrawable(images.get(0).getDrawable());
        }
        //Hide adChoice.
        LinearLayout choiceAd = (LinearLayout) adView.findViewById(R.id.ad_ll_Choices);
        choiceAd.setVisibility(View.GONE);
        // Some aren't guaranteed, however, and should be checked.
        NativeAd.Image logoImage = ggContentAd.getLogo();
        if (logoImage == null) {
            adView.getLogoView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) adView.getLogoView()).setImageDrawable(logoImage.getDrawable());
            adView.getLogoView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(ggContentAd);
        //register close listener
        ImageView closeIv = (ImageView) adView.findViewById(R.id.ad_iv_close);
        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewListener.onClose();
            }
        });
        parent.removeAllViews();
        parent.addView(adView);
        viewListener.loaded();
    }

    /**
     * TODO 谷歌应用插屏广告
     *
     * @param ggAppInstallAd
     */
    private void displayGGSplashInstall(NativeAppInstallAd ggAppInstallAd, final ViewListener viewListener) {
        /**
         * TODO
         * 加载谷歌app广告视图
         * txm 20170905
         */
        NativeAppInstallAdView adView = (NativeAppInstallAdView) LayoutInflater.from(GlobalConfig.getInstance().getContext())
                .inflate(R.layout.ad_gg_install_splash, null);

        adView.setHeadlineView(adView.findViewById(R.id.ad_tv_title));
        adView.setBodyView(adView.findViewById(R.id.ad_tv_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_tv_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_iv_logo));
        //set coverview.
        adView.setImageView(adView.findViewById(R.id.ad_iv_image));
//        ImageView adImageView = (ImageView) adView.findViewById(R.id.ad_iv_image);

        List<NativeAd.Image> images = ggAppInstallAd.getImages();
        if (images.size() > 0) {
            ((ImageView)adView.getImageView()).setImageDrawable(images.get(0).getDrawable());
        }
        //hide adChoice.
        LinearLayout choiceAd = (LinearLayout) adView.findViewById(R.id.ad_ll_Choices);
        choiceAd.setVisibility(View.GONE);

        // Some assets are guaranteed to be in every NativeAppInstallAd.
        ((TextView) adView.getHeadlineView()).setText(ggAppInstallAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(ggAppInstallAd.getBody());
        ((TextView) adView.getCallToActionView()).setText(ggAppInstallAd.getCallToAction());
        ((ImageView) adView.getIconView()).setImageDrawable(
                ggAppInstallAd.getIcon().getDrawable());
        // Assign native ad object to the native view.
        adView.setNativeAd(ggAppInstallAd);
        //register close listener
        ImageView closeIv = (ImageView) adView.findViewById(R.id.ad_iv_close);
        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: ");
                viewListener.onClose();
            }
        });
        parent.removeAllViews();
        parent.addView(adView);
        viewListener.loaded();
    }

    /**
     * TODO 脸书插屏广告
     *
     * @param fbAd
     */
    private void displayFBSplashContent(com.facebook.ads.NativeAd fbAd, final ViewListener viewListener) {
        // Add the Ad view into the ad container.
        LinearLayout adView = (LinearLayout) LayoutInflater.from(GlobalConfig.getInstance().getContext()).inflate(R.layout.ad_fb_splash, null);
        parent.removeAllViews();
        parent.addView(adView);
        // Create native UI using the ad metadata.
        ImageView nativeAdIcon = (ImageView) adView.findViewById(R.id.ad_iv_logo);

        TextView nativeAdTitle = (TextView) adView.findViewById(R.id.ad_tv_title);
        ImageView nativeAdImage = (ImageView) adView.findViewById(R.id.ad_iv_image);
        TextView nativeAdBody = (TextView) adView.findViewById(R.id.ad_tv_body);
        TextView nativeAdCallToAction = (TextView) adView.findViewById(R.id
                .ad_tv_call_to_action);
        TextView nativeAdAd = (TextView) adView.findViewById(R.id.ad_ad);

        // Set the Text.
        nativeAdTitle.setText(fbAd.getAdTitle());
        nativeAdBody.setText(fbAd.getAdBody());
        nativeAdCallToAction.setText(fbAd.getAdCallToAction());

        // Download and display the ad icon.
        com.facebook.ads.NativeAd.Image adIcon = fbAd.getAdIcon();
        com.facebook.ads.NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);

        // Download and display the cover image.
        //        Glide.with(GlobalConfig.getInstance().getContext()).load(fbAd.getAdCoverImage().getUrl()).into(nativeAdImage);
        com.facebook.ads.NativeAd.downloadAndDisplayImage(fbAd.getAdCoverImage(), nativeAdImage);
        // Add the AdChoices icon
        LinearLayout adChoicesContainer = (LinearLayout) adView.findViewById(R.id
                .ad_ll_Choices);
        AdChoicesView adChoicesView = new AdChoicesView(GlobalConfig.getInstance().getContext(), fbAd, true);
        adChoicesContainer.addView(adChoicesView);

        //register close listener
        ImageView closeIv = (ImageView) adView.findViewById(R.id.ad_iv_close);
        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewListener.onClose();
            }
        });
        // Register the Title and CTA button to listen for clicks.
        //        fbAd.registerViewForInteraction(adView);

        //把close图片的点击事件放出来
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdIcon);
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdImage);
        clickableViews.add(nativeAdBody);
        clickableViews.add(nativeAdCallToAction);
        clickableViews.add(nativeAdAd);
        fbAd.registerViewForInteraction(adView, clickableViews);
        viewListener.loaded();
    }

    /**
     * 销毁这个广告
     */
    public void destory() {

    }


    /**
     * TODO 插屏广告关闭的监听
     */
    public interface ViewListener {
        void onClose();

        void loaded();

        void error(AdError error);
    }


    // TODO: 2017/9/7 冯国芮
    // TODO: 2017/9/7 displayFBContent_small
    // TODO: 2017/9/7 displayFBContent_big
    // TODO: 2017/9/7 displayGGContent_big
    // TODO: 2017/9/7 displayGGContent_small
    // TODO: 2017/9/7 displayGGAppInstall_big
    // TODO: 2017/9/7 displayGGAppInstall_small

    /**
     * TODO 显示facebook广告
     * big
     */
    private void displayFBContent_big(com.facebook.ads.NativeAd fbAd, ViewGroup viewGroup) {
        if (fbAd != null) {
            fbAd.unregisterView();
        }

        LinearLayout fbAdView = (LinearLayout) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.ad_fb_big, null);
        viewGroup.removeAllViews();
        viewGroup.addView(fbAdView);

        ImageView fbNativeAdImage = (ImageView) fbAdView.findViewById(R.id.ad_iv_image);
        FrameLayout fbNativeAdChoices = (FrameLayout) fbAdView.findViewById(R.id.ad_fl_Choices);
        TextView fbNativeAdBody = (TextView) fbAdView.findViewById(R.id.ad_tv_body);
        ImageView fbNativeAdLogo = (ImageView) fbAdView.findViewById(R.id.ad_iv_logo);
        TextView fbNativeATitle = (TextView) fbAdView.findViewById(R.id.ad_tv_title);
        TextView fbNativeAdCallToAction = (TextView) fbAdView.findViewById(R.id.ad_tv_call_to_action);

        com.facebook.ads.AdChoicesView adChoicesView = new com.facebook.ads.AdChoicesView(GlobalConfig.getInstance().getContext(), fbAd, true);
        adChoicesView.setBackgroundColor(Color.parseColor("#CC000000"));
        fbNativeAdChoices.addView(adChoicesView);

        //绑定数据.
        com.facebook.ads.NativeAd.downloadAndDisplayImage(fbAd.getAdCoverImage(), fbNativeAdImage);
        fbNativeAdBody.setText(fbAd.getAdBody());
        com.facebook.ads.NativeAd.downloadAndDisplayImage(fbAd.getAdIcon(), fbNativeAdLogo);
        fbNativeATitle.setText(fbAd.getAdTitle());
        fbNativeAdCallToAction.setText(fbAd.getAdCallToAction());
        fbAd.registerViewForInteraction(fbAdView);
    }

    /**
     * TODO 显示facebook广告
     * small
     */
    private void displayFBContent_small(com.facebook.ads.NativeAd fbAd, ViewGroup viewGroup) {
        if (fbAd != null) {
            fbAd.unregisterView();
        }

        LinearLayout fbAdView = (LinearLayout) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.ad_fb_small, null);
        viewGroup.removeAllViews();
        viewGroup.addView(fbAdView);

        ImageView fbNativeAdImage = (ImageView) fbAdView.findViewById(R.id.ad_iv_image);
        FrameLayout fbNativeAdChoices = (FrameLayout) fbAdView.findViewById(R.id.ad_fl_Choices);
        TextView fbNativeATitle = (TextView) fbAdView.findViewById(R.id.ad_tv_title);
        TextView fbNativeAdBody = (TextView) fbAdView.findViewById(R.id.ad_tv_body);
        TextView fbNativeAdCallToAction = (TextView) fbAdView.findViewById(R.id.ad_tv_call_to_action);

        ImageView fbNativeAdLogo = (ImageView) fbAdView.findViewById(R.id.ad_iv_logo);
        if (fbAd.getAdIcon() != null) {//有LOGO
            com.facebook.ads.NativeAd.downloadAndDisplayImage(fbAd.getAdIcon(), fbNativeAdLogo);
            fbNativeAdLogo.setVisibility(View.VISIBLE);
        } else {//没有LOGO
            fbNativeAdLogo.setVisibility(View.GONE);
        }
        //绑定数据
        com.facebook.ads.NativeAd.downloadAndDisplayImage(fbAd.getAdCoverImage(), fbNativeAdImage);
        com.facebook.ads.AdChoicesView adChoicesView = new com.facebook.ads.AdChoicesView(GlobalConfig.getInstance().getContext(), fbAd, true);
        adChoicesView.setBackgroundColor(Color.parseColor("#CC000000"));

        fbNativeAdChoices.addView(adChoicesView);
        fbNativeAdBody.setText(fbAd.getAdBody());
        fbNativeATitle.setText(fbAd.getAdTitle());
        fbNativeAdCallToAction.setText(fbAd.getAdCallToAction());
        fbAd.registerViewForInteraction(fbAdView);
    }

    /**
     * TODO 显示google广告Content
     * big
     */
    private void displayGGContent_big(NativeContentAd ggContentAd, ViewGroup viewGroup) {

        NativeContentAdView ggAdView = (NativeContentAdView) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.ad_gg_content_big, null);
        viewGroup.removeAllViews();
        viewGroup.addView(ggAdView);

        ImageView ggNativeAdImage = (ImageView) ggAdView.findViewById(R.id.ad_iv_image);//图片
        FrameLayout ggNativeAdChoices = (FrameLayout) ggAdView.findViewById(R.id.ad_fl_Choices);//蓝三角
        TextView ggNativeAdBody = (TextView) ggAdView.findViewById(R.id.ad_tv_body);//内容
        ImageView ggNativeAdLogo = (ImageView) ggAdView.findViewById(R.id.ad_iv_logo);//logo
        TextView ggNativeATitle = (TextView) ggAdView.findViewById(R.id.ad_tv_title);//title
        TextView ggNativeAdCallToAction = (TextView) ggAdView.findViewById(R.id.ad_tv_call_to_action);//安装

        ggNativeAdChoices.setVisibility(View.GONE);//gg没有这个view

        ggAdView.setImageView(ggNativeAdImage);

        ggAdView.setBodyView(ggNativeAdBody);
        ggAdView.setLogoView(ggNativeAdLogo);
        ggAdView.setHeadlineView(ggNativeATitle);
        ggAdView.setCallToActionView(ggNativeAdCallToAction);

        //设置内容
        List<NativeAd.Image> images = ggContentAd.getImages();
        if (images.size() > 0) {
            ((ImageView) ggAdView.getImageView()).setImageDrawable(images.get(0).getDrawable());//1
        }

        NativeAd.Image logoImage = ggContentAd.getLogo();

        if (logoImage == null) {
            ggAdView.getLogoView().setVisibility(View.GONE);
        } else {
            ((ImageView) ggAdView.getLogoView()).setImageDrawable(logoImage.getDrawable());//2
            ggAdView.getLogoView().setVisibility(View.VISIBLE);
        }

        ((TextView) ggAdView.getBodyView()).setText(ggContentAd.getHeadline());//3
        ((TextView) ggAdView.getHeadlineView()).setText(ggContentAd.getBody());//4
        ((TextView) ggAdView.getCallToActionView()).setText(ggContentAd.getCallToAction());//5

        ggAdView.setNativeAd(ggContentAd);
    }

    /**
     * TODO 显示google广告Content
     * small
     */
    private void displayGGContent_small(NativeContentAd ggContentAd, ViewGroup viewGroup) {

        NativeContentAdView ggAdView = (NativeContentAdView) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.ad_gg_content_small, null);
        viewGroup.removeAllViews();
        viewGroup.addView(ggAdView);


        ImageView ggNativeAdImage = (ImageView) ggAdView.findViewById(R.id.ad_iv_image);//图片
        FrameLayout ggNativeAdChoices = (FrameLayout) ggAdView.findViewById(R.id.ad_fl_Choices);//蓝三角
        TextView ggNativeAdBody = (TextView) ggAdView.findViewById(R.id.ad_tv_body);//内容
        ImageView ggNativeAdLogo = (ImageView) ggAdView.findViewById(R.id.ad_iv_logo);//logo
        TextView ggNativeATitle = (TextView) ggAdView.findViewById(R.id.ad_tv_title);//title
        TextView ggNativeAdCallToAction = (TextView) ggAdView.findViewById(R.id.ad_tv_call_to_action);//安装

        // TODO: 2017/9/7 ???? Google下没有发现ad字样
        TextView ggNativeSource = (TextView) ggAdView.findViewById(R.id.ad_ad);//安装

        ggNativeAdChoices.setVisibility(View.GONE);//GG没有蓝色三角

        ggAdView.setImageView(ggNativeAdImage);
        ggAdView.setBodyView(ggNativeAdBody);
        ggAdView.setLogoView(ggNativeAdLogo);
        ggAdView.setHeadlineView(ggNativeATitle);
        ggAdView.setCallToActionView(ggNativeAdCallToAction);

        //设置内容
        List<NativeAd.Image> images = ggContentAd.getImages();
        if (images.size() > 0) {
            ((ImageView) ggAdView.getImageView()).setImageDrawable(images.get(0).getDrawable());
        }

        NativeAd.Image logoImage = ggContentAd.getLogo();

        if (logoImage == null) {
            ggAdView.getLogoView().setVisibility(View.GONE);
        } else {
            ((ImageView) ggAdView.getLogoView()).setImageDrawable(logoImage.getDrawable());
            ggAdView.getLogoView().setVisibility(View.VISIBLE);
        }

        ((TextView) ggAdView.getBodyView()).setText(ggContentAd.getHeadline());
        ((TextView) ggAdView.getHeadlineView()).setText(ggContentAd.getBody());
        ((TextView) ggAdView.getCallToActionView()).setText(ggContentAd.getCallToAction());

        ggAdView.setNativeAd(ggContentAd);
    }

    /**
     * TODO 显示google广告AppInstall
     * big
     */
    private void displayGGAppInstall_big(NativeAppInstallAd ggAppInstallAd, ViewGroup viewGroup) {

        NativeAppInstallAdView ggAdView = (NativeAppInstallAdView) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.ad_gg_install_big, null);
        viewGroup.removeAllViews();
        viewGroup.addView(ggAdView);


        ImageView ggNativeAdImage = (ImageView) ggAdView.findViewById(R.id.ad_iv_image);//图片
        FrameLayout ggNativeAdChoices = (FrameLayout) ggAdView.findViewById(R.id.ad_fl_Choices);//蓝三角
        TextView ggNativeAdBody = (TextView) ggAdView.findViewById(R.id.ad_tv_body);//内容
        ImageView ggNativeAdLogo = (ImageView) ggAdView.findViewById(R.id.ad_iv_logo);//logo
        TextView ggNativeATitle = (TextView) ggAdView.findViewById(R.id.ad_tv_title);//title
        TextView ggNativeAdCallToAction = (TextView) ggAdView.findViewById(R.id.ad_tv_call_to_action);//安装

        ggNativeAdChoices.setVisibility(View.GONE);//gg没有这个view

        ggAdView.setImageView(ggNativeAdImage);
        ggAdView.setBodyView(ggNativeAdBody);
        ggAdView.setIconView(ggNativeAdLogo);
        ggAdView.setHeadlineView(ggNativeATitle);
        ggAdView.setCallToActionView(ggNativeAdCallToAction);

        //设置内容
        List<NativeAd.Image> images = ggAppInstallAd.getImages();
        if (images.size() > 0) {
            ((ImageView) ggAdView.getImageView()).setImageDrawable(images.get(0).getDrawable());
        }

        NativeAd.Image logoImage = ggAppInstallAd.getIcon();

        if (logoImage == null) {
            ggAdView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) ggAdView.getIconView()).setImageDrawable(logoImage.getDrawable());
            ggAdView.getIconView().setVisibility(View.VISIBLE);
        }

        ((TextView) ggAdView.getBodyView()).setText(ggAppInstallAd.getHeadline());
        ((TextView) ggAdView.getHeadlineView()).setText(ggAppInstallAd.getBody());
        ((TextView) ggAdView.getCallToActionView()).setText(ggAppInstallAd.getCallToAction());
        ggAdView.setNativeAd(ggAppInstallAd);
    }

    /**
     * TODO 显示google广告AppInstall
     * small
     */
    private void displayGGAppInstall_small(NativeAppInstallAd ggAppInstallAd, ViewGroup viewGroup) {
        NativeAppInstallAdView ggAdView = (NativeAppInstallAdView) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.ad_gg_install_small, null);
        viewGroup.removeAllViews();
        viewGroup.addView(ggAdView);

        ImageView ggNativeAdImage = (ImageView) ggAdView.findViewById(R.id.ad_iv_image);//图片
        FrameLayout ggNativeAdChoices = (FrameLayout) ggAdView.findViewById(R.id.ad_fl_Choices);//蓝三角
        TextView ggNativeAdBody = (TextView) ggAdView.findViewById(R.id.ad_tv_body);//内容
        ImageView ggNativeAdLogo = (ImageView) ggAdView.findViewById(R.id.ad_iv_logo);//logo
        TextView ggNativeATitle = (TextView) ggAdView.findViewById(R.id.ad_tv_title);//title
        TextView ggNativeAdCallToAction = (TextView) ggAdView.findViewById(R.id.ad_tv_call_to_action);//安装

        ggNativeAdChoices.setVisibility(View.GONE);//GG没有蓝色三角

        ggAdView.setImageView(ggNativeAdImage);
        ggAdView.setBodyView(ggNativeAdBody);
        ggAdView.setIconView(ggNativeAdLogo);
        ggAdView.setHeadlineView(ggNativeATitle);
        ggAdView.setCallToActionView(ggNativeAdCallToAction);

        //设置内容
        List<NativeAd.Image> images = ggAppInstallAd.getImages();
        if (images.size() > 0) {
            ((ImageView) ggAdView.getImageView()).setImageDrawable(images.get(0).getDrawable());
        }

        NativeAd.Image logoImage = ggAppInstallAd.getIcon();

        if (logoImage == null) {
            ggAdView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) ggAdView.getIconView()).setImageDrawable(logoImage.getDrawable());
            ggAdView.getIconView().setVisibility(View.VISIBLE);
        }

        ((TextView) ggAdView.getBodyView()).setText(ggAppInstallAd.getHeadline());
        ((TextView) ggAdView.getHeadlineView()).setText(ggAppInstallAd.getBody());
        ((TextView) ggAdView.getCallToActionView()).setText(ggAppInstallAd.getCallToAction());
        ggAdView.setNativeAd(ggAppInstallAd);
    }

    /**
     * TODO 广告的内容回调
     */
    public interface ContentAdListener {
        void call(AdContent adContent);

        void onError(AdError error);
    }

}
