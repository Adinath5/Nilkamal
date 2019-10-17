package com.atharvainfo.nilkamal.Others;


import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {


    @FormUrlEncoded
    @POST("api.php?apicall=addUserLeave")
        // @POST("getuserlogin.php")
    Call<ServerResponse> leavapp(@Field("vdate") String vdate,
                                 @Field("fromdate") String fromdate,
                                 @Field("todate") String todate,
                                 @Field("leavreason") String leavreason,
                                 @Field("noofdays") String noofdays);
    //  @Field("noofdays") String noofdays);


}
