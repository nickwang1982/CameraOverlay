package me.ideia.cameraoverlay;

import pete.android.study.ConvolutionMatrix;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.view.View;

public class PhotoEffects extends View {

	private static final float[] NEGATIVE = {
		-1,  0,  0, 0, 255,
		0, -1,  0, 0, 255,
		0,  0, -1, 0, 255, 
		0,  0,  0, 1, 0 };
	
	private static float[] HIGH_CONSTRAST = {
		2,  0,  0, 0, 0,
		0, 2,  0, 0, 0,
		0,  0, 2, 0, 0, 
		0,  0,  0, 1, 0 };
	private static double[][] EDGE_DETECT = {	
		{2,3,-3},
		{1,-1,1},
		{-2,1,-2}};
	private static float[] CONTRAST_BW={
		1,  1,  1, 0, 0,
		1, 1,  1, 0, 0,
		1,  1, 1, 0, 0, 
		0,  0,  0, 1, 0 };
	static private float[] GRAY_SCALE={
		0.3f, 0.3f, 0.3f, 0, 0,
		0.3f, 0.3f, 0.3f, 0, 0,
		0.3f, 0.3f, 0.3f, 0, 0, 
		0,  0,  0, 1, 0 };
	private static float[] HUE1={
		0, 1, 0, 0, 0,
		0, 0, 1, 0, 0,
		1, 0, 0, 0, 0, 
		0, 0, 0, 1, 0};
	private static float[] HUE2 ={
		0,  0,  1, 0, 0,
		1,  0,  0, 0, 0,
		0,  1,  0, 0, 0, 
		0,  0,  0, 1, 0 };


	private Bitmap bmp = null;
	private Bitmap bmpOriginal = null;
	private int alpha = 128;
	private int height = 0;
	private int width = 0;
	private int vertical = 100;
	private int horizontal = 100;
	private int grid = 100;
	private boolean inverted = false;
	private boolean pictureNotSelected = true;
	private SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
	public void selectedPicture() {
		pictureNotSelected = false;
	}
	public boolean withoutPicture() {
		return pictureNotSelected;
	}

	public PhotoEffects(Context context) {
		super(context);
		invalidate();
	}

