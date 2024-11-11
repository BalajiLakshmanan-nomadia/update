package com.synchroteam.retrofit;

import com.synchroteam.retrofit.models.JobPoolService.JobPoolRequest;
import com.synchroteam.retrofit.models.JobPoolService.JobPoolResult;
import com.synchroteam.retrofit.models.JobPoolService.JobPoolScheduleRequest;
import com.synchroteam.retrofit.models.NotificationService.EventNotiRequest;
import com.synchroteam.retrofit.models.NotificationService.EventNotiResult;
import com.synchroteam.retrofit.models.mobileAuth.AuthReq;
import com.synchroteam.retrofit.models.mobileAuth.ChangePasswordModel;
import com.synchroteam.retrofit.models.mobileAuth.MobileAuth;
import com.synchroteam.retrofit.models.paymentservice.PaymentServiceModel;
import com.synchroteam.retrofit.models.paymentservice.PaymentServiceModelBefore;
import com.synchroteam.retrofit.models.syncservice.SyncService;
import com.synchroteam.utils.Commons;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Trident.
 */
public interface ApiInterface {

    @GET(Commons.SYNCSERVER)
    Call<SyncService> synchronizeServer(@Query("domain") String domain);

    @FormUrlEncoded
    @POST
    Call<PaymentServiceModel> paymentService(@Url String url, @Field("token") String token, @Field("idCustomer") int idCustomer,
                                             @Field("amount") int amount, @Field("currency") String currency,
                                             @Field("ccToken") String ccToken, @Field("ccEmail") String ccEmail,
                                             @Field("idInvoice") String idInvoice, @Field("idRemoteInvoice") String idRemoteInvoice);


//    @FormUrlEncoded
//    @Headers({
//            "Content-Type: application/json",
//            "User-Agent: StWorkerHeader"
//    })
//    @POST
//    Call<EventNotiResult> notificationEventService(@Url String url, @Field("IdCustomer") int idCustomer,
//                                                   @Field("IdJob") String idJob,
//                                                   @Field("JobStatus") int jobStatus, @Field("EventOrigin") String eventOrigin,
//                                                   @Field("timestamp") String timeStamp);


    @Headers({
            "Content-Type: application/json",
            "User-Agent: StWorkerHeader"
    })
    @POST
    Call<EventNotiResult> notificationEventService(@Url String url, @Body EventNotiRequest eventNotiRequest);


    @Headers({
            "Content-Type: application/json",
            "User-Agent: StWorkerHeader"
    })
    @POST
    Call<JobPoolResult> scheduleReserveService(@Url String url, @Body JobPoolRequest jobPoolRequest);

    @Headers({
            "Content-Type: application/json",
            "User-Agent: StWorkerHeader"
    })
    @POST
    Call<JobPoolResult> scheduleJobPoolService(@Url String url, @Body JobPoolScheduleRequest jobPoolRequest);

    @Headers({
            "Content-Type: application/json",
            "User-Agent: StWorkerHeader"
    })
    @POST
    Call<MobileAuth> getMobAuth(@Url String url, @Body AuthReq authReq);
    @Headers({
            "Content-Type: application/json",
            "User-Agent: StWorkerHeader"
    })
    @POST
    Call<MobileAuth> getChangePassword(@Url String url, @Body ChangePasswordModel authReq);


    @FormUrlEncoded
    @POST
    Call<PaymentServiceModelBefore> paymentServiceBefore(@Url String url, @Field("token") String token, @Field("idCustomer") int idCustomer,
                                                         @Field("amount") int amount, @Field("currency") String currency,
                                                         @Field("ccToken") String ccToken, @Field("ccEmail") String ccEmail,
                                                         @Field("idInvoice") String idInvoice, @Field("idRemoteInvoice") String idRemoteInvoice, @Field("addPaymentIntent") String addPaymentIntent);

    @FormUrlEncoded
    @POST
    Call<PaymentServiceModel> paymentServiceAfter(@Url String url,  @Field("idCustomer") int idCustomer,
                                                  @Field("amount") int amount,
                                                  @Field("ccEmail") String ccEmail,
                                                  @Field("idInvoice") String idInvoice,
                                                  @Field("idRemoteInvoice") String idRemoteInvoice,
                                                  @Field("paymentIntentId") String paymentIntentId);
}