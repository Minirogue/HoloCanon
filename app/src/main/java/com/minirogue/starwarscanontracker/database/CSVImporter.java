package com.minirogue.starwarscanontracker.database;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import com.minirogue.starwarscanontracker.R;

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

    public static final int SOURCE_ONLINE = 1;
    private WeakReference<Application> appRef;
    private HashMap<String, Integer> convertType = new HashMap<>();
    private long newVersionId;


    public CSVImporter(Application application){
        appRef = new WeakReference<>(application);
    }

    private void importCSVToMediaTypeTable(InputStream inputStream){
        //Log.d(TAG, "starting media_type import");
        Application app = appRef.get();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            MediaDatabase db = MediaDatabase.getMediaDataBase(app);
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
                            //Log.d(TAG, "type ID'ed "+row[i]+" mapped to "+Integer.valueOf(row[i]));
                            break;
                        case "media_type":
                            mediaType.setText(row[i]);
                            //Log.d(TAG, "text found "+row[i]);
                            break;
                        default:
                            //System.out.println("Unused header: " + header[i]);
                    }
                }
                long insertSuccessful = db.getDaoType().insert(mediaType);
                if (insertSuccessful == -1){
                    db.getDaoType().update(mediaType);
                }
                convertType.put(mediaType.getText(), mediaType.getId());
                //Log.d(TAG, "type added "+mediaType.getId()+" "+mediaType.getText());
            }
            //Log.d(TAG, "queried mediaTypeTable: "+db.getDaoType().getAllNonLive());
        } catch (IOException ex) {
            throw new RuntimeException("Error reading CSV file: " + ex);
        } finally {
            try {
                inputStream.close();
            } catch (IOException ex) {
                //Log.e("CSVImporter","Error while closing input stream from CSV file: " + ex);
            }
        }
    }

    private void importCSVToMediaDatabase(InputStream inputStream){
        Application app = appRef.get();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            MediaDatabase db = MediaDatabase.getMediaDataBase(app);
            MediaItem newItem;
            String[] header = reader.readLine().split(",");
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",");
                newItem = new MediaItem();
                for (int i = 0; i < row.length; i++) {
                    switch (header[i]) {
                        case "ID":
                            newItem.id = (Integer.valueOf(row[i]));
                            break;
                        case "title":
                            newItem.title = (row[i]);
                            break;
                        case "type":
                            //Log.d(TAG, row[i]);
                            Integer newType = convertType.get(row[i]);
                            if (newType == null){
                                newItem.type = -1;
                            }else {
                                newItem.type = (newType);
                            }
                            break;
                        case "description":
                            newItem.description = (row[i]);
                            break;
                        case "author":
                            newItem.author = (row[i]);
                            break;
                        case "image":
                            newItem.imageURL = (row[i]);
                            break;
                        case "released":
                            if (row[i].equals("")){
                                newItem.date = "99/99/9999";
                            }else {
                                newItem.date = (row[i]);
                            }
                            break;
                        case "timeline":
                            if (row[i].equals("")){
                                newItem.timeline = 10000.0;
                            } else {
                                newItem.timeline = Double.valueOf(row[i]);
                            }
                            break;
                        default:
                            //System.out.println("Unused header: " + header[i]);
                    }
                    long insertSuccessful = db.getDaoMedia().insert(newItem);
                    if (insertSuccessful == -1){
                        db.getDaoMedia().update(newItem);
                    }
                    db.getDaoMedia().insert(new MediaNotes(newItem.id));
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error reading CSV file: " + ex);
        } finally {
            try {
                inputStream.close();
            } catch (IOException ex) {
                //Log.e("CSVImporter","Error while closing input stream from CSV file: " + ex);
            }
        }
    }

    /*private void importCSVToCharacterDatabase(InputStream inputStream){
        Application app = appRef.get();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            MediaDatabase db = MediaDatabase.getMediaDataBase(app);
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
    }*/

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Toast.makeText(appRef.get(), "Updating Database", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Void doInBackground(Integer... params) {
        InputStream inputStream;
        if (params[0] == SOURCE_ONLINE){
            try {
                //update version number
                URL url = new URL("https://docs.google.com/spreadsheets/d/e/2PACX-1vRvJaZHf3HHC_-XhWM4zftX9G_vnePy2-qxQ-NlmBs8a_tdBSSBjuerie6AMWQWp4H6R__BK9Q_li2g/pub?gid=1842257512&single=true&output=csv");
                inputStream = url.openStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                newVersionId = Long.valueOf(reader.readLine().split(",")[0]);
                //import the media types
                url = new URL("https://docs.google.com/spreadsheets/d/e/2PACX-1vRvJaZHf3HHC_-XhWM4zftX9G_vnePy2-qxQ-NlmBs8a_tdBSSBjuerie6AMWQWp4H6R__BK9Q_li2g/pub?gid=1834840175&single=true&output=csv");
                inputStream = url.openStream();
                importCSVToMediaTypeTable(inputStream);
                //import the main media table
                url = new URL("https://docs.google.com/spreadsheets/d/e/2PACX-1vRvJaZHf3HHC_-XhWM4zftX9G_vnePy2-qxQ-NlmBs8a_tdBSSBjuerie6AMWQWp4H6R__BK9Q_li2g/pub?gid=0&single=true&output=csv");
                inputStream = url.openStream();
                importCSVToMediaDatabase(inputStream);
                //import the character table
                /*url = new URL("https://docs.google.com/spreadsheets/d/e/2PACX-1vRvJaZHf3HHC_-XhWM4zftX9G_vnePy2-qxQ-NlmBs8a_tdBSSBjuerie6AMWQWp4H6R__BK9Q_li2g/pub?gid=1862227068&single=true&output=csv");
                inputStream = url.openStream();
                importCSVToCharacterDatabase(inputStream);*/
            } catch (MalformedURLException ex) {
                //Log.e("DatabaseUpdate", ex.toString());
                cancel(true);
                return null;
            } catch (IOException ex) {
                //Log.e("DatabaseUpdate", ex.toString());
                cancel(true);
                return null;
            }
        }
        Application app = appRef.get();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(app);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(app.getString(R.string.current_database_version),newVersionId);
        editor.apply();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Toast.makeText(appRef.get(), "Database updated", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCancelled(Void aVoid) {
        super.onCancelled(aVoid);
        Toast.makeText(appRef.get(), "Database not fully updated", Toast.LENGTH_SHORT).show();
    }
}
