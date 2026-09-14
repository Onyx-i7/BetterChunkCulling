// SPDX-License-Identifier: GPL-3.0-or-later
package com.oxnull.betterchunkculling;

/**
 * Formula base del elipsoide segun Google:
 *
 * Rh = renderDistanceChunks * 16.0
 * k = 1.0 / max(0.05, verticalStretch)
 *
 * Distancia elíptica al cuadrado:
 * dx^2 + (dy * k)^2 + dz^2
 *
 * Si esa distancia es mayor que Rh^2 el chunk se descarta
 */
public final class CullingMath {
    private static final int SECTION_SIZE = 16;

    // Minimo permitido para verticalStretch
    private static final double MIN_VERTICAL_STRETCH = 0.05D;

    // Pequeño margen para evitar parpadeo justo en el limite del radio
    private static final double EPSILON = 1.0D;

    private CullingMath() {
    }

    /**
     * Comprueba el punto mas cercano del AABB de 16x16x16 al elipsoide
     * Esto evita descartar chunks que están parcialmente dentro
     *
     * @param minX                 Coordenada X minima de la sección
     * @param minY                 Coordenada Y minima de la sección
     * @param minZ                 Coordenada Z minima de la sección
     * @param cameraX              Posición X de la camara/jugador
     * @param cameraY              Posición Y de la camara/jugador
     * @param cameraZ              Posición Z de la camara/jugador
     * @param renderDistanceChunks Distancia de renderizado en chunks
     * @param verticalStretch      Factor vertical configurado
     * @return true si el chunk debe renderizarse
     */
    public static boolean shouldRenderAabb(
            int minX,
            int minY,
            int minZ,
            double cameraX,
            double cameraY,
            double cameraZ,
            int renderDistanceChunks,
            double verticalStretch
    ) {
        double rh = horizontalRadius(renderDistanceChunks);
        double rhSquared = rh * rh;

        // k escala el eje Y
        // Si verticalStretch = 0.5, k = 2.0 por lo que el radio vertical
        // efectivo se convierte en Rh * 0.5
        double k = verticalScale(verticalStretch);

        // Coordenadas X relativas a la camara
        double minXd = minX - cameraX;
        double maxXd = minX + SECTION_SIZE - cameraX;

        // Coordenadas Y relativas a la camara y escaladas por k
        double minYd = (minY - cameraY) * k;
        double maxYd = (minY + SECTION_SIZE - cameraY) * k;

        // Coordenadas Z relativas a la camara
        double minZd = minZ - cameraZ;
        double maxZd = minZ + SECTION_SIZE - cameraZ;

        // Punto mas cercano del AABB al origen en espacio eliptico transformado
        double dx = closestToZero(minXd, maxXd);
        double dy = closestToZero(minYd, maxYd);
        double dz = closestToZero(minZd, maxZd);

        // Comparacion al cuadrado
        double distanceSquared = (dx * dx) + (dy * dy) + (dz * dz);

        return distanceSquared <= rhSquared + EPSILON;
    }

    /**
     * dx^2 + (dy * k)^2 + dz^2 <= Rh^2
     *
     * @param minX                 Coordenada X minima de la seccion
     * @param minY                 Coordenada Y minima de la seccion
     * @param minZ                 Coordenada Z minima de la seccion
     * @param cameraX              Posición X de la camara/jugador
     * @param cameraY              Posición Y de la camara/jugador
     * @param cameraZ              Posición Z de la camara/jugador
     * @param renderDistanceChunks Distancia de renderizado en chunks
     * @param verticalStretch      Factor vertical configurado
     * @return true si el centro del chunk está dentro del elipsoide
     */
    public static boolean shouldRenderCenter(
            int minX,
            int minY,
            int minZ,
            double cameraX,
            double cameraY,
            double cameraZ,
            int renderDistanceChunks,
            double verticalStretch
    ) {
        double rh = horizontalRadius(renderDistanceChunks);
        double k = verticalScale(verticalStretch);

        // Centro del chunk de 16x16x16
        double dx = (minX + 8.0D) - cameraX;
        double dy = (minY + 8.0D) - cameraY;
        double dz = (minZ + 8.0D) - cameraZ;

        double scaledDy = dy * k;

        double distanceSquared = (dx * dx) + (scaledDy * scaledDy) + (dz * dz);

        return distanceSquared <= (rh * rh) + EPSILON;
    }

    /**
     * Radio horizontal en bloques
     *
     * @param renderDistanceChunks distancia de renderizado en chunks
     * @return Radio horizontal en bloques
     */
    private static double horizontalRadius(int renderDistanceChunks) {
        int safeRenderDistance = Math.max(1, renderDistanceChunks);
        return safeRenderDistance * 16.0D;
    }

    /**
     * Calcula k:
     *
     * k = 1.0 / max(0.05, verticalStretch)
     *
     * @param verticalStretch configurado por el usuario
     * @return Factor de escala vertical
     */
    private static double verticalScale(double verticalStretch) {
        double stretch = verticalStretch;

        if (Double.isNaN(stretch) || stretch < MIN_VERTICAL_STRETCH) {
            stretch = MIN_VERTICAL_STRETCH;
        }

        return 1.0D / stretch;
    }

    /**
     * Devuelve la coordenada mas cercana a cero dentro de un intervalo
     *
     * Si el intervalo contiene el cero, la distancia mas cercana es 0
     * Si el intervalo es positivo, el más cercano es min
     * Si el intervalo es negativo, el más cercano es max
     *
     * @param min Valor mínimo del intervalo
     * @param max Valor máximo del intervalo
     * @return Valor del intervalo más cercano a cero
     */
    private static double closestToZero(double min, double max) {
        if (min > 0.0D) {
            return min;
        }

        if (max < 0.0D) {
            return max;
        }

        return 0.0D;
    }
}
