package fr.madu59.dynamichud.config;

import net.minecraft.client.resources.language.I18n;

public class Option<T> {
    public String id;
    public transient String name;
    public transient String description;
    public T value;
    public transient T defaultValue;

    public Option(String id, String name, String description, T value, T defaultValue) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.value = value;
        this.defaultValue = defaultValue;
        SettingsManager.ALL_OPTIONS.add(this);
    }

    public void resetToDefault() {
        this.value = this.defaultValue;
    }

    public T getValue() {
        return this.value;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return I18n.get(this.name);
    }

    public String getDescription() {
        return I18n.get(this.description);
    }

    public void setToNextValue() {
        this.value = cycle(this.value);
    }

    public void setValue(T value){
        this.value = value;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setDescription(String description){
        this.description = description;
    }

    @SuppressWarnings("unchecked")
    public T cycle(T value) {
        if (value instanceof Enum<?> enumValue) {
            Enum<?>[] constants = enumValue.getDeclaringClass().getEnumConstants();
            int nextOrdinal = (enumValue.ordinal() + 1) % constants.length;
            return (T) constants[nextOrdinal];
        }
        else return null;
    }

    public static enum ElementState {
		ENABLED,
		DYNAMIC,
		DISABLED;
	}

    public static enum CrosshairState {
		ENABLED,
		DYNAMIC,
        ADAPTIVE,
		DISABLED;
	}
}
