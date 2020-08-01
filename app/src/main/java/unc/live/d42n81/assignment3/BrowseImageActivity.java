package unc.live.d42n81.assignment3;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class BrowseImageActivity extends AppCompatActivity {

    SQLiteDatabase db;
    GestureDetector gdt;
    ImageView browseImageView;
    EditText tagEditText;
    EditText sizeEditText;

    // Array of Bitmaps to hold my images as a result from db search:
    // Maybe make this an array of byte arrays?
    ArrayList<String> tagArrayList = new ArrayList<>();
    ArrayList<Integer> sizeArrayList = new ArrayList<>();
    ArrayList<byte[]> byteArrayList = new ArrayList<>();
    ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
    int arrayListIndex = 0;
    int arrayListSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_image);

        // Initialize UI elements:
        browseImageView = findViewById(R.id.browseImageDisplay);
        tagEditText = findViewById(R.id.tagSearchEditText);
        sizeEditText = findViewById(R.id.sizeEditText);

        // Init Database:
        this.db = openOrCreateDatabase("SomeDB", Context.MODE_PRIVATE, null);

        // Create an async task to handle imageView Swiping:
         gdt = new GestureDetector(new GestureListener(this));
        browseImageView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                gdt.onTouchEvent(event);
                return true;
            } });

    }

    public void loadOnClick(View v){
        // Clear existing arrayLists:
        this.tagArrayList.clear();
        this.sizeArrayList.clear();
        this.byteArrayList.clear();
        this.bitmapArrayList.clear();
        // Load picture from entered info.
        String sqlFindString = "";
        // Case 1:
        if(this.tagEditText.getText().toString().equals("") || this.tagEditText.getText().
                toString().equals(" ")) {
            // Tag is not entered

            // Case 2:
            if(this.sizeEditText.getText().toString().equals("") || this.sizeEditText.getText()
                    .toString().equals(" ")) {
                // Tag and size is not entered.
                Toast.makeText(this, "You must enter a Tag or Size value to search " +
                        "images", Toast.LENGTH_LONG).show();
                return;
            }
            // Handle case 1 Here. We only have a size value:
            Log.v("MyTag", "Case 1. We only have a size value.");
            int sizeInt = Integer.parseInt(this.sizeEditText.getText().toString());
            // Specify upper and lower bounds as +- 25% of entered size value:
            int upperSizeBound =  (int) Math.round(sizeInt * 1.25);
            int lowerSizeBound =  (int) Math.round(sizeInt * 0.75);
            sqlFindString = "SELECT * FROM Photos WHERE Size > " + lowerSizeBound + " AND Size < "
                    + upperSizeBound + ";";
            Cursor c = this.db.rawQuery(sqlFindString, null);
            c.moveToFirst();
            Log.v("MyTag", "Move to First = " + c.moveToFirst());
            if(!c.moveToFirst()) {
                Log.v("My Tag", "Select is empty");
                Toast.makeText(this, "No results found for Selection", Toast.LENGTH_LONG).show();
                return;
            }
            // else, we have a selection:
            // Fill up arrays with selection data:
            Log.v("MyTag", "Filling up arrayLists with data.");
            while(true) {
                // While cursor has next entry
                this.tagArrayList.add(c.getString(0));
                Log.v("MyTag", "Added Tag, " + c.getString(0));
                this.sizeArrayList.add(c.getInt(1));
                Log.v("MyTag", "Added size, " + c.getInt(1));
                this.byteArrayList.add(c.getBlob(2));
                Log.v("MyTag", "Added blob, " + c.getBlob(2).toString());

                if (!c.moveToNext()) {
                    break;
                }
            }
            displayLoadData();
        } else{
            // We know we have a tag. Let's split the tag string to see if there are multiple tags
            String[] arrayOfTags = this.tagEditText.getText().toString().split(";");
            // Do we have size?
            if(this.sizeEditText.getText().toString().equals("") || this.sizeEditText.getText()
                    .toString().equals(" ")) {
                // Case 3.
                // We do not have a size value. Only a tag.
                Log.v("MyTag", "Case 3. We have only a tag value.");
//                sqlFindString = "SELECT * FROM Photos WHERE Tag = '" + tagEditText.getText().
//                        toString() + "'";
                sqlFindString = "SELECT * FROM Photos WHERE Tag = '" + arrayOfTags[0] + "'";
                for(int i = 1; i < arrayOfTags.length; i++) {
                    sqlFindString += " OR Tag = '" + arrayOfTags[i] + "'";
                }
                sqlFindString += ";";
                Cursor c = this.db.rawQuery(sqlFindString, null);
                c.moveToFirst();
                Log.v("MyTag", "Move to First = " + c.moveToFirst());
                if(!c.moveToFirst()) {
                    Log.v("My Tag", "Select is empty");
                    Toast.makeText(this, "No results found for Selection", Toast.LENGTH_LONG).show();
                    return;
                }

                while(true) {
                    // While cursor has next entry
                    this.tagArrayList.add(c.getString(0));
                    Log.v("MyTag", "Added Tag, " + c.getString(0));
                    this.sizeArrayList.add(c.getInt(1));
                    Log.v("MyTag", "Added size, " + c.getInt(1));
                    this.byteArrayList.add(c.getBlob(2));
                    Log.v("MyTag", "Added blob, " + c.getBlob(2).toString());

                    if (!c.moveToNext()) {
                        break;
                    }
                }
                // I'll need to handle the removal of duplicate values in display load data
                displayLoadData();
            } else {
                // Case 4.
                // We have a tag and a size value entered.
                sqlFindString = "SELECT * FROM Photos WHERE Tag = '" + arrayOfTags[0] + "'";
                // fill tags query
                for(int i = 1; i < arrayOfTags.length; i++) {
                    sqlFindString += " OR Tag = '" + arrayOfTags[i] + "'";
                }
                // handle size query
                int sizeInt = Integer.parseInt(this.sizeEditText.getText().toString());
                // Specify upper and lower bounds as +- 25% of entered size value:
                int upperSizeBound =  (int) Math.round(sizeInt * 1.25);
                int lowerSizeBound =  (int) Math.round(sizeInt * 0.75);
                sqlFindString += " AND Size > " + lowerSizeBound + " AND Size < "
                        + upperSizeBound + ";";

                // Make selection:
                Cursor c = this.db.rawQuery(sqlFindString, null);
                c.moveToFirst();
                Log.v("MyTag", "Move to First = " + c.moveToFirst());
                if(!c.moveToFirst()) {
                    Log.v("My Tag", "Select is empty");
                    Toast.makeText(this, "No results found for Selection", Toast.LENGTH_LONG).show();
                    return;
                }
                while(true) {
                    // While cursor has next entry
                    this.tagArrayList.add(c.getString(0));
                    Log.v("MyTag", "Added Tag, " + c.getString(0));
                    this.sizeArrayList.add(c.getInt(1));
                    Log.v("MyTag", "Added size, " + c.getInt(1));
                    this.byteArrayList.add(c.getBlob(2));
                    Log.v("MyTag", "Added blob, " + c.getBlob(2).toString());

                    if (!c.moveToNext()) {
                        break;
                    }
                }
                displayLoadData();

            }
        }
    }

    private Bitmap convertByteArrayToBitmap(byte [] bytes) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }

    public void displayLoadData() {
        // take data from loaded arrays display them to user's screen.
        Log.v("MyTag", "displayLoadedData called!");
        this.arrayListSize = this.tagArrayList.size();
        // handle the removal of duplicates.
        // Maybe I don't need to handle this removal of duplicates. It is probably fine.
        // I'll comment out this removal of duplicates for now:

//        for (int i = 0; i < byteArrayList.size(); i++) {
//            if(i == byteArrayList.size() -1) {
//                // we have reached the end of the list. Break
//                break;
//            }
//            // else:
//            if(Arrays.equals(byteArrayList.get(i), byteArrayList.get(i+1))) {
//                //remove elements from everything at that i index.
//                this.tagArrayList.remove(i);
//                this.sizeArrayList.remove(i);
//                this.byteArrayList.remove(i);
//            }
//        }
        // convert byte arrays from selection to bitmap arrayList:
        for(int i = 0; i < byteArrayList.size(); i++) {
            Log.v("MyTag", "Converting byteArrayToInt number " + i);
            this.bitmapArrayList.add(convertByteArrayToBitmap(byteArrayList.get(i)));
        }
        // Set Ui elements with data:
        this.browseImageView.setImageBitmap(this.bitmapArrayList.get(0));
        Log.v("MyTag", "Set browseImageView");
        this.tagEditText.setHint(this.tagArrayList.get(0));
        Log.v("MyTag", "Set tagEditText");
        this.sizeEditText.setHint("" + this.sizeArrayList.get(0));
        Log.v("MyTag", "Set sizeEditText");
    }

    // arrayList index methods:
    public int getArrayListIndex() {
        return this.arrayListIndex;
    }

    public int getArrayListSize() {
        return this.arrayListSize;
    }
    public void increaseArrayListIndex(){
        if(arrayListIndex == arrayListSize - 1) {
            // we are at the end of the list.
            Toast.makeText(this, "No more items in selection", Toast.LENGTH_LONG).show();
            return;
        } else {
            this.arrayListIndex++;
            this.browseImageView.setImageBitmap(this.bitmapArrayList.get(arrayListIndex));
            Log.v("MyTag", "Set browseImageView");
            this.tagEditText.setHint(this.tagArrayList.get(arrayListIndex));
            Log.v("MyTag", "Set tagEditText");
            this.sizeEditText.setHint("" + this.sizeArrayList.get(arrayListIndex));
            Log.v("MyTag", "Set sizeEditText");
        }
    }
    public void decreaseArrayListIndex() {
        if(this.arrayListIndex == 0) {
            Toast.makeText(this, "No previous items in selection", Toast.LENGTH_LONG).show();
            return;
        } else {
            this.arrayListIndex--;
            this.browseImageView.setImageBitmap(this.bitmapArrayList.get(arrayListIndex));
            Log.v("MyTag", "Set browseImageView");
            this.tagEditText.setHint(this.tagArrayList.get(arrayListIndex));
            Log.v("MyTag", "Set tagEditText");
            this.sizeEditText.setHint("" + this.sizeArrayList.get(arrayListIndex));
            Log.v("MyTag", "Set sizeEditText");
        }
    }

}
