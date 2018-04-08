package com.example.android.eulerityhack;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import java.util.List;

public class AppEntryPoint extends AppCompatActivity implements  AsyncResponse {

    private final String IMAGE_URL = "https://eulerity-hackathon.appspot.com/image";
    public static final int REQUEST_CODE = 123;
    public static final int RESULT_CODE = 456;
    private GridView gridView;
    private GridViewAdapter gridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_entry_point);
        fire();
    }

    private void fire() {

        AppRequest appRequest = new AppRequest();
        appRequest.delegate = this;
        appRequest.initImageList(this, IMAGE_URL);
    }

    @Override
    public void processFinish(final List<ImageDTO> images){

        gridView = (GridView) findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(this, images);
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(AppEntryPoint.this, ImageActivity.class);
                intent.putExtra("img", images.get(position).getImage());
                intent.putExtra("imageDTO", images.get(position));
                intent.putExtra("index", position);
                startActivity(intent);
//                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    @Override
    public void processFinish(String url) {

    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onFailure() {

    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if ((requestCode == REQUEST_CODE) && (resultCode == RESULT_CODE)) {

            ImageDTO imageDTO = (ImageDTO) data.getSerializableExtra("filtered");
            int index = data.getIntExtra("position", 0);
            gridAdapter.setItem(imageDTO, index);
            gridView.refreshDrawableState();
        }
    }*/
}
