package gameClasses;

import android.graphics.Bitmap;

/**
 * Created by Ayham on 12/13/2018.
 */

public class GameCharacter {

    protected Bitmap image;
    protected int rowCount;
    protected int colCount;
    protected int totalWidth;
    protected int totalHeight;
    protected int width;
    protected int height;
    protected int x;
    protected int y;

    protected  int xCenter;
    protected  int yCenter;
    protected  int radius;

    public GameCharacter( int rowCount, int colCount, int x, int y) {

        this.rowCount = rowCount;
        this.colCount = colCount;
        this.x = x;
        this.y = y;


    }

    public GameCharacter(int x, int y) {
        this.x = x;
        this.y = y;
    }

    protected void updateImageInfo(int totalWidth, int totalHeight) {
        this.totalWidth = totalWidth;
        this.totalHeight = totalHeight;
        this.width = this.totalWidth / colCount;
        this.height = this.totalHeight / rowCount;
        this.radius = this.width/2;
        this.xCenter= this.x +this.radius;
        this.yCenter = this.y +this.radius;
    }
    protected void updateImageInfo(int totalWidth, int totalHeight,int rowCount, int colCount) {
        this.rowCount = rowCount;
        this.colCount = colCount;
        this.totalWidth = totalWidth;
        this.totalHeight = totalHeight;
        this.width = this.totalWidth / colCount;
        this.height = this.totalHeight / rowCount;
        this.radius = this.width/2;
        this.xCenter= this.x +this.radius;
        this.yCenter = this.y +this.radius;
    }
    public GameCharacter(Bitmap image, int rowCount, int colCount, int x, int y) {

        this.image = image;
        this.rowCount = rowCount;
        this.colCount = colCount;
        this.x = x;
        this.y = y;
        this.totalWidth = image.getWidth();
        this.totalHeight = image.getHeight();
        this.width = this.totalWidth / colCount;
        this.height = this.totalHeight / rowCount;

        this.radius = this.width/2;
        this.xCenter= this.x +this.radius;
        this.yCenter = this.y +this.radius;

    }
    /**
     * method to creat a bitmap as sub image of the original image
     * @param row
     * @param col
     * @return
     */
    protected Bitmap createSubImageAt(int row, int col) {
        // createBitmap(bitmap, x, y, width, height).
        Bitmap subImage = Bitmap.createBitmap(image, col * width, row * height, width, height);
        return subImage;
    }


    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
        updateCenter();
    }

    public void setX(int x) {
        this.x = x;
        updateCenter();
    }

    public int getHeight() {
        return height;
    }


    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
        this.radius = this.width/2;
        updateCenter();
    }

    public void setHeight(int height) {
        this.height = height;
        this.radius = this.width/2;
        updateCenter();
    }

    public void updateCenter() {
        this.xCenter= this.x +this.radius;
        this.yCenter = this.y +this.radius;
    }
    public int getxCenter() {
        return xCenter;
    }

    public void setxCenter(int xCenter) {
        this.xCenter = xCenter;
    }

    public int getyCenter() {
        return yCenter;
    }

    public void setyCenter(int yCenter) {
        this.yCenter = yCenter;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
