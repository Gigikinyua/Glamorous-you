package com.jemimah.glamorous_you.retrofit;

import com.jemimah.glamorous_you.model.Appointment;
import com.jemimah.glamorous_you.model.Business;
import com.jemimah.glamorous_you.model.County;
import com.jemimah.glamorous_you.model.Service;
import com.jemimah.glamorous_you.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiClient {

    @POST("register_user")
    @FormUrlEncoded
    Call<User> registerUser(@Field("first_name") String mobile, @Field("surname") String surname,
                            @Field("phone") String phone, @Field("email") String email,
                            @Field("password") String password);

    @POST("login")
    @FormUrlEncoded
    Call<User> login(@Field("email") String email, @Field("password") String password);

    @GET("get_services")
    Call<List<Service>> getServices();

    @GET("get_businesses/{category}")
    Call<List<Business>> getBusinesses(@Path("category") Integer category);

    @POST("book_appointment")
    @FormUrlEncoded
    Call<List<Appointment>> bookAppointment(@Field("userID") int userID, @Field("business_id") int business_id,
                                      @Field("services[]") List<Long> services,
                                      @Field("no_of_adults") int no_of_adults,
                                      @Field("no_of_children") int no_of_children,
                                      @Field("appointment_date") String appointment_date,
                                      @Field("appointment_time") String appointment_time,
                                      @Field("payment_type") String payment_type);

    @GET("get_appointments/{userID}")
    Call<List<Appointment>> getAppointments(@Path("userID") Integer userID);

    @GET("get_business_history/{businessID}")
    Call<List<Appointment>> getBusinessHistory(@Path("businessID") Integer businessID);

    @GET("get_counties")
    Call<County[]> getCounties();

    @GET("get_my_businesses/{userID}")
    Call<List<Business>> getMyBusinesses(@Path("userID") Integer userID);

    @POST("register_business")
    @FormUrlEncoded
    Call<Business> registerBusiness(@Field("name") String name, @Field("service_id") int service_id,
                                            @Field("registered_by") int registered_by,
                                            @Field("county") int county,
                                            @Field("sub_county") int sub_county);

    @POST("save_status")
    @FormUrlEncoded
    Call<Appointment> saveStatus(@Field("appointmentId") int id, @Field("status") String status);
}