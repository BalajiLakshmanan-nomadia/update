package com.synchroteam.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.synchroteam3.R;

// TODO: Auto-generated Javadoc
/**
 * The Class CalanderDateTextView.
 */
public class CalanderDateTextView extends TextView {

	/** The started job. */
	private int startedJob;

	/** The stroke width. */
	private float STROKE_WIDTH;

	/** The suspendedjob. */
	private int suspendedjob;

	/** The scheduled job. */
	private int scheduledJob;

	/** The completed job. */
	private int completedJob;

	/** The color codestarted job. */
	private int colorCodestartedJob;

	/** The color codesuspendedjob. */
	private int colorCodesuspendedjob;

	/** The color codescheduled job. */
	private int colorCodescheduledJob;

	/** The color codecompleted job. */
	private int colorCodecompletedJob;

	/** The paint. */
	private Paint paint = new Paint();

	/**
	 * Instantiates a new calander date text view.
	 * 
	 * @param context
	 *            the context
	 */
	public CalanderDateTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Instantiates a new calander date text view.
	 * 
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attrs
	 */
	public CalanderDateTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		paint.setAntiAlias(true);
		paint.setDither(true);
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.CalanderTextViewAttr, 0, 0);

		try {

			STROKE_WIDTH = a
					.getDimension(
							R.styleable.CalanderTextViewAttr_strokeWidthCalanderTextView,
							0);

		} finally {
			a.recycle();
		}
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(STROKE_WIDTH);

	}

	/**
	 * Gets the started job.
	 * 
	 * @return the started job
	 */
	public int getStartedJob() {
		return startedJob;
	}

	/**
	 * Sets the started job.
	 * 
	 * @param startedJob
	 *            the started job
	 * @param colorCodeStartJob
	 *            the color code start job
	 */
	public void setStartedJob(int startedJob, int colorCodeStartJob) {
		this.startedJob = startedJob;
		this.colorCodestartedJob = colorCodeStartJob;
	}

	/**
	 * Gets the suspendedjob.
	 * 
	 * @return the suspendedjob
	 */
	public int getSuspendedjob() {
		return suspendedjob;
	}

	/**
	 * Sets the suspendedjob.
	 * 
	 * @param suspendedjob
	 *            the suspendedjob
	 * @param colorCodeSuspendedJob
	 *            the color code suspended job
	 */
	public void setSuspendedjob(int suspendedjob, int colorCodeSuspendedJob) {
		this.suspendedjob = suspendedjob;
		this.colorCodesuspendedjob = colorCodeSuspendedJob;

	}

	/**
	 * Gets the scheduled job.
	 * 
	 * @return the scheduled job
	 */
	public int getScheduledJob() {
		return scheduledJob;
	}

	/**
	 * Sets the scheduled job.
	 * 
	 * @param scheduledJob
	 *            the scheduled job
	 * @param colorCodeScheduledJob
	 *            the color code scheduled job
	 */
	public void setScheduledJob(int scheduledJob, int colorCodeScheduledJob) {
		this.scheduledJob = scheduledJob;
		this.colorCodescheduledJob = colorCodeScheduledJob;
	}

	/**
	 * Gets the completed job.
	 * 
	 * @return the completed job
	 */
	public int getCompletedJob() {
		return completedJob;
	}

	/**
	 * Sets the completed job.
	 * 
	 * @param completedJob
	 *            the completed job
	 * @param colorCodeCompletedJob
	 *            the color code completed job
	 */
	public void setCompletedJob(int completedJob, int colorCodeCompletedJob) {
		this.completedJob = completedJob;
		this.colorCodecompletedJob = colorCodeCompletedJob;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.TextView#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub

		super.onMeasure(widthMeasureSpec, widthMeasureSpec);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.TextView#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		int startY = this.getMeasuredWidth();
		int stopY = startY;
		int widthView = this.getMeasuredWidth();
		int strokeWidth = (int) STROKE_WIDTH;
		paint.setColor(colorCodestartedJob);
		//
		// canvas.drawLine(0, startY-10, widthView,stopY-10, paint);
		// canvas.drawLine(0, startY-20, widthView,stopY-20, paint);
		// canvas.drawLine(0, startY-30, widthView,stopY-30, paint);
		//
		for (int i = 0; i < startedJob; i++) {
			canvas.drawLine(0, startY, widthView, stopY, paint);
			startY = startY - strokeWidth;
			stopY = startY;
		}

		paint.setColor(colorCodecompletedJob);
		for (int i = 0; i < completedJob; i++) {
			canvas.drawLine(0, startY, widthView, stopY, paint);
			startY = startY - strokeWidth;
			stopY = startY;
		}

		paint.setColor(colorCodesuspendedjob);
		for (int i = 0; i < suspendedjob; i++) {
			canvas.drawLine(0, startY, widthView, stopY, paint);
			startY = startY - strokeWidth;
			stopY = startY;
		}

		paint.setColor(colorCodescheduledJob);
		for (int i = 0; i < scheduledJob; i++) {
			canvas.drawLine(0, startY, widthView, stopY, paint);
			startY = startY - strokeWidth;
			stopY = startY;
		

		}
	

		canvas.save();
		super.onDraw(canvas);

	}

}
