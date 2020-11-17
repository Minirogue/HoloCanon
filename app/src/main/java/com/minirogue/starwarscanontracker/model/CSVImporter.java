package com.minirogue.starwarscanontracker.model;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import com.minirogue.starwarscanontracker.R;
import com.minirogue.starwarscanontracker.model.room.MediaDatabase;
import com.minirogue.starwarscanontracker.model.room.entity.Company;
import com.minirogue.starwarscanontracker.model.room.entity.MediaItem;
import com.minirogue.starwarscanontracker.model.room.entity.MediaNotes;
import com.minirogue.starwarscanontracker.model.room.entity.MediaType;
import com.minirogue.starwarscanontracker.model.room.entity.Series;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.HashMap;

import javax.inject.Inject;

//TODO convert to kotlin and use coroutines
public class CSVImporter extends AsyncTask<Boolean, String, Void> {

    //private static final String TAG = "CSVImport";

    private final WeakReference<Application> appRef;
    private final boolean wifiOnly;
    private final ConnectivityManager connMgr;
    private final HashMap<String, Integer> convertType = new HashMap<>();
    private final HashMap<String, Integer> convertSeries = new HashMap<>();
    private final HashMap<String, Integer> convertCompany = new HashMap<>();
    private long newVersionId;
    private boolean forced = false;
    private final FilterUpdater filterUpdater;

    @Inject
    public CSVImporter(Application application, FilterUpdater filterUpdater) {
        appRef = new WeakReference<>(application);
        connMgr = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiOnly = PreferenceManager.getDefaultSharedPreferences(appRef.get()).getBoolean(appRef.get().getString(R.string.setting_unmetered_sync_only), true);
        this.filterUpdater = filterUpdater;
    }

