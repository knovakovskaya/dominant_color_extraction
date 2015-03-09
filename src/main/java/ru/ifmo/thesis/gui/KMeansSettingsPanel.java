package ru.ifmo.thesis.gui;

import ru.ifmo.thesis.algo.CommonSettings;
import ru.ifmo.thesis.algo.KMeans;

import javax.swing.JComboBox;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class KMeansSettingsPanel extends SettingsPanel{

    public JComboBox<String> startPointsBox;

    public KMeansSettingsPanel(){
        super();
    }

    public KMeansSettingsPanel(CommonSettings pcs){
        super(pcs);
    }


    public KMeansSettingsPanel(CommonSettings pcs, boolean pHaveImage){
        super(pcs,pHaveImage);
    }

    @Override
    public void createContentPane(){
        createBasicElements();
        String[] startPointsList = {"Diagonal", "Random", "Smart(top colors)"};

        startPointsBox = new JComboBox<>(startPointsList);
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
        setLayout(new GridBagLayout());
        add(loadfile);
        add(algoBox);
        add(startPointsBox);
        add(cnumLabel);
        add(clusterNum);
        add(calculate);
    }

}
