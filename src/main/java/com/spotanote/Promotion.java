package com.spotanote;
//import java.util.Objects;

public class Promotion {

    private final int id;
    private String promotionName;
    private String promotionDescription;
    private String promotionSong;
    private String recordName;

    //database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/SpotANote";
    private static final String DB_USER = "spotanote_user";
    private static final String DB_PASSWORD = "password";


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


    public Boolean uploadPromotion() {
        // Implement the logic to upload the promotion to the database
        // This is a placeholder implementation, you should replace it with actual database interaction code
        return true; // Return true if the upload was successful, false otherwise
    }

    public Boolean insideDatabase() {
        // Implement the logic to check if the promotion is inside the database
        // This is a placeholder implementation, you should replace it with actual database interaction code
        return true; // Return true if the promotion is found in the database, false otherwise
    }


}
