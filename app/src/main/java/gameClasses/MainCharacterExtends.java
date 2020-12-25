package gameClasses;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import chemestryClasses.DataHolder;
import chemestryClasses.Element;

/**
 * Created by Ayham on 12/29/2018.
 */

public class MainCharacterExtends extends GameCharacter {
    // index of rwo based on movement direction
    private static final int ROW_HAPPY = 0;
    private static final int ROW_SAD = 1;

    private static final int COULUMN_STRAGIT = 0;
    private static final int COULUMN_RIGHT = 1;
    private static final int COULUMN_LEFT = 2;
    private static final int COULUMN_UP = 3;
    private static final int COULUMN_DOWEN = 4;

    // Row index of Image are being used.
    private int rowUsing = ROW_HAPPY;
    private int colUsing = COULUMN_STRAGIT;
    // bitmap array for all direction
    private Bitmap[] happyImages;
    private Bitmap[] sadImages;
    //    private Bitmap[] topToBottoms;
//    private Bitmap[] bottomToTops;
    // Velocity of game character (percantage of pixel/millisecond)
    private float VELOCITY = 0.0004f;
    private float movingVectorX = 0f;
    private float movingVectorY = 0f;
    private long lastDrawMsTime = -1;
    private int counter = 0;

    private String type;
    private String myId;

    private GameSurface gameSurface;
    private int canvasHeight;
    private int canvasWidth;

    private int lives;
    private Paint mainCharPaint;
    private Element element;
    // these two will changes
    private int numOfCurrentElctrons;
    private int numOfNeededElctrons;
    private int totalNumOfHappyElectrons;
    //    private float angle = 0;
    private float referenceAngle = 0;

    private int[] elecReferenceAngleSpeed ={ 14 ,12, 10, 8, 6, 4,2};
    private int[] elecReferenceAngle = new int[7];

    private int elecShellRadius;
    private int elecRadius;

    // paintes
    private int symbolFontSize = 14;//38;
    private int atomicFontSize = 8;//26;

    private float screenDensity;

    float symolPaintHeight ;
    float atomicNumPaintHeight;

    private Paint symolPaint = new Paint();
    private Paint electronPaint = new Paint();
    private Paint electronAlphPaint = new Paint();
    private Paint eShellpaint1 = new Paint();
    private Paint eShellpaint2 = new Paint();
    private Paint atomicNumPaint = new Paint();
    private Paint atomicNumPaint2 = new Paint();

    private int happyColor;
    private int sadColor;

    private boolean isHintCharacter;
    private boolean isHintHappyCharacter;

    private boolean isAlreadyHappy;
    private Typeface skranji_bold_font;


