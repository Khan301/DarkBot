package com.github.khan301.darkbot.gui.titlebar;

import com.github.khan301.darkbot.gui.components.MainButton;

import javax.swing.*;
import java.awt.event.ActionListener;

abstract class TitleBarButton<T extends JFrame> extends MainButton implements ActionListener {

    T frame;

    TitleBarButton(Icon icon, T frame) {
        super(icon);
        this.frame = frame;
    }

    TitleBarButton(String text, T frame) {
        super(text);
        this.frame = frame;
    }

}
