package com.ewaiter.android.e_waiter;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.ewaiter.android.e_waiter.data.MenuItemsContract;
import com.ewaiter.android.e_waiter.data.MenuItemsDbHelper;

import java.util.ArrayList;

public class Cart extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CART_ITEMS_LOADER = 100;
    CartItemsCursorAdapter mAdapter;
    TextView totalSum;
    ArrayList<FirebaseCursorPojo> cursorPojoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        RecyclerView recyclerView = findViewById(R.id.recyclerView_cart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CartItemsCursorAdapter(this, getItems());
        recyclerView.setAdapter(mAdapter);

        getLoaderManager().initLoader(CART_ITEMS_LOADER, null, this);

        totalSum = findViewById(R.id.cart_total);

        MenuItemsDbHelper dbHelper = new MenuItemsDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + MenuItemsContract.MenuItemsEntry.COLUMN_ITEM_PRICE + "*" + MenuItemsContract.MenuItemsEntry.COLUMN_ITEM_QUANTITY + ") as Total FROM " + MenuItemsContract.MenuItemsEntry.TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            int total = cursor.getInt(cursor.getColumnIndex("Total"));
            totalSum.setText(String.valueOf(total));
        }

        while (getItems().moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(MenuItemsContract.MenuItemsEntry.COLUMN_ITEM_NAME));
            String category = cursor.getString(cursor.getColumnIndex(MenuItemsContract.MenuItemsEntry.COLUMN_ITEM_CATEGORY));
            int quantity = cursor.getInt(cursor.getColumnIndex(MenuItemsContract.MenuItemsEntry.COLUMN_ITEM_QUANTITY));
            int unitPrice = cursor.getInt(cursor.getColumnIndex(MenuItemsContract.MenuItemsEntry.COLUMN_ITEM_PRICE));
            cursorPojoList.add(new FirebaseCursorPojo(name,category,unitPrice,quantity));
        }
    }


    private Cursor getItems() {
        String[] projection = {MenuItemsContract.MenuItemsEntry.COLUMN_ITEM_NAME, MenuItemsContract.MenuItemsEntry.COLUMN_ITEM_QUANTITY, MenuItemsContract.MenuItemsEntry.COLUMN_ITEM_PRICE, MenuItemsContract.MenuItemsEntry.COLUMN_ITEM_CATEGORY};
        String selection = MenuItemsContract.MenuItemsEntry.COLUMN_ITEM_QUANTITY + ">?";
        String[] selectionArgs = new String[] {"0"};

        Cursor cursor = getContentResolver().query(MenuItemsContract.MenuItemsEntry.CONTENT_URI,projection,selection,selectionArgs,null,null);
        return cursor;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {MenuItemsContract.MenuItemsEntry.COLUMN_ITEM_NAME, MenuItemsContract.MenuItemsEntry.COLUMN_ITEM_QUANTITY, MenuItemsContract.MenuItemsEntry.COLUMN_ITEM_PRICE, MenuItemsContract.MenuItemsEntry.COLUMN_ITEM_CATEGORY};
        String selection = MenuItemsContract.MenuItemsEntry.COLUMN_ITEM_QUANTITY + ">?";
        String[] selectionArgs = new String[] {"0"};
        return new CursorLoader(this, MenuItemsContract.MenuItemsEntry.CONTENT_URI,projection,selection,selectionArgs,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //update with new cursor containing updated data
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart_order_proceed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_proceed:
                // Save pet to database
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public ArrayList<FirebaseCursorPojo> getArrayList() {
        return cursorPojoList;
    }


}