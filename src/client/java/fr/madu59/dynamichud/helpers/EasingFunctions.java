package fr.madu59.dynamichud.helpers;

import fr.madu59.dynamichud.config.SettingsManager;

public class EasingFunctions {

    public static float ease(float t, Type type, boolean bool, float value, float dt, float speed) {
        switch (type) {
            case LINEAR:
                return t;
            case EASE_IN_QUAD:
                return t * t;
            case EASE_OUT_QUAD:
                return t * (2 - t);
            case EASE_IN_OUT_QUAD:
                if (t < 0.5) {
                    return 2 * t * t;
                } else {
                    return -1 + (4 - 2 * t) * t;
                }
            case CUBIC:
                return 3 * t * t * t - 2 * t * t;
            case SQUARE_ROOT:
                return (float)(bool? Math.sqrt(t):(1-Math.sqrt(1-t)));
            case EXPONENTIAL:
                if(bool && SettingsManager.FADEIN_TYPE.getValue() == EasingFunctions.FadingType.INSTANT) return 1;
                if(!bool && SettingsManager.FADEOUT_TYPE.getValue() == EasingFunctions.FadingType.INSTANT) return 0;
                return value + (float)(((bool?1:0) - value) * (1 - Math.exp(-dt * (1.1-speed))));
            default:
                return t;
        }
    }

    public enum Type {
        LINEAR,
        EASE_IN_QUAD,
        EASE_OUT_QUAD,
        EASE_IN_OUT_QUAD,
        CUBIC,
        SQUARE_ROOT,
        EXPONENTIAL
    }

    public enum FadingType {
        SMOOTH,
        INSTANT
    }
}
