package com.github.khan301.darkbot.modules;

import com.github.khan301.darkbot.Main;
import com.github.khan301.darkbot.core.entities.Portal;
import com.github.khan301.darkbot.core.itf.MapChange;
import com.github.khan301.darkbot.core.itf.Module;
import com.github.khan301.darkbot.core.manager.HeroManager;
import com.github.khan301.darkbot.core.manager.StarManager;
import com.github.khan301.darkbot.core.objects.Map;
import com.github.khan301.darkbot.core.utils.Drive;

public class MapModule implements Module, MapChange {


    private Main main;
    private HeroManager hero;
    private Drive drive;
    private StarManager star;

    private Module back;
    private Portal current;
    private Map target;
    private long lastMapChange;

    @Override
    public void install(Main main) {
        this.hero = main.hero;
        this.drive = main.hero.drive;
        this.star = main.starManager;
        this.main = main;
        this.back = main.module;
    }

    @Override
    public String status() {
        return "Traveling to " + target.name + (current != null ? ", next map: " + current.target.name : "");
    }

    @Override
    public boolean canRefresh() {
        return false;
    }

    public void setTarget(Map target) {
        lastMapChange = System.currentTimeMillis();
        this.target = target;
    }

    @Override
    public void tick() {
        if (hero.map != target || current == null || current.removed)
            current = star.next(hero, target);

        if (current == null) {
            if (System.currentTimeMillis() - lastMapChange > 3000) {
                goBack();
            }
            return;
        }

        if (current.locationInfo.distance(hero) > 1500) // Portal very close, no need to disable pet
            main.guiManager.pet.setEnabled(false);
        double distance = current.locationInfo.distance(hero);
        hero.runMode();

        if (distance < 100) hero.jumpPortal(current);
        else if (current.locationInfo.isLoaded() && !drive.movingTo().equals(current.locationInfo.now)) drive.move(current);
    }

    @Override
    public void onMapChange() {
        lastMapChange = System.currentTimeMillis();
        if (hero.map == target) {
            goBack();
            current = null;
        } else {
            current = star.next(hero, target);
        }
    }

    private void goBack() {
        if (back != null) main.setModule(this.back);
        back = null;
    }

}
