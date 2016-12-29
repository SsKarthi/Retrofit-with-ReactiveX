package com.example.karthi.retrofit2_example.Helper;


import com.example.karthi.retrofit2_example.Response.BasicResponse;
import com.example.karthi.retrofit2_example.Response.GitHubUserResponse;
import com.example.karthi.retrofit2_example.Response.GitUserDetails;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface ApiInterface {

    @GET("volleyExample/film.php")
    Call<BasicResponse> getData();

    @GET("octocat/repos")
    Observable<List<GitHubUserResponse>> getGitHubUserData();

    @GET("{username}")
    Observable<GitUserDetails> getGitUserDetails(@Path("username") String username);

}
