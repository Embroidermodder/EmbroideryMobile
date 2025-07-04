package com.embroidermodder.library;

import com.embroidermodder.library.geom.Points;

public interface EmbObject {
    EmbThread getThread();
    Points getPoints();
    int getType();
}
