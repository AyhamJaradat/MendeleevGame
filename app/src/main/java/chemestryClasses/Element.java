package chemestryClasses;

/**
 * Created by Ayham on 12/22/2018.
 */

public class Element {

    private String name;
    private String nameTurk;
    private String symbol;
    private int levelNum;
    private String type; // philizat / not philizat/ simi philis
    private String typeTurk; // philizat / not philizat/ simi philis
    private String state; // sa2el, salb, ma2e3 , gaz,
    private String stateTurk; // sa2el, salb, ma2e3 , gaz,
    private int atomicNumber; // el 3add el thare (num of protons)
    // initially proton=electron
    private int numOfElectrons;
    private int numOfProtons;
    private int numOfNutrons;
    //proton+nutron = el 3addad el kotale
    private int numOfElecInLastShell;
    private int rowNum;
    private int columnNum;
    //    private boolean isNoble;
    private String color;
    private String colorTurk;
    private String year;
    private String oldGroup;
    private String block;
//    private int[] shells = new int[10]; // max 7 shells .. make it 10, put in each shell num of electrons

    private int[] elecShellConfig = new int[7];
    private int[] indexToLevel = {0, 1, 1, 2, 2, 3, 2, 3, 4, 3, 4, 5, 3, 4, 5, 6, 4, 5, 6};
    private int[] maxPerLoop = {2, 2, 6, 2, 6, 2, 10, 6, 2, 10, 6, 2, 14, 10, 6, 2, 14, 10, 6};


    private boolean isNextMovingElectron;
    private int electronMovingCounter;
    private int powerMovingCounter;


    public Element() {

    }

    private void fillElecShellCongig() {
        int i = 0;
        int atomic = this.atomicNumber;
        while (atomic > 0) {

            if (atomic >= maxPerLoop[i]) {
                atomic -= maxPerLoop[i];
                elecShellConfig[indexToLevel[i]] += maxPerLoop[i];

            } else {
                elecShellConfig[indexToLevel[i]] += atomic;
                atomic = 0;
            }
            i++;
        }


        // find num of electrons in last shell
        for (int j = elecShellConfig.length - 1; j >= 0; j--) {
            if (elecShellConfig[j] > 0) {
                this.numOfElecInLastShell = elecShellConfig[j];
                break;
            }


        }

    }

