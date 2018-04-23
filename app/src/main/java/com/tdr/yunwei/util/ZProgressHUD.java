package com.tdr.yunwei.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.tdr.yunwei.R;


public class ZProgressHUD extends Dialog {
    

	static ZProgressHUD instance;
	View view;
	TextView tvMessage;
	ImageView ivSuccess;
	ImageView ivFailure;
	ImageView ivProgressSpinner;
	AnimationDrawable adProgressSpinner;
	Activity mActivity;
    
	OnDialogDismiss onDialogDismiss;
    
	public OnDialogDismiss getOnDialogDismiss() {
		return onDialogDismiss;
	}
    
	public void setOnDialogDismiss(OnDialogDismiss onDialogDismiss) {
		this.onDialogDismiss = onDialogDismiss;
	}
    
	public static ZProgressHUD getInstance(Activity mActivity) {
		if (instance == null) {
			instance = new ZProgressHUD(mActivity);
		}
		return instance;
	}
    
	public ZProgressHUD(Activity mActivity) {
		super(mActivity, R.style.DialogTheme);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		this.setCanceledOnTouchOutside(false);
		this.mActivity = mActivity;
		view = getLayoutInflater().inflate(R.layout.dialog_progress, null);
		tvMessage = (TextView) view.findViewById(R.id.textview_message);

		ivProgressSpinner = (ImageView) view.findViewById(R.id.imageview_progress_spinner);
		ivProgressSpinner.setImageResource(R.drawable.round_spinner_fade);
		adProgressSpinner = (AnimationDrawable) ivProgressSpinner.getDrawable();
		this.setContentView(view);
	}


	public void setMessage(String message) {
		tvMessage.setText(message);
	}
    
	@Override
	public void show() {
		if (!mActivity.isFinishing()) {
			super.show();
		} else {
			instance = null;
		}
	}

    @Override
    public boolean isShowing() {
        return super.isShowing();
    }

    public void dismissWithSuccess() {
		tvMessage.setText("Success");
		showSuccessImage();
        
		if (onDialogDismiss != null) {
			this.setOnDismissListener(new OnDismissListener() {
                
				@Override
				public void onDismiss(DialogInterface dialog) {
					onDialogDismiss.onDismiss();
				}
			});
		}
		dismissHUD();
	}
    
	public void dismissWithSuccess(String message) {
		showSuccessImage();
		if (message != null) {
			tvMessage.setText(message);
		} else {
			tvMessage.setText("");
		}
        
		if (onDialogDismiss != null) {
			this.setOnDismissListener(new OnDismissListener() {
                
				@Override
				public void onDismiss(DialogInterface dialog) {
					onDialogDismiss.onDismiss();
				}
			});
		}
		dismissHUD();
	}
    
	public void dismissWithFailure() {
		showFailureImage();
		tvMessage.setText("Failure");
		if (onDialogDismiss != null) {
			this.setOnDismissListener(new OnDismissListener() {
                
				@Override
				public void onDismiss(DialogInterface dialog) {
					onDialogDismiss.onDismiss();
				}
			});
		}
		dismissHUD();
	}
    
	public void dismissWithFailure(String message) {
		showFailureImage();
		if (message != null) {
			tvMessage.setText(message);
		} else {
			tvMessage.setText("");
		}
		if (onDialogDismiss != null) {
			this.setOnDismissListener(new OnDismissListener() {
                
				@Override
				public void onDismiss(DialogInterface dialog) {
					onDialogDismiss.onDismiss();
				}
			});
		}
		dismissHUD();
	}
    
	protected void showSuccessImage() {
		ivProgressSpinner.setVisibility(View.GONE);
		ivSuccess.setVisibility(View.VISIBLE);
	}
    
	protected void showFailureImage() {
		ivProgressSpinner.setVisibility(View.GONE);
		ivFailure.setVisibility(View.VISIBLE);
	}
    
	protected void reset() {
		ivProgressSpinner.setVisibility(View.VISIBLE);
		ivFailure.setVisibility(View.GONE);
		ivSuccess.setVisibility(View.GONE);
		tvMessage.setText("Loading ...");
	}
    
	protected void dismissHUD() {
		AsyncTask<String, Integer, Long> task = new AsyncTask<String, Integer, Long>() {
            
			@Override
			protected Long doInBackground(String... params) {
				SystemClock.sleep(500);
				return null;
			}
            
			@Override
			protected void onPostExecute(Long result) {
				super.onPostExecute(result);
				dismiss();
				reset();
			}
		};
		task.execute();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		ivProgressSpinner.post(new Runnable() {
            
			@Override
			public void run() {
				adProgressSpinner.start();
                
			}
		});
	}
    
	public interface OnDialogDismiss {
		void onDismiss();
	}
    
}
