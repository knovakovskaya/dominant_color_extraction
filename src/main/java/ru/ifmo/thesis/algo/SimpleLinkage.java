package ru.ifmo.thesis.algo;


import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class SimpleLinkage extends AClusteringAlgorithms {

    public SimpleLinkage(String filename, CommonSettings cs){
        super(filename, cs);
    }

    @Override
    public BufferedImage calculateClusters() {
        int loops = 0, tmp;
        int h = origin.getHeight(), w = origin.getWidth();
        int[] lut = new int[w * h];
        createClusterizedImage(lut);

        System.out.println("Single-Linkage algorithm:\n"
                + "  in " + loops + " loops");
        return clustorized;
    }

}
