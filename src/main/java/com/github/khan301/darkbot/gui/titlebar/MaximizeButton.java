package com.github.khan301.darkbot.gui.titlebar;

import com.github.khan301.darkbot.gui.utils.UIUtils;
import com.github.khan301.darkbot.gui.utils.window.WindowUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class MaximizeButton extends TitleBarButton<JFrame> {

    MaximizeButton(JFrame frame) {
        super(UIUtils.getIcon("maximize"), frame);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        WindowUtils.toggleMaximized(frame);
    }

}
