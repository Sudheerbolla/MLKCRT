package com.godavarisandroid.mystore.utils;

public class Constants {
    public static final boolean isLive = true;

    //staging credentials
    private static final String merchantKeyStaging = "MrY2LcMZ3MgiJ_kF";
    private static final String midStaging = "DuckHa49923302193796";
    private static final String channelNameStagingMobile = "APPSTAGING";
    private static final String userEmailStaging = "username@emailprovider.com";
    private static final String userMobileStaging = "7777777777";
    private static final String industryTypeIdStaging = "Retail";
    private static final String CALL_BACK_URL_STAGING = "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=";

    //live credentials
    private static final String merchantKeyLive = "3XoruE8jGZH16llk";
    private static final String midLive = "DuckHa89382440353111";
    private static final String channelNameLiveMobile = "APPPROD";
    private static final String userEmailLive = "jitender.agarwal@gmail.com";
    private static final String userMobileLive = "9966238282";
    private static final String industryTypeIdLive = "Retail109";
    private static final String CALL_BACK_URL_LIVE = "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=";
    //private static final String CALL_BACK_URL_LIVE = "http://10.0.131.34/xampp/PaytmKit/Paytm_Web/Paytm_Web/PaytmKit/pgResponse.php";

    public static final String PAYTM_MID = isLive ? midLive : midStaging;
    public static final String PAYTM_CHANNEL_NAME_MOBILE = isLive ? channelNameLiveMobile : channelNameStagingMobile;
    public static final String PAYTM_MERCHANT_KEY = isLive ? merchantKeyLive : merchantKeyStaging;
    public static final String PAYTM_USER_EMAIL = isLive ? userEmailLive : userEmailStaging;
    public static final String PAYTM_USER_MOBILE = isLive ? userMobileLive : userMobileStaging;
    public static final String PAYTM_INDUSTRY_TYPE_ID = isLive ? industryTypeIdLive : industryTypeIdStaging;
    public static final String PAYTM_CALL_BACK_URL = isLive ? CALL_BACK_URL_LIVE : CALL_BACK_URL_STAGING;
    public static final String PAYTM_CHANNEL_ID_MOBILE = "WAP";
    public static final String PAYTM_USER_PREFIX = "MILKCARTCUST0";

/*

test wallet details:
mob:7777777777
pw:Paytm12345
otp:489871


Merchant Panel Login

Link

Password

Username

URL

Sent to email

6302971295




 •  Transaction URL : https://securegw-stage.paytm.in/theia/processTransaction
     •  Transaction Status URL :https://securegw-stage.paytm.in/merchant-status/getTxnStatus
*/
}
