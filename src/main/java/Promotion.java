//import java.util.Objects;
//package com.spotanote;

public class Promotion {

    private final int id;
    private String promotionName;
    private String promotionDescription;
    private String promotionSong;
    private String recordName;


    public Promotion(int id, String promotionName, String promotionDescription, String promotionSong, String recordName){
        this.id = id;
        this.promotionName = promotionName;
        this.promotionDescription = promotionDescription;
        this.promotionSong = promotionSong;
        this.recordName = recordName;
    }

    public int getId(){
        return id;
    }

    public String getPromotionName(){
        return promotionName;
    }

    public String getPromotionDescription(){
        return promotionDescription;
    }

    public String getPromotionSong(){
        return promotionSong;
    }

    public String getRecordName(){
        return recordName;
    }
}
