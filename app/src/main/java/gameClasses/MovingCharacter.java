package gameClasses;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Random;

import imagesSingleton.MovingElectronImage;
import imagesSingleton.MovingPowerImage;
import utils.Constants;

/**
 * Created by Ayham on 12/21/2018.
 */

public class MovingCharacter extends GameCharacter implements Constants {

    // one rwo based on movement direction from top to butom
    private static final int ROW_TOP_TO_BOTTOM = 0;
    // Velocity of game character ( percentage of pixel/millisecond)
    public float VELOCITY = 0.0002f;
    Paint paint = new Paint();
    private float[] velocityPerLevel = {0.0002f, 0.00025f, 0.0003f, 0.0004f, 0.0005f, 0.00055f, 0.0006f, 0.00065f};
    // Row index of Image are being used.
    private int rowUsing = ROW_TOP_TO_BOTTOM;
    private int colUsing;
    // bitmap array for all direction
    private Bitmap[] topToBottoms;
    private Bitmap hintImageBitmap;
    private float movingVectorX = 10.01f;// 0;
    private float movingVectorY = 0.01f;
    private int canvasHeight, canvasWidth;
    private long lastDrawMsTime = -1;
    private String type;
    private int counter = 0;
    private boolean isDone = false;
    private GameSurface gameSurface;
    private long fallingObjectTime = -1;
    //    private boolean amIFire;
    private int difficultyLevel;
    private Random generator;
    private int hitCounter = 0;
    private boolean isHintCharacter;
    private MovingElectronImage electronImage;
    private MovingPowerImage powerImage;
    private int diyAfter;


    public MovingCharacter(GameSurface gameSurface, Bitmap image, int rowCount, int colCount, int x, int y, String type, int diffLevel, boolean isHint) {
        super(rowCount, colCount, x, y);

        this.type = type;
        if (this.type.equalsIgnoreCase(ELECTRON_TYPE)) {


            electronImage = MovingElectronImage.getInstance(image, rowCount, colCount);
            updateImageInfo(electronImage.getTotalWidth(), electronImage.getTotalHeight());
            this.topToBottoms = electronImage.getImages();
        } else {
            powerImage = MovingPowerImage.getInstance(image, rowCount, colCount);
            updateImageInfo(powerImage.getTotalWidth(), powerImage.getTotalHeight());
            this.topToBottoms = powerImage.getImages();
        }
        this.isHintCharacter = isHint;
        this.gameSurface = gameSurface;


        this.difficultyLevel = diffLevel;
        switch (difficultyLevel) {
            case 1:
            case 2:
                diyAfter = 3;
                break;
            case 3:
            case 4:
            case 5:
                diyAfter = 5;
                break;
            case 6:
            case 7:
                diyAfter = 6;
                break;
        }
        this.canvasHeight = this.gameSurface.getHeight();
        this.canvasWidth = this.gameSurface.getWidth();
        generator = new Random();
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
//        if (this.difficultyLevel == 1 || this.difficultyLevel == 2 || this.difficultyLevel == 3) {
//            VELOCITY = VELOCITY * this.canvasHeight;
//            movingVectorY = movingVectorY * this.canvasHeight;
//            movingVectorX = movingVectorX * this.canvasWidth;
//        } else if (this.difficultyLevel == 4) {
//            VELOCITY = 0.0004f * this.canvasHeight;
//            movingVectorY = movingVectorY * this.canvasHeight;
//            movingVectorX = movingVectorX * this.canvasWidth;
//        } else {
//            VELOCITY = 0.0006f * this.canvasHeight;
//            movingVectorY = movingVectorY * this.canvasHeight;
//            movingVectorX = movingVectorX * this.canvasWidth;
//        }
        // for every direction , create the movement images and add them to the array of the movement
//        this.topToBottoms = new Bitmap[colCount]; // 3
//        for (int col = 0; col < this.colCount; col++) {
//            this.topToBottoms[col] = this.createSubImageAt(ROW_TOP_TO_BOTTOM, col);
//        }


    }


    /**
     * function to get the array of bitmaps for the current direction
     *
     * @return
     */
    public Bitmap[] getMoveBitmaps() {
        switch (rowUsing) {
            case ROW_TOP_TO_BOTTOM:
                return this.topToBottoms;
            default:
                return null;
        }
    }

    /**
     * function to get the current image from the array of current moving bitmaps
     *
     * @return
     */
    public Bitmap getCurrentMoveBitmap() {
        Bitmap[] bitmaps = this.getMoveBitmaps();
        return bitmaps[this.colUsing];
    }

    public void update() {
        // update moving images
        this.counter++;
        if (this.counter % 10 == 0) {
            this.colUsing++;
            if (colUsing >= this.colCount) {
                this.colUsing = 0;
            }
        }

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
//        this.x = x + (int) (distance * movingVectorX / movingVectorLength);
//        this.y = y + (int) (distance * movingVectorY / movingVectorLength);
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
            this.movingVectorX = -1*Math.abs(this.movingVectorX);
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
            this.movingVectorY = -1*Math.abs(this.movingVectorY);
            isYDirChanged = true;
        }
        if (isYDirChanged || isXDirChanged) {
            hitCounter++;
        }

        if (hitCounter == diyAfter - 1) {
            paint.setAlpha(150);
        } else if (hitCounter >= diyAfter) {
            isDone = true; // character ready to diy
        }
        // keep time to find delta between frams
        lastDrawMsTime = System.currentTimeMillis();
    }


    public void draw(Canvas canvas) {
        if (fallingObjectTime == -1) {
            fallingObjectTime = System.currentTimeMillis();
        }
        // change trancperancy based on time passed
//        int trancparancy = 255;
        long now = System.currentTimeMillis();
//        if (this.isMultiplayer) {
//            if (!this.amIFire && this.type.equals(FIRE_TYPE) || this.amIFire && this.type.equals(WATER_TYPE)) {
//                float yDestancePerc = (float) y / this.canvasHeight;
//
////            trancparancy = getTrancparancyBasedOnLevel(this.difficultyLevel, yDestancePerc);
//                if (yDestancePerc > 0.15) {
//                    trancparancy = 0;
//                }
//
//            }
//        }


//        if(this.isHintCharacter){
//            trancparancy = 120;
//        }

        if (this.isHintCharacter) {
            canvas.drawBitmap(this.hintImageBitmap, x, y, paint);
        } else {
            canvas.drawBitmap(this.getCurrentMoveBitmap(), x, y, paint);
        }


    }

    private int getTrancparancyBasedOnLevel(int difficultyLevel, float yDestancePerc) {
        int tranc = 255;
        switch (difficultyLevel) {
            case 1: // always visible
                tranc = 255;
                break;
            case 2:
                if (yDestancePerc > 0.6) {
                    tranc = 80;
                } else if (yDestancePerc > 0.4) {
                    tranc = 140;
                }
                break;
            case 3:
                if (yDestancePerc > 0.5) {
                    tranc = 0;
                } else if (yDestancePerc > 0.35) {
                    tranc = 100;
                }
                break;
            case 4:
            case 5:
                if (yDestancePerc > 0.4) {
                    tranc = 0;
                } else if (yDestancePerc > 0.3) {
                    tranc = 100;
                }
                break;

        }

        return tranc;

    }


    public boolean isReachGround() {
        return isDone;
    }

    public String getType() {
        return type;
    }

    public void setMovingVector(int movingVectorX, int movingVectorY) {
        this.movingVectorX = movingVectorX;
        this.movingVectorY = movingVectorY;
    }


    public void resizeImage(int length) {

        if (this.type.equalsIgnoreCase(POWER_TYPE)) {
            this.hintImageBitmap = powerImage.resizeImage(length, 0);
            setWidth(this.hintImageBitmap.getWidth());
            setHeight(this.hintImageBitmap.getHeight());


        } else {
            this.colUsing = 1;
//            this.topToBottoms[1] = Bitmap.createScaledBitmap
//                    (this.topToBottoms[1], length, length, false);
            this.hintImageBitmap = electronImage.resizeImage(length, 1);
            setWidth(this.hintImageBitmap.getWidth());
            setHeight(this.hintImageBitmap.getHeight());
        }
    }

    public void updateAlpha(boolean isWithAlpha) {
        paint.setAlpha(120);
    }
}
