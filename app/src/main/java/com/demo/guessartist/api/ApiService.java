package com.demo.guessartist.api;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface ApiService {

    @GET("#1")
    Observable<String> getContent();

}
