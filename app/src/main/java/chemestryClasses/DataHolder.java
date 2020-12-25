package chemestryClasses;

import java.util.ArrayList;

/**
 * Created by Ayham on 12/22/2018.
 */

public class DataHolder {


    private static DataHolder instance;

    private ArrayList<Element> elements = new ArrayList<Element>();
    private ArrayList<Compound> compounds = new ArrayList<Compound>();

    private boolean SETTINGS_IS_ENGLISH = true;
    private boolean SETTINGS_IS_LEFT_HAND = true;
    private boolean SETTINGS_IS_SOUNDS_OFF = true;
    private int leftFreeLevels;
    private int leftFreeLevels2;


    private DataHolder() {


    }


    public static DataHolder getInstance() {
        if (instance == null) {
            instance = new DataHolder();
        }
        return instance;
    }

    public ArrayList<Compound> getCompounds() {
        return compounds;
    }

    public void setCompounds(ArrayList<Compound> compounds) {
        this.compounds = compounds;
    }

    public ArrayList<Element> getElements() {
        return elements;
    }

    public void setElements(ArrayList<Element> elements) {
        this.elements = elements;
    }

    public Element getElementByLevel(int level) {
        return elements.get(level);
    }

    public Compound getCompoundByLevel(int level) {
        return compounds.get(level);
    }

    public boolean isSETTINGS_IS_ENGLISH() {
        return SETTINGS_IS_ENGLISH;
    }

    public void setSETTINGS_IS_ENGLISH(boolean SETTINGS_IS_ENGLISH) {
        this.SETTINGS_IS_ENGLISH = SETTINGS_IS_ENGLISH;
    }

    public boolean isSETTINGS_IS_LEFT_HAND() {
        return SETTINGS_IS_LEFT_HAND;
    }

    public void setSETTINGS_IS_LEFT_HAND(boolean SETTINGS_IS_LEFT_HAND) {
        this.SETTINGS_IS_LEFT_HAND = SETTINGS_IS_LEFT_HAND;
    }

    public boolean isSETTINGS_IS_SOUNDS_OFF() {
        return SETTINGS_IS_SOUNDS_OFF;
    }

    public void setSETTINGS_IS_SOUNDS_OFF(boolean SETTINGS_IS_SOUNDS_OFF) {
        this.SETTINGS_IS_SOUNDS_OFF = SETTINGS_IS_SOUNDS_OFF;
    }

    public int getLeftFreeLevels() {
        return leftFreeLevels;
    }

    public void setLeftFreeLevels(int leftFreeLevels) {
        this.leftFreeLevels = leftFreeLevels;
    }

    public int getLeftFreeLevels2() {
        return leftFreeLevels2;
    }

    public void setLeftFreeLevels2(int leftFreeLevels2) {
        this.leftFreeLevels2 = leftFreeLevels2;
    }
}
