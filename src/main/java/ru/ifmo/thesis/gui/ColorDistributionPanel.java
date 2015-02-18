package ru.ifmo.thesis.gui;

import ru.ifmo.thesis.algo.CommonSettings;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;


class ColorDistributionPanel extends JPanel {

    public ColorDistributionPanel(){
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setVisible(true);
    }

    public void colorize(CommonSettings cs, ArrayList<Map.Entry<Color, Integer>> colors){
        removeAll();
        for (final Map.Entry<Color, Integer> color : colors) {
            ColorPanel rectangle = new ColorPanel(color.getKey());
            add(rectangle);
        }
        revalidate();
    }

    private class ColorPanel extends JPanel {
        private final Color color;

        public ColorPanel(final Color color) {
            this.color = color;
        }

        @Override
        protected void paintComponent(final Graphics g) {
            super.paintComponent(g);
            g.setColor(color);
            g.drawRect(0, 0, getWidth(), getHeight());
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }


}
