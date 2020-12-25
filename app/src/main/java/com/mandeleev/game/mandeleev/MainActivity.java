package com.mandeleev.game.mandeleev;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.SubscriptSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
//import android.widget.GridLayout;
import android.support.v7.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

import chemestryClasses.Compound;
import chemestryClasses.DataHolder;
import chemestryClasses.Element;
import gameClasses.GameSurface;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import utils.Constants;
import utils.MyBounceInterpolator;


public class MainActivity extends Activity implements Constants,
        View.OnClickListener, View.OnTouchListener {

    // is mobile went to sleep or player pause
    private boolean activityRunning;
    // are we already playing?
    private boolean isPlaying = false;
    // when dialog to choose next or repeat visible
    private boolean isGameInEndedPhase = false;
    private int curScreenId = -1;
    private int curPlayType = -1;

    // variables to access the game surface
    private SurfaceHolder surfaceHolder;
    private GameSurface gameSurface;
    // the surface view container in game screen
    private FrameLayout surfaceview_container;
    private Context gameContext;
    private Typeface skranji_reg_font;
    private Typeface skranji_bold_font;

    private GridLayout mainGrid, mainGrid2;
    private SharedPreferences sharedPref;

    private Dialog gameFinishDialog;
    private Dialog settingsDialog;
    private Dialog dialog;

    private int currLevel;
    private int currLevel2;

    private Animation myAnim;
    private Animation swing;

    /**
     * Called when the activity is first created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // Set fullscreen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        // set layout file
        setContentView(R.layout.activity_main);
        initialization();
        Log.d(TAG, "on create");
        // finish with splash
        //TODO: wait for 5 seconds
        switchToScreen(R.id.screen_splash);
        runOnUiThread(new Runnable() {
            //
            @Override
            public void run() {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //add your code here
                        switchToMainScreen();
                    }
                }, 2000);


            }
        });


        initializeElementsInfo();

    }


    private void initializeElementsInfo() {

        // intitlize dateHolder
        DataHolder dataHolder = DataHolder.getInstance();
        ArrayList<Element> elements = new ArrayList<Element>();
        ArrayList<Compound> compounds = new ArrayList<Compound>();
        if (dataHolder.getElements() == null || dataHolder.getElements().size() == 0) {
            try {
                AssetManager am = getAssets();// If this line gives you ERROR then try AssetManager am=getActivity().getAssets();
                InputStream is = am.open("data/dataa.xls");
                WorkbookSettings ws = new WorkbookSettings();
                ws.setEncoding("Cp1252");
                Workbook wb = Workbook.getWorkbook(is, ws);


                Sheet s = wb.getSheet(0);

                int row = s.getRows();
                int col = s.getColumns();
                String value = "";
                for (int i = 1; i < row; i++) {
                    Element el = new Element();
                    for (int c = 0; c < col; c++) {
                        Cell z = s.getCell(c, i);

                        value = z.getContents().trim();
                        switch (c) {
                            case 0:// atomic
                                el.setAtomicNumber(Integer.parseInt(value));
                                break;
                            case 1:// symbol
                                el.setSymbol(value);
                                break;
                            case 2:// group
                                el.setColumnNum(Integer.parseInt(value));
                                break;
                            case 3://period
                                el.setRowNum(Integer.parseInt(value));
                                break;
                            case 4://turkish name
                                el.setNameTurk(value);
                                break;
                            case 5://name english
                                el.setName(value);
                                break;
                            case 6: //turkish color
                                el.setColorTurk(value);
                                break;
                            case 7://color
                                el.setColor(value);
                                break;
                            case 8://year
                                el.setYear(value);
                                break;
                            case 9: // turkish phase
                                el.setStateTurk(value);
                                break;
                            case 10://phase
                                el.setState(value);
                                break;
                            case 11:// turkish series
                                el.setTypeTurk(value);
                                break;
                            case 12://Series type
                                el.setType(value);
                                break;
                            case 13://oldGroup
                                el.setOldGroup(value);
                                break;
                            case 14://block
                                el.setBlock(value);
                                break;
                            case 15://nutron
                                el.setNumOfNutrons(Integer.parseInt(value));
                                break;

                        }

                    }
                    elements.add(el);
                }
                DataHolder.getInstance().setElements(elements);

                // compounds

                s = wb.getSheet(1);
                row = s.getRows();
                col = s.getColumns();
                value = "";
                for (int i = 1; i < row; i++) {
                    Compound el = new Compound();
                    for (int c = 0; c < col; c++) {
                        Cell z = s.getCell(c, i);
                        value = z.getContents().trim();
                        switch (c) {
                            case 0:// evel number
                                el.setLevelNum(Integer.parseInt(value));
                                break;
                            case 1:// symbol
                                el.setSymbol(value);
                                break;
                            case 2:// english name
                                el.setName(value);
                                break;
                            case 3://second english name
                                el.setSecondName(value);
                                break;
                            case 4://turkish name
                                el.setName_t(value);
                                break;
                            case 5://second turkish name
                                el.setSecondName_t(value);
                                break;
                            case 6: //bond
                                el.setBond(value);
                                break;
                            case 7://bond t
                                el.setBond_t(value);
                                break;
                            case 8://type
                                el.setType(value);
                                break;
                            case 9: // turkish type
                                el.setType_t(value);
                                break;
                            case 10://elements
                                el.setTxtElements(value);
                                break;
                            case 11://elements
                                el.setTxtMovingElements(value);
                                break;
                        }

                    }
                    compounds.add(el);
                }
                DataHolder.getInstance().setCompounds(compounds);


            } catch (Exception e) {
                Log.e("error readin excel", "damn");
            }
            Log.d("excel", "size" + DataHolder.getInstance().getCompounds().size());
        }

    }

    /**
     * To initialize component in the activity
     */
    private void initialization() {
// font creation from assets
//        skranji_reg_font = Typeface.createFromAsset(this.getAssets(), "fonts/trajan_pro_bold.ttf");
        skranji_reg_font = Typeface.createFromAsset(this.getAssets(), "fonts/Skranji-Regular.ttf");
        skranji_bold_font = Typeface.createFromAsset(this.getAssets(), "fonts/Skranji-Bold.ttf");
        // get context
        gameContext = this;

        setFontToButtons();

        // set up a click listener for everything we care about
        for (int id : CLICKABLES) {
            findViewById(id).setOnClickListener(this);

        }

        mainGrid = (GridLayout) findViewById(R.id.mainGrid);
        mainGrid2 = (GridLayout) findViewById(R.id.mainGrid2);

        sharedPref = gameContext.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        // custom dialog
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        // make background transparent
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // game finish dialog
        gameFinishDialog = new Dialog(this);
        gameFinishDialog.setCanceledOnTouchOutside(false);
        gameFinishDialog.setCancelable(false);
        gameFinishDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        gameFinishDialog.setContentView(R.layout.game_finish_dialog);
        gameFinishDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        settingsDialog = new Dialog(this);
        settingsDialog.setCanceledOnTouchOutside(false);
        settingsDialog.setCancelable(false);
        settingsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        settingsDialog.setContentView(R.layout.settings_dialog);
        settingsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        // read settings from shared preferences
        boolean isEnglish = sharedPref.getBoolean(getString(R.string.is_english_key), true);
        boolean isLeftHand = sharedPref.getBoolean(getString(R.string.is_left_hand_key), true);
        boolean isSoundsOff = sharedPref.getBoolean(getString(R.string.is_sounds_off_key), false);
        int leftFreeLevels = sharedPref.getInt(getString(R.string.left_free_levels_key), 5);
        int leftFreeLevels2 = sharedPref.getInt(getString(R.string.left_free_levels_key2), 3);
        DataHolder dh = DataHolder.getInstance();
        dh.setSETTINGS_IS_ENGLISH(isEnglish);
        dh.setSETTINGS_IS_LEFT_HAND(isLeftHand);
        dh.setSETTINGS_IS_SOUNDS_OFF(isSoundsOff);
        dh.setLeftFreeLevels(leftFreeLevels);
        dh.setLeftFreeLevels2(leftFreeLevels2);

        myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);

        swing =AnimationUtils.loadAnimation(getApplicationContext(), R.anim.swings2);

    }

    private void setSingleEvent(GridLayout mainGrid) {
        //Loop all child item of Main Grid
        int levelIndex = 0;
        DataHolder dataHolder = DataHolder.getInstance();
        final int currentLevel = sharedPref.getInt(getString(R.string.saved_high_level_key), 1);
        for (int i = 0; i < mainGrid.getChildCount(); i++) {
            //You can see , all child item is CardView , so we just cast object to CardView
            CardView cardView = (CardView) mainGrid.getChildAt(i);

//            if (cardView.getElevation() == 0) {
            if (cardView.getTag().equals("NotLevelTag")) {

//               Toast.makeText(gameContext,"invisible",Toast.LENGTH_SHORT).show();
                continue;
            }

            levelIndex++;
            int finalLeveLIndex = levelIndex;

            if (levelIndex >= 57 && levelIndex <= 73) {
                finalLeveLIndex += 15;
            } else if (levelIndex >= 74 && levelIndex <= 88) {
                finalLeveLIndex += 30;

            } else if (levelIndex >= 89 && levelIndex <= 103) {
                finalLeveLIndex -= 32;
            } else if (levelIndex >= 104 && levelIndex <= 118) {
                finalLeveLIndex -= 15;
            }
            final int finalI = finalLeveLIndex;
//            if((i >=1 && i <= 16) || i>=){
//
//            }

//            cardView.setBackgroundColor(getResources().getColor(getLevelColor(finalI)));


            GridLayout gridLayout = (GridLayout) cardView.findViewWithTag("lockGrid");
//            ImageView image = (ImageView) cardView.findViewWithTag("lockedLevelImage");
            gridLayout.setVisibility(finalI > currentLevel ? View.VISIBLE : View.INVISIBLE);
//            image.setVisibility(View.VISIBLE);

            FrameLayout frameLayout = (FrameLayout) cardView.findViewWithTag("cardFrame");
            frameLayout.setBackgroundResource(finalI > currentLevel ? R.drawable.gray_base_color1 : 0);
            TextView symbolText = (TextView) cardView.findViewWithTag("levelSymbolTextView");
            symbolText.setText(dataHolder.getElementByLevel(finalI - 1).getSymbol());
            symbolText.setVisibility(View.VISIBLE);
            symbolText.setTextColor(getResources().getColor(finalI > currentLevel ? R.color.textGrayColor : R.color.textBlackColor));
            symbolText.setTypeface(skranji_reg_font);

            TextView levelNumText = (TextView) cardView.findViewWithTag("levelNumberTextView");
            levelNumText.setTypeface(skranji_reg_font);
            levelNumText.setText(finalI + "");
            levelNumText.setTextColor(getResources().getColor(finalI > currentLevel ? R.color.textGrayColor : R.color.textBlackColor));
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!isPlaying && finalI <= currentLevel) {
                        keepScreenOn();
                        startGame(finalI - 1, Constants.GAME_ATOM_TYPE);
                    } else {
                        view.startAnimation(myAnim);
                    }


                }
            });
        }
    }

    private void setSingleEvent2(GridLayout mainGrid) {
        //Loop all child item of Main Grid
        int levelIndex = 0;
        DataHolder dataHolder = DataHolder.getInstance();
        final int currentLevel = sharedPref.getInt(getString(R.string.saved_high_level_key2), 1);
        for (int i = 0; i < mainGrid.getChildCount(); i++) {
            //You can see , all child item is CardView , so we just cast object to CardView
            CardView cardView = (CardView) mainGrid.getChildAt(i);


            levelIndex++;
            final int finalI = levelIndex;


            GridLayout gridLayout = (GridLayout) cardView.findViewWithTag("lockGrid");
            gridLayout.setVisibility(finalI > currentLevel ? View.VISIBLE : View.INVISIBLE);

            FrameLayout frameLayout = (FrameLayout) cardView.findViewWithTag("cardFrame");
            if (finalI <= currentLevel) {
                frameLayout.setBackgroundColor(getResources().getColor(R.color.compound_Level_color));
            }
//            @color/compound_Level_color

            TextView symbolText = (TextView) cardView.findViewWithTag("MolecTextView");
//            symbolText.setText(Html.fromHtml(getString(R.string.test_string2)));

            symbolText.setText(getCompoundSymbol(finalI));
            symbolText.setVisibility(View.VISIBLE);
            symbolText.setTextColor(getResources().getColor(finalI > currentLevel ? R.color.textGrayColor : R.color.white));
            symbolText.setTypeface(skranji_bold_font);

            TextView levelNumText = (TextView) cardView.findViewWithTag("levelTextView");
            levelNumText.setTypeface(skranji_reg_font);
            levelNumText.setText(finalI + "");
            levelNumText.setTextColor(getResources().getColor(finalI > currentLevel ? R.color.textGrayColor : R.color.white));
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!isPlaying && finalI <= currentLevel) {
                        keepScreenOn();
                        startGame(finalI - 1, Constants.GAME_MOLECULE_TYPE);
                    } else {
                        view.startAnimation(myAnim);
                    }


                }
            });
        }
    }

    private SpannableStringBuilder getCompoundSymbol(int level) {
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

    private int getLevelColor(int level) {

        /*    <color name="baseColor1">#3f3</color><!--Non Metal-->
    <color name="baseColor2">#fc3</color><!--Alkali Metals-->
    <color name="baseColor3">#ff3</color><!--Alkaline Earth-->
    <color name="baseColor4">#c99</color><!--Transition Metal-->
    <color name="baseColor5">#6cf</color><!--Noble Gas-->
    <color name="baseColor6">#3fc</color><!--Halogen-->
    <color name="baseColor7">#6c9</color><!--Semi Metal-->
    <color name="baseColor8">#9cc</color><!--Basic Metal-->
    <color name="baseColor9">#fc9</color><!--Lanthanides-->
    <color name="baseColor10">#fcc</color><!--Actinides-->*/

        if (level == 1 || (level >= 6 && level <= 8) || level == 15 || level == 16 || level == 34) {
            return R.color.baseColor1;
//            return R.drawable.gray_base_color1;

        } else if (level == 3 || level == 11 || level == 19 || level == 37 || level == 55 || level == 87) {
            return R.color.baseColor2;
        } else if (level == 4 || level == 12 || level == 20 || level == 38 || level == 56 || level == 88) {
            return R.color.baseColor3;
        } else if (level == 5 || level == 14 || level == 32 || level == 33 || level == 51 || level == 52 || level == 84) {
            return R.color.baseColor7;
        } else if (level == 9 || level == 17 || level == 35 || level == 53 || level == 85 || level == 117) {
            return R.color.baseColor6;
        } else if (level == 2 || level == 10 || level == 18 || level == 36 || level == 54 || level == 86 || level == 118) {
            return R.color.baseColor5;
        } else if (level == 13 || level == 31 || level == 49 || level == 50 || level == 81 || level == 82 || level == 83 || (level >= 113 && level <= 116)) {
            return R.color.baseColor8;
        } else if (level >= 57 && level <= 71) {
            return R.color.baseColor9;
        } else if (level >= 89 && level <= 103) {
            return R.color.baseColor10;
        } else {
            return R.color.baseColor4;
        }
    }

    /**
     * set the font on buttons
     */
    private void setFontToButtons() {

//        ((Button) findViewById(R.id.button_single_player)).setTypeface(skranji_reg_font);
//        ((Button) findViewById(R.id.button_quick_game)).setTypeface(skranji_reg_font);
//        ((Button) findViewById(R.id.button_invite_players)).setTypeface(skranji_reg_font);
//        ((Button) findViewById(R.id.button_see_invitations)).setTypeface(skranji_reg_font);

    }

    @Override
    public void onClick(View v) {
//        v.startAnimation(swing);
        switch (v.getId()) {
            case R.id.button_play1:


                // play Atom game
                switchToScreen(R.id.screen_levels);
                break;
            case R.id.button_play_2:
                // play Molecules game
                switchToScreen(R.id.screen_levels2);
//                switchToScreen(R.id.screen_wait);
                break;
            case R.id.button_settings:
                showSettingsDialog();
                break;

        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean toReturn = false;
        switch (v.getId()) {

        }
        return false;
    }

    // Activity interfaces
    @Override
    public void onPause() {
        super.onPause();
        activityRunning = false;
        Log.d(TAG, "onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        activityRunning = true;
        Log.d(TAG, "onResume");
    }

    // Activity is going to the background. We have to leave the current room.
    @Override
    public void onStop() {
        Log.d(TAG, "**** got onStop");
        // if we're in a room, leave it.
        leaveRoom(true);
        // stop trying to keep the screen on
//        stopKeepingScreenOn();
//        switchToMainScreen();

        super.onStop();
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        leaveRoom(true);
        activityRunning = false;
//        switchToScreen(R.id.screen_splash);
        super.onDestroy();
    }

    // Handle back key to make sure we cleanly leave a game if we are in the middle of one
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {


            if (curScreenId == R.id.screen_game) {
                // pressing back while we are in screen game
                // should show a dialog and if yes go to main screen (leaveRoom)
//                leaveRoom();
                showLeaveGameDialog();
                return true;
            } else if (curScreenId == R.id.screen_main) {
                // should show dialog and if yes finsih the game activity
                showExitGameDialog();
                return false;
            } else if (curScreenId == R.id.screen_levels || curScreenId == R.id.screen_levels2) {
                switchToScreen(R.id.screen_main);
                return true;
            }
        }
        return super.onKeyDown(keyCode, e);
    }

    /**
     * show confirmation dialog when user tries to leave the game during session
     */
    private void showLeaveGameDialog() {
        // set the custom dialog components - text, image and button
        boolean isEnglish = DataHolder.getInstance().isSETTINGS_IS_ENGLISH();
        TextView text = (TextView) dialog.findViewById(R.id.textViewTitle);
        text.setTypeface(skranji_bold_font);
        if (isEnglish)
            text.setText(R.string.leave_game_label);
        else
            text.setText(R.string.leave_game_label_t);

        TextView message = (TextView) dialog.findViewById(R.id.textViewMessage);
        message.setTypeface(skranji_reg_font);
        if (isEnglish)
            message.setText(R.string.areyou_sure_label);
        else
            message.setText(R.string.areyou_sure_label_t);

        Button dialogOkButton = (Button) dialog.findViewById(R.id.settingsYesBtn);
        // if button is clicked, close the custom dialog
        dialogOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // user wants to sign out
                // sign out.
                Log.d(TAG, "leave the game during session");
                dialog.dismiss();
                leaveRoom(false);
            }
        });
        Button dialogCancelButton = (Button) dialog.findViewById(R.id.settingNoBtn);
        // if button is clicked, close the custom dialog
        dialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * show confirmation dialog when user tries to exit the game
     */
    private void showExitGameDialog() {
        boolean isEnglish = DataHolder.getInstance().isSETTINGS_IS_ENGLISH();
        // set the custom dialog components - text, image and button
        TextView text = (TextView) dialog.findViewById(R.id.textViewTitle);
        text.setTypeface(skranji_bold_font);
        if (isEnglish)
            text.setText(R.string.exit_game_label);
        else
            text.setText(R.string.exit_game_label_t);
        TextView message = (TextView) dialog.findViewById(R.id.textViewMessage);
        message.setTypeface(skranji_reg_font);
        if (isEnglish)
            message.setText(R.string.areyou_sure_label);
        else
            message.setText(R.string.areyou_sure_label_t);

        Button dialogOkButton = (Button) dialog.findViewById(R.id.settingsYesBtn);

        // if button is clicked, close the custom dialog
        dialogOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // user wants to sign out
                // sign out.
                Log.d(TAG, "Exist the game");
                dialog.dismiss();
                MainActivity.this.finish();


            }
        });
        Button dialogCancelButton = (Button) dialog.findViewById(R.id.settingNoBtn);
        // if button is clicked, close the custom dialog
        dialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    // Show error message about game being cancelled and return to main screen.
    private void showGameError() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.game_problem))
                .setNeutralButton(android.R.string.ok, null).show();
        switchToMainScreen();

    }

    private void showGameError(String msg) {
        new AlertDialog.Builder(this)
                .setMessage(msg)
                .setNeutralButton(android.R.string.ok, null).show();
        switchToMainScreen();

    }

