package fr.madu59.dynamichud.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import fr.madu59.dynamichud.DynamicHudMod;
import fr.madu59.dynamichud.config.SettingsManager;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;

import net.fabricmc.loader.api.FabricLoader;

public class SettingsManager<T extends Enum<T>> {

    public static List<Option<?>> ALL_OPTIONS = new ArrayList<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("dynamichud.json");
    private static Map<String, String> loadedSettings = loadSettings();

    public static Option<Option.ElementState> HOTBAR_STATE = loadOptionWithDefaults(
        "hotbar_state",
        "dynamichud.config.option.hotbar_state.name",
        "dynamichud.config.option.hotbar_state.description",
        Option.ElementState.ENABLED
    );

    public static Option<Option.ElementState> HEALTH_STATE = loadOptionWithDefaults(
        "health_state",
        "dynamichud.config.option.health_state.name",
        "dynamichud.config.option.health_state.description",
        Option.ElementState.DYNAMIC
    );

    public static Option<Option.ElementState> ARMOR_STATE = loadOptionWithDefaults(
        "armor_state",
        "dynamichud.config.option.armor_state.name",
        "dynamichud.config.option.armor_state.description",
        Option.ElementState.DYNAMIC
    );

    public static Option<Option.ElementState> MOUNT_HEALTH_STATE = loadOptionWithDefaults(
        "mount_health_state",
        "dynamichud.config.option.mount_health_state.name",
        "dynamichud.config.option.mount_health_state.description",
        Option.ElementState.DYNAMIC
    );

    public static Option<Option.ElementState> AIR_STATE = loadOptionWithDefaults(
        "air_state",
        "dynamichud.config.option.air_state.name",
        "dynamichud.config.option.air_state.description",
        Option.ElementState.DYNAMIC
    );

    public static Option<Option.ElementState> FOOD_STATE = loadOptionWithDefaults(
        "food_state",
        "dynamichud.config.option.food_state.name",
        "dynamichud.config.option.food_state.description",
        Option.ElementState.DYNAMIC
    );

    public static Option<Option.ElementState> CONTEXTUAL_BAR_STATE = loadOptionWithDefaults(
        "contextual_bar_state",
        "dynamichud.config.option.contextual_bar_state.name",
        "dynamichud.config.option.contextual_bar_state.description",
        Option.ElementState.DYNAMIC
    );

    public static Option<Option.CrosshairState> CROSSHAIR_STATE = loadOptionWithDefaults(
        "crosshair_state",
        "dynamichud.config.option.crosshair_state.name",
        "dynamichud.config.option.crosshair_state.description",
        Option.CrosshairState.ADAPTIVE
    );

    public static void saveSettings(List<Option<?>> options) {
        Map<String, String> map = toMap(options);
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(map, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, String> toMap(List<Option<?>> options) {
        Map<String, String> map = new LinkedHashMap<>();
        for (Option<?> option : options) {
            map.put(option.getId(), option.value.toString());
        }
        return map;
    }

    private static Map<String, String> loadSettings() {
        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            Type type = new TypeToken<Map<String, String>>() {}.getType();
            Map<String, String> map = GSON.fromJson(reader, type);
            return map;
        } catch (Exception e) {
            DynamicHudMod.LOGGER.info("[DynamicHud] Config file not found or invalid, using default");
            return null;
        }
    }

    private static <T extends Enum<T>> T getOptionValue(String key, Class<T> enumClass) {
        if (loadedSettings == null || !loadedSettings.containsKey(key)) return null;
        else return Enum.valueOf(enumClass, loadedSettings.get(key));
    }

    private static <T extends Enum<T>> Option<T> loadOptionWithDefaults(String id, String name, String description, T defaultValue) {
        T optionValue= getOptionValue(id, defaultValue.getDeclaringClass());
        if (optionValue == null) optionValue = defaultValue;
        Option<T> option = new Option<T>(
                id,
                name,
                description,
                optionValue,
                defaultValue
        );
        ALL_OPTIONS.add(option);
        return option;
    }
}