package com.github.khan301.darkbot.gui.titlebar;

import com.github.khan301.darkbot.gui.MainGui;
import com.github.khan301.darkbot.gui.utils.UIUtils;

import java.awt.*;
import java.awt.event.ActionEvent;

public class CloseButton extends TitleBarButton<MainGui> {

    CloseButton(MainGui main) {
        super(UIUtils.getIcon("close"), main);
        super.actionColor = Color.decode("#6E2B28");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        frame.tryClose();
    }

}
