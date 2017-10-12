package com.alperencan.inventory.android.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alperencan.inventory.android.data.InventoryContract.InventoryEntry;

/**
 * {@link ContentProvider} for Inventory app.
 */

public class InventoryProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    /**
     * URI matcher code for the content URI for the products table
     */
    private static final int PRODUCTS = 100;

    /**
     * URI matcher code for the content URI for a single product in the products table
     */
    private static final int PRODUCT_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return the root URI.
     * It's common the use NO_MATCH as the input for this case.
     */
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called form this class.
    static {
        /*
         * The content URI of the form "content://com.alperencan.inventory.android/products"will map to the
         * integer code {@link #PRODUCTS}. This URI is used to provide access to MULTIPLE rows
         * of the products table.
         */
        uriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_PRODUCTS, PRODUCTS);

        /*
         * The content URI of the form "content://com.alperencan.inventory.android/products/#" will map to the
         * integer code {@link #PRODUCT_ID}. This URI is used to provide access to ONE single row
         * of the products table.
         *
         * In this case, the "#" wildcard is used where "#" can be substituted for an integer.
         */
        uriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_PRODUCTS + "#", PRODUCT_ID);
    }

    /**
     * Database helper object
     */
    private InventoryDbHelper dbHelper;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        dbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Get readable database
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = uriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                /*
                 * For the PRODUCTS code, query the products table directly with the given
                 * projection, selection, selection arguments, and sort order.
                 */
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PRODUCT_ID:
                /*
                 * For the PRODUCT_ID code, extract out the ID fron the URI.
                 */
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                /*
                 * This will perform a query on the products table to return a Cursor containing
                 * relevant row of the table.
                 */
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        return cursor;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a product into the database with the given content values.
     * Return the new content URI for that specific row in the database.
     *
     * @param uri           content URI
     * @param contentValues product attributes
     * @return URI for the inserted product.
     */
    private Uri insertProduct(Uri uri, ContentValues contentValues) {
        // Check that the name is not null
        String name = contentValues.getAsString(InventoryEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product requires a name!");
        }

        // Check that the quantity is valid
        Integer quantity = contentValues.getAsInteger(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Product requires valid quantity!");
        }

        // Check that the price is valid
        Float price = contentValues.getAsFloat(InventoryEntry.COLUMN_PRODUCT_PRICE);
        if (price == null || price < 0.0) {
            throw new IllegalArgumentException("Product requires valid price");
        }

        // Check that the photoUrl is not null
        String photoUrl = contentValues.getAsString(InventoryEntry.COLUMN_PRODUCT_PHOTO_URL);
        if (photoUrl == null) {
            throw new IllegalArgumentException("Product requires a photo URL!");
        }

        // Get writeable database
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

        // Insert new product with the given values
        long id = sqLiteDatabase.insert(InventoryEntry.TABLE_NAME, null, contentValues);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);

    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
