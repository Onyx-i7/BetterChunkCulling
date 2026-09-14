// SPDX-License-Identifier: GPL-3.0-or-later
package com.oxnull.betterchunkculling.mixin;

import com.oxnull.betterchunkculling.BCCConfig;
import com.oxnull.betterchunkculling.BetterChunkCulling;
import com.oxnull.betterchunkculling.CullingMath;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Mixin sobre ChunkRenderDispatcher para culling elipsoidal 3D
@Mixin(ChunkRenderDispatcher.class)
public abstract class MixinChunkRenderDispatcher {

    @Unique
    private static int bcc$skipped = 0;

    // Intercepta updateChunkLater al inicio
    @Inject(method = "updateChunkLater", at = @At("HEAD"), cancellable = true)
    private void betterchunkculling$cancelIfOutside(
            RenderChunk chunkRenderer,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (chunkRenderer == null) {
            cir.setReturnValue(false);
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world == null) return;

        Entity view = mc.getRenderViewEntity();
        if (view == null) view = mc.player;
        if (view == null) return;

        double camX = view.posX;
        double camY = view.posY + view.getEyeHeight();
        double camZ = view.posZ;

        int chunkX = chunkRenderer.getPosition().getX();
        int chunkY = chunkRenderer.getPosition().getY();
        int chunkZ = chunkRenderer.getPosition().getZ();

        boolean inside = CullingMath.shouldRenderAabb(
                chunkX, chunkY, chunkZ,
                camX, camY, camZ,
                mc.gameSettings.renderDistanceChunks,
                BCCConfig.verticalStretch
        );

        if (!inside) {
            bcc$skipped++;
            
            if (bcc$skipped % 100 == 1 && BCCConfig.debug) {
                BetterChunkCulling.logger.info(
                        "[BCC] Discarded compilation chunks: {} (verticalStretch={})",
                        bcc$skipped, BCCConfig.verticalStretch
                );
            }
            
            cir.setReturnValue(false);
        }
    }
}
