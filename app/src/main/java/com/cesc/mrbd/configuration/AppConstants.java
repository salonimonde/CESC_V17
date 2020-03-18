package com.cesc.mrbd.configuration;

/**
 * Created by Bynry01 on 22-08-2016.
 */
public interface AppConstants {

    /*String BASE_URL = "http://192.168.1.42:9008/api/";  //  Local
    String PROFILE_IMAGE_URL = "http://192.168.1.42:9008";  //   Local*/

    String BASE_URL = "http://159.89.165.67/api/";  // newest prod
    String PROFILE_IMAGE_URL = "http://159.89.165.67/";  //newest prod

    String REQUEST_LOGIN = "login";
    String URL_LOGIN = BASE_URL + REQUEST_LOGIN;

    String UPDATE_DEVICE_TOKEN = "update_fcm";
    String URL_UPDATE_DEVICE_TOKEN = BASE_URL + UPDATE_DEVICE_TOKEN;

    String REQUEST_GET_JOB_CARDS = "get_job_cards";
    String URL_GET_JOB_CARDS = BASE_URL + REQUEST_GET_JOB_CARDS + "?page=";

    String REQUEST_GET_DEASSIGNED_REASSIGNED_JOB_CARDS = "get_deassigned_reassigned_job_cards";
    String URL_GET_DEASSIGNED_REASSIGNED_JOB_CARDS = BASE_URL + REQUEST_GET_DEASSIGNED_REASSIGNED_JOB_CARDS + "?page=";

    String REQEST_UPLOAD_METER_READING = "upload_meter_reading";
    String URL_UPLOAD_METER_READING = BASE_URL + REQEST_UPLOAD_METER_READING;

    String REQEST_UPLOAD_UNBILL_METER_READING = "add_new_consumer";
    String URL_UPLOAD_UNBILL_METER_READING = BASE_URL + REQEST_UPLOAD_UNBILL_METER_READING;

    String MY_SCORE_DETAILS = "marks";
    String URL_MY_SCORE_DETAILS = BASE_URL + MY_SCORE_DETAILS;

    String REQUEST_GET_DEASSIGNED_REASSIGNED_BILLING_DETAILS = "get_bd_deassigned_reassigned_job_cards";
    String URL_GET_DEASSIGNED_REASSIGNED_BILLING_DETAILS = BASE_URL + REQUEST_GET_DEASSIGNED_REASSIGNED_BILLING_DETAILS;

    String REQUEST_USER_PROFILE_IMAGE = "update_user_profile_image";
    String URL_USER_PROFILE_IMAGE = BASE_URL + REQUEST_USER_PROFILE_IMAGE;

    String REQUEST_GET_BILLING_DETAILS = "get_billing_detail_job_cards";
    String URL_GET_BILLING_DETAILS = BASE_URL + REQUEST_GET_BILLING_DETAILS;

    String REQUEST_UPLOAD_BILLING_DISTRIBUTION = "upload_billing_distribution";
    String URL_UPLOAD_BILLING_DISTRIBUTION = BASE_URL + REQUEST_UPLOAD_BILLING_DISTRIBUTION;

    String REQUEST_GET_PAYMENT_DETAILS = "payment_calculation";
    String URL_GET_PAYMENT_DETAILS = BASE_URL + REQUEST_GET_PAYMENT_DETAILS;

    String REQUEST_GET_DISCONNECTION_DETAILS = "get_disconnection_job_cards";
    String URL_GET_DISCONNECTION_DETAILS = BASE_URL + REQUEST_GET_DISCONNECTION_DETAILS;

    String REQUEST_DEASSIGN_DISCONNECTION_DETAILS = "get_disconnection_deassigned_reassigned_job_cards";
    String URL_DEASSIGN_DISCONNECTION_DETAILS = BASE_URL + REQUEST_DEASSIGN_DISCONNECTION_DETAILS;

    String REQUEST_UPLOAD_DISCONNECTION_DETAILS = "update_disconnection_jobcard_status";
    String URL_UPLOAD_DISCONNECTION_DETAILS = BASE_URL + REQUEST_UPLOAD_DISCONNECTION_DETAILS;

    String CURRENT_JOB_CARD = "current_consumer";
    String JOB_CARD_STATUS_ALLOCATED = "ALLOCATED";
    String JOB_CARD_STATUS_COMPLETED = "COMPLETED";
    String BILL_CARD_STATUS_ALLOCATED = "Started";
    String DEVICE_FCM_TOKEN = "device_fcm_token";
    String CURRENT_BIll_CARD = "current_bill";
    String CURRENT_METER_READER_ID = "current_meter_reader_id";
    String CURRENT_CONSUMER_OBJ = "current_consumer_obj";
    String CONSUMER_OBJ = "consumer_obj";
    String BINDER_NO = "binder_no";
    String ZONE_CODE = "zone_code";


    String SHARED_PREF = "mAppNameHere";
    String PROFILE_IMAGE = "";
    String LANGUAGE_SELECTED = "english";
    String DESTINATION_LONG = "DESTINATION_LONG";
    String DESTINATION_LAT = "DESTINATION_LAT";
    String SCREEN_FROM_EXIT = "screen_from_exit";
    String FILTER_BINDER = "filter_binder";
    String FCM_KEY = "fcm_key";
    String TOTAL = "total";
    String METER_READER_ID = "";
    String DISCONNECTION_ADAPTER_VALUE = "";
    String PERCENTAGE = "0";

    long DEFAULT_SCANNING_TIME = 30 * 1000;
    int ALL_PERMISSIONS_RESULT = 107;
    int CAMERA_SUSPICIOUS_RESULT_CODE = 140;
    int CAMERA_RESULT_CODE = 141;
    int REQUEST_ID_MULTIPLE_PERMISSIONS = 12;
    int UPLOAD_COUNT = 5;
    int REQUEST_CHECK_SETTINGS = 10000;
    int UPLOAD_HISTORY_DATE_COUNT = 15;
    int UPLOAD_BILL_HISTORY_DATE_COUNT = 15;


}