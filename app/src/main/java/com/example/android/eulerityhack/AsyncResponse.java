package com.example.android.eulerityhack;

import java.util.List;

public interface AsyncResponse {
    void processFinish(List<ImageDTO> images);
    void processFinish(String url);
    void onSuccess();
    void onFailure();
}
