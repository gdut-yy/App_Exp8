package net.devyy.exp8;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ZYY on 2018/4/24.
 */

public class DBAdapter {
    private static final String DB_NAME="test.db";
    private static final String DB_TABLE="staff";
    private static final int DB_VERSION=1;

    public static final String KEY_ID="_id";
    public static final String KEY_NAME="name";
    public static final String KEY_SEX="sex";
    public static final String KEY_DEPARTMENT="department";
    public static final String KEY_SALARY="salary";

    private SQLiteDatabase db;
    private final Context context;
    private DBOpenHelper dbOpenHelper;

    private static class DBOpenHelper extends SQLiteOpenHelper{
        public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
            super(context, name, factory, version);
        }

        public static final String DB_CREATE="create table " + DB_TABLE
                + "(" + KEY_ID + " integer primary key autoincrement, "
                + KEY_NAME + " text, "
                + KEY_SEX + " text, "
                + KEY_DEPARTMENT + " text, "
                + KEY_SALARY + " float);";

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

    public DBAdapter(Context _context){
        context=_context;
    }

    public void open() throws SQLException{
        dbOpenHelper=new DBOpenHelper(context, DB_NAME, null, DB_VERSION);
        try{
            db=dbOpenHelper.getWritableDatabase();
        }catch (SQLException ex){
            dbOpenHelper.getReadableDatabase();
        }
    }

    public void close(){
        if(db!=null){
            db.close();
            db=null;
        }
    }

    // 添加功能
    public long insert(People people){
        ContentValues newValues=new ContentValues();

        newValues.put(KEY_NAME, people.Name);
        newValues.put(KEY_SEX, people.Sex);
        newValues.put(KEY_DEPARTMENT, people.Department);
        newValues.put(KEY_SALARY, people.Salary);

        return db.insert(DB_TABLE, null, newValues);
    }

    // 删除功能
    public long deleteAllData(){
        return db.delete(DB_TABLE, null, null);
    }

    public long deleteOneData(long id){
        return db.delete(DB_TABLE, KEY_ID + "=" + id, null);
    }

    // 更新功能
    public long updateOneData(long id, People people){
        ContentValues updateValues = new ContentValues();
        updateValues.put(KEY_NAME, people.Name);
        updateValues.put(KEY_SEX, people.Sex);
        updateValues.put(KEY_DEPARTMENT, people.Department);
        updateValues.put(KEY_SALARY, people.Salary);

        return db.update(DB_TABLE, updateValues, KEY_ID + "=" + id, null);
    }

    // 查询功能
    public People[] getOneData(long id){
        Cursor results = db.query(DB_TABLE, new String[]{KEY_ID, KEY_NAME, KEY_SEX, KEY_DEPARTMENT, KEY_SALARY},
                KEY_ID + "=" + id, null, null, null, null);

        return ConvertToPeople(results);
    }

    public People[] getAllData(){
        Cursor results = db.query(DB_TABLE, new String[]{KEY_ID, KEY_NAME, KEY_SEX, KEY_DEPARTMENT, KEY_SALARY},
                null, null, null, null, null);

        return ConvertToPeople(results);
    }

    private People[] ConvertToPeople(Cursor cursor){
        int resultCounts = cursor.getCount();
        if(resultCounts==0 || !cursor.moveToFirst()){
            return null;
        }
        People[] peoples = new People[resultCounts];
        for(int i=0; i<resultCounts; i++){
            peoples[i]=new People();
            peoples[i].ID=cursor.getInt(0);
            peoples[i].Name=cursor.getString(cursor.getColumnIndex(KEY_NAME));
            peoples[i].Sex=cursor.getString(cursor.getColumnIndex(KEY_SEX));
            peoples[i].Department=cursor.getString(cursor.getColumnIndex(KEY_DEPARTMENT));
            peoples[i].Salary=cursor.getFloat(cursor.getColumnIndex(KEY_SALARY));
            cursor.moveToNext();
        }
        return peoples;
    }
}
