package maps.android.bignerdranch.com.locatormaps;

/**
 * Created by Bender on 21/09/2015.
 */
public class GalleryItem {
	private String mCaption;
	private String mId;
	private String mUrl;

	@Override
	public String toString() {
		return mCaption;
	}

	public void setCaption(String caption) {
		mCaption = caption;
	}

	public String getId() {
		return mId;
	}

	public void setId(String id) {
		mId = id;
	}

	public String getUrl() {
		return mUrl;
	}

	public void setUrl(String url) {
		mUrl = url;
	}
}