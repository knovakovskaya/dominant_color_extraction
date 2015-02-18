package ru.ifmo.thesis.gui;

import ru.ifmo.thesis.algo.CommonSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.util.*;


class ColorDistributionPanel extends JPanel {

    public JPanel cdp;
    JCheckBox perc;
    public ArrayList<Map.Entry<Color,Integer>> colors_list;
    private int pixel_num;

    public ColorDistributionPanel(){
        colors_list = null;
        pixel_num = 0;

        setLayout(new BorderLayout());
        cdp = new JPanel();
        cdp.setLayout(new BoxLayout(cdp, BoxLayout.X_AXIS));

        JPanel bottomPanel = new JPanel(){
            @Override
            public Dimension getMaximumSize() {
                Dimension size = getPreferredSize();
                size.height = 30;
                size.width = Short.MAX_VALUE;
                return size;
            }
        };
        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JLabel label = new JLabel("Percentage");
        perc = new JCheckBox();
        perc.setEnabled(false);
        perc.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                colorize(colors_list, pixel_num);
            }
        });
        bottomPanel.add(label);
        bottomPanel.add(perc);

        add(cdp);
        add(bottomPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    public void clearAll(){
        cdp.removeAll();
        colors_list = null;
    }

    public void colorize(ArrayList<Map.Entry<Color, Integer>> colors, int pixelNum){
        if (colors != null){
            cdp.removeAll();
            colors_list = colors;
            pixel_num = pixelNum;
            Collections.sort(colors, new Comparator<Map.Entry<Color,Integer>>() {
                @Override
                public int compare(Map.Entry<Color,Integer> o1, Map.Entry<Color,Integer> o2){
                    return  o2.getValue()-o1.getValue();
                }
            });
            for (final Map.Entry<Color, Integer> color : colors) {
                ColorPanel rectangle = new ColorPanel(color.getKey(), (double)color.getValue()*100/pixel_num, perc.isSelected());
                cdp.add(rectangle);
            }
            revalidate();
            repaint();
        }
    }

    private class ColorPanel extends JPanel {
        private final Color color;
        private final boolean printPercentage;
        private final double percentage;

        public ColorPanel(final Color color, double p, boolean pp) {
            this.color = color;
            this.percentage = p;
            this.printPercentage = pp;
        }

        @Override
        protected void paintComponent(final Graphics g) {
            super.paintComponent(g);
            g.setColor(color);
            g.drawRect(0, 0, getWidth(), getHeight());
            g.fillRect(0, 0, getWidth(), getHeight());
            if (printPercentage){
                double round = (double)((int)(percentage*100))/100;
                String ps = String.valueOf(round) +  "%";
                if (round == 0)
                    ps = "<0.01%";
                g.setColor(Color.black);
                g.drawString(ps, getWidth()/2 - 20, getHeight()/2);
            }
        }
    }


}
