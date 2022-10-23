package com.futurevalley.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.futurevalley.item.ItemChannel;
import com.futurevalley.item.ItemMovie;
import com.futurevalley.item.ItemRecent;
import com.futurevalley.item.ItemSeries;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "liveTv.db";

    public static final String TABLE_MOVIE = "movie";
    public static final String TABLE_SERIES = "series";
    public static final String TABLE_CHANNEL = "channel";
    public static final String TABLE_RECENT = "recent";

    public static final String MOVIE_ID = "id";
    public static final String MOVIE_TITLE = "movie_title";
    public static final String MOVIE_POSTER = "movie_poster";
    public static final String MOVIE_LANGUAGE = "movie_language";
    public static final String MOVIE_LANGUAGE_BACK = "movie_language_back";

    public static final String SERIES_ID = "id";
    public static final String SERIES_TITLE = "series_title";
    public static final String SERIES_POSTER = "series_poster";

    public static final String CHANNEL_ID = "id";
    public static final String CHANNEL_TITLE = "channel_title";
    public static final String CHANNEL_POSTER = "channel_poster";

    public static final String RECENT_ID = "id";
    private static final String RECENT_AUTO_ID = "auto_id";
    public static final String RECENT_TITLE = "recent_title";
    public static final String RECENT_IMAGE = "recent_image";
    public static final String RECENT_TYPE = "recent_type";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_MOVIE_TABLE = "CREATE TABLE " + TABLE_MOVIE + "("
                + MOVIE_ID + " INTEGER,"
                + MOVIE_TITLE + " TEXT,"
                + MOVIE_POSTER + " TEXT,"
                + MOVIE_LANGUAGE + " TEXT,"
                + MOVIE_LANGUAGE_BACK + " TEXT"
                + ")";

        String CREATE_SERIES_TABLE = "CREATE TABLE " + TABLE_SERIES + "("
                + SERIES_ID + " INTEGER,"
                + SERIES_TITLE + " TEXT,"
                + SERIES_POSTER + " TEXT"
                + ")";

        String CREATE_CHANNEL_TABLE = "CREATE TABLE " + TABLE_CHANNEL + "("
                + CHANNEL_ID + " INTEGER,"
                + CHANNEL_TITLE + " TEXT,"
                + CHANNEL_POSTER + " TEXT"
                + ")";

        String CREATE_RECENT_TABLE = "CREATE TABLE " + TABLE_RECENT + "("
                + RECENT_ID + " INTEGER,"
                + RECENT_AUTO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + RECENT_TITLE + " TEXT,"
                + RECENT_IMAGE + " TEXT,"
                + RECENT_TYPE + " TEXT"
                + ")";

        db.execSQL(CREATE_MOVIE_TABLE);
        db.execSQL(CREATE_SERIES_TABLE);
        db.execSQL(CREATE_CHANNEL_TABLE);
        db.execSQL(CREATE_RECENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHANNEL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECENT);
        onCreate(db);
    }

    public boolean getFavouriteById(String postId, String tableName) {
        boolean count = false;
        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = new String[]{postId};
        Cursor cursor = db.rawQuery("SELECT id FROM " + tableName + " WHERE id=? ", args);
        if (cursor.moveToFirst()) {
            count = true;
        }
        cursor.close();
        db.close();
        return count;
    }

    public boolean getRecentById(String postId, String postType) {
        boolean count = false;
        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = new String[]{postId, postType};
        Cursor cursor = db.rawQuery("SELECT id FROM " + TABLE_RECENT + " WHERE id=? and recent_type=? ", args);
        if (cursor.moveToFirst()) {
            count = true;
        }
        cursor.close();
        db.close();
        return count;
    }

    public void removeFavouriteById(String postId, String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + tableName + " WHERE id = " + postId);
        db.close();
    }

    public void addFavourite(String TableName, ContentValues contentvalues, String s1) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TableName, s1, contentvalues);
    }

    public void addRecent(String TableName, ContentValues contentvalues, String s1) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insertWithOnConflict(TableName, s1, contentvalues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public ArrayList<ItemMovie> getFavouriteMovie() {
        ArrayList<ItemMovie> movieList = new ArrayList<>();
        String selectQuery = "SELECT *  FROM "
                + TABLE_MOVIE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ItemMovie itemMovie = new ItemMovie();
                itemMovie.setId(cursor.getString(cursor.getColumnIndexOrThrow(MOVIE_ID)));
                itemMovie.setMovieTitle(cursor.getString(cursor.getColumnIndexOrThrow(MOVIE_TITLE)));
                itemMovie.setMoviePoster(cursor.getString(cursor.getColumnIndexOrThrow(MOVIE_POSTER)));
                itemMovie.setLanguageName(cursor.getString(cursor.getColumnIndexOrThrow(MOVIE_LANGUAGE)));
                itemMovie.setLanguageBackground(cursor.getString(cursor.getColumnIndexOrThrow(MOVIE_LANGUAGE_BACK)));
                movieList.add(itemMovie);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return movieList;
    }

    public ArrayList<ItemSeries> getFavouriteSeries() {
        ArrayList<ItemSeries> seriesList = new ArrayList<>();
        String selectQuery = "SELECT *  FROM "
                + TABLE_SERIES;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ItemSeries itemSeries = new ItemSeries();
                itemSeries.setId(cursor.getString(cursor.getColumnIndexOrThrow(SERIES_ID)));
                itemSeries.setSeriesName(cursor.getString(cursor.getColumnIndexOrThrow(SERIES_TITLE)));
                itemSeries.setSeriesPoster(cursor.getString(cursor.getColumnIndexOrThrow(SERIES_POSTER)));
                seriesList.add(itemSeries);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return seriesList;
    }

    public ArrayList<ItemChannel> getFavouriteChannel() {
        ArrayList<ItemChannel> channelList = new ArrayList<>();
        String selectQuery = "SELECT *  FROM "
                + TABLE_CHANNEL;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ItemChannel itemChannel = new ItemChannel();
                itemChannel.setId(cursor.getString(cursor.getColumnIndexOrThrow(CHANNEL_ID)));
                itemChannel.setChannelName(cursor.getString(cursor.getColumnIndexOrThrow(CHANNEL_TITLE)));
                itemChannel.setImage(cursor.getString(cursor.getColumnIndexOrThrow(CHANNEL_POSTER)));
                channelList.add(itemChannel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return channelList;
    }

    public ArrayList<ItemRecent> getRecent(boolean limit) {
        ArrayList<ItemRecent> recentList = new ArrayList<>();
        String selectQuery = "SELECT *  FROM "
                + TABLE_RECENT + " ORDER BY auto_id DESC";
        if (limit) {
            selectQuery = selectQuery + " LIMIT 4";
        }
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ItemRecent itemRecent = new ItemRecent();
                itemRecent.setId(cursor.getString(cursor.getColumnIndexOrThrow(RECENT_ID)));
                itemRecent.setRecentTitle(cursor.getString(cursor.getColumnIndexOrThrow(RECENT_TITLE)));
                itemRecent.setRecentImage(cursor.getString(cursor.getColumnIndexOrThrow(RECENT_IMAGE)));
                itemRecent.setRecentType(cursor.getString(cursor.getColumnIndexOrThrow(RECENT_TYPE)));
                recentList.add(itemRecent);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return recentList;
    }
}
