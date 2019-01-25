package com.godavarisandroid.mystore.webUtils;

public class WebServices {
    //    public static String BASE_URL = "http://srestates.in/STORE/ws/ws_store.php";

    private static String DEV_BASE_URL = "https://milkcart.xyz/API/ws/ws_store.php";
    private static String PROD_BASE_URL = "https://www.milkcart.co.in/API/ws/ws_store.php";
    private static String TEST_BASE_URL = "http://milkcart.co.in/Test/ws/ws_store.php";
    public static String PAYTM_CHECKHASH_BASE_URL = "https://milkcart.co.in/Paymentgateway/generateChecksum.php";

    public static String BASE_URL = TEST_BASE_URL;

    public static String URL_REQUEST_FOR_OTP = BASE_URL + "action=request_for_otp";
    public static String URL_OTP_VALIDATION = BASE_URL + "action=otp_validation";
    public static String URL_USER_VALIDATION = BASE_URL + "action=user_validation";
    public static String URL_CITIES = BASE_URL + "action=cities";
    public static String URL_AREAS = BASE_URL + "action=citys";
    public static String URL_APARTMENTS = BASE_URL + "action=apartments";
    public static String URL_REGISTRATION = BASE_URL + "action=registration";
    public static String URL_UPDATE_ADDRESS = BASE_URL + "action=edit_address";
    public static String URL_CATEGORIES = BASE_URL + "action=categorys";
    public static String URL_BRANDS = BASE_URL + "action=brands";
    public static String URL_PRODUCTS = BASE_URL + "action=products";
    public static String URL_SUBSCRIPTION = BASE_URL + "action=subscription";
    public static String URL_ACTIVE_SUBSCRIPTION = BASE_URL + "action=active_subsctiptions";
    public static String URL_PAUSE_SUBSCRIPTION = BASE_URL + "action=pass_subscription";
    public static String URL_RESUME_SUBSCRIPTION = BASE_URL + "action=resume_subscription";
    public static String URL_INACTIVE_SUBSCRIPTION = BASE_URL + "action=delete_subscription";
    public static String URL_MODIFY_SUBSCRIPTION = BASE_URL + "action=modify_subscription";
    public static String URL_NEXT_DAY_DELIVERY = BASE_URL + "action=next_delivery";
    public static String URL_MODIFY_NEXT_DELIVERY = BASE_URL + "action=next_delivery_modify";
    public static String URL_MY_CURRENT_CALENDAR = BASE_URL + "action=calender_dates";
    public static String URL_DELIVERY_HISTORY = BASE_URL + "action=delivery_history";
    public static String URL_FEEDBACK_QUESTIONS = BASE_URL + "action=feedback_questions";
    public static String URL_FEEDBACK = BASE_URL + "action=feedback";
    public static String URL_WALLET = BASE_URL + "action=wallet_amount";
    public static String URL_ADS = BASE_URL + "action=ads";
    public static String URL_OFFERS = BASE_URL + "action=offers";
}
