package com.github.khan301.darkbot.core.objects;

import com.github.khan301.darkbot.core.itf.Updatable;

import static com.github.khan301.darkbot.Main.API;

public class Point extends Updatable {

    public double x;
    public double y;

    public void update() {
        this.x = API.readMemoryDouble(address + 32);
        this.y = API.readMemoryDouble(address + 40);
    }
}
