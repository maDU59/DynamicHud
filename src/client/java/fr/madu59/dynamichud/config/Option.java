package fr.madu59.dynamichud.config;

import net.minecraft.client.resources.language.I18n;

public class Option<T extends Enum<T>> {
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

    public String getValueAsString() {
        return this.value.toString();
    }

    public String getValueAsTranslatedString() {
        return I18n.get(this.value.toString());
    }

    public void setToNextValue() {
        this.value = cycle(this.value);
    }

    public void setName(String name){
        this.name = name;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public T cycle(T value) {
        T[] constants = value.getDeclaringClass().getEnumConstants();
        int nextOrdinal = (value.ordinal() + 1) % constants.length;
        return constants[nextOrdinal];
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