	public void setBitmap(Bitmap bitmap) {
		if (height > 0 && width > 0) {

			Bitmap resized = resizeBitmap(bitmap);
			bmp = resized;
			bmpOriginal = resized;
			inverted = false;
			
		}
		invalidate();
	}
	public Bitmap resizeBitmap(Bitmap bitmap) {
		Bitmap resized;
		if (bitmap.getHeight() > bitmap.getWidth()) {
			float curScaleX;
			//float curScaleY;
			curScaleX = width / bitmap.getHeight();
			//curScaleY = height / bitmap.getWidth();

			Matrix matrix = new Matrix();
			matrix.postScale(curScaleX, curScaleX);
			matrix.postRotate(-90);

			resized = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);
		} else {
			resized = Bitmap.createScaledBitmap(bitmap, width, height,
					true);
		}
		return resized;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		height = canvas.getHeight();
		width = canvas.getWidth();
		if (bmp == null) {
			setBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.image));
			pictureNotSelected  = true;
		} else {
			int top = -(bmp.getHeight() - height) / 2;
		    canvas.drawBitmap(bmp, 0, top, getPaint());
			super.onDraw(canvas);
		}
	}

	public void setAlpha(int progress) {
		this.alpha = progress;
		invalidate();

	}
	public Paint getPaint() {
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);

		paint.setAlpha(this.alpha);
		paint.setFilterBitmap(true);
		return paint;
	}

	public Bitmap grid( Bitmap source, int size ) {
		if (size < 1)
			size = 1;
		size = size * source.getWidth() / 100;
		Bitmap result = Bitmap.createBitmap( source.getWidth(), source.getHeight(), Config.ARGB_8888 );
		for( int x = 0; x < source.getWidth(); x++ ) {
			for( int y = 0; y < source.getHeight(); y++ ) {
				if (((int)(x / size) % 2 == 1) != ((int)(y / size) % 2 == 1)) {
					int c = Color.argb(0, 0, 0, 0);
					result.setPixel( x, y, c );
				} else {
					result.setPixel( x, y, source.getPixel(x, y) );
				}
			}
		}

		return result;
	}

	public Bitmap horizontal( Bitmap source, int size ) {
		if (size < 1)
			size = 1;
		size = size * source.getWidth() / 100;
		Bitmap result = Bitmap.createBitmap( source.getWidth(), source.getHeight(), Config.ARGB_8888 );
		for( int x = 0; x < source.getWidth(); x++ ) {
			for( int y = 0; y < source.getHeight(); y++ ) {
				if (((int)(y / size) % 2 == 1)) {
					int c = Color.argb(0, 0, 0, 0);
					result.setPixel( x, y, c );
				} else {
					result.setPixel( x, y, source.getPixel(x, y) );
				}
			}
		}

		return result;
	}

	public Bitmap vertical( Bitmap source, int size ) {
		if (size < 1)
			size = 1;
		size = size * source.getWidth() / 100;
		Bitmap result = Bitmap.createBitmap( source.getWidth(), source.getHeight(), Config.ARGB_8888 );
		for( int x = 0; x < source.getWidth(); x++ ) {
			for( int y = 0; y < source.getHeight(); y++ ) {
				if (((int)(x / size) % 2 == 1)) {
					int c = Color.argb(0, 0, 0, 0);
					result.setPixel( x, y, c );
				} else {
					result.setPixel( x, y, source.getPixel(x, y) );
				}
			}
		}

		return result;
	}

	public Bitmap convertToNegative(Bitmap sampleBitmap){
		inverted = !inverted;
		return applyMatrix(sampleBitmap, new ColorMatrix(), PhotoEffects.NEGATIVE);
	}

	public Bitmap highContrast(Bitmap sampleBitmap){
		ColorMatrix matrix =new ColorMatrix();


		return applyMatrix(sampleBitmap, matrix, HIGH_CONSTRAST);
	}

	public Bitmap contrastBW(Bitmap sampleBitmap){
		return applyMatrix(sampleBitmap, new ColorMatrix(), CONTRAST_BW);
	}

	public Bitmap grayScale(Bitmap sampleBitmap){
		return applyMatrix(sampleBitmap, new ColorMatrix(),	GRAY_SCALE);
	}

	public Bitmap hue1(Bitmap sampleBitmap){
		return applyMatrix(sampleBitmap, new ColorMatrix(), HUE1);
	}

	public Bitmap hue2(Bitmap sampleBitmap){
		return applyMatrix(sampleBitmap, new ColorMatrix(), HUE2);
	}

	public Bitmap alpha1(Bitmap source){
		Bitmap result = Bitmap.createBitmap( source.getWidth(), source.getHeight(), Config.ARGB_8888 );
		for( int x = 0; x < source.getWidth(); x++ ) {
			for( int y = 0; y < source.getHeight(); y++ ) {
				int argb = source.getPixel(x, y);
				int c = Color.argb(Color.red(argb), 0, Color.green(argb), Color.blue(argb));
				result.setPixel( x, y, c );
			}
		}

		return result;
	}

	public Bitmap alpha2(Bitmap source){
		Bitmap result = Bitmap.createBitmap( source.getWidth(), source.getHeight(), Config.ARGB_8888 );
		for( int x = 0; x < source.getWidth(); x++ ) {
			for( int y = 0; y < source.getHeight(); y++ ) {
				int argb = source.getPixel(x, y);
				int c = Color.argb(Color.green(argb), Color.red(argb), 0, Color.blue(argb));
				result.setPixel( x, y, c );
			}
		}

		return result;
	}

	public Bitmap alpha3(Bitmap source){
		Bitmap result = Bitmap.createBitmap( source.getWidth(), source.getHeight(), Config.ARGB_8888 );
		for( int x = 0; x < source.getWidth(); x++ ) {
			for( int y = 0; y < source.getHeight(); y++ ) {
				int argb = source.getPixel(x, y);
				int c = Color.argb(Color.blue(argb), Color.red(argb), Color.green(argb), 0);
				result.setPixel( x, y, c );
			}
		}

		return result;
	}

	public Bitmap alpha4(Bitmap source){
		return alpha(source, 255,255,255);
	}

	public Bitmap alpha5(Bitmap source){
		return alpha(source, 0, 0, 0);
	}
	public Bitmap alpha(Bitmap source, int r, int g, int b){
		Bitmap result = Bitmap.createBitmap( source.getWidth(), source.getHeight(), Config.ARGB_8888 );
		for( int x = 0; x < source.getWidth(); x++ ) {
			for( int y = 0; y < source.getHeight(); y++ ) {
				int argb = source.getPixel(x, y);
				int c = Color.argb(255 - (Color.blue(argb) + Color.red(argb) + Color.green(argb)) / 3, r, g, b);
				result.setPixel( x, y, c );
			}
		}

		return result;
	}

	private Bitmap applyMatrix(Bitmap sampleBitmap, ColorMatrix matrix,
			float[] negMat) {
		matrix.set(negMat);
		final ColorMatrixColorFilter colorFilter= new ColorMatrixColorFilter(matrix);
		Bitmap rBitmap = sampleBitmap.copy(Bitmap.Config.ARGB_8888, true);
		Paint paint=new Paint();
		paint.setColorFilter(colorFilter);
		Canvas myCanvas =new Canvas(rBitmap);
		myCanvas.drawBitmap(rBitmap, 0, 0, paint);
		return rBitmap;
	}

	public void edgeDetect() {
		ConvolutionMatrix cm = new ConvolutionMatrix();
		cm.applyConfig(EDGE_DETECT);
		bmp = cm.computeConvolution3x3(currentImage());
		invalidate();
	}

	public void edgeDetectTransparent() {
		edgeDetect();
	    alpha4();
		invalidate();
	}

	public void invert() {
		bmp = convertToNegative(bmp);
		invalidate();
	}

	public void resetEffect() {
		bmp = bmpOriginal;
		invalidate();
	}

	public void highContrast() {
		bmp = highContrast(currentImage());
		invalidate();
	}
	
	public  void contrastBW() {
		bmp = contrastBW(currentImage());
		invalidate();
	}

	public void alpha1() {
		bmp = alpha1(currentImage());
		invalidate();
	}
	
	public void alpha2() {
		bmp = alpha2(currentImage());
		invalidate();
	}
	public void alpha3() {
		bmp = alpha3(currentImage());
		invalidate();
	}
	public void alpha4() {
		bmp = alpha4(currentImage());
		invalidate();
	}
	public void alpha5() {
		bmp = alpha5(currentImage());
		invalidate();
	}
	public void grayScale() {
		bmp = grayScale(currentImage());
		invalidate();
	}
	public void hue1() {
		bmp = hue1(currentImage());
		invalidate();
	}
	public void hue2() {
		bmp = hue2(currentImage());
		invalidate();
	}
	private Bitmap currentImage() {
		
		return preferences.getBoolean("mix_effects", false) ? bmp : bmpOriginal;
	}
	public void grid(int size) {
		grid = size;
		bmp = grid(bmpOriginal, size);
		invalidate();
	}
	public void vertical(int size) {
		vertical = size;
		bmp = vertical(bmpOriginal, size);
		invalidate();
	}
	public void horizontal(int size) {
		horizontal = size;
		bmp = horizontal(bmpOriginal, size);
		invalidate();
	}

	public int getAlphaValue() {
		return alpha;
	}

	public int getGrid() {
		return grid;
	}

	public int getHorizontal() {
		return horizontal;
	}

	public int getVertical() {
		return vertical;
	}

	public boolean isInverted() {
		return inverted;
	}
	public Bitmap getBmp() {
		return bmp;
	}

}
