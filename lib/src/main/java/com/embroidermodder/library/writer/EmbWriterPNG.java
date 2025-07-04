package com.embroidermodder.library.writer;

import android.graphics.Bitmap;

import com.embroidermodder.library.EmbPattern;
import com.embroidermodder.library.EmbPatternViewer;
import com.embroidermodder.library.Format;

import java.io.OutputStream;

public class EmbWriterPNG implements Format.Writer {
    @Override
    public void write(EmbPattern pattern, OutputStream stream) {
        EmbPatternViewer viewRootLayer = new EmbPatternViewer(pattern);
        Bitmap bitmap = viewRootLayer.getThumbnail(
                Math.max(pattern.getStitches().getWidth(), 1),
                Math.max(pattern.getStitches().getHeight(), 1));
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
    }
}
