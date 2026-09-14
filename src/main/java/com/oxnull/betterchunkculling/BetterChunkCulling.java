// SPDX-License-Identifier: GPL-3.0-or-later
package com.oxnull.betterchunkculling;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = BetterChunkCulling.MODID,
        name = BetterChunkCulling.NAME,
        version = BetterChunkCulling.VERSION,
        acceptedMinecraftVersions = "[1.12.2]",
        clientSideOnly = true,
        dependencies = BetterChunkCulling.DEPENDENCIES,
        useMetadata = true
)
public final class BetterChunkCulling {

    public static final String MODID = "betterchunkculling";
    public static final String NAME = "Better Chunk Culling";
    public static final String VERSION = "1.0";

    public static final String DEPENDENCIES = "required-after:mixinbooter";

    public static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();

        logger.info("Better Chunk Culling initialized");
        logger.info("Mod ID: {}", MODID);
        logger.info("Version: {}", VERSION);
        logger.info("current verticalStretch: {}", BCCConfig.verticalStretch);
    }
}
