// SPDX-License-Identifier: GPL-3.0-or-later
package com.oxnull.betterchunkculling;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
* Configuracion del mod
*/
@Config(
        modid = BetterChunkCulling.MODID,
        name = "betterchunkculling"
)
public final class BCCConfig {

    private BCCConfig() {
    }

    // factor de escala vertical del elipsoide
    @Config.Comment({
            "Vertical scale factor of the rendering ellipsoid",
            "0.5 = the effective vertical radius is half the horizontal radius",
            "1.0 = perfect sphere",
            "Larger values increase the vertical radius",
            "Very small values greatly reduce the vertical rendering"
    })
    @Config.Name("verticalStretch")
    @Config.RangeDouble(min = 0.05D, max = 16.0D)
    public static double verticalStretch = 0.5D;

    // Desactiva la niebla del juego completamente
    @Config.Comment({
            "Turn off the game's fog",
            "Allows you to see the edges of ellipsoidal culling",
            "It also slightly improves performance",
            "It may make the game look less atmospheric"
    })
    @Config.Name("noFog")
    public static boolean noFog = true;

    // Activa o desactiva los mensajes de debug en la consola
    @Config.Comment({
            "Enable debug messages in the console",
            "Displays information about discarded chunks",
            "Useful for verifying that the mod works",
            "Disable to reduce console spam"
    })
    @Config.Name("debug")
    public static boolean debug = false;

    // Actualiza la configuracion si se cambia
    @Mod.EventBusSubscriber(
            modid = BetterChunkCulling.MODID,
            value = Side.CLIENT
    )
    public static class ConfigSyncHandler {

        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (BetterChunkCulling.MODID.equals(event.getModID())) {
                ConfigManager.sync(BetterChunkCulling.MODID, Config.Type.INSTANCE);

                if (BetterChunkCulling.logger != null) {
                    BetterChunkCulling.logger.info(
                            "Updated settings: verticalStretch={}, noFog={}, debug={}",
                            verticalStretch, noFog, debug
                    );
                }
            }
        }
    }
}
