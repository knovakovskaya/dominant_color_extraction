package ru.ifmo.thesis.gui;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel{
    private BufferedImage img;
    public boolean doNotResize;
    private int defaultImageBorder;

    private int imgHeight;
    private int imgWidth;
    private int imgXBorder;
    private int imgYBorder;


    public ImagePanel(BufferedImage bi, int defaultBorder){
        img = bi;
        defaultImageBorder = defaultBorder;
        doNotResize =  false;
    }

    public ImagePanel(BufferedImage bi, Dimension paneSize){
        setSize(paneSize);
        img = bi;
        defaultImageBorder = 0;
        doNotResize = true;
    }

    public void setImage(BufferedImage bi){
        img = bi;
    }

    public BufferedImage getImg(){
        return img;
    }

    public void enableMaximizeOnClick(){
        addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent arg0){
            if (img != null) {
                final JDialog dialog = new JDialog();
                int x = (img.getWidth()>800)? 800: img.getWidth();
                int y = (img.getHeight()>600)? 600: img.getHeight();
                ImagePanel contentPane = new ImagePanel(img, new Dimension(x,y));
                dialog.setContentPane(contentPane);
                dialog.setSize(new Dimension(x,y));
                dialog.setLocation(20,40);
                dialog.setVisible(true);
            }
            }
        });
    }

    public void calcImgSize(){
        if (img == null){
            imgWidth = imgHeight = 0;
        } else {
            if (doNotResize) {
                imgHeight = img.getHeight();
                imgWidth = img.getWidth();
            } else {
                if ((img.getWidth() < getWidth() - defaultImageBorder*2) && //2small pic. do not resize
                        (img.getHeight() < getHeight() - defaultImageBorder*2)){
                    imgXBorder = getWidth() - img.getWidth()/2;
                    imgYBorder = getHeight() - img.getHeight()/2;
                    imgWidth = getWidth();
                    imgHeight = getHeight();
                }else{
                    if ((double)img.getWidth()/getWidth() > (double)img.getHeight()/getHeight()){
                        //choose side to base on
                        if ((double)img.getWidth()/img.getHeight() > (double)getWidth()/getHeight()){
                            imgXBorder = 0;
                            imgWidth = getWidth();
                        }else{
                            imgXBorder = defaultImageBorder;
                            imgWidth = getWidth() - 2*imgXBorder;
                        }
                        imgHeight = (int)(((double)getWidth()/img.getWidth())*img.getHeight());
                        imgYBorder = (getHeight() - imgHeight)/2;
                    }else{
                        if ((double)img.getHeight()/img.getWidth() > (double)getHeight()/getWidth()){
                            imgYBorder = 0;
                            imgHeight = getHeight();
                        }else{
                            imgYBorder = defaultImageBorder;
                            imgHeight = getHeight() - imgYBorder*2;
                        }
                        imgWidth = (int)(((double)getHeight()/img.getHeight())*img.getWidth());
                        imgXBorder = (getWidth() - imgWidth)/2;
                    }
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (img != null) {
            BufferedImage printerImage = null;
            super.paintComponent(g);
            calcImgSize();
            if (doNotResize) {
                printerImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
                g.drawImage(img, 0, 0, imgWidth, imgHeight, null);
            } else {
                printerImage = new BufferedImage(imgHeight, imgWidth, BufferedImage.TYPE_INT_RGB);
                g.drawImage(img, imgXBorder, imgYBorder, imgWidth, imgHeight, null);
            }
        }
    }
 }