//    private void startGameAgain() {
//        // clear surface
//        if (surfaceview_container != null)
//            surfaceview_container.removeAllViews();
//        if (surfaceHolder != null && gameSurface != null)
//            gameSurface.surfaceDestroyed(surfaceHolder);
//
//        // reset flags
//        isGameInEndedPhase = false;
//        startGame(0);
//
//    }

    // Start the gameplay phase of the game.
    private void startGame(int level, int playType) {

        curPlayType = playType;
        isPlaying = true;
        if (playType == Constants.GAME_ATOM_TYPE) {
            currLevel = level;
        } else {
            currLevel2 = level;
        }

        switchToScreen(R.id.screen_game);

        surfaceview_container =
                (FrameLayout) findViewById(R.id.surfaceViewContainer);
        SurfaceView surfaceView = new GameSurface(this, playType);
        surfaceHolder = surfaceView.getHolder();
        gameSurface = (GameSurface) surfaceView;
        if (surfaceview_container != null)
            surfaceview_container.removeAllViews();
        // add our surface view
        surfaceview_container.addView(surfaceView);


        gameSurface.startGame(level, playType);
    }

    public void switchToScreen(int screenId) {


        // make the requested screen visible; hide all others.
        for (int id : SCREENS) {
            try {
                findViewById(id).setVisibility(screenId == id ? View.VISIBLE : View.GONE);
            } catch (Exception e) {
                Log.d("ayham Error", "exception " + e);
            }

        }

        if (screenId == R.id.screen_main) {
            curPlayType = -1;
            changeMainButtonsLanguage();
            // stop animation if exists

        } else if (screenId == R.id.screen_splash) {
            // start animation
            ImageView char2Down = (ImageView) findViewById(R.id.char2);
            ImageView power = (ImageView) findViewById(R.id.power);
            ImageView char1Top = (ImageView) findViewById(R.id.char1);
            ImageView electron = (ImageView) findViewById(R.id.electron);
            ImageView title = (ImageView) findViewById(R.id.titleImage);


            Animation animToRight =
                    AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move1);
            Animation animToRight1 =
                    AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move11);
            Animation animToLeft =
                    AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move2);
            Animation animToLeft1 =
                    AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move22);
            Animation swing =
                    AnimationUtils.loadAnimation(getApplicationContext(), R.anim.swing);
            title.startAnimation(swing);
            char1Top.startAnimation(animToLeft);
            power.startAnimation(animToRight);
            char2Down.startAnimation(animToRight1);
            electron.startAnimation(animToLeft1);


        } else if (screenId == R.id.screen_levels) {
            //Set Event
            setSingleEvent(mainGrid);
        } else if (screenId == R.id.screen_levels2) {
            //Set Event
            setSingleEvent2(mainGrid2);
        } else if (screenId == R.id.screen_wait) {
            // start animation of characters
//            ((AnimationDrawable) waitingFireIV.getBackground()).start();
//            ((AnimationDrawable) waitingWaterIV.getBackground()).start();
        } else {
//            ((AnimationDrawable) waitingFireIV.getBackground()).stop();
//            ((AnimationDrawable) waitingWaterIV.getBackground()).stop();
        }
        curScreenId = screenId;
    }

    private void changeMainButtonsLanguage() {
        boolean isEnglish = DataHolder.getInstance().isSETTINGS_IS_ENGLISH();

        if (isEnglish) {
            ((LinearLayout) findViewById(R.id.main_menu_layout)).setBackground(getResources().getDrawable(R.drawable.main_menu));
            ((Button) findViewById(R.id.button_play1)).setBackground(getResources().getDrawable(R.drawable.btn1_e));
            ((Button) findViewById(R.id.button_play_2)).setBackground(getResources().getDrawable(R.drawable.btn2_e));
//            ((Button) findViewById(R.id.button_play_3)).setBackground(getResources().getDrawable(R.drawable.btn3_e));
            ((Button) findViewById(R.id.button_settings)).setBackground(getResources().getDrawable(R.drawable.settings_e));


        } else {
            ((LinearLayout) findViewById(R.id.main_menu_layout)).setBackground(getResources().getDrawable(R.drawable.main_menu_t));
            ((Button) findViewById(R.id.button_play1)).setBackground(getResources().getDrawable(R.drawable.btn1_t));
            ((Button) findViewById(R.id.button_play_2)).setBackground(getResources().getDrawable(R.drawable.btn2_t));
//            ((Button) findViewById(R.id.button_play_3)).setBackground(getResources().getDrawable(R.drawable.btn3_t));
            ((Button) findViewById(R.id.button_settings)).setBackground(getResources().getDrawable(R.drawable.settings_t));
        }
    }

    private void switchToMainScreen() {
        switchToScreen(R.id.screen_main);

        for (int id : CLICKABLES) {
            findViewById(id).startAnimation(swing);

        }

//        if (mRealTimeMultiplayerClient != null) {
//            setSignInOutButtonStyle(true);
//        } else {
//            setSignInOutButtonStyle(false);
//        }
    }

    // Sets the flag to keep this screen on. It's recommended to do that during
    // the
    // handshake when setting up a game, because if the screen turns off, the
    // game will be
    // cancelled.
    private void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    // Clears the flag that keeps the screen on.
    private void stopKeepingScreenOn() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * called when game finishes by winning or loosing
     *
     * @param isWinning
     * @param currLevel
     * @param secondsScrore
     */
    public void showGameFinishScreen(final boolean isWinning, final int currLevel, final int secondsScrore) {

        // changes in the UI should be made on main UI thread
        // because this method is called from surface view thread

        if (isWinning) {

            if (curPlayType == Constants.GAME_ATOM_TYPE) {
                //save level
                int achievedLevel = sharedPref.getInt(getString(R.string.saved_high_level_key), 1);
                if (currLevel + 2 > achievedLevel) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt(getString(R.string.saved_high_level_key), currLevel + 2);
//            editor.apply();
                    editor.commit();
                }
            } else {
                //save level
                int achievedLevel = sharedPref.getInt(getString(R.string.saved_high_level_key2), 1);
                if (currLevel + 2 > achievedLevel) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt(getString(R.string.saved_high_level_key2), currLevel + 2);
//            editor.apply();
                    editor.commit();
                }

            }

        }
        runOnUiThread(new Runnable() {
            public void run() {
//                isPlaying = false;
                isGameInEndedPhase = true;
//                leaveRoom();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //add your code here
                        showGameFinishDialog(isWinning, secondsScrore);
                    }
                }, 1000);

            }
        });


    }

    // Leave the room.
    public void leaveRoom(boolean isMainScreen) {
        Log.d(TAG, "Leaving room.");
        isPlaying = false;
        if (surfaceview_container != null)
            surfaceview_container.removeAllViews();
        if (surfaceHolder != null && gameSurface != null)
            gameSurface.surfaceDestroyed(surfaceHolder);

        stopKeepingScreenOn();

        if (isMainScreen)
            switchToMainScreen();
        else {
            if (curPlayType == Constants.GAME_ATOM_TYPE) {

                switchToScreen(R.id.screen_levels);
            } else {
                switchToScreen(R.id.screen_levels2);
            }
        }


    }

    /**
     * show score dialoge when game finishes or ended
     *
     * @param isWin
     * @param secondsScrore
     */
    private void showGameFinishDialog(final boolean isWin, int secondsScrore) {

        // set the custom dialog components - text, image and button
//        TextView text = (TextView) gameFinishDialog.findViewById(R.id.textViewTitle);
//        text.setTypeface(skranji_reg_font);

        ImageView scoresImage = (ImageView) gameFinishDialog.findViewById(R.id.scoreStarsImageView);
        switch (secondsScrore) {
            case 0:
                scoresImage.setImageResource(R.drawable.score0);
                break;
            case 1:
                scoresImage.setImageResource(R.drawable.score1);
                break;
            case 2:
                scoresImage.setImageResource(R.drawable.score2);
                break;
            case 3:
                scoresImage.setImageResource(R.drawable.score3);
                break;
        }

        // set score
        TextView scoreText = (TextView) gameFinishDialog.findViewById(R.id.textViewMessage);
//        scoreText.setTypeface(skranji_reg_font);
        scoreText.setTypeface(skranji_bold_font);
        if (isWin) {
            if (DataHolder.getInstance().isSETTINGS_IS_ENGLISH())
                scoreText.setText(R.string.level_cleared);
            else
                scoreText.setText(R.string.level_cleared_t);
        } else {
            if (DataHolder.getInstance().isSETTINGS_IS_ENGLISH())
                scoreText.setText(R.string.level_faild);
            else
                scoreText.setText(R.string.level_faild_t);

        }
//        scoreText.setText("Time Score : " + secondsScrore);

        Button dialogOkButton = (Button) gameFinishDialog.findViewById(R.id.dialogButtonRepeat);
//        dialogOkButton.setTypeface(skranji_reg_font);
//        dialogOkButton.setText("Play again");
        // if button is clicked, close the custom dialog
        dialogOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameFinishDialog.dismiss();

                // single player mode, play again
                if (curPlayType == Constants.GAME_ATOM_TYPE) {
                    startGame(currLevel, curPlayType);
                } else {
                    startGame(currLevel2, curPlayType);
                }


            }
        });
        Button dialogCancelButton = (Button) gameFinishDialog.findViewById(R.id.dialogButtonMenu);
