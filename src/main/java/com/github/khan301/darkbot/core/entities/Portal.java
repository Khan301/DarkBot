package com.github.khan301.darkbot.core.entities;

import com.github.khan301.darkbot.Main;
import com.github.khan301.darkbot.config.ConfigEntity;
import com.github.khan301.darkbot.core.objects.Map;

public class Portal extends Entity {

    private final PortalMatcher matcher;

    public final Map target;
    public final int factionId;
    public int type;

    public Portal(int searchType, int searchX, int searchY, Map target, int factionId) {
        super(-1);
        this.matcher = new PortalMatcher(searchType, searchX, searchY);

        super.removed = true;
        this.target = target;
        this.factionId = factionId;
    }

    public Portal(int id, int type, int x, int y) {
        this(type, x, y, null, -1);
        this.id = id;
    }

    public boolean matches(int x, int y, int type) {
        return matcher.matches(x, y, type);
    }

    @Override
    public void update() {
        super.update();

        type = Main.API.readMemoryInt(address + 112);
        if (locationInfo.isMoving()) ConfigEntity.INSTANCE.updateSafetyFor(this);
    }

    @Override
    public void added() {
        super.added();
        ConfigEntity.INSTANCE.updateSafetyFor(this);
    }

    @Override
    public String toString() {
        return "(" + locationInfo.now + ")" + type;
    }

    // Holds the search criteria portals in the star manager
    private class PortalMatcher {
        private int searchType, searchX, searchY;

        PortalMatcher(int searchType, int searchX, int searchY) {
            this.searchType = searchType;
            this.searchX = searchX;
            this.searchY = searchY;
        }

        boolean matches(int x, int y, int type) {
            return (searchType != 1 && searchType == type) || // By type
                    (searchX != -1 && searchY != -1 && searchX == x && searchY == y); // By pos
        }

    }

}
