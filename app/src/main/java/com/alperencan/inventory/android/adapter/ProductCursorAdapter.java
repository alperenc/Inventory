package com.alperencan.inventory.android.adapter;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alperencan.inventory.android.R;
import com.alperencan.inventory.android.data.InventoryContract.InventoryEntry;

/**
 * {@link ProductCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of product data as its data source. This adapter knows
 * how to create list items for each row of pet data in the {@link Cursor}.
 */

public class ProductCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link ProductCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the product data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current product can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        ImageView productImageView = view.findViewById(R.id.product_image);
        TextView nameTextView = view.findViewById(R.id.name_text);
        TextView quantityTextView = view.findViewById(R.id.quantity_text);
        TextView priceTextView = view.findViewById(R.id.price_text);
        ImageButton saleImageButton = view.findViewById(R.id.sale_button);

        // Find the columns of the product attributes from the Cursor for the current product
        final long id = cursor.getLong(cursor.getColumnIndex(InventoryEntry._ID));
        String name = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME));
        Uri imageUri = Uri.parse(cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PHOTO_URL)));
        final int quantity = cursor.getInt(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY));
        float price = cursor.getFloat(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE));

        // Update the views with the attributes for the current product
        productImageView.setImageURI(imageUri);
        nameTextView.setText(name);
        quantityTextView.setText(String.format("%s %s ", context.getString(R.string.quantity), String.valueOf(quantity)));

        // If the product price is 0.0, then use some default text
        // that says "Price: TBD", so the TextView isn't showing '0.0'.
        if (price == 0) {
            priceTextView.setText(String.format("%s %s", context.getString(R.string.price), context.getString(R.string.tbd)));
        } else {
            priceTextView.setText(String.format("%s %s", context.getString(R.string.currency), String.valueOf(price)));
        }

        saleImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantity > 0) {
                    Uri productUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantity - 1);

                    int rowsAffected = view.getContext().getContentResolver().update(productUri, contentValues, null, null);
                    // Show a Toast message depending on whether or not the update was successful
                    if (rowsAffected == 0) {
                        Toast.makeText(context, context.getString(R.string.update_product_failed),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, context.getString(R.string.update_product_successful),
                                Toast.LENGTH_SHORT).show();

                        context.getContentResolver().notifyChange(productUri, null);
                    }
                } else {
                    Toast.makeText(context, context.getString(R.string.out_of_stock), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
