package com.example.mydiary;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Activity_new extends AppCompatActivity {
    private SQLiteHelper helper;
    private Intent intent;
    private EditText emptyTitle;
    private EditText emptyDate;
    private EditText emptyAuthor;
    private EditText emptyContent;
    private TextView emptyCounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        helper = new SQLiteHelper(this, "my_diary.db", null, 1);
        intent = getIntent();
        emptyTitle = findViewById(R.id.emptyTitle);
        emptyDate = findViewById(R.id.emptyDate);
        emptyAuthor = findViewById(R.id.emptyAuthor);
        emptyContent = findViewById(R.id.emptyContent);
        emptyCounts = findViewById(R.id.emptyCounts);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        Date currentDateTime = new Date();
        String formattedDateTime = dateFormat.format(currentDateTime);

        emptyDate.setText(formattedDateTime);
        emptyAuthor.setText(intent.getStringExtra("authorName"));
        emptyCounts.setText("0个字");

        // 实时显示字数
        emptyContent.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // 在文字变化前的操作，此处不需要处理
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // 在文字变化时的操作，此处统计文字数量并更新显示
                int count = charSequence.length();
                emptyCounts.setText(count + "个字");
            }

            public void afterTextChanged(Editable editable) {
                // 在文字变化后的操作，此处不需要处理
            }
        });
    }

    public void upPictureInEmpty(View view) {

    }

    public void createDiary(View view) {
        String bodyTitle = emptyTitle.getText().toString();
        String bodyDate = emptyDate.getText().toString();
        String bodyAuthor = emptyAuthor.getText().toString();
        String bodyContent = emptyContent.getText().toString();
        boolean isNull = bodyTitle.isEmpty() || (bodyTitle.isEmpty() && bodyDate.isEmpty() && bodyAuthor.isEmpty() && bodyContent.isEmpty());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("确定新建日记吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (!isNull && helper.insert(bodyTitle, bodyDate, bodyAuthor, bodyContent)) {
                    Toast.makeText(Activity_new.this, "新建成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Activity_new.this, Activity_main.class);
                    startActivity(intent);
                } else
                    Toast.makeText(Activity_new.this, "新建失败\n快动手写点什么吧", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }

    @Override // 日记本标题栏的返回按钮点击事件
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
                    NavUtils.navigateUpFromSameTask(Activity_new.this);
                }
            });
            builder.setNegativeButton("取消", null);
            builder.create().show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
