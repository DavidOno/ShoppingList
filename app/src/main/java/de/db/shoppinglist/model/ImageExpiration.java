package de.db.shoppinglist.model;

import java.text.SimpleDateFormat;

public class ImageExpiration {

    private final static String pattern = "yyyy-MM-dd";
    public final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

    private String date;
    private String downloadUri;

    /**
     * Firebase requires an emptry constructor.
     */
    public ImageExpiration(){
        //empty construtor
    }

    public ImageExpiration(String date, String downloadUri) {
        this.date = date;
        this.downloadUri = downloadUri;
    }

    public String getDate() {
        return date;
    }

    public String getDownloadUri() {
        return downloadUri;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
