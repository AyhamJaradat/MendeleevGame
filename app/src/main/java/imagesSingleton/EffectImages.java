package imagesSingleton;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.mandeleev.game.mandeleev.R;

import gameClasses.GameSurface;
import utils.Constants;

/**
 * Created by Ayham on 12/26/2018.
 */

public class EffectImages implements Constants {

    private static EffectImages instance;


    private Bitmap[] originalImages;
    private Bitmap[][] images;


    private int[] rowCount;
    private int[] colCount;
    private int[] totalWidth;
    private int[] totalHeight;
    private int[] width;
    private int[] height;

    private EffectImages(GameSurface gameSurface) {

        this.originalImages = new Bitmap[EFFECTS_SIZE];

        this.rowCount = new int[EFFECTS_SIZE];
        this.colCount = new int[EFFECTS_SIZE];

        this.rowCount[POWER_EXPLOSION_INDEX] = 5;
        this.rowCount[ELECTRON_EXPLOSION_INDEX] = 4;
        this.rowCount[GET_ELECTRON_INDEX] = 5;
        this.rowCount[LOSE_ELECTRON_INDEX] = 5;
        this.rowCount[ELECTRON_DISAPPEAR_INDEX] = 2;
        this.rowCount[POWER_DISAPPEAR_INDEX] = 2;
//        this.rowCount[WIN_INDEX] = 2;

        this.colCount[POWER_EXPLOSION_INDEX] = 5;
        this.colCount[ELECTRON_EXPLOSION_INDEX] = 5;
        this.colCount[GET_ELECTRON_INDEX] = 5;
        this.colCount[LOSE_ELECTRON_INDEX] = 5;
        this.colCount[ELECTRON_DISAPPEAR_INDEX] = 4;
        this.colCount[POWER_DISAPPEAR_INDEX] = 4;
//        this.colCount[WIN_INDEX] = 5;


        this.images = new Bitmap[EFFECTS_SIZE][];





//        this.originalImages[GET_ELECTRON_INDEX] = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.get_electron2);
//        this.originalImages[LOSE_ELECTRON_INDEX] = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.lose_electron2);







        this.totalWidth = new int[EFFECTS_SIZE];//originalImage.getWidth();
        this.totalHeight = new int[EFFECTS_SIZE];//originalImage.getHeight();
        this.width = new int[EFFECTS_SIZE];// this.totalWidth / colCount;
        this.height = new int[EFFECTS_SIZE];// this.totalHeight / rowCount;
        for (int i = 0; i < EFFECTS_SIZE; i++) {

            switch (i){
                case POWER_EXPLOSION_INDEX:
                    this.originalImages[POWER_EXPLOSION_INDEX] = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.power_explosion);
                    break;
                case ELECTRON_EXPLOSION_INDEX:
                    this.originalImages[ELECTRON_EXPLOSION_INDEX] = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.electron_explosion);
                    break;
                case GET_ELECTRON_INDEX:
                    this.originalImages[GET_ELECTRON_INDEX] = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.get_electron);
                    break;
                case LOSE_ELECTRON_INDEX:
                    this.originalImages[LOSE_ELECTRON_INDEX] = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.lose_electron);
                    break;
                case ELECTRON_DISAPPEAR_INDEX:
                    this.originalImages[ELECTRON_DISAPPEAR_INDEX] = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.electron_disappear);
                    break;
                case POWER_DISAPPEAR_INDEX:
                    this.originalImages[POWER_DISAPPEAR_INDEX] = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.power_disappear);
                    break;
//                case WIN_INDEX:
//                    this.originalImages[WIN_INDEX] = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.win_effect3);
//                    break;
            }

            this.totalWidth[i] = originalImages[i].getWidth();
            this.totalHeight[i] = originalImages[i].getHeight();
            this.width[i] = totalWidth[i] / colCount[i];
            this.height[i] = totalHeight[i] / rowCount[i];

            this.images[i] = new Bitmap[colCount[i] * rowCount[i]];
            int index = 0;
            for (int row = 0; row < this.rowCount[i]; row++) {
                for (int col = 0; col < this.colCount[i]; col++) {
                    this.images[i][index++] = this.createSubImageAt(i, row, col);
                }
            }
            originalImages[i].recycle();

        }
    }

    public static EffectImages getInstance(GameSurface gameSurface) {
        if (instance == null) {
            instance = new EffectImages(gameSurface);
        }
        return instance;
    }

    private Bitmap createSubImageAt(int index, int row, int col) {
        // createBitmap(bitmap, x, y, width, height).
        Bitmap subImage = Bitmap.createBitmap(originalImages[index], col * width[index], row * height[index], width[index], height[index]);
        return subImage;
    }

    public int getTotalWidth(int index) {
        return totalWidth[ index];
    }



    public int getTotalHeight(int index) {
        return totalHeight[index];
    }


    public int getWidth(int index) {
        return width[index];
    }


    public int getHeight(int index) {
        return height[index];
    }



    public Bitmap[] getImages(int index) {
        return images[index];
    }


    public int getRowCount(int index) {
        return rowCount[index];
    }

    public int getColCount(int index) {
        return colCount[index];
    }
}
