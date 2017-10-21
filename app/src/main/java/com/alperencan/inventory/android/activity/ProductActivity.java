package com.alperencan.inventory.android.activity;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

    /**
     * EditText field to enter the product's supplier name
     */
    private EditText supplierNameEditText;

    /**
     * EditText field to enter the product's supplier phone
     */
    private EditText supplierPhoneEditText;

    /**
     * Boolean flag that keeps track of whether the product has been edited (true) or not (false)
     */
    private boolean productHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the productHasChanged boolean to true.
     */
    private View.OnTouchListener touchListener = new View.OnTouchListener() {

        /**
         * Called when a touch event is dispatched to a view. This allows listeners to
         * get a chance to respond before the target view.
         *
         * @param v     The view the touch event has been dispatched to.
         * @param event The MotionEvent object containing full information about
         *              the event.
         * @return True if the listener has consumed the event, false otherwise.
         */
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            productHasChanged = true;
            return false;
        }
    };

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
        supplierNameEditText = (EditText) findViewById(R.id.edit_supplier_name);
        supplierPhoneEditText = (EditText) findViewById(R.id.edit_supplier_phone);
        ImageButton subtractQuantityButton = (ImageButton) findViewById(R.id.action_subtract_quantity);
        ImageButton addQuantityButton = (ImageButton) findViewById(R.id.action_add_quantity);
        Button orderMoreButton = (Button) findViewById(R.id.action_order);


        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        nameEditText.setOnTouchListener(touchListener);
        imageUriEditText.setOnTouchListener(touchListener);
        quantityEditText.setOnTouchListener(touchListener);
        priceEditText.setOnTouchListener(touchListener);
        supplierNameEditText.setOnTouchListener(touchListener);
        supplierPhoneEditText.setOnTouchListener(touchListener);
        subtractQuantityButton.setOnTouchListener(touchListener);
        addQuantityButton.setOnTouchListener(touchListener);
        orderMoreButton.setOnTouchListener(touchListener);

        subtractQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = Integer.parseInt(quantityEditText.getText().toString().trim());
                if (currentQuantity > 0) {
                    quantityEditText.setText(String.valueOf(currentQuantity - 1));
                }
            }
        });

        addQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = Integer.parseInt(quantityEditText.getText().toString().trim());
                quantityEditText.setText(String.valueOf(currentQuantity + 1));
            }
        });

        orderMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!supplierPhoneEditText.getText().toString().trim().isEmpty()) {
                    // Create new intent to go to Phone app
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(String.format("tel:%s", supplierPhoneEditText.getText().toString().trim())));
                    startActivity(intent);
                } else {
                    Toast.makeText(view.getContext(), view.getContext().getString(R.string.no_supplier_phone),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        if (!productHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void saveProduct() {
        // Read from input fields. Use trim to eliminate leading or trailing white space
        String nameString = nameEditText.getText().toString().trim();
        String imageUriString = imageUriEditText.getText().toString().trim();
        String quantityString = quantityEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();
        String supplierNameString = supplierNameEditText.getText().toString().trim();
        String supplierPhoneString = supplierPhoneEditText.getText().toString().trim();

        if (selectedProductUri == null && TextUtils.isEmpty(nameString)) {
            // Since no name was entered we can return early without creating a product.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Create a ContentValues object where column names are the keys, and product attributes are the values.
        ContentValues contentValues = new ContentValues();
        contentValues.put(InventoryEntry.COLUMN_PRODUCT_NAME, nameString);

        // If the image uri is not provided by the user, don't include it in ContentValues object. Instead, use default value.
        if (!TextUtils.isEmpty(imageUriString)) {
            contentValues.put(InventoryEntry.COLUMN_PRODUCT_PHOTO_URL, imageUriString);
        }

        // If the quantity is not provided by the user, don't try to parse the string into an
        // integer value and don't include in ContentValues object. Instead, use default value.
        if (!TextUtils.isEmpty(quantityString)) {
            contentValues.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, Integer.parseInt(quantityString));
        }

        // If the price is not provided by the user, don't try to parse the string into a
        // float value and don't include in ContentValues object. Instead, use default value.
        if (!TextUtils.isEmpty(priceString)) {
            contentValues.put(InventoryEntry.COLUMN_PRODUCT_PRICE, Float.parseFloat(priceString));
        }

        contentValues.put(InventoryEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        contentValues.put(InventoryEntry.COLUMN_SUPPLIER_PHONE, supplierPhoneString);

        // Determine if this is a new or existing product by checking if selectedProductUri is null
        if (selectedProductUri != null) {
            // This is an existing product, so update the the product with content URI: selectedProductUri
            // and pass in the new ContentValues. Pass in null for selection and selection args because
            // selectedProductUri will already identify the correct row in the database
            int rowsAffected = getContentResolver().update(selectedProductUri, contentValues, null, null);

            // Show a Toast message depending on whether or not the update was successful
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.update_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Insert a new product into the provider, returning the content URI for the new product.
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, contentValues);

            // Show a Toast message depending on whether or not the insertion/update was successful
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
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this product.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the product in the database.
     */
    private void deleteProduct() {
        // Only perform the delete if this is an existing product.
        if (selectedProductUri != null) {
            // Call the ContentResolver to delete the pet at the given URI.
            // Pass in null for the selection and selection args because the selectedProductUri
            // content URI already identifies the product that we want.
            int rowsDeleted = getContentResolver().delete(selectedProductUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new product, hide the "Delete" menu item.
        if (selectedProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
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
                saveProduct();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the product hasn't changed, continue with navigating up to parent activity
                // which is the {@link InventoryActivity}.
                if (!productHasChanged) {
                    NavUtils.navigateUpFromSameTask(ProductActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(ProductActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
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
            int supplierNameIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_PHONE);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String imageUri = cursor.getString(imageUriColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            float price = cursor.getFloat(priceColumnIndex);
            String supplierName = cursor.getString(supplierNameIndex);
            final String supplierPhone = cursor.getString(supplierPhoneIndex);

            // Update the views on the screen with the values from the database
            nameEditText.setText(name);
            imageUriEditText.setText(imageUri);
            quantityEditText.setText(String.valueOf(quantity));
            priceEditText.setText(String.valueOf(price));
            supplierNameEditText.setText(supplierName);
            supplierPhoneEditText.setText(supplierPhone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        nameEditText.setText("");
        imageUriEditText.setText("");
        quantityEditText.setText("");
        priceEditText.setText("");
        supplierNameEditText.setText("");
        supplierPhoneEditText.setText("");
    }
}
