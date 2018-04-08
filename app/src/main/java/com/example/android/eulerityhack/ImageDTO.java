package com.example.android.eulerityhack;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageDTO implements Serializable {

    public class ProxyBitmap implements Serializable {

        // this array will hold the pixels of the bitmap Image
        private int[] pixels;
        private int width;
        private int height;
        private String name;

        public ProxyBitmap(Bitmap bitmap){

            width = bitmap.getWidth();
            height = bitmap.getHeight();
            pixels = new int [width*height];
            bitmap.getPixels(pixels,0,width,0,0,width,height);
        }
    }

    private String url;
    private String name;
    private Date created;
    private Date updated;
    private ProxyBitmap image;

    public ImageDTO(String url, String created, String updated) throws ParseException {

        this.url = url;
        this.created = stringToDate(created);
        this.updated = stringToDate(updated);
        setName(url);
    }

    public static Date stringToDate(String strDate) throws ParseException {

        final String DATE_FORMAT = "MMM dd, YYYY h:m:s aaa";
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        Date date = formatter.parse(strDate);
        return date;
    }

    public void setName(String url) {

        String[] parts = url.split("/");
        String[] fileWithExt = parts[parts.length-1].split("\\.");
        name = fileWithExt[0];
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public Date getCreated() {
        return created;
    }

    public Date getUpdated() {
        return updated;
    }

    public Bitmap getImage() {
        return Bitmap.createBitmap(image.pixels, image.width, image.height, Bitmap.Config.ARGB_8888);
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public void setImage(Bitmap image) {
        this.image = new ProxyBitmap(image);
    }
}
