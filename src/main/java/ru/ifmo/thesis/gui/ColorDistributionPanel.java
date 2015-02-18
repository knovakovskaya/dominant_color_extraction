package ru.ifmo.thesis.gui;

import ru.ifmo.thesis.algo.CommonSettings;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;


class ColorDistributionPanel extends JPanel {
    public Dimension pms;

    public ColorDistributionPanel(Dimension minsize){
        pms = minsize;
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setMinimumSize(new Dimension(minsize.width, minsize.height));
        setSize(new Dimension(minsize.width, minsize.height));
        setMaximumSize(new Dimension(minsize.width, minsize.height));
        setVisible(true);
    }

    public void colorize(CommonSettings cs, ArrayList<Map.Entry<Color, Integer>> colors){
        removeAll();
        final int width = getWidth() / cs.clustersNum - 1;
        final int height = getHeight();
        for (final Map.Entry<Color, Integer> color : colors) {
            ColorPanel rectangle = new ColorPanel(color.getKey(), new Dimension(width, height));
            add(rectangle);
        }
        revalidate();
    }

    private class ColorPanel extends JPanel {
        private final Color color;

        public ColorPanel(final Color color, final Dimension size) {
            this.setPreferredSize(size);
            this.color = color;
        }

        @Override
        protected void paintComponent(final Graphics g) {
            super.paintComponent(g);
            g.setColor(color);
            g.drawRect(0, 0, getPreferredSize().width, getPreferredSize().height);
            g.fillRect(0, 0, getPreferredSize().width, getPreferredSize().height);
        }
    }


}
