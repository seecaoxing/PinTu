package com.see.game.view;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.see.game.game_pintu.R;
import com.see.game.utils.ImagePiece;
import com.see.game.utils.ImageSplitterUtil;

public class GamePintuLayout extends RelativeLayout implements OnClickListener {

	private int mColumn = 3;
	/**
	 * �������ڱ߾�
	 */
	private int mPadding;
	/**
	 * ÿ��Сͼ֮��ľ���(��,��) dp
	 */
	private int mMargin = 3;

	/**
	 * ��Ϸ���Ŀ��
	 */
	private int mWidth;

	private ImageView[] mGamePintuItems;

	private int mItemWidth;

	/**
	 * ��Ϸ��ͼƬ
	 */
	private Bitmap mBitmap;

	private List<ImagePiece> mItemBitmaps;

	private boolean once;

	private final static int TIME_CHANGED = 0x110;
	private final static int NEXT_LEVEL = 0x111;

	public GamePintuListener mListener;

	private boolean isGameSuccess;

	private boolean isGameOver;
	private boolean isTimeEnabled = false;
	
	private int mTime;

	/**
	 * �����Ƿ���ʱ��
	 * 
	 * @param isTimeEnabled
	 */
	public void setTimeEnabled(boolean isTimeEnabled) {

		this.isTimeEnabled = isTimeEnabled;

	}

	/**
	 * ���ýӿڻص�
	 * 
	 * @param mListener
	 */
	public void setOnGamePintuListener(GamePintuListener mListener) {
		
		this.mListener = mListener;
		
	}

	private int level = 1;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case TIME_CHANGED:

				if (isGameSuccess||isGameOver) {
					return;
				}
				
				if (mListener!=null) {
					mListener.timechanged(mTime);
				}
				mTime--;
				break;

			case NEXT_LEVEL:
				level = level+1;
				if (mListener!=null) {
			
					mListener.nextLevel(level);
				  
				}else {
					
					nextLevel();
				
				}
				
