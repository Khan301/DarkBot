package com.github.khan301.darkbot.core.objects;

import com.github.khan301.darkbot.core.entities.Entity;
import com.github.khan301.darkbot.core.itf.Updatable;
import com.github.khan301.darkbot.core.utils.Location;

import static com.github.khan301.darkbot.Main.API;

public class LocationInfo extends Updatable {

    public final Location now;
    public final Location last;

    private long lastUpdate;
    public double angle;
    public double speed;

    public LocationInfo(long address) {
        this.address = address;

        this.now = new Location();
        this.last = new Location();
    }

    public LocationInfo(double x, double y) {
        this(0);

        this.now.x = x;
        this.now.y = y;
    }

    @Override
    public void update() {
        double newX = API.readMemoryDouble(address + 32);
        double newY = API.readMemoryDouble(address + 40);

        // Update only if both x and y changed, or >50 ms since last update
        if ((newX == now.x || newY == now.y) && System.currentTimeMillis() - lastUpdate < 50) return;
        lastUpdate = System.currentTimeMillis();

        last.x = now.x;
        last.y = now.y;

        now.x = newX;
        now.y = newY;

        angle = now.angle(last);
        speed = now.distance(last) * 10;
    }

    public Location destinationInTime(long time) {

        Location destination = new Location();

        if (last.x != 0 && last.y != 0) {
            double move = speed * (time / 1000d);

            destination.x = now.x + Math.cos(angle) * move;
            destination.y = now.y + Math.sin(angle) * move;
        } else {
            destination.x = now.x;
            destination.y = now.y;
        }

        return destination;
    }

    public double distance(double x, double y) {
        return now.distance(x, y);
    }

    public double distance(LocationInfo locationInfo) {
        return now.distance(locationInfo.now);
    }

    public double distance(Entity entity) {
        return now.distance(entity.locationInfo.now);
    }

    public boolean isLoaded() {
        return address != 0;
    }

    public boolean isMoving() {
        return !now.equals(last);
    }
}