package ru.ifmo.thesis.gui;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel{
    private BufferedImage img;
    public boolean doNotResize;
    private int imgHeight;
    private int imgWidth;
    private int imgBorder;
    public Dimension pms;

    public ImagePanel(BufferedImage bi, int border, boolean dnr, Dimension paneMinimumSize){
        pms = paneMinimumSize;
        setImage(bi, border, dnr);
    }

    public void setImage(BufferedImage bi, int border, boolean dnr){
        img = bi;
        imgBorder = border;
        doNotResize = dnr;
        onResize();
    }

    public void setImgBorder(int border){
        imgBorder = border;
    }

    public void enableMaximizeOnClick(){
        addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent arg0){
            if (img != null) {
                final JDialog dialog = new JDialog();
                dialog.setContentPane(new ImagePanel(img, 0, true, new Dimension(img.getWidth(), img.getHeight())));
                dialog.setMaximumSize(new Dimension(img.getWidth(), img.getHeight()));
                dialog.setMinimumSize(new Dimension(img.getWidth(), img.getHeight()));
                dialog.setVisible(true);
            }
            }
        });
    }

    public void setDoNotResize(boolean dnr){
        doNotResize = dnr;
        onResize();
    }

    public void onResize(){
        if (img == null){
            imgWidth = imgHeight = 0;
            setMinimumSize(new Dimension(pms.width, pms.height));
            setMaximumSize(new Dimension(pms.width, pms.height));
        } else {
            if (doNotResize) {
                imgHeight = img.getHeight();
                imgWidth = img.getWidth();
                setPreferredSize(new Dimension(imgWidth, imgHeight));
            } else {
                if (img.getWidth() > getWidth() - 2*imgBorder){
                    imgHeight = (int)(img.getHeight()*(pms.width-2*imgBorder)/img.getWidth());
                    imgWidth = pms.width-2*imgBorder;
                    setPreferredSize(new Dimension(imgWidth+2*imgBorder, imgHeight+2*imgBorder));
                }else{
                    imgHeight = img.getHeight();
                    imgWidth = img.getWidth();
                    setPreferredSize(new Dimension(pms.width, imgHeight + 2 * imgBorder));
                }
            }
            setMinimumSize(new Dimension(getPreferredSize().width, getPreferredSize().height));
            setMaximumSize(new Dimension(getPreferredSize().width, getPreferredSize().height));
        }
        revalidate();
    }


    @Override
    protected void paintComponent(Graphics g) {
        if (img != null) {
            BufferedImage printerImage = null;
            super.paintComponent(g);
            if (doNotResize) {
                printerImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
                g.drawImage(img, 0, 0, imgWidth, imgHeight, null);
            } else {
                printerImage = new BufferedImage(imgHeight, imgWidth, BufferedImage.TYPE_INT_RGB);
                int realBorder = (img.getWidth() > getWidth() - 2*imgBorder) ? imgBorder: (getWidth()-img.getWidth())/2;
                g.drawImage(img, realBorder, imgBorder, imgWidth, imgHeight, null);
            }
        }
    }
 }
