package com.minirogue.starwarsmediatracker.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.minirogue.starwarsmediatracker.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;

public class CSVImporter extends AsyncTask<Integer, Void, Void> {

    public static final int SOURCE_ONLINE = 1;
    public static final int SOURCE_RAW_RESOURCES = 2;
    private WeakReference<Context> ctxRef;


    public CSVImporter(Context ctx){
        ctxRef = new WeakReference<>(ctx.getApplicationContext());
    }

    private void importCSVToMediaDatabase(InputStream inputStream){
        Context ctx = ctxRef.get();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            MediaDatabase db = MediaDatabase.getMediaDataBase(ctx);
            MediaItem newItem;
            String[] header = reader.readLine().split(",");
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",");//TODO this does not handle cases where the entry contains a comma
                newItem = new MediaItem();
                for (int i = 0; i < row.length; i++) {
                    switch (header[i]) {
                        case "ID":
                            newItem.setId(Integer.valueOf(row[i]));
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
                            System.out.println("Unused header: " + header[i]);
                    }
                    if (db.getDaoMedia().getMediaItemById(newItem.getId()) == null) {
                        db.getDaoMedia().insertSingleMediaItem(newItem);
                    } else {
                        db.getDaoMedia().updateMedia(newItem);
                    }
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error reading CSV file: " + ex);
        } finally {
            try {
                inputStream.close();
            } catch (IOException ex) {
                Log.e("CSVImporter","Error while closing input stream from CSV file: " + ex);
            }
        }
    }

    private void importCSVToCharacterDatabase(InputStream inputStream){
        Context ctx = ctxRef.get();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            MediaDatabase db = MediaDatabase.getMediaDataBase(ctx);
            db.getDaoCharacter().clearMediaCharacterJoin();
            Character newCharacter;
            String[] header = reader.readLine().split(",");
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",");//TODO this does not handle cases where the entry contains a comma
                newCharacter = new Character();
                for (int i = 0; i < row.length; i++) {
                    String[] appearances = new String[]{};
                    switch (header[i]) {
                        case "id":
                            newCharacter.setId(Integer.valueOf(row[i]));
                            break;
                        case "name":
                            newCharacter.setName(row[i]);
                            break;
                        case "appearances":
                            Log.d("Appearances", row[i]);
                            appearances = row[i].split(";");
                            break;
                        default:
                            System.out.println("Unused header: " + header[i]);
                    }
                    if (db.getDaoCharacter().getCharacterById(newCharacter.getId()) == null) {
                        db.getDaoCharacter().insert(newCharacter);
                    } else {
                        db.getDaoCharacter().update(newCharacter);
                    }
                    for (String strMedia : appearances){
                        MediaCharacterJoin mcj = new MediaCharacterJoin(Integer.valueOf(strMedia),newCharacter.getId());
                        db.getDaoCharacter().insert(mcj);
                    }
                }
            }
            Log.d("CSVimport", db.getDaoCharacter().getAllMCJoin().toString());
        } catch (IOException ex) {
            throw new RuntimeException("Error reading CSV file: " + ex);
        } finally {
            try {
                inputStream.close();
            } catch (IOException ex) {
                Log.e("CSVImporter","Error while closing input stream from CSV file: " + ex);
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
                return MediaItem.MEDIATYPE_NONE;
        }
    }

    @Override
    protected Void doInBackground(Integer... params) {
        Context ctx = ctxRef.get();
        InputStream inputStream;
        if (params[0] == SOURCE_ONLINE){
            try {
                URL url = new URL("https://docs.google.com/spreadsheets/d/e/2PACX-1vRvJaZHf3HHC_-XhWM4zftX9G_vnePy2-qxQ-NlmBs8a_tdBSSBjuerie6AMWQWp4H6R__BK9Q_li2g/pub?gid=0&single=true&output=csv");
                inputStream = url.openStream();
                importCSVToMediaDatabase(inputStream);
                url = new URL("https://docs.google.com/spreadsheets/d/e/2PACX-1vRvJaZHf3HHC_-XhWM4zftX9G_vnePy2-qxQ-NlmBs8a_tdBSSBjuerie6AMWQWp4H6R__BK9Q_li2g/pub?gid=1862227068&single=true&output=csv");
                inputStream = url.openStream();
                importCSVToCharacterDatabase(inputStream);
            } catch (MalformedURLException ex) {
                Log.e("DatabaseUpdate", ex.toString());
                return null;
            } catch (IOException ex) {
                Log.e("DatabaseUpdate", ex.toString());
                return null;
            }
        }
        else {
            inputStream = ctx.getResources().openRawResource(R.raw.starwarsmediadb);
            importCSVToMediaDatabase(inputStream);
            inputStream = ctx.getResources().openRawResource(R.raw.starwarscharacterdb);
            importCSVToCharacterDatabase(inputStream);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Context ctx = ctxRef.get();
        Toast.makeText(ctx, "Database updated", Toast.LENGTH_SHORT).show();
    }
}
