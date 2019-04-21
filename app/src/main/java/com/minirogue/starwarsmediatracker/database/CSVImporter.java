package com.minirogue.starwarsmediatracker.database;

import android.content.Context;

import com.minirogue.starwarsmediatracker.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CSVImporter {

    public static void importCSVToDatabase(Context ctx){
        InputStream inputStream = ctx.getResources().openRawResource(R.raw.starwarsmediadb);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try{
            MediaDatabase db = MediaDatabase.getMediaDataBase(ctx);
            MediaItem newItem;
            String[] header = reader.readLine().split(",");
            String csvLine;
            while ((csvLine = reader.readLine()) != null){
                String[] row = csvLine.split(",");//TODO this does not handle cases where the entry contains a comma
                newItem = new MediaItem();
                for (int i = 0; i< header.length; i++){
                    switch (header[i]) {
                        case "ID":
                            newItem.setMediaID(Integer.valueOf(row[i]));
                            break;
                        case "title":
                            newItem.setTitle(row[i]);
                            break;
                        case "type":
                            newItem.setType(convertType(row[i]));
                            break;
                        case "description":
                            newItem.setDescription(row[i]);
                            break;
                        default:
                            System.out.println("Unused header: "+header[i]);
                    }
                    if (db.daoAccess().getMediaItemById(newItem.getMediaID()) == null){
                        db.daoAccess().insertSingleMediaItem(newItem);
                    }
                    else{
                        db.daoAccess().updateMedia(newItem);
                    }
                }
            }
        }
        catch (IOException ex){
            throw new RuntimeException("Error reading CSV file: "+ex);
        }
        finally{
            try{
                inputStream.close();
            }
            catch(IOException e){
                throw new RuntimeException("Error while closing input stream from CSV file: "+e);
            }
        }
    }

    private static int convertType(String csvType){
        switch (csvType){
            case "Movie":
                return MediaItem.MEDIATYPE_MOVIE;
            case "Book":
                return MediaItem.MEDIATYPE_BOOK;
            default:
                return 0;
        }
    }

}
