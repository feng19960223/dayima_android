package com.taiqudong.android.enayeh.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.taiqudong.android.enayeh.R;

/**
 * LoadingDialog
 *
 * @author TXM
 *         progress dialog
 */
public class LoadingDialog extends Dialog {

	public LoadingDialog(Context context) {
		super(context);
	}

	public LoadingDialog(Context context, int themeResId) {
		super(context, themeResId);
	}

	protected LoadingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public static class Builder {
		private final Context context;

		private String description;

		public Builder(Context context, String description) {
			this.context = context;
			this.description = description;
		}

		public LoadingDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final LoadingDialog dialog = new LoadingDialog(context, R.style.LoadingDialog);
			View layout = inflater.inflate(R.layout.dialog_loading, null);
			//set description.
			TextView tv= (TextView) layout.findViewById(R.id.tv_description);
			tv.setText(description);
			dialog.addContentView(layout, new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
			dialog.setContentView(layout);
			return dialog;
		}
	}
}