    public MainCharacterExtends(GameSurface gameSurface, Bitmap image, int x, int y, int rows, int columns, int level, int hintCharacter) {
        super(image, rows/*number of rows */, columns/*number of columns*/, x, y);


        this.gameSurface = gameSurface;
        this.element = DataHolder.getInstance().getElementByLevel(level);
        this.numOfCurrentElctrons = element.getNumOfElecInLastShell();
        this.numOfNeededElctrons = this.element.getNumOfNeededElectrons();
        this.isAlreadyHappy = this.numOfNeededElctrons == 0 ? true : false;
        screenDensity = gameSurface.getResources().getDisplayMetrics().density;
        // decide row usage
        this.rowUsing = this.numOfNeededElctrons == 0 ? ROW_HAPPY : ROW_SAD;

        if (hintCharacter == 0) {
            this.isHintCharacter = false;
            elecShellRadius =(int) (10*screenDensity);
            elecRadius = (int) (3.5 * screenDensity);
        } else {
            this.isHintCharacter = true;
            elecShellRadius = (int) (7*screenDensity);//20;
            elecRadius = (int) (2.5 * screenDensity);
            if (hintCharacter == 1) {
                // Happy hint
                this.isHintHappyCharacter = true;
                this.rowUsing = ROW_HAPPY;
                this.numOfCurrentElctrons += this.numOfNeededElctrons;
            } else if (hintCharacter == 2) {
                // Sad hint
                this.isHintHappyCharacter = false;
                this.rowUsing = ROW_SAD;
            }
        }

        this.totalNumOfHappyElectrons = this.numOfCurrentElctrons + this.numOfNeededElctrons;

        this.canvasHeight = this.gameSurface.getHeight();
        this.canvasWidth = this.gameSurface.getWidth();
        skranji_bold_font = Typeface.createFromAsset(gameSurface.getContext().getAssets(), "fonts/Skranji-Bold.ttf");

        this.lives = 2;
        mainCharPaint = new Paint();


        // for every direction , create the movement images and add them to the array of the movement
        this.happyImages = new Bitmap[colCount]; // 3
        this.sadImages = new Bitmap[colCount]; // 3
        for (int col = 0; col < this.colCount; col++) {
            this.happyImages[col] = this.createSubImageAt(ROW_HAPPY, col);
            this.sadImages[col] = this.createSubImageAt(ROW_SAD, col);
        }


        happyColor = Color.WHITE;
        sadColor = Color.rgb(252, 93, 93); //Color.rgb(250,68,4);

        symolPaint.setTextSize(symbolFontSize* screenDensity);
        symolPaint.setTypeface(skranji_bold_font);
        Paint.FontMetrics fm = symolPaint.getFontMetrics();
        symolPaintHeight = fm.descent - fm.ascent;

        eShellpaint1.setColor(Color.WHITE); // Text Color
        eShellpaint1.setStyle(Paint.Style.STROKE);

        // haleh
        eShellpaint2.setColor(Color.WHITE); // Text Color
        eShellpaint2.setStyle(Paint.Style.STROKE);
        eShellpaint2.setStrokeWidth(elecShellRadius);
        eShellpaint2.setAlpha(35);//0-255:50
        electronPaint.setColor(Color.rgb(13, 195, 249));
        electronAlphPaint.setColor(Color.argb(160, 13, 195, 249));

        atomicNumPaint.setColor(Color.WHITE);
        atomicNumPaint.setShadowLayer(2f, 0f, 0f, 0xFF000000);
        atomicNumPaint.setTypeface(skranji_bold_font);
        atomicNumPaint.setTextSize(atomicFontSize* screenDensity);
        atomicNumPaint2.setColor(Color.argb(150, 0, 0, 0));
        atomicNumPaint2.setTypeface(skranji_bold_font);
        atomicNumPaint2.setStyle(Paint.Style.STROKE);
        atomicNumPaint2.setStrokeWidth(1);
        atomicNumPaint2.setTextSize(atomicFontSize* screenDensity);
        Paint.FontMetrics fm2 = atomicNumPaint.getFontMetrics();
        atomicNumPaintHeight = fm2.descent - fm2.ascent;

    }

    /**
     * function to get the array of bitmaps for the current direction
     *
     * @return
     */
    public Bitmap[] getMoveBitmaps() {
        switch (rowUsing) {
            case ROW_SAD:
                return this.sadImages;
            case ROW_HAPPY:
                return this.happyImages;
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

        // Current time in millisecon
        long now = System.currentTimeMillis();
        // Never once did draw.
        if (lastDrawMsTime == -1) {
            lastDrawMsTime = now;
        }
        //  get delta time since last frame
//        int deltaTime = 30;/*(int) ((now - lastDrawMsTime));*/
        int deltaTime = (int) ((now - lastDrawMsTime));
//        if (((now - lastDrawMsTime) / 1000000) > 100) {
// Distance moves

        float distance = VELOCITY * deltaTime;
        double movingVectorLength = Math.sqrt(movingVectorX * movingVectorX + movingVectorY * movingVectorY);
// Calculate the new position of the game character.

        // in every update there should be fixed ratio of pixels to move
//        this.x = x + (int) (distance * movingVectorX / movingVectorLength);
//        this.y = y + (int) (distance * movingVectorY / movingVectorLength);

        setX(x + (int) (distance * movingVectorX / movingVectorLength));
        setY(y + (int) (distance * movingVectorY / movingVectorLength));
        // Make sure character does not go out of the screen
        if (this.x < 0) {
//            this.x = 0;
            setX(0);
        } else if (this.x > this.canvasWidth - width) {
//            this.x = this.canvasWidth - width;
            setX(this.canvasWidth - width);
        }

        if (this.y < 0) {
//            this.y = 0;
            setY(0);
        } else if (this.y > this.canvasHeight - height) {
//            this.y = this.canvasHeight - height;
            setY(this.canvasHeight - height);
        }

        //TODO: rowUsing
//        if (movingVectorX == 0) {
//            this.rowUsing = ROW_HAPPY;
//        }

        // columnUsing
        if (movingVectorX == 0 && movingVectorY == 0) {
            this.colUsing = COULUMN_STRAGIT;
        } else if (movingVectorX > 0) {
            if (movingVectorY > 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY)) {
                this.colUsing = COULUMN_DOWEN;
            } else if (movingVectorY < 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY)) {
                this.colUsing = COULUMN_UP;
            } else {
                this.colUsing = COULUMN_RIGHT;
            }
        } else {
            if (movingVectorY > 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY)) {
                this.colUsing = COULUMN_DOWEN;
            } else if (movingVectorY < 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY)) {
                this.colUsing = COULUMN_UP;
            } else {
                this.colUsing = COULUMN_LEFT;
            }
        }

        this.counter++;

        if (this.isHintCharacter) {
            if (this.counter % 5 == 0) {
                // move electrons
                referenceAngle = (referenceAngle + 5) % 360;

            }
        } else {
            referenceAngle = (referenceAngle + 5) % 360;
        }
        if (this.counter % 5 == 0) {
            for (int i = 0; i < elecReferenceAngle.length; i++) {
                elecReferenceAngle[i] = (elecReferenceAngle[i] + elecReferenceAngleSpeed[i]) % 360;
            }
        }
        // not moving ... slow animation
