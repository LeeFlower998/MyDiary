package com.example.mydiary;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

public class Activity_content extends AppCompatActivity {
    private SQLiteHelper helper;
    private Intent intent;
    private String id;
    Map<String, String> dataMap;
    private TextView filledId;
    private EditText filledTitle;
    private EditText filledDate;
    private EditText filledAuthor;
    private EditText filledContent;
    private TextView filledCounts;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 启用ActionBar的返回按钮

        helper = new SQLiteHelper(this, "my_diary.db", null, 1);
        intent = getIntent();
        id = intent.getStringExtra("index");
        dataMap = helper.getData(id);
        filledId = findViewById(R.id.filledId);
        filledTitle = findViewById(R.id.filledTitle);
        filledDate = findViewById(R.id.filledDate);
        filledAuthor = findViewById(R.id.filledAuthor);
        filledContent = findViewById(R.id.filledContent);
        filledCounts = findViewById(R.id.filledCounts);

        String id = dataMap.get("id");
        String title = dataMap.get("title");
        String date = dataMap.get("date");
        String author = dataMap.get("author");
        String content = dataMap.get("content");
        filledId.setText(id);
        filledTitle.setText(title);
        filledDate.setText(date);
        filledAuthor.setText(author);
        filledContent.setText(content);
        // 显示字数
        int length = content.length();
        filledCounts.setText(length + "个字");

        // 实时显示字数
        filledContent.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // 在文字变化前的操作，此处不需要处理
            }
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // 在文字变化时的操作，此处统计文字数量并更新显示
                int count = charSequence.length();
                filledCounts.setText(count + "个字");
            }
            public void afterTextChanged(Editable editable) {
                // 在文字变化后的操作，此处不需要处理
            }
        });
    }

    public void upPictureInFilled(View view) {
    }

    public void saveDiary(View view) {
        String id = filledId.getText().toString();
        String title = filledTitle.getText().toString();
        String date = filledDate.getText().toString();
        String author = filledAuthor.getText().toString();
        String content = filledContent.getText().toString();
        boolean isNull = title.isEmpty() || (title.isEmpty() && date.isEmpty() && author.isEmpty() && content.isEmpty());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("确定保存编辑吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (!isNull && helper.update(id, title, date, author, content)) {
                    Toast.makeText(Activity_content.this, "保存成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Activity_content.this, Activity_main.class);
                    startActivity(intent);
                } else
                    Toast.makeText(Activity_content.this, "保存失败\n不可以什么都不写哦", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("确定不保存编辑并返回吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // 执行返回操作
                finish();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // 检查点击的是否是返回按钮
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("确定不保存编辑并返回吗？");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 执行返回操作
                    NavUtils.navigateUpFromSameTask(Activity_content.this);
                }
            });
            builder.setNegativeButton("取消", null);
            builder.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
