package net.devyy.exp8;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private ContentResolver resolver;

    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_sex)
    EditText etSex;
    @BindView(R.id.et_department)
    EditText etDepartment;
    @BindView(R.id.et_salary)
    EditText etSalary;
    @BindView(R.id.bt_add)
    Button btAdd;
    @BindView(R.id.bt_queryAll)
    Button btQueryAll;
    @BindView(R.id.bt_clear)
    Button btClear;
    @BindView(R.id.bt_deleteAll)
    Button btDeleteAll;
    @BindView(R.id.et_idEntry)
    EditText etIdEntry;
    @BindView(R.id.bt_query)
    Button btQuery;
    @BindView(R.id.bt_delete)
    Button btDelete;
    @BindView(R.id.bt_update)
    Button btUpdate;
    @BindView(R.id.tv_show)
    TextView tvShow;
    @BindView(R.id.tv_display)
    TextView tvDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        resolver=this.getContentResolver();
    }

    @OnClick({R.id.bt_add, R.id.bt_queryAll, R.id.bt_clear, R.id.bt_deleteAll, R.id.bt_query, R.id.bt_delete, R.id.bt_update})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_add:   // 添加数据
                add();
                break;
            case R.id.bt_queryAll:  // 全部显示
                queryAll();
                break;
            case R.id.bt_clear: // 清除显示
                tvDisplay.setText("");
                break;
            case R.id.bt_deleteAll: // 全部删除
                deleteAll();
                break;
            case R.id.bt_query: // ID查询
                query();
                break;
            case R.id.bt_delete:    // ID删除
                delete();
                break;
            case R.id.bt_update:    // ID更新
                update();
                break;
        }
    }

    private void add(){
        ContentValues values = new ContentValues();

        values.put(People.KEY_NAME, etName.getText().toString());
        values.put(People.KEY_SEX, etSex.getText().toString());
        values.put(People.KEY_DEPARTMENT, etDepartment.getText().toString());
        values.put(People.KEY_SALARY, Float.parseFloat(etSalary.getText().toString()));

        Uri newUri = resolver.insert(People.CONTENT_URI, values);

        tvShow.setText("添加成功，URI:" + newUri);
    }

    private void queryAll(){
        Cursor cursor = resolver.query(People.CONTENT_URI,
                new String[] { People.KEY_ID, People.KEY_NAME, People.KEY_SEX, People.KEY_DEPARTMENT, People.KEY_SALARY},
                null, null, null);
        if (cursor == null){
            tvShow.setText("数据库中没有数据");
            return;
        }
        tvShow.setText("数据库：" + String.valueOf(cursor.getCount()) + "条记录");

        String msg = "";
        if (cursor.moveToFirst()){
            do{
                msg += "ID：" + cursor.getInt(cursor.getColumnIndex(People.KEY_ID)) + "，";
                msg += "姓名：" + cursor.getString(cursor.getColumnIndex(People.KEY_NAME))+ "，";
                msg += "性别：" + cursor.getString(cursor.getColumnIndex(People.KEY_SEX))+ "，";
                msg += "所在部门：" + cursor.getString(cursor.getColumnIndex(People.KEY_DEPARTMENT))+ "，";
                msg += "工资：" + cursor.getFloat(cursor.getColumnIndex(People.KEY_SALARY)) + "\n";
            }while(cursor.moveToNext());
        }

        tvDisplay.setText(msg);
    }

    private void deleteAll(){
        resolver.delete(People.CONTENT_URI, null, null);
        String msg = "数据全部删除" ;
        tvShow.setText(msg);
    }

    private void query(){
        Uri uri = Uri.parse(People.CONTENT_URI_STRING + "/" + etIdEntry.getText().toString());
        Cursor cursor = resolver.query(uri,
                new String[] { People.KEY_ID, People.KEY_NAME, People.KEY_SEX, People.KEY_DEPARTMENT, People.KEY_SALARY},
                null, null, null);
        if (cursor == null){
            tvShow.setText("数据库中没有数据");
            return;
        }

        String msg = "";
        if (cursor.moveToFirst()){
            msg += "ID：" + cursor.getInt(cursor.getColumnIndex(People.KEY_ID)) + "，";
            msg += "姓名：" + cursor.getString(cursor.getColumnIndex(People.KEY_NAME))+ "，";
            msg += "性别：" + cursor.getString(cursor.getColumnIndex(People.KEY_SEX))+ "，";
            msg += "所在部门：" + cursor.getString(cursor.getColumnIndex(People.KEY_DEPARTMENT))+ "，";
            msg += "工资：" + cursor.getFloat(cursor.getColumnIndex(People.KEY_SALARY)) + "\n";
        }

        tvShow.setText("数据库：");
        tvDisplay.setText(msg);
    }

    private void delete(){
        Uri uri = Uri.parse(People.CONTENT_URI_STRING + "/" + etIdEntry.getText().toString());
        int result = resolver.delete(uri, null, null);
        String msg = "删除ID为"+etIdEntry.getText().toString()+"的数据" + (result>0?"成功":"失败");
        tvShow.setText(msg);
    }

    private void update(){
        ContentValues values = new ContentValues();

        values.put(People.KEY_NAME, etName.getText().toString());
        values.put(People.KEY_SEX, etSex.getText().toString());
        values.put(People.KEY_DEPARTMENT, etDepartment.getText().toString());
        values.put(People.KEY_SALARY, Float.valueOf(etSalary.getText().toString()));

        Uri uri = Uri.parse(People.CONTENT_URI_STRING + "/" + etIdEntry.getText().toString());
        int result = resolver.update(uri, values, null, null);

        String msg = "更新ID为"+etIdEntry.getText().toString()+"的数据" + (result>0?"成功":"失败");
        tvShow.setText(msg);
    }
}
