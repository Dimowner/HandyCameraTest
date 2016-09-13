package test.example.handycamera.data;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created on 11.09.2016.
 * @author Dimowner
 */
public class ImageItem implements Parcelable {

	public static final String EXTRAS_KEY_IMAGE = "image_item";

	public static long NO_ID = -1;

	private long mId;
	private String mTitle;
	private String mPath;
	private Bitmap mImg;


	public ImageItem(long mId, String mTitle, String mPath, Bitmap mImg) {
		if (mId >= 0 || mId == NO_ID) {
			this.mId = mId;
		}
		this.mTitle = mTitle;
		this.mPath = mPath;
		this.mImg = mImg;
	}

	//----- START Parcelable implementation ----------
	public ImageItem(Parcel in) {
		this.mId = in.readLong();
		String[] data = new String[2];
		in.readStringArray(data);
		mTitle = data[0];
		mPath = data[1];
		mImg = in.readParcelable(ImageItem.class.getClassLoader());
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeLong(mId);
		out.writeStringArray(new String[] {mTitle, mPath});
		out.writeParcelable(mImg, PARCELABLE_WRITE_RETURN_VALUE);
	}

	public static final Parcelable.Creator<ImageItem> CREATOR
			= new Parcelable.Creator<ImageItem>() {
		public ImageItem createFromParcel(Parcel in) {
			return new ImageItem(in);
		}

		public ImageItem[] newArray(int size) {
			return new ImageItem[size];
		}
	};
	//----- END Parcelable implementation ----------

	public long getId() {
		return mId;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setmTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	public String getPath() {
		return mPath;
	}

	public Bitmap getImg() {
		return mImg;
	}

	@Override
	public String toString() {
		return "ImageItem[ id = " + mId
				+ ", title = " + mTitle
				+ ", path = " + mPath + "];";
	}
}
