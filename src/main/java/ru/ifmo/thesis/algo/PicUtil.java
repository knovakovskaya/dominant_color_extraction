package ru.ifmo.thesis.algo;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.imageio.ImageIO;

public abstract class PicUtil {
    public static int getRed(int rgb){
        /* returns 3rd 8 bits from int */
        return (rgb >> 16) & 255;
    }
    public static int getGreen(int rgb){
        /* returns 2nd 8 bits from int */
        return (rgb >> 8) & 255;
    }
    public static int getBlue(int rgb){
        /* returns 1st 8 bits from int */
        return rgb & 255;
    }

    public static int toRGB(int r, int g, int b){
        return r | g | b;
    }

    public static void saveImage(String filename, BufferedImage image) {
        File file = new File(filename);
        try {
            ImageIO.write(image, "png", file);
        } catch (Exception e) {
            System.out.println(e.toString() + " Image '" + filename + "' saving failed.");
        }
    }

    public static BufferedImage loadImage(String filename) {
        BufferedImage result = null;
        try {
            result = ImageIO.read(new File(filename));
        } catch (Exception e) {
            System.out.println(e.toString() + " Image '" + filename + "' not found.");
        }
        return result;
    }

    public static HashMap<Integer, Integer> parseColorsFromPicture(BufferedImage img){
        HashMap<Integer, Integer> colors = new HashMap<>();
        int w = img.getWidth();
        int h = img.getHeight();

        for (int i = 0; i < h; ++i){
            for (int j = 0; j < w; ++j) {
                int color = img.getRGB(j, i);
                if (!colors.containsKey(color)){
                    colors.put(color, 1);
                }else{
                    int pixelsNum = colors.get(color);
                    colors.put(color, pixelsNum+1);
                }
            }
        }

        return colors;
    }

    public static Collection<Entry<Integer, Integer>> getSortedColors(HashMap<Integer, Integer> colors) {
        /*
         * sorts colors with number of pixels, that have this color
         */
        ArrayList<Entry<Integer, Integer>> colorList = new ArrayList<>(colors.entrySet());
        Collections.sort(colorList, new Comparator<Entry<Integer, Integer>>() {
            @Override
            public int compare(final Entry<Integer, Integer> o1, final Entry<Integer, Integer> o2) {
                return -o1.getValue() + o2.getValue();
            }
        });
        return colorList;
    }
    
    public static Collection<Entry<Integer, Integer>> getOrderedColors(Collection<Entry<Integer, Integer>> colors) {
        /*
         * colors ordered with distance to 0;0;0
         */
        ArrayList<Entry<Integer, Integer>> colorList = new ArrayList<>(colors);
        Collections.sort(colorList, new Comparator<Entry<Integer, Integer>>() {
            @Override
            public int compare(final Entry<Integer, Integer> o1, final Entry<Integer, Integer> o2) {
                int v1 = getRed(o1.getKey()) + getBlue(o1.getKey()) + getGreen(o1.getKey())/3;
                int v2 = getRed(o2.getKey()) + getBlue(o2.getKey()) + getGreen(o2.getKey())/3;
                return -v1 + v2;
            }
        });
        return colorList;
    }


    public static Collection<Entry<Color, Integer>> castToColorArray(Collection<Entry<Integer, Integer>> colors){
        HashMap<Color, Integer> awtColors = new HashMap<>();
        for (Entry<Integer, Integer> simpleColor: colors)
            awtColors.put(new Color(simpleColor.getKey()), simpleColor.getValue());
        return new ArrayList<>(awtColors.entrySet());
    }
}
