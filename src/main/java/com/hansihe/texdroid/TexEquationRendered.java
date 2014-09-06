package com.hansihe.texdroid;

import android.os.Parcel;
import android.os.Parcelable;

public class TexEquationRendered implements Parcelable {

    private String svg;
    private int width;
    private int height;

    public TexEquationRendered(String svg, int width, int height) {
        this.svg = svg;
        this.width = width;
        this.height = height;
    }

    public String getSvg() {
        return svg;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getAspectRatio() {
        return (float)width / (float)height;
    }

    // Parcel stuff below this line

    public int describeContents() {
        return 0;
    }

    public static TexEquationRendered readFromParcel(Parcel in) {
        return new TexEquationRendered(in.readString(), in.readInt(), in.readInt());
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(svg);
        out.writeInt(width);
        out.writeInt(height);
    }

    public static final Parcelable.Creator<TexEquationRendered> CREATOR
            = new Parcelable.Creator<TexEquationRendered>() {

        @Override
        public TexEquationRendered createFromParcel(Parcel parcel) {
            return readFromParcel(parcel);
        }

        @Override
        public TexEquationRendered[] newArray(int size) {
            return new TexEquationRendered[size];
        }
    };

}
