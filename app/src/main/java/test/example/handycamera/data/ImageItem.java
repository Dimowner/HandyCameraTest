package test.example.handycamera.data;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created on 11.09.2016.
 * @author Dimowner
 */
public class ImageItem implements Parcelable {

	public static final String EXTRAS_KEY_IMAGE = "image_item";
	public static final DateFormat DATE_FORMAT
					= new SimpleDateFormat("MM.dd.yyyy", Locale.getDefault());
	public static long NO_ID = -1;

	private long mId;
	private String mTitle;
	private Date mDate;
	private String mPath;
	private Bitmap mImg;


	public ImageItem(long id, String title, Date date, String path, Bitmap img) {
		if (id >= 0 || id == NO_ID) {
			this.mId = id;
		}
		this.mDate = date;
		this.mTitle = title;
		this.mPath = path;
		this.mImg = img;
	}

	//----- START Parcelable implementation ----------
	public ImageItem(Parcel in) {
		long[] l = new long[2];
		in.readLongArray(l);
		mId = l[0];
		mDate = new Date(l[1]);

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
		out.writeLongArray(new long[] {mId, mDate.getTime()});
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

	public void setTitle(String title) {
		this.mTitle = title;
	}

	public Date getDate() {
		return mDate;
	}

	public String getFormattedDate() {
		return DATE_FORMAT.format(mDate);
	}

	public long getDateMills() {
		return mDate.getTime();
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
				+ ", date = " + DATE_FORMAT.format(mDate)
				+ ", path = " + mPath + "];";
	}
}
