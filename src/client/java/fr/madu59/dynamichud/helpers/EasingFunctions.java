package fr.madu59.dynamichud.helpers;

public class EasingFunctions {

    public static float ease(float value, Type type) {
        switch (type) {
            case LINEAR:
                return value;
            case EASE_IN_QUAD:
                return value * value;
            case EASE_OUT_QUAD:
                return value * (2 - value);
            case EASE_IN_OUT_QUAD:
                if (value < 0.5) {
                    return 2 * value * value;
                } else {
                    return -1 + (4 - 2 * value) * value;
                }
            default:
                return value;
        }
    }

    public enum Type {
        LINEAR,
        EASE_IN_QUAD,
        EASE_OUT_QUAD,
        EASE_IN_OUT_QUAD
    }

    public enum FadingType {
        SMOOTH,
        INSTANT
    }
}
