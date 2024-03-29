package com.github.khan301.darkbot.gui.theming;

import com.bulenkov.darcula.ui.DarculaComboBoxUI;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.InsetsUIResource;
import java.awt.*;

public class DarculaComboBox extends DarculaComboBoxUI {


    public DarculaComboBox(JComboBox comboBox) {
        super(comboBox);
    }

    public static ComponentUI createUI(JComponent c) {
        return new DarculaComboBox((JComboBox)c);
    }

    @Override
    protected Insets getInsets() {
        return Boolean.TRUE.equals(comboBox.getClientProperty("ConfigTree")) ? new InsetsUIResource(0, 9, 1, 5) : super.getInsets();
    }

}
