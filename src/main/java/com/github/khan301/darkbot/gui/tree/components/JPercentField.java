package com.github.khan301.darkbot.gui.tree.components;

import com.bulenkov.iconloader.util.Gray;
import com.github.khan301.darkbot.config.tree.ConfigField;
import com.github.khan301.darkbot.gui.AdvancedConfig;
import com.github.khan301.darkbot.gui.tree.OptionEditor;

import javax.swing.*;
import java.awt.*;

public class JPercentField extends JSpinner implements OptionEditor {

    private ConfigField field;

    public JPercentField() {
        super(new SpinnerNumberModel(0, 0, 1, 0.05));
        putClientProperty("ConfigTree", true);
        setBorder(BorderFactory.createLineBorder(Gray._90));
        setEditor(new JSpinner.NumberEditor(this, "0%"));

        addChangeListener(e -> {
            if (field != null) field.set(getValue());
        });
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public void edit(ConfigField field) {
        this.field = null;
        setValue(field.get());
        this.field = field;
    }

    @Override
    public Dimension getPreferredSize() {
        return AdvancedConfig.forcePreferredHeight(super.getPreferredSize());
    }

}
