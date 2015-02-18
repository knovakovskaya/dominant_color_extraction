package ru.ifmo.thesis.gui;

import ru.ifmo.thesis.algo.CommonSettings;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.util.*;


class ColorDistributionPanel extends JPanel {

    public JPanel cdp;
    JCheckBox perc;
    public ArrayList<Map.Entry<Color,Integer>> colors_list;
    private int pixel_num;

    private MathSettingsPanel mathPane;

    public ColorDistributionPanel(){
        colors_list = null;
        pixel_num = 0;

        setLayout(new BorderLayout());
        cdp = new JPanel();
        cdp.setLayout(new GridLayout(0, 10));

        JPanel bottomPanel = new JPanel(){
            @Override
            public Dimension getMaximumSize() {
                Dimension size = getPreferredSize();
                size.height = 30;
                size.width = Short.MAX_VALUE;
                return size;
            }
        };
        bottomPanel.setLayout(new BorderLayout());

        JPanel rightBottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        perc = new JCheckBox();
        perc.setSelected(true);
        perc.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                colorize(colors_list, pixel_num);
            }
        });
        rightBottomPanel.add(new JLabel("Percentage"));
        rightBottomPanel.add(perc);
        mathPane = new MathSettingsPanel();
        mathPane.calculateDistribution.addActionListener(calculateColorDistributionActionListener);

        bottomPanel.add(mathPane, BorderLayout.WEST);
        bottomPanel.add(rightBottomPanel, BorderLayout.EAST);

        add(cdp);
        add(bottomPanel, BorderLayout.SOUTH);
        setVisible(true);
    }


    public void clearAll(){
        cdp.removeAll();
        colors_list = null;
        mathPane.setHaveColorDistribution(false);
    }

    private void setColorBoxes(int colorsNum){
        final int MAX_IN_LINE = 10;
        cdp.removeAll();
        if (colorsNum <= MAX_IN_LINE){
            cdp.setLayout(new GridLayout(0, colorsNum));
        } else {
            int rows = (colorsNum-1)/MAX_IN_LINE+1;
            for (int i = 0; i < MAX_IN_LINE+1; ++i){
                if (((colorsNum+rows-1)/(i+1) == rows) &&
                        (colorsNum-rows*(i+1)+1 < 2)){
                    cdp.setLayout(new GridLayout(0, i+1));
                    return;
                }
            }
        }
    }

    public void colorize(ArrayList<Map.Entry<Color, Integer>> colors, int pixelNum){
        if (colors != null){
            setColorBoxes(colors.size());
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
            mathPane.setHaveColorDistribution(true);
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
            setBorder(BorderFactory.createLineBorder(Color.black));
        }

        public Color getTextColor(){
            if (color.getBlue()+color.getGreen()+color.getRed() < 255)
                return Color.white;
            return Color.black;
        }

        public Dimension getTextPosition(){
            int dx = (percentage < 10) ? 3: 7;
            if (getWidth()/2 < 20+dx)
                return new Dimension(dx, getHeight()+2);
            return new Dimension(getWidth()/2-20-dx, getHeight()/2+2);
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
                if (getWidth() > 60){
                    g.setColor(getTextColor());
                    g.setFont(new Font("default", Font.BOLD, 16));
                    g.drawString(ps, getTextPosition().width, getTextPosition().height);
                }
            }
        }
    }

    public ActionListener calculateColorDistributionActionListener = new ActionListener(  ) {
        public void actionPerformed(ActionEvent event) {
            final JDialog dialog = new GraphDialog(
                                          mathPane.getBConst(),
                                          mathPane.getLambda(),
                                          colors_list.size(),
                                          colors_list);
            dialog.setVisible(true);
        }
    };

}
