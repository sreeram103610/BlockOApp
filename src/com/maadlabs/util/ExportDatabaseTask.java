package com.maadlabs.util;

import java.io.File;
import java.io.FileWriter;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVWriter;

import com.maadlabs.blocko.model.MySQLiteHelper;

public class ExportDatabaseTask extends AsyncTask<String, Void, Boolean> {
	
	Context context;
	MySQLiteHelper dbHelper;
	SQLiteDatabase database;
	String TableName, fileName;
	ProgressDialog dialog;
	
	public ExportDatabaseTask(Context context, String tableName, String fName) {
		
		dbHelper = new MySQLiteHelper(context);
		TableName = tableName;
		database = dbHelper.getWritableDatabase();
		this.context = context;
		this.fileName = fName;
	
	}

    @Override
    protected void onPreExecute() {
    	
    	dialog = new ProgressDialog(context);
        dialog.setMessage("Exporting database...");
        dialog.show();
    }

    protected Boolean doInBackground(final String... args) {
        File dbFile = context.getDatabasePath(MySQLiteHelper.DATABASE_NAME+".db");
        System.out.println(dbFile);  // displays the data base path in your logcat 
         File exportDir = new File(Environment.getExternalStorageDirectory(), "");

        if (!exportDir.exists()) { exportDir.mkdirs(); }

        File file = new File(exportDir, fileName+".csv");
        try {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            Cursor curCSV = database.rawQuery("select * from " + TableName,null);
            csvWrite.writeNext(curCSV.getColumnNames());
            while(curCSV.moveToNext()) {
                String arrStr[] ={curCSV.getString(0),curCSV.getString(1)};
                // curCSV.getString(3),curCSV.getString(4)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
            return true;
        } catch(Exception sqlEx) {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
            return false;
        } 
    }

    protected void onPostExecute(final Boolean success) {
        if (this.dialog.isShowing()) { this.dialog.dismiss(); }
        if (success) {
            Toast.makeText(context, "Export successful!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Export failed", Toast.LENGTH_SHORT).show();
        }
    }
}