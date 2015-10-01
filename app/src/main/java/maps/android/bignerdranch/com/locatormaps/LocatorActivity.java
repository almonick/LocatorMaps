package maps.android.bignerdranch.com.locatormaps;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class LocatorActivity extends SingleFragmentActivity {

	private static final int REQUEST_DIALOG = 0;

	@Override
	protected Fragment createFragment() {
		return LocateFragment.newInstance();
	}

	@Override
	protected void onResume() {
		super.onResume();
		final int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if(errorCode != ConnectionResult.SUCCESS){
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode, this, REQUEST_DIALOG, new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					finish();
				}
			});
			errorDialog.show();
		}
	}
}
