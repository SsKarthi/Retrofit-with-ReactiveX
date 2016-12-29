package com.example.karthi.retrofit2_example;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.karthi.retrofit2_example.Adaper.ListviewAdapter;
import com.example.karthi.retrofit2_example.Helper.ApiClient;
import com.example.karthi.retrofit2_example.Helper.ApiInterface;
import com.example.karthi.retrofit2_example.Response.CombinedResults;
import com.example.karthi.retrofit2_example.Response.GitHubUserResponse;
import com.example.karthi.retrofit2_example.Response.GitUserDetails;
import com.example.karthi.retrofit2_example.Response.JSONResponses;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    ListView list_view;
    ListviewAdapter adapter;
    public static ArrayList<JSONResponses> data;
    ProgressBar loading;
    Subscription subscribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list_view = (ListView) findViewById(R.id.listView);
        loading = (ProgressBar) findViewById(R.id.loading);

        getFromGitHub();

    }


    public void getFromGitHub() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        ApiInterface apiService1 = ApiClient.getClient().create(ApiInterface.class);

        Observable<List<GitHubUserResponse>> GitHub = apiService.getGitHubUserData();
        Observable<GitUserDetails> GitHub1 = apiService1.getGitUserDetails("SsKarthi").first();


        subscribe = Observable.zip(GitHub, GitHub1, (first, second) -> {
            return new CombinedResults(first, second);
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CombinedResults>() {
                    @Override
                    public void onCompleted() {
                        subscribe.unsubscribe();

                    }

                    @Override
                    public void onError(Throwable e) {
                        Snackbar.make(findViewById(R.id.root_layout), "You are not connected to the internet.", Snackbar.LENGTH_LONG).show();
                        loading.setVisibility(View.INVISIBLE);
                    }


                    @Override
                    public void onNext(CombinedResults combinedResults) {

                        if (combinedResults.first.size() > 0) {
                            list_view.setVisibility(View.VISIBLE);
                            loading.setVisibility(View.INVISIBLE);
                            adapter = new ListviewAdapter(MainActivity.this, combinedResults.first);
                            list_view.setAdapter(adapter);
                            Log.d("Response", combinedResults.first.size() + new Gson().toJson(combinedResults.first));
                        }

                        findViewById(R.id.lnrview).setVisibility(View.VISIBLE);
                        findViewById(R.id.loading1).setVisibility(View.INVISIBLE);
                        ((TextView) findViewById(R.id.textView1)).setText(combinedResults.second.getName());
                        ((TextView) findViewById(R.id.textView2)).setText(combinedResults.second.getCompany());
                        ((TextView) findViewById(R.id.textView3)).setText(combinedResults.second.getEmail());
                        ((TextView) findViewById(R.id.textView4)).setText(combinedResults.second.getLocation());
                        Log.d("Response1", new Gson().toJson(combinedResults.second));
                    }
                });


        Observable<CombinedResults> combinedLatest
                = Observable.combineLatest(GitHub, GitHub1, new Func2<List<GitHubUserResponse>, GitUserDetails, CombinedResults>() {
            @Override
            public CombinedResults call(List<GitHubUserResponse> GitHub, GitUserDetails GitHub1) {
                // Do something with the results of both threads
                return new CombinedResults(GitHub, GitHub1);
            }
        });


    }



}
