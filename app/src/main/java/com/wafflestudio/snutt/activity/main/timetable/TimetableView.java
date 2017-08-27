package com.wafflestudio.snutt.activity.main.timetable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.activity.main.MainActivity;
import com.wafflestudio.snutt.data.Lecture;
import com.wafflestudio.snutt.dialog.CustomLectureDialog;
import com.wafflestudio.snutt.util.App;
import com.wafflestudio.snutt.util.TimetableUtil;

public class TimetableView extends View {
	public static TimetableView mInstance;
	Paint backgroundPaint;
	Paint linePaint, topLabelTextPaint, leftLabelTextPaint, leftLabelTextPaint2;
	Paint[] lecturePaint, lectureBorderPaint; 
	Paint lectureTextPaint;
	MainActivity mContext;
	String[] wdays;
	float leftLabelWidth = App.dpTopx(60);
	float topLabelHeight = App.dpTopx(30);
	float unitWidth, unitHeight;
	TextRect lectureTextRect;
	
	//사용자 정의 시간표 추가
	int mCustomWday = -1;
	float mCustomDuration = -1;
	float mCustomStartTime = -1;
	Paint mCustomPaint = new Paint();

	public TimetableView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = (MainActivity) context;
		mInstance = this;
		init();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		unitHeight = (getHeight() - topLabelHeight) / 26f;
		unitWidth = (getWidth() - leftLabelWidth) / 6;
		invalidate();
	}

	void init(){
		setDrawingCacheEnabled(true);
		backgroundPaint = new Paint();
		backgroundPaint.setColor(0xfff3f3f3);

		linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		linePaint.setColor(0xffcccccc);
		linePaint.setStrokeWidth(App.dpTopx(1));

		topLabelTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		topLabelTextPaint.setColor(0xff000000);
		topLabelTextPaint.setTextSize(App.spTopx(12));
		topLabelTextPaint.setTextAlign(Align.CENTER);
		topLabelTextPaint.setFakeBoldText(true);

		leftLabelTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		leftLabelTextPaint.setFakeBoldText(true);
		leftLabelTextPaint.setColor(0xff000000);
		leftLabelTextPaint.setTextSize(App.spTopx(14));
		leftLabelTextPaint.setTextAlign(Align.CENTER);
		leftLabelTextPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
		leftLabelTextPaint2.setColor(0xff000000);
		leftLabelTextPaint2.setTextSize(App.spTopx(10));
		leftLabelTextPaint2.setTextAlign(Align.CENTER);

		wdays = new String[6];
		wdays[0] = mContext.getResources().getString(R.string.wday_mon);
		wdays[1] = mContext.getResources().getString(R.string.wday_tue);
		wdays[2] = mContext.getResources().getString(R.string.wday_wed);
		wdays[3] = mContext.getResources().getString(R.string.wday_thu);
		wdays[4] = mContext.getResources().getString(R.string.wday_fri);
		wdays[5] = mContext.getResources().getString(R.string.wday_sat);

		lecturePaint = new Paint[7];
		lectureBorderPaint = new Paint[lecturePaint.length];
		for (int i=0;i<lecturePaint.length;i++){
			lecturePaint[i] = new Paint();
			lectureBorderPaint[i] = new Paint();
		}
		lecturePaint[0].setColor(0xffffffff);
		lecturePaint[1].setColor(0xffffcccc);
		lecturePaint[2].setColor(0xffffffcc);
		lecturePaint[3].setColor(0xffccffcc);
		lecturePaint[4].setColor(0xffccffff);
		lecturePaint[5].setColor(0xffccccff);
		lecturePaint[6].setColor(0xffffccff);
		lectureBorderPaint[0].setColor(0xffcccccc);
		lectureBorderPaint[1].setColor(0xffffaaaa);
		lectureBorderPaint[2].setColor(0xffffffaa);
		lectureBorderPaint[3].setColor(0xffaaffaa);
		lectureBorderPaint[4].setColor(0xffaaffff);
		lectureBorderPaint[5].setColor(0xffaaaaff);
		lectureBorderPaint[6].setColor(0xffffaaff);


		lectureTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		lectureTextPaint.setColor(0xff000000);
		lectureTextPaint.setTextSize(App.spTopx(11));

		lectureTextRect = new TextRect(lectureTextPaint);
	}


	float getTextWidth(String text, Paint paint){
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, text.length(), bounds);
		return bounds.width();
	}
	float getTextHeight(String text, Paint paint){
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, text.length(), bounds);
		return bounds.height();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event){
		float x = event.getX();
		float y = event.getY();
		
		if (event.getAction() == MotionEvent.ACTION_DOWN){
			//x, y를 요일, 교시로 
			int wday = (int) ((x - leftLabelWidth) / unitWidth);
			float time = ((int) ((y - topLabelHeight) / unitHeight)) / 2f;

			boolean lectureClicked = false;
			//터치한 게 selectedLecture이면
			if (Lecture.selectedLecture != null && Lecture.selectedLecture.contains(wday, time)){
				lectureClicked = true;
			}
			//터치한 게 내 강의 중 하나
			for (int i=0;i<Lecture.myLectures.size();i++){
				if (Lecture.myLectures.get(i).contains(wday, time)){
					lectureClicked = true;
				}
			}
			//빈 공간 클릭
			if (lectureClicked == false && mContext.getCustomEditable()){
				mCustomStartTime = time;
				mCustomWday = wday;
				mCustomDuration = 0.5f;
			} else {
				resetCustomVariables();
			}
			invalidate();

		} else if (event.getAction() == MotionEvent.ACTION_MOVE){
			//x, y를 교시로 
			float time = ((int) ((y - topLabelHeight) / unitHeight)) / 2f;
			
			if (mCustomStartTime != -1 && mCustomWday != -1){
				float duration = time - mCustomStartTime + 0.5f;
				Lecture tmpLecture = new Lecture("", "", mCustomWday, mCustomStartTime, duration);
				if (!tmpLecture.alreadyExistClassTime() && mContext.getCustomEditable()){
					mCustomDuration = duration;
				}
			}
			
			invalidate();
		} else if (event.getAction() == MotionEvent.ACTION_UP){
			//x, y를 요일, 교시로 
			int wday = (int) ((x - leftLabelWidth) / unitWidth);
			float time = ((int) ((y - topLabelHeight) / unitHeight)) / 2f;

			boolean lectureClicked = false;
			//터치한 게 selectedLecture이면 강의 추가
			if (Lecture.selectedLecture != null && Lecture.selectedLecture.contains(wday, time)){
				Lecture.addMyLecture(mContext, Lecture.selectedLecture);
				lectureClicked = true;
			}
			//터치한 게 내 강의 중 하나
			for (int i=0;i<Lecture.myLectures.size();i++){
				if (Lecture.myLectures.get(i).contains(wday, time)){
					lectureClicked = true;
					Lecture.myLectures.get(i).setNextColor();
					invalidate();
					Lecture.saveMyLectures();
				}
			}
			//빈 공간 클릭 시선택 해제
			if (lectureClicked == false){
				Lecture.selectedLecture = null;
				if (TimetableView.mInstance != null)
					TimetableView.mInstance.invalidate();
				if (MainActivity.mSearchAdapter != null)
					MainActivity.mSearchAdapter.notifyDataSetChanged();
			}
			
			if (mCustomWday != -1 && mCustomStartTime != -1 && mCustomDuration > 0 ){
				if (mContext.getCustomEditable()){
					new CustomLectureDialog(mContext, mCustomWday, mCustomStartTime, mCustomDuration).show();
				}
			}
			
			resetCustomVariables();
			invalidate();
		}

		return true;
	}
	
	public void resetCustomVariables(){
		mCustomStartTime = mCustomDuration = mCustomWday = -1;
		invalidate();
	}
	
	public Handler shareHandler = new Handler(){
		public void handleMessage(Message msg) {
			String path = (String) msg.obj;
			Uri uri = Uri.parse("file://" + path);
			// share
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("image/png");
			intent.putExtra(Intent.EXTRA_STREAM, uri);
			mContext.startActivity(Intent.createChooser(intent, App.getAppContext().getResources().getString(R.string.share_image)));

			// scan file
			MediaScannerConnection.scanFile(mContext, new String[]{path}, null, new OnScanCompletedListener() {
				@Override
				public void onScanCompleted(String path, Uri uri) {
					Log.i("ExternalStorage", "Scanned " + path + ":");
					Log.i("ExternalStorage", "-> uri=" + uri);
				}
			});

			Toast.makeText(mContext, path + " saved!", Toast.LENGTH_LONG).show();
		}
	};

	//이미지 저장
	public void saveImage(){
		DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
		final int bitmapWidth = metrics.widthPixels;
		final int bitmapHeight = metrics.heightPixels;
		
		new Thread(){
			@SuppressLint("SimpleDateFormat")
			@Override
			public void run(){
				try {
					Bitmap resultBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
					Canvas canvas = new Canvas(resultBitmap);
					canvas.drawRect(0, 0, bitmapWidth, bitmapHeight, backgroundPaint);
					drawTimetable(canvas, bitmapWidth, bitmapHeight, true);
					
					//폴더 생성
					File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/snutt/");
					dir.mkdirs();
					//파일 이름 결정
					SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd_HHmmss");
					String filename = s.format(new Date());
					File file = new File(dir.getPath() + "/" + filename + ".png");
					//저장
					resultBitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));
					
					Message msg = new Message();
					msg.obj = file.getPath();
					shareHandler.sendMessage(msg);
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}.start();
		//이미지 저장
	}

	//주어진 canvas에 시간표를 그림
	public void drawTimetable(Canvas canvas, int canvasWidth, int canvasHeight, boolean export){
		float unitHeight = (canvasHeight - topLabelHeight) / 26f;
		float unitWidth = (canvasWidth - leftLabelWidth) / 6;
		
		//가로 줄 28개
		canvas.drawLine(0, 0, canvasWidth, 0, linePaint);
		canvas.drawLine(0, canvasHeight, canvasWidth, canvasHeight, linePaint);
		for (int i=0;i<26;i++){
			float height = topLabelHeight + unitHeight * i;
			if (i%2 == 1)
				canvas.drawLine(leftLabelWidth, height, canvasWidth, height, linePaint);
			else
				canvas.drawLine(0, height, canvasWidth, height, linePaint);
		}
		//세로 줄 그리기
		for (int i=0;i<6;i++){
			float width = leftLabelWidth + unitWidth * i;
			float textHeight = getTextHeight(wdays[0], topLabelTextPaint);
			canvas.drawLine(width, 0, width, canvasHeight, linePaint);
			canvas.drawText(wdays[i], (leftLabelWidth + unitWidth * (i+0.5f)), (topLabelHeight+textHeight)/2f, topLabelTextPaint);
		}
		canvas.drawLine(0, 0, 0, canvasHeight, linePaint);
		canvas.drawLine(canvasWidth, 0, canvasWidth, canvasHeight, linePaint);
		//교시 텍스트 그리기
		for (int i=0;i<13;i++){
			String str1 = i + getResources().getString(R.string.class_time);
			String str2 = TimetableUtil.zeroStr(i+8) + ":00~" + TimetableUtil.zeroStr(i+9) + ":00";
			float textHeight = getTextHeight(str1, leftLabelTextPaint);
			float textHeight2 = getTextHeight(str2, leftLabelTextPaint2);
			float padding = App.dpTopx(5);
			if (canvasWidth > canvasHeight) padding = 0;
			float height = topLabelHeight + unitHeight * (i * 2 + 1) + (textHeight + textHeight2 + padding) / 2f;
			canvas.drawText(str1, leftLabelWidth/2f, height - textHeight2 - padding, leftLabelTextPaint);
			canvas.drawText(str2, leftLabelWidth/2f, height, leftLabelTextPaint2);
		}
		//내 강의 그리기
		for (int i=0;i<Lecture.myLectures.size();i++){
			drawLecture(canvas, canvasWidth, canvasHeight, Lecture.myLectures.get(i), Lecture.myLectures.get(i).colorIndex);
		}
		if (!export){
			//현재 선택한 강의 그리기
			if (Lecture.selectedLecture != null && Lecture.myLectures.indexOf(Lecture.selectedLecture) == -1){
				drawLecture(canvas, canvasWidth, canvasHeight, Lecture.selectedLecture, 0);
			}
		}
		
		//사용자 정의 시간표 추가중..
		if (mCustomStartTime != -1 && mCustomWday != -1 && mCustomDuration > 0){
			drawCustomBox(canvas, canvasWidth, canvasHeight, mCustomWday, mCustomStartTime, mCustomDuration);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		drawTimetable(canvas, getWidth(), getHeight(), false);
	}

	void drawLecture(Canvas canvas, float canvasWidth, float canvasHeight, Lecture lecture, int colorIndex){
		//class_time : 월(1.5-1.5)/수(1.5-1.5)
		String[] classStr = lecture.class_time.split("/");
		for (int i=0;i<classStr.length;i++){
			String str = classStr[i];
			//str : 월(1.5-1.5)
			int wday;
			float startTime, duration;
			if (str.trim().length() == 0) continue;
			String[] str1 = str.split("\\(");
			String[] str2 = str1[1].split("\\)");
			String[] str3 = str2[0].split("-");
			wday = TimetableUtil.wdayToNumber(str1[0]);
			startTime = Float.parseFloat(str3[0]);
			duration = Float.parseFloat(str3[1]);

			String[] locations = lecture.location.split("/");
			String location = "";
			if (i < locations.length) {
				location = locations[i];
			}
			drawClass(canvas, canvasWidth, canvasHeight, lecture.course_title, location, wday, startTime, duration, colorIndex);
		}
	}

	//사각형 하나를 그림
	void drawClass(Canvas canvas, float canvasWidth, float canvasHeight, String course_title, String location, int wday, float startTime, float duration, int colorIndex){
		float unitHeight = (canvasHeight - topLabelHeight) / 26f;
		float unitWidth = (canvasWidth - leftLabelWidth) / 6;
		
		//startTime : 시작 교시
		float left = leftLabelWidth + wday * unitWidth;
		float right = leftLabelWidth + wday * unitWidth + unitWidth;
		float top = topLabelHeight + startTime * unitHeight * 2;
		float bottom = topLabelHeight + startTime * unitHeight * 2 + (unitHeight * duration * 2);
		float borderWidth = App.dpTopx(3);
		canvas.drawRect(left, top, right, bottom, lectureBorderPaint[colorIndex]);
		canvas.drawRect(left+borderWidth, top+borderWidth, right-borderWidth, bottom-borderWidth, lecturePaint[colorIndex]);
		//강의명, 강의실 기록
		String str = course_title + "\n" + location;
		int width = (int)(right - left);
		int height = (int)(bottom - top);
		int strHeight = lectureTextRect.prepare(str, width, height);
		lectureTextRect.draw(canvas, (int)left, (int)(top + (height - strHeight)/2), width);
	}
	
	//사용자 정의 시간표용..
	void drawCustomBox(Canvas canvas, float canvasWidth, float canvasHeight, int wday, float startTime, float duration){
		if (!mContext.getCustomEditable()) return;
		
		float unitHeight = (canvasHeight - topLabelHeight) / 26f;
		float unitWidth = (canvasWidth - leftLabelWidth) / 6;
		
		//startTime : 시작 교시
		float left = leftLabelWidth + wday * unitWidth;
		float right = leftLabelWidth + wday * unitWidth + unitWidth;
		float top = topLabelHeight + startTime * unitHeight * 2;
		float bottom = topLabelHeight + startTime * unitHeight * 2 + (unitHeight * duration * 2);
		float borderWidth = App.dpTopx(3);
		
		mCustomPaint.setColor(Color.RED);
		mCustomPaint.setStyle(Paint.Style.STROKE);
		mCustomPaint.setStrokeWidth(borderWidth);
		mCustomPaint.setPathEffect(new DashPathEffect(new float[] {App.dpTopx(6),App.dpTopx(3)}, 0));
		
//		canvas.drawRect(left, top, right, bottom, mCustomPaint);
		canvas.drawRect(left+borderWidth/2, top+borderWidth/2, right-borderWidth/2, bottom-borderWidth/2, mCustomPaint);
	}


}