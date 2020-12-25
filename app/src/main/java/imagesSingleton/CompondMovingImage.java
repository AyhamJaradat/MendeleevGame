package imagesSingleton;

import android.graphics.Bitmap;

/**
 * Created by Ayham on 1/17/2019.
 */

public class CompondMovingImage {
    private static CompondMovingImage instance;
    private Bitmap originalImage;
    private Bitmap images;
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

    private CompondMovingImage(Bitmap originalImage, int rowCount, int colCount) {
        this.originalImage = originalImage;
        this.rowCount = rowCount;
        this.colCount = colCount;
        this.totalWidth = originalImage.getWidth();
        this.totalHeight = originalImage.getHeight();
        this.width = this.totalWidth / colCount;
        this.height = this.totalHeight / rowCount;
        this.images = originalImage;

    }

    public static CompondMovingImage getInstance(Bitmap image, int rowCount, int colCount) {
        if (instance == null) {
            instance = new CompondMovingImage(image, rowCount, colCount);
        }
        return instance;
    }
    public Bitmap resizeImage(int length) {

        if (this.hintImage == null || length != hitImageLength) {
            this.hintImage = Bitmap.createScaledBitmap
                    (this.images, length, length, false);
            hitImageLength = length;
            hitImageWidth = this.images.getWidth();
            hitImageHight = this.images.getHeight();

        }
        return this.hintImage;
    }


    public Bitmap getCurrentMoveBitmap(int index) {
        return this.images;
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


    public Bitmap getImages() {
        return images;
    }


}
