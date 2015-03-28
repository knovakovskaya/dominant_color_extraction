package ru.ifmo.thesis.gui;

import ru.ifmo.thesis.algo.CommonSettings;
import ru.ifmo.thesis.gui.Util.JValueField;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class SettingsPanel extends JPanel {
    public JButton loadfile;
    public JButton calculate;
    public JComboBox<String> algoBox;
    public JLabel cnumLabel;
    public JValueField clusterNum;
    public JCheckBox enableMergeCheckbox;
    public JMergeSettingsPanel mergePane;

    public CommonSettings cs;
    public String[] algoList = {"KMeans",};// "SingleLinkage"};
    public boolean haveImage;
    public JPanel topPane;

    public class SPDocumentListener implements DocumentListener {
        public void patchConfig(){}

        @Override
        public void insertUpdate(final DocumentEvent e) {
            patchConfig();
            calculate.setEnabled(readyToStart());
        }

        @Override
        public void removeUpdate(final DocumentEvent e) {
            patchConfig();
            calculate.setEnabled(readyToStart());
        }

        @Override
        public void changedUpdate(final DocumentEvent e){
            patchConfig();
            calculate.setEnabled(readyToStart());
        }
    };

    public SettingsPanel(){
        this(null);
    }

    public SettingsPanel(CommonSettings pcs){
        this(pcs, false);
    }

    public SettingsPanel(CommonSettings pcs, boolean pHaveImage){
        cs = new CommonSettings(pcs);
        createContentPane();
        setHaveImage(pHaveImage);
    }

    protected void fillTopPane(){
        topPane.setLayout(new GridBagLayout());
        topPane.add(loadfile);
        topPane.add(algoBox);
        topPane.add(cnumLabel);
        topPane.add(clusterNum);
        topPane.add(enableMergeCheckbox);
        topPane.add(calculate);
    }

    public void createContentPane(){
        createBasicElements();
        fillTopPane();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(topPane);
        add(mergePane);
        mergePane.setVisible(false);
    }

    public void createBasicElements(){
        topPane = new JPanel();

        loadfile = new JButton("Open image");
        calculate = new JButton("Run");
        calculate.setEnabled(false);
        algoBox = new JComboBox<>(algoList);
        algoBox.setSelectedIndex(0);
        //action listeners for above added from main frame

        cnumLabel = new JLabel("cluster number: ");
        clusterNum = new JValueField(String.valueOf(cs.clustersNum), JValueField.Type.PINT, 2);
        clusterNum.getDocument().addDocumentListener(new SPDocumentListener(){
            @Override
            public void patchConfig(){
                if (clusterNum.correctValue()) cs.clustersNum = Integer.parseInt(clusterNum.getText());
            }
        });


        enableMergeCheckbox = new JCheckBox("enable post-merge", cs.mergeType!=CommonSettings.MergeType.DISABLED);
        enableMergeCheckbox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                mergePane.setVisible(e.getStateChange() == 1);
                if (e.getStateChange() == 1)
                    cs.mergeType = mergePane.getMergeType();
                else
                    cs.mergeType = CommonSettings.MergeType.DISABLED;
                calculate.setEnabled(readyToStart());
            }
        });
        mergePane = new JMergeSettingsPanel();
        mergePane.sizeBorder.getDocument().addDocumentListener(new SPDocumentListener(){
            @Override
            public void patchConfig(){
                if (mergePane.sizeBorder.correctValue())
                    cs.border = Double.parseDouble(mergePane.sizeBorder.getText());
            }
        });
        mergePane.similarityBorder.getDocument().addDocumentListener(new SPDocumentListener(){
            @Override
            public void patchConfig(){
                if (mergePane.similarityBorder.correctValue())
                    cs.minDist = Integer.parseInt(mergePane.similarityBorder.getText());
            }
        });
    }

    public void setHaveImage(boolean hi){
        haveImage = hi;
        calculate.setEnabled(readyToStart());
    }


    public boolean readyToStart(){
        return clusterNum.correctValue() && haveImage &&
                (!enableMergeCheckbox.isSelected() ||
                        (mergePane.similarityBorder.correctValue() &&
                         mergePane.sizeBorder.correctValue()));
    }

    class JMergeSettingsPanel extends JPanel{
        public JValueField sizeBorder;
        public JValueField similarityBorder;
        public JComboBox<String> cond;

        public JMergeSettingsPanel(){
            createContent();
            createContentPane();
        }

        public void createContentPane(){
            setLayout(new GridBagLayout());
            add(new JLabel("Merge if: cluster is <= than "));
            add(sizeBorder);
            add(new JLabel("of pic"));
            add(cond);
            add(new JLabel(" distance to closest cluster is <= "));
            add(similarityBorder);
        }

        public void createContent(){
            String[] comboVariance = {"or", "and"};
            cond = new JComboBox<>(comboVariance);
            cond.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JComboBox cb = (JComboBox) e.getSource();
                    if (enableMergeCheckbox.isSelected())
                        cs.mergeType = getMergeType();
                }
            });
            if (cs.mergeType == CommonSettings.MergeType.OR ||
                    cs.mergeType == CommonSettings.MergeType.DISABLED)
            cond.setSelectedIndex(0);
            sizeBorder = new JValueField(Double.toString(cs.border), JValueField.Type.PERCENTAGE, 5);
            similarityBorder = new JValueField(Integer.toString(cs.minDist), JValueField.Type.UINT, 5);
        }

        public CommonSettings.MergeType getMergeType(){
            switch ((String) cond.getSelectedItem()) {
                case "or":
                    return CommonSettings.MergeType.OR;
                default:
                    return CommonSettings.MergeType.AND;
            }
        }
    }
}
