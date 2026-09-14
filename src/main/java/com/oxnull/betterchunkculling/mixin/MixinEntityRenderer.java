// SPDX-License-Identifier: GPL-3.0-or-later
package com.oxnull.betterchunkculling.mixin;

import com.oxnull.betterchunkculling.BCCConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Mixin para desactivar el fog para notar mejor el funcionamiento del mod
@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {

    @Unique
    private static final Logger BCC_LOGGER = LogManager.getLogger("BetterChunkCulling");

    @Unique
    private static int bcc$fogFrame = 0;

    // Intercepta setupFog al final del metodo
    @Inject(
            method = "setupFog",
            at = @At("RETURN")
    )
    private void betterchunkculling$disableFog(
            int startCoords,
            float partialTicks,
            CallbackInfo ci
    ) {
        // Solo aplicar NoFog si la opción esta activada
        if (!BCCConfig.noFog) {
            return;
        }

        bcc$fogFrame++;

        if (bcc$fogFrame % 400 == 1 && BCCConfig.debug) {
            BCC_LOGGER.info("[BCC] Fog Off - frame {}", bcc$fogFrame);
        }

        // Sacar la configuracion desde Minecraft
        Minecraft mc = Minecraft.getMinecraft();
        float renderDistance = (float) (mc.gameSettings.renderDistanceChunks * 16);

        // Desactivar el fog
        GL11.glFogf(GL11.GL_FOG_START, renderDistance * 100.0f);
        GL11.glFogf(GL11.GL_FOG_END, renderDistance * 200.0f);
        GL11.glFogf(GL11.GL_FOG_DENSITY, 0.0f);
        GL11.glDisable(GL11.GL_FOG);
    }
}
