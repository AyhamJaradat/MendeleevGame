package gameClasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.mandeleev.game.mandeleev.MainActivity;
import com.mandeleev.game.mandeleev.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import chemestryClasses.Compound;
import chemestryClasses.DataHolder;
import chemestryClasses.Element;
import imagesSingleton.EffectImages;
import utils.Constants;

/**
 * Created by Ayham on 12/12/2018.
 */

public class GameSurface extends SurfaceView implements Constants, SurfaceHolder.Callback, CustomCountDownTimer.TimerTickListener {


    private final List<MainCharacter> mainCharacterList = new ArrayList<MainCharacter>();
    private final List<MovingCharacter> movingCharacters = new ArrayList<MovingCharacter>();
    private final List<MovingCharacter> hintCharacters = new ArrayList<MovingCharacter>();
    private final List<CompoundMovingCharacter> compHintCharacters = new ArrayList<CompoundMovingCharacter>();
    private final List<Explosion> explosionList = new ArrayList<Explosion>();
    private Paint rectPaint = new Paint();
    private Paint rectStkPaint = new Paint();
    private Paint profBoxPaint = new Paint();
    private Paint profileRectPaint = new Paint();
    private Paint profileRectSrkPaint = new Paint();
    private Paint defaultPaint = new Paint();
    private Paint profTextPaint1 = new Paint(), profTextPaint2 = new Paint(), profTextPaint3 = new Paint();
    private int profRectWidth, profRectHeight, profRectStartX, profRectStartY, profileMargin;
    private int profPlayX, profPlayY;
    private MainCharacterExtends mainCharacterExtends;
    private MainCharacter happyHintCharacter, sadHintCharacter;
    private CompoundCharacter compoundCharacter;
    private List<CompoundMovingCharacter> comMovingCharList = new ArrayList<CompoundMovingCharacter>();
    //JoyStick variable
    private float centerX;
    private float centerY;
    private float movingX;
    private float movingY;
    private float baseRadius;
    private float hatRadius;
    private GameThread gameThread;
    private Context mainActivityContext = null;
    private String timer = "";
    private boolean isGameplaying;
    private boolean isCharProfile;
    private Paint timerTextPaint, timertimePaint;
    private int secondsScore = 0;
    private int secondLeft = 0;
    private CustomCountDownTimer customCountDownTimer;
    //    private Typeface custom_font;
    // Timer
    private long levelTotalSeconds = (2 * 60 * 1000);
    private long totalSeconds = levelTotalSeconds + (4 * 1000);// 4 seconds for tutorials
    private long intervalSeconds = 1 * 1000;
    private int lastfallingSecond = (int) (levelTotalSeconds / 1000);
    private Bitmap bkBitmap, compoundBitmap,
            character1Bitmap2, electronBitmap1, power1, elementBitmap;
    private Rect dest;
    private RectF hitRectF, profileRectF;
    private Random generator;
    private long lastFallingDrawingTime = -1;
    private boolean isNextFallingFire = false;
    private Bitmap joystickBaseBitmap, joystickHatBitmap;
    private Bitmap plusBitmap, equalBitmap, xBitmap, tickBitmap;
    private Bitmap profPlayBitmap, headrBitmap, compoundProfileBitmap;
    private boolean isFirstTick;
    private Element element;
    private Compound compound;
    private Paint bgPaint = new Paint();
    private Paint horRecPaint = new Paint();
    private Paint horLinePaint = new Paint();
    private Paint elementTextPaint1 = new Paint();
    private Paint elementTextPaint2 = new Paint();
    private Paint plusPaint = new Paint();
    private float hintHeight;
    private float topBarTopSpace = 0;
    private boolean isDoneOneTime = false;
    private GameSurface myGameSurface;
    private int currLevel;
    private int playType;

    private float screenDensity;

    private int fontSize1 = 18;// 55;
    private int fontSize2 = 16;//50;
    private int fontSize3 = 14;//45;
    private int fontSize4 = 12;//30;
    private Shader groupShader, periodShader, elecShader, protShader, nutronShader, missionShader, infoShader, hintBoxShader;
    private Path myArc = new Path();
    private RectF oval = new RectF();

    //    sounds ids
    private int soundIdBackground;
    private int soundIdHit;
    private int soundIdEat;
    private boolean soundPoolLoaded;
    private SoundPool soundPool;

    private Typeface skranji_reg_font;
    private Typeface skranji_bold_font;

    public GameSurface(Context context, int playType) {
        super(context);
        myGameSurface = this;
        this.playType = playType;
        this.mainActivityContext = context;
        // Make Game Surface focusable so it can handle events. .
        this.setFocusable(true);
        // Sét callback.
        this.getHolder().addCallback(this);

        if (!DataHolder.getInstance().isSETTINGS_IS_SOUNDS_OFF())
            this.initSoundPool();
        this.isGameplaying = true;
        this.isCharProfile = true;
        initialization();


        customCountDownTimer = new CustomCountDownTimer(totalSeconds, intervalSeconds, this);

    }

    public GameSurface(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        myGameSurface = this;
        this.setFocusable(true);
        getHolder().addCallback(this);
    }

    public GameSurface(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        myGameSurface = this;
        this.setFocusable(true);
        getHolder().addCallback(this);
    }

    private void setProfileDimensions() {
        int canvasHeight = getHeight();
        int canvasWidth = getWidth();
        profileMargin = Math.min(canvasWidth, canvasHeight) / 12;
        profRectWidth = canvasWidth - (2 * profileMargin);
        profRectHeight = canvasHeight - (2 * profileMargin);

        profRectStartX = profileMargin;
        profRectStartY = profileMargin;

//        profPlayBitmap = Bitmap.createScaledBitmap
//                (profPlayBitmap, (int) ((hintHeight / 2) + (hintHeight) / 3), (int) (hintHeight / 2), false);


        profPlayX = profRectStartX + (profRectWidth / 2) - (profPlayBitmap.getWidth() / 2);
        profPlayY = profRectStartY + profRectHeight - profPlayBitmap.getHeight() - (profileMargin / 2);

    }

    private void setupDimensions() {

        baseRadius = Math.min(getWidth(), getHeight()) / 8;
        hatRadius = Math.min(getWidth(), getHeight()) / 12;
        hintHeight = (hatRadius * 2) / 1.5f;
        float margin = hatRadius;
        if (DataHolder.getInstance().isSETTINGS_IS_LEFT_HAND())
            centerX = baseRadius + margin;//getWidth() / 2;
        else
            centerX = getWidth() - baseRadius - margin;//getWidth() / 2;
        centerY = getHeight() - baseRadius - margin;//getHeight() / 2;
        movingX = centerX;
        movingY = centerY;

        // resize the joystick images
        if (!joystickBaseBitmap.isRecycled()) {
            joystickBaseBitmap = Bitmap.createScaledBitmap
                    (joystickBaseBitmap, (int) (baseRadius * 2), (int) (baseRadius * 2), false);
//        if(!joystickHatBitmap.isRecycled())
            joystickHatBitmap = Bitmap.createScaledBitmap
                    (joystickHatBitmap, (int) (hatRadius * 2), (int) (hatRadius * 2), false);

//        resize hint images
//        if(!equalBitmap.isRecycled())
            tickBitmap = Bitmap.createScaledBitmap
                    (tickBitmap, (int) (2 * hatRadius / 3), (int) (2 * hatRadius / 3), false);

            if (playType == Constants.GAME_ATOM_TYPE) {
                equalBitmap = Bitmap.createScaledBitmap
                        (equalBitmap, (int) (hatRadius / 2), (int) (hatRadius / 2), false);


                xBitmap = Bitmap.createScaledBitmap
                        (xBitmap, (int) ((hintHeight / 2) + (hintHeight) / 3), (int) (hintHeight / 2), false);

                happyHintCharacter.resizeImage((int) (hintHeight));
                sadHintCharacter.resizeImage((int) (hintHeight));

                for (MovingCharacter hintchar : hintCharacters) {
                    hintchar.resizeImage((int) (hintHeight / 2));
                }
            } else {

                for (CompoundMovingCharacter hintchar : compHintCharacters) {
                    hintchar.resizeImage((int) (hintHeight / 2));
                }
            }

//        if(!plusBitmap.isRecycled())
            plusBitmap = Bitmap.createScaledBitmap
                    (plusBitmap, (int) (hatRadius / 2), (int) (hatRadius / 2), false);


        }
    }

