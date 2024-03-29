package com.github.khan301.darkbot.gui.safety;

import com.github.khan301.darkbot.config.ConfigEntity;
import com.github.khan301.darkbot.config.SafetyInfo;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

class SafetyEditor extends JPanel {

    private JComboBox<SafetyInfo.RunMode> runEditor;
    private JComboBox<SafetyInfo.JumpMode> jumpEditor;
    private JComboBox<SafetyInfo.CbsMode> cbsEditor;
    private JSlider diameterEditor;

    SafetyEditor(SafetiesEditor editor) {
        super(new MigLayout("wrap 3, fill", "[][][]"));

        runEditor = new JComboBox<>(SafetyInfo.RunMode.values());
        jumpEditor = new JComboBox<>(SafetyInfo.JumpMode.values());
        cbsEditor = new JComboBox<>(SafetyInfo.CbsMode.values());
        diameterEditor = new JSlider(50, 8000);
        edit(null);

        add(new JLabel(" [All]  Use condition"));
        add(new JLabel(" [Portal]  Jump condition"));
        add(new JLabel(" [CBS]  Use condition"));
        add(runEditor, "grow");
        add(jumpEditor, "grow");
        add(cbsEditor, "grow");
        add(diameterEditor, "span, grow");

        runEditor.addActionListener(a -> {
            if (editor.editing != null) {
                editor.editing.runMode = runEditor.getItemAt(runEditor.getSelectedIndex());
                ConfigEntity.changed();
            }
        });
        jumpEditor.addActionListener(a -> {
            if (editor.editing != null) {
                editor.editing.jumpMode = jumpEditor.getItemAt(jumpEditor.getSelectedIndex());
                ConfigEntity.changed();
            }
        });
        cbsEditor.addActionListener(a -> {
            if (editor.editing != null) {
                editor.editing.cbsMode = cbsEditor.getItemAt(cbsEditor.getSelectedIndex());
                ConfigEntity.changed();
            }
        });
        diameterEditor.addChangeListener(a -> {
            if (editor.editing != null) {
                editor.editing.diameter = diameterEditor.getValue();
                editor.edit(editor.editing);
                ConfigEntity.changed();
            }
        });
    }

    void edit(SafetyInfo editing) {
        runEditor.setEnabled(editing != null);
        jumpEditor.setEnabled(editing != null && editing.type == SafetyInfo.Type.PORTAL);
        cbsEditor.setEnabled(editing != null && editing.type == SafetyInfo.Type.CBS);
        diameterEditor.setEnabled(editing != null);

        setEdit(runEditor, editing == null ? null : editing.runMode);
        setEdit(jumpEditor, editing == null ? null : editing.jumpMode);
        setEdit(cbsEditor, editing == null ? null : editing.cbsMode);
        diameterEditor.setValue(editing == null ? 50 : editing.diameter);
    }

    private <T> void setEdit(JComboBox<T> combo, T obj) {
        if (obj == null) combo.setSelectedIndex(-1);
        else combo.setSelectedItem(obj);
    }

}
