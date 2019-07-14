package com.github.khan301.darkbot.gui.titlebar;

import com.github.khan301.darkbot.gui.utils.SimpleMouseListener;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class ConfigTitleBar extends JPanel implements SimpleMouseListener {

    public ConfigTitleBar(JFrame frame, JPanel tabs) {
        super(new MigLayout("ins 0, gap 0, fill", "[][grow, 30px::][]", "[]"));

        add(tabs);
        add(new DragArea(frame), "grow");
        add(new HideButton(frame), "grow");
    }

}