    private void drawJoystick(Canvas myCanvas, float newX, float newY) {
        if (getHolder().getSurface().isValid()) {
            //First determine the sin and cos of the angle that the touched point is at relative to the center of the joystick
//            float hypotenuse = (float) Math.sqrt(Math.pow(newX - centerX, 2) + Math.pow(newY - centerY, 2));
//            float sin = (newY - centerY) / hypotenuse; //sin = o/h
//            float cos = (newX - centerX) / hypotenuse; //cos = a/h

            //Draw the base first before shading
            myCanvas.drawBitmap(joystickBaseBitmap, centerX - (joystickBaseBitmap.getWidth() / 2),
                    centerY - (joystickBaseBitmap.getHeight() / 2), null);
            //Drawing the joystick hat
            myCanvas.drawBitmap(joystickHatBitmap, newX - (joystickHatBitmap.getWidth() / 2),
                    newY - (joystickHatBitmap.getHeight() / 2), null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (isCharProfile) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // check if on play button
                //Check if the x and y position of the touch is inside the bitmap
                if (x > profPlayX && x < profPlayX + profPlayBitmap.getWidth() && y > profPlayY && y < profPlayY + profPlayBitmap.getHeight()) {
                    //Bitmap touched
                    startActualGame();
                }
            }

        } else {


            if (event.getAction() != MotionEvent.ACTION_UP) {

                float displacement = (float) Math.sqrt((Math.pow(x - centerX, 2)) + Math.pow(y - centerY, 2));
                if (displacement < baseRadius) {
                    movingX = x;
                    movingY = y;
                    if (this.playType == Constants.GAME_ATOM_TYPE) {
                        this.mainCharacterList.get(0).setMovingVector((x - centerX) / baseRadius, (y - centerY) / baseRadius);
                    } else {

                        this.compoundCharacter.setMovingVector((x - centerX) / baseRadius, (y - centerY) / baseRadius);
                    }

                } else {
                    float ratio = baseRadius / displacement;
                    float constrainedX = centerX + (x - centerX) * ratio;
                    float constrainedY = centerY + (y - centerY) * ratio;
                    movingX = constrainedX;
                    movingY = constrainedY;
                    if (this.playType == Constants.GAME_ATOM_TYPE) {
                        this.mainCharacterList.get(0).setMovingVector((constrainedX - centerX) / baseRadius, (constrainedY - centerY) / baseRadius);
                    } else {

                        this.compoundCharacter.setMovingVector((constrainedX - centerX) / baseRadius, (constrainedY - centerY) / baseRadius);
                    }
                }
            } else /*if (event.getAction() == MotionEvent.ACTION_UP) */ {
                movingX = centerX;
                movingY = centerY;
                if (this.playType == Constants.GAME_ATOM_TYPE) {
                    this.mainCharacterList.get(0).setMovingVector(0, 0);
                } else {

                    this.compoundCharacter.setMovingVector(0, 0);
                }
            }
        }
        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        setupDimensions();

        setProfileDimensions();

        // create Rect for background bitmap
        dest = new Rect(0, 0, getWidth(), getHeight());
        hitRectF = new RectF();
        profileRectF = new RectF();
        topBarTopSpace = (getHeight() / 2) - hintHeight;

        // update chibi posotions and canvuas dimensions
        if (this.playType == Constants.GAME_ATOM_TYPE) {
            for (MainCharacter chibi : mainCharacterList) {
                chibi.updateCanvuasDimensions(this.getHeight(), this.getWidth());
            }

            mainCharacterExtends.updateCanvuasDimensions(this.getHeight(), this.getWidth());
            happyHintCharacter.updateCanvuasDimensions(this.getHeight(), this.getWidth());
            sadHintCharacter.updateCanvuasDimensions(this.getHeight(), this.getWidth());
        } else {
            compoundCharacter.updateCanvuasDimensions(this.getHeight(), this.getWidth());
        }

        lastFallingDrawingTime = System.nanoTime();
        this.gameThread = new GameThread(this, holder);
        this.gameThread.setRunning(true);
        this.gameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        stopSounds();
        customCountDownTimer.cancel();
        this.isGameplaying = false;
        while (retry) {
            try {
                this.gameThread.setRunning(false);

                // Parent thread must wait until the end of GameThread.
                this.gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = false;
        }
        //recycle bitmaps
        bkBitmap.recycle();
        if (this.playType == Constants.GAME_ATOM_TYPE) {
            character1Bitmap2.recycle();
            electronBitmap1.recycle();
            power1.recycle();
            xBitmap.recycle();

            equalBitmap.recycle();
        } else {
            compoundBitmap.recycle();
            compoundProfileBitmap.recycle();
//            elementBitmap.recycle();
        }
        tickBitmap.recycle();
        plusBitmap.recycle();
        joystickBaseBitmap.recycle();
        joystickHatBitmap.recycle();
        profPlayBitmap.recycle();
        headrBitmap.recycle();


    }

    @Override
    public void onTick(long millisUntilFinished) {

        // make timer 5 second more than needed ..
        // in thses first 5 second draw the top bar in the center with
        // move it up until reaches its place. and stop .. with increasing transparentcy
        if (isGameplaying) {

            int difficultyLevel = 1;
            if (this.playType == Constants.GAME_ATOM_TYPE) {
                difficultyLevel = element.getRowNum();
            } else {

                difficultyLevel = compound.getLevelDifficulty();
            }
            secondLeft = (int) (millisUntilFinished / 1000);
            // update second score
            int minLeft = secondLeft / 60;
            if (isGameplaying) {
                if (this.playType == Constants.GAME_ATOM_TYPE && mainCharacterList.get(0).isAlreadyHappy()) {
                    secondsScore = 3;
                } else {
                    if (secondLeft >= 60) {
                        // in first 60 seconds
                        secondsScore = 3;
                    } else if (secondLeft >= 30) {
                        // before last 30 seconds
                        secondsScore = 2;
                    } else {
                        // during last 30 seconds
                        secondsScore = 1;
                    }
                }
            }
            String secondDisplay = "";
            if (secondLeft % 60 < 10) {
                secondDisplay = "0" + String.valueOf(secondLeft % 60);
            } else {
                secondDisplay = String.valueOf(secondLeft % 60);
            }
            if (secondLeft > levelTotalSeconds / 1000) {
                timer = "02:00";

            } else {
                timer = "0" + String.valueOf(minLeft) + ":" + secondDisplay;
                if (topBarTopSpace > 0) {
                    topBarTopSpace = 0;
                }
            }


            // game difficulty level based on time
//            if (secondLeft <= 30) {
//                // last 30 seconds ,, most difficult, new one every 1 second
//                if (lastfallingSecond - secondLeft > 1) {
//
//                    drawNewfallingCharacters(difficultyLevel, element.isNextElectron());
//                    lastfallingSecond = secondLeft;
//                }
//            } else {
            // during normal time ,, new one every 2 seconds
            if (lastfallingSecond - secondLeft > 2) {
                if (this.playType == Constants.GAME_ATOM_TYPE)
                    drawNewfallingCharacters(difficultyLevel, element.isNextElectron());
                else {
                    String nextMovingSymbol = compound.nextMoving();
                    if (!nextMovingSymbol.equals("none"))
                        drawNewfallingCharacters2(difficultyLevel, nextMovingSymbol);
                }
                lastfallingSecond = secondLeft;
            }
//            }
            if (isFirstTick) {
                isFirstTick = false;
                // initialize effects images
                EffectImages.getInstance(myGameSurface);
            }
        }
    }

    private void drawNewfallingCharacters2(int difficultyLevel, String symbol) {

        int x1 = 0;
        int x2 = this.getWidth();
        synchronized (this.comMovingCharList) {
            if (this.isGameplaying) {
                float xRandomPerc = generator.nextInt(101);
                int XRandomPixcel = generator.nextInt(101) < 50 ? x1 : x2;
                int YRandomPixcel = (int) ((xRandomPerc / 100) * this.getHeight());
                CompoundMovingCharacter fallingCharacter = new CompoundMovingCharacter(this, elementBitmap, 1, 1, XRandomPixcel, YRandomPixcel, "any", difficultyLevel, symbol, false);
                this.comMovingCharList.add(fallingCharacter);


                lastFallingDrawingTime = System.nanoTime();
            }
        }

    }

    private void drawNewfallingCharacters(int difficultyLevel, boolean isNextElectron) {

        int x1 = 0;
        int x2 = this.getWidth();
        synchronized (this.movingCharacters) {
            if (this.isGameplaying) {
              /*  if (difficultyLevel == 4 || difficultyLevel == 5 || difficultyLevel == 3) {
                    float xRandomPerc = generator.nextInt(101);
                    int XRandomPixcel = generator.nextInt(101) < 50 ? x1 : x2;
                    int YRandomPixcel = (int) ((xRandomPerc / 100) * this.getHeight());
                    MovingCharacter fallingCharacter = new MovingCharacter(this, electronBitmap1, 1, 3, XRandomPixcel, YRandomPixcel, ELECTRON_TYPE, difficultyLevel, false);
                    this.movingCharacters.add(fallingCharacter);
                    float x1RandomPerc = generator.nextInt(101); //20%
                    int X1RandomPixcel = generator.nextInt(101) < 50 ? x1 : x2;
                    int Y1RandomPixcel = (int) ((x1RandomPerc / 100) * this.getHeight());
                    MovingCharacter fallingCharacter1 = new MovingCharacter(this, power1, 1, 3, X1RandomPixcel, Y1RandomPixcel, POWER_TYPE, difficultyLevel, false);
                    this.movingCharacters.add(fallingCharacter1);
                } else */
                if (isNextElectron) {
                    float xRandomPerc = generator.nextInt(101);
                    int XRandomPixcel = generator.nextInt(101) < 50 ? x1 : x2;
                    int YRandomPixcel = (int) ((xRandomPerc / 100) * this.getHeight());
                    MovingCharacter fallingCharacter = new MovingCharacter(this, electronBitmap1, 1, 3, XRandomPixcel, YRandomPixcel, ELECTRON_TYPE, difficultyLevel, false);
                    this.movingCharacters.add(fallingCharacter);
                } else {

                    float x1RandomPerc = generator.nextInt(101); //20%
                    int X1RandomPixcel = generator.nextInt(101) < 50 ? x1 : x2;
                    int Y1RandomPixcel = (int) ((x1RandomPerc / 100) * this.getHeight());
                    MovingCharacter fallingCharacter1 = new MovingCharacter(this, power1, 1, 3, X1RandomPixcel, Y1RandomPixcel, POWER_TYPE, difficultyLevel, false);
                    this.movingCharacters.add(fallingCharacter1);
                }
                lastFallingDrawingTime = System.nanoTime();
            }
        }

    }

    @Override
    public void onFinish() {
        // could be winning and could be loosing
        if (this.playType == Constants.GAME_ATOM_TYPE) {
            if (mainCharacterList.get(0).isAlreadyHappy()) {
                endOfGame(true);
            } else {
                endOfGame(false);
            }

        } else {
            // with compounds ,, finishing of time means losing
            endOfGame(false);
        }


    }

    @Override
    public void onCancel() {

    }

    private void showStarterDialog() {
//        starterDialog = new Dialog(this.mainActivityContext);
//        starterDialog.setCanceledOnTouchOutside(false);
//        starterDialog.setCancelable(false);
//        starterDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        starterDialog.setContentView(R.layout.message_dialog);
//        starterDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        starterDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //style id
//        // set the custom dialog components - text, image and button
//        TextView text = (TextView) starterDialog.findViewById(R.id.textViewTitle);
//        text.setTypeface(custom_font);
//
//        TextView messageText = (TextView) starterDialog.findViewById(R.id.finalMessage);
//        AnimationDrawable imageAnimation;
//        ImageView characterImage = (ImageView) starterDialog.findViewById(R.id.imageViewCharacter);
//        int color = 0;
//
//        if (this.amIFire) {
//            text.setText("Yor are Fire");
//            color = getResources().getColor(R.color.FireBackgroundColor);
////            text.setTextColor(color);
//            messageText.setText("Survive, and Help Water !");
////            messageText.setTextColor(color);
//            characterImage.setBackgroundResource(R.drawable.moving_fire_animation);
//            imageAnimation = (AnimationDrawable) characterImage.getBackground();
//            imageAnimation.start();
//        } else {
//            text.setText("Yor are Water");
//            color = getResources().getColor(R.color.ButtonTextColor);
////            text.setTextColor(color);
//            messageText.setText("Survive, and Help Fire !");
////            messageText.setTextColor(color);
//            characterImage.setBackgroundResource(R.drawable.moving_water_animation);
//            imageAnimation = (AnimationDrawable) characterImage.getBackground();
//            imageAnimation.start();
//        }
//
//
//        starterDialog.show();
    }

    private void hideStarterDialog() {
//        starterDialog.dismiss();
    }

    private void initialization() {


        screenDensity = getResources().getDisplayMetrics().density;
        bkBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.bk1);
        joystickBaseBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.trans_light_base);
        joystickHatBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.shaded_hat);
        plusBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.plus);


        profPlayBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.play);
        headrBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.header_label);
        if (this.playType == Constants.GAME_ATOM_TYPE) {
            electronBitmap1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.electron2);
            power1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.power1);
            equalBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.equal);
            xBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.bad);

        } else {
            elementBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.atom);

        }
        tickBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.tick);

        skranji_reg_font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Skranji-Regular.ttf");
        skranji_bold_font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Skranji-Bold.ttf");

        // style for word Time left
        timerTextPaint = new Paint();
//        timerTextPaint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/Asimov.otf"));
        timerTextPaint.setTypeface(skranji_bold_font);
        timerTextPaint.setARGB(255, 255, 217, 159);
        timerTextPaint.setTextSize(fontSize4 * screenDensity);
        timerTextPaint.setAntiAlias(true);
        timerTextPaint.setFakeBoldText(true);
        // style of actual time
        timertimePaint = new Paint();
//        timertimePaint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/Asimov.otf"));
        timertimePaint.setTypeface(skranji_bold_font);
        timertimePaint.setARGB(255, 230, 230, 230);
        timertimePaint.setTextSize(fontSize2 * screenDensity);
        timertimePaint.setAntiAlias(true);
        timertimePaint.setShadowLayer(1.5f, 0, 0.5f, 0xFFFFFFFF);
        timertimePaint.setStrokeWidth(1f);
        timertimePaint.setStyle(Paint.Style.FILL);

        bgPaint.setFilterBitmap(true);

        horRecPaint.setColor(Color.WHITE);
        horRecPaint.setAlpha(45);
        horRecPaint.setStyle(Paint.Style.FILL);
        horLinePaint.setColor(Color.WHITE);
        horLinePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        horLinePaint.setStrokeWidth(1f);


        elementTextPaint1.setColor(Color.rgb(95, 44, 130));
        elementTextPaint1.setShadowLayer(20f, 0f, 0f, 0xFFFFFFFF);
//        elementTextPaint1.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/Asimov.otf"));
        elementTextPaint1.setTypeface(skranji_bold_font);
//        elementTextPaint1.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        elementTextPaint1.setTextSize(fontSize1 * screenDensity);

        elementTextPaint2.setColor(Color.argb(150, 255, 255, 255));
        elementTextPaint2.setStyle(Paint.Style.STROKE);
        elementTextPaint2.setStrokeWidth(2);
//        elementTextPaint2.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/Asimov.otf"));
        elementTextPaint2.setTypeface(skranji_bold_font);
//        elementTextPaint2.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        elementTextPaint2.setTextSize(fontSize1 * screenDensity);


//        rectPaint.setColor(Color.argb(120, 95, 44, 130));
        rectPaint.setColor(Color.rgb(95, 44, 130));
//        rectPaint.setAlpha(230);


        rectStkPaint.setStyle(Paint.Style.STROKE);
        rectStkPaint.setColor(Color.argb(220, 0, 0, 0));
//        rectStkPaint.setColor(Color.argb(220,255,255,255));
        rectStkPaint.setStrokeWidth(1);
        rectStkPaint.setShadowLayer(2f, 5f, 4f, 0xFF000000);
        //        rectStkPaint.setShadowLayer(2f, 5f, 4f, 0xFFFFFFFF);

