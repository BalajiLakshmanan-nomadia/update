package com.synchroteam.utils;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import androidx.fragment.app.FragmentActivity;
import static android.app.ProgressDialog.STYLE_HORIZONTAL;
import static android.app.ProgressDialog.STYLE_SPINNER;
import android.os.Handler;

/**
 * Class to keep static utility methods for various utility tasks Please Define
 * DIALOG_ICON before using the class
 */
public final class DialogUtils {

	private static ProgressDialog pd;
	public static int DIALOG_ICON = 0;
	private static Activity mActivity;
	public static boolean showProgress = false;


	public static void showInfoDialog(final Activity activity,
			final String message) {

		AlertDialog.Builder builder = null;
		if (activity.getParent() != null)
			builder = new AlertDialog.Builder(activity.getParent());
		else
			builder = new AlertDialog.Builder(activity);

		// builder.setTitle("Alert!");
		builder.setIcon(DIALOG_ICON);
		if (message != null)
			builder.setMessage(message);

		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Runtime.getRuntime().gc();
			}
		});
		AlertDialog msg = builder.create();

		msg.show();
	}

	/**
	 * To show Dialog with message
	 * 

	 */
	public static void showInfoDialog(final Activity activity,
			final String title, final String message) {
		AlertDialog.Builder builder = null;
		if (activity.getParent() != null)
			builder = new AlertDialog.Builder(activity.getParent());
		else
			builder = new AlertDialog.Builder(activity);

		if (title != null)
			builder.setTitle(title);
		builder.setIcon(DIALOG_ICON);
		if (message != null)
			builder.setMessage(message);

		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(dialog!=null)
				dialog.dismiss();
				Runtime.getRuntime().gc();
			}
		});
		AlertDialog msg = builder.create();

		msg.show();
	}

	public static void showInfoDialogWithTwoButtons(final Activity activity,
			final String title, final String message, final String buttonOK,
			final String buttonCancel) {
		AlertDialog.Builder builder = null;
		if (activity.getParent() != null)
			builder = new AlertDialog.Builder(activity.getParent());
		else
			builder = new AlertDialog.Builder(activity);

		if (title != null)
			builder.setTitle(title);
		builder.setIcon(DIALOG_ICON);
		if (message != null)
			builder.setMessage(message);

		builder.setPositiveButton(buttonOK,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Runtime.getRuntime().gc();
					}
				});

		builder.setNegativeButton(buttonCancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Runtime.getRuntime().gc();
					}
				});
		AlertDialog msg = builder.create();

		msg.show();
	}

	/**
	 * To show Dialog with message and finish an activity when tap on Ok
	 * 
	 * @param context
	 * @param info
	 */
	public static void showFinishDialog(final Activity activity,
			final String message) {
		try {
			AlertDialog.Builder builder = null;
			if (activity.getParent() != null)
				builder = new AlertDialog.Builder(activity.getParent());
			else
				builder = new AlertDialog.Builder(activity);

			builder.setCancelable(false);
			builder.setIcon(DIALOG_ICON);
			if (message != null)
				builder.setMessage(message);

			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							Runtime.getRuntime().gc();
							activity.finish();
						}
					});
			AlertDialog msg = builder.create();

			msg.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * To show Dialog with message and finish an activity when tap on Ok
	 * 
	 * @param context
	 * @param info
	 */
	public static void showFinishDialog(final Activity activity,
			final String title, final String message) {
		try {
			AlertDialog.Builder builder = null;
			if (activity.getParent() != null)
				builder = new AlertDialog.Builder(activity.getParent());
			else
				builder = new AlertDialog.Builder(activity);

			if (title != null)
				builder.setTitle(title);
			builder.setCancelable(false);
			builder.setIcon(DIALOG_ICON);
			if (message != null)
				builder.setMessage(message);

			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							Runtime.getRuntime().gc();
							activity.finish();
						}
					});
			AlertDialog msg = builder.create();

			msg.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*****
	 * To show Dialog with message and pop current fragment when tap on Ok
	 * 
	 * @param fActivity
	 * @param title
	 * @param message
	 */
	public static void showFinishFragmentDialog(
			final FragmentActivity fActivity, final String title,
			final String message) {
		try {
			AlertDialog.Builder builder = null;
			if (fActivity.getParent() != null)
				builder = new AlertDialog.Builder(fActivity.getParent());
			else
				builder = new AlertDialog.Builder(fActivity);

			if (title != null)
				builder.setTitle(title);
			builder.setCancelable(false);
			builder.setIcon(DIALOG_ICON);
			if (message != null)
				builder.setMessage(message);

			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							Runtime.getRuntime().gc();
							fActivity.getSupportFragmentManager()
									.popBackStackImmediate();
						}
					});
			AlertDialog msg = builder.create();
			msg.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/****
	 * To show confirmation dialog with custom title,message,icon and buttons
	 * name. With an interface to respond with tapping on dialog' buttons.
	 * 
	 * @param activity
	 * @param title
	 * @param message
	 * @param isCancelable
	 * @param iconResId
	 * @param positiveBtnName
	 * @param negativeBtnName
	 * @param DialogResponseInterface
	 */
	public static AlertDialog showConfirmationDialog(final Activity activity,
			final String title, final String message,
			final boolean isCancelable, final int iconResId,
			final String positiveBtnName, final String negativeBtnName,
			final DialogResponseInterface dri) {

		try {
			AlertDialog.Builder builder = null;
			if (activity.getParent() != null)
				builder = new AlertDialog.Builder(activity.getParent());
			else
				builder = new AlertDialog.Builder(activity);

			if (title != null)
				builder.setTitle(title);
			builder.setCancelable(isCancelable);
			builder.setIcon(iconResId);
			if (message != null)
				builder.setMessage(message);

			if (positiveBtnName != null) {
				builder.setPositiveButton(positiveBtnName,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								dri.doOnPositiveBtnClick(activity);
							}
						});
			}
			if (negativeBtnName != null) {
				builder.setNegativeButton(negativeBtnName,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								dri.doOnNegativeBtnClick(activity);
							}
						});
			}
			AlertDialog msg = builder.create();
			msg.show();
			return msg;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * To show ProgressDialog while fetching in progress
	 *
	 * @param context
	 * @param title
	 * @param message
	 * @param isCancelable
	 */
	public static void showProgressDialog(final Activity activity,
										  final String title, final String message, final boolean isCancelable) {
		showProgressDialog(activity, title, message, isCancelable, false);
	}
	/**
	 * To show ProgressDialog while fetching in progress
	 * 
	 * @param context
	 * @param title
	 * @param message
	 * @param isCancelable
	 */
	public static void showProgressDialog(final Activity activity,
			final String title, final String message, final boolean isCancelable, final boolean showProgress) {

		DialogUtils.showProgress = showProgress;

		try {
			if (pd == null) {
				Activity parent = activity.getParent();
				if (parent != null)
					mActivity = parent;
				else
					mActivity = activity;
				pd = new ProgressDialog(mActivity);
			} else
				pd.dismiss();

			//////////////////////////////////pd.setTitle(title);
			pd.setMessage(message);
			pd.setIcon(DIALOG_ICON);

			if (showProgress) {
				pd.setProgressStyle(STYLE_HORIZONTAL);
				pd.setMax(100);
				pd.setProgressPercentFormat(NumberFormat.getPercentInstance());
				pd.setProgressNumberFormat(null);
			}
			else {
				pd.setProgressStyle(STYLE_SPINNER);
			}

			pd.setCancelable(isCancelable);
			pd.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * To dismiss progressDialog
	 */
	public static void dismissProgressDialog() {
		try {
			if (pd != null && pd.isShowing()) {
				pd.dismiss();
				pd = null;
				mActivity = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			pd = null;
		}
	}

	public static void updateProgress(int progress, int secondProgress) {

		try {
			if (pd != null && mActivity != null) {
				new Handler(mActivity.getMainLooper()).post(() -> {
					if (pd != null) {
						pd.setProgress(progress);
						pd.setSecondaryProgress(secondProgress);
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param dateString
	 * @param patternRequired
	 * @param dateStringPattern
	 * @return String
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getDateWithPattern(Date date, String patternRequired) {
		try {
			SimpleDateFormat requiredSdf = new SimpleDateFormat(patternRequired);
			return requiredSdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
			return date.toString();
		}
	}
}
