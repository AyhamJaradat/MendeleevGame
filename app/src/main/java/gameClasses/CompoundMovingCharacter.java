package gameClasses;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import java.util.Random;

import imagesSingleton.CompondMovingImage;
import utils.Constants;

/**
 * Created by Ayham on 1/17/2019.
 */

public class CompoundMovingCharacter extends GameCharacter implements Constants {
    // one rwo based on movement direction from top to butom

    // Velocity of game character ( percentage of pixel/millisecond)
    public float VELOCITY = 0.0002f;
    private Paint paint = new Paint();
    private float[] velocityPerLevel = {0.0002f, 0.00025f, 0.0003f, 0.00035f, 0.0004f, 0.00045f, 0.0005f, 0.00055f};
    // Row index of Image are being used.
    private int rowUsing = 0;
    private int colUsing = 0;
    // bitmap array for all direction
    private Bitmap image;
    private Bitmap hintImageBitmap;
    private float movingVectorX = 10.01f;// 0;
    private float movingVectorY = 0.01f;
    private int canvasHeight, canvasWidth;
    private long lastDrawMsTime = -1;
    private String type;
    private String symbol;
    private Paint symbolPaint = new Paint();
    private int counter = 0;
    private boolean isDone = false;
    private GameSurface gameSurface;
    private long fallingObjectTime = -1;
    //    private boolean amIFire;
    private int difficultyLevel;
    private Random generator;
    private boolean isHintCharacter;
    private int hitCounter = 0;
    private CompondMovingImage charImage;

    private int symbolFontSize = 12;//38;
    private int symbolsubFontSize = 11;//38;
    private Typeface skranji_reg_font;
    private Typeface skranji_bold_font;
    private float screenDensity;
    private float fHeight1;


    public CompoundMovingCharacter(GameSurface gameSurface, Bitmap image, int rowCount, int colCount, int x, int y, String type, int diffLevel, String symbol,boolean isHint) {
        super(rowCount, colCount, x, y);

        this.type = type;
        charImage = CompondMovingImage.getInstance(image, rowCount, colCount);
        updateImageInfo(charImage.getTotalWidth(), charImage.getTotalHeight());
        this.image = charImage.getImages();
        screenDensity = gameSurface.getResources().getDisplayMetrics().density;
        this.symbol = symbol;
        this.gameSurface = gameSurface;


        this.difficultyLevel = diffLevel;
        this.isHintCharacter = isHint;

        if(this.isHintCharacter){
            symbolFontSize=10;
            symbolsubFontSize=9;
        }else{
            paint.setAlpha(140);
        }

        this.canvasHeight = this.gameSurface.getHeight();
        this.canvasWidth = this.gameSurface.getWidth();
        generator = new Random();
        skranji_reg_font = Typeface.createFromAsset(gameSurface.getContext().getAssets(), "fonts/Skranji-Regular.ttf");
        skranji_bold_font = Typeface.createFromAsset(gameSurface.getContext().getAssets(), "fonts/Skranji-Bold.ttf");
        // decide moving directions based on x ,y
        if (x < 50) {
            // start from left
            movingVectorX = 0.1f + generator.nextFloat();
            if (y < this.canvasHeight / 2) {
                // goes down
                movingVectorY = 0.1f + generator.nextFloat();
            } else {
                // goes up
                movingVectorY = -1 * (0.1f + generator.nextFloat());
            }
        } else {
            setX(this.gameSurface.getWidth() - width);
            // start from right
            movingVectorX = -1 * (0.1f + generator.nextFloat());
            if (y < this.canvasHeight / 2) {
                // goes down
                movingVectorY = 0.1f + generator.nextFloat();
            } else {
                // goes up
                movingVectorY = -1 * (0.1f + generator.nextFloat());
            }
        }
        if (this.y > this.canvasHeight - height) {
            setY(this.canvasHeight - height);
        }

        VELOCITY = velocityPerLevel[this.difficultyLevel] * this.canvasHeight;
        movingVectorY = movingVectorY * this.canvasHeight;
        movingVectorX = movingVectorX * this.canvasWidth;

//        symbolPaint.setColor(Color.parseColor("#E98F14"));
        symbolPaint.setColor(Color.parseColor("#E38810"));
        symbolPaint.setTextSize(symbolFontSize * screenDensity);
        symbolPaint.setShadowLayer(2f, 0f, 0f, 0xFF000000);
        symbolPaint.setTypeface(skranji_bold_font);
//        symbolPaint.setAlpha(200);



        Paint.FontMetrics fHeight = symbolPaint.getFontMetrics();
         fHeight1 = fHeight.descent - fHeight.ascent;


    }

    /**
     * function to get the current image from the array of current moving bitmaps
     *
     * @return
     */
    public Bitmap getCurrentMoveBitmap() {
        return this.image;
    }