    private void importCSVToMediaTypeTable(InputStream inputStream) {
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
                            mediaType.setId(Integer.parseInt(row[i]));
                            //Log.d(TAG, "type ID'ed "+row[i]+" mapped to "+Integer.valueOf(row[i]));
                            break;
                        case "media_type":
                            mediaType.setText(row[i]);
                            //Log.d(TAG, "title found "+row[i]);
                            break;
                        default:
                            //System.out.println("Unused header: " + header[i]);
                    }
                }
                long insertSuccessful = db.getDaoType().insert(mediaType);
                if (insertSuccessful == -1) {
                    db.getDaoType().update(mediaType);
                }
                convertType.put(mediaType.getText(), mediaType.getId());
                //Log.d(TAG, "type added "+mediaType.getId()+" "+mediaType.getTitle());
                if (wifiOnly && connMgr.isActiveNetworkMetered()) {
                    cancel(true);
                    break;
                }
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

    private void importCSVToSeriesTable(InputStream inputStream) {
        //Log.d(TAG, "starting media_type import");
        Application app = appRef.get();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            MediaDatabase db = MediaDatabase.getMediaDataBase(app);
            Series series;
            String[] header = reader.readLine().split(",");
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",");
                series = new Series();
                for (int i = 0; i < row.length; i++) {
                    switch (header[i]) {
                        case "id":
                            series.setId(Integer.parseInt(row[i]));
                            break;
                        case "name":
                            series.setTitle(row[i]);
                            break;
                        case "image":
                            series.setImageURL(row[i]);
                            break;
                        case "description":
                            series.setDescription(row[i].replace(";", ","));
                            break;
                        default:
                            //System.out.println("Unused header: " + header[i]);
                    }
                }
                long insertSuccessful = db.getDaoSeries().insert(series);
                if (insertSuccessful == -1) {
                    db.getDaoSeries().update(series);
                }
                convertSeries.put(series.getTitle(), series.getId());
                //Log.d(TAG, "type added "+mediaType.getId()+" "+mediaType.getTitle());
                if (wifiOnly && connMgr.isActiveNetworkMetered()) {
                    cancel(true);
                    break;
                }
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

    private void importCSVToCompanyTable(InputStream inputStream) {
        //Log.d(TAG, "starting media_type import");
        Application app = appRef.get();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            MediaDatabase db = MediaDatabase.getMediaDataBase(app);
            Company company;
            String[] header = reader.readLine().split(",");
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",");
                int id = 0;
                String name = "";
                for (int i = 0; i < row.length; i++) {
                    switch (header[i]) {
                        case "id":
                            id = (Integer.parseInt(row[i]));
                            break;
                        case "name":
                            name = (row[i]);
                        default:
                            //System.out.println("Unused header: " + header[i]);
                    }
                }
                company = new Company(id, name);
                long insertSuccessful = db.getDaoCompany().insert(company);
                if (insertSuccessful == -1) {
                    db.getDaoCompany().update(company);
                }
                convertCompany.put(company.getCompanyName(), company.getId());
                //Log.d(TAG, "type added "+mediaType.getId()+" "+mediaType.getTitle());
                if (wifiOnly && connMgr.isActiveNetworkMetered()) {
                    cancel(true);
                    break;
                }
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


    private void importCSVToMediaDatabase(InputStream inputStream) {
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
                            newItem.setId(Integer.parseInt(row[i]));
                            break;
                        case "title":
                            newItem.setTitle(row[i].replace(";", ","));
                            break;
                        case "series":
                            Integer newSeries = convertSeries.get(row[i]);
                            //convertSeries.get(key) returns null if there is no mapping for the key
                            if (newSeries == null) {
                                newItem.setSeries(-1);
                            } else {
                                newItem.setSeries(newSeries);
                            }
                            break;
                        case "type":
                            Integer newType = convertType.get(row[i]);
                            //convertType.get(key) returns null if there is no mapping for the key
                            if (newType == null) {
                                newItem.setType(-1);
                            } else {
                                newItem.setType(newType);
                            }
                            break;
                        case "description":
                            //semicolons are used as placeholders for commas in the room
                            // to not break the CSV format
                            newItem.setDescription(row[i].replace(";", ","));
                            break;
                        case "review":
                            //semicolons are used as placeholders for commas in the room
                            // to not break the CSV format
                            newItem.setReview(row[i].replace(";", ","));
                            break;
                        case "author":
                            newItem.setAuthor(row[i]);
                            break;
                        case "image":
                            newItem.setImageURL(row[i]);
                            break;
                        case "amazon_link":
                            newItem.setAmazonLink(row[i]);
                            break;
                        case "amazon_stream":
                            newItem.setAmazonStream(row[i]);
                            break;
                        case "released":
                            if (row[i].equals("")) {
                                //default value for release date
                                newItem.setDate("99/99/9999");
                            } else {
                                newItem.setDate(row[i]);
                            }
                            break;
                        case "timeline":
                            if (row[i].equals("")) {
                                newItem.setTimeline(10000.0);
                            } else {
                                newItem.setTimeline(Double.parseDouble(row[i]));
                            }
                            break;
                        case "publisher":
                            Integer newCompany = convertCompany.get(row[i]);
                            if (newCompany == null) {
                                newItem.setPublisher(-1);
                            } else {
                                newItem.setPublisher(newCompany);
                            }
                            break;
                        default:
                            //System.out.println("Unused header: " + header[i]);
                    }
                }
                long insertSuccessful = db.getDaoMedia().insert(newItem);
                if (insertSuccessful == -1) {
                    db.getDaoMedia().update(newItem);
                }
                db.getDaoMedia().insert(new MediaNotes(newItem.getId(), false, false, false));
                if (wifiOnly && connMgr.isActiveNetworkMetered()) {
                    cancel(true);
                    break;
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
    protected Void doInBackground(Boolean... params) {
        this.forced = params[0];
        if (wifiOnly && connMgr.isActiveNetworkMetered()) {
            cancel(true);
            return null;
        }
        InputStream inputStream = null;
        try {
            //update version number
            URL url = new URL("https://docs.google.com/spreadsheets/d/e/2PACX-1vRvJaZHf3HHC_-XhWM4zftX9G_vnePy2-qxQ-NlmBs8a_tdBSSBjuerie6AMWQWp4H6R__BK9Q_li2g/pub?gid=1842257512&single=true&output=csv");
            inputStream = url.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            newVersionId = Long.parseLong(reader.readLine().split(",")[0]);
            inputStream.close();
            if (!forced && newVersionId == PreferenceManager.getDefaultSharedPreferences(appRef.get()).getLong(appRef.get().getString(R.string.current_database_version), 0)) {
                cancel(true);
            } else if (forced) {
                publishProgress("Updating Database");
            }
            //import the media types
            if (isCancelled()) {
                return null;
            }
            url = new URL("https://docs.google.com/spreadsheets/d/e/2PACX-1vRvJaZHf3HHC_-XhWM4zftX9G_vnePy2-qxQ-NlmBs8a_tdBSSBjuerie6AMWQWp4H6R__BK9Q_li2g/pub?gid=1834840175&single=true&output=csv");
            inputStream = url.openStream();
            importCSVToMediaTypeTable(inputStream);
            inputStream.close();
            //import the series table
            if (isCancelled()) {
                return null;
            }
            url = new URL("https://docs.google.com/spreadsheets/d/e/2PACX-1vRvJaZHf3HHC_-XhWM4zftX9G_vnePy2-qxQ-NlmBs8a_tdBSSBjuerie6AMWQWp4H6R__BK9Q_li2g/pub?gid=485468234&single=true&output=csv");
            inputStream = url.openStream();
            importCSVToSeriesTable(inputStream);
            inputStream.close();
            //import the main media table
            if (isCancelled()) {
                return null;
            }
            url = new URL("https://docs.google.com/spreadsheets/d/e/2PACX-1vRvJaZHf3HHC_-XhWM4zftX9G_vnePy2-qxQ-NlmBs8a_tdBSSBjuerie6AMWQWp4H6R__BK9Q_li2g/pub?gid=1639412894&single=true&output=csv");
            inputStream = url.openStream();
            importCSVToCompanyTable(inputStream);
            inputStream.close();
            //import the main media table
            if (isCancelled()) {
                return null;
            }
            url = new URL("https://docs.google.com/spreadsheets/d/e/2PACX-1vRvJaZHf3HHC_-XhWM4zftX9G_vnePy2-qxQ-NlmBs8a_tdBSSBjuerie6AMWQWp4H6R__BK9Q_li2g/pub?gid=0&single=true&output=csv");
            inputStream = url.openStream();
            importCSVToMediaDatabase(inputStream);
            inputStream.close();
            //import the character table
                /*url = new URL("https://docs.google.com/spreadsheets/d/e/2PACX-1vRvJaZHf3HHC_-XhWM4zftX9G_vnePy2-qxQ-NlmBs8a_tdBSSBjuerie6AMWQWp4H6R__BK9Q_li2g/pub?gid=1862227068&single=true&output=csv");
                inputStream = url.openStream();
                importCSVToCharacterDatabase(inputStream);*/
        } catch (IOException ex) {
            //Log.e("DatabaseUpdate", ex.toString());
            cancel(true);
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Application app = appRef.get();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(app);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(app.getString(R.string.current_database_version), newVersionId);
        editor.apply();
        filterUpdater.updateFilters();
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Toast.makeText(appRef.get(), values[0], Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (forced) {
            Toast.makeText(appRef.get(), "Database updated", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCancelled(Void aVoid) {
        super.onCancelled(aVoid);
        if (forced) {
            Toast.makeText(appRef.get(), "Database update failed", Toast.LENGTH_LONG).show();
        }
    }
}
