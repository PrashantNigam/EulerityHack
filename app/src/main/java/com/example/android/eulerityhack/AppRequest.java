package com.example.android.eulerityhack;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;


public class AppRequest {

    private final String TAG = AppRequest.class.getName();
    private final String URL_KEY = "url";
    private final String CREATED_KEY = "created";
    private final String UPDATED_KEY = "updated";
    public AsyncResponse delegate;

    public AppRequest() {
        super();
    }

    public void initImageList(Context context, String url) {

        StringRequest jsonRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    delegate.processFinish(getImages(response));
                } catch (JSONException | ParseException | IOException e) {
                    Log.i(TAG, e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
            }
        });

        Volley.newRequestQueue(context).add(jsonRequest);
    }

    public List<ImageDTO> getImages(String jsonString) throws JSONException, ParseException, IOException {

        List<ImageDTO> images = new ArrayList<>();
        JSONArray imageArr = new JSONArray(jsonString);
        for (int i = 0; i < imageArr.length(); i++) {

            JSONObject imageJson = imageArr.getJSONObject(i);
            ImageDTO image = new ImageDTO(imageJson.getString(URL_KEY),
                    imageJson.getString(CREATED_KEY),
                    imageJson.getString(UPDATED_KEY));
            images.add(image);
        }
        return images;
    }

    public void getUploadURL(Context context, String apiURL) {

        StringRequest jsonRequest = new StringRequest(Request.Method.GET, apiURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    delegate.processFinish(getURLFromJSON(response));
                } catch (JSONException e) {
                    Log.i(TAG, e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
            }
        });

        Volley.newRequestQueue(context).add(jsonRequest);
    }

    private String getURLFromJSON(String json) throws JSONException {

        JSONObject jsonObject = new JSONObject(json);
        return (String) jsonObject.get(URL_KEY);
    }

    public void createAndExecuteMultiPartRequest(final File filteredFile, final ImageDTO imageDTO, final String uploadURL) throws IOException {

        new Thread(new Runnable() {

            @Override
            public void run() {

                RequestBody requestBody = new MultipartBody.Builder()
                        .addFormDataPart("appid", "pn745@nyu.edu")
                        .addFormDataPart("original", imageDTO.getUrl())
                        .addFormDataPart("image", imageDTO.getName(),
                                RequestBody.create(MediaType.parse("image/jpeg"), filteredFile))
                        .build();
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(uploadURL)
                        .post(requestBody)
                        .build();
                OkHttpClient client = new OkHttpClient();
                try (okhttp3.Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "Success");
                        delegate.onSuccess();
                    } else {
                        Log.i(TAG, "Failure");
                        delegate.onFailure();
                    }
                } catch (IOException e) {
                    Log.i(TAG, e.getMessage());
                }
            }
        }).start();
    }
}
