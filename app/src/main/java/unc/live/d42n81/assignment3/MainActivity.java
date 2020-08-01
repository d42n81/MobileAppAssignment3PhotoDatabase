package unc.live.d42n81.assignment3;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase db;
    Bitmap photo;
    int width;
    int height;
    CustomSQLDB customDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Init Database:
        db = this.openOrCreateDatabase ("SomeDB", Context.MODE_PRIVATE, null);
        // Create Photos Table:
        db.execSQL("CREATE TABLE IF NOT EXISTS Photos(TAG TEXT, Size INT, Photo BLOB);");

        // Now feed it to the serializable wrapper class, CustomDB:
        this.customDB = new CustomSQLDB(this.db);

        // Logcat the contents of my database when on this activity:
        printDatabase();
    }

    public void captureOnClick(View v) {
        // Create a camera Intent.
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        int cameraRequestCode = 1;
        // Call start activity for result.
        startActivityForResult(cameraIntent, cameraRequestCode);
        // Make a onActivityResult function. Call the intent to display that image.
        // Pass database to this second intent.
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 1) {
            // we have gotten image back.
            this.photo = (Bitmap) data.getExtras().get("data");
            // convert this photo to byte array:
            byte [] photoBytes = convertBitmapToByteArray(this.photo);
            // Now start viewImageActivity
            Intent myIntent = new Intent(MainActivity.this,ViewCaptureActivity.class);
            // Pass it DB and our picture.
            // Do I need to pass it the DB? Or can I just reopen the database in this Intent?
//            myIntent.putExtra("database", this.db);
            myIntent.putExtra("photoBytes", photoBytes);
            // Do I need to send the original bitmap image?
            // Yes I do. I put in in a Serializable wrapper class.
            // Actually, SQLite Databases cannot be serialized.
//            myIntent.putExtra("serializedDB", this.customDB);

            MainActivity.this.startActivity(myIntent);
        }
    }

    private byte[] convertBitmapToByteArray(Bitmap bmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        bmap.recycle();
        return byteArray;
    }

    private Bitmap convertByteArrayToBitmap(byte [] bytes) {
        Bitmap.Config configBmp = Bitmap.Config.valueOf(photo.getConfig().name());
        Bitmap bitmap_tmp = Bitmap.createBitmap(this.width, height, configBmp);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        bitmap_tmp.copyPixelsFromBuffer(buffer);
        return bitmap_tmp;
    }

    public void browseOnClick(View v) {
        // Create a browse intent.
        Intent myIntent = new Intent(MainActivity.this,BrowseImageActivity.class);
        MainActivity.this.startActivity(myIntent);

    }

    public void printDatabase() {
        String resultString = "";
        String sqlFindString = "SELECT * FROM Photos" + ";";
        Cursor c = this.db.rawQuery(sqlFindString, null);
        c.moveToFirst();
        Log.v("MyTag", "Move to First = " + c.moveToFirst());
        if (!c.moveToFirst()) {
            Log.v("My Tag", "Database is empty");
            return;
        }
        // else:
        while (true) {
            resultString = c.getInt(1) + " ";
//            ArrayList<Double> doubleArrayList = new ArrayList<>();


//            resultString += "\n\nTag = " + c.getString(0) + "\nSize = " + c.getInt(1) +
//                    "\nImageBlob = " + c.getBlob(2).toString();

            if (!c.moveToNext()) {
                break;
            }
        }
        String[] stringArray = resultString.split(" ");
        ArrayList<Integer> intArrayList = new ArrayList();
        for(String a : stringArray) {
            intArrayList.add(Integer.parseInt(a));
        }

        int[] intArray =  new int[intArrayList.size()];
        for(int i = 0; i < intArrayList.size(); i++) {
            intArray[i] = intArrayList.get(i);
        }
        Arrays.sort(intArray);
        int temp = Integer.MAX_VALUE;
        Log.v("MyTag", "Contents of Database:" + resultString);
    }
}