    public String getName() {

        if (DataHolder.getInstance().isSETTINGS_IS_ENGLISH())
            return name;
        else
            return nameTurk;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public String getType() {
        if (DataHolder.getInstance().isSETTINGS_IS_ENGLISH())
            return type;
        else
            return typeTurk;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        if (DataHolder.getInstance().isSETTINGS_IS_ENGLISH())
            return state;
        else
            return stateTurk;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getAtomicNumber() {
        return atomicNumber;

    }

    public void setAtomicNumber(int atomicNumber) {
        this.atomicNumber = atomicNumber;
        setNumOfElectrons(atomicNumber);
        setNumOfProtons(atomicNumber);
        fillElecShellCongig();
    }

    public void resetMovingCounters() {
        electronMovingCounter = 0;
        powerMovingCounter = 0;
        isNextMovingElectron = false;
        int neededElect = getNumOfNeededElectrons();
        if (neededElect >= 1 && neededElect <= 4) {
            isNextMovingElectron = true;
        }
    }

    public boolean isNextElectron() {

        int neededElect = getNumOfNeededElectrons();

        if (neededElect == 0) {
            isNextMovingElectron = !isNextMovingElectron;

        } else {

            if (neededElect > 0) {
                // send more power
                if (powerMovingCounter < (getRowNum() + 4)) {
                    isNextMovingElectron = false;
                    powerMovingCounter++;

                } else {

                    if (columnNum == 1 || columnNum == 2 || columnNum == 13 || columnNum == 18) {
//                        1/1
                        isNextMovingElectron = !isNextMovingElectron;
                    } else if ((columnNum >= 3 && columnNum <= 5) || columnNum == 14 || (atomicNumber >= 57 && atomicNumber <= 61) || (atomicNumber >= 89 && atomicNumber <= 93)) {
//                        1/2
                        if (electronMovingCounter % 3 == 0) {
                            //0
                            isNextMovingElectron = true;
                        } else {
                            //1,2
                            isNextMovingElectron = false;
                        }
                        electronMovingCounter++;


                    } else if ((columnNum >= 6 && columnNum <= 9) || columnNum == 15 || columnNum == 16 || (atomicNumber >= 62 && atomicNumber <= 66) || (atomicNumber >= 94 && atomicNumber <= 98)) {
                        //1/3
                        if (electronMovingCounter % 4 == 0) {
                            //0
                            isNextMovingElectron = true;
                        } else {
                            //1,2,3
                            isNextMovingElectron = false;
                        }
                        electronMovingCounter++;
                    } else if ((columnNum >= 10 && columnNum <= 12) || columnNum == 17 || (atomicNumber >= 67 && atomicNumber <= 71) || (atomicNumber >= 99 && atomicNumber <= 103)) {
                        //1/4
                        if (electronMovingCounter % 5 == 0) {
                            //0
                            isNextMovingElectron = true;
                        } else {
                            //1,2,3,4
                            isNextMovingElectron = false;
                        }
                        electronMovingCounter++;
                    }
                }


            } else {
//                send more electron
                if (electronMovingCounter < (getRowNum() + 4)) {
                    isNextMovingElectron = true;
                    electronMovingCounter++;
                } else {

                    if (columnNum == 1 || columnNum == 2 || columnNum == 13 || columnNum == 18) {
//                        1/1
                        isNextMovingElectron = !isNextMovingElectron;
                    } else if ((columnNum >= 3 && columnNum <= 5) || columnNum == 14) {
//                        1/2
                        if (powerMovingCounter % 3 == 0) {
                            //0
                            isNextMovingElectron = false;
                        } else {
                            //1,2
                            isNextMovingElectron = true;
                        }
                        powerMovingCounter++;


                    } else if ((columnNum >= 6 && columnNum <= 9) || columnNum == 15 || columnNum == 16) {
                        //1/3
                        if (powerMovingCounter % 4 == 0) {
                            //0
                            isNextMovingElectron = false;
                        } else {
                            //1,2,3
                            isNextMovingElectron = true;
                        }
                        powerMovingCounter++;
                    } else if ((columnNum >= 10 && columnNum <= 12) || columnNum == 17) {
                        //1/4
                        if (powerMovingCounter % 5 == 0) {
                            //0
                            isNextMovingElectron = false;
                        } else {
                            //1,2,3,4
                            isNextMovingElectron = true;
                        }
                        powerMovingCounter++;
                    }

                }
            }
        }

        return isNextMovingElectron;
    }

    public int getNumOfElectrons() {
        return numOfElectrons;
    }

    public void setNumOfElectrons(int numOfElectrons) {
        this.numOfElectrons = numOfElectrons;
    }

    public int getNumOfProtons() {
        return numOfProtons;
    }

    public void setNumOfProtons(int numOfProtons) {
        this.numOfProtons = numOfProtons;
    }

    public int getNumOfNutrons() {
        return numOfNutrons;
    }

    public void setNumOfNutrons(int numOfNutrons) {
        this.numOfNutrons = numOfNutrons;
    }

    public int getNumOfElecInLastShell() {
        return numOfElecInLastShell;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public int getRowNum() {
        return rowNum;
    }

    public void setRowNum(int rowNum) {
        this.rowNum = rowNum;
    }

    public int getColumnNum() {
        return columnNum;
    }

    public void setColumnNum(int columnNum) {
        this.columnNum = columnNum;
    }

//    public boolean isNoble() {
//        return isNoble;
//    }
//
//    public void setNoble(boolean noble) {
//        isNoble = noble;
//    }


    public int getNumOfNeededElectrons() {

        switch (this.columnNum) {
            case 1:
                return -1;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 101:
            case 102:
                return -2;
            case 13:
                return -3;
            case 14:
                return 4;
            case 15:
                return 3;
            case 16:
                return 2;
            case 17:
                return 1;
            case 18:
                return 0;
        }
        return 0;
    }

    public int[] getElecShellConfig() {
        return elecShellConfig;
    }

    public String getColor() {
        if (DataHolder.getInstance().isSETTINGS_IS_ENGLISH())
            return color;
        else
            return colorTurk;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getOldGroup() {
        return oldGroup;
    }

    public void setOldGroup(String oldGroup) {
        this.oldGroup = oldGroup;
    }

    public void setNameTurk(String nameTurk) {
        this.nameTurk = nameTurk;
    }

    public void setTypeTurk(String typeTurk) {
        this.typeTurk = typeTurk;
    }

    public void setStateTurk(String stateTurk) {
        this.stateTurk = stateTurk;
    }

    public void setColorTurk(String colorTurk) {
        this.colorTurk = colorTurk;
    }
}
