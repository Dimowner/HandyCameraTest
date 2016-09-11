package test.example.handycamera;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import test.example.handycamera.data.ImagesTable;
import test.example.handycamera.data.SQLiteHelper;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		GalleryFragment tasksFragment =
				(GalleryFragment) getSupportFragmentManager().findFragmentById(R.id.gallery_fragment);
		if (tasksFragment == null) {
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.add(R.id.gallery_fragment, GalleryFragment.newInstance());
			transaction.commit();;
		}

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
//				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//						.setAction("Action", null).show();
			makePhoto();
			}
		});
	}


	private void makePhoto() {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
			runCameraActivityAPI21();
		} else{
			// do something for phones running an SDK before lollipop
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			imageUri = getOutputMediaFileUri();
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			startActivityForResult(intent, REQUEST_CODE_MAKE_PHOTO);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.v(LOG_TAG, "onACtivityResult reqCode = " + requestCode + " resultCode= " + resultCode);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case REQUEST_CODE_MAKE_PHOTO:
					Intent intent = new Intent(getApplicationContext(), ImageEditActivity.class);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
					startActivity(intent);
//					if (data != null) {
//						//Get bitmap from file;
//						Bitmap image = null;
//						FileInputStream stream;
//						try {
//							File f = new File(imageUri.getPath());
//							stream = new FileInputStream(f);
//							if (!f.delete()) {
//								Log.e(LOG_TAG, "Image file uri:" + imageUri.getPath() + " failed to delete");
//							}
//							BitmapFactory.Options options = new BitmapFactory.Options();
//							options.inPreferredConfig = Bitmap.Config.RGB_565;
//							options.inJustDecodeBounds = false;
//							image = BitmapFactory.decodeStream(stream , null, options);
//						} catch (FileNotFoundException e) {
//							Log.e(LOG_TAG, "", e);
//						} catch (OutOfMemoryError e) {
//							Log.e(LOG_TAG, "", e);
//						}
////						operateWithCameraResults(image);
//						Log.v(LOG_TAG, "Operate With camera results");
//					}
					break;
			}
		}
	}

	private void runCameraActivityAPI21() {
		startActivityForResult(new Intent(this, CameraActivity.class), REQUEST_CODE_MAKE_PHOTO);
	}

	/**
	 * Получить путь к файлу в формате Uri.
	 * @return Путь к файлу.
	 */
	private Uri getOutputMediaFileUri() {
		File f = getOutputMediaFile();
		if (f != null) {
			return Uri.fromFile(f);
		} else {
			return null;
		}
	}

	/**
	 * Создать файл в который будет сохранено изображение.
	 * @return Файл.
	 */
	private File getOutputMediaFile() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

			File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
					Environment.DIRECTORY_PICTURES), IMAGES_FOLDER_NAME);
			// Create the storage directory if it does not exist
			if (!mediaStorageDir.exists()){
				if (!mediaStorageDir.mkdirs()){
//					UIUtil.showToast(MenuBase.this, R.string.menubase_failed_create_directory);
//					TODO:fix massage
					Log.e(LOG_TAG, "Failed to create folder");
					return null;
				}
			}
			// Create a media file name
			String timeStamp = new SimpleDateFormat(
					"yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
			return new File(mediaStorageDir.getPath() + File.separator +
					"IMG_"+ timeStamp + ".png");
		} else {
			return null;
		}
	}

	/**
	 * Write image info to local db
	 * @param title image title.
	 * @param path image file location.
	 * @return id of created record.
	 */
	private long insertImage(String title, String path) {
		SQLiteHelper helper = SQLiteHelper.getInstance(getApplicationContext());
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ImagesTable.COLUMN_TITLE, title);
		values.put(ImagesTable.COLUMN_IMG_LOCATION, path);
		return db.insert(ImagesTable.TABLE_IMAGES, null, values);
	}

//	/**
//	 * Сохранить изображение в папку изображений приложения.
//	 * @param imageName Название изображения в файловой системе устройства.
//	 * @param image Изображение для сохранения.
//	 * @return Признак успешного выполнения.
//	 */
//	public String saveImageIntoAppDir(String imageName, Bitmap image) {
//		if (image != null) {
//			try {
//				File path = FileUtil.getStorageDir(FileUtil.getAppImagesDir());
//				if (path != null && !StrUtil.empty(imageName)) {
//					File imageFile = FileUtil.createFile(path, imageName);
//					if (imageFile != null) {
//						if (FileUtil.writeImage(imageFile, image)) {
//							LOGD(LOG_TAG, "Image saved: " + imageFile.getAbsolutePath());
//							return imageFile.getAbsolutePath();
//						}
//					}
//					if (imageFile != null && imageFile.exists()) {
//						imageFile.delete();
//					}
//				}
//			} catch (Exception e) {
//				LOGE(LOG_TAG, "", e);
//			}
//		}
//		LOGE(LOG_TAG, "Не удалось записать изображение.");
//		return null;
//	}


	private static final int REQUEST_CODE_MAKE_PHOTO = 1;

	public static final String IMAGES_FOLDER_NAME = "HandyCamera";

	/** Ссылка на изображение в формате Uri */
	protected Uri imageUri = null;

	/** Tag for logging information. */
	private final String LOG_TAG = "MainActivity";

}
