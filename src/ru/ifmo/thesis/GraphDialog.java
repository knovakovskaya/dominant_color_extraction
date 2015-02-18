package ru.ifmo.thesis;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class GraphDialog extends JDialog {
   private String savePath = "";
   public static final int DIALOG_WIDTH = 800;
   public static final int DIALOG_HEIGHT = 600;
   private final double bConst;
   private final double lambda;
   private final int numberOfPoints;
   private final List<Map.Entry<Color, Integer>> realValues;

   public GraphDialog(
         final double bConst,
         final double lambda,
         final int numberOfPoints,
         final List<Map.Entry<Color, Integer>> realValues
   ) {
      super();
      this.bConst = bConst;
      this.lambda = lambda;
      this.numberOfPoints = numberOfPoints;
      this.realValues = realValues;
      Collections.sort(realValues, new Comparator<Map.Entry<Color, Integer>>() {
         @Override
         public int compare(final Map.Entry<Color, Integer> o1, final Map.Entry<Color, Integer> o2) {
            return -o1.getValue() + o2.getValue();
         }
      });
      setModal(false);
      setSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT + 50));
      setTitle("Распределение");
      setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      setContentPane(createOptionPane());
   }

   private JPanel createOptionPane() {
      final JPanel optionPane = new JPanel(new BorderLayout());
      final JPanel graphPane = new GraphPanel(createZipf(), createActual());
      graphPane.setPreferredSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
      graphPane.setVisible(true);
      graphPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      final JButton exportButton = new JButton("Экспорт данных");
      exportButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            final JFileChooser chooser = new JFileChooser(savePath);
            final Path exportedFile = Paths.get("экспорт.txt");
            try (final BufferedWriter bw = Files.newBufferedWriter(exportedFile, Charset.defaultCharset())) {
               final List<Double> zipf = createZipfProbabilities();
               final List<Double> actual = createActualProbabilities();
               final double k = computeKConst();
               final double xiSquared = computeXiSquared(zipf, actual);
               bw.append(String.format("Параметры:%n В-константа:%f\tЛямбда:%f\tКоличество цветов:%d%n", bConst, lambda, numberOfPoints));
               bw.append(String.format("K-константа:%f\tРазница по критерию хи-квадрат:%f%n%n", k, xiSquared));
               bw.append(String.format("Таблица распределений%n"));
               bw.append(String.format("номер\tОжидаемое(Ципф)\tРеальное\tЦвет%n"));
               for (int i = 0; i < realValues.size(); i++) {
                  bw.append(String.format("%d:\t\t%f\t%f\t%s%n", i, zipf.get(i), actual.get(i), createHex(realValues.get(i).getKey())));
               }
            } catch (IOException ex) {
               System.out.println("Error while saving file");
            }
            chooser.setSelectedFile(exportedFile.toFile());
            int returnVal = chooser.showSaveDialog(GraphDialog.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
               savePath = chooser.getSelectedFile().getAbsolutePath();
               try {
                  Files.copy(exportedFile, Paths.get(chooser.getSelectedFile().getPath()), StandardCopyOption.REPLACE_EXISTING);
                  Files.deleteIfExists(exportedFile);
               } catch (IOException ex) {
                  System.out.println("Error while saving file");
               }
            }
         }
      });
      exportButton.setPreferredSize(new Dimension(100, 20));
      optionPane.add(graphPane, BorderLayout.PAGE_START);
      optionPane.add(exportButton, BorderLayout.PAGE_END);
      return optionPane;
   }

   private String createHex(final Color color) {
      return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
   }

   private List<Line> createZipf() {
      final List<Line> result = new ArrayList<>(numberOfPoints - 1);
//      double actualProbability = computeFirstProbability();
//      final double k = actualProbability / (Math.pow(bConst + 1, lambda));
//      int currentIndex = 2;
//      while (currentIndex <= numberOfPoints) {
//         double nextProbability = k / Math.pow(bConst + currentIndex, lambda);
//         result.add(makeLineFromProbabilities(actualProbability, nextProbability, currentIndex, Color.RED));
//         actualProbability = nextProbability;
//         currentIndex++;
//      }
      final List<Double> zipfProbs = createZipfProbabilities();
      double actualProbability = zipfProbs.get(0);
      int currentIndex = 1;
      while (currentIndex < numberOfPoints) {
         double nextProbability = zipfProbs.get(currentIndex);
         result.add(makeLineFromProbabilities(actualProbability, nextProbability, currentIndex++, Color.RED));
         actualProbability = nextProbability;
      }
      return result;
   }

   private List<Line> createActual() {
      final List<Line> result = new ArrayList<>(numberOfPoints - 1);
//      int sum = computeSum();
//      Iterator<Map.Entry<Color, Integer>> iterator = realValues.iterator();
//      double currentProbability = iterator.next().getValue() / (double) sum;
//      int currentIndex = 2;
//      while (iterator.hasNext()) {
//         double nextProbability = iterator.next().getValue() / (double) sum;
//         result.add(makeLineFromProbabilities(currentProbability, nextProbability, currentIndex, Color.BLACK));
//         currentProbability = nextProbability;
//         currentIndex++;
//      }
//      return result;
      final List<Double> actualProbabilities = createActualProbabilities();
      double actualProbability = actualProbabilities.get(0);
      int currentIndex = 1;
      while (currentIndex < numberOfPoints) {
         double nextProbability = actualProbabilities.get(currentIndex);
         result.add(makeLineFromProbabilities(actualProbability, nextProbability, currentIndex++, Color.RED));
         actualProbability = nextProbability;
      }
      return result;
   }

   private List<Double> createZipfProbabilities() {
      final double k = computeKConst();
      final List<Double> results = new ArrayList<>(realValues.size());
      results.add(computeFirstProbability());
      for (int i = 2; i <= realValues.size(); i++) {
         results.add(k / Math.pow(bConst + i, lambda));
      }
      return results;
   }

   private List<Double> createActualProbabilities() {
      final List<Double> result = new ArrayList<>(realValues.size());
      final double sum = computeSum();
      for   (final Map.Entry<Color, Integer> color : realValues) {
         result.add((double)color.getValue() / sum);
      }
      return result;
   }

   private double computeKConst() {
      return computeFirstProbability() / (Math.pow(bConst + 1, lambda));
   }

   private double computeFirstProbability() {
      int sum = computeSum();
      return realValues.get(0).getValue() / (double) sum;
   }

   private int computeSum() {
      int sum = 0;
      for (final Map.Entry<Color, Integer> color : realValues) {
         sum += color.getValue();
      }
      return sum;
   }

   private Line makeLineFromProbabilities(final double first, final double second, final int index, final Color color) {
      int widthOfSegment = Math.round(DIALOG_WIDTH / (float) numberOfPoints);
      return new Line(
            index * widthOfSegment,
            DIALOG_HEIGHT - (int) Math.round(DIALOG_HEIGHT * first),
            (index + 1) * widthOfSegment,
            DIALOG_HEIGHT - (int) Math.round(DIALOG_HEIGHT * second),
            color);
   }

   private double computeXiSquared(final List<Double> expected, final List<Double> actual) {
      double result = 0;
      if (expected.size() != actual.size()) {
         throw new IllegalArgumentException("distributions must have equal amount of points");
      }
      for (int i = 0; i < expected.size(); i++) {
         result += Math.pow((actual.get(i) - expected.get(i)), 2) / expected.get(i);
      }
      return result;
   }

   private class GraphPanel extends JPanel {

      private final List<Line> zipf;
      private final List<Line> actual;

      public GraphPanel(final List<Line> zipf, final List<Line> actual) {
         this.zipf = zipf;
         this.actual = actual;
         setBackground(Color.WHITE);
      }

      @Override
      protected void paintComponent(final Graphics g) {
         super.paintComponent(g);
         g.setColor(Color.BLACK);
         for (final Line line : zipf) {
            g.drawRect(line.x1 - 5 , line.y1 - 5, 10, 10);
            g.drawLine(line.x1, line.y1, line.x2, line.y2);
         }
         g.setColor(Color.RED);
         for (final Line line : actual) {
            g.drawRect(line.x1 - 5, line.y1 - 5, 10, 10);
            g.drawLine(line.x1, line.y1, line.x2, line.y2);
         }
         g.setColor(Color.BLACK);
         g.drawString("Черная линия -- эталонное распределение", 10, 40);
         g.drawString("Красная линия -- реальное распределение", 10, 60);
         g.drawString(String.format("Разница по критерию хи-квадрат: %f%n", computeXiSquared(createZipfProbabilities(), createActualProbabilities())), 10, 80);
      }
   }

   private class Line {
      final int x1;
      final int y1;
      final int x2;
      final int y2;
      final Color color;

      private Line(final int x1, final int y1, final int x2, final int y2, final Color color) {
         this.x1 = x1;
         this.y1 = y1;
         this.x2 = x2;
         this.y2 = y2;
         this.color = color;
      }
   }

}
