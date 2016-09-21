package ua.com.sofon.handycamera.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import ua.com.sofon.handycamera.util.FileUtil;

/**
 * Created on 12.09.2016.
 * @author Dimowner
 */
public class ImagesDataSource {

	private static ImagesDataSource mInstance;

	public static final int DEFAULT_SIZE = 500;//px
	private int mHeight = DEFAULT_SIZE;
	private int mWidth  = DEFAULT_SIZE;

	private SQLiteDatabase mDb;

	/** Tag for logging information. */
	private final String LOG_TAG = "ImagesDataSource";


	// Prevent direct instantiation.
	private ImagesDataSource(@NonNull Context context) {
		SQLiteHelper mDbHelper = new SQLiteHelper(context);
		mDb = mDbHelper.getWritableDatabase();
	}

	public static ImagesDataSource getInstance(@NonNull Context context) {
		if (mInstance == null) {
			mInstance = new ImagesDataSource(context);
		}
		return mInstance;
	}

	public void setHeight(int height) {
		this.mHeight = height;
	}

	public void setWidth(int width) {
		this.mWidth = width;
	}

	public List<ImageItem> getImages() {
		List<ImageItem> images = new ArrayList<>();
		try {

			String[] projection = {
					ImagesTable.COLUMN_ID,
					ImagesTable.COLUMN_TITLE,
					ImagesTable.COLUMN_DATE,
					ImagesTable.COLUMN_IMG_LOCATION
			};

			Cursor c = mDb.query(
					ImagesTable.TABLE_NAME, projection, null, null, null, null, null);

			if (c != null && c.getCount() > 0) {
				while (c.moveToNext()) {
					long itemId = c.getLong(c.getColumnIndexOrThrow(ImagesTable.COLUMN_ID));
					String title = c.getString(c.getColumnIndexOrThrow(ImagesTable.COLUMN_TITLE));
					Date date = new Date(c.getLong(c.getColumnIndexOrThrow(ImagesTable.COLUMN_DATE)));
					String location =	c.getString(c.getColumnIndexOrThrow(ImagesTable.COLUMN_IMG_LOCATION));
					ImageItem item = new ImageItem(itemId, title, date, location, loadBitmap(location));
					images.add(item);
				}
			}
			if (c != null) {
				c.close();
			}
		} catch (IllegalStateException e) {
			Log.e(LOG_TAG, "", e);
		}
		return images;
	}

	public ImageItem getImage(long id) {
		try {
			String[] projection = {
					ImagesTable.COLUMN_ID,
					ImagesTable.COLUMN_TITLE,
					ImagesTable.COLUMN_DATE,
					ImagesTable.COLUMN_IMG_LOCATION
			};

			String selection = ImagesTable.COLUMN_ID + " LIKE ?";
			String[] selectionArgs = {String.valueOf(id)};

			Cursor c = mDb.query(
					ImagesTable.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

			ImageItem item = null;

			if (c != null && c.getCount() > 0) {
				c.moveToFirst();
				long itemId = c.getLong(c.getColumnIndexOrThrow(ImagesTable.COLUMN_ID));
				String title = c.getString(c.getColumnIndexOrThrow(ImagesTable.COLUMN_TITLE));
				Date date = new Date(c.getLong(c.getColumnIndexOrThrow(ImagesTable.COLUMN_DATE)));
				String location = c.getString(c.getColumnIndexOrThrow(ImagesTable.COLUMN_IMG_LOCATION));
				item = new ImageItem(itemId, title, date, location, loadBitmap(location));
			}
			if (c != null) {
				c.close();
			}

			return item;
		} catch (IllegalStateException e) {
			Log.e(LOG_TAG, "", e);
		}
		return null;
	}

	private Bitmap loadBitmap(String path) {
		try {
			return FileUtil.decodeSampledBitmapFromFile(path, mWidth, mHeight);
		} catch (OutOfMemoryError e) {
			Log.e(LOG_TAG, "", e);
		}
		return null;
	}

	public void insertImage(@NonNull ImageItem item) {
		try {
			if (item.getImg() != null) {
				FileUtil.writeImage(new File(item.getPath()), item.getImg());
			}

			ContentValues values = new ContentValues();
			values.put(ImagesTable.COLUMN_TITLE, item.getTitle());
			values.put(ImagesTable.COLUMN_DATE, item.getDateMills());
			values.put(ImagesTable.COLUMN_IMG_LOCATION, item.getPath());

			mDb.insert(ImagesTable.TABLE_NAME, null, values);

		} catch (IllegalStateException e) {
			Log.e(LOG_TAG, "", e);
		}
	}

	public void updateImage(@NonNull ImageItem item) {
		try {
			if (item.getImg() != null) {
				FileUtil.writeImage(new File(item.getPath()), item.getImg());
			}

			ContentValues values = new ContentValues();
			values.put(ImagesTable.COLUMN_TITLE, item.getTitle());
			values.put(ImagesTable.COLUMN_DATE, item.getDateMills());
			values.put(ImagesTable.COLUMN_IMG_LOCATION, item.getPath());

			String selection = ImagesTable.COLUMN_ID + " LIKE ?";
			String[] selectionArgs = {String.valueOf(item.getId())};

			mDb.update(ImagesTable.TABLE_NAME, values, selection, selectionArgs);

		} catch (IllegalStateException e) {
			Log.e(LOG_TAG, "", e);
		}
	}

	public void deleteImage(long id) {
		try {
			String selection = ImagesTable.COLUMN_ID + " LIKE ?";
			String[] selectionArgs = {String.valueOf(id)};

			mDb.delete(ImagesTable.TABLE_NAME, selection, selectionArgs);
		} catch (IllegalStateException e) {
			Log.e(LOG_TAG, "", e);
		}
	}
}
