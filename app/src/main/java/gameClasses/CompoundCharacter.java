package gameClasses;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import chemestryClasses.Compound;
import chemestryClasses.DataHolder;

/**
 * Created by Ayham on 1/17/2019.
 */

public class CompoundCharacter extends GameCharacter {

    private float symolPaintHeight;
    private float elementPaintHeight;
    // Row index of Image are being used.
    private int rowUsing = 0;
    private int colUsing = 0;
    // bitmap array for all direction
    private Bitmap[] images;

    // Velocity of game character (percantage of pixel/millisecond)
    private float VELOCITY = 0.0004f;
    private float[] velocityPerLevel = {0.0002f, 0.00025f, 0.0003f, 0.00035f, 0.0004f, 0.00045f, 0.0005f, 0.00055f};
    private float movingVectorX = 0f;
    private float movingVectorY = 0f;
    private long lastDrawMsTime = -1;
    private int counter = 0;
    private float screenDensity;
    private String type;
    private GameSurface gameSurface;
    private int canvasHeight;
    private int canvasWidth;
    private int numberOfElements;
    private Paint mainCharPaint;
    private Compound compound;
    // these two will changes
    private int numOfCurrentElements;
    // paintes
    private int symbolFontSize = 14;//38;
    private int elementFontSize = 8;//26;
    private int signFontSize = 14;//26;
    private Paint symolPaint = new Paint();
    private Paint elementPaint = new Paint();
    private Paint elementPaint2 = new Paint();
    private Paint signPaint = new Paint();
    private Paint signPaint2 = new Paint();
    private int happyColor;
    private int sadColor;

    private Typeface skranji_reg_font;
    private Typeface skranji_bold_font;

    private int nextSymbolIndex = 0;
    private String[] elementsList;


    public CompoundCharacter(GameSurface gameSurface, Bitmap image, int x, int y, int rows, int columns, int level) {
        super(image, rows/*number of rows */, columns/*number of columns*/, x, y);


        this.gameSurface = gameSurface;
        this.compound = DataHolder.getInstance().getCompoundByLevel(level);
        this.elementsList = compound.getElementLists();
        this.numOfCurrentElements = 0;
        this.numberOfElements = compound.getNumOfElements();

        // decide row usage
        this.rowUsing = 0;
        screenDensity = gameSurface.getResources().getDisplayMetrics().density;


        symbolFontSize = 10;// 28;
        signFontSize = 13;
        this.colUsing = numberOfElements - 2;
        this.canvasHeight = this.gameSurface.getHeight();
        this.canvasWidth = this.gameSurface.getWidth();

        skranji_reg_font = Typeface.createFromAsset(gameSurface.getContext().getAssets(), "fonts/Skranji-Regular.ttf");
        skranji_bold_font = Typeface.createFromAsset(gameSurface.getContext().getAssets(), "fonts/Skranji-Bold.ttf");

        mainCharPaint = new Paint();


        // for every direction , create the movement images and add them to the array of the movement
        this.images = new Bitmap[colCount];

        for (int col = 0; col < this.colCount; col++) {

            this.images[col] = this.createSubImageAt(0, col);
        }


        happyColor = Color.WHITE;
        sadColor = Color.rgb(252, 93, 93); //Color.rgb(250,68,4);

        symolPaint.setTextSize(symbolFontSize * screenDensity);
        symolPaint.setTypeface(skranji_bold_font);
        Paint.FontMetrics fm = symolPaint.getFontMetrics();
        symolPaintHeight = fm.descent - fm.ascent;


        elementPaint.setColor(Color.WHITE);
        elementPaint.setTypeface(skranji_bold_font);
        elementPaint.setShadowLayer(1f, 0f, 0f, 0xFF000000);
        elementPaint.setTextSize(elementFontSize * screenDensity);

        elementPaint2.setColor(Color.parseColor("#E38810"));
        elementPaint2.setTypeface(skranji_bold_font);
        elementPaint2.setTextSize(elementFontSize * screenDensity);
        elementPaint2.setShadowLayer(1f, 0f, 0f, 0xFF000000);
        Paint.FontMetrics fm2 = elementPaint.getFontMetrics();
        elementPaintHeight = fm2.descent - fm2.ascent;

        signPaint.setColor(Color.WHITE);
        signPaint.setShadowLayer(2f, 0f, 0f, 0xFF000000);
        signPaint.setTypeface(skranji_bold_font);
        signPaint.setTextSize(signFontSize * screenDensity);
        signPaint2.setColor(Color.argb(150, 0, 0, 0));
        signPaint2.setTypeface(skranji_bold_font);
        signPaint2.setStyle(Paint.Style.STROKE);
        signPaint2.setStrokeWidth(1);
        signPaint2.setTextSize(signFontSize * screenDensity);

    }


