package com.viomi.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

/**
 * 带文字圆形进度条
 * Created by William on 2018/2/1.
 */

public class ProgressWheel extends View {

    public final static int CHANGE_TYPE_INCREASE = 0;//递增
    public final static int CHANGE_TYPE_DECREASE = 1;//递减
    // Sizes (with defaults)
    private int layout_height = 0;
    private int layout_width = 0;
    private int fullRadius = 100;
    private int circleRadius = 80;
    private int barLength = 60;
    private int barWidth = 20;
    private int rimWidth = 20;
    private int textSize = 20;
    private int unitTextSize = 20;

    // Padding (with defaults)
    private int paddingTop = 5;
    private int paddingBottom = 5;
    private int paddingLeft = 5;
    private int paddingRight = 5;

    // Colors (with defaults)
    private int barColor = 0xAA000000;
    private int circleColor = 0x00000000;
    private int rimColor = 0xAADDDDDD;
    private int textColor = 0xFF000000;

    // Paints
    private Paint barPaint = new Paint();
    private Paint circlePaint = new Paint();
    private Paint rimPaint = new Paint();
    private Paint textPaint = new Paint();
    private Paint unitTextPaint = new Paint();

    // Rectangles
    @SuppressWarnings("unused")
    private RectF rectBounds = new RectF();
    private RectF circleBounds = new RectF();

    // Animation
    // The amount of pixels to move the bar by on each draw
    private int spinSpeed = 2;
    // The number of milliseconds to wait inbetween each draw
    private int delayMillis = 0;

    int progress = 0;
    boolean isSpinning = false;
    private String text = "";
    private String uint = "%";//单位
    private Context context;


    private Handler spinHandler = new Handler() {
        /**
         * This is the code that will increment the progress variable and so
         * spin the wheel
         */
        @Override
        public void handleMessage(Message msg) {
            invalidate();
            if (isSpinning) {
                progress += spinSpeed;
                if (progress > 360) {
                    progress = 0;
                }
                spinHandler.sendEmptyMessageDelayed(0, delayMillis);
            }
            // super.handleMessage(msg);
        }
    };

    /**
     * The constructor for the ProgressWheel
     *
     * @param context
     * @param attrs
     */
    public ProgressWheel(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        parseAttributes(context.obtainStyledAttributes(attrs, R.styleable.ProgressWheel));
    }

    // ----------------------------------
    // Setting up stuff
    // ----------------------------------

    /**
     * Use onSizeChanged instead of onAttachedToWindow to get the dimensions of
     * the view, because this method is called after measuring the dimensions of
     * MATCH_PARENT & WRAP_CONTENT. Use this dimensions to setup the bounds and
     * paints.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Share the dimensions
        layout_width = w;
        layout_height = h;

        setupBounds();
        setupPaints();
        invalidate();
    }

    /**
     * Set the properties of the paints we're using to draw the progress wheel
     */
    private void setupPaints() {
        barPaint.setColor(barColor);
        barPaint.setAntiAlias(true);
        barPaint.setStyle(Paint.Style.STROKE);
        barPaint.setStrokeWidth(barWidth);

        rimPaint.setColor(rimColor);
        rimPaint.setAntiAlias(true);
        rimPaint.setStyle(Paint.Style.STROKE);
        rimPaint.setStrokeWidth(rimWidth);

        circlePaint.setColor(circleColor);
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.FILL);

        textPaint.setColor(textColor);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/DINCond-Medium.otf"));

        unitTextPaint.setColor(textColor);
        unitTextPaint.setStyle(Paint.Style.FILL);
        unitTextPaint.setAntiAlias(true);
        unitTextPaint.setTextSize(unitTextSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    /**
     * Set the bounds of the component
     */
    private void setupBounds() {
        // Width should equal to Height, find the min value to steup the circle
        int minValue = Math.min(layout_width, layout_height);

        // Calc the Offset if needed
        int xOffset = layout_width - minValue;
        int yOffset = layout_height - minValue;

        // Add the offset
        paddingTop = this.getPaddingTop() + (yOffset / 2);
        paddingBottom = this.getPaddingBottom() + (yOffset / 2);
        paddingLeft = this.getPaddingLeft() + (xOffset / 2);
        paddingRight = this.getPaddingRight() + (xOffset / 2);

        rectBounds = new RectF(paddingLeft, paddingTop,
                this.getLayoutParams().width - paddingRight, this.getLayoutParams().height
                - paddingBottom);

        circleBounds = new RectF(paddingLeft + barWidth, paddingTop + barWidth,
                this.getLayoutParams().width - paddingRight - barWidth,
                this.getLayoutParams().height - paddingBottom - barWidth);

        fullRadius = (this.getLayoutParams().width - paddingRight - barWidth) / 2;
        circleRadius = (fullRadius - barWidth) + 1;
    }

    /**
     * Parse the attributes passed to the view from the XML
     *
     * @param a the attributes to parse
     */
    private void parseAttributes(TypedArray a) {
        barWidth = (int) a.getDimension(R.styleable.ProgressWheel_barWidth, barWidth);

        rimWidth = (int) a.getDimension(R.styleable.ProgressWheel_rimWidth, rimWidth);

        spinSpeed = (int) a.getDimension(R.styleable.ProgressWheel_spinSpeed, spinSpeed);

        delayMillis = (int) a.getInteger(R.styleable.ProgressWheel_delayMillis, delayMillis);
        if (delayMillis < 0) {
            delayMillis = 0;
        }

        barColor = a.getColor(R.styleable.ProgressWheel_barColor, barColor);
        barLength = (int) a.getDimension(R.styleable.ProgressWheel_barLength, barLength);
        textSize = (int) a.getDimension(R.styleable.ProgressWheel_textSize, textSize);
        unitTextSize = (int) a.getDimension(R.styleable.ProgressWheel_unitTextSize, unitTextSize);
        textColor = (int) a.getColor(R.styleable.ProgressWheel_textColor, textColor);

        // if the text is empty , so ignore it
        if (a.hasValue(R.styleable.ProgressWheel_text)) {
            setText(a.getString(R.styleable.ProgressWheel_text));
        }

        rimColor = (int) a.getColor(R.styleable.ProgressWheel_rimColor, rimColor);

        circleColor = (int) a.getColor(R.styleable.ProgressWheel_circleColor, circleColor);

        // Recycle
        a.recycle();
    }

    // ----------------------------------
    // Animation stuff
    // ----------------------------------
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Draw the rim
        canvas.drawArc(circleBounds, 360, 360, false, rimPaint);
        // Draw the bar
        if (isSpinning) {
            canvas.drawArc(circleBounds, progress - 90, barLength, false, barPaint);
        } else {
            canvas.drawArc(circleBounds, -90, progress, false, barPaint);
        }
        // Draw the inner circle
        canvas.drawCircle((circleBounds.width() / 2) + rimWidth + paddingLeft,
                (circleBounds.height() / 2) + rimWidth + paddingTop, circleRadius, circlePaint);

        // Draw the text
        float offset = unitTextPaint.measureText(uint) / 2;
        canvas.drawText(text, this.getWidth() / 2 - offset, this.getHeight() / 2 + (textSize / 2), textPaint);
        offset = textPaint.measureText(text) / 2;
        canvas.drawText(uint, this.getWidth() / 2 + offset, this.getHeight() / 2 + (textSize / 2), unitTextPaint);

    }

