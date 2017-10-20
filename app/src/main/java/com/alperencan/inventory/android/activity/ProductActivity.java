package com.alperencan.inventory.android.activity;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.alperencan.inventory.android.R;
import com.alperencan.inventory.android.data.InventoryContract.InventoryEntry;

/**
 * Allows user to create a new product or edit an existing one.
 */
public class ProductActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the product data loader
     */
    private static final int EXISTING_PRODUCT_LOADER = 0;

    /**
     * Content URI for the selected product (null, if it is a new product
     */
    private Uri selectedProductUri;

    /**
     * EditText field to enter the product's name
     */
    private EditText nameEditText;

    /**
     * EditText field to enter the product's image url
     */
    private EditText imageUriEditText;

    /**
     * EditText field to enter the product's quantity
     */
    private EditText quantityEditText;

    /**
     * EditText field to enter the product's price
     */
    private EditText priceEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        // If the intent that was used to launch this activity contains a product content URI,
        selectedProductUri = getIntent().getData();
        // then we know that we are editing an existing product
        if (selectedProductUri != null) {
            // Change the app bar to say "Edit Product"
            setTitle(getString(R.string.product_activity_title_edit_product));

            // Initialize a loader to read the product data from the database
            // and display the values in the editor
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        } else {
            // Change the app bar to say "Add a Product"
            setTitle(getString(R.string.product_activity_title_new_product));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            invalidateOptionsMenu();
        }

        // Find all relevant views that we will need to read user input from
        nameEditText = (EditText) findViewById(R.id.edit_product_name);
        imageUriEditText = (EditText) findViewById(R.id.edit_product_image_uri);
        quantityEditText = (EditText) findViewById(R.id.edit_product_quantity);
        priceEditText = (EditText) findViewById(R.id.edit_product_price);
    }

    private void insertProduct() {
        // Read from input fields. Use trim to eliminate leading or trailing white space
        String nameString = nameEditText.getText().toString().trim();
        String imageUriString = imageUriEditText.getText().toString().trim();
        int quantity = Integer.parseInt(quantityEditText.getText().toString().trim());
        float price = Float.parseFloat(priceEditText.getText().toString().trim());

        // Create a ContentValues object where column names are the keys, and product attributes are the values.
        ContentValues contentValues = new ContentValues();
        contentValues.put(InventoryEntry.COLUMN_PRODUCT_NAME, nameString);
        contentValues.put(InventoryEntry.COLUMN_PRODUCT_PHOTO_URL, imageUriString);
        contentValues.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        contentValues.put(InventoryEntry.COLUMN_PRODUCT_PRICE, price);

        // Insert a new product into the provider, returning the content URI for the new product.
        Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, contentValues);

        // Show a Toast message depending on whether or not the insertion was successful
        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.insert_product_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.insert_product_successful),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save product to database
                insertProduct();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since product activity show all product attributes,use null for projection parameter
        return new CursorLoader(this,       // Parent activity context
                selectedProductUri,         // Query the content URI for the selected product
                null,                       // Columns to include in the resulting Cursor (null for all columns)
                null,                       // No selection clause
                null,                       // No selection args
                null);                      // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row (and only row) of the cursor and read data from it
        if (cursor.moveToFirst()) {
            // Find the columns of product attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int imageUriColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PHOTO_URL);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String imageUri = cursor.getString(imageUriColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            float price = cursor.getFloat(priceColumnIndex);

            // Update the views on the screen with the values from the database
            nameEditText.setText(name);
            imageUriEditText.setText(imageUri);
            quantityEditText.setText(String.valueOf(quantity));
            priceEditText.setText(String.valueOf(price));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        nameEditText.setText("");
        imageUriEditText.setText("");
        quantityEditText.setText("");
        priceEditText.setText("");
    }
}
