package ru.ifmo.thesis.gui;

import ru.ifmo.thesis.algo.CommonSettings;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class SettingsPanel extends JPanel {
    public JButton loadfile;
    public JButton calculate;

    public JComboBox<String> algoBox;

    public JLabel cnumLabel;
    public JTextField clusterNum;

    public CommonSettings cs;
    public String[] algoList = {"KMeans", "SingleLinkage"};
    public boolean haveImage;
    public final int maskEnabled = 1;
    public int currentEnabled; //mask for fields

    public SettingsPanel(){
        this(null);
    }

    public SettingsPanel(CommonSettings pcs){
        this(pcs, false);
    }

    public SettingsPanel(CommonSettings pcs, boolean pHaveImage){
        currentEnabled = 1;
        if (pcs != null){
            cs = new CommonSettings(pcs);
        }else{
            cs = new CommonSettings(10, 0.001, 0.05, CommonSettings.StartPointsAlgo.RANDOM);
        }
        createContentPane();
        setHaveImage(pHaveImage);
    }

    public void createContentPane(){
        createBasicElements();
        setLayout(new GridBagLayout());
        add(loadfile);
        add(algoBox);
        add(cnumLabel);
        add(clusterNum);
        add(calculate);
    }

    public void createBasicElements(){
        loadfile = new JButton("Open image");
        calculate = new JButton("Run");
        calculate.setEnabled(false);
        algoBox = new JComboBox<>(algoList);
        algoBox.setSelectedIndex(0);
        //action listeners for above added from main frame

        cnumLabel = new JLabel("cluster number: ");
        clusterNum = new JTextField(String.valueOf(cs.clustersNum));
        clusterNum.setColumns(2);
        clusterNum.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(final DocumentEvent e) {
                currentEnabled = maskClusterNum(currentEnabled);
                calculate.setEnabled((currentEnabled == maskEnabled)&&haveImage);
            }

            @Override
            public void removeUpdate(final DocumentEvent e) {
                currentEnabled = maskClusterNum(currentEnabled);
                calculate.setEnabled((currentEnabled == maskEnabled)&&haveImage);
            }

            @Override
            public void changedUpdate(final DocumentEvent e){
                currentEnabled = maskClusterNum(currentEnabled);
                calculate.setEnabled((currentEnabled == maskEnabled)&&haveImage);
            }
        });
    }

    public void setHaveImage(boolean hi){
        haveImage = hi;
        calculate.setEnabled((currentEnabled == maskEnabled)&&haveImage);
    }

    /*disable/enable start button on cluster nums input*/
    private int maskClusterNum(int maskOld){
        int maskNew;
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
}
