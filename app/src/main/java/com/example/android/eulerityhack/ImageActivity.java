package com.example.android.eulerityhack;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.zomato.photofilters.imageprocessors.Filter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import static android.view.View.OnClickListener;

public class ImageActivity extends AppCompatActivity implements ThumbnailCallback, AsyncResponse {

    private final String UPLOAD_URL = "https://eulerity-hackathon.appspot.com/upload";
    private final String TAG = ImageActivity.class.getName();

    static {
        System.loadLibrary("NativeImageProcessor");
    }

    private Activity activity;
    private RecyclerView thumbListView;
    private ImageView placeHolderImageView;
    private EditText captionText;
    private Bitmap drawable;
    private Bitmap filteredImage;
    private ImageDTO imageDTO;
    private int imgIndex;
    private AppRequest appRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_activity);
        getSupportActionBar().setTitle("Filters");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity = this;
        drawable = (Bitmap) getIntent().getParcelableExtra("img");
        imageDTO = (ImageDTO) getIntent().getSerializableExtra("imageDTO");
        imgIndex = getIntent().getIntExtra("index", 0);
        filteredImage = drawable;
        initUIWidgets();
    }

    private void initUIWidgets() {

        thumbListView = findViewById(R.id.thumbnails);
        placeHolderImageView = findViewById(R.id.place_holder_imageview);
        placeHolderImageView.setImageBitmap(Bitmap.createScaledBitmap(drawable, 640, 640, false));
        captionText = findViewById(R.id.caption);
        initHorizontalList();
        initSaveButton();
        initCaptionButton();
    }

    private void initSaveButton() {

        Button save = findViewById(R.id.save);
        save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (filteredImage != null) {

                    processCaption();
                    fireForUploadURL();
                    /*imageDTO.setImage(filteredImage);
                    imageDTO.setUpdated(new Date());
                    Intent intent = new Intent();
                    intent.putExtra("filtered", imageDTO);
                    intent.putExtra("index", imgIndex);
                    setResult(AppEntryPoint.RESULT_CODE, intent);*/
                    captionText.setText(null);
                    captionText.setVisibility(View.GONE);
                }
            }
        });
    }

    private void processCaption() {

        String caption = captionText.getText().toString();
        if (caption != null) {

            Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
            paintText.setColor(Color.CYAN);
            paintText.setTextSize(35);
            paintText.setStyle(Paint.Style.FILL);
            paintText.setShadowLayer(10F, 10F, 10F, Color.BLACK);
            Rect rectText = new Rect();
            paintText.getTextBounds(caption, 0, caption.length(), rectText);
            paintText.breakText(caption, true, filteredImage.getWidth(), null);

            filteredImage = getMutableBitmap(filteredImage);
            Canvas canvas = new Canvas(filteredImage);
            canvas.drawText(caption, 0, rectText.height(), paintText);
        }
    }

    private Bitmap getMutableBitmap(Bitmap immutable) {
        return immutable.copy(Bitmap.Config.ARGB_8888, true);
    }


    private void initCaptionButton() {

        Button putCaption = findViewById(R.id.putCaption);
        putCaption.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                captionText.setVisibility(View.VISIBLE);
            }
        });
    }

    private void fireForUploadURL() {

        appRequest = new AppRequest();
        appRequest.delegate = ImageActivity.this;
        appRequest.getUploadURL(ImageActivity.this, UPLOAD_URL);
    }

    @Override
    public void processFinish(String url) {

        File filteredFile = null;
        try {
            filteredFile = bitmapToFile(filteredImage);
        } catch (IOException e) {
            Log.i(TAG, e.getMessage());
        }

        try {
            appRequest.createAndExecuteMultiPartRequest(filteredFile, imageDTO, url);
        } catch (IOException e) {
            Log.i(TAG, e.getMessage());
        }
    }

    @Override
    public void onSuccess() {

        Snackbar.make(findViewById(android.R.id.content), "Upload Successful!", Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.GREEN)
                .show();
    }

    @Override
    public void onFailure() {

        Snackbar.make(findViewById(android.R.id.content), "Upload Failed!", Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.RED)
                .show();
    }

    private File bitmapToFile(Bitmap bitmap) throws IOException {

        File file = new File(getCacheDir(), imageDTO.getName() + ".jpeg");
        OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
        os.close();
        return file;
    }


    private void initHorizontalList() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager.scrollToPosition(0);
        thumbListView.setLayoutManager(layoutManager);
        thumbListView.setHasFixedSize(true);
        bindDataToAdapter();
    }

    private void bindDataToAdapter() {

        final Context context = this.getApplication();
        Handler handler = new Handler();
        Runnable r = new Runnable() {

            public void run() {

                Bitmap thumbImage = Bitmap.createScaledBitmap(drawable, 640, 640, false);
                ThumbnailsManager.clearThumbs();
                List<Filter> filters = FilterPack.getFilterPack(getApplicationContext());

                for (Filter filter : filters) {

                    ThumbnailItem thumbnailItem = new ThumbnailItem();
                    thumbnailItem.image = thumbImage;
                    thumbnailItem.filter = filter;
                    ThumbnailsManager.addThumb(thumbnailItem);
                }

                List<ThumbnailItem> thumbs = ThumbnailsManager.processThumbs(context);

                ThumbnailsAdapter adapter = new ThumbnailsAdapter(thumbs, (ThumbnailCallback) activity);
                thumbListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        };
        handler.post(r);
    }

    @Override
    public void onThumbnailClick(Filter filter) {

        filteredImage = Bitmap.createScaledBitmap(drawable, 640, 640, false);
        placeHolderImageView.setImageBitmap(filter.processFilter(filteredImage));
    }

    @Override
    public void processFinish(List<ImageDTO> images) {}
}
