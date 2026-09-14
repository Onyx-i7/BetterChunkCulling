// SPDX-License-Identifier: GPL-3.0-or-later
package com.oxnull.betterchunkculling.mixin;

import com.oxnull.betterchunkculling.BCCConfig;
import com.oxnull.betterchunkculling.BetterChunkCulling;
import com.oxnull.betterchunkculling.CullingMath;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Mixin para aplicar culling
 * Intercepta isBoundingBoxInFrustum en cualquier metodo de RenderGlobal
 */
@Mixin(RenderGlobal.class)
public abstract class MixinRenderGlobalVisibility {

    @Unique
    private static int bcc$culledVisual = 0;

    // Intercepta cualquier llamada a ICamera.isBoundingBoxInFrustum desde RenderGlobal
    @Redirect(
            method = "*",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/culling/ICamera;isBoundingBoxInFrustum(Lnet/minecraft/util/math/AxisAlignedBB;)Z"
            )
    )
    private boolean betterchunkculling$filterByEllipsoid(
            ICamera camera,
            AxisAlignedBB aabb
    ) {
        boolean inFrustum = camera.isBoundingBoxInFrustum(aabb);
        if (!inFrustum) {
            return false;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world == null) return true;

        Entity view = mc.getRenderViewEntity();
        if (view == null) view = mc.player;
        if (view == null) return true;

        double camX = view.posX;
        double camY = view.posY + view.getEyeHeight();
        double camZ = view.posZ;

        boolean inside = CullingMath.shouldRenderCenter(
                (int) Math.floor(aabb.minX),
                (int) Math.floor(aabb.minY),
                (int) Math.floor(aabb.minZ),
                camX, camY, camZ,
                mc.gameSettings.renderDistanceChunks,
                BCCConfig.verticalStretch
        );

        if (!inside) {
            bcc$culledVisual++;
            
            if (bcc$culledVisual % 200 == 1 && BCCConfig.debug) {
                BetterChunkCulling.logger.info(
                        "[BCC] Discarded chunks: {} (verticalStretch={})",
                        bcc$culledVisual, BCCConfig.verticalStretch
                );
            }
            return false;
        }

        return true;
    }
}
