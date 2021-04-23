package com.puerlink.widgets;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.puerlink.appcommon.R;

public class MessageDialog extends Dialog {

	public MessageDialog(Context context) {
		super(context);
	}

	public MessageDialog(Context context, int theme) {
		super(context, theme);
	}
	
	public static class Builder
	{
		private Context mContext;
		private boolean mCancelable;
		private String mCaption;
		private String mMessage;
		private View mContentView;
		private String mOkText;
		private String mCancelText;
		private OnClickListener mOkListener;
		private OnClickListener mCancelListener;

		private boolean mIsNight = false;

		public Builder(Context context)
		{
			mContext = context;

			mCancelable = true;
			mCaption = (String)mContext.getText(R.string.title_message_dialog);
			mOkText = (String)mContext.getText(R.string.caption_message_dialog_ok_button);
			mCancelText = (String)mContext.getText(R.string.caption_message_dialog_cancel_button);
		}

		public Builder setNightMode(boolean value)
		{
			mIsNight = value;
			return this;
		}

		public Builder setCaption(String caption)
		{
			mCaption = caption;
			return this;
		}

		public Builder setCaption(int captionId)
		{
			mCaption = (String)mContext.getText(captionId);
			return this;
		}

		public Builder setMessage(String message)
		{
			mMessage = message;
			return this;
		}

		public Builder setMessage(int messageId)
		{
			mMessage = (String)mContext.getText(messageId);
			return this;
		}

		public Builder setContentView(View v)
		{
			mContentView = v;
			return this;
		}

		public Builder setCancelable(boolean cancelable)
		{
			mCancelable = cancelable;
			return this;
		}

		public Builder hideOkButton()
		{
			mOkText = null;
			mOkListener = null;
			return this;
		}

		public Builder setOkButton(String text, OnClickListener listener)
		{
			mOkText = text;
			mOkListener = listener;
			return this;
		}

		public Builder setOkButton(int textId, OnClickListener listener)
		{
			mOkText = (String)mContext.getText(textId);
			mOkListener = listener;
			return this;
		}

		public Builder setOkButtonListener(OnClickListener listener)
		{
			mOkListener = listener;
			return this;
		}

		public Builder hideCancelButton()
		{
			mCancelText = null;
			mCancelListener = null;
			return this;
		}

		public Builder setCancelButton(String text, OnClickListener listener)
		{
			mCancelText = text;
			mCancelListener = listener;
			return this;
		}

		public Builder setCancelButton(int textId, OnClickListener listener)
		{
			mCancelText = (String)mContext.getText(textId);
			mCancelListener = listener;
			return this;
		}

		public Builder setCancelButtonListener(OnClickListener listener)
		{
			mCancelListener = listener;
			return this;
		}

		private void updateThemeStyle(View view)
		{
			Context context = view.getContext();

			if (mIsNight)
			{
				view.setBackgroundResource(R.drawable.widget_message_dialog_night);
				((TextView)view.findViewById(R.id.message_dialog_caption))
						.setTextColor(context.getResources().getColorStateList(R.drawable.widget_selector_message_title_text_night));
				view.findViewById(R.id.view_splitline)
						.setBackgroundColor(Color.parseColor("#161A1D"));
				((Button)view.findViewById(R.id.message_dialog_button_ok))
						.setTextColor(context.getResources().getColorStateList(R.drawable.widget_selector_message_ok_button_text_night));
				((Button)view.findViewById(R.id.message_dialog_button_cancel))
						.setTextColor(context.getResources().getColorStateList(R.drawable.widget_selector_message_cancel_button_text_night));
				view.findViewById(R.id.message_dialog_button_split)
						.setBackgroundColor(Color.parseColor("#161A1D"));
			}
			else
			{
				view.setBackgroundResource(R.drawable.widget_message_dialog_day);
				((TextView)view.findViewById(R.id.message_dialog_caption))
						.setTextColor(context.getResources().getColorStateList(R.drawable.widget_selector_message_title_text_day));
				view.findViewById(R.id.view_splitline)
						.setBackgroundColor(Color.parseColor("#C3C3C3"));
				((Button)view.findViewById(R.id.message_dialog_button_ok))
						.setTextColor(context.getResources().getColorStateList(R.drawable.widget_selector_message_ok_button_text_day));
				((Button)view.findViewById(R.id.message_dialog_button_cancel))
						.setTextColor(context.getResources().getColorStateList(R.drawable.widget_selector_message_cancel_button_text_day));
				view.findViewById(R.id.message_dialog_button_split)
						.setBackgroundColor(Color.parseColor("#C3C3C3"));
			}
		}