//        plusPaint.setAlpha(120);

        profileRectPaint.setColor(Color.rgb(95, 44, 130));
        profileRectPaint.setAlpha(60);
        profileRectSrkPaint.setColor(Color.rgb(254, 200, 61));
        profileRectSrkPaint.setStyle(Paint.Style.STROKE);
        profileRectSrkPaint.setStrokeWidth(2);
        profileRectSrkPaint.setShadowLayer(2f, 5f, 4f, 0xFF000000);



        /*
        profTextPaint1 : Header + mission
        profTextPaint2 : values
        profTextPaint3 : labels
         */
        profTextPaint1.setTypeface(skranji_bold_font);
        profTextPaint2.setTypeface(skranji_bold_font);
        profTextPaint3.setTypeface(skranji_reg_font);

        profTextPaint1.setTextSize(fontSize1 * screenDensity);
        profTextPaint2.setTextSize(fontSize3 * screenDensity);
        profTextPaint3.setTextSize(fontSize3 * screenDensity);

        profTextPaint1.setColor(Color.argb(222, 255, 255, 255));
        profTextPaint2.setColor(Color.argb(222, 255, 255, 255));
        profTextPaint3.setColor(Color.argb(222, 255, 255, 255));

        profTextPaint1.setShadowLayer(10f, 0f, 0f, Color.rgb(254, 200, 61));
        profTextPaint2.setShadowLayer(5f, 0f, 0f, Color.rgb(254, 200, 61));
        profTextPaint3.setShadowLayer(20f, 0f, 0f, 0xFF000000);

        profBoxPaint.setStyle(Paint.Style.FILL);
        profBoxPaint.setStrokeWidth(2);
        profBoxPaint.setColor(Color.rgb(0, 0, 0));
    }

    private void initSoundPool() {
        // With Android API >= 21.
        if (Build.VERSION.SDK_INT >= 21) {

            AudioAttributes audioAttrib = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setAudioAttributes(audioAttrib).setMaxStreams(MAX_STREAMS);

            this.soundPool = builder.build();
        }
        // With Android API < 21
        else {
            // SoundPool(int maxStreams, int streamType, int srcQuality)
            this.soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        }

        // When SoundPool load complete.
        this.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPoolLoaded = true;


                // Playing char running sounds.
//                startCharRunningSounds();
                playSoundBackground();
            }
        });

        this.soundIdBackground = this.soundPool.load(this.getContext(), R.raw.background, 1);
        this.soundIdHit = this.soundPool.load(this.getContext(), R.raw.hit, 1);
        this.soundIdEat = this.soundPool.load(this.getContext(), R.raw.eat1, 1);

