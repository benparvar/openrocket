package net.sf.openrocket.android.rocket;

import java.io.File;

import net.sf.openrocket.R;
import net.sf.openrocket.aerodynamics.WarningSet;
import net.sf.openrocket.android.Application;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class OpenRocketLoader extends FragmentActivity {
	private static final String TAG = "OpenRocketLoader";

	private ProgressDialog progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Intent i = getIntent();
		Uri file = i.getData();
		loadOrkFile(file);
	}

	@Override
	protected void onDestroy() {
		if ( progress != null ) {
			if ( progress.isShowing() ) {
				progress.dismiss();
			}
			progress = null;
		}
		super.onDestroy();
	}

	private void loadOrkFile( Uri file ) {
		Log.d(TAG,"Use ork file: " + file);
		String path = file.getPath();
		File orkFile = new File(path);
		progress = ProgressDialog.show(this, "Loading file", "");

		final OpenRocketLoaderTask task = new OpenRocketLoaderTask() {

			/* (non-Javadoc)
			 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
			 */
			@Override
			protected void onPostExecute(OpenRocketLoaderResult result) {
				super.onPostExecute(result);
				Log.d(TAG,"Finished loading " + OpenRocketLoader.this);
				finishedLoading(result);
			}

		};

		task.execute(orkFile);

	}
	
	private void finishedLoading(OpenRocketLoaderResult result) {
		if ( progress.isShowing() ) {
			progress.dismiss();
		}

		WarningSet warnings = result.warnings;
		if (warnings == null || warnings.isEmpty()) {
			((Application)OpenRocketLoader.this.getApplication()).setRocketDocument( result.rocket );
			Intent i = new Intent(this,OpenRocketViewer.class);
			startActivity(i);
			finish();
		} else {
			// TODO - Build a warning listing dialog
			DialogFragment newFragment = WarningDialogFragment.newInstance();
			newFragment.show(getSupportFragmentManager(), "dialog");
		}
	}

    public void doPositiveClick() {
        // Do stuff here.
        Log.i("FragmentAlertDialog", "Positive click!");
        finish();
    }

    public void doNegativeClick() {
        // Do stuff here.
        Log.i("FragmentAlertDialog", "Negative click!");
        finish();
    }

    public static class WarningDialogFragment extends DialogFragment {

	    public static WarningDialogFragment newInstance() {
	    	WarningDialogFragment frag = new WarningDialogFragment();
	        Bundle args = new Bundle();
	        frag.setArguments(args);
	        return frag;
	    }

	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {

	        return new AlertDialog.Builder(getActivity())
//	                .setIcon(android.R.drawable.alert_dialog_icon)
	                .setTitle("Warnings")
	                .setPositiveButton("OK",
	                    new DialogInterface.OnClickListener() {
	                        public void onClick(DialogInterface dialog, int whichButton) {
	                            ((OpenRocketLoader)getActivity()).doPositiveClick();
	                        }
	                    }
	                )
	                .setNegativeButton("Cancel",
	                    new DialogInterface.OnClickListener() {
	                        public void onClick(DialogInterface dialog, int whichButton) {
	                            ((OpenRocketLoader)getActivity()).doNegativeClick();
	                        }
	                    }
	                )
	                .create();
	    }
	}
}
