package ru.ifmo.thesis.gui;

import ru.ifmo.thesis.algo.KMeans;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DrawMain extends JFrame {

    JButton fileChooseButton = new JButton("Choose file");
    JTextField clusterCount = new JTextField(3);
    JButton startButton = new JButton("Start");
    String filename = "";

    public static final int WINDOW_WIDTH = 640;
    public static final int WINDOW_HEIGHT = 480;
    private JTextField lambdaField;
    private JTextField bConstField;
    private Button graphButton;
    private List<Map.Entry<Color, Integer>> colors = new ArrayList<>();

    public DrawMain() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setTitle("prototype");
        setContentPane(createContentPane());
        setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        DrawMain main = new DrawMain();
    }

    private JPanel createContentPane() {
        final JPanel contentPane = new JPanel();
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        contentPane.setLayout(new BorderLayout());
        buttonPane.add(fileChooseButton);
        buttonPane.add(startButton);
        startButton.setEnabled(checkStart());
        buttonPane.add(clusterCount);
        clusterCount.setText("5");
        clusterCount.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(final DocumentEvent e) {
                startButton.setEnabled(checkStart());
            }

            @Override
            public void removeUpdate(final DocumentEvent e) {
                startButton.setEnabled(checkStart());
            }

            @Override
            public void changedUpdate(final DocumentEvent e) {
                startButton.setEnabled(checkStart());
            }
        });
        contentPane.add(buttonPane, BorderLayout.PAGE_END);
        fileChooseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Images", "jpg", "png", "gif");
                JFileChooser chooser = new JFileChooser(Paths.get(filename).toAbsolutePath().toString());
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(getParent());
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    filename = chooser.getSelectedFile().getAbsolutePath();
                }
                startButton.setEnabled(checkStart());
                graphButton.setEnabled(checkGraph());
            }
        });
        lambdaField = new JTextField(5);
        lambdaField.setText("1");
        lambdaField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(final DocumentEvent e) {
                graphButton.setEnabled(checkGraph());
            }

            @Override
            public void removeUpdate(final DocumentEvent e) {
                graphButton.setEnabled(checkGraph());
            }

            @Override
            public void changedUpdate(final DocumentEvent e) {
                graphButton.setEnabled(checkGraph());
            }
        });
        bConstField = new JTextField(5);
        bConstField.setText("1");
        bConstField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(final DocumentEvent e) {
                graphButton.setEnabled(checkGraph());
            }

            @Override
            public void removeUpdate(final DocumentEvent e) {
                graphButton.setEnabled(checkGraph());
            }

            @Override
            public void changedUpdate(final DocumentEvent e) {
                graphButton.setEnabled(checkGraph());
            }
        });
        graphButton = new Button("Распределение");
        graphButton.setEnabled(checkGraph());
        graphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final JDialog dialog = new GraphDialog(
                        Double.parseDouble(bConstField.getText()),
                        Double.parseDouble(lambdaField.getText()),
                        colors.size(),
                        colors);
                dialog.setVisible(true);
            }
        });
        buttonPane.add(new JLabel("Лямбда"));
        buttonPane.add(lambdaField);
        buttonPane.add(new JLabel("B-константа"));
        buttonPane.add(bConstField);
        buttonPane.add(graphButton);
        final JPanel histPane = new JPanel();
        histPane.setLayout(new BoxLayout(histPane, BoxLayout.X_AXIS));
        histPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        histPane.setPreferredSize(new Dimension(WINDOW_WIDTH, 400));
        histPane.setVisible(true);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                int clusterNumber = Integer.parseInt(clusterCount.getText());
                histPane.removeAll();
                colors.clear();
                colors = new ArrayList<>(KMeans.calculateAndGetColors(filename, clusterNumber));
                final int width = WINDOW_WIDTH / clusterNumber - 1;
                final int height = histPane.getHeight();
                for (final Map.Entry<Color, Integer> color : colors) {
                    ColoredRectangle rectangle = new ColoredRectangle(color.getKey(), new Dimension(width, height));
                    histPane.add(rectangle);
                }
                DrawMain.this.revalidate();
                graphButton.setEnabled(checkGraph());
            }
        });
        contentPane.add(histPane, BorderLayout.PAGE_START);
        return contentPane;
    }

    private boolean checkGraph() {
        double lambda;
        double bConst;
        try {
            lambda = Double.parseDouble(lambdaField.getText());
            bConst = Double.parseDouble(bConstField.getText());
        } catch (Exception e) {
            return false;
        }
        return !colors.isEmpty() && lambda > 0 && bConst >= 0;
    }

    private boolean checkStart() {
        int clusters;
        try {
            clusters = Integer.parseInt(clusterCount.getText());
        } catch (Exception e) {
            return false;
        }
        return !filename.equals("") && clusters > 0 && clusters <= 255;
    }

    private class ColoredRectangle extends JPanel {

        private final Color color;

        public ColoredRectangle(final Color color, final Dimension size) {
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
