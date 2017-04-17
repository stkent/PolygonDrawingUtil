/*
 * Copyright 2017 Stuart Kent
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.stkent.polygondrawingutil;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * An efficient utility class for drawing regular polygons on a {@link Canvas}.
 */
public class PolygonDrawingUtil {

    private final Path backingPath = new Path();
    private final RectF tempCornerArcBounds = new RectF();

    /**
     * Draws a regular polygon.
     *
     * Note that this method is not thread safe. This is not an issue if (as is typical) all invocations are made on the
     * UI thread.
     *
     * @param canvas       the {@link Canvas} to draw on
     * @param sideCount    the number of sides of the polygon
     * @param centerX      the x-coordinate of the polygon center in pixels
     * @param centerY      the y-coordinate of the polygon center in pixels
     * @param outerRadius  the distance from the polygon center to any vertex (ignoring corner rounding) in pixels
     * @param cornerRadius the radius of the rounding applied to each corner of the polygon in pixels
     * @param rotation     the rotation of the polygon in degrees
     * @param paint        the {@link Paint} to draw with
     */
    public void drawPolygon(
            @NonNull final Canvas canvas,
            @IntRange(from = 3) final int sideCount,
            final float centerX,
            final float centerY,
            @FloatRange(from = 0, fromInclusive = false) final float outerRadius,
            @FloatRange(from = 0) final float cornerRadius,
            final float rotation,
            @NonNull final Paint paint) {

        final float inRadius = (float) (outerRadius * Math.cos(toRadians(180.0 / sideCount)));

        if (inRadius < cornerRadius) {
            /*
             * If the supplied corner radius is too small, we default to drawing the "incircle".
             *   - https://web.archive.org/web/20170415150442/https://en.wikipedia.org/wiki/Regular_polygon
             *   - https://web.archive.org/web/20170415150415/http://www.mathopenref.com/polygonincircle.html
             */
            canvas.drawCircle(centerX, centerY, inRadius, paint);
        } else {
            canvas.save();
            canvas.rotate(-rotation, centerX, centerY);
            backingPath.rewind();

            if (abs(cornerRadius) < 0.01) {
                constructNonRoundedPolygonPath(sideCount, centerX, centerY, outerRadius);
            } else {
                constructRoundedPolygonPath(sideCount, centerX, centerY, outerRadius, cornerRadius);
            }

            canvas.drawPath(backingPath, paint);
            canvas.restore();
        }
    }

    private void constructNonRoundedPolygonPath(
            @IntRange(from = 3) final int sideCount,
            final float centerX,
            final float centerY,
            @FloatRange(from = 0, fromInclusive = false) final float outerRadius) {

        for (int cornerNumber = 0; cornerNumber < sideCount; cornerNumber++) {
            final double angleToCorner = cornerNumber * (360.0 / sideCount);
            final float cornerX = (float) (centerX + outerRadius * cos(toRadians(angleToCorner)));
            final float cornerY = (float) (centerY + outerRadius * sin(toRadians(angleToCorner)));

            if (cornerNumber == 0) {
                backingPath.moveTo(cornerX, cornerY);
            } else {
                backingPath.lineTo(cornerX, cornerY);
            }
        }

        // Draw the final straight edge.
        backingPath.close();
    }

    private void constructRoundedPolygonPath(
            @IntRange(from = 3) final int sideCount,
            final float centerX,
            final float centerY,
            @FloatRange(from = 0, fromInclusive = false) final float outerRadius,
            @FloatRange(from = 0) final float cornerRadius) {

        final double halfInteriorCornerAngle = 90 - (180.0 / sideCount);
        final float halfCornerArcSweepAngle = (float) (90 - halfInteriorCornerAngle);
        final double distanceToCornerArcCenter = outerRadius - cornerRadius / sin(toRadians(halfInteriorCornerAngle));

        for (int cornerNumber = 0; cornerNumber < sideCount; cornerNumber++) {
            final double angleToCorner = cornerNumber * (360.0 / sideCount);
            final float cornerCenterX = (float) (centerX + distanceToCornerArcCenter * cos(toRadians(angleToCorner)));
            final float cornerCenterY = (float) (centerY + distanceToCornerArcCenter * sin(toRadians(angleToCorner)));

            tempCornerArcBounds.set(
                    cornerCenterX - cornerRadius,
                    cornerCenterY - cornerRadius,
                    cornerCenterX + cornerRadius,
                    cornerCenterY + cornerRadius);

            /*
             * Quoted from the arcTo documentation:
             *
             *   "Append the specified arc to the path as a new contour. If the start of the path is different from the
             *    path's current last point, then an automatic lineTo() is added to connect the current contour to the
             *    start of the arc. However, if the path is empty, then we call moveTo() with the first point of the
             *    arc."
             *
             * We construct our polygon by sequentially drawing rounded corners using arcTo, and leverage the
             * automatically-added moveTo/lineTo instructions to connect these corners with straight edges.
             */
            backingPath.arcTo(
                    tempCornerArcBounds,
                    (float) (angleToCorner - halfCornerArcSweepAngle),
                    2 * halfCornerArcSweepAngle);
        }

        // Draw the final straight edge.
        backingPath.close();
    }

    private static double toRadians(final double degrees) {
        return 2 * PI * degrees / 360;
    }

}