//


        // keep time to find delta between frams
        lastDrawMsTime = System.currentTimeMillis();
    }


    public void draw(Canvas canvas) {
        // set character trancparency based on lives count
        Bitmap bitmap = this.getCurrentMoveBitmap();
//        Paint paint = new Paint();
        symolPaint.setColor(this.rowUsing == ROW_HAPPY ? happyColor : sadColor); // Text Color
//        atomicNumPaint.setColor(this.rowUsing == ROW_HAPPY ? happyColor : sadColor);
        // Text Size
        String text = this.element.getSymbol();
        float textWidth = symolPaint.measureText(text);
        int xOffset = (int) ((bitmap.getWidth() - textWidth) / 2f);// - (int)(fontSize/2f);
        canvas.drawText(text, x + xOffset, y + symolPaintHeight/2  + bitmap.getHeight() / 2, symolPaint);

//        if (!this.isHintCharacter) {
//            canvas.drawBitmap(bitmap, x, y, null);
//        } else {
        canvas.drawBitmap(bitmap, x, y, mainCharPaint);
//        }
        // atomic number
        if (!this.isHintCharacter) {
            canvas.drawText(element.getAtomicNumber() + "", x + 10, y + (atomicNumPaintHeight+symolPaintHeight)/2 + bitmap.getHeight() / 2, atomicNumPaint);
            canvas.drawText(element.getAtomicNumber() + "", x + 10, y + (atomicNumPaintHeight+symolPaintHeight)/2 + bitmap.getHeight() / 2, atomicNumPaint2);
        }

        //draw electrons shell
        float centerX = x + bitmap.getWidth() / 2;
        float centerY = y + bitmap.getHeight() / 2;
        float radius = elecShellRadius + bitmap.getWidth() / 2;
        // madar elctrons
//        canvas.drawCircle(centerX, centerY, radius, eShellpaint1);
//
//        canvas.drawCircle(centerX, centerY, radius - (elecShellRadius / 2), eShellpaint2);

        // electrons
        float angle = referenceAngle;
//        for (int i = 0; i < this.numOfCurrentElctrons; i++) {
//            float cX = (float) (centerX + radius * Math.cos(angle * Math.PI / 180));
//            float cY = (float) (centerY + radius * Math.sin(angle * Math.PI / 180));
//            canvas.drawCircle(cX, cY, 10, electronPaint);
//            angle = (angle + 45) % 360;
//        }

        int[] listOfShells = element.getElecShellConfig();
        for (int j = 0; j < listOfShells.length; j++) {

            if (listOfShells[j] > 0) {

                boolean isLastShell = false;
                if(j==listOfShells.length-1 || listOfShells[j+1] == 0){
                    isLastShell=true;
                }


                // draw cirle for shell
                canvas.drawCircle(centerX, centerY,((j)*elecShellRadius)+ radius, eShellpaint1);
                 angle = elecReferenceAngle[j];
                // draw electrons in the shell
                for (int i = 0; i < listOfShells[j]; i++) {
                    float cX = (float) (centerX + (((j)*elecShellRadius)+radius) * Math.cos(angle * Math.PI / 180));
                    float cY = (float) (centerY + (((j)*elecShellRadius)+ radius) * Math.sin(angle * Math.PI / 180));
                    if(isLastShell){
                        canvas.drawCircle(cX, cY, elecRadius, electronPaint);
                    }else{
                        canvas.drawCircle(cX, cY, elecRadius, electronAlphPaint);
                    }

                    angle = (angle + (360/listOfShells[j])) % 360;
                }


            }

        }

        // TODO : draw electron sha7neh (- / +)
        // based on obtained and lost electrons


    }

    /**
     * function to change the moving victor of the character in order to move or stop
     *
     * @param movingVectorX
     * @param movingVectorY
     */
    public void setMovingVector(float movingVectorX, float movingVectorY) {

        this.movingVectorX = movingVectorX * this.canvasWidth;
        this.movingVectorY = movingVectorY * this.canvasHeight;
    }


    public String getType() {
        return type;
    }

    public String getMyId() {
        return myId;
    }

    public int getLives() {
        return lives;
    }

    public void decrementLives() {
        this.lives -= 1;
    }

