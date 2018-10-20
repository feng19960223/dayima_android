package com.taiqudong.android.enayeh.application.retrofit.bean;

import java.util.List;

/**
 * Created by tangxianming on 2017/9/7.
 */

public class AdConfig extends Basic {

    /**
     * data : {"ads":[{"placeId":"Backhome","type":"type3","sources":[{"adKey":"1336833246413944_1387325374698064","adType":"fb"},{"adKey":"ca-app-pub-9477775406918793/3214875149","adType":"gg"}]},{"placeId":"Splash","type":"type3","sources":[{"adKey":"1336833246413944_1387325374698064","adType":"fb"},{"adKey":"ca-app-pub-9477775406918793/3214875149","adType":"gg"}]}],"global":{"requestTimeout":8000,"cacheTimeout":1800000,"launchAdTimeout":5000,"backhomeAdInterval":144000000,"globalAd":{"placeId":"global","type":"type1","step":0,"sources":[{"adKey":"1336833246413944_1387325998031335","adType":"fb"},{"adKey":"ca-app-pub-9477775406918793/5442594990","adType":"gg"}]}}}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * ads : [{"placeId":"Backhome","type":"type3","sources":[{"adKey":"1336833246413944_1387325374698064","adType":"fb"},{"adKey":"ca-app-pub-9477775406918793/3214875149","adType":"gg"}]},{"placeId":"Splash","type":"type3","sources":[{"adKey":"1336833246413944_1387325374698064","adType":"fb"},{"adKey":"ca-app-pub-9477775406918793/3214875149","adType":"gg"}]}]
         * global : {"requestTimeout":8000,"cacheTimeout":1800000,"launchAdTimeout":5000,"backhomeAdInterval":144000000,"globalAd":{"placeId":"global","type":"type1","step":0,"sources":[{"adKey":"1336833246413944_1387325998031335","adType":"fb"},{"adKey":"ca-app-pub-9477775406918793/5442594990","adType":"gg"}]}}
         */

        private GlobalBean global;
        private List<AdsBean> ads;

        public GlobalBean getGlobal() {
            return global;
        }

        public void setGlobal(GlobalBean global) {
            this.global = global;
        }

        public List<AdsBean> getAds() {
            return ads;
        }

        public void setAds(List<AdsBean> ads) {
            this.ads = ads;
        }

        public static class GlobalBean {
            /**
             * requestTimeout : 8000
             * cacheTimeout : 1800000
             * launchAdTimeout : 5000
             * backhomeAdInterval : 144000000
             * globalAd : {"placeId":"global","type":"type1","step":0,"sources":[{"adKey":"1336833246413944_1387325998031335","adType":"fb"},{"adKey":"ca-app-pub-9477775406918793/5442594990","adType":"gg"}]}
             */

            private int requestTimeout;
            private int cacheTimeout;
            private int launchAdTimeout;
            private int backhomeAdInterval;
            private GlobalAdBean globalAd;

            public int getRequestTimeout() {
                return requestTimeout;
            }

            public void setRequestTimeout(int requestTimeout) {
                this.requestTimeout = requestTimeout;
            }

            public int getCacheTimeout() {
                return cacheTimeout;
            }

            public void setCacheTimeout(int cacheTimeout) {
                this.cacheTimeout = cacheTimeout;
            }

            public int getLaunchAdTimeout() {
                return launchAdTimeout;
            }

            public void setLaunchAdTimeout(int launchAdTimeout) {
                this.launchAdTimeout = launchAdTimeout;
            }

            public int getBackhomeAdInterval() {
                return backhomeAdInterval;
            }

            public void setBackhomeAdInterval(int backhomeAdInterval) {
                this.backhomeAdInterval = backhomeAdInterval;
            }

            public GlobalAdBean getGlobalAd() {
                return globalAd;
            }

            public void setGlobalAd(GlobalAdBean globalAd) {
                this.globalAd = globalAd;
            }

            public static class GlobalAdBean {
                /**
                 * placeId : global
                 * type : type1
                 * step : 0
                 * sources : [{"adKey":"1336833246413944_1387325998031335","adType":"fb"},{"adKey":"ca-app-pub-9477775406918793/5442594990","adType":"gg"}]
                 */

                private String placeId;
                private String type;
                private int step;
                private List<SourcesBean> sources;

                public String getPlaceId() {
                    return placeId;
                }

                public void setPlaceId(String placeId) {
                    this.placeId = placeId;
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

                public List<SourcesBean> getSources() {
                    return sources;
                }

                public void setSources(List<SourcesBean> sources) {
                    this.sources = sources;
                }

                public static class SourcesBean {
                    /**
                     * adKey : 1336833246413944_1387325998031335
                     * adType : fb
                     */

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
            }
        }

        public static class AdsBean {
            /**
             * placeId : Backhome
             * type : type3
             * sources : [{"adKey":"1336833246413944_1387325374698064","adType":"fb"},{"adKey":"ca-app-pub-9477775406918793/3214875149","adType":"gg"}]
             */

            private String placeId;
            private String type;
            private List<SourcesBeanX> sources;

            public String getPlaceId() {
                return placeId;
            }

            public void setPlaceId(String placeId) {
                this.placeId = placeId;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public List<SourcesBeanX> getSources() {
                return sources;
            }

            public void setSources(List<SourcesBeanX> sources) {
                this.sources = sources;
            }

            public static class SourcesBeanX {
                /**
                 * adKey : 1336833246413944_1387325374698064
                 * adType : fb
                 */

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
        }
    }
}
