package utils;

import com.mandeleev.game.mandeleev.R;

/**
 * Created by Ayham on 12/12/2018.
 */

public interface Constants {
    String TAG = "MandeleevGameTag";

    int[] CLICKABLES = {
            R.id.button_play1,R.id.button_play_2, R.id.button_settings
    };
    int[] SCREENS = {
            R.id.screen_splash, R.id.screen_main
            , R.id.screen_levels, R.id.screen_wait,
            R.id.screen_game, R.id.screen_levels2

    };
    int MAX_STREAMS = 100;

    String ELECTRON_TYPE = "Electron";
    String POWER_TYPE = "Power";

    String POWER_EXPLOSION_EFFECT = "powerExplosionEffect";
    String ELECTRON_EXPLOSION_EFFECT = "electronExplosionEffect";
    String GET_ELECTRON_EFFECT = "getElectronEffect";
    String LOSE_ELECTRON_EFFECT = "loseElectronEffect";
    String ELECTRON_DISAPPEAR_EFFECT = "electronDisappearEffect";
    String POWER_DISAPPEAR_EFFECT = "powerDisappearEffect";
//    String WIN_EFFECT = "winEffect";

    int EFFECTS_SIZE = 6;
    int POWER_EXPLOSION_INDEX = 0;
    int ELECTRON_EXPLOSION_INDEX =1;
    int GET_ELECTRON_INDEX = 2;
    int LOSE_ELECTRON_INDEX = 3;
    int ELECTRON_DISAPPEAR_INDEX = 4;
    int POWER_DISAPPEAR_INDEX = 5;
//    int WIN_INDEX = 6;

//    String WATER_STAR_TYPE = "waterStar";
//    String FIRE_STAR_TYPE = "fireStar";
//    String WATER_DISAPPEAR_TYPE = "waterDisappear";
//    String FIRE_DISAPPEAR_TYPE = "fireDisappear";

    boolean SETTINGS_IS_ENGLISH = true;
    boolean SETTINGS_IS_LEFT_HAND = true;
    boolean SETTINGS_IS_SOUNDS_OFF= true;

    int GAME_ATOM_TYPE = 1;
    int GAME_MOLECULE_TYPE =2;
}