    public void update() {
        // update moving images
        this.counter++;


        // Current time in Millisseconds
        long now = System.currentTimeMillis();

        // Never once did draw.
        if (lastDrawMsTime == -1) {
            lastDrawMsTime = now;
        }
        // get delta time since last frame
        int deltaTime = (int) ((now - lastDrawMsTime));

        // Distance moves
        float distance = VELOCITY * deltaTime;
        double movingVectorLength = Math.sqrt(movingVectorX * movingVectorX + movingVectorY * movingVectorY);
        // Calculate the new position of the game character.
        setX(x + (int) (distance * movingVectorX / movingVectorLength));
        setY(y + (int) (distance * movingVectorY / movingVectorLength));
        // Make sure character does not go out of the screen
        boolean isXDirChanged = false, isYDirChanged = false;
        if (this.x < 0) {
//            this.x = 0;
            setX(0);
            this.movingVectorX = Math.abs(this.movingVectorX);
            isXDirChanged = true;

        } else if (this.x > this.gameSurface.getWidth() - width) {
//            this.x = this.gameSurface.getWidth() - width;
            setX(this.gameSurface.getWidth() - width);
            this.movingVectorX = -1 * Math.abs(this.movingVectorX);
            isXDirChanged = true;

        }
        if (this.y < 0) {
//            this.y = 0;
            setY(0);
            this.movingVectorY = Math.abs(this.movingVectorY);
            isYDirChanged = true;

        } else if (this.y > this.canvasHeight - height) {
//            this.y = this.canvasHeight - height;
            setY(this.canvasHeight - height);
            this.movingVectorY = -1 * Math.abs(this.movingVectorY);
            isYDirChanged = true;
        }
        if (isYDirChanged || isXDirChanged) {
            hitCounter++;
        }
        // keep time to find delta between frams
        lastDrawMsTime = System.currentTimeMillis();
    }


    public void draw(Canvas canvas) {
        if (fallingObjectTime == -1) {
            fallingObjectTime = System.currentTimeMillis();
        }
        long now = System.currentTimeMillis();

        if (this.isHintCharacter) {
            canvas.drawBitmap(this.hintImageBitmap, x, y, paint);
        } else {
            canvas.drawBitmap(this.image, x, y, paint);
        }

        //TODO: draw symbol
        float symbolWidth = symbolPaint.measureText(symbol);
        if(this.isHintCharacter){
            if(symbol.length() == 3){
                //CO3 ,SO4,
                String firstPart = symbol.substring(0,2);
                String secondPart = symbol.substring(2,3);
                symbolPaint.setTextSize((symbolFontSize-1) * screenDensity);
                canvas.drawText(firstPart,x+(this.hintImageBitmap.getWidth()-symbolWidth)/2,y+fHeight1/3+this.hintImageBitmap.getHeight()/2,symbolPaint);
                float w= symbolPaint.measureText(firstPart);
                symbolPaint.setTextSize((symbolsubFontSize-1) * screenDensity);
                canvas.drawText(secondPart,w+x+(this.hintImageBitmap.getWidth()-symbolWidth)/2,y-symbolPaint.ascent()/2+fHeight1/3+this.hintImageBitmap.getHeight()/2,symbolPaint);
            }else{
                canvas.drawText(symbol,x+(this.hintImageBitmap.getWidth()-symbolWidth)/2,y+fHeight1/3+this.hintImageBitmap.getHeight()/2,symbolPaint);
            }

        }else{

            if(symbol.length() == 3){
                //CO3 ,SO4,
                String firstPart = symbol.substring(0,2);
                String secondPart = symbol.substring(2,3);
                symbolPaint.setTextSize((symbolFontSize-1) * screenDensity);
                canvas.drawText(firstPart,x+(this.image.getWidth()-symbolWidth)/2,y+fHeight1/3+this.image.getHeight()/2,symbolPaint);
                float w= symbolPaint.measureText(firstPart);
                symbolPaint.setTextSize((symbolsubFontSize-1) * screenDensity);
                canvas.drawText(secondPart,w+x+(this.image.getWidth()-symbolWidth)/2,y-symbolPaint.ascent()/2+fHeight1/3+this.image.getHeight()/2,symbolPaint);
            }else{
                canvas.drawText(symbol,x+(this.image.getWidth()-symbolWidth)/2,y+fHeight1/3+this.image.getHeight()/2,symbolPaint);
            }

        }


    }


    public String getType() {
        return type;
    }

    public void setMovingVector(int movingVectorX, int movingVectorY) {
        this.movingVectorX = movingVectorX;
        this.movingVectorY = movingVectorY;
    }

    public String getSymbol() {
        return symbol;
    }

    public void resizeImage(int length) {
            this.hintImageBitmap = charImage.resizeImage(length);
            setWidth(this.hintImageBitmap.getWidth());
            setHeight(this.hintImageBitmap.getHeight());
    }

    public void updateAlpha(boolean isWithAlpha) {
        paint.setAlpha(120);
        symbolPaint.setAlpha(200);
    }
}