		public MessageDialog build()
		{
			LayoutInflater li = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			final MessageDialog dialog = new MessageDialog(mContext, R.style.MessageDialog);
			dialog.setCancelable(mCancelable);
			
			View contentView = li.inflate(R.layout.widget_message_dialog, null);
			updateThemeStyle(contentView);
			dialog.addContentView(contentView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			
			//标题
			((TextView)contentView.findViewById(R.id.message_dialog_caption)).setText(mCaption);
			
			//确定按钮
			if (!TextUtils.isEmpty(mOkText))
			{
				Button okButton = ((Button)contentView.findViewById(R.id.message_dialog_button_ok));
				okButton.setText(mOkText);
				if (mOkListener != null)
				{
					okButton.setOnClickListener(new View.OnClickListener() {

						public void onClick(View v) {
							mOkListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
						}
						
					});
				}
				else
				{
					okButton.setOnClickListener(new View.OnClickListener() {

						public void onClick(View v) {
							dialog.cancel();
						}
						
					});
				}

				if (!TextUtils.isEmpty(mCancelText))
				{
					if (mIsNight)
					{
						okButton.setBackgroundResource(R.drawable.widget_selector_message_button_background_night2_left);
					}
					else
					{
						okButton.setBackgroundResource(R.drawable.widget_selector_message_button_background_day2_left);
					}
				}
				else
				{
					if (mIsNight)
					{
						okButton.setBackgroundResource(R.drawable.widget_selector_message_button_background_night);
					}
					else
					{
						okButton.setBackgroundResource(R.drawable.widget_selector_message_button_background_day);
					}
				}
			}
			else
			{
				contentView.findViewById(R.id.message_dialog_button_split).setVisibility(View.GONE);
				contentView.findViewById(R.id.message_dialog_button_ok).setVisibility(View.GONE);
			}
			
			if (mCancelText != null)
			{
				Button cancelButton = ((Button)contentView.findViewById(R.id.message_dialog_button_cancel));
				cancelButton.setText(mCancelText);
				if (mCancelListener != null) 
				{
					cancelButton.setOnClickListener(new View.OnClickListener() {
						
						public void onClick(View v) {
							mCancelListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
						}
						
					});	
				}
				else
				{
					cancelButton.setOnClickListener(new View.OnClickListener() {
						
						public void onClick(View v) {
							dialog.cancel();
						}
						
					});	
				}

				if (!TextUtils.isEmpty(mOkText))
				{
					if (mIsNight)
					{
						cancelButton.setBackgroundResource(R.drawable.widget_selector_message_button_background_night2_right);
					}
					else
					{
						cancelButton.setBackgroundResource(R.drawable.widget_selector_message_button_background_day2_right);
					}
				}
				else
				{
					if (mIsNight)
					{
						cancelButton.setBackgroundResource(R.drawable.widget_selector_message_button_background_night);
					}
					else
					{
						cancelButton.setBackgroundResource(R.drawable.widget_selector_message_button_background_day);
					}
				}
			}
			else
			{
				contentView.findViewById(R.id.message_dialog_button_split).setVisibility(View.GONE);
				contentView.findViewById(R.id.message_dialog_button_cancel).setVisibility(View.GONE);
			}
			
			if (mMessage != null)
			{
				((TextView)contentView.findViewById(R.id.message_dialog_content)).setText(mMessage);
			}
			else
			{
				if (mContentView != null)
				{
					((LinearLayout)contentView.findViewById(R.id.message_dialog_content_container)).removeAllViews();
					((LinearLayout)contentView.findViewById(R.id.message_dialog_content_container)).addView(mContentView, 
							new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				}
			}
			
			dialog.setContentView(contentView);
			
			return dialog;
		}
	}

}
