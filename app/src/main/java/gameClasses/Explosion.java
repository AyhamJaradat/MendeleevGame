package gameClasses;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import imagesSingleton.EffectImages;
import utils.Constants;

/**
 * Created by Ayham on 12/25/2018.
 */

public class Explosion extends GameCharacter implements Constants {
    //    private int rowIndex = 0;
//    private int colIndex = -1;
    private int animationIndex = 0;
    private boolean finish = false;
    private GameSurface gameSurface;
    private Bitmap[] images;
    private String type;

    private EffectImages effectImages;

    public Explosion(GameSurface gameSurface, Bitmap image, int x, int y, String characterType, int rowCount, int colCount) {
        super(image, rowCount, colCount, x, y);
        this.type = characterType;
        this.gameSurface = gameSurface;

        this.images = new Bitmap[colCount * rowCount];
        int index = 0;
        for (int row = 0; row < this.rowCount; row++) {
            for (int col = 0; col < this.colCount; col++) {
                this.images[index++] = this.createSubImageAt(row, col);

            }
        }

    }

    public Explosion(GameSurface gameSurface, int x, int y, String characterType) {
        super(x, y);
        this.type = characterType;
        this.gameSurface = gameSurface;
        effectImages = EffectImages.getInstance(gameSurface);
        int index = 0;
        switch (characterType) {
            case POWER_EXPLOSION_EFFECT:
                index = POWER_EXPLOSION_INDEX;
                break;
            case ELECTRON_EXPLOSION_EFFECT:
                index = ELECTRON_EXPLOSION_INDEX;
                break;
            case GET_ELECTRON_EFFECT:
                index = GET_ELECTRON_INDEX;
                break;
            case LOSE_ELECTRON_EFFECT:
                index = LOSE_ELECTRON_INDEX;
                break;
            case ELECTRON_DISAPPEAR_EFFECT:
                index = ELECTRON_DISAPPEAR_INDEX;
                break;
            case POWER_DISAPPEAR_EFFECT:
                index = POWER_DISAPPEAR_INDEX;
                break;
//            case WIN_EFFECT:
//                index = WIN_INDEX;
//                break;
        }
        this.images = effectImages.getImages(index);
        updateImageInfo(effectImages.getTotalWidth(index), effectImages.getTotalHeight(index), effectImages.getRowCount(index), effectImages.getColCount(index));
    }

    public void update() {


        // Play sound explosion.wav.
//        if (this.colIndex == 0 && this.rowIndex == 0) {
//        if (this.animationIndex == this.images.length-1) {
        if (this.animationIndex == 0 &&  (this.type.equalsIgnoreCase(POWER_EXPLOSION_EFFECT) || this.type.equalsIgnoreCase(ELECTRON_EXPLOSION_EFFECT) ) ) {
            this.gameSurface.playSoundExplosion();
        }else if(this.animationIndex == 0 &&  (this.type.equalsIgnoreCase(GET_ELECTRON_EFFECT) || this.type.equalsIgnoreCase(LOSE_ELECTRON_EFFECT) )){
            this.gameSurface.playSoundEat();
        }
        // increment column to next frame
//        this.colIndex++;
        this.animationIndex++;
        // once reach last column
        if (this.animationIndex == this.images.length - 1) {
            this.finish = true;
        }
//        if (this.colIndex >= this.colCount) {
//            // go back to first column
//            this.colIndex = 0;
//            // increment row if more than one row exists
//            /*if (this.type.equalsIgnoreCase(WATER_STAR_TYPE) ||this.type.equalsIgnoreCase(FIRE_STAR_TYPE) ) {
////                starts are one row only
//                this.finish = true;
//            } else {*/
//            this.rowIndex++;
//            if (this.rowIndex >= this.rowCount) {
//                this.finish = true;
//            }
////            }
//        }
    }

    public void draw(Canvas canvas) {
        if (!finish) {
            //TODO: enhance performance , create them only once
//            Bitmap bitmap = this.createSubImageAt(rowIndex, colIndex);
            Bitmap bitmap = this.images[animationIndex];
            canvas.drawBitmap(bitmap, this.x - (width / 2), this.y - (height / 2), null);
        }
    }

    public boolean isFinish() {
        return finish;
    }
}