//        dialogCancelButton.setText("Main Menu");
//        dialogCancelButton.setTypeface(skranji_reg_font);
        // if button is clicked, close the custom dialog
        dialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameFinishDialog.dismiss();

                leaveRoom(false);
            }
        });

        Button dialogForwardButton = (Button) gameFinishDialog.findViewById(R.id.dialogButtonNext);
        dialogForwardButton.setTypeface(skranji_bold_font);
        if (isWin) {
            dialogForwardButton.setText("");
            dialogForwardButton.setBackgroundResource(R.drawable.forward);
        } else {
            if (curPlayType == Constants.GAME_ATOM_TYPE) {
                if (currLevel + 1 < sharedPref.getInt(getString(R.string.saved_high_level_key), 1)) {
                    dialogForwardButton.setText("");
                    dialogForwardButton.setBackgroundResource(R.drawable.forward);
                } else {
                    //show number of free hints
                    int leftFreeLevels = DataHolder.getInstance().getLeftFreeLevels();
                    if (leftFreeLevels <= 0) {
                        dialogForwardButton.setText("");
                        dialogForwardButton.setBackgroundResource(R.drawable.forward_disabled);
                    } else {
                        dialogForwardButton.setText("" + leftFreeLevels);
                        dialogForwardButton.setBackgroundResource(R.drawable.forward_disabled);
                    }

                }
            } else {
//                Constants.GAME_MOLECULE_TYPE
                if (currLevel2 + 1 < sharedPref.getInt(getString(R.string.saved_high_level_key2), 1)) {
                    dialogForwardButton.setText("");
                    dialogForwardButton.setBackgroundResource(R.drawable.forward);
                } else {
                    //show number of free hints
                    int leftFreeLevels = DataHolder.getInstance().getLeftFreeLevels2();
                    if (leftFreeLevels <= 0) {
                        dialogForwardButton.setText("");
                        dialogForwardButton.setBackgroundResource(R.drawable.forward_disabled);
                    } else {
                        dialogForwardButton.setText("" + leftFreeLevels);
                        dialogForwardButton.setBackgroundResource(R.drawable.forward_disabled);
                    }

                }
            }


        }
        // if button is clicked, close the custom dialog
        dialogForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isWin) {
                    gameFinishDialog.dismiss();
                    if (curPlayType == Constants.GAME_ATOM_TYPE) {
                        currLevel++;
                        startGame(currLevel, curPlayType);
                    } else {
                        currLevel2++;
                        startGame(currLevel2, curPlayType);
                    }
                } else {
                    if (curPlayType == Constants.GAME_ATOM_TYPE) {
                        if (currLevel + 1 < sharedPref.getInt(getString(R.string.saved_high_level_key), 1)) {
                            gameFinishDialog.dismiss();
                            currLevel++;
                            startGame(currLevel, Constants.GAME_ATOM_TYPE);
                        } else {
                            //check num of free levels
                            int leftFreeLevels = DataHolder.getInstance().getLeftFreeLevels();
                            if (leftFreeLevels > 0) {
                                leftFreeLevels--;
                                //save new left free levels
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putInt(getString(R.string.left_free_levels_key), leftFreeLevels);

                                int achievedLevel = sharedPref.getInt(getString(R.string.saved_high_level_key), 1);
                                if (currLevel + 2 > achievedLevel) {
                                    editor.putInt(getString(R.string.saved_high_level_key), currLevel + 2);
                                }
                                editor.commit();
                                DataHolder.getInstance().setLeftFreeLevels(leftFreeLevels);
                                gameFinishDialog.dismiss();
                                currLevel++;
                                startGame(currLevel, Constants.GAME_ATOM_TYPE);
                            }
                        }
                    } else {
                        if (currLevel2 + 1 < sharedPref.getInt(getString(R.string.saved_high_level_key2), 1)) {
                            gameFinishDialog.dismiss();
                            currLevel2++;
                            startGame(currLevel2, Constants.GAME_MOLECULE_TYPE);
                        } else {
                            //check num of free levels
                            int leftFreeLevels = DataHolder.getInstance().getLeftFreeLevels2();
                            if (leftFreeLevels > 0) {
                                leftFreeLevels--;
                                //save new left free levels
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putInt(getString(R.string.left_free_levels_key2), leftFreeLevels);

                                int achievedLevel = sharedPref.getInt(getString(R.string.saved_high_level_key2), 1);
                                if (currLevel2 + 2 > achievedLevel) {
                                    editor.putInt(getString(R.string.saved_high_level_key2), currLevel2 + 2);
                                }
                                editor.commit();
                                DataHolder.getInstance().setLeftFreeLevels2(leftFreeLevels);
                                gameFinishDialog.dismiss();
                                currLevel2++;
                                startGame(currLevel2, Constants.GAME_MOLECULE_TYPE);
                            }
                        }
                    }
                }
            }
        });

        gameFinishDialog.show();
    }

    private void showSettingsDialog() {
//        settingsDialog

        // set the custom dialog components - text, image and button
        TextView text = (TextView) settingsDialog.findViewById(R.id.settingsLabel);
        text.setTypeface(skranji_bold_font);


        // set score
        TextView languageLabel = (TextView) settingsDialog.findViewById(R.id.languageLabel);
        TextView playWithLabel = (TextView) settingsDialog.findViewById(R.id.playWithLabel);
        TextView soundsLabel = (TextView) settingsDialog.findViewById(R.id.soundsLabel);
        languageLabel.setTypeface(skranji_bold_font);
        playWithLabel.setTypeface(skranji_bold_font);
        soundsLabel.setTypeface(skranji_bold_font);

        DataHolder dh = DataHolder.getInstance();
        RadioButton englishRB = (RadioButton) settingsDialog.findViewById(R.id.rbEnglishLang);
        RadioButton turkishRB = (RadioButton) settingsDialog.findViewById(R.id.rbTurkishLang);
        englishRB.setTypeface(skranji_reg_font);
        turkishRB.setTypeface(skranji_reg_font);
        if (dh.isSETTINGS_IS_ENGLISH()) {
            englishRB.setChecked(true);
        } else {
            turkishRB.setChecked(true);
        }

        RadioButton leftHandRB = (RadioButton) settingsDialog.findViewById(R.id.rbLeftHand);
        RadioButton rightHandRB = (RadioButton) settingsDialog.findViewById(R.id.rbRightHand);
        leftHandRB.setTypeface(skranji_reg_font);
        rightHandRB.setTypeface(skranji_reg_font);
        if (dh.isSETTINGS_IS_LEFT_HAND()) {
            leftHandRB.setChecked(true);
        } else {
            rightHandRB.setChecked(true);
        }

        RadioButton soundsOffRB = (RadioButton) settingsDialog.findViewById(R.id.rbOff);
        RadioButton soundsOnRB = (RadioButton) settingsDialog.findViewById(R.id.rbOn);
        soundsOffRB.setTypeface(skranji_reg_font);
        soundsOnRB.setTypeface(skranji_reg_font);
        if (dh.isSETTINGS_IS_SOUNDS_OFF()) {
            soundsOffRB.setChecked(true);
        } else {
            soundsOnRB.setChecked(true);
        }

        // set current language
        if (dh.isSETTINGS_IS_ENGLISH()) {
            text.setText(R.string.settings_label);
            languageLabel.setText(R.string.settings_language);
            playWithLabel.setText(R.string.settings_play_with);
            soundsLabel.setText(R.string.settings_sounds);
            englishRB.setText(R.string.radio_english);
            turkishRB.setText(R.string.radio_turkish);
            leftHandRB.setText(R.string.radio_left_hand);
            rightHandRB.setText(R.string.radio_right_hand);
            soundsOffRB.setText(R.string.radio_off);
            soundsOnRB.setText(R.string.radio_on);

        } else {
            text.setText(R.string.settings_label_t);
            languageLabel.setText(R.string.settings_language_t);
            playWithLabel.setText(R.string.settings_play_with_t);
            soundsLabel.setText(R.string.settings_sounds_t);
            englishRB.setText(R.string.radio_english_t);
            turkishRB.setText(R.string.radio_turkish_t);
            leftHandRB.setText(R.string.radio_left_hand_t);
            rightHandRB.setText(R.string.radio_right_hand_t);
            soundsOffRB.setText(R.string.radio_off_t);
            soundsOnRB.setText(R.string.radio_on_t);

        }

        Button dialogOkButton = (Button) settingsDialog.findViewById(R.id.settingsYesBtn);
        // if button is clicked, close the custom dialog
        dialogOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //save selected options

                //language
                boolean isEnglish = ((RadioButton) settingsDialog.findViewById(R.id.rbEnglishLang)).isChecked();
                boolean isLeftHand = ((RadioButton) settingsDialog.findViewById(R.id.rbLeftHand)).isChecked();
                boolean isSoundsOff = ((RadioButton) settingsDialog.findViewById(R.id.rbOff)).isChecked();

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(getString(R.string.is_english_key), isEnglish);
                editor.putBoolean(getString(R.string.is_left_hand_key), isLeftHand);
                editor.putBoolean(getString(R.string.is_sounds_off_key), isSoundsOff);
//            editor.apply();
                editor.commit();
                // update settings constans
                DataHolder dh = DataHolder.getInstance();
                dh.setSETTINGS_IS_ENGLISH(isEnglish);
                dh.setSETTINGS_IS_LEFT_HAND(isLeftHand);
                dh.setSETTINGS_IS_SOUNDS_OFF(isSoundsOff);

                changeMainButtonsLanguage();

                settingsDialog.dismiss();


            }
        });
        Button dialogCancelButton = (Button) settingsDialog.findViewById(R.id.settingNoBtn);
        // if button is clicked, close the custom dialog
        dialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsDialog.dismiss();
            }
        });


        settingsDialog.show();
    }
}
