package com.github.khan301.darkbot.core.entities;

import com.github.khan301.darkbot.config.ConfigEntity;

public class BasePoint extends Entity {

    public BasePoint(int id) {
        super(id);
    }

    @Override
    public void update() {
        super.update();

        if (locationInfo.isMoving() && clickable.address != 0) {
            ConfigEntity.INSTANCE.updateSafetyFor(this);
        }
    }

    @Override
    public void removed() {
        super.removed();
        ConfigEntity.INSTANCE.updateSafetyFor(this);
    }

}