    /**
     * Reset the count (in increment mode)
     */
    public void resetCount() {
        progress = 0;
        setText("0");
        invalidate();
    }

    /**
     * Turn off spin mode
     */
    public void stopSpinning() {
        isSpinning = false;
        progress = 0;
        spinHandler.removeMessages(0);
    }

    /**
     * Puts the view on spin mode
     */
    public void spin() {
        isSpinning = true;
        spinHandler.sendEmptyMessage(0);
    }

    /**
     * Increment the progress by 1 (of 360)
     */
    public void incrementProgress() {
        isSpinning = false;
        progress++;
        setText(Math.round(((float) progress / 360) * 100) + "");
        spinHandler.sendEmptyMessage(0);
    }

    /**
     * Set the progress to a specific value
     *
     * @param type 递增或递减 {@link #CHANGE_TYPE_INCREASE,#CHANGE_TYPE_DECREASE}
     */
    public void setProgress(int i, int type) {
        if (type == CHANGE_TYPE_INCREASE) {
            setText("" + i);
        } else {
            setText("" + (100 - i));
        }
        isSpinning = false;
        progress = (int) ((((float) i) * 360) / 100);
        spinHandler.sendEmptyMessage(0);
    }

    /**
     * Set the progress to a specific value
     */
    public void setProgress(int i) {

        setText("" + i);
        isSpinning = false;
        progress = (int) ((((float) i) * 360) / 100);
        spinHandler.sendEmptyMessage(0);
    }

    public void setProgressColor(int color) {
        rimPaint.setColor(color);
        textPaint.setColor(color);
        unitTextPaint.setColor(color);
        barPaint.setColor(barColor);
        invalidate();
    }

    public void setFlueColor(int color) {
        rimPaint.setColor(color);
        textPaint.setColor(color);
        unitTextPaint.setColor(color);
        barPaint.setColor(color);
        invalidate();
    }

    // ----------------------------------
    // Getters + setters
    // ----------------------------------

    /**
     * Set the text in the progress bar Doesn't invalidate the view
     *
     * @param text the text to show ('\n' constitutes a new line)
     */
    public void setText(String text) {
        this.text = text;
    }

    public int getCircleRadius() {
        return circleRadius;
    }

    public void setCircleRadius(int circleRadius) {
        this.circleRadius = circleRadius;
    }

    public int getBarLength() {
        return barLength;
    }

    public void setBarLength(int barLength) {
        this.barLength = barLength;
    }

    public int getBarWidth() {
        return barWidth;
    }

    public void setBarWidth(int barWidth) {
        this.barWidth = barWidth;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public int getPaddingTop() {
        return paddingTop;
    }

    public void setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
    }

    public int getPaddingBottom() {
        return paddingBottom;
    }

    public void setPaddingBottom(int paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    public int getPaddingLeft() {
        return paddingLeft;
    }

    public void setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    public int getPaddingRight() {
        return paddingRight;
    }

    public void setPaddingRight(int paddingRight) {
        this.paddingRight = paddingRight;
    }

    public int getBarColor() {
        return barColor;
    }

    public void setBarColor(int barColor) {
        this.barColor = barColor;
    }

    public int getCircleColor() {
        return circleColor;
    }

    public void setCircleColor(int circleColor) {
        this.circleColor = circleColor;
    }

    public int getRimColor() {
        return rimColor;
    }

    public void setRimColor(int rimColor) {
        this.rimColor = rimColor;
    }

    public Shader getRimShader() {
        return rimPaint.getShader();
    }

    public void setRimShader(Shader shader) {
        this.rimPaint.setShader(shader);
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getSpinSpeed() {
        return spinSpeed;
    }

    public void setSpinSpeed(int spinSpeed) {
        this.spinSpeed = spinSpeed;
    }

    public int getRimWidth() {
        return rimWidth;
    }

    public void setRimWidth(int rimWidth) {
        this.rimWidth = rimWidth;
    }

    public int getDelayMillis() {
        return delayMillis;
    }

    public void setDelayMillis(int delayMillis) {
        this.delayMillis = delayMillis;
    }
}