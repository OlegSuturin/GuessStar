package com.example.guessstar;

import android.graphics.Bitmap;

public class Star {
    private String name;
    private Bitmap bitmap;
    private String addressImg;

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Star(String name, String addressImg) {
        this.name = name;
        this.addressImg = addressImg;
    }

    public String getName() {
        return name;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getAddressImg() {
        return addressImg;
    }
}
