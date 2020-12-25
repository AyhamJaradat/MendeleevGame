package chemestryClasses;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.SubscriptSpan;

/**
 * Created by Ayham on 1/16/2019.
 */

public class Compound {

    private String symbol;
    private int levelNum;
    private String name;
    private String name_t;
    private String secondName;
    private String secondName_t;
    private String bond;
    private String bond_t;
    private String type;
    private String type_t;

    private String txtElements;
    private String txtMovingElements;

    private int levelDifficulty;

    private int numOfElements;
    private int numOfMoving;

    private String[] elementList;
    private String[] movingList;
    private int nextMovingIndex = -1;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getLevelNum() {
        return levelNum;
    }

    public void setLevelNum(int levelNum) {
        this.levelNum = levelNum;
    }

    public String getName() {
        if (DataHolder.getInstance().isSETTINGS_IS_ENGLISH())
            return name;
        else
            return name_t;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName_t() {
        return name_t;
    }

    public void setName_t(String name_t) {
        this.name_t = name_t;
    }

    public String getSecondName() {
        if (DataHolder.getInstance().isSETTINGS_IS_ENGLISH())
            return secondName;
        else
            return secondName_t;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getSecondName_t() {
        return secondName_t;
    }

    public void setSecondName_t(String secondName_t) {
        this.secondName_t = secondName_t;
    }

    public String getBond() {
        if (DataHolder.getInstance().isSETTINGS_IS_ENGLISH())
            return bond;
        else
            return bond_t;
    }

    public void setBond(String bond) {
        this.bond = bond;
    }

    public String getBond_t() {
        return bond_t;
    }

    public void setBond_t(String bond_t) {
        this.bond_t = bond_t;
    }

    public String getType() {
        if (DataHolder.getInstance().isSETTINGS_IS_ENGLISH())
            return type;
        else
            return type_t;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType_t() {
        return type_t;
    }

    public void setType_t(String type_t) {
        this.type_t = type_t;
    }

    public String getTxtElements() {
        return txtElements;
    }

    public void setTxtElements(String txtElements) {

        this.txtElements = txtElements;
        elementList = txtElements.split("-");
        setNumOfElements(elementList.length);


    }

    public int getNumOfElements() {


        return numOfElements;
    }

    public void setNumOfElements(int numOfElements) {
        this.numOfElements = numOfElements;
    }

    public String nextMoving() {
        nextMovingIndex++;
        if (nextMovingIndex >= this.movingList.length)
            return "none";
        return this.movingList[nextMovingIndex];
    }

    public SpannableStringBuilder getSymbolByLevel(int level) {
        SpannableStringBuilder cs;
        switch (level) {
            case 1:
                cs = new SpannableStringBuilder("NaCl");
                return cs;
            case 2:
                cs = new SpannableStringBuilder("KBr");
                return cs;
            case 3:
                cs = new SpannableStringBuilder("HCl");
                return cs;
            case 4:
                cs = new SpannableStringBuilder("CaO");
                return cs;
            case 5:
                cs = new SpannableStringBuilder("MgBr2");
                cs.setSpan(new SubscriptSpan(), 4, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                cs.setSpan(new RelativeSizeSpan(0.75f), 4, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                return cs;

            case 6:
                cs = new SpannableStringBuilder("CaCl2");
                cs.setSpan(new SubscriptSpan(), 4, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                cs.setSpan(new RelativeSizeSpan(0.75f), 4, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                return cs;
            case 7:
                cs = new SpannableStringBuilder("Na2CO3");
                cs.setSpan(new SubscriptSpan(), 2, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                cs.setSpan(new RelativeSizeSpan(0.75f), 2, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                cs.setSpan(new SubscriptSpan(), 5, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                cs.setSpan(new RelativeSizeSpan(0.75f), 5, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                return cs;
            case 8:
                cs = new SpannableStringBuilder("H2O");
                cs.setSpan(new SubscriptSpan(), 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                cs.setSpan(new RelativeSizeSpan(0.75f), 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                return cs;
            case 9:
                cs = new SpannableStringBuilder("CO2");
                cs.setSpan(new SubscriptSpan(), 2, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                cs.setSpan(new RelativeSizeSpan(0.75f), 2, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                return cs;
            case 10:
                cs = new SpannableStringBuilder("Na2SO4");
                cs.setSpan(new SubscriptSpan(), 2, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                cs.setSpan(new RelativeSizeSpan(0.75f), 2, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                cs.setSpan(new SubscriptSpan(), 5, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                cs.setSpan(new RelativeSizeSpan(0.75f), 5, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                return cs;

            case 11:
                cs = new SpannableStringBuilder("CO3");
                cs.setSpan(new SubscriptSpan(), 2, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                cs.setSpan(new RelativeSizeSpan(0.75f), 2, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                return cs;

            case 12:
                cs = new SpannableStringBuilder("NH3");
                cs.setSpan(new SubscriptSpan(), 2, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                cs.setSpan(new RelativeSizeSpan(0.75f), 2, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                return cs;

            case 13:
                cs = new SpannableStringBuilder("CH4");
                cs.setSpan(new SubscriptSpan(), 2, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                cs.setSpan(new RelativeSizeSpan(0.75f), 2, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                return cs;

            case 14:
                cs = new SpannableStringBuilder("Al2O3");
                cs.setSpan(new SubscriptSpan(), 2, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                cs.setSpan(new RelativeSizeSpan(0.75f), 2, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                cs.setSpan(new SubscriptSpan(), 4, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                cs.setSpan(new RelativeSizeSpan(0.75f), 4, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                return cs;

            case 15:
                cs = new SpannableStringBuilder("SO4");
                cs.setSpan(new SubscriptSpan(), 2, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                cs.setSpan(new RelativeSizeSpan(0.75f), 2, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                return cs;

            case 16:
                cs = new SpannableStringBuilder("PO4");
                cs.setSpan(new SubscriptSpan(), 2, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                cs.setSpan(new RelativeSizeSpan(0.75f), 2, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                return cs;

        }
        return null;
    }

    public String[] getElementLists() {
        return elementList;
    }

    public void setTxtMovingElements(String txtMovingElements) {
        this.txtMovingElements = txtMovingElements;
        movingList = txtMovingElements.split("-");
        setNumOfMoving(movingList.length);
    }

    public int getNumOfMoving() {
        return numOfMoving;
    }

    public void setNumOfMoving(int numOfMoving) {
        this.numOfMoving = numOfMoving;
    }

    public void resetMovingCounters() {
        this.nextMovingIndex = -1;
    }

    public int getLevelDifficulty() {

        if (levelNum < 2)
            return 1;
        else if (levelNum < 4)
            return 2;
        else if (levelNum < 7)
            return 3;
        else if (levelNum < 10)
            return 4;
        else if (levelNum < 13)
            return 5;
        else if (levelNum < 16)
            return 6;
        else
            return 7;
    }
}