//        this.soundIdWaterRun = this.soundPool.load(this.getContext(), R.raw.humping, 1);
//        this.soundIdFireHit = this.soundPool.load(this.getContext(), R.raw.fire_hit, 1);
//        this.soundIdWaterHit = this.soundPool.load(this.getContext(), R.raw.water_hit, 1);


    }

    public void playSoundExplosion() {

        if (this.soundPoolLoaded) {
            float leftVolumn = 0.2f;
            float rightVolumn = 0.2f;
            // Play sound explosion.wav
//            int streamId = this.soundPool.play(this.soundIdExplosion, leftVolumn, rightVolumn, 1, 0, 1f);
            int streamfireId = this.soundPool.play(this.soundIdHit, leftVolumn, rightVolumn, 1, 0, 1f);

        }
    }
    public void playSoundEat() {

        if (this.soundPoolLoaded) {
            float leftVolumn = 0.2f;
            float rightVolumn = 0.2f;
            // Play sound explosion.wav
//            int streamId = this.soundPool.play(this.soundIdExplosion, leftVolumn, rightVolumn, 1, 0, 1f);
            int streamfireId = this.soundPool.play(this.soundIdEat, leftVolumn, rightVolumn, 1, 0, 1f);

        }
    }


    public void playSoundBackground() {
        if (this.soundPoolLoaded) {
            float leftVolumn = 0.8f;
            float rightVolumn = 0.8f;
            // Play sound background.mp3
            if (this.soundPool != null)
                this.soundPool.play(this.soundIdBackground, leftVolumn, rightVolumn, 1, -1, 1f);
        }
    }

    public void update() {


        if (secondLeft > levelTotalSeconds / 1000 && secondLeft < (levelTotalSeconds / 1000) + 2) {
            // during the last two seconds before the game starts
            // move topbar up
            if (topBarTopSpace > 0) {
                topBarTopSpace -= (hintHeight / 5);
            }
        }

        if (topBarTopSpace < 0) {
            topBarTopSpace = 0;
        }

        if (isCharProfile) {
            if (this.playType == Constants.GAME_ATOM_TYPE)
                mainCharacterExtends.update();
        }
        // update main characters
        if (isGameplaying) {
            if (this.playType == Constants.GAME_ATOM_TYPE) {
                for (MainCharacter chibi : mainCharacterList) {
                    chibi.update();
                }
            } else {
                compoundCharacter.update();
            }
        }
        if (this.playType == Constants.GAME_ATOM_TYPE) {
            happyHintCharacter.update();
            sadHintCharacter.update();
        }
//        // update explosion objects
        synchronized (this.explosionList) {
            for (Explosion explosion : this.explosionList) {
                explosion.update();
            }
        }

//
//        // update falling object positions
        if (this.playType == Constants.GAME_ATOM_TYPE) {
            synchronized (this.movingCharacters) {

                for (MovingCharacter movingCharacter : this.movingCharacters) {
                    movingCharacter.update();
                }

            }
        } else {

            synchronized (this.comMovingCharList) {
                for (CompoundMovingCharacter movingCharacter : this.comMovingCharList) {
                    Log.d("ayham3", "5");
                    movingCharacter.update();
                }
            }
        }
//        // remove falling objects when reach ground
        if (this.playType == Constants.GAME_ATOM_TYPE) {
            synchronized (this.movingCharacters) {
                Iterator<MovingCharacter> movingIterator = this.movingCharacters.iterator();
                while (movingIterator.hasNext()) {
                    MovingCharacter fallingCharacter = movingIterator.next();
                    if (fallingCharacter.isReachGround()) {
                        // add ground hit animation
                        Explosion explosion;
                        if (fallingCharacter.getType().equalsIgnoreCase(ELECTRON_TYPE)) {
                            explosion = new Explosion(this, fallingCharacter.getxCenter(), fallingCharacter.getyCenter(), ELECTRON_DISAPPEAR_EFFECT);
                            this.explosionList.add(explosion);
                        } else if (fallingCharacter.getType().equalsIgnoreCase(POWER_TYPE)) {
                            explosion = new Explosion(this, fallingCharacter.getxCenter(), fallingCharacter.getyCenter(), POWER_DISAPPEAR_EFFECT);
                            this.explosionList.add(explosion);
                        }

                        // falling object reached ground, Remove the current element from the iterator & list.
                        movingIterator.remove();

                        continue;
                    }
                }
            }
        } else {
            //TODO: moc
            //nth because atoms does not disappear
        }
//        // remove explosion object if they finish
        synchronized (this.explosionList) {
            Iterator<Explosion> iterator = this.explosionList.iterator();
            while (iterator.hasNext()) {
                Explosion explosion = iterator.next();
                if (explosion.isFinish()) {
                    // If explosion finish, Remove the current element from the iterator & list.
                    iterator.remove();
                    continue;
                }
            }
        }

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        // draw back ground
        canvas.drawBitmap(bkBitmap, null, dest, bgPaint);
        int canvasWidth = this.getWidth();
        // draw topBar
        if (isCharProfile) {
            // draw char profile view
            if (this.playType == Constants.GAME_ATOM_TYPE) {
                drawCharProfile(canvas, canvasWidth, this.getHeight());
            } else {
                drawCharProfile2(canvas, canvasWidth, this.getHeight());
            }
        } else {
            if (this.playType == Constants.GAME_ATOM_TYPE) {
                drawTopBar(canvas, canvasWidth);
            } else {
                drawTopBar2(canvas, canvasWidth);
            }


            if (secondLeft <= levelTotalSeconds / 1000) {
                // draw joystick
                drawJoystick(canvas, movingX, movingY);

                if (this.playType == Constants.GAME_ATOM_TYPE) {


                    //  draw main character
                    for (MainCharacter chibi : mainCharacterList) {
                        chibi.draw(canvas);
                    }

                    // draw moving characters
                    for (MovingCharacter movingCharacter : this.movingCharacters) {
                        movingCharacter.draw(canvas);
                    }
                } else {

                    compoundCharacter.draw(canvas);
                    // draw moving characters
                    for (CompoundMovingCharacter movingCharacter : this.comMovingCharList) {
                        Log.d("ayham3", "55");
                        movingCharacter.draw(canvas);
                    }
                }

                // check for collisions and death
                if (this.isGameplaying) {
                    if (this.playType == Constants.GAME_ATOM_TYPE)
                        checkForCollision();
                    else {

                        checkForCollision2();
                    }
                }
                for (Explosion explosion : this.explosionList) {
                    explosion.draw(canvas);
                }
            }
        }
    }


    private void drawCharProfile2(Canvas canvas, int canvasWidth, int canvasHeight) {

        profileRectF.set(profRectStartX, profRectStartY, profRectStartX + profRectWidth, profRectStartX + profRectHeight);
        canvas.drawRoundRect(profileRectF, 50, 50, profileRectPaint);
        canvas.drawRoundRect(profileRectF, 50, 50, profileRectSrkPaint);
        // draw play button
        canvas.drawBitmap(profPlayBitmap, profPlayX, profPlayY, defaultPaint);

        // draw compound profile image
        canvas.drawBitmap(compoundProfileBitmap, profRectStartX + profRectWidth / 2, profRectStartY+profileMargin/ 2 /*+ headrBitmap.getHeight() / 2*/, defaultPaint);

        Paint.FontMetrics fm = profTextPaint3.getFontMetrics();
        float height = fm.descent - fm.ascent;
        // draw name and symbol
        int lineMargin = profRectHeight / 4;
        String compondSymbol = compound.getSymbol();

        float headerX = profRectStartX + (profRectWidth / 2) - (headrBitmap.getWidth() / 2);
        float headerY = profRectStartY - (headrBitmap.getHeight() / 2);
        canvas.drawBitmap(headrBitmap, headerX, headerY, defaultPaint);

        float textWidth = profTextPaint1.measureText(compondSymbol);
        float elmNameX = profRectStartX + (profRectWidth / 2) - (textWidth / 2);
        profTextPaint1.setTextSize(fontSize1 * screenDensity);
        drawCompoundSymbol(canvas, elmNameX, profRectStartY + height / 4, true);


        int innerMargin = profileMargin / 3;
        int startX = profRectStartX + profileMargin;

        boolean isEnglish = DataHolder.getInstance().isSETTINGS_IS_ENGLISH();

        // compound Name
        String compoundName1 = compound.getName();
        String compoundName2 = compound.getSecondName();
        if (!compoundName2.equalsIgnoreCase("")) {
            compoundName2 = "( " + compound.getSecondName() + " )";
        }
        // groub /period
        int groubBoxY = profRectStartY + profileMargin + (lineMargin / 2) - 2 * innerMargin;
        int groubWidth = (int) profTextPaint2.measureText(compoundName1) + 2 * innerMargin;
        if (!compoundName2.equalsIgnoreCase("")) {
            groubWidth = Math.max(groubWidth, (int) profTextPaint2.measureText(compoundName2) + 2 * innerMargin);
        }

//        group box
        hitRectF.set(startX, groubBoxY, startX + groubWidth, groubBoxY + (2 * height) + innerMargin);
        if (groupShader == null) {
            groupShader = new RadialGradient(startX + (groubWidth / 2), groubBoxY + ((2 * height) + innerMargin) / 2, ((2 * groubWidth)), Color.rgb(95, 44, 130), Color.BLACK, Shader.TileMode.CLAMP);
        }
        profBoxPaint.setShader(groupShader);
        canvas.drawRoundRect(hitRectF, 20, 20, profBoxPaint);
        if (compoundName2.equalsIgnoreCase("")) {
            //        canvas.drawText(group, startX + ((groubWidth - profTextPaint2.measureText(group)) / 2), groubBoxY + innerMargin + (height / 2), profTextPaint2);
            canvas.drawText(compoundName1, startX + ((groubWidth - profTextPaint2.measureText(compoundName1)) / 2), groubBoxY + 2 * innerMargin + height / 2, profTextPaint2);
        } else {
            canvas.drawText(compoundName1, startX + ((groubWidth - profTextPaint2.measureText(compoundName1)) / 2), groubBoxY + innerMargin + (height / 2), profTextPaint2);
            canvas.drawText(compoundName2, startX + ((groubWidth - profTextPaint2.measureText(compoundName2)) / 2), groubBoxY + 2 * innerMargin + height, profTextPaint2);
        }
        int i = 1;
        int boxesY = (i * lineMargin) + (lineMargin / 2);
        int section2Y = profRectStartY + profileMargin + boxesY - innerMargin;


        String compundType = compound.getType();
        String compoundBond = compound.getBond();
        int electronBoxY = (int) (section2Y /*+ height*/ - innerMargin);
        int elcBoxWidth = (int) profTextPaint3.measureText(compundType) + 2 * innerMargin;
        elcBoxWidth = Math.max(elcBoxWidth, (int) profTextPaint3.measureText(compoundBond) + 2 * innerMargin);
        hitRectF.set(startX, electronBoxY, startX + elcBoxWidth, electronBoxY + (2 * height) + innerMargin);
        if (elecShader == null) {
            elecShader = new RadialGradient(startX + (elcBoxWidth / 2), electronBoxY + ((2 * height) + innerMargin) / 2, ((2 * elcBoxWidth)), Color.rgb(95, 44, 130), Color.BLACK, Shader.TileMode.CLAMP);
        }
        profBoxPaint.setShader(elecShader);
        canvas.drawRoundRect(hitRectF, 20, 20, profBoxPaint);
        canvas.drawText(compundType, startX + ((elcBoxWidth - profTextPaint3.measureText(compundType)) / 2), electronBoxY + innerMargin + (height / 2), profTextPaint3);
        canvas.drawText(compoundBond, startX + ((elcBoxWidth - profTextPaint3.measureText(compoundBond)) / 2), electronBoxY + 2 * innerMargin + height, profTextPaint3);


        i++;
        int infoY = (i * lineMargin) + (lineMargin / 2);
        int section3Y = profRectStartY + profileMargin + infoY;
        // info box
        int infoBoxY = (int) (section3Y - height);
        drawProfileMission2(canvas, startX, infoBoxY);


    }

    private void drawProfileMission2(Canvas canvas, int startX, int boxY) {
        boolean isEnglish = DataHolder.getInstance().isSETTINGS_IS_ENGLISH();
        String missionLabel = isEnglish ? getResources().getString(R.string.mission_label) : getResources().getString(R.string.mission_label_t);
        int hintCharLength = compHintCharacters.get(0).getWidth();
        int rectWidth = 9 * hintCharLength;
        Paint.FontMetrics fHeight = profTextPaint1.getFontMetrics();
        float fHeight1 = fHeight.descent - fHeight.ascent;

//        int startX = profRectStartX + (profRectWidth / 2) + (profPlayBitmap.getWidth() / 2);
        int availableSpace = (profRectWidth / 2) - (profPlayBitmap.getWidth() / 2);
        int recX = startX;
        int rectHeight = 2 * (profPlayBitmap.getHeight() / 3);
        int rectHeight1 = (profPlayBitmap.getHeight() - (profileMargin / 4));
        int recY1 = boxY;// profRectStartY + profRectHeight - rectHeight1 - (profileMargin / 4);
//        int recY = profRectStartY + profRectHeight - rectHeight - (profileMargin / 4);
        int recY = (int) (boxY + fHeight1);


        hitRectF.set(recX, recY1, recX + rectWidth, rectHeight1 + recY1);

        if (missionShader == null) {
            missionShader = new RadialGradient(recX + (rectWidth / 2), recY1 + (rectHeight1 / 2), ((2 * rectHeight1)), Color.rgb(95, 44, 130), Color.BLACK, Shader.TileMode.CLAMP);
        }
        profBoxPaint.setShader(missionShader);
        canvas.drawRoundRect(hitRectF, 30, 30, profBoxPaint);

        int mrg = rectHeight / 2;
        int mrg2 = mrg / 2;
        int x1 = 0, x2 = 0, x3 = 0, x4 = 0, x5 = 0;
        int y = recY + mrg2 + (mrg2 / 4);


        // draw mission text
        canvas.drawText(missionLabel, recX + (profileMargin / 4), recY + (mrg2 / 4), profTextPaint1);


        switch (compHintCharacters.size()) {

            case 2:
                x1 = recX + (rectWidth / 2) - hintCharLength - (hintCharLength / 4);
                x2 = recX + (rectWidth / 2) + (hintCharLength / 4);
                break;
            case 3:

                x1 = recX + (rectWidth / 2) - (2 * hintCharLength);
                x2 = recX + (rectWidth / 2) - (hintCharLength / 2);
                x3 = recX + (rectWidth / 2) + (hintCharLength);
                break;
            case 4:
                x1 = recX + (rectWidth / 2) - 2 * hintCharLength - (hintCharLength / 2) - (hintCharLength / 4);
                x2 = recX + (rectWidth / 2) - hintCharLength - (hintCharLength / 4);
                x3 = recX + (rectWidth / 2) + (hintCharLength / 4);
                x4 = recX + (rectWidth / 2) + (hintCharLength / 4) + hintCharLength + (hintCharLength / 2);
                break;
            case 5:
                x1 = recX + (rectWidth / 2) - (3 * hintCharLength) - (hintCharLength / 2);
                x2 = recX + (rectWidth / 2) - (2 * hintCharLength);
                x3 = recX + (rectWidth / 2) - (hintCharLength / 2);
                x4 = recX + (rectWidth / 2) + (hintCharLength);
                x5 = recX + (rectWidth / 2) + (2 * hintCharLength) + (hintCharLength / 2);
                break;
        }

        int index = 1;
        for (CompoundMovingCharacter hintChar : compHintCharacters) {

            switch (index) {
                case 1:
                    hintChar.setX(x1);
                    break;
                case 2:
                    hintChar.setX(x2);
                    break;
                case 3:
                    hintChar.setX(x3);
                    break;
                case 4:
                    hintChar.setX(x4);
                    break;
                case 5:
                    hintChar.setX(x5);
                    break;
            }
            hintChar.setY(y);
            hintChar.draw(canvas);
            index++;
        }


    }


    private void drawCharProfile(Canvas canvas, int canvasWidth, int canvasHeight) {


        profileRectF.set(profRectStartX, profRectStartY, profRectStartX + profRectWidth, profRectStartX + profRectHeight);
        canvas.drawRoundRect(profileRectF, 50, 50, profileRectPaint);
        canvas.drawRoundRect(profileRectF, 50, 50, profileRectSrkPaint);
        // draw play button
        canvas.drawBitmap(profPlayBitmap, profPlayX, profPlayY, defaultPaint);


        //
        // draw main character
        int offs = ((profRectWidth / 2) - mainCharacterExtends.getWidth()) / 2;
        mainCharacterExtends.setX(profRectStartX + (profRectWidth / 2) + offs);
        mainCharacterExtends.setY(profRectStartY + (profRectHeight / 2) - (mainCharacterExtends.getHeight()));


        mainCharacterExtends.draw(canvas);
        Paint.FontMetrics fm = profTextPaint3.getFontMetrics();
        float height = fm.descent - fm.ascent;
        // draw name and symbol
        int lineMargin = profRectHeight / 4;
        String elementName = element.getName() + " ";

        float headerX = profRectStartX + (profRectWidth / 2) - (headrBitmap.getWidth() / 2);
        float headerY = profRectStartY - (headrBitmap.getHeight() / 2);
        canvas.drawBitmap(headrBitmap, headerX, headerY, defaultPaint);

        float textWidth = profTextPaint1.measureText(elementName);
        float fullTextWidth = textWidth + profTextPaint1.measureText("( " + element.getSymbol() + " ) ");

        float elmNameX = profRectStartX + (profRectWidth / 2) - (fullTextWidth / 2);

//        oval = new RectF(elmNameX,headerY+headrBitmap.getHeight()/2,elmNameX+fullTextWidth,headerY+headrBitmap.getHeight());
        oval.set(elmNameX, headerY + headrBitmap.getHeight() / 2, elmNameX + fullTextWidth, headerY + headrBitmap.getHeight());
        myArc.addArc(oval, -160, 180);
        canvas.drawTextOnPath(elementName + "( " + element.getSymbol() + " )", myArc, 0, 0, profTextPaint1);
//        canvas.drawText(elementName, elmNameX, profRectStartY + height/2, profTextPaint1);
//        canvas.drawText("( " + element.getSymbol() + " )",elmNameX+ textWidth, profRectStartY +height/2, profTextPaint2);


        int innerMargin = profileMargin / 3;
        int startX = profRectStartX + profileMargin;
        if (element.getColumnNum() == 2) {
            startX = profRectStartX + profileMargin / 2;
        }
        boolean isEnglish = DataHolder.getInstance().isSETTINGS_IS_ENGLISH();
        // groub /period
        String groupLabel = isEnglish ? getResources().getString(R.string.group_label) : getResources().getString(R.string.group_label_t);
        String periodLabel = isEnglish ? getResources().getString(R.string.period_label) : getResources().getString(R.string.period_label_t);
        int groubBoxX = startX;
        int groubBoxY = profRectStartY + profileMargin + (lineMargin / 2) - 2 * innerMargin;
        int groubWidth = (int) profTextPaint3.measureText(groupLabel) + 2 * innerMargin;

//        group box
        String group = (element.getColumnNum() == 101 || element.getColumnNum() == 102) ? "N/A" : element.getColumnNum() + "";
        hitRectF.set(startX, groubBoxY, startX + groubWidth, groubBoxY + (2 * height) + innerMargin);
        if (groupShader == null) {
            groupShader = new RadialGradient(startX + (groubWidth / 2), groubBoxY + ((2 * height) + innerMargin) / 2, ((2 * groubWidth)), Color.rgb(95, 44, 130), Color.BLACK, Shader.TileMode.CLAMP);
        }
        profBoxPaint.setShader(groupShader);
        canvas.drawRoundRect(hitRectF, 20, 20, profBoxPaint);
        canvas.drawText(group, startX + ((groubWidth - profTextPaint2.measureText(group)) / 2), groubBoxY + innerMargin + (height / 2), profTextPaint2);
        canvas.drawText(groupLabel, startX + innerMargin, groubBoxY + 2 * innerMargin + height, profTextPaint3);

        // period box
        int periodWidth = (int) profTextPaint3.measureText(periodLabel) + 2 * innerMargin;
        int startX2 = startX + groubWidth + innerMargin;
        hitRectF.set(startX2, groubBoxY, startX2 + periodWidth, groubBoxY + (2 * height) + innerMargin);

        if (periodShader == null) {
            periodShader = new RadialGradient(startX2 + (periodWidth / 2), groubBoxY + ((2 * height) + innerMargin) / 2, ((2 * periodWidth)), Color.rgb(95, 44, 130), Color.BLACK, Shader.TileMode.CLAMP);
        }
        profBoxPaint.setShader(periodShader);
        canvas.drawRoundRect(hitRectF, 20, 20, profBoxPaint);
        canvas.drawText(element.getRowNum() + "", startX2 + ((periodWidth - profTextPaint2.measureText(element.getRowNum() + "")) / 2), groubBoxY + innerMargin + (height / 2), profTextPaint2);
        canvas.drawText(periodLabel, startX2 + innerMargin, groubBoxY + 2 * innerMargin + height, profTextPaint3);

        // atomic number
        String atomicLabel = isEnglish ? getResources().getString(R.string.atomic_n_label) : getResources().getString(R.string.atomic_n_label_t);
        int i = 1;
        int boxesY = (i * lineMargin) + (lineMargin / 2);
        int section2Y = profRectStartY + profileMargin + boxesY - innerMargin;
        canvas.drawText(atomicLabel, startX, section2Y - height / 2, profTextPaint3);
        canvas.drawText(element.getAtomicNumber() + "", startX + profTextPaint3.measureText(atomicLabel) + innerMargin, section2Y - height / 2, profTextPaint2);
//        i++;
        // boexes
        //        electron box
        String electronLabel = isEnglish ? getResources().getString(R.string.electron_label) : getResources().getString(R.string.electron_label_t);
        String neutronLabel = isEnglish ? getResources().getString(R.string.untron_label) : getResources().getString(R.string.untron_label_t);
        String protonLabel = isEnglish ? getResources().getString(R.string.proton_label) : getResources().getString(R.string.proton_label_t);
        int electronBoxY = (int) (section2Y + height - innerMargin);
        int elcBoxWidth = (int) profTextPaint3.measureText(electronLabel) + 2 * innerMargin;
        hitRectF.set(startX, electronBoxY, startX + elcBoxWidth, electronBoxY + (2 * height) + innerMargin);
        if (elecShader == null) {
            elecShader = new RadialGradient(startX + (elcBoxWidth / 2), electronBoxY + ((2 * height) + innerMargin) / 2, ((2 * elcBoxWidth)), Color.rgb(95, 44, 130), Color.BLACK, Shader.TileMode.CLAMP);
        }
        profBoxPaint.setShader(elecShader);
        canvas.drawRoundRect(hitRectF, 20, 20, profBoxPaint);
        canvas.drawText(element.getNumOfElectrons() + "", startX + ((elcBoxWidth - profTextPaint2.measureText(element.getNumOfElectrons() + "")) / 2), electronBoxY + innerMargin + (height / 2), profTextPaint2);
        canvas.drawText(electronLabel, startX + innerMargin, electronBoxY + 2 * innerMargin + height, profTextPaint3);

        //        proton box

        int protonBoxWidth = (int) profTextPaint3.measureText(protonLabel) + 2 * innerMargin;
        startX2 = startX + elcBoxWidth + innerMargin;
        hitRectF.set(startX2, electronBoxY, startX2 + protonBoxWidth, electronBoxY + (2 * height) + innerMargin);
        if (protShader == null) {
            protShader = new RadialGradient(startX2 + (protonBoxWidth / 2), electronBoxY + ((2 * height) + innerMargin) / 2, ((2 * protonBoxWidth)), Color.rgb(95, 44, 130), Color.BLACK, Shader.TileMode.CLAMP);
        }
        profBoxPaint.setShader(protShader);
        canvas.drawRoundRect(hitRectF, 20, 20, profBoxPaint);
        canvas.drawText(element.getNumOfProtons() + "", startX2 + ((protonBoxWidth - profTextPaint2.measureText(element.getNumOfProtons() + "")) / 2), electronBoxY + innerMargin + (height / 2), profTextPaint2);
        canvas.drawText(protonLabel, startX2 + innerMargin, electronBoxY + 2 * innerMargin + height, profTextPaint3);

        //        nutron box

        int nutronBoxWidth = (int) profTextPaint3.measureText(neutronLabel) + 2 * innerMargin;
        int startX3 = startX2 + protonBoxWidth + innerMargin;
        hitRectF.set(startX3, electronBoxY, startX3 + nutronBoxWidth, electronBoxY + (2 * height) + innerMargin);
        if (nutronShader == null) {
            nutronShader = new RadialGradient(startX3 + (nutronBoxWidth / 2), electronBoxY + ((2 * height) + innerMargin) / 2, ((2 * nutronBoxWidth)), Color.rgb(95, 44, 130), Color.BLACK, Shader.TileMode.CLAMP);
        }
        profBoxPaint.setShader(nutronShader);
        canvas.drawRoundRect(hitRectF, 20, 20, profBoxPaint);
        canvas.drawText(element.getNumOfNutrons() + "", startX3 + ((nutronBoxWidth - profTextPaint2.measureText(element.getNumOfNutrons() + "")) / 2), electronBoxY + innerMargin + (height / 2), profTextPaint2);
        canvas.drawText(neutronLabel, startX3 + innerMargin, electronBoxY + 2 * innerMargin + height, profTextPaint3);


        String seriesLabel = isEnglish ? getResources().getString(R.string.type_label) : getResources().getString(R.string.type_label_t);
        String phaseLabel = isEnglish ? getResources().getString(R.string.phase_label) : getResources().getString(R.string.phase_label_t);
        String colorLabel = isEnglish ? getResources().getString(R.string.color_label) : getResources().getString(R.string.color_label_t);
        i++;
        int infoY = (i * lineMargin) + (lineMargin / 2);
        int section3Y = profRectStartY + profileMargin + infoY;
        // info box
        int infoBoxY = (int) (section3Y - height);
        int infoBoxWidth = startX3 - startX;

        int infoBoxWidth2 = (int) (profTextPaint3.measureText(seriesLabel) + 3 * innerMargin + profTextPaint2.measureText(element.getType() + ""));
        if (infoBoxWidth < infoBoxWidth2) {
            infoBoxWidth = infoBoxWidth2;
        }

        hitRectF.set(startX, infoBoxY, startX + infoBoxWidth, infoBoxY + (3 * height) + innerMargin);
        if (infoShader == null) {
            infoShader = new RadialGradient(startX + (infoBoxWidth / 2), infoBoxY + ((3 * height) + innerMargin) / 2, 2 * infoBoxWidth, Color.rgb(95, 44, 130), Color.BLACK, Shader.TileMode.CLAMP);
        }
        profBoxPaint.setShader(infoShader);
        canvas.drawRoundRect(hitRectF, 20, 20, profBoxPaint);

        startX = startX + innerMargin;
        canvas.drawText(phaseLabel, startX, section3Y, profTextPaint3);
        canvas.drawText(element.getState() + "", startX + profTextPaint3.measureText(phaseLabel) + innerMargin, section3Y, profTextPaint2);
//        i++;
        canvas.drawText(seriesLabel, startX, section3Y + height, profTextPaint3);
        canvas.drawText(element.getType() + "", startX + profTextPaint3.measureText(seriesLabel) + innerMargin, section3Y + height, profTextPaint2);
//        i++;
        canvas.drawText(colorLabel, startX, section3Y + 2 * height, profTextPaint3);
        canvas.drawText(element.getColor() + "", startX + profTextPaint3.measureText(colorLabel) + innerMargin, section3Y + 2 * height, profTextPaint2);

        String yearLabel = isEnglish ? getResources().getString(R.string.year_label) : getResources().getString(R.string.year_label_t);
        i++;
        infoY = (i * lineMargin) + (lineMargin / 2);
        int section4Y = profRectStartY + profileMargin + infoY;
        canvas.drawText(yearLabel, startX, section4Y, profTextPaint3);
        canvas.drawText(element.getYear() + "", startX + profTextPaint3.measureText(yearLabel) + innerMargin, section4Y, profTextPaint2);


        drawProfileMission(canvas, canvasWidth);


    }

    private void checkForCollision() {
        Iterator<MainCharacter> iterator = this.mainCharacterList.iterator();
        while (iterator.hasNext()) {
            MainCharacter chibi = iterator.next();
            MovingCharacter fallingCharacter;
            // Water character in Water player OR fire character on Fire player
            for (int i = 0; i < this.movingCharacters.size(); i++) {
                fallingCharacter = this.movingCharacters.get(i);
                double destance = Math.sqrt((fallingCharacter.getyCenter() - chibi.getyCenter()) * (fallingCharacter.getyCenter() - chibi.getyCenter()) + (fallingCharacter.getxCenter() - chibi.getxCenter()) * (fallingCharacter.getxCenter() - chibi.getxCenter()));
                if (destance < (fallingCharacter.getRadius() + chibi.getRadius())) {

                    // collision detected
                    if (chibi.isAlreadyHappy()) {
                        // already happpy

                        //colide with object should disappear
                        // draw explosion effect
                        // Create Explosion object.
                        Explosion explosion;
                        if (fallingCharacter.getType().equalsIgnoreCase(ELECTRON_TYPE)) {

                            explosion = new Explosion(this, chibi.getxCenter(), chibi.getyCenter(), Constants.ELECTRON_EXPLOSION_EFFECT);
                        } else {
                            explosion = new Explosion(this, chibi.getxCenter(), chibi.getyCenter(), Constants.POWER_EXPLOSION_EFFECT);
                        }
                        this.explosionList.add(explosion);
                        // and game level lose
                        this.movingCharacters.remove(i);
                        i--;

                        chibi.makeItHappy(false);
                        // add explosion effect
                        endOfGame(false);


                    } else {
                        // Not happy
                        if (chibi.getNumOfNeededElctrons() > 0) {

                            if (fallingCharacter.getType().equalsIgnoreCase(ELECTRON_TYPE)) {
                                // if colide with electron good
                                // decrement needed electrons
                                // increment current electrons
                                // remove electron moving object
                                // check for win
                                // remove falling object
                                this.movingCharacters.remove(i);
                                i--;
                                // add effect of eating electron
                                chibi.decNumOfNeededElctrons();
                                chibi.incNumOfCurrentElctrons();

//                                sadHintCharacter.incNumOfCurrentElctrons();


                                this.explosionList.add(new Explosion(this, chibi.getxCenter(), chibi.getyCenter(), Constants.GET_ELECTRON_EFFECT));
                                if (chibi.getNumOfNeededElctrons() == 0) {
                                    // WIIN
                                    chibi.makeItHappy(true);
                                    endOfGame(true);
                                }

                            } else if (fallingCharacter.getType().equalsIgnoreCase(POWER_TYPE)) {
                                // not good
                                // draw explosion effect
                                // Create Explosion object.
                                this.explosionList.add(new Explosion(this, chibi.getxCenter(), chibi.getyCenter(), POWER_EXPLOSION_EFFECT));
                                // game lose
                                this.movingCharacters.remove(i);
                                i--;
//                                add explosion effect
                                endOfGame(false);

                            }


                        } else if (chibi.getNumOfNeededElctrons() < 0) {
                            if (fallingCharacter.getType().equalsIgnoreCase(ELECTRON_TYPE)) {
                                // not good
                                // draw explosion effect
                                // Create Explosion object.

                                this.explosionList.add(new Explosion(this, chibi.getxCenter(), chibi.getyCenter(), Constants.ELECTRON_EXPLOSION_EFFECT));
                                // game lose
                                this.movingCharacters.remove(i);
                                i--;
                                endOfGame(false);

                            } else if (fallingCharacter.getType().equalsIgnoreCase(POWER_TYPE)) {
                                // if colide with power good
                                // increment needed electrons
                                // decrement current electrons
                                // remove power moving object
                                // check for win
                                this.movingCharacters.remove(i);
                                i--;
                                // add effect of eating electron
                                chibi.incNumOfNeededElctrons();
                                chibi.decNumOfCurrentElctrons();
//                                sadHintCharacter.decNumOfCurrentElctrons();
//                                lose electron
                                this.explosionList.add(new Explosion(this, chibi.getxCenter(), chibi.getyCenter(), Constants.LOSE_ELECTRON_EFFECT));
                                if (chibi.getNumOfNeededElctrons() == 0) {
                                    // WIIN
                                    chibi.makeItHappy(true);
                                    endOfGame(true);
                                }
                            }
                        }


                    }

                }


            }

        }
    }

    private void checkForCollision2() {


        CompoundCharacter chibi = compoundCharacter;
        CompoundMovingCharacter fallingCharacter;

        for (int i = 0; i < this.comMovingCharList.size(); i++) {
            fallingCharacter = this.comMovingCharList.get(i);
            double destance = Math.sqrt((fallingCharacter.getyCenter() - chibi.getyCenter()) * (fallingCharacter.getyCenter() - chibi.getyCenter()) + (fallingCharacter.getxCenter() - chibi.getxCenter()) * (fallingCharacter.getxCenter() - chibi.getxCenter()));
            if (destance < (fallingCharacter.getRadius() + chibi.getRadius())) {

                // collision detected
                if (chibi.getNextNeededSymbol().equals(fallingCharacter.getSymbol())) {
                    // win good


                    //colide with object should disappear
                    // draw explosion effect
                    // Create Explosion object.
                    this.explosionList.add(new Explosion(this, chibi.getxCenter(), chibi.getyCenter(), Constants.ELECTRON_DISAPPEAR_EFFECT));
                    this.explosionList.add(new Explosion(this, chibi.getxCenter(), chibi.getyCenter(), Constants.POWER_DISAPPEAR_EFFECT));
                    this.explosionList.add(new Explosion(this, chibi.getxCenter(), chibi.getyCenter(), Constants.ELECTRON_DISAPPEAR_EFFECT));
                    this.explosionList.add(new Explosion(this, chibi.getxCenter(), chibi.getyCenter(), Constants.POWER_DISAPPEAR_EFFECT));
                    // and game level lose
                    playSoundEat();
                    this.comMovingCharList.remove(i);
                    i--;

                    boolean isWinLevel = chibi.eatGoodNextSymbol(chibi.getNextNeededSymbol());
//                        // add explosion effect
                    if (isWinLevel)
                        endOfGame(true);


                } else {
                    // Loose
                    this.explosionList.add(new Explosion(this, chibi.getxCenter(), chibi.getyCenter(), Constants.POWER_EXPLOSION_EFFECT));
                    // game lose
                    this.comMovingCharList.remove(i);
                    i--;
//                                add explosion effect
                    endOfGame(false);


                }

            }


        }


    }

    private void drawTopBar2(Canvas canvas, int canvasWidth) {
        drawTimeLeft(canvas, canvasWidth);
        // draw name of element
        String elementName = compound.getName();
        Log.d("namee",elementName);
        float textWidth = elementTextPaint1.measureText(elementName);
        String elementSecondName = compound.getSecondName() + "";
        float symbolWidth = elementTextPaint1.measureText(elementSecondName);
        int xOffset = (int) ((textWidth - symbolWidth) / 2f);

        Paint.FontMetrics fm = elementTextPaint2.getFontMetrics();
        float height = fm.descent - fm.ascent;
        //strok name
        canvas.drawText(elementName, hintHeight, hintHeight , elementTextPaint2);
        //text name
        canvas.drawText(elementName, hintHeight, hintHeight , elementTextPaint1);
        //strok number
        canvas.drawText(elementSecondName, hintHeight + xOffset, (hintHeight ) + height, elementTextPaint2);
        //text number
        canvas.drawText(elementSecondName, hintHeight + xOffset, (hintHeight ) + height, elementTextPaint1);

        // draw hintCharacters

        // background rectangle
//        SpannableStringBuilder combondSymbol = compound.getSymbolByLevel(currLevel + 1);
        String combondSymbol = compound.getSymbol();
        float combondSymbolWidth = profTextPaint1.measureText(combondSymbol);
        int hintCharLength = compHintCharacters.get(0).getWidth();

        float width1 = ((compHintCharacters.size() + compHintCharacters.size() / 2) * hintCharLength);
//        int rectWidth = canvasWidth / 2;
        int rectWidth = ((int) Math.max(combondSymbolWidth, width1)) + 2 * hintCharLength;
        float rectHeight = hatRadius * 2;
        int recX = (canvasWidth - rectWidth) / 2;
        int margin = (int) (15 * screenDensity);
        // set rectangle dimensions and position
        if (topBarTopSpace == 0 && !isDoneOneTime) {
            rectPaint.setAlpha(230);
            for (CompoundMovingCharacter hintChar : compHintCharacters) {
                hintChar.updateAlpha(true);
            }
            isDoneOneTime = true;
        }
        hitRectF.set(recX, margin + topBarTopSpace, recX + rectWidth, rectHeight + margin + topBarTopSpace);


        if (topBarTopSpace != 0) {
            hintBoxShader = new RadialGradient(recX + (rectWidth / 2), margin + topBarTopSpace + (rectHeight / 2), ((2 * rectWidth)), Color.rgb(95, 44, 130), Color.BLACK, Shader.TileMode.CLAMP);
        }
        rectPaint.setShader(hintBoxShader);
        canvas.drawRoundRect(hitRectF, 20, 20, rectStkPaint);
        canvas.drawRoundRect(hitRectF, 20, 20, rectPaint);

        // symbol

        Paint.FontMetrics fHeight = profTextPaint1.getFontMetrics();
        float fHeight1 = fHeight.descent - fHeight.ascent;
        float symbolX = recX + (rectWidth - combondSymbolWidth) / 2;
        profTextPaint1.setTextSize((fontSize1 - 2) * screenDensity);
        float symbolY = margin + topBarTopSpace + fHeight1;
        drawCompoundSymbol(canvas, symbolX, symbolY, false);


        int x1 = 0, x2 = 0, x3 = 0, x4 = 0, x5 = 0, y = 0;


        y = (int) (topBarTopSpace + margin + (rectHeight / 2) + (hintCharLength / 4));
        switch (compHintCharacters.size()) {
            case 2:

                x1 = recX + (rectWidth / 2) - (hintCharLength) - (hintCharLength / 4);
                x2 = recX + (rectWidth / 2) + (hintCharLength / 4);
                break;
            case 3:
                x1 = recX + (rectWidth / 2) - 2 * (hintCharLength);
                x2 = recX + (rectWidth / 2) - (hintCharLength / 2);
                x3 = recX + (rectWidth / 2) + (hintCharLength);
                break;
            case 4:
                x1 = recX + (rectWidth / 2) - (2 * hintCharLength) - (hintCharLength / 2) - (hintCharLength / 4);
                x2 = recX + (rectWidth / 2) - (hintCharLength) - (hintCharLength / 4);
                x3 = recX + (rectWidth / 2) + (hintCharLength / 4);
                x4 = recX + (rectWidth / 2) + (hintCharLength / 4) + (hintCharLength) + (hintCharLength / 2);
                break;
            case 5:
                x1 = recX + (rectWidth / 2) - 3 * (hintCharLength) - (hintCharLength / 2);
                x2 = recX + (rectWidth / 2) - 2 * (hintCharLength);
                x3 = recX + (rectWidth / 2) - (hintCharLength / 2);
                x4 = recX + (rectWidth / 2) + (hintCharLength);
                x5 = recX + (rectWidth / 2) + (2 * hintCharLength) + (hintCharLength / 2);

                break;
        }

        int index = 1;
        for (CompoundMovingCharacter hintChar : compHintCharacters) {

            switch (index) {
                case 1:
                    hintChar.setX(x1);
                    hintChar.setY(y);
                    break;
                case 2:
                    hintChar.setX(x2);
                    hintChar.setY(y);
                    break;
                case 3:
                    hintChar.setX(x3);
                    hintChar.setY(y);
                    break;
                case 4:
                    hintChar.setX(x4);
                    hintChar.setY(y);
                    break;
                case 5:
                    hintChar.setX(x5);
                    hintChar.setY(y);
                    break;
            }

            hintChar.draw(canvas);
            index++;
        }

        //
        int tickWidth = tickBitmap.getWidth() / 4;
//            for (int i=0;i<numberOfAlreadyDone;i++){
        int numberOfAlreadyDone = compoundCharacter.getNumOfCurrentElements();
        switch (numberOfAlreadyDone) {
            case 1:
                // draw on first
                canvas.drawBitmap(tickBitmap, x1 + tickWidth, y - tickWidth, defaultPaint);
                break;
            case 2:
                // draw on first two
                canvas.drawBitmap(tickBitmap, x1 + tickWidth, y - tickWidth, defaultPaint);
                canvas.drawBitmap(tickBitmap, x2 + tickWidth, y - tickWidth, defaultPaint);
                break;
            case 3:
                // draw on first three
                canvas.drawBitmap(tickBitmap, x1 + tickWidth, y - tickWidth, defaultPaint);
                canvas.drawBitmap(tickBitmap, x2 + tickWidth, y - tickWidth, defaultPaint);
                canvas.drawBitmap(tickBitmap, x3 + tickWidth, y - tickWidth, defaultPaint);
                break;
            case 4:
                // draw on forth of them
                canvas.drawBitmap(tickBitmap, x1 + tickWidth, y - tickWidth, defaultPaint);
                canvas.drawBitmap(tickBitmap, x2 + tickWidth, y - tickWidth, defaultPaint);
                canvas.drawBitmap(tickBitmap, x3 + tickWidth, y - tickWidth, defaultPaint);
                canvas.drawBitmap(tickBitmap, x4 + tickWidth, y - tickWidth, defaultPaint);
                break;
            case 5:
                // draw on forth of them
                canvas.drawBitmap(tickBitmap, x1 + tickWidth, y - tickWidth, defaultPaint);
                canvas.drawBitmap(tickBitmap, x2 + tickWidth, y - tickWidth, defaultPaint);
                canvas.drawBitmap(tickBitmap, x3 + tickWidth, y - tickWidth, defaultPaint);
                canvas.drawBitmap(tickBitmap, x4 + tickWidth, y - tickWidth, defaultPaint);
                canvas.drawBitmap(tickBitmap, x5 + tickWidth, y - tickWidth, defaultPaint);
                break;

        }


    }

    private void drawCompoundSymbol(Canvas canvas, float symbolX, float symbolY, boolean isProfile) {

        String combondSymbol = compound.getSymbol();
        if (compound.getLevelNum() == 9 || compound.getLevelNum() == 11 || compound.getLevelNum() == 12
                || compound.getLevelNum() == 13 || compound.getLevelNum() == 15
                || compound.getLevelNum() == 16) {
//            XX2
            String symbol = combondSymbol;
            String firstPart = symbol.substring(0, 2);
            String secondPart = symbol.substring(2, 3);
            profTextPaint1.setTextSize((fontSize1 - 2) * screenDensity);
            canvas.drawText(firstPart, symbolX, symbolY, profTextPaint1);
            float w = profTextPaint1.measureText(firstPart);
            profTextPaint1.setTextSize((fontSize1 - 4) * screenDensity);
            canvas.drawText(secondPart, w + symbolX, symbolY - profTextPaint1.ascent() / 2, profTextPaint1);


        } else if (compound.getLevelNum() == 7 || compound.getLevelNum() == 10) {
            // XX2XX3
            String symbol = combondSymbol;
            String firstPart = symbol.substring(0, 2);
            String secondPart = symbol.substring(2, 3);
            String thirdPart = symbol.substring(3, 5);
            String fourthPart = symbol.substring(5, 6);
            profTextPaint1.setTextSize((fontSize1 - 2) * screenDensity);
            canvas.drawText(firstPart, symbolX, symbolY, profTextPaint1);
            float w = profTextPaint1.measureText(firstPart);
            profTextPaint1.setTextSize((fontSize1 - 4) * screenDensity);
            canvas.drawText(secondPart, w + symbolX, symbolY - profTextPaint1.ascent() / 2, profTextPaint1);
            float w1 = profTextPaint1.measureText(secondPart);
            profTextPaint1.setTextSize((fontSize1 - 2) * screenDensity);
            canvas.drawText(thirdPart, symbolX + w + w1, symbolY, profTextPaint1);
            float w2 = profTextPaint1.measureText(thirdPart);
            profTextPaint1.setTextSize((fontSize1 - 4) * screenDensity);
            canvas.drawText(fourthPart, w + w1 + w2 + symbolX, symbolY - profTextPaint1.ascent() / 2, profTextPaint1);

        } else if (compound.getLevelNum() == 14) {
            //Al2O3
            String symbol = combondSymbol;
            String firstPart = symbol.substring(0, 2);
            String secondPart = symbol.substring(2, 3);
            String thirdPart = symbol.substring(3, 4);
            String fourthPart = symbol.substring(4, 5);
            profTextPaint1.setTextSize((fontSize1 - 2) * screenDensity);
            canvas.drawText(firstPart, symbolX, symbolY, profTextPaint1);
            float w = profTextPaint1.measureText(firstPart);
            profTextPaint1.setTextSize((fontSize1 - 4) * screenDensity);
            canvas.drawText(secondPart, w + symbolX, symbolY - profTextPaint1.ascent() / 2, profTextPaint1);
            float w1 = profTextPaint1.measureText(secondPart);
            profTextPaint1.setTextSize((fontSize1 - 2) * screenDensity);
            canvas.drawText(thirdPart, symbolX + w + w1, symbolY, profTextPaint1);
            float w2 = profTextPaint1.measureText(thirdPart);
            profTextPaint1.setTextSize((fontSize1 - 4) * screenDensity);
            canvas.drawText(fourthPart, w + w1 + w2 + symbolX, symbolY - profTextPaint1.ascent() / 2, profTextPaint1);

        } else if (compound.getLevelNum() == 5 || compound.getLevelNum() == 6) {
            //MgBr2 || CaCl2
            String symbol = combondSymbol;
            String firstPart = symbol.substring(0, 4);
            String secondPart = symbol.substring(4, 5);
            profTextPaint1.setTextSize((fontSize1 - 2) * screenDensity);
            canvas.drawText(firstPart, symbolX, symbolY, profTextPaint1);
            float w = profTextPaint1.measureText(firstPart);
            profTextPaint1.setTextSize((fontSize1 - 4) * screenDensity);
            canvas.drawText(secondPart, w + symbolX, symbolY - profTextPaint1.ascent() / 2, profTextPaint1);
        } else if (compound.getLevelNum() == 8) {
            //H2O
            String symbol = combondSymbol;
            String firstPart = symbol.substring(0, 1);
            String secondPart = symbol.substring(1, 2);
            String thirdPart = symbol.substring(2, 3);
            profTextPaint1.setTextSize((fontSize1 - 2) * screenDensity);
            canvas.drawText(firstPart, symbolX, symbolY, profTextPaint1);
            float w = profTextPaint1.measureText(firstPart);
            profTextPaint1.setTextSize((fontSize1 - 4) * screenDensity);
            canvas.drawText(secondPart, w + symbolX, symbolY - profTextPaint1.ascent() / 2, profTextPaint1);
            float w1 = profTextPaint1.measureText(secondPart);
            profTextPaint1.setTextSize((fontSize1 - 2) * screenDensity);
            canvas.drawText(thirdPart, symbolX + w + w1, symbolY, profTextPaint1);

        } else {
            // no numbers
            canvas.drawText(combondSymbol, symbolX, symbolY, profTextPaint1);
        }
    }

    private void drawTopBar(Canvas canvas, int canvasWidth) {

        drawTimeLeft(canvas, canvasWidth);
        // draw name of element
        String elementName = element.getName();
        float textWidth = elementTextPaint1.measureText(elementName);
        String elemSymbol = element.getAtomicNumber() + "";
        float symbolWidth = elementTextPaint1.measureText(elemSymbol);
        int xOffset = (int) ((textWidth - symbolWidth) / 2f);

        Paint.FontMetrics fm = elementTextPaint2.getFontMetrics();
        float height = fm.descent - fm.ascent;
        //strok name
        canvas.drawText(elementName, hintHeight, hintHeight , elementTextPaint2);
        //text name
        canvas.drawText(elementName, hintHeight, hintHeight , elementTextPaint1);
        //strok number
        canvas.drawText(elemSymbol, hintHeight + xOffset, (hintHeight ) + height, elementTextPaint2);
        //text number
        canvas.drawText(elemSymbol, hintHeight + xOffset, (hintHeight ) + height, elementTextPaint1);

        // draw hintCharacters

        // background rectangle

        int rectWidth = canvasWidth / 2;
        float rectHeight = hatRadius * 2;
        int recX = rectWidth / 2;
        int margin = (int) (15 * screenDensity);
        // set rectangle dimensions and position
        if (topBarTopSpace == 0 && !isDoneOneTime) {
            rectPaint.setAlpha(230);
            happyHintCharacter.updateAlpha(true);
            sadHintCharacter.updateAlpha(true);
            for (MovingCharacter hintChar : hintCharacters) {
                hintChar.updateAlpha(true);
            }
            plusPaint.setAlpha(120);
            isDoneOneTime = true;
        }
        hitRectF.set(recX, margin + topBarTopSpace, recX + rectWidth, rectHeight + margin + topBarTopSpace);


        if (topBarTopSpace != 0) {
            hintBoxShader = new RadialGradient(recX + (rectWidth / 2), margin + topBarTopSpace + (rectHeight / 2), ((2 * rectWidth)), Color.rgb(95, 44, 130), Color.BLACK, Shader.TileMode.CLAMP);
        }
        rectPaint.setShader(hintBoxShader);


        canvas.drawRoundRect(hitRectF, 20, 20, rectStkPaint);
        canvas.drawRoundRect(hitRectF, 20, 20, rectPaint);


        int numOfNeededElectrons = element.getNumOfNeededElectrons();
        int mrg = happyHintCharacter.getWidth() / 2;
        int x1 = 0, y1 = 0, x2 = 0, y2 = 0, x3 = 0, y3 = 0, x4 = 0, y4 = 0;
        int hintCharLength = hintCharacters.get(0).getWidth();


        if (numOfNeededElectrons == 0) {
            //already happy
            happyHintCharacter.setX(recX + (rectWidth / 2) - (happyHintCharacter.getWidth() / 2));
            happyHintCharacter.setY((int) (topBarTopSpace + margin + (rectHeight / 2) - (happyHintCharacter.getHeight() / 2)));


            happyHintCharacter.draw(canvas);

//            x1 = recX  + margin + hintCharLength;
            x1 = recX + mrg + hintCharLength;
            y1 = (int) (topBarTopSpace + margin + (rectHeight / 2) - (hintCharLength / 2));
            x2 = recX + rectWidth - 2 * hintCharLength - mrg;
            y2 = (int) (topBarTopSpace + margin + (rectHeight / 2) - (hintCharLength / 2));

            hintCharacters.get(0).setX(x1);
            hintCharacters.get(0).setY(y1);
            hintCharacters.get(1).setX(x2);
            hintCharacters.get(1).setY(y2);

            hintCharacters.get(0).draw(canvas);
            hintCharacters.get(1).draw(canvas);
            canvas.drawBitmap(xBitmap, x1 - (hintCharLength / 3), y1 + hintCharLength - 15, plusPaint);
            canvas.drawBitmap(xBitmap, x2 - (hintCharLength / 3), y2 + hintCharLength - 15, plusPaint);


        } else {
            // draw plus and equal
            canvas.drawBitmap(plusBitmap, recX + (rectWidth / 3) - (plusBitmap.getWidth() / 2), topBarTopSpace + margin + (rectHeight / 2) - (plusBitmap.getHeight() / 2), plusPaint);
            canvas.drawBitmap(equalBitmap, recX + (2 * (rectWidth / 3)) /*- (equalBitmap.getWidth() / 2)*/, topBarTopSpace + margin + (rectHeight / 2) - (equalBitmap.getHeight() / 2), plusPaint);


            happyHintCharacter.setY((int) (topBarTopSpace + margin + (rectHeight / 2) - (happyHintCharacter.getHeight() / 2)));
            happyHintCharacter.setX(recX + rectWidth - happyHintCharacter.getWidth() - mrg);

            sadHintCharacter.setX(recX + mrg);
            sadHintCharacter.setY((int) (topBarTopSpace + margin + (rectHeight / 2) - (sadHintCharacter.getHeight() / 2)));


            happyHintCharacter.draw(canvas);
            sadHintCharacter.draw(canvas);

            int currentElctrons = mainCharacterList.get(0).getNumOfCurrentElctrons();
            int originalNumber = mainCharacterList.get(0).getOriginalNumOfElctrons();
            int numberOfAlreadyDone = Math.abs(Math.abs(currentElctrons) - originalNumber);

            switch (hintCharacters.size()) {
                case 1:
//                    x1 = recX + (rectWidth / 3) + ((rectWidth / 3) / 2) - (hintCharLength / 2);
                    x1 = recX + (rectWidth / 2) - (hintCharLength / 2);
//                    y1 = (int) (topBarTopSpace + margin + (rectHeight / 2) - (hintCharLength / 2));
                    y1 = (int) (topBarTopSpace + margin + (rectHeight / 2) - (hintCharLength / 2));

                    break;
                case 2:
//                    x1 = recX + (rectWidth / 3) + margin + hintCharLength;
                    x1 = recX + (rectWidth / 2) - (hintCharLength);
                    y1 = (int) (topBarTopSpace + margin + (rectHeight / 2) - (hintCharLength / 2));
//                    x2 = recX + 2 * (rectWidth / 3) - margin - 2 * hintCharLength;
                    x2 = recX + (rectWidth / 2) + (hintCharLength / 2);
                    y2 = (int) (topBarTopSpace + margin + (rectHeight / 2) - (hintCharLength / 2));

                    break;
                case 3:
//                    x1 = recX + (rectWidth / 3) + margin + hintCharLength;
                    x1 = recX + (rectWidth / 2) - (hintCharLength);
//                    y1 = (int) (topBarTopSpace + (rectHeight / 2) - (hintCharLength));
                    y1 = (int) (topBarTopSpace + margin + (rectHeight / 2) - hintCharLength - hintCharLength / 4);
//                    x2 = recX + 2 * (rectWidth / 3) - margin - 2 * hintCharLength;
                    x2 = recX + (rectWidth / 2) + (hintCharLength / 2);
//                    y2 = (int) (topBarTopSpace + (rectHeight / 2) - (hintCharLength));
                    y2 = (int) (topBarTopSpace + margin + (rectHeight / 2) - hintCharLength - hintCharLength / 4);
//                    x3 = recX + (rectWidth / 3) + margin + hintCharLength;
                    x3 = recX + (rectWidth / 2) - (hintCharLength);
                    y3 = (int) (topBarTopSpace + margin + (rectHeight / 2) + (hintCharLength / 4));

                    break;
                case 4:
//                    x1 = recX + (rectWidth / 3) + margin + hintCharLength;
                    x1 = recX + (rectWidth / 2) - (hintCharLength);
//                    y1 = (int) (topBarTopSpace + (rectHeight / 2) - (hintCharLength));
                    y1 = (int) (topBarTopSpace + margin + (rectHeight / 2) - hintCharLength - hintCharLength / 4);
//                    x2 = recX + 2 * (rectWidth / 3) - margin - 2 * hintCharLength;
                    x2 = recX + (rectWidth / 2) + (hintCharLength / 2);
//                    y2 = (int) (topBarTopSpace + (rectHeight / 2) - (hintCharLength));
                    y2 = (int) (topBarTopSpace + margin + (rectHeight / 2) - hintCharLength - hintCharLength / 4);
//                    x3 = recX + (rectWidth / 3) + margin + hintCharLength;
                    x3 = recX + (rectWidth / 2) - (hintCharLength);
//                    y3 = (int) (topBarTopSpace + (rectHeight / 2) + (hintCharLength / 2));
                    y3 = (int) (topBarTopSpace + margin + (rectHeight / 2) + (hintCharLength / 4));
//                    x4 = recX + 2 * (rectWidth / 3) - margin - 2 * hintCharLength;
                    x4 = recX + (rectWidth / 2) + (hintCharLength / 2);
//                    y4 = (int) (topBarTopSpace + (rectHeight / 2) + (hintCharLength / 2));
                    y4 = (int) (topBarTopSpace + margin + (rectHeight / 2) + (hintCharLength / 4));
                    break;
            }

            int index = 1;
            for (MovingCharacter hintChar : hintCharacters) {

                switch (index) {
                    case 1:
                        hintChar.setX(x1);
                        hintChar.setY(y1);
                        break;
                    case 2:
                        hintChar.setX(x2);
                        hintChar.setY(y2);
                        break;
                    case 3:
                        hintChar.setX(x3);
                        hintChar.setY(y3);
                        break;
                    case 4:
                        hintChar.setX(x4);
                        hintChar.setY(y4);
                        break;
                }

                hintChar.draw(canvas);
                index++;
            }

            //
            int tickWidth = tickBitmap.getWidth() / 4;
//            for (int i=0;i<numberOfAlreadyDone;i++){

            switch (numberOfAlreadyDone) {
                case 1:
                    // draw on first
                    canvas.drawBitmap(tickBitmap, x1 + tickWidth, y1 - tickWidth, defaultPaint);
                    break;
                case 2:
                    // draw on first two
                    canvas.drawBitmap(tickBitmap, x1 + tickWidth, y1 - tickWidth, defaultPaint);
                    canvas.drawBitmap(tickBitmap, x2 + tickWidth, y2 - tickWidth, defaultPaint);
                    break;
                case 3:
                    // draw on first three
                    canvas.drawBitmap(tickBitmap, x1 + tickWidth, y1 - tickWidth, defaultPaint);
                    canvas.drawBitmap(tickBitmap, x2 + tickWidth, y2 - tickWidth, defaultPaint);
                    canvas.drawBitmap(tickBitmap, x3 + tickWidth, y3 - tickWidth, defaultPaint);
                    break;
                case 4:
                    // draw on forth of them
                    canvas.drawBitmap(tickBitmap, x1 + tickWidth, y1 - tickWidth, defaultPaint);
                    canvas.drawBitmap(tickBitmap, x2 + tickWidth, y2 - tickWidth, defaultPaint);
                    canvas.drawBitmap(tickBitmap, x3 + tickWidth, y3 - tickWidth, defaultPaint);
                    canvas.drawBitmap(tickBitmap, x4 + tickWidth, y4 - tickWidth, defaultPaint);
                    break;

            }
//            }

        }


        //


    }

    private void drawProfileMission(Canvas canvas, int canvasWidth) {

        boolean isEnglish = DataHolder.getInstance().isSETTINGS_IS_ENGLISH();
        String staySafeLabel = isEnglish ? getResources().getString(R.string.stay_safe) : getResources().getString(R.string.stay_safe_t);
        String missionLabel = isEnglish ? getResources().getString(R.string.mission_label) : getResources().getString(R.string.mission_label_t);
        int rectWidth = (profileMargin / 2);
        int numOfNeededElectrons = element.getNumOfNeededElectrons();
        String task = "";
        if (numOfNeededElectrons == 0) {
            task = " " +staySafeLabel;

        } else if (numOfNeededElectrons > 0) {
            if (isEnglish)
                task = " Collect " + numOfNeededElectrons + (numOfNeededElectrons == 1 ? " Electron." : " Electrons.");
            else
                task = " " + numOfNeededElectrons + " Elektron Topla.";

        } else {
            if (isEnglish)
                task = " Lose " + Math.abs(numOfNeededElectrons) + (Math.abs(numOfNeededElectrons) == 1 ? " Electron." : " Electrons.");
            else
                task = " " + Math.abs(numOfNeededElectrons) + " Elektron Kaybet.";

        }
        rectWidth += (int) profTextPaint1.measureText(missionLabel);
        rectWidth += (int) profTextPaint3.measureText(task);

//        int rectWidth = (profRectWidth / 2) - profPlayBitmap.getWidth() - (profPlayBitmap.getWidth() / 2);
        int hintCharLength = hintCharacters.get(0).getWidth();
//        switch (hintCharacters.size()) {
//            case 1:
//                rectWidth = 3*hintCharLength;
//                break;
//            case 2:
//                rectWidth = 5*hintCharLength;
//                break;
//            case 3:
//                rectWidth = 7*hintCharLength;
//                break;
//            case 4:
//                rectWidth = 9*hintCharLength;
//                break;
//        }
        if (rectWidth < 9 * hintCharLength)
            rectWidth = 9 * hintCharLength;

        int startX = profRectStartX + (profRectWidth / 2) + (profPlayBitmap.getWidth() / 2);
        int availableSpace = (profRectWidth / 2) - (profPlayBitmap.getWidth() / 2);
        int recX = startX + (availableSpace - rectWidth) / 2;
        int rectHeight = 2 * (profPlayBitmap.getHeight() / 3);
        int rectHeight1 = (profPlayBitmap.getHeight() - (profileMargin / 4));
        int recY1 = profRectStartY + profRectHeight - rectHeight1 - (profileMargin / 4);
        int recY = profRectStartY + profRectHeight - rectHeight - (profileMargin / 4);


        hitRectF.set(recX, recY1, recX + rectWidth, rectHeight1 + recY1);

        if (missionShader == null) {
            missionShader = new RadialGradient(recX + (rectWidth / 2), recY1 + (rectHeight1 / 2), ((2 * rectHeight1)), Color.rgb(95, 44, 130), Color.BLACK, Shader.TileMode.CLAMP);
        }
        profBoxPaint.setShader(missionShader);
        canvas.drawRoundRect(hitRectF, 30, 30, profBoxPaint);
//        canvas.drawRoundRect(hitRectF, 50, 50, rectStkPaint);
//        canvas.drawRoundRect(hitRectF, 50, 50, rectPaint);


        int mrg = rectHeight / 2;
        int mrg2 = mrg / 2;
        int x1 = 0, x2 = 0, x3 = 0, x4 = 0;
        int y = recY + mrg2 + (mrg2 / 4);


        // draw mission text
        canvas.drawText(missionLabel, recX + (profileMargin / 4), recY + (mrg2 / 4), profTextPaint1);
        canvas.drawText(task, recX + (profileMargin / 4) + profTextPaint1.measureText(missionLabel), recY + (mrg2 / 4), profTextPaint3);

        if (numOfNeededElectrons == 0) {
            //already happy
            x1 = recX + (rectWidth / 2) - hintCharLength - (hintCharLength / 2);
            x2 = recX + (rectWidth / 2) + (hintCharLength / 2);

            hintCharacters.get(0).setX(x1);
            hintCharacters.get(0).setY(y);
            hintCharacters.get(1).setX(x2);
            hintCharacters.get(1).setY(y);

            hintCharacters.get(0).draw(canvas);
            hintCharacters.get(1).draw(canvas);
//            canvas.drawBitmap(xBitmap, x1 - (hintCharLength / 3), y1 + hintCharLength - 15, plusPaint);
//            canvas.drawBitmap(xBitmap, x2 - (hintCharLength / 3), y2 + hintCharLength - 15, plusPaint);
            canvas.drawBitmap(xBitmap, x1 - (hintCharLength / 3), y + 2 * (hintCharLength / 3), plusPaint);
            canvas.drawBitmap(xBitmap, x2 - (hintCharLength / 3), y + 2 * (hintCharLength / 3), plusPaint);


        } else {


            switch (hintCharacters.size()) {
                case 1:
                    x1 = recX + (rectWidth / 2) - (hintCharLength / 2);
                    break;
                case 2:
                    x1 = recX + (rectWidth / 2) - hintCharLength - (hintCharLength / 2);
                    x2 = recX + (rectWidth / 2) + (hintCharLength / 2);
                    break;
                case 3:
                    x1 = recX + (rectWidth / 2) - (hintCharLength / 2);
                    x2 = recX + (rectWidth / 2) - (2 * hintCharLength) - (hintCharLength / 2);
                    x3 = recX + (rectWidth / 2) + hintCharLength + (hintCharLength / 2);
                    break;
                case 4:
                    x1 = recX + (rectWidth / 2) - hintCharLength - (hintCharLength / 2);
                    x2 = recX + (rectWidth / 2) + (hintCharLength / 2);
                    x3 = recX + (rectWidth / 2) - (3 * hintCharLength) - (hintCharLength / 2);
                    x4 = recX + (rectWidth / 2) + (hintCharLength / 2) + 2 * hintCharLength;

                    break;
            }

            int index = 1;
            for (MovingCharacter hintChar : hintCharacters) {

                switch (index) {
                    case 1:
                        hintChar.setX(x1);
                        break;
                    case 2:
                        hintChar.setX(x2);
                        break;
                    case 3:
                        hintChar.setX(x3);
                        break;
                    case 4:
                        hintChar.setX(x4);
                        break;
                }
                hintChar.setY(y);
                hintChar.draw(canvas);
                index++;
            }
        }


    }

    /**
     * function to be called when game ends , by loosing or by timer
     *
     * @param isWinner
     */
    private void endOfGame(boolean isWinner) {
        if (this.isGameplaying) {
            // game finish
            customCountDownTimer.cancel();
            this.isGameplaying = false;
            if (isWinner) {
                // win case
                ((MainActivity) this.mainActivityContext).showGameFinishScreen(true, currLevel, secondsScore);
            } else {
                // lose case
                ((MainActivity) this.mainActivityContext).showGameFinishScreen(false, currLevel, 0);
            }
        }
    }

    /**
     * function to draw the left time of the game
     *
     * @param canvas
     * @param canvasWidth
     */
    private void drawTimeLeft(Canvas canvas, int canvasWidth) {
        // rectangle to draw time inside
//        RectF rectF = new RectF();
        Paint paint = new Paint();
        paint.setARGB(128, 0, 0, 0);
        int rectWidth = (int)(70*screenDensity);
        int rectHeight = 100;
        int margin = (int)(15*screenDensity);
        int timeMargin =(int)(9*screenDensity);
        // set rectangle dimensions and position
//        rectF.set(canvasWidth - rectWidth - margin, margin, canvasWidth - margin, rectHeight + margin);
//        canvas.drawRect(rectF, paint);
//        canvas.drawText("Time left", canvasWidth - rectWidth + 3, 50, timerTextPaint);
        canvas.drawText(timer, canvasWidth - rectWidth - hintHeight + timeMargin, (hintHeight ) , elementTextPaint1);
        canvas.drawText(timer, canvasWidth - rectWidth - hintHeight + timeMargin, (hintHeight ) , elementTextPaint2);

    }


    public void startGame(int level, int playType) {
        this.currLevel = level;
        this.playType = playType;
        generator = new Random();
        if (playType == Constants.GAME_ATOM_TYPE) {
            this.element = DataHolder.getInstance().getElementByLevel(level);
            if (this.mainCharacterList.size() == 0) {
                character1Bitmap2 = getMainCharacterBitmap(level + 1);
                MainCharacter ch2 = new MainCharacter(this, character1Bitmap2, this.getWidth() / 2, this.getHeight() / 2, 2, 5, level, 0);
                happyHintCharacter = new MainCharacter(this, character1Bitmap2, this.getWidth() / 2, this.getHeight() / 2, 2, 5, level, 1);
                sadHintCharacter = new MainCharacter(this, character1Bitmap2, this.getWidth() / 2, this.getHeight() / 2, 2, 5, level, 2);
                mainCharacterExtends = new MainCharacterExtends(this, character1Bitmap2, this.getWidth() / 2, this.getHeight() / 2, 2, 5, level, 0);
                // recycle the main character image
                // make sure we don't use it anymore
                character1Bitmap2.recycle();
                if (this.element.getNumOfNeededElectrons() > 0) {
                    // electrons
                    for (int i = 0; i < this.element.getNumOfNeededElectrons(); i++) {
                        hintCharacters.add(new MovingCharacter(this, electronBitmap1, 1, 3, 0, 0, ELECTRON_TYPE, 0, true));
                    }
                } else if (this.element.getNumOfNeededElectrons() < 0) {
                    // pwoer
                    for (int i = 0; i < Math.abs(this.element.getNumOfNeededElectrons()); i++) {
                        hintCharacters.add(new MovingCharacter(this, power1, 1, 3, 0, 0, POWER_TYPE, 0, true));
                    }
                } else {
                    //happy
                    hintCharacters.add(new MovingCharacter(this, electronBitmap1, 1, 3, 0, 0, ELECTRON_TYPE, 0, true));
                    hintCharacters.add(new MovingCharacter(this, power1, 1, 3, 0, 0, POWER_TYPE, 0, true));
                }
                this.mainCharacterList.add(ch2);

            }
        } else {
//            Constants.GAME_MOLECULE_TYPE
            this.compound = DataHolder.getInstance().getCompoundByLevel(level);
            if (compoundCharacter == null) {
                compoundBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.compound_grid1);
                compoundCharacter = new CompoundCharacter(this, compoundBitmap, this.getWidth() / 2, this.getHeight() / 2, 1, 4, level);
                compoundBitmap.recycle();

                compoundProfileBitmap = getMainCompoundProfileBitmap(level + 1);

                for (int i = 0; i < this.compound.getElementLists().length; i++) {
                    compHintCharacters.add(new CompoundMovingCharacter(this, elementBitmap, 1, 1, 0, 0, "any", 0, this.compound.getElementLists()[i], true));
                }


            }
        }
        isCharProfile = true;
    }

    private Bitmap getMainCompoundProfileBitmap(int level) {
        switch (level) {
            case 1:
                return BitmapFactory.decodeResource(this.getResources(), R.drawable.compound1);

            case 2:
                return BitmapFactory.decodeResource(this.getResources(), R.drawable.compound2);
            case 3:
                return BitmapFactory.decodeResource(this.getResources(), R.drawable.compound3);
            case 4:
                return BitmapFactory.decodeResource(this.getResources(), R.drawable.compound4);
            case 5:
                return BitmapFactory.decodeResource(this.getResources(), R.drawable.compound5);
            case 6:
                return BitmapFactory.decodeResource(this.getResources(), R.drawable.compound6);
            case 7:
                return BitmapFactory.decodeResource(this.getResources(), R.drawable.compound7);
            case 8:
                return BitmapFactory.decodeResource(this.getResources(), R.drawable.compound8);
            case 9:
                return BitmapFactory.decodeResource(this.getResources(), R.drawable.compound9);
            case 10:
                return BitmapFactory.decodeResource(this.getResources(), R.drawable.compound10);
            case 11:
                return BitmapFactory.decodeResource(this.getResources(), R.drawable.compound11);
            case 12:
                return BitmapFactory.decodeResource(this.getResources(), R.drawable.compound12);
            case 13:
                return BitmapFactory.decodeResource(this.getResources(), R.drawable.compound13);
            case 14:
                return BitmapFactory.decodeResource(this.getResources(), R.drawable.compound14);
            case 15:
                return BitmapFactory.decodeResource(this.getResources(), R.drawable.compound15);
            case 16:
                return BitmapFactory.decodeResource(this.getResources(), R.drawable.compound16);

        }
        return null;
    }

    private Bitmap getMainCharacterBitmap(int level) {

        if (level == 1 || level == 2 || (level >= 6 && level <= 10) || (level >= 15 && level <= 18) || (level >= 34 && level <= 36) || (level >= 53 && level <= 54) || level == 86 || level == 118) {
            // la felezat
            return BitmapFactory.decodeResource(this.getResources(), R.drawable.main_character_grid1);
        } else if (level == 5 || level == 14 || level == 32 || level == 33 || level == 51 || level == 52 || level == 85 || level == 117) {
            // ashbah felezat
            return BitmapFactory.decodeResource(this.getResources(), R.drawable.main_character_grid2);
        } else {
            // felezat
            return BitmapFactory.decodeResource(this.getResources(), R.drawable.main_character_grid3);
        }

    }

    //called when pressing play butn in side character profile
    private void startActualGame() {
        isCharProfile = false;
        if (this.playType == Constants.GAME_MOLECULE_TYPE)
            compoundProfileBitmap.recycle();
        // start time
        customCountDownTimer = new CustomCountDownTimer(totalSeconds, intervalSeconds, this);
        isFirstTick = true;
        if (this.playType == Constants.GAME_ATOM_TYPE)
            element.resetMovingCounters();
        else {
            //TODO:moc
            // no need
            compound.resetMovingCounters();
        }
        customCountDownTimer.start();
//        new Handler().postDelayed(new Runnable() {
//                                      @Override
//                                      public void run() {
//                                          isFirstTick = true;
//                                          customCountDownTimer.start();
//                                      }
//                                  },
//                0);


    }


    // freez the game because one player has leaved or is disconnected
    public void freezGame() {
        this.isGameplaying = false;
    }

    public void stopSounds() {
        if (this.soundPoolLoaded) {
            this.soundPool.stop(this.soundIdBackground);
//            this.soundPool.stop(this.soundIdFireRun);
//            this.soundPool.stop(this.soundIdWaterHit);
//            this.soundPool.stop(this.soundIdWaterRun);

            // release sound pool
            this.soundPool.release();
            this.soundPool = null;
            this.soundPoolLoaded = false;
        }
    }


}
