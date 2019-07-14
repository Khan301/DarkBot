package com.github.khan301.darkbot.gui.titlebar;

import com.github.khan301.darkbot.gui.MainGui;
import com.github.khan301.darkbot.gui.utils.UIUtils;
import com.github.khan301.darkbot.utils.SystemUtils;

import java.awt.event.ActionEvent;

public class StatsButton extends TitleBarButton<MainGui> {

    StatsButton(MainGui frame) {
        super(UIUtils.getIcon("stats"), frame);
        super.setVisible(false);
        setToolTipText("Open stats view");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

}
