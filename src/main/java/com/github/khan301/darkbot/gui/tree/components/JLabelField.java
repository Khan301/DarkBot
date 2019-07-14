package com.github.khan301.darkbot.gui.tree.components;

import com.github.khan301.darkbot.config.tree.ConfigField;
import com.github.khan301.darkbot.gui.AdvancedConfig;
import com.github.khan301.darkbot.gui.tree.OptionEditor;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class JLabelField extends JLabel implements OptionEditor {

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public void edit(ConfigField field) {
        setText(Objects.toString(field.get(), ""));
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        d.height = AdvancedConfig.ROW_HEIGHT;
        return d;
    }

}
