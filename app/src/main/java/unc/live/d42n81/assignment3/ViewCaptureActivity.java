package unc.live.d42n81.assignment3;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.ByteBuffer;

public class ViewCaptureActivity extends AppCompatActivity {

//    CustomSQLDB customDB;
    SQLiteDatabase db;
    byte[] photoByteArray;
    Bitmap originalBitmap;
    ImageView pictureDisplay;
    EditText tagEditText;
    TextView sizeTextView;
    int bitmapSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_capture);
        // Collect Data from what was sent from the intent:
        this.photoByteArray = this.getIntent().getExtras().getByteArray("photoBytes");
        // SQLite databases cannot be serialized. But I wonder if I can just open that
        // database normally?
//        this.customDB = (CustomSQLDB) this.getIntent().getSerializableExtra("serializedDB");
//        db = this.customDB.getDB();
        db = this.openOrCreateDatabase ("SomeDB", Context.MODE_PRIVATE, null);
        // Assign Pic to ImageView:
        this.originalBitmap = convertByteArrayToBitmap(this.photoByteArray);
        this.pictureDisplay = findViewById(R.id.imageDisplay);
        this.pictureDisplay.setImageBitmap(this.originalBitmap);
        // Get view resources we will be working with:
        this.tagEditText = findViewById(R.id.tagEditText);
        this.sizeTextView = findViewById(R.id.sizeTextView);
        // Set text of SizeView:
        // Getting a null pointer exception at this line. Is my original bitmap null?
        // After some testing, turns out my original bitmap is null. The photoBytes are not null.
        // I'll investigate what is going wrong tomorrow.
        bitmapSize = this.originalBitmap.getWidth() * this.originalBitmap.getHeight();
        String bitmapSizeString = "" + bitmapSize;
        this.sizeTextView.setText(bitmapSizeString);

    }

    private Bitmap convertByteArrayToBitmap(byte [] bytes) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }

    public void saveOnClick(View v) {
        // handle saving pic as a blob to the database.
        Log.v("myTag", "Save Clicked!");
        if(this.tagEditText.getText().toString().equals("") || this.tagEditText.getText().toString().equals(" ")) {
            Toast.makeText(this, "You must enter a Tag before saving a Picture",
                    Toast.LENGTH_LONG).show();
            return;
        }
        Log.v("MyTag", "Tag Text: " + this.tagEditText.getText().toString());
        // else:
        String tagString = this.tagEditText.getText().toString();
        // I need to split this by semicolons. Maybe then put it into an array?
        String[] arrayOfTags = tagString.split(";");
        // I wonder how I put multiple tags into my db. Multiple tag columns?
        // Multiple entries of the same picture but with a different tag?
        // Multiple entries of the same tag should work. I just need to make sure that when I query
        // by tag, size, or tag and size that I query again to remove duplicates.
        // Probably only have to double query on size only, since Tags among duplicates should be
        // unique. Though different images could have the same tag.

        for(String s:arrayOfTags){
            ContentValues rowData = new ContentValues();
            rowData.put("Tag", s);
            rowData.put("Size", this.bitmapSize);
            rowData.put("Photo", this.photoByteArray);

            db.insert("Photos", null, rowData);
        }

        Toast.makeText(this, "Photo Saved", Toast.LENGTH_LONG).show();

        // Now return to the main intent.
        Intent myIntent = new Intent(ViewCaptureActivity.this, MainActivity.class);
        ViewCaptureActivity.this.startActivity(myIntent);

    }
}
