package com.taiqudong.android.ad.stats;

/**
 * Created by zhangxiang on 2017/8/7.
 */

public interface EventConsts {

    String Cong = "desc"; //表示动作从哪里发生

    String e_RiLiJieMianZhanShi = "RiLiJieMianZhanShi";//日历界面展示
    String e_JingQiBianJi = "JingQiBianJi";//经期编辑
    String e_JingQiGuanLiMianBanZhanShi = "JingQiGuanLiMianBanZhanShi";//经期管理面板展示
    String e_ZuoAiJiLuMianBanZhanShi = "ZuoAiJiLuMianBanZhanShi";//做爱记录面板展示
    String e_XinQingMianBanZhanShi = "XinQingMianBanZhanShi";//心情面板展示
    String e_JianKangJiLuZhanShi = "JianKangJiLuZhanShi";//健康记录展示
    String e_JingQiGuanLiBianJi = "JingQiGuanLiBianJi";//经期管理编辑
    String e_ZuoAiJiLuBianJi = "ZuoAiJiLuBianJi";//做爱记录编辑
    String e_XinQiMianBanBianJi = "XinQingMianBanBianJi";//心情面板编辑
    String e_JianKangJiLuBianJi = "JianKangJiLuBianJi";//健康记录编辑
    String e_TiXingJieMianZhanShi = "TiXingJieMianZhanShi";//提醒界面展示
    String e_XiangQingYeZhanShi = "XiangQingYeZhanShi";//详情页展示
    String e_TiZhongJiLu = "TiZhongJiLu";//体重记录

    String p_DianJiRiLiRuKou = "DianJiRiLiRuKou";//点击日历入口
    String p_DianJiQiPao = "DianJiQiPao";//点击气泡
    String p_DianJiCeBianLan = "DianJiCeBianLan";//点击侧边栏
    String p_DianJiBianJiAnNiu = "DianJiBianJiAnNiu";//点击编辑按钮
    String p_BianJiJingQi = "BianJiJingQi";//编辑经期
    String p_JingQiGuanLiAnNiuDianJi = "JingQiGuanLiAnNiuDianJi";//经期管理按钮点击
    String p_ZuoAiJiLuAnNiuDianJi = "ZuoAiJiLuAnNiuDianJi";//做爱记录按钮点击
    String p_XinQingAnNiuDianJi = "XinQingAnNiuDianJi";//心情按钮点击
    String p_JianKangJiLuAnNiuDinaJi = "JianKangJiLuAnNiuDianJi";//健康记录按钮点击
    String p_ShouYeMianBanJingXingBianJi = "ShouYeMianBanJinXingBianJi";//首页面板进行编辑
    String p_RiLiYeMianJingXingBianJi = "ShouYeTiXingAnNiuDianJi";//日历页面进行编辑
    String p_ShouYeTiXingAnNiuDianJi = "ShouYeTiXingAnNiuDianJi";//首页提醒按钮点击
    String p_CaiDanZhongTiXingAnNiuDianJi = "CaiDanZhongTiXingAnNiuDianJi";//菜单中提醒按钮点击
    String p_DianJiCiShu = "DianJiCiShu";//点击次数
    String p_JiLuChengGongCiShu = "JiLuChengGongCiShu";//记录成功次数

    String bz_ShouYeJianKangAnNiu = "ShouYeJianKangAnNiu";//首页健康按钮
    String bz_RiLiYe = "RiLiYe";//日历页
    String bz_TiZhongBiao = "TiZhongBiao";//体重表


    //大姨妈第二版，新打点所有用的关键字符串
    String HomeFeedShow = "HomeFeedShow";
    String TurnPagePic = "TurnPagePic";
    String VideoFeedShow = "VideoFeedShow";
    String TurnPageVideo = "TurnPageVideo";
    String DetailPicShow = "DetailPicShow";

    String DetailVideoShow = "DetailVideoShow";
    String VideoPlaySuccessfully = "VideoPlaySuccessfully";
    String ListPicSave = "ListPicSave";
    String ListVideoSave = "ListVideoSave";
    String DialogLoginShow = "DialogLoginShow";

    String LoginSuccessfully = "LoginSuccessfully";
    String DetailPicCommentSuccessfully = "DetailPicCommentSuccessfully";
    String DetailVideoCommentSuccessfully = "DetailVideoCommentSuccessfully";
    String ListPicMore = "ListPicMore";

    String ListVideoMore = "ListVideoMore";

    String ShareSuccessfully = "ShareSuccessfully";


    //参数
    String tid = "tid";
    String action = "action";
    String source = "source";
    String index = "index";
    String target = "target";

    //参数选择
    String click = "click";
    String show = "show";//
    String detail = "detail";//详情页
    String feed = "feed";//feed流

    String dialog = "dialog";//dialog
    String personalCenter = "personalCenter";//个人中心

    String noInterested = "noInterested";
    String like = "like";
    String dislike = "dislike";

    //分享
    String fb = "fb";
    String twitter = "twitter";
    String whatsapp = "whatsapp";

    //动作
    String ACTION = "action";
    String REFRESH = "refresh";
    String LOADMORE = "loadmore";

    //广告
    String GG = "gg";
    String FB = "fb";
    String AD_ACTION = "Action";
    String AD_SDK = "sdk";
    String AD_REQUEST = "Ad_request";
    String AD_REQUEST_FAILED = "Ad_requestfailed";
    String AD_TIMEOUT = "Ad_timeout";
    String AD_IMP = "Ad_Imp";
    String AD_CLICK = "Ad_Click";
    String CTG = "ctg";
    String AD_CHACHE = "AdCache";
    String WRITE = "write";
    String READ_SUCCESSFULLY = "readSuccessfully";
}