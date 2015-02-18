package ru.ifmo.thesis.gui;

import ru.ifmo.thesis.algo.AClusteringAlgorithms;
import ru.ifmo.thesis.algo.CommonSettings;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Arc2D;

public class SettingsPanel extends JPanel {
    public JButton loadfile;
    public JButton calculate;

    public JComboBox algoBox;
    public JComboBox startPointsBox;

    public JTextField border;
    public JTextField clusterNum;

    public CommonSettings cs;
    public String[] algoList;
    public boolean haveImage;
    public final int maskEnabled = 3;
    public int currentEnabled; //mask for fields

    SettingsPanel(Dimension d){
        setMinimumSize(d);
        setSize(d);
        setMaximumSize(d);
        cs = new CommonSettings(10, 0.001, 0.05, CommonSettings.StartPointsAlgo.RANDOM);
        algoList = new String[1];
        algoList[0] = "KMeans";
        currentEnabled = 3;
        haveImage = false;
        createContentPane();
    }

    public void createContentPane(){
        String[] startPointsList = {"Diagonal", "Random", "Smart(top colors)"};
        setLayout(new GridBagLayout());

        loadfile = new JButton("Open image");
        calculate = new JButton("Run");
        calculate.setEnabled(false);
        algoBox = new JComboBox(algoList);
        algoBox.setSelectedIndex(0);
        //action listeners here from added from main frame

        startPointsBox = new JComboBox(startPointsList);
        startPointsBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox) e.getSource();
                String startPoint = (String) cb.getSelectedItem();
                switch (startPoint) {
                    case "Diagonal":
                        cs.startPoint = CommonSettings.StartPointsAlgo.DIAG;
                        break;
                    case "Random":
                        cs.startPoint = CommonSettings.StartPointsAlgo.RANDOM;
                        break;
                    case "Smart(top colors)":
                        cs.startPoint = CommonSettings.StartPointsAlgo.SMART_TOP_RANDOM;
                        break;
                }

            }
        });
        startPointsBox.setSelectedIndex(1);


        JLabel cnumLabel = new JLabel("cluster number: ");
        clusterNum = new JTextField(String.valueOf(cs.clustersNum));
        clusterNum.setColumns(2);
        clusterNum.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(final DocumentEvent e) {
                currentEnabled = maskClusterNum(clusterNum.getText(), currentEnabled);
                calculate.setEnabled((currentEnabled == maskEnabled)&&haveImage);
            }

            @Override
            public void removeUpdate(final DocumentEvent e) {
                currentEnabled = maskClusterNum(clusterNum.getText(), currentEnabled);
                calculate.setEnabled((currentEnabled == maskEnabled)&&haveImage);
            }

            @Override
            public void changedUpdate(final DocumentEvent e){
                currentEnabled = maskClusterNum(clusterNum.getText(), currentEnabled);
                calculate.setEnabled((currentEnabled == maskEnabled)&&haveImage);
            }
        });

        JLabel borderLabel  = new JLabel("border: ");
        border = new JTextField(String.valueOf(cs.border));
        border.setColumns(5);
        border.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(final DocumentEvent e) {
                currentEnabled = currentEnabled|2;
                calculate.setEnabled((currentEnabled == maskEnabled)&&haveImage);
            }

            @Override
            public void removeUpdate(final DocumentEvent e) {
                currentEnabled = currentEnabled&(Integer.MAX_VALUE-2);
                calculate.setEnabled((currentEnabled == maskEnabled)&&haveImage);
            }

            @Override
            public void changedUpdate(final DocumentEvent e){
                currentEnabled = currentEnabled|2;
                calculate.setEnabled((currentEnabled == maskEnabled)&&haveImage);
            }
        });
        add(loadfile);
        add(algoBox);
        add(startPointsBox);
        add(cnumLabel);
        add(clusterNum);
        add(borderLabel);
        add(border);
        add(calculate);
    }

    public void setHaveImage(boolean hi){
        haveImage = hi;
        calculate.setEnabled((currentEnabled == maskEnabled)&&haveImage);
    }

    private int maskClusterNum(String s, int maskOld){
        int maskNew = maskOld;
        try{
            if (Integer.parseInt(clusterNum.getText()) > 0) {
                maskNew = maskOld | 1;
                cs.clustersNum = Integer.parseInt(clusterNum.getText());
            }else{
                maskNew = maskOld & (Integer.MAX_VALUE - 1);
            }
        }catch(Exception exc) {
             maskNew = maskOld & (Integer.MAX_VALUE - 1);
        }
        return maskNew;
    }

    private int maskBorder(String s, int maskOld){
        int maskNew = maskOld;
        try{
            if (Double.parseDouble(border.getText()) > 0) {
                maskNew = maskOld | 2;
                cs.border = Double.parseDouble(border.getText());
            }else{
                maskNew = maskOld & (Integer.MAX_VALUE - 2);
            }
        }catch(Exception exc) {
             maskNew = maskOld & (Integer.MAX_VALUE - 2);
        }
        return maskNew;
    }
}
