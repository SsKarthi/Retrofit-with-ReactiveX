package com.example.karthi.retrofit2_example.Response;

import java.util.List;

/**
 * Created by Karthik on 12/28/2016.
 */

public class CombinedResults {
    public List<GitHubUserResponse> first;
    public GitUserDetails second;

    public CombinedResults(List<GitHubUserResponse> first, GitUserDetails second) {
        this.first = first;
        this.second = second;
    }


}

