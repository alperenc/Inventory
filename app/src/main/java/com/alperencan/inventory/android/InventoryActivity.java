package com.alperencan.inventory.android;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.alperencan.inventory.android.data.InventoryContract.InventoryEntry;

public class InventoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_products) {
            addDummyProducts();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addDummyProducts() {
        // Create a ContentValues object where column names are the keys,
        // and product attributes are the values.
        ContentValues iPhone8Values = new ContentValues();
        iPhone8Values.put(InventoryEntry.COLUMN_PRODUCT_NAME, "iPhone 8");
        iPhone8Values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, 100);
        iPhone8Values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, 699.00);
        iPhone8Values.put(InventoryEntry.COLUMN_PRODUCT_PHOTO_URL, "android.resource://com.alperencan.inventory.android/res/drawable/iphone8.png");

        // Insert a new row for product in the database
        getContentResolver().insert(InventoryEntry.CONTENT_URI, iPhone8Values);

        // Create a ContentValues object where column names are the keys,
        // and product attributes are the values.
        ContentValues iPhone8PlusValues = new ContentValues();
        iPhone8PlusValues.put(InventoryEntry.COLUMN_PRODUCT_NAME, "iPhone 8+");
        iPhone8PlusValues.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, 100);
        iPhone8PlusValues.put(InventoryEntry.COLUMN_PRODUCT_PRICE, 799.00);
        iPhone8PlusValues.put(InventoryEntry.COLUMN_PRODUCT_PHOTO_URL, "android.resource://com.alperencan.inventory.android/res/drawable/iphone8_plus.png");

        // Insert a new row for product in the database
        getContentResolver().insert(InventoryEntry.CONTENT_URI, iPhone8PlusValues);

        // Create a ContentValues object where column names are the keys,
        // and product attributes are the values.
        ContentValues iPhoneXValues = new ContentValues();
        iPhoneXValues.put(InventoryEntry.COLUMN_PRODUCT_NAME, "iPhone X");
        iPhoneXValues.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, 100);
        iPhoneXValues.put(InventoryEntry.COLUMN_PRODUCT_PRICE, 999.00);
        iPhoneXValues.put(InventoryEntry.COLUMN_PRODUCT_PHOTO_URL, "android.resource://com.alperencan.inventory.android/res/drawable/iphone_x.png");

        // Insert a new row for product in the database
        getContentResolver().insert(InventoryEntry.CONTENT_URI, iPhoneXValues);

        // Create a ContentValues object where column names are the keys,
        // and product attributes are the values.
        ContentValues Pixel2Values = new ContentValues();
        Pixel2Values.put(InventoryEntry.COLUMN_PRODUCT_NAME, "Pixel 2");
        Pixel2Values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, 100);
        Pixel2Values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, 649.00);
        Pixel2Values.put(InventoryEntry.COLUMN_PRODUCT_PHOTO_URL, "android.resource://com.alperencan.inventory.android/res/drawable/pixel2.png");

        // Insert a new row for product in the database
        getContentResolver().insert(InventoryEntry.CONTENT_URI, Pixel2Values);

        // Create a ContentValues object where column names are the keys,
        // and product attributes are the values.
        ContentValues Pixel2XLValues = new ContentValues();
        Pixel2XLValues.put(InventoryEntry.COLUMN_PRODUCT_NAME, "Pixel 2 XL");
        Pixel2XLValues.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, 100);
        Pixel2XLValues.put(InventoryEntry.COLUMN_PRODUCT_PRICE, 849.00);
        Pixel2XLValues.put(InventoryEntry.COLUMN_PRODUCT_PHOTO_URL, "android.resource://com.alperencan.inventory.android/res/drawable/pixel2_xl.png");

        // Insert a new row for product in the database
        getContentResolver().insert(InventoryEntry.CONTENT_URI, Pixel2XLValues);
    }
}
