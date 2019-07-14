package com.github.khan301.darkbot.core.itf;

import com.github.khan301.darkbot.core.utils.pathfinder.Area;

public interface Obstacle {

    Area getArea();

    boolean isRemoved();

    boolean use();
}
