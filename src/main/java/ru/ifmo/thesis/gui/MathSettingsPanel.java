package ru.ifmo.thesis.gui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.geom.Arc2D;

public class MathSettingsPanel extends JPanel{
    private JTextField lambdaField;
    private JTextField bconstField;
    public JButton calculateDistribution;

    private final int maskEnabled = 3;
    private int currentEnabled;
    private boolean haveColorDistribution;

    MathSettingsPanel(){
        setLayout(new FlowLayout(FlowLayout.LEFT));
        createContentPane();
        currentEnabled = maskEnabled;
        haveColorDistribution = false;
        calculateDistribution.setEnabled(false);
    }

    private void createContentPane(){
        lambdaField = new JTextField("1");
        lambdaField.setColumns(4);
        lambdaField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(final DocumentEvent e) {
                currentEnabled = maskDoubleTextField(lambdaField.getText(), currentEnabled, 0);
                calculateDistribution.setEnabled((currentEnabled == maskEnabled) && haveColorDistribution);
            }

            @Override
            public void removeUpdate(final DocumentEvent e) {
                currentEnabled = maskDoubleTextField(lambdaField.getText(), currentEnabled, 0);
                calculateDistribution.setEnabled((currentEnabled == maskEnabled) && haveColorDistribution);
            }

            @Override
            public void changedUpdate(final DocumentEvent e) {
                currentEnabled = maskDoubleTextField(lambdaField.getText(), currentEnabled, 0);
                calculateDistribution.setEnabled((currentEnabled == maskEnabled) && haveColorDistribution);
            }
        });
        bconstField = new JTextField("2");
        bconstField.setColumns(4);
        bconstField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(final DocumentEvent e) {
                currentEnabled = maskDoubleTextField(bconstField.getText(), currentEnabled, 1);
                calculateDistribution.setEnabled((currentEnabled == maskEnabled) && haveColorDistribution);
            }

            @Override
            public void removeUpdate(final DocumentEvent e) {
                currentEnabled = maskDoubleTextField(bconstField.getText(), currentEnabled, 1);
                calculateDistribution.setEnabled((currentEnabled == maskEnabled) && haveColorDistribution);
            }

            @Override
            public void changedUpdate(final DocumentEvent e) {
                currentEnabled = maskDoubleTextField(bconstField.getText(), currentEnabled, 1);
                calculateDistribution.setEnabled((currentEnabled == maskEnabled) && haveColorDistribution);
            }
        });
        calculateDistribution = new JButton("distribution");

        add(new JLabel("lambda"));
        add(lambdaField);
        add(new JLabel("B-constant"));
        add(bconstField);
        add(calculateDistribution);
    }

    public double getLambda(){
        return Double.parseDouble(lambdaField.getText());
    }

    public double getBConst(){
        return Double.parseDouble(bconstField.getText());
    }

    public void setHaveColorDistribution(boolean have){
        haveColorDistribution = have;
        calculateDistribution.setEnabled((currentEnabled == maskEnabled) && haveColorDistribution);
        lambdaField.setEnabled(haveColorDistribution);
        bconstField.setEnabled(haveColorDistribution);
    }


    private int maskDoubleTextField(String sValue, int maskOld, int bitpos){
        int maskNew = 0;
        try{
            if (Double.parseDouble(sValue) > 0) {
                maskNew = maskOld | (1<<bitpos);
                Double.parseDouble(sValue);
            }else{
                maskNew = maskOld & ~(1<<bitpos);
            }
        }catch(Exception exc) {
            maskNew = maskOld & ~(1<<bitpos);
        }
        return maskNew;
    }


}