    /**
     * function to get the current image from the array of current moving bitmaps
     *
     * @return
     */
    public Bitmap getCurrentMoveBitmap() {

        return this.images[this.colUsing];

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

        // keep time to find delta between frams
        lastDrawMsTime = System.currentTimeMillis();
    }


    public void draw(Canvas canvas) {
        // set character trancparency based on lives count
        Bitmap bitmap = this.getCurrentMoveBitmap();
//        Paint paint = new Paint();
        symolPaint.setColor(happyColor); // Text Color

        // Text Size
        String text = this.compound.getSymbol();
        float textWidth = symolPaint.measureText(text);
        int xOffset = (int) ((bitmap.getWidth() - textWidth) / 2f);// - (int)(symbolFontSize/2f);
//        canvas.drawText(text, x + xOffset, y + symolPaintHeight / 2 + bitmap.getHeight() / 2, symolPaint);

        canvas.drawBitmap(bitmap, x, y, mainCharPaint);

        // draw elements in the circles
        int circleWidth = bitmap.getWidth() / 3;
        float tempWidth = 0;
        String symbol = "";
        elementPaint.setTextSize(elementFontSize * screenDensity);
        elementPaint2.setTextSize(elementFontSize * screenDensity);
        switch (this.compound.getNumOfElements()) {
            case 2:
                symbol = compound.getElementLists()[0];
                tempWidth = elementPaint.measureText(symbol);
                canvas.drawText(symbol, x + bitmap.getWidth() / 2 - circleWidth + (circleWidth - tempWidth) / 2, y + (elementPaintHeight) + circleWidth / 5 + bitmap.getHeight() / 2, nextSymbolIndex > 0 ? elementPaint2 : elementPaint);
                symbol = compound.getElementLists()[1];
                tempWidth = elementPaint.measureText(symbol);
                canvas.drawText(symbol, x + bitmap.getWidth() / 2 + (circleWidth - tempWidth) / 2, y + (elementPaintHeight) + circleWidth / 5 + bitmap.getHeight() / 2, nextSymbolIndex > 1 ? elementPaint2 : elementPaint);
                break;
            case 3:
                symbol = compound.getElementLists()[0];
                tempWidth = elementPaint.measureText(symbol);
                canvas.drawText(symbol, x + bitmap.getWidth() / 2 - circleWidth / 2 + (circleWidth - tempWidth) / 2, y + elementPaintHeight / 3 + bitmap.getHeight() / 2, nextSymbolIndex > 0 ? elementPaint2 : elementPaint);
                symbol = compound.getElementLists()[1];
                tempWidth = elementPaint.measureText(symbol);
                canvas.drawText(symbol, x + bitmap.getWidth() / 2 - circleWidth + (circleWidth - tempWidth) / 2, y + (elementPaintHeight) + circleWidth / 2 + bitmap.getHeight() / 2, nextSymbolIndex > 1 ? elementPaint2 : elementPaint);
                symbol = compound.getElementLists()[2];

                if (symbol.equalsIgnoreCase("SO4") || symbol.equalsIgnoreCase("CO3")) {
                    //CO3 ,SO4,
                    String firstPart = symbol.substring(0, 2);
                    String secondPart = symbol.substring(2, 3);
                    elementPaint.setTextSize((elementFontSize - 1) * screenDensity);
                    canvas.drawText(firstPart, x + bitmap.getWidth() / 2 + (circleWidth - tempWidth) / 2, y + (elementPaintHeight) + circleWidth / 2 + bitmap.getHeight() / 2, nextSymbolIndex > 2 ? elementPaint2 : elementPaint);
                    float w = elementPaint.measureText(firstPart);
                    elementPaint.setTextSize((elementFontSize - 2) * screenDensity);
                    canvas.drawText(secondPart, w + x + bitmap.getWidth() / 2 + (circleWidth - tempWidth) / 2, y - elementPaint.ascent() / 2 + (elementPaintHeight) + circleWidth / 2 + bitmap.getHeight() / 2, nextSymbolIndex > 2 ? elementPaint2 : elementPaint);
                } else {
                    tempWidth = elementPaint.measureText(symbol);
                    canvas.drawText(symbol, x + bitmap.getWidth() / 2 + (circleWidth - tempWidth) / 2, y + (elementPaintHeight) + circleWidth / 2 + bitmap.getHeight() / 2, nextSymbolIndex > 2 ? elementPaint2 : elementPaint);
                }
                break;
            case 4:
                symbol = compound.getElementLists()[0];
                tempWidth = elementPaint.measureText(symbol);
                canvas.drawText(symbol, x + (circleWidth - tempWidth) / 2, y + elementPaintHeight / 3 + bitmap.getHeight() / 2, nextSymbolIndex > 0 ? elementPaint2 : elementPaint);
                symbol = compound.getElementLists()[1];
                tempWidth = elementPaint.measureText(symbol);
                canvas.drawText(symbol, x + bitmap.getWidth() - circleWidth + (circleWidth - tempWidth) / 2, y + elementPaintHeight / 3 + bitmap.getHeight() / 2, nextSymbolIndex > 1 ? elementPaint2 : elementPaint);
                symbol = compound.getElementLists()[2];
                tempWidth = elementPaint.measureText(symbol);
                canvas.drawText(symbol, x + bitmap.getWidth() / 2 - circleWidth + (circleWidth - tempWidth) / 2, y + (elementPaintHeight) + circleWidth / 2 + bitmap.getHeight() / 2, nextSymbolIndex > 2 ? elementPaint2 : elementPaint);
                symbol = compound.getElementLists()[3];
                tempWidth = elementPaint.measureText(symbol);
                canvas.drawText(symbol, x + bitmap.getWidth() / 2 + (circleWidth - tempWidth) / 2, y + (elementPaintHeight) + circleWidth / 2 + bitmap.getHeight() / 2, nextSymbolIndex > 3 ? elementPaint2 : elementPaint);

                break;
            case 5:
                symbol = compound.getElementLists()[0];
                tempWidth = elementPaint.measureText(symbol);
                canvas.drawText(symbol, x + (circleWidth - tempWidth) / 2, y + elementPaintHeight / 3 + bitmap.getHeight() / 2, nextSymbolIndex > 0 ? elementPaint2 : elementPaint);
                symbol = compound.getElementLists()[1];
                tempWidth = elementPaint.measureText(symbol);
                canvas.drawText(symbol, x + bitmap.getWidth() / 2 - circleWidth / 2 + (circleWidth - tempWidth) / 2, y + elementPaintHeight / 3 + bitmap.getHeight() / 2, nextSymbolIndex > 1 ? elementPaint2 : elementPaint);
                symbol = compound.getElementLists()[2];
                tempWidth = elementPaint.measureText(symbol);
                canvas.drawText(symbol, x + bitmap.getWidth() - circleWidth + (circleWidth - tempWidth) / 2, y + elementPaintHeight / 3 + bitmap.getHeight() / 2, nextSymbolIndex > 2 ? elementPaint2 : elementPaint);
                symbol = compound.getElementLists()[3];
                tempWidth = elementPaint.measureText(symbol);
                canvas.drawText(symbol, x + bitmap.getWidth() / 2 - circleWidth + (circleWidth - tempWidth) / 2, y + (elementPaintHeight) + circleWidth / 2 + bitmap.getHeight() / 2, nextSymbolIndex > 3 ? elementPaint2 : elementPaint);
                symbol = compound.getElementLists()[4];
                tempWidth = elementPaint.measureText(symbol);
                canvas.drawText(symbol, x + bitmap.getWidth() / 2 + (circleWidth - tempWidth) / 2, y + (elementPaintHeight) + circleWidth / 2 + bitmap.getHeight() / 2, nextSymbolIndex > 4 ? elementPaint2 : elementPaint);

                break;

        }



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
        VELOCITY = velocityPerLevel[compound.getLevelDifficulty()] * this.canvasHeight;
//        movingVectorX = movingVectorX * this.canvasWidth;
    }


    public int getNumOfCurrentElements() {
        return numOfCurrentElements;
    }

    public void setNumOfCurrentElements(int numOfCurrentElements) {
        this.numOfCurrentElements = numOfCurrentElements;
    }

    public String getNextNeededSymbol() {
        return elementsList[nextSymbolIndex];
    }

    public void incNumOfCurrentElements() {
        numOfCurrentElements++;
    }


    public boolean eatGoodNextSymbol(String nextNeededSymbol) {
        nextSymbolIndex++;
        incNumOfCurrentElements();
        if (nextSymbolIndex >= elementsList.length) {
            return true;
        }
        return false;

    }
}
