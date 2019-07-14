package com.github.khan301.darkbot.gui.tree;

import com.github.khan301.darkbot.config.tree.ConfigField;

import javax.swing.*;

public interface OptionEditor {

    JComponent getComponent();
    void edit(ConfigField field);

}
