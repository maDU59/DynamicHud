package fr.madu59.dynamichud.config.configscreen;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
        @Override
        public ConfigScreenFactory<DynamicHudConfigScreen> getModConfigScreenFactory() {
                return DynamicHudConfigScreen::new;
        }
}