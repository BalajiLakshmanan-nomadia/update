package com.synchroteam.listadapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.synchroteam.beans.Photo_Pda;
import com.synchroteam.dao.Dao;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.technicalsupport.JobDetails;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * Adapter Class to for Attachment List. created for future purpose
 * 
 * @author Ideavate.solution
 */
public class AttachmentJobsListAdapter extends BaseAdapter {

	/** The context. */
	private JobDetails context;
	
	/** The photo. */
	
	/** The pho pdas. */
	private ArrayList<Photo_Pda> phoPdas;
	
	/** The data access object. */
	private Dao dataAccessObject;
	
	/** The height. */
	
	/** The inflater. */
	private LayoutInflater inflater;
	
	
	
	/** The cd_status. */
	

	
	

	
	/**
	 * Instantiates a new attachment jobs list adapter.
	 *
	 * @param context the context
	 * @param photo_Pdas the photo_ pdas
	 * @param dataAccessObject the data access object
	 */
	public AttachmentJobsListAdapter(JobDetails context,
			ArrayList<Photo_Pda> photo_Pdas, Dao dataAccessObject) {
		this.context = context;
		this.phoPdas = photo_Pdas;
		this.dataAccessObject = dataAccessObject;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		
		
		//
		// Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
		// R.drawable.default_image);
		// this.width=bitmap.getWidth();
		// this.height=bitmap.getHeight();

	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View row = inflater.inflate(R.layout.layout_attachement_list_tem,
				parent, false);

		final int pos=position;
		RelativeLayout containerAttachment=(RelativeLayout) row.findViewById(R.id.containerAttachment);
		final Photo_Pda photo_Pda = (Photo_Pda) getItem(position);
		byte[] mPhoto = photo_Pda.getPhoto();

		ImageView img = (ImageView) row.findViewById(R.id.attachmentIv);

//		Bitmap bitmap;
//		BitmapFactory.Options opt = new BitmapFactory.Options();
//		opt.inDither = true;
//		opt.inPreferredConfig = Bitmap.Config.RGB_565;
//		bitmap = BitmapFactory.decodeByteArray(mPhoto, 0, mPhoto.length, opt);
////		 bmd = new BitmapDrawable(resizeBitmap(bitmap,newwidth,newheight));
//		img.setImageBitmap(bitmap);

		//new changes
		Glide.with(context)
				.load(mPhoto)
				.asBitmap()
				.override(200, 200)
				.fitCenter()
				.placeholder(R.drawable.library_iicon)
				.into(img);

//		 img.setImageDrawable(bmd);

		final TextView label = (TextView) row
				.findViewById(R.id.attachmentCommentEt);
			label.setText(photo_Pda.getCommentaire());
			


			containerAttachment.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(context.getUpdatedValueOfStatus()==KEYS.JObDetail.JOB__STARTED){
				modifyComment(photo_Pda.getIdPhoto(),label.getText().toString(),photo_Pda.getPhoto(),pos);
				notifyDataSetChanged();
				}
			}
		});
//		containerAttachment.setLongClickable(true);
		
		containerAttachment.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				Logger.log("SetOnClickListner", "Called");
				if(context.getUpdatedValueOfStatus()==KEYS.JObDetail.JOB__STARTED){
				 deleteAttachment(photo_Pda,pos);
				}
				
				
				return false;
				
				
				
				
				
			}
		});

		
		return row;
	}

	/**
	 * Modify comment.
	 *
	 * @param idPh the id ph
	 * @param cmtr the cmtr
	 * @param photo the photo
	 * @param pos the pos
	 */
	protected void modifyComment(final String idPh,final String cmtr, byte[] photo,final int pos) {
		// TODO Auto-generated method stub
		
	AlertDialog.Builder	adb = new AlertDialog.Builder(context);
		
	View	alertDialogView = inflater.inflate(R.layout.dialogphoto, null);
	adb.setView(alertDialogView);
	final EditText commentEt=(EditText) alertDialogView.findViewById(R.id.commentaire);
	if((context).getUpdatedValueOfStatus() == 3)
	{
		adb.setNeutralButton(R.string.modifier, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which)
			{
				
				String cm = String.valueOf(commentEt.getText());
				dataAccessObject.modifierPhotoById(idPh,cm);
				phoPdas.get(pos).setString(cm);
				notifyDataSetChanged();
				
			} });
	}
	adb.setNegativeButton(R.string.textCancelLable, new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			//remplir();
		}
	});

	adb.show();
		
		
		
		
		
	}

	/**
	 * Resize bitmap.
	 *
	 * @param bitmap the bitmap
	 * @param i the i
	 * @param j the j
	 * @return the bitmap
	 */
	public Bitmap resizeBitmap(Bitmap bitmap, int i, int j) {
		Bitmap resizedBitmap;
		int width, height, newWidth, newHeight;
		float scaleWidth, scaleHeight;
		Matrix matrix;
		width = bitmap.getWidth();
		height = bitmap.getHeight();
		newWidth = i;
		newHeight = j;
		scaleWidth = ((float) newWidth) / width;
		scaleHeight = ((float) newHeight) / height;
		matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, i, j, matrix, true);
		return (resizedBitmap);
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return phoPdas.size();
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return phoPdas.get(position);
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	/**
	 * Delete attachment.
	 *
	 * @param photo_Pda the photo_ pda
	 * @param pos the pos
	 */
	protected void deleteAttachment(final Photo_Pda photo_Pda, final int pos) {
		// TODO Auto-generated method stub
		AlertDialog.Builder adbC = new AlertDialog.Builder(context);
		adbC.setMessage(R.string.txt_confirm);
		adbC.setPositiveButton(R.string.textYesLable,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dataAccessObject.deletePhoto(photo_Pda.getIdPhoto());
                        phoPdas.remove(pos);						
						notifyDataSetChanged();
						
						
					}
				});
		adbC.setNegativeButton(R.string.textNoLable,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		adbC.show();
	}
	
	
	
	
}
