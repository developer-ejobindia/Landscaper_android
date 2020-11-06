package com.seazoned.landscaper.service.api;

/**
 * Created by root on 3/2/18.
 */

public class Api {
    //public static String sHost="http://192.168.1.2:8080/dev/seazonedapp/public/api";
    //public static String sHost="http://103.230.103.142:8080/dev/seazonedapp/public/api";

    public static String sHost = "http://seazoned.com/api";

    public static String sUserLogin=sHost+"/authenticate?";//username=svkde@gmail.com&password=1234";
    public static String sServiceRequest=sHost+"/service-request";//token
    public static String sServicePending=sHost+"/service-pending";//token
    public static String sAcceptOrDeclineRequest=sHost+"/accept-reject-service";//book_service_id=3&status=1// status 1 for accept and status -1 is for decline
    public static String sUserInfo=sHost+"/userinfo";//token
    public static String sUserInfoEdit=sHost+"/landscaper-edit";//first_name=test&last_name=landscaper&tel=9874563210&address=kolkata
    public static String sServiceList=sHost+"/service-list";
    public static String sAddProfileImage=sHost+"/change-profile-picture?";
    public static String sAddFeatureImage=sHost+"/change-landscaper-feature-picture?";
    public static String sFaceBookLogin=sHost+"/landscaper-fb-login?";
    public static String sUserRegistration=sHost+"/landscaper-registration?";
    public static String sGooglePlusLogin=sHost+"/landscaper-google-login?";
    public static String sChangePassword=sHost+"/change-password";
    public static String sAddService=sHost+"/add-lanscaper-details";
    public static String sLandscaperServiceList=sHost+"/view-added-services";
    public static String sLandscaperDeleteService=sHost+"/delete-added-services";
    public static String sAddProviderDetails=sHost+"/add-provider-details";
    public static String sEditProviderDetails=sHost+"/edit-service-hours-and-others";
    public static String sViewServiceDetails=sHost+"/view-service";
    public static String sEditService=sHost+"/edit-service";
    public static String sViewServiceHours=sHost+"/view-service-hours-and-others";
    public static String sAddPaypalAccount=sHost+"/add-paypal-account";
    public static String sViewPaypalAccount=sHost+"/view-paypal-account";
    public static String sRemovePaypalAccount=sHost+"/delete-paypal-account";
    public static String sBookingHistory=sHost+"/landscaper-booking-history";
    public static String sBookingHistoryDetails=sHost+"/booking-history-details-landscaper";
    public static String sViewTransactionHistory=sHost+"/view-transaction-history";
    public static String sSaveDeviceToken=sHost+"/subscribe";
    public static String sEndJobLandscaper=sHost+"/end-job-landscaper";
    public static String sCompleteJobImage=sHost+"/upload-landscaper-images?";
    public static String sContactUs=sHost+"/contact-us-landscaper";
    public static String sNotificationList=sHost+"/notification-list-landscaper";
    public static String sChatList=sHost+"/landscaper-chat-list";
    public static String sFaq=sHost+"/get_faq";

    public static String sForgotPass=sHost+"/emailCheck";
    public static String sOTP=sHost+"/otpCheck";
    public static String sNewPass=sHost+"/new-password";

    public static String sGetNotificationStatus=sHost+"/get-notification-status";
    public static String sUpdateLicense=sHost+"/change-landscaper-driver-image";
}
