package com.github.khan301.darkbot.gui.titlebar;

import com.github.khan301.darkbot.config.ConfigEntity;
import com.github.khan301.darkbot.gui.utils.UIUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class PinButton extends TitleBarButton<JFrame> {

    private static final Icon PIN = UIUtils.getIcon("pin"), UNPIN = UIUtils.getIcon("unpin");

    PinButton(JFrame frame) {
        super(PIN, frame);
        setToolTipText("Always on top");
        setBackground();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        frame.setAlwaysOnTop(!frame.isAlwaysOnTop());
        ConfigEntity.INSTANCE.getConfig().MISCELLANEOUS.DISPLAY.ALWAYS_ON_TOP = frame.isAlwaysOnTop();
        ConfigEntity.changed();
    }

    protected void setBackground() {
        if (frame.isAlwaysOnTop()) {
            setBackground(actionColor.darker());
            setIcon(UNPIN);
        } else {
            super.setBackground();
            setIcon(PIN);
        }
    }

}
