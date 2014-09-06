package com.hansihe.texdroid;

import android.os.Parcel;
import android.os.Parcelable;

public class TexEquation implements Parcelable {

    private String markup;

    public TexEquation(String markup) {
        this.markup = markup;
    }

    public String getMarkup() {
        return markup;
    }

    // Parcel stuff below this line

    public int describeContents() {
        return 0;
    }

    public static TexEquation readFromParcel(Parcel in) {
        return new TexEquation(in.readString());
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(markup);
    }

    public static final Parcelable.Creator<TexEquation> CREATOR
            = new Parcelable.Creator<TexEquation>() {

        @Override
        public TexEquation createFromParcel(Parcel parcel) {
            return readFromParcel(parcel);
        }

        @Override
        public TexEquation[] newArray(int size) {
            return new TexEquation[size];
        }
    };

}
