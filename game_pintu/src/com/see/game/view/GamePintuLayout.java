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
	 * 容器的内边距
	 */
	private int mPadding;
	/**
	 * 每张小图之间的距离(横,纵) dp
	 */
	private int mMargin = 3;

	/**
	 * 游戏面板的宽度
	 */
	private int mWidth;

	private ImageView[] mGamePintuItems;

	private int mItemWidth;

	/**
	 * 游戏的图片
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
	 * 设置是否开启时间
	 * 
	 * @param isTimeEnabled
	 */
	public void setTimeEnabled(boolean isTimeEnabled) {

		this.isTimeEnabled = isTimeEnabled;

	}

	/**
	 * 设置接口回调
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
		 * 取宽和高中的小值
		 */
		mWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());
		if (!once) {
			// 进行切图,以及排序
			initBitmap();

			// 设置ImageView(Item)的宽高等属性
			initItem();

			checkTimeEnable();
			
			once = true;
		}
		setMeasuredDimension(mWidth, mWidth);

	}

	private void checkTimeEnable() {

		if (isTimeEnabled) {
			/**
			 * 根据当前等级设置时间
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
	 * 进行切图,以及排序
	 */
	private void initBitmap() {

		if (mBitmap == null) {
			mBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.test);

		}
		mItemBitmaps = ImageSplitterUtil.splitImage(mBitmap, mColumn);

		// 使用sort完成我们的乱序
		Collections.sort(mItemBitmaps, new Comparator<ImagePiece>() {

			@Override
			public int compare(ImagePiece a, ImagePiece b) {

				return Math.random() > 0.5 ? 1 : -1;
			}
		});

	}

	/**
	 * 设置ImageView(Item)的宽高等属性
	 */
	private void initItem() {

		mItemWidth = (mWidth - mPadding * 2 - mMargin * (mColumn - 1))
				/ mColumn;
		mGamePintuItems = new ImageView[mColumn * mColumn];
		// 生成我们的item,设置Rule
		for (int i = 0; i < mGamePintuItems.length; i++) {
			ImageView item = new ImageView(getContext());
			item.setOnClickListener(this);
			item.setImageBitmap(mItemBitmaps.get(i).getBitmap());
			mGamePintuItems[i] = item;
			item.setId(i + 1);
			// 在Item的tag中存储了index
			item.setTag(i + "_" + mItemBitmaps.get(i).getIndex());

			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					mItemWidth, mItemWidth);

			// 设置item间横向间隙,通过rightMargin
			// 不是最后一列
			if ((i + 1) % mColumn != 0) {

				lp.rightMargin = mMargin;
			}
			// 不是第一列
			// 设置谁在谁的右边
			if (i % mColumn != 0) {
				lp.addRule(RelativeLayout.RIGHT_OF,
						mGamePintuItems[i - 1].getId());
			}

			// 如果不是第一行
			// 设置item间纵向间距
			// 设置topMargin和rule
			if ((i + 1) > mColumn) {
				lp.topMargin = mMargin;
				lp.addRule(RelativeLayout.BELOW,
						mGamePintuItems[i - mColumn].getId());
			}

			addView(item, lp);
		}

	}

	/**
	 * 获取多个参数的最小值
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
		 * 两次点击同一个Item
		 */
		if (mFirst == v) {
			mFirst.setColorFilter(null);
			mFirst = null;
			return;
		}
		if (mFirst == null) {
			mFirst = (ImageView) v;
			// 设置颜色(前两个数字是透明度eg:55)
			mFirst.setColorFilter(Color.parseColor("#55FF0000"));
		} else {
			mSecond = (ImageView) v;
			mFirst.setColorFilter(null);
			// 交换我们的Item
			exchangeView();
		}

	}

	/**
	 * 动画层
	 */
	private RelativeLayout mAnimLayout;

	/**
	 * 交换我们的Item
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

		// 设置动画
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

		// 监听动画
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

				// 查看是否成功
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
	 * 查看是否成功
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
	 * 构造我们的动画层
	 */
	public void setUpAnimLayout() {

		if (mAnimLayout == null) {
			mAnimLayout = new RelativeLayout(getContext());
			addView(mAnimLayout);
		}
	}

	/**
	 * 根据tag获取id
	 * 
	 * @param tag
	 * @return
	 */
	public int getImageIdByTag(String tag) {

		String[] TagArray = tag.split("_");
		return Integer.parseInt(TagArray[0]);
	}

	/**
	 * 根据tag获取index
	 * 
	 * @param tag
	 * @return
	 */
	public int getImageIndexByTag(String tag) {

		String[] TagArray = tag.split("_");
		return Integer.parseInt(TagArray[1]);
	}

}
