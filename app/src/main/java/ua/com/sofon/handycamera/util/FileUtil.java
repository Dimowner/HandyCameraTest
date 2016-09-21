package ua.com.sofon.handycamera.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Class for working with files
 * Created on 12.09.2016.
 * @author Dimowner
 */
public class FileUtil {

	/** Application directory name. */
	public static final String APPLICATION_DIR = "HandyCamera";

	/** Tag for logging information. */
	private static final String LOG_TAG = "FileUtil";

	private FileUtil() {
	}
	/**
	 * Create file.
	 * If it is not exists, than create it.
	 * @param path Path to file.
	 * @param fileName File name.
	 */
	public static File createFile(File path, String fileName) {
		if (path != null) {
			Log.d(LOG_TAG, "createFile path = " + path.getAbsolutePath() + " fileName = " + fileName);
			File file = new File(path + "/" + fileName);
			//Create file if need.
			if (!file.exists()) {
				try {
					if (file.createNewFile()) {
						Log.i(LOG_TAG, "The file was successfully created! - " + file.getAbsolutePath());
					} else {
						Log.i(LOG_TAG, "The file exist! - " + file.getAbsolutePath());
					}
				} catch (IOException e) {
					Log.e(LOG_TAG, "Failed to create the file.", e);
					return null;
				}
			}
			if (!file.canWrite()) {
				Log.e(LOG_TAG, "The file can not be written.");
			}
			return file;
		} else {
			return null;
		}
	}

	/**
	 * Write bitmap into file.
	 * Записать изображение в файл.
	 * @param file The file in which is recorded the image.
	 * @param bitmap The image that will be recorded in the file.
	 * @return True if success, else false.
	 */
	public static boolean writeImage(File file, Bitmap bitmap) {
		if (!file.canWrite()) {
			Log.e(LOG_TAG, "The file can not be written.");
			return false;
		}
		if (bitmap == null) {
			Log.e(LOG_TAG, "Failed to write! bitmap is null.");
			return false;
		}
		try {
			FileOutputStream fos = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.JPEG, 40, fos)) {
				fos.close();
				return true;
			}
			fos.close();
		} catch (FileNotFoundException e) {
			Log.e(LOG_TAG, "File not found: " + e.getMessage());
		} catch (IOException e) {
			Log.e(LOG_TAG, "Error accessing file: " + e.getMessage());
		}
		return false;
	}

	/**
	 * Get public external storage directory
	 * @param dirName Directory name.
	 */
	public static File getStorageDir(String dirName) {
		if (dirName != null && !dirName.isEmpty()) {
			File file = new File(Environment.getExternalStorageDirectory(), dirName);
			if (isExternalStorageReadable() && isExternalStorageWritable()) {
				if (!file.exists() && !file.mkdirs()) {
					Log.e(LOG_TAG, "Directory " + file.getAbsolutePath() + " was not created");
				}
			}
			return file;
		} else {
			return null;
		}
	}

	/**
	 * Create directory in external storage public directory.
	 * @param dirName Directory name.
	 */
	public static File getPicturesStorageDir(String dirName) {
		// Get the directory for the user's public pictures directory.
		File file = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES), dirName);
		if (file.exists()) {
			Log.i(LOG_TAG, "Directory " + file.getAbsolutePath() + " is exists");
		} else if (!file.mkdirs()) {
			Log.e(LOG_TAG, "Directory " + file.getAbsolutePath() + " was not created");
		}
		Log.v(LOG_TAG, "picturesFile = " + file.getPath());
		return file;
	}

	/**
	 * Checks if external storage is available for read and write.
	 */
	public static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(state);
	}

	/**
	 * Checks if external storage is available to at least read.
	 */
	public static boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		return (Environment.MEDIA_MOUNTED.equals(state) ||
				Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
	}

	/**
	 * Read {@link Bitmap} from file system.
	 * @param path Path to file.
	 * @return {@link Bitmap}.
	 */
	public static Bitmap readBitmapFromFile(String path) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		return BitmapFactory.decodeFile(path, options);
	}

	/**
	 * Decode and sample down a bitmap from a file to the requested width and height.
	 *
	 * @param filename The full path of the file to decode
	 * @param reqWidth The requested width of the resulting bitmap
	 * @param reqHeight The requested height of the resulting bitmap
	 * @return A bitmap sampled down from the original with the same aspect ratio and dimensions
	 *         that are equal to or greater than the requested width and height
	 */
	public static Bitmap decodeSampledBitmapFromFile(
											String filename, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filename, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filename, options);
	}

	/**
	 * Calculate an inSampleSize for use in a {@link android.graphics.BitmapFactory.Options} object when decoding
	 * bitmaps using the decode* methods from {@link android.graphics.BitmapFactory}. This implementation calculates
	 * the closest inSampleSize that is a power of 2 and will result in the final decoded bitmap
	 * having a width and height equal to or larger than the requested width and height.
	 *
	 * @param options An options object with out* params already populated (run through a decode*
	 *            method with inJustDecodeBounds==true
	 * @param reqWidth The requested width of the resulting bitmap
	 * @param reqHeight The requested height of the resulting bitmap
	 * @return The value to be used for inSampleSize
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
														 int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}

			// This offers some additional logic in case the image has a strange
			// aspect ratio. For example, a panorama may have a much larger
			// width than height. In these cases the total pixels might still
			// end up being too large to fit comfortably in memory, so we should
			// be more aggressive with sample down the image (=larger inSampleSize).

			long totalPixels = width * height / inSampleSize;

			// Anything more than 2x the requested pixels we'll sample down further
			final long totalReqPixelsCap = reqWidth * reqHeight * 2;

			while (totalPixels > totalReqPixelsCap) {
				inSampleSize *= 2;
				totalPixels /= 2;
			}
		}
		return inSampleSize;
	}

	/**
	 * Recursively remove file or directory with children.
	 * @param file File to remove
	 */
	public static boolean deleteRecursivelyDirs(File file) {
		boolean ok = true;
		if (file != null ) {
			if (file.exists()) {
				if (file.isDirectory()) {
					String[] children = file.list();
					for (int i = 0; i < children.length; i++) {
						ok &= deleteRecursivelyDirs(new File(file, children[i]));
					}
				}
				if (ok && file.delete()) {
					Log.e(LOG_TAG, "File deleted: " + file.getAbsolutePath());
				}
			}
		}
		return ok;
	}

	/**
	 * Create new file for image with generated name.
	 * @return Created file.
	 */
	public static File getNewImageFile() {
		String timeStamp = new SimpleDateFormat(
				"yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
		return FileUtil.createFile(
				FileUtil.getStorageDir(FileUtil.APPLICATION_DIR),
				"IMG_"+ timeStamp + ".jpeg");
	}
}
