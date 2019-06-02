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
import java.util.HashMap;

public class CSVImporter extends AsyncTask<Integer, Void, Void> {

    private static final String TAG = "CSVImport";

    private static final int SOURCE_ONLINE = 1;
    public static final int SOURCE_RAW_RESOURCES = 2;
    private WeakReference<Context> ctxRef;
    private HashMap<String, Integer> convertType = new HashMap<>();


    public CSVImporter(Context ctx){
        ctxRef = new WeakReference<>(ctx.getApplicationContext());
    }

    private void importCSVToMediaTypeTable(InputStream inputStream){
        Log.d(TAG, "starting media_type import");
        Context ctx = ctxRef.get();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            MediaDatabase db = MediaDatabase.getMediaDataBase(ctx);
            MediaType mediaType;
            String[] header = reader.readLine().split(",");
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",");
                mediaType = new MediaType();
                for (int i = 0; i < row.length; i++) {
                    switch (header[i]) {
                        case "id":
                            mediaType.setId(Integer.valueOf(row[i]));
                            Log.d(TAG, "type ID'ed "+row[i]+" mapped to "+Integer.valueOf(row[i]));
                            break;
                        case "media_type":
                            mediaType.setText(row[i]);
                            Log.d(TAG, "text found "+row[i]);
                            break;
                        default:
                            System.out.println("Unused header: " + header[i]);
                    }
                }
                long insertSuccessful = db.getDaoType().insert(mediaType);
                if (insertSuccessful == -1){
                    db.getDaoType().update(mediaType);
                }
                convertType.put(mediaType.getText(), mediaType.getId());
                Log.d(TAG, "type added "+mediaType.getId()+" "+mediaType.getText());
            }
            Log.d(TAG, "queried mediaTypeTable: "+db.getDaoType().getAllNonLive());
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

    private void importCSVToMediaDatabase(InputStream inputStream){
        Context ctx = ctxRef.get();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            MediaDatabase db = MediaDatabase.getMediaDataBase(ctx);
            MediaItem newItem;
            String[] header = reader.readLine().split(",");
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",");
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
                            newItem.setType(convertType.get(row[i]));
                            break;
                        case "description":
                            newItem.setDescription(row[i]);
                            break;
                        case "author":
                            newItem.setAuthor(row[i]);
                            break;
                        default:
                            System.out.println("Unused header: " + header[i]);
                    }
                    long insertSuccessful = db.getDaoMedia().insert(newItem);
                    if (insertSuccessful == -1){
                        db.getDaoMedia().update(newItem);
                    }
                    db.getDaoMedia().insert(new MediaNotes(newItem.getId()));
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
            DaoCharacter charDao = db.getDaoCharacter();
            charDao.clearMediaCharacterJoin();
            Character newCharacter;
            String[] header = reader.readLine().split(",");
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",");
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
                        case "major_appearance":
                            Log.d("Appearances", row[i]);
                            appearances = row[i].split(";");
                            break;
                        default:
                            System.out.println("Unused header: " + header[i]);
                    }
                    long didInsertWork = charDao.insert(newCharacter);
                    if (didInsertWork == -1){
                        charDao.update(newCharacter);
                    }
                    for (String strMedia : appearances){
                        charDao.insert(new MediaCharacterJoin(Integer.valueOf(strMedia),newCharacter.getId()));
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

    @Override
    protected Void doInBackground(Integer... params) {
        Context ctx = ctxRef.get();
        InputStream inputStream;
        if (params[0] == SOURCE_ONLINE){
            try {
                //import the media types
                URL url = new URL("https://docs.google.com/spreadsheets/d/e/2PACX-1vRvJaZHf3HHC_-XhWM4zftX9G_vnePy2-qxQ-NlmBs8a_tdBSSBjuerie6AMWQWp4H6R__BK9Q_li2g/pub?gid=1834840175&single=true&output=csv");
                inputStream = url.openStream();
                importCSVToMediaTypeTable(inputStream);
                //import the main media table
                url = new URL("https://docs.google.com/spreadsheets/d/e/2PACX-1vRvJaZHf3HHC_-XhWM4zftX9G_vnePy2-qxQ-NlmBs8a_tdBSSBjuerie6AMWQWp4H6R__BK9Q_li2g/pub?gid=0&single=true&output=csv");
                inputStream = url.openStream();
                importCSVToMediaDatabase(inputStream);
                //import the character table
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
            inputStream = ctx.getResources().openRawResource(R.raw.starwarsmediatypedb);
            importCSVToMediaTypeTable(inputStream);
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
