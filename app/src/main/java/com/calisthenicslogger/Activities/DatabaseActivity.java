package com.calisthenicslogger.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ajts.androidmads.sqliteimpex.SQLiteImporterExporter;
import com.calisthenicslogger.R;

import net.rdrei.android.dirchooser.DirectoryChooserActivity;
import net.rdrei.android.dirchooser.DirectoryChooserConfig;

public class DatabaseActivity extends AppCompatActivity {

    SQLiteImporterExporter sqLiteImporterExporter;
    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
    public static String db = "app_db";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_activity);

        SQLiteImporterExporter sqLiteImporterExporter = new SQLiteImporterExporter(getApplicationContext(), db);

// Listeners for Import and Export DB
        sqLiteImporterExporter.setOnImportListener(new SQLiteImporterExporter.ImportListener() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        sqLiteImporterExporter.setOnExportListener(new SQLiteImporterExporter.ExportListener() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void DirectoryButtonClick(View v)
    {
        final Intent chooserIntent = new Intent(this, DirectoryChooserActivity.class);

        final DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                .newDirectoryName("DirChooserSample")
                .allowReadOnlyDirectory(true)
                .allowNewDirectoryNameModification(true)
                .build();

        chooserIntent.putExtra(DirectoryChooserActivity.EXTRA_CONFIG, config);

// REQUEST_DIRECTORY is a constant integer to identify the request, e.g. 0
        startActivityForResult(chooserIntent, 0);
    }

    public void ImportButtonClick(View v)
    {
        try {
            sqLiteImporterExporter.importDataBase(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ExportButtonClick(View v)
    {
        try {
            sqLiteImporterExporter.exportDataBase(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {
                this.path = data.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR);
                TextView directoryTextView = findViewById(R.id.directoryTextView);
                directoryTextView.setText(this.path);
            } else {
                // Nothing selected
            }
        }
    }

}
