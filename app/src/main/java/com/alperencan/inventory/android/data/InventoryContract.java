package com.alperencan.inventory.android.data;

import android.net.Uri;
import android.provider.BaseColumns;

import java.util.Set;

/**
 * API contract for the Inventory app.
 */

public final class InventoryContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private InventoryContract() {}

    /**
     * Inner class that defines constant values for the inventory database table.
     * Each entry in the table represents a single inventory item.
     */
    public static final class InventoryEntry implements BaseColumns {

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
         * Photo of the product.
         *
         * TYPE: TEXT
         */
        public final static String COLUMN_PRODUCT_PHOTO_URL = "photo";
    }
}
