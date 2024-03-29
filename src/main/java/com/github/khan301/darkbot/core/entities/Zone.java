package com.github.khan301.darkbot.core.entities;

import com.github.khan301.darkbot.core.utils.Location;
import com.github.khan301.darkbot.core.utils.pathfinder.Area;

import static com.github.khan301.darkbot.Main.API;

public class Zone
        extends Entity {

    private final Area area = new Area(0, 0, 0, 0);

    Zone(int id) {
        super(id);
    }

    @Override
    public void update() {
        super.update();

        Location now = locationInfo.now;

        area.set(now.x, now.y, now.x + API.readMemoryDouble(address + 232), now.y + API.readMemoryDouble(address + 240));
    }

    @Override
    public void update(long address) {
        super.update(address);
    }

    public Area getZone() {
        return area;
    }

}
