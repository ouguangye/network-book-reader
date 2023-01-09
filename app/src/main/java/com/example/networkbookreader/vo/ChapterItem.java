package com.example.networkbookreader.vo;

import android.os.Parcel;
import android.os.Parcelable;

public class ChapterItem implements Parcelable {
    private String href;
    private String name;

    public ChapterItem(String href, String name) {
        this.href = href;
        this.name = name;
    }

    protected ChapterItem(Parcel in) {
        href = in.readString();
        name = in.readString();
    }

    public static final Creator<ChapterItem> CREATOR = new Creator<ChapterItem>() {
        @Override
        public ChapterItem createFromParcel(Parcel in) {
            return new ChapterItem(in);
        }

        @Override
        public ChapterItem[] newArray(int size) {
            return new ChapterItem[size];
        }
    };

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(href);
        parcel.writeString(name);
    }
}
