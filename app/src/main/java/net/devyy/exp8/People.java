package net.devyy.exp8;

import android.net.Uri;

import java.security.PublicKey;

/**
 * Created by ZYY on 2018/4/24.
 */

public class People {
    public int ID=-1;
    public String Name;
    public String Sex;
    public String Department;
    public float Salary;

    @Override
    public String toString( ) {
        String result="";
        result+="ID:" + this.ID + ",";
        result+="姓名:" + this.Name + ",";
        result+="性别:" + this.Sex + ",";
        result+="所在部门:" + this.Department + ",";
        result+="工资:" + this.Salary + ",";
        return result;
    }

    public static final String MIME_DIR_PREFIX="vnd.android.cursor.dir";
    public static final String MIME_ITEM_PREFIX="vnd.android.cursor.item";
    public static final String MIME_ITEM="vnd.devyy.people";

    public static final String MINE_TYPE_SINGLE=MIME_ITEM_PREFIX+"/"+MIME_ITEM;
    public static final String MINE_TYPE_MULTIPLE=MIME_DIR_PREFIX+"/"+MIME_ITEM;

    public static final String AUTHORITY="net.devyy.exp8.PeopleProvider";
    public static final String PATH_SINGLE="people/#";
    public static final String PATH_MULTIPLE="people";
    public static final String CONTENT_URI_STRING="content://" + AUTHORITY + "/" + PATH_MULTIPLE;
    public static final Uri CONTENT_URI=Uri.parse(CONTENT_URI_STRING);

    public static final String KEY_ID="_id";
    public static final String KEY_NAME="name";
    public static final String KEY_SEX="sex";
    public static final String KEY_DEPARTMENT="department";
    public static final String KEY_SALARY="salary";
}
