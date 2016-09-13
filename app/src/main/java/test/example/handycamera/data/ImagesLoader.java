package test.example.handycamera.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom {@link android.content.Loader} for a list of {@link ImageItem}, using the
 * {@link ImagesDataSource} as its source. This Loader is a {@link AsyncTaskLoader} so it queries
 * the data asynchronously.
 */
public class ImagesLoader extends AsyncTaskLoader<List<ImageItem>>
			implements ImagesDataSource.ImagesDataSourceObserver {

	private ImagesDataSource mDataSource;

	private long imageID = ImageItem.NO_ID;

	public ImagesLoader(Context context, @NonNull ImagesDataSource dataSource) {
		super(context);
		Log.v("ImagesLoader", "iMageLoade");
		mDataSource = dataSource;
	}

	public ImagesLoader(Context context, @NonNull ImagesDataSource dataSource, long imageID) {
		super(context);
		Log.v("ImagesLoader", "iMageLoade");
		mDataSource = dataSource;
		this.imageID = imageID;
	}

	@Override
	public List<ImageItem> loadInBackground() {
		Log.v("ImagesLoader", "loadInbackground");
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
		Log.v("ImagesLoader", "deliverResult");
		if (isReset()) {
			return;
		}

		if (isStarted()) {
			super.deliverResult(data);
		}

	}

	@Override
	protected void onStartLoading() {
		Log.v("ImagesLoader", "onStartLoading");
		// Deliver any previously loaded data immediately if available.
//		if (mDataSource.cachedImagesAvailable()) {
//			deliverResult(mDataSource.getCachedImages());
//		}

		// Begin monitoring the underlying data source.
		mDataSource.addContentObserver(this);

//		if (takeContentChanged()) { //|| !mDataSource.cachedImagesAvailable()) {
			// When a change has  been delivered or the repository cache isn't available, we force
			// a load.
			forceLoad();
//		}
	}

	@Override
	protected void onStopLoading() {
		Log.v("ImagesLoader", "onStopLoading");
		cancelLoad();
	}

	@Override
	protected void onReset() {
		Log.v("ImagesLoader", "onResetLoading");
		onStopLoading();
		mDataSource.removeContentObserver(this);
	}

	@Override
	public void onTasksChanged() {
		Log.v("ImagesLoader", "onTaskChanged");
		if (isStarted()) {
			forceLoad();
		}
	}
}
