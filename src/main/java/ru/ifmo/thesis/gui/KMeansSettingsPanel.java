package ru.ifmo.thesis.gui;

import ru.ifmo.thesis.algo.CommonSettings;

import javax.swing.JComboBox;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class KMeansSettingsPanel extends SettingsPanel{

    public JComboBox<String> startPointsBox;

    public KMeansSettingsPanel(CommonSettings pcs){
        this(pcs, false);
    }


    public KMeansSettingsPanel(CommonSettings pcs, boolean pHaveImage){
        super(pcs,pHaveImage);
        if (pcs.startPoint == CommonSettings.StartPointsAlgo.DIAG)
            startPointsBox.setSelectedIndex(0);
        if (pcs.startPoint == CommonSettings.StartPointsAlgo.RANDOM)
            startPointsBox.setSelectedIndex(1);
        if (pcs.startPoint == CommonSettings.StartPointsAlgo.PLUS_PLUS)
            startPointsBox.setSelectedIndex(2);
    }

    @Override
    public void createContentPane(){
        createBasicElements();
        String[] startPointsList = {"Diagonal", "Random", "PlusPlus"};

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
                    case "PlusPlus":
                        cs.startPoint = CommonSettings.StartPointsAlgo.PLUS_PLUS;
                        break;
                }
            }
        });
        setLayout(new GridBagLayout());
        add(loadfile);
        add(algoBox);
        add(startPointsBox);
        add(cnumLabel);
        add(clusterNum);
        add(calculate);
    }

}
