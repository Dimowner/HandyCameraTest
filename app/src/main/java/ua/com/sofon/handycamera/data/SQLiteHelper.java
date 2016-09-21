package ua.com.sofon.handycamera.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * A helper class to manage database creation and version management.
 * Created on 10.09.2016.
 * @author Dimowner
 */
public class SQLiteHelper extends SQLiteOpenHelper implements BaseColumns {

	private static final String DATABASE_NAME = "handycamera.db";

	public static final int DATABASE_VERSION = 2;

	/** Tag for logging information. */
	private static final String LOG_TAG = "SQLiteHelper";
	/**
	 * Constructor.
	 * @param context Context to use to open or create the database.
	 */
	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * Constructor.
	 * @param context Context to use to open or create the database.
	 * @param name Name of the database file, or null for an in-memory database
	 * @param factory Factory to use for creating cursor objects, or null for the default
	 * 					version number of the database (starting at 1);
	 * @param version Database version number.
	 */
	public SQLiteHelper(Context context, String name, CursorFactory factory,
								int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		ImagesTable.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(LOG_TAG, "onUpgrage() update database from version: "
				+ oldVersion + " to version: " + newVersion);

		ImagesTable.onUpgrade(db, oldVersion, newVersion);
		onCreate(db);
	}
}
