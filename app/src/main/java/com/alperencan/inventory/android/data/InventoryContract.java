package com.alperencan.inventory.android.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API contract for the Inventory app.
 */

public final class InventoryContract {

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.alperencan.inventory.android";
    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    /**
     * Possible path (appended to base content URI for possible URI's)
     */
    public static final String PATH_PRODUCTS = "products";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private InventoryContract() {}

    /**
     * Inner class that defines constant values for the inventory database table.
     * Each entry in the table represents a single inventory item.
     */
    public static final class InventoryEntry implements BaseColumns {

        /**
         * The content URI to access the product data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of products.
         */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single product.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /** Name of database table for products */
        public final static String TABLE_NAME = "products";

        /**
         * Unique ID number for the product (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the product.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_NAME ="name";

        /**
         * Photo URL of the product.
         *
         * TYPE: TEXT
         */
        public final static String COLUMN_PRODUCT_PHOTO_URL = "photoUrl";

        /**
         * Quantity of the product.
         *
         * TYPE: INTEGER
         */
        public final static String COLUMN_PRODUCT_QUANTITY= "quantity";

        /**
         * Price of the product.
         *
         * TYPE: FLOAT
         */
        public final static String COLUMN_PRODUCT_PRICE = "price";

        /**
         * Supplier name of the product.
         *
         * TYPE: TEXT
         */
        public final static String COLUMN_SUPPLIER_NAME = "supplierName";

        /**
         * Supplier phone of the product.
         * <p>
         * TYPE: TEXT
         */
        public final static String COLUMN_SUPPLIER_PHONE = "supplierPhone";

    }
}
