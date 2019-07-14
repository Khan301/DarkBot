package com.github.khan301.darkbot.core.itf;

import com.github.khan301.darkbot.Main;

public interface Module {

    void install(Main main);

    boolean canRefresh();

    void tick();

    default String status() {
        return null;
    }

}
