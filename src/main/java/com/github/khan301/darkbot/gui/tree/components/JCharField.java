package com.github.khan301.darkbot.gui.tree.components;

import com.bulenkov.iconloader.util.Gray;
import com.github.khan301.darkbot.config.tree.ConfigField;
import com.github.khan301.darkbot.gui.AdvancedConfig;
import com.github.khan301.darkbot.gui.tree.OptionEditor;
import com.github.khan301.darkbot.gui.utils.GeneralDocumentListener;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Objects;

public class JCharField extends JTextField implements OptionEditor {

    private static final char EMPTY = (char) 0;
    private static final DocumentFilter SINGLE_CHAR_DOCUMENT = new DocumentFilter(){
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            super.replace(fb, 0, fb.getDocument().getLength(), text == null || text.isEmpty() || text.equals("\b") ? "" : text.substring(text.length() - 1), attrs);
        }
    };

    private ConfigField field;

    public JCharField() {
        putClientProperty("ConfigTree", true);
        setBorder(BorderFactory.createLineBorder(Gray._90));
        ((AbstractDocument) getDocument()).setDocumentFilter(SINGLE_CHAR_DOCUMENT);
        getDocument().addDocumentListener((GeneralDocumentListener) e -> setValue(getValue()));
        setPreferredSize(new Dimension(20, AdvancedConfig.EDITOR_HEIGHT)); // Set 20 px wide
        setMaximumSize(new Dimension(1000, AdvancedConfig.EDITOR_HEIGHT)); // Force height, required to match size
        setHorizontalAlignment(CENTER);
        //setCaretColor(new Color(0, 0, 0, 0));
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                select(getDocument().getLength(), getDocument().getLength());
            }
        });
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public void edit(ConfigField field) {
        this.field = null;
        setText(Objects.toString(field.get(), ""));
        this.field = field;
    }

    public Character getValue() {
        if (field != null && field.isPrimitive() && getText().isEmpty()) return EMPTY;
        return getText().isEmpty() ? null : getText().charAt(0);
    }

    protected void setValue(Character value) {
        if (field != null) field.set(value);
    }

    @Override
    public Dimension getPreferredSize() {
        return AdvancedConfig.forcePreferredHeight(super.getPreferredSize());
    }

}