//    public float getXPosition() {
//        return (((float) this.x) / this.canvasWidth);
//    }
//
//    public void setXPosition(float xPercentage) {
//        this.x = (int) (xPercentage * this.canvasWidth);
//    }

    /**
     * To update the canvas dimensions after the canvas is created
     *
     * @param canvasHeight
     * @param canvasWidth
     */
    public void updateCanvuasDimensions(int canvasHeight, int canvasWidth) {
        this.canvasHeight = canvasHeight;
        this.canvasWidth = canvasWidth;
        // update character y position
        setY((canvasHeight - width) / 2);
        setX((canvasWidth - height) / 2);
        // velocity and moving speed depends on canvas width
//        VELOCITY = VELOCITY * this.canvasWidth;
        VELOCITY = VELOCITY * this.canvasHeight;
//        movingVectorX = movingVectorX * this.canvasWidth;
    }


    public int getNumOfCurrentElctrons() {
        return numOfCurrentElctrons;
    }

    public void setNumOfCurrentElctrons(int numOfCurrentElctrons) {
        this.numOfCurrentElctrons = numOfCurrentElctrons;
    }

    public void incNumOfCurrentElctrons() {
        numOfCurrentElctrons++;
    }

    public void decNumOfCurrentElctrons() {
        numOfCurrentElctrons--;
    }

    // only for hint
    public void resizeImage(int length) {

        if (this.isHintHappyCharacter) {
            this.happyImages[0] = Bitmap.createScaledBitmap
                    (this.happyImages[0], length, length, false);

//            width = this.happyImages[0].getWidth();
//            height = this.happyImages[0].getHeight();
            setWidth(this.happyImages[0].getWidth());
            setHeight(this.happyImages[0].getHeight());
        } else {
            this.sadImages[0] = Bitmap.createScaledBitmap
                    (this.sadImages[0], length, length, false);
//            width = this.sadImages[0].getWidth();
//            height = this.sadImages[0].getHeight();
            setWidth(this.sadImages[0].getWidth());
            setHeight(this.sadImages[0].getHeight());
        }

    }

    public void updateAlpha(boolean isWithAlph) {
//        if (this.isHintCharacter) {
        //add alpha
        happyColor = Color.argb(120, 255, 255, 255);
        sadColor = Color.argb(120, 252, 93, 93);
        eShellpaint1.setColor(Color.argb(120, 255, 255, 255));
        eShellpaint2.setColor(Color.argb(120, 255, 255, 255));
        eShellpaint2.setAlpha(25);//0-255:50
        electronPaint.setColor(Color.argb(120, 13, 195, 249));
        symolPaint.setAlpha(120);
        mainCharPaint.setAlpha(123);
//        }
    }

    public int getNumOfNeededElctrons() {
        return numOfNeededElctrons;
    }

    public void setNumOfNeededElctrons(int numOfNeededElctrons) {
        this.numOfNeededElctrons = numOfNeededElctrons;
    }

    public void incNumOfNeededElctrons() {
        numOfNeededElctrons++;
    }

    public void decNumOfNeededElctrons() {
        numOfNeededElctrons--;
    }

    public int getTotalNumOfHappyElectrons() {
        return totalNumOfHappyElectrons;
    }

    public void setTotalNumOfHappyElectrons(int totalNumOfHappyElectrons) {
        this.totalNumOfHappyElectrons = totalNumOfHappyElectrons;
    }

    public boolean isAlreadyHappy() {
        return isAlreadyHappy;
    }

    public void setAlreadyHappy(boolean alreadyHappy) {
        isAlreadyHappy = alreadyHappy;
    }

    public void makeItHappy(boolean isHappy) {
        this.rowUsing = isHappy ? ROW_HAPPY : ROW_SAD;
    }
}
