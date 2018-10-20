package com.taiqudong.android.ad;

import android.support.annotation.Keep;

import java.util.List;

/**
 * 每个广告位的设置
 * Created by zhangxiang on 2017/9/4.
 */
@Keep
public class AdConfig {

    //app中的广告位id， 这个是app中预设的 id
    String placeId;

    //广告的展示类型,对应不同的UI type1 type2
    String type;

    //如果广告出现在feed 流中，广告之间的间隔
    int step;

    private List<Source> sources;

    public List<Source> getSources() {
        return sources;
    }

    public void setSources(List<Source> sources) {
        this.sources = sources;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    /**
     * 广告数据源
     */
    public static class Source {

        public final static String AD_TYPE_GG = Constants.AD_TYPE_GG; //来自google的广告
        public final static String AD_TYPE_FB = Constants.AD_TYPE_FB; //来自facebook的广告

        private String adKey;
        private String adType;

        public String getAdKey() {
            return adKey;
        }

        public void setAdKey(String adKey) {
            this.adKey = adKey;
        }

        public String getAdType() {
            return adType;
        }

        public void setAdType(String adType) {
            this.adType = adType;
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}
