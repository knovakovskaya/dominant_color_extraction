package ru.ifmo.thesis.gui;

import ru.ifmo.thesis.algo.AClusteringAlgorithms;
import ru.ifmo.thesis.algo.CommonSettings;
import ru.ifmo.thesis.algo.KMeans;
import ru.ifmo.thesis.algo.SimpleLinkage;
import ru.ifmo.thesis.gui.Util.ImagePanel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

public class Application extends JFrame {
    private SettingsPanel settingsPane;
    private ImagePanel originalImgPane;
    private ImagePanel clustorizedImgPane;
    private ColorDistributionPanel colorDistributionPane;

    private final int DEFAULT_WIDTH = 1024;
    private final int DEFAULT_HEIGHT = 600;

    AClusteringAlgorithms algo;
    String filename;

    Application(){
        filename = "";
        algo = null;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setTitle("Dominant color extraction application");
        //setJMenuBar(createMenu());
        setContentPane(createContentPane());
        setVisible(true);
        revalidate();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //create menu

    private JMenuBar createMenu(){
        final JMenuBar menuBar = new JMenuBar();
        JMenu menu;
        JMenuItem menuItem;


        menu = new JMenu("File");
        menuBar.add(menu);

        menuItem = new JMenuItem("Open Image");
        menuItem.setMnemonic(KeyEvent.VK_A);
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Open new image");
        //menuItem.addActionListener(openImageActionListener);
        menuItem.setEnabled(false);
        menu.add(menuItem);

        menuItem = new JMenuItem("Save Image");
        menuItem.setMnemonic(KeyEvent.VK_A);
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Save clustorized image");
        menuItem.setEnabled(false);
        menu.add(menuItem);

        menuItem = new JMenuItem("Save color distribution");
        menuItem.setMnemonic(KeyEvent.VK_A);
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Save color distribution");
        menuItem.setEnabled(false);
        menu.add(menuItem);

        menuItem = new JMenuItem("Close image");
        menuItem.setMnemonic(KeyEvent.VK_A);
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Close image and remove all calculated info");
        menuItem.setEnabled(false);
        menu.add(menuItem);

        ////////////////////////////////////////////////////////////

        menu = new JMenu("About");
        menu.setMnemonic(KeyEvent.VK_A);
        menu.setEnabled(false);
        menuBar.add(menu);

        return menuBar;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //create main pane

    private JPanel createContentPane(){
        final JPanel contentPane = new JPanel();
        contentPane.setLayout(new GridBagLayout());

        settingsPane = new KMeansSettingsPanel(new CommonSettings(10,
                                                                  0.001,
                                                                  0,
                                                                  CommonSettings.MergeType.DISABLED,
                                                                  CommonSettings.StartPointsAlgo.MAX_DIST));
        settingsPane.calculate.addActionListener(calculateActionListener);
        settingsPane.loadfile.addActionListener(openImageActionListener);
        settingsPane.algoBox.addActionListener(changeAlgoActionListener);
        updateAlgo();

        JPanel imagesPane = new JPanel();
        imagesPane.setLayout(new BoxLayout(imagesPane, BoxLayout.X_AXIS));
        originalImgPane = new ImagePanel(null, 10);
        clustorizedImgPane = new ImagePanel(null, 10);
        originalImgPane.setBorder(BorderFactory.createLineBorder(Color.black));
        originalImgPane.enableMaximizeOnClick();
        clustorizedImgPane.setBorder(BorderFactory.createLineBorder(Color.black));
        clustorizedImgPane.enableMaximizeOnClick();
        imagesPane.add(originalImgPane);
        imagesPane.add(clustorizedImgPane);

        colorDistributionPane = new ColorDistributionPanel();
        colorDistributionPane.clearAll();

        contentPane.add(imagesPane, getYPercentGBC(60,1));
        contentPane.add(colorDistributionPane, getYPercentGBC(25,2));
        contentPane.add(settingsPane, getYPercentGBC(15,0)); //order matters(look at change settings panel code
        return contentPane;
    }

    private GridBagConstraints getYPercentGBC(int yperc, int posy){
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = posy;
        gbc.gridwidth = gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = yperc;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        return gbc;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Action listeners

    public ActionListener openImageActionListener = new ActionListener(  ) {
        public void actionPerformed(ActionEvent event) {
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Images", "jpg", "png", "gif");
            JFileChooser chooser = new JFileChooser(Paths.get(filename).toAbsolutePath().toString());
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(getParent());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                filename = chooser.getSelectedFile().getAbsolutePath();
            }
            settingsPane.setHaveImage(true);
            updateAlgo();
            originalImgPane.setImage(algo.getOriginalImage());
            clearStand();
            updateChildren();
        }
    };

    public ActionListener calculateActionListener = new ActionListener(  ) {
        public void actionPerformed(ActionEvent event) {
            setCursor (Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            clearStand();
            updateAlgo();
            ArrayList<Map.Entry<Color, Integer>> colors = new ArrayList<>(algo.calculateAndGetColors());
            clustorizedImgPane.setImage(algo.getClustorizedImage());
            colorDistributionPane.colorize(colors, algo.getClustorizedImage().getWidth()*algo.getClustorizedImage().getHeight());
            updateChildren();
            setCursor (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    };

    public ActionListener changeAlgoActionListener = new ActionListener(  ) {
        public void actionPerformed(ActionEvent event) {
            JComboBox cb = (JComboBox) event.getSource();
            String startPoint = (String) cb.getSelectedItem();
            switch (startPoint) {
                case "KMeans":
                    settingsPane = new KMeansSettingsPanel(settingsPane.cs, algo.getOriginalImage() != null);
                    settingsPane.algoBox.setSelectedIndex(0);
                    changeSettingsPane(settingsPane);
                    break;
                case "SingleLinkage":
                    settingsPane = new SettingsPanel(settingsPane.cs, algo.getOriginalImage() != null);
                    settingsPane.algoBox.setSelectedIndex(1);
                    changeSettingsPane(settingsPane);
                    break;
            }

            updateAlgo();
            clearStand();
            updateChildren();
        }
    };

    private void changeSettingsPane(SettingsPanel newPane){
        settingsPane.calculate.addActionListener(calculateActionListener);
        settingsPane.loadfile.addActionListener(openImageActionListener);
        settingsPane.algoBox.addActionListener(changeAlgoActionListener);
        Component toRemove = getContentPane().getComponent(2);
        GridBagLayout layout = (GridBagLayout)getContentPane().getLayout();
        GridBagConstraints gbc = layout.getConstraints(getContentPane());
        remove(toRemove);
        getContentPane().add(settingsPane, getYPercentGBC(15,0));
    }

    private void updateAlgo(){
        switch ((String)settingsPane.algoBox.getSelectedItem()){
            case "KMeans":
                algo = new KMeans(filename, settingsPane.cs, KMeans.KMeansMode.CONTINUOUS);
                break;
            case "SingleLinkage":
                algo = new SimpleLinkage(filename, settingsPane.cs);
        }
    }

    private void clearStand(){
        if (originalImgPane.getImg() != null){
            clustorizedImgPane.setImage(null);
            colorDistributionPane.clearAll();
        }
    }

    private void updateChildren(){
        revalidate();
        repaint();
    }

     ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Entrace

    public static void main(String[] args) throws Exception {
        Application main = new Application();
    }
}
