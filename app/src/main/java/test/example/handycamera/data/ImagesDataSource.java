package test.example.handycamera.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import test.example.handycamera.gallery.GalleryFragment;
import test.example.handycamera.util.FileUtil;

/**
 * Created on 12.09.2016.
 * @author Dimowner
 */
public class ImagesDataSource {

	private static ImagesDataSource mInstance;

	private SQLiteHelper mDbHelper;

	private SQLiteDatabase mDb;

	private List<ImagesDataSourceObserver> mObservers = new ArrayList<ImagesDataSourceObserver>();

	/** Tag for logging information. */
	private final String LOG_TAG = "ImagesDataSource";


	// Prevent direct instantiation.
	private ImagesDataSource(@NonNull Context context) {
		mDbHelper = new SQLiteHelper(context);
		mDb = mDbHelper.getWritableDatabase();
	}

	public static ImagesDataSource getInstance(@NonNull Context context) {
		if (mInstance == null) {
			mInstance = new ImagesDataSource(context);
		}
		return mInstance;
	}

	public List<ImageItem> getImages() {
		List<ImageItem> images = new ArrayList<>();
		try {

			String[] projection = {
					ImagesTable.COLUMN_ID,
					ImagesTable.COLUMN_TITLE,
					ImagesTable.COLUMN_IMG_LOCATION
			};

			Cursor c = mDb.query(
					ImagesTable.TABLE_NAME, projection, null, null, null, null, null);

			if (c != null && c.getCount() > 0) {
				while (c.moveToNext()) {
					long itemId = c
							.getLong(c.getColumnIndexOrThrow(ImagesTable.COLUMN_ID));
					String title = c
							.getString(c.getColumnIndexOrThrow(ImagesTable.COLUMN_TITLE));
					String location =
							c.getString(c.getColumnIndexOrThrow(ImagesTable.COLUMN_IMG_LOCATION));
					ImageItem item = new ImageItem(itemId, title, location, loadBitmap(location));
					Log.v(LOG_TAG, "item = " + item.toString());
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
					ImagesTable.COLUMN_IMG_LOCATION
			};

			String selection = ImagesTable.COLUMN_ID + " LIKE ?";
			String[] selectionArgs = {String.valueOf(id)};

			Cursor c = mDb.query(
					ImagesTable.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

			ImageItem item = null;

			if (c != null && c.getCount() > 0) {
				c.moveToFirst();
				long itemId = c
						.getLong(c.getColumnIndexOrThrow(ImagesTable.COLUMN_ID));
				String title = c
						.getString(c.getColumnIndexOrThrow(ImagesTable.COLUMN_TITLE));
				String location =
						c.getString(c.getColumnIndexOrThrow(ImagesTable.COLUMN_IMG_LOCATION));
				item = new ImageItem(itemId, title, location, loadBitmap(location));
				Log.v(LOG_TAG, "item = " + item.toString());
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
			Bitmap bitmap = FileUtil.readBitmapFromFile(path);
			Log.v(LOG_TAG, "loadBitmap bitmap is null = " + (bitmap == null));
			return bitmap;
		} catch (OutOfMemoryError e) {
			Log.e(LOG_TAG, "", e);
		}
		return null;
	}

	private String saveBitmap(Bitmap bitmap) {
		String timeStamp = new SimpleDateFormat(
				"yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
		File file = FileUtil.createFile(
				FileUtil.getStorageDir(FileUtil.APPLICATION_DIR),
				"IMG_"+ timeStamp + ".jpeg");
		FileUtil.writeImage(file, bitmap);
		return file.getPath();
	}

	public void saveImage(@NonNull ImageItem item) {
		Log.v(LOG_TAG, "saveImage = " + item.toString());
		try {
			String path = null;
			if (item.getImg() != null) {
				path = saveBitmap(item.getImg());
			}

			ContentValues values = new ContentValues();
//			values.put(ImagesTable.COLUMN_ID, item.getId());
			values.put(ImagesTable.COLUMN_TITLE, item.getTitle());
			values.put(ImagesTable.COLUMN_IMG_LOCATION, path);

			mDb.insert(ImagesTable.TABLE_NAME, null, values);


			notifyContentObserver();
		} catch (IllegalStateException e) {
			Log.e(LOG_TAG, "", e);
		}
	}

	public void deleteImage(long id) {
		Log.v(LOG_TAG, "deleteImage id = " + id);
		try {
			String selection = ImagesTable.COLUMN_ID + " LIKE ?";
			String[] selectionArgs = {String.valueOf(id)};

			mDb.delete(ImagesTable.TABLE_NAME, selection, selectionArgs);
			notifyContentObserver();
		} catch (IllegalStateException e) {
			Log.e(LOG_TAG, "", e);
		}
	}

	public void addContentObserver(ImagesDataSourceObserver observer) {
		if (!mObservers.contains(observer)) {
			mObservers.add(observer);
		}
	}

	public void removeContentObserver(ImagesDataSourceObserver observer) {
		if (mObservers.contains(observer)) {
			mObservers.remove(observer);
		}
	}

	private void notifyContentObserver() {
		Log.v(LOG_TAG, "notifyObserver");
		for (ImagesDataSourceObserver observer : mObservers) {
			observer.onTasksChanged();
		}
	}

	public interface ImagesDataSourceObserver {
		void onTasksChanged();
	}
}
