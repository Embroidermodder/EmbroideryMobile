package com.embroidermodder.embroideryviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

import com.embroidermodder.embroideryviewer.geom.DataPoints;

import java.util.ArrayList;

public class EmbPatternViewer extends ArrayList<StitchBlock> {
    EmbPattern pattern;

    private static final float PIXELS_PER_MM = 10;


    public EmbPatternViewer(EmbPattern pattern) {
        this.pattern = pattern;
        refresh();
    }

    public void refresh() {
        clear();
        int threadIndex = 0;
        DataPoints stitches = pattern.getStitches();
        int start;
        int stop = 0;

        do {
            start = stop;
            stop = -1;

            for (int i = start, ie = stitches.size(); i < ie; i++) {
                int data = stitches.getData(i);
                if (data == EmbPattern.COLOR_CHANGE) {
                    if (start != 0) { //if colorchange op, before any stitches, ignore it.
                        threadIndex++;
                    }
                }
                if (data == EmbPattern.STITCH) {
                    start = i;
                    break;
                }
            }
            for (int i = start, ie = stitches.size(); i < ie; i++) {
                int data = stitches.getData(i);
                if (data != EmbPattern.STITCH) {
                    stop = i;
                    add(new StitchBlock(stitches, start, stop, pattern.getThreadOrFiller(threadIndex), 0));
                    break;
                }
            }
        } while ((stop != -1) && (start != stop));
    }

    public Bitmap getThumbnail(float _width, float _height) {
        RectF viewPort = calculateBoundingBox();
        float scale = Math.min(_height / viewPort.height(), _width / viewPort.width());
        Matrix matrix = new Matrix();
        if (scale != 0) {
            matrix.postTranslate(-viewPort.left, -viewPort.top);
            matrix.postScale(scale, scale);
        }

        Bitmap bmp = Bitmap.createBitmap((int) _width, (int) _height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        if (matrix != null) canvas.concat(matrix);
        for (StitchBlock stitchBlock : this) {
            stitchBlock.draw(canvas, paint);
        }
        return bmp;
    }

    public RectF calculateBoundingBox() {
        DataPoints stitches = pattern.getStitches();
        return new RectF(stitches.getMinX(), stitches.getMinY(),stitches.getMaxX(), stitches.getMaxY());
    }

    private float pixelstomm(float v) {
        return v / PIXELS_PER_MM;
    }


    public String getStatistics(Context context) {
        RectF bounds = calculateBoundingBox();
        StringBuilder sb = new StringBuilder();
        int totalSize = getTotalSize();
        int jumpCount = getJumpCount();
        int colorCount = getColorCount();
        sb.append(context.getString(R.string.normal_stitches)).append(totalSize).append('\n');
        sb.append(context.getString(R.string.jumps)).append(jumpCount).append('\n');
        sb.append(context.getString(R.string.colors)).append(colorCount).append('\n');
        sb.append(context.getString(R.string.size)).append(convert(bounds.width())).append(" mm X ").append(convert(bounds.height())).append(" mm\n");
        return sb.toString();
    }

    public String convert(float v) {
        return String.format("%.1f", pixelstomm(v));
    }


    private int getTotalSize() {
        int count = 0;
        for (StitchBlock sb : this) {
            count += sb.size();
        }
        return count;
    }

    private float getTotalLength() {
        float count = 0;
        for (StitchBlock sb : this) {
            for (int i = 0, s = sb.size() - 1; i < s; i++) {
                count += sb.distanceSegment(i);
            }
        }
        return count;
    }

    private int getJumpCount() {
        return size();
    }

    private int getColorCount() {
        return size();
    }

    private float getMaxStitch() {
        float count = Float.NEGATIVE_INFINITY;
        float current;
        for (StitchBlock sb : this) {
            for (int i = 0, s = sb.size() - 1; i < s; i++) {
                current = sb.distanceSegment(i);
                if (current > count) {
                    count = current;
                }
            }
        }
        return count;
    }

    private float getMinStitch() {
        float count = Float.POSITIVE_INFINITY;
        float current;
        for (StitchBlock sb : this) {
            for (int i = 0, s = sb.size() - 1; i < s; i++) {
                current = sb.distanceSegment(i);
                if (current < count) {
                    count = current;
                }
            }
        }
        return count;
    }

    private int getCountRange(float min, float max) {
        int count = 0;
        float current;
        for (StitchBlock sb : this) {
            for (int i = 0, s = sb.size() - 1; i < s; i++) {
                current = sb.distanceSegment(i);
                if ((current >= min) && (current <= max)) {
                    count++;
                }
            }
        }
        return count;
    }

}
