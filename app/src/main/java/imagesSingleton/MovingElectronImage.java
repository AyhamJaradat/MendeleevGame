package imagesSingleton;

import android.graphics.Bitmap;

/**
 * Created by Ayham on 12/25/2018.
 */

public class MovingElectronImage {


    private static MovingElectronImage instance;

    private Bitmap originalImage;
    private Bitmap[] images;
    private Bitmap hintImage;
    private int hitImageLength;
    private int hitImageWidth;
    private int hitImageHight;
    private int rowCount;
    private int colCount;
    private int totalWidth;
    private int totalHeight;
    private int width;
    private int height;

    private MovingElectronImage(Bitmap originalImage, int rowCount, int colCount) {
        this.originalImage = originalImage;
        this.rowCount = rowCount;
        this.colCount = colCount;
        this.totalWidth = originalImage.getWidth();
        this.totalHeight = originalImage.getHeight();
        this.width = this.totalWidth / colCount;
        this.height = this.totalHeight / rowCount;


        this.images = new Bitmap[colCount * rowCount];
        int index = 0;
        for (int row = 0; row < this.rowCount; row++) {
            for (int col = 0; col < this.colCount; col++) {
                this.images[index++] = this.createSubImageAt(row, col);
            }
        }
        originalImage.recycle();
    }

    public static MovingElectronImage getInstance(Bitmap image, int rowCount, int colCount) {
        if (instance == null) {
            instance = new MovingElectronImage(image, rowCount, colCount);
        }
        return instance;
    }

    private Bitmap createSubImageAt(int row, int col) {
        // createBitmap(bitmap, x, y, width, height).
        Bitmap subImage = Bitmap.createBitmap(originalImage, col * width, row * height, width, height);
        return subImage;
    }

    public Bitmap resizeImage(int length, int index) {

        if (this.hintImage == null || length != hitImageLength) {
            this.hintImage = Bitmap.createScaledBitmap
                    (this.images[index], length, length, false);
            hitImageLength = length;
            hitImageWidth = this.images[index].getWidth();
            hitImageHight = this.images[index].getHeight();

        }
    return this.hintImage;
    }

    public Bitmap getCurrentMoveBitmap(int index) {
        return this.images[index];
    }


    public int getTotalWidth() {
        return totalWidth;
    }

    public void setTotalWidth(int totalWidth) {
        this.totalWidth = totalWidth;
    }

    public int getTotalHeight() {
        return totalHeight;
    }

    public void setTotalHeight(int totalHeight) {
        this.totalHeight = totalHeight;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Bitmap getHintImage() {
        return hintImage;
    }

    public int getHitImageWidth() {
        return hitImageWidth;
    }

    public int getHitImageHight() {
        return hitImageHight;
    }

    public Bitmap[] getImages() {
        return images;
    }
}
