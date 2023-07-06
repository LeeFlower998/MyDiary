package com.example.mydiary;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

public class Activity_main extends AppCompatActivity {
    private SQLiteHelper helper;
    private SQLiteDatabase database;
    private ListView diaryList;
    private TextView diaryCounts;
    private List<Map<String, String>> dataList;
    private SimpleAdapter adapter;
    private String authorName;
    private String authorEmail;
    private SharedPreferences preferences;
    private int index;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper = new SQLiteHelper(this, "my_diary.db", null, 1);
        database = helper.getWritableDatabase(); // 创建数据库，同时会调用SQLiteHelper类的onCreate方法
        diaryList = findViewById(R.id.diaryList);
        diaryCounts = findViewById(R.id.wordCounts);
        dataList = helper.getAll();
        diaryCounts.setText(dataList.size() + "个日记");
        adapter = new SimpleAdapter(
                this,
                dataList,
                R.layout.activity_listview,
                new String[]{"id", "title", "date", "author", "content"},
                new int[]{R.id._id, R.id.title, R.id.date, R.id.author, R.id.content}
        );
        diaryList.setAdapter(adapter);

        // 列项目的点击事件
        diaryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LinearLayout rootView = (LinearLayout) view;
                TextView textView = rootView.findViewById(R.id._id);
                id = textView.getText().toString();
                Intent intent = new Intent(Activity_main.this, Activity_content.class);
                intent.putExtra("index", id);
                startActivity(intent);
            }
        });

        // 列项目的长按事件
        diaryList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                index = i;
                LinearLayout rootView = (LinearLayout) view;
                TextView textView = rootView.findViewById(R.id._id);
                id = textView.getText().toString();

                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_main.this);
                builder.setTitle("提示");
                builder.setMessage("确定删除该日记吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (helper.delete(id)) {
                            Toast.makeText(Activity_main.this, "删除成功", Toast.LENGTH_SHORT).show();
                            dataList.remove(index);
                            adapter.notifyDataSetChanged();
                            diaryCounts.setText(dataList.size() + "个日记");
                        } else
                            Toast.makeText(Activity_main.this, "删除失败", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create().show();
                return true;
            }
        });
        preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        authorName = preferences.getString("AuthorName", "Default Author");
        authorEmail = preferences.getString("AuthorEmail", "default@example.com");
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataList = helper.getAll();
        diaryCounts.setText(dataList.size() + "个日记");
        adapter = new SimpleAdapter(
                this,
                dataList,
                R.layout.activity_listview,
                new String[]{"id", "title", "date", "author", "content"},
                new int[]{R.id._id, R.id.title, R.id.date, R.id.author, R.id.content}
        );
        diaryList.setAdapter(adapter);
    }

    public void authorInfo(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("作者信息");
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_authorinfo, null);
        final EditText nameEditText = dialogView.findViewById(R.id.nameEditText);
        final EditText emailEditText = dialogView.findViewById(R.id.emailEditText);
        nameEditText.setText(authorName);
        emailEditText.setText(authorEmail);
        builder.setView(dialogView);
        builder.setNegativeButton("编辑", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_main.this);
                builder.setTitle("编辑作者信息");
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.activity_authorinfo, null);
                final EditText nameEditText = dialogView.findViewById(R.id.nameEditText);
                final EditText emailEditText = dialogView.findViewById(R.id.emailEditText);
                nameEditText.setText(authorName);
                emailEditText.setText(authorEmail);
                nameEditText.setEnabled(true);
                emailEditText.setEnabled(true);
                builder.setView(dialogView);
                builder.setNegativeButton("取消", null);
                builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        authorName = nameEditText.getText().toString();
                        authorEmail = emailEditText.getText().toString();
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("AuthorName", authorName);
                        editor.putString("AuthorEmail", authorEmail);
                        editor.apply();
                        Toast.makeText(Activity_main.this, "保存成功", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.create().show();
            }
        });
        builder.setPositiveButton("完成", null);
        builder.create().show(); // 显示对话框
    }

    public void toNewDiary(View view) {
        Intent intent = new Intent(Activity_main.this, Activity_new.class);
        intent.putExtra("authorName", authorName);
        startActivity(intent);
    }
}