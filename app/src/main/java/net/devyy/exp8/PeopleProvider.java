package net.devyy.exp8;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by ZYY on 2018/4/25.
 */

public class PeopleProvider extends ContentProvider {
    private static final String DB_NAME="test.db";
    private static final String DB_TABLE="staff";
    private static final int DB_VERSION=1;

    private SQLiteDatabase db;
    private DBOpenHelper dbOpenHelper;

    private static final int MULTIPLE_PEOPLE=1;
    private static final int SINGLE_PEOPLE=2;
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(People.AUTHORITY, People.PATH_MULTIPLE, MULTIPLE_PEOPLE);
        uriMatcher.addURI(People.AUTHORITY, People.PATH_SINGLE, SINGLE_PEOPLE);
    }

    @Override
    public boolean onCreate( ) {
        Context context = getContext();
        dbOpenHelper = new DBOpenHelper(context, DB_NAME, null, DB_VERSION);
        db = dbOpenHelper.getWritableDatabase();

        if (db == null)
            return false;
        else
            return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(DB_TABLE);
        switch(uriMatcher.match(uri)){
            case SINGLE_PEOPLE:
                qb.appendWhere(People.KEY_ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                break;
        }
        Cursor cursor = qb.query(db,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch(uriMatcher.match(uri)){
            case MULTIPLE_PEOPLE:
                return People.MINE_TYPE_MULTIPLE;
            case SINGLE_PEOPLE:
                return People.MINE_TYPE_SINGLE;
            default:
                throw new IllegalArgumentException("Unkown uri:"+uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long id = db.insert(DB_TABLE, null, values);
        if ( id > 0 ){
            Uri newUri = ContentUris.withAppendedId(People.CONTENT_URI, id);
            getContext().getContentResolver().notifyChange(newUri, null);
            return newUri;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count = 0;
        switch(uriMatcher.match(uri)){
            case MULTIPLE_PEOPLE:
                count = db.delete(DB_TABLE, selection, selectionArgs);
                break;
            case SINGLE_PEOPLE:
                String segment = uri.getPathSegments().get(1);
                count = db.delete(DB_TABLE, People.KEY_ID + "=" + segment, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI:" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count;
        switch(uriMatcher.match(uri)){
            case MULTIPLE_PEOPLE:
                count = db.update(DB_TABLE, values, selection, selectionArgs);
                break;
            case SINGLE_PEOPLE:
                String segment = uri.getPathSegments().get(1);
                count = db.update(DB_TABLE, values, People.KEY_ID+"="+segment, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknow URI:" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    private static class DBOpenHelper extends SQLiteOpenHelper {
        public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
            super(context, name, factory, version);
        }

        public static final String DB_CREATE="create table " + DB_TABLE
                + "(" + People.KEY_ID + " integer primary key autoincrement, "
                + People.KEY_NAME + " text, "
                + People.KEY_SEX + " text, "
                + People.KEY_DEPARTMENT + " text, "
                + People.KEY_SALARY + " float);";

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
            onCreate(db);
        }
    }
}
