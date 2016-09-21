package ua.com.sofon.handycamera.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom {@link android.content.Loader} for a list of {@link ImageItem}, using the
 * {@link ImagesDataSource} as its source. This Loader is a {@link AsyncTaskLoader} so it queries
 * the data asynchronously.
 */
public class ImagesLoader extends AsyncTaskLoader<List<ImageItem>> {

	private ImagesDataSource mDataSource;

	private long imageID = ImageItem.NO_ID;

	public ImagesLoader(Context context, @NonNull ImagesDataSource dataSource) {
		super(context);
		mDataSource = dataSource;
	}

	public ImagesLoader(Context context, @NonNull ImagesDataSource dataSource, long imageID) {
		super(context);
		mDataSource = dataSource;
		this.imageID = imageID;
	}

	@Override
	public List<ImageItem> loadInBackground() {
		if (imageID == ImageItem.NO_ID) {
			return mDataSource.getImages();
		} else {
			List<ImageItem> list = new ArrayList<>();
			list.add(mDataSource.getImage(imageID));
			return list;
		}
	}

	@Override
	public void deliverResult(List<ImageItem> data) {
		if (isReset()) {
			return;
		}

		if (isStarted()) {
			super.deliverResult(data);
		}

	}

	@Override
	protected void onStartLoading() {
		// Begin monitoring the underlying data source.
		forceLoad();
	}

	@Override
	protected void onStopLoading() {
		cancelLoad();
	}

	@Override
	protected void onReset() {
		onStopLoading();
	}
}