				break;
			default:
				break;
			}

		};
	};
	

	public GamePintuLayout(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public GamePintuLayout(Context context, AttributeSet attrs) {

		this(context, attrs, 0);
	}

	public GamePintuLayout(Context context, AttributeSet attrs, int defStyle) {

		super(context, attrs, defStyle);

		init();

	}

	private void init() {

		mMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				3, getResources().getDisplayMetrics());
		mPadding = min(getPaddingLeft(), getPaddingRight(), getPaddingTop(),
				getPaddingBottom());

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		/**
		 * ȡ��͸��е�Сֵ
		 */
		mWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());
		if (!once) {
			// ������ͼ,�Լ�����
			initBitmap();

			// ����ImageView(Item)�Ŀ�ߵ�����
			initItem();

			checkTimeEnable();
			
			once = true;
		}
		setMeasuredDimension(mWidth, mWidth);

	}

	private void checkTimeEnable() {

		if (isTimeEnabled) {
			/**
			 * ���ݵ�ǰ�ȼ�����ʱ��
			 */
			countTimeBaseLevel();
			handler.sendEmptyMessage(TIME_CHANGED);
		}
	}
	private double mLevel;
	
	private void countTimeBaseLevel() {

		mTime = (int) Math.pow(2, mLevel)*60;
	}

	/**
	 * ������ͼ,�Լ�����
	 */
	private void initBitmap() {

		if (mBitmap == null) {
			mBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.test);

		}
		mItemBitmaps = ImageSplitterUtil.splitImage(mBitmap, mColumn);

		// ʹ��sort������ǵ�����
		Collections.sort(mItemBitmaps, new Comparator<ImagePiece>() {

			@Override
			public int compare(ImagePiece a, ImagePiece b) {

				return Math.random() > 0.5 ? 1 : -1;
			}
		});

	}

	/**
	 * ����ImageView(Item)�Ŀ�ߵ�����
	 */
	private void initItem() {

		mItemWidth = (mWidth - mPadding * 2 - mMargin * (mColumn - 1))
				/ mColumn;
		mGamePintuItems = new ImageView[mColumn * mColumn];
		// �������ǵ�item,����Rule
		for (int i = 0; i < mGamePintuItems.length; i++) {
			ImageView item = new ImageView(getContext());
			item.setOnClickListener(this);
			item.setImageBitmap(mItemBitmaps.get(i).getBitmap());
			mGamePintuItems[i] = item;
			item.setId(i + 1);
			// ��Item��tag�д洢��index
			item.setTag(i + "_" + mItemBitmaps.get(i).getIndex());

			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					mItemWidth, mItemWidth);

			// ����item������϶,ͨ��rightMargin
			// �������һ��
			if ((i + 1) % mColumn != 0) {

				lp.rightMargin = mMargin;
			}
			// ���ǵ�һ��
			// ����˭��˭���ұ�
			if (i % mColumn != 0) {
				lp.addRule(RelativeLayout.RIGHT_OF,
						mGamePintuItems[i - 1].getId());
			}

			// ������ǵ�һ��
			// ����item��������
			// ����topMargin��rule
			if ((i + 1) > mColumn) {
				lp.topMargin = mMargin;
				lp.addRule(RelativeLayout.BELOW,
						mGamePintuItems[i - mColumn].getId());
			}

			addView(item, lp);
		}

	}

	/**
	 * ��ȡ�����������Сֵ
	 * 
	 * @param paddingLeft
	 * @param paddingRight
	 * @param paddingTop
	 * @param paddingBottom
	 * @return
	 */
	private int min(int... params) {

		int min = params[0];
		for (int param : params) {
			if (param < min) {
				min = param;
			}
		}

		return min;
	}

	private ImageView mFirst;
	private ImageView mSecond;

	private boolean isAniming = false;

	@Override
	public void onClick(View v) {

		if (isAniming) {
			return;
		}

		/**
		 * ���ε��ͬһ��Item
		 */
		if (mFirst == v) {
			mFirst.setColorFilter(null);
			mFirst = null;
			return;
		}
		if (mFirst == null) {
			mFirst = (ImageView) v;
			// ������ɫ(ǰ����������͸����eg:55)
			mFirst.setColorFilter(Color.parseColor("#55FF0000"));
		} else {
			mSecond = (ImageView) v;
			mFirst.setColorFilter(null);
			// �������ǵ�Item
			exchangeView();
		}

	}

	/**
	 * ������
	 */
	private RelativeLayout mAnimLayout;

	/**
	 * �������ǵ�Item
	 */
	public void exchangeView() {

		setUpAnimLayout();

		ImageView firstAnimImage = new ImageView(getContext());

		final Bitmap firstBitmap = mItemBitmaps.get(
				getImageIdByTag((String) mFirst.getTag())).getBitmap();

		firstAnimImage.setImageBitmap(firstBitmap);

		RelativeLayout.LayoutParams lp1 = new LayoutParams(mItemWidth,
				mItemWidth);
		lp1.leftMargin = mFirst.getLeft() - mPadding;

		lp1.topMargin = mFirst.getTop() - mPadding;

		firstAnimImage.setLayoutParams(lp1);

		mAnimLayout.addView(firstAnimImage);

		ImageView secondAnimImage = new ImageView(getContext());

		final Bitmap secondBitmap = mItemBitmaps.get(
				getImageIdByTag((String) mSecond.getTag())).getBitmap();

		secondAnimImage.setImageBitmap(secondBitmap);

		RelativeLayout.LayoutParams lp2 = new LayoutParams(mItemWidth,
				mItemWidth);

		lp2.leftMargin = mSecond.getLeft() - mPadding;

		lp2.topMargin = mSecond.getTop() - mPadding;

		secondAnimImage.setLayoutParams(lp2);

		mAnimLayout.addView(secondAnimImage);

		// ���ö���
		TranslateAnimation animFirst = new TranslateAnimation(0,
				mSecond.getLeft() - mFirst.getLeft(), 0, mSecond.getTop()
						- mFirst.getTop());

		animFirst.setDuration(300);

		animFirst.setFillAfter(true);

		firstAnimImage.startAnimation(animFirst);

		TranslateAnimation animSecond = new TranslateAnimation(0,
				-mSecond.getLeft() + mFirst.getLeft(), 0, -mSecond.getTop()
						+ mFirst.getTop());

		animSecond.setDuration(300);

		animSecond.setFillAfter(true);

		secondAnimImage.startAnimation(animSecond);

		// ��������
		animFirst.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

				isAniming = true;
				mFirst.setVisibility(View.INVISIBLE);
				mSecond.setVisibility(View.INVISIBLE);

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {

				String firstTag = (String) mFirst.getTag();

				String secondTag = (String) mSecond.getTag();

				mFirst.setImageBitmap(secondBitmap);

				mSecond.setImageBitmap(firstBitmap);

				mFirst.setTag(secondTag);

				mSecond.setTag(firstTag);

				mFirst.setVisibility(View.VISIBLE);

				mSecond.setVisibility(View.VISIBLE);

				mFirst = mSecond = null;

				// �鿴�Ƿ�ɹ�
				checkSuccess();

				mAnimLayout.removeAllViews();

				isAniming = false;
			}

		});

	}

	public void nextLevel() {
		this.removeAllViews();
		mAnimLayout = null;
		mColumn++;
		isGameSuccess = false;
		initBitmap();
		initItem();

	}

	private boolean isSuccess;

	/**
	 * �鿴�Ƿ�ɹ�
	 */
	public void checkSuccess() {
		isSuccess = true;
		for (int i = 0; i < mGamePintuItems.length; i++) {

			if (getImageIndexByTag((String) mGamePintuItems[i].getTag()) == i) {

			} else {

				isSuccess = false;
			}
			System.out.println(getImageIndexByTag((String) mGamePintuItems[i].getTag()));

		}
		System.out.println("-------------------------");
		if (isSuccess) {
			Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
			handler.sendEmptyMessage(NEXT_LEVEL);
		}

	}

	/**
	 * �������ǵĶ�����
	 */
	public void setUpAnimLayout() {

		if (mAnimLayout == null) {
			mAnimLayout = new RelativeLayout(getContext());
			addView(mAnimLayout);
		}
	}

	/**
	 * ����tag��ȡid
	 * 
	 * @param tag
	 * @return
	 */
	public int getImageIdByTag(String tag) {

		String[] TagArray = tag.split("_");
		return Integer.parseInt(TagArray[0]);
	}

	/**
	 * ����tag��ȡindex
	 * 
	 * @param tag
	 * @return
	 */
	public int getImageIndexByTag(String tag) {

		String[] TagArray = tag.split("_");
		return Integer.parseInt(TagArray[1]);
	}

}
