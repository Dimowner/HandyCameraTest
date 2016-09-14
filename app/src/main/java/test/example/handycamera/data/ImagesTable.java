package test.example.handycamera.data;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Table Images SQLite database.
 * Created on 10.09.2016.
 * @author Dimowner
 */
public class ImagesTable {

	/** Table name */
	public static final String TABLE_NAME = "images";

	/** Table fields names */
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_IMG_LOCATION = "img_location";

	/** Creation script for table Images. */
	private static final String DATABASE_CREATE_IMAGES_TABLE_SCRIPT =
			"CREATE TABLE " + TABLE_NAME + " ("
					+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ COLUMN_TITLE + " TEXT NOT NULL, "
					+ COLUMN_DATE + " INTEGER NOT NULL, "
					+ COLUMN_IMG_LOCATION + " TEXT NOT NULL);";

	/** Tag for logging information. */
	private static final String LOG_TAG = "ImagesTable";


	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE_IMAGES_TABLE_SCRIPT);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
										  int newVersion) {
		Log.d(LOG_TAG, "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	}
}
