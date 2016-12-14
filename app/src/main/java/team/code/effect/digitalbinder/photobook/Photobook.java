package team.code.effect.digitalbinder.photobook;

import android.os.Parcel;
import android.os.Parcelable;

public class Photobook{
    private int photobook_id;
    private String title;
    private String filename;
    private int color;
    private String regdate;

    public int getPhotobook_id() {
        return photobook_id;
    }
    public void setPhotobook_id(int photobook_id) {
        this.photobook_id = photobook_id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }
    public int getColor() {
        return color;
    }
    public void setColor(int color) {
        this.color = color;
    }
    public String getRegdate() {
        return regdate;
    }
    public void setRegdate(String regdate) {
        this.regdate = regdate;
    }
}
