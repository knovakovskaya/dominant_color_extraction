package ru.ifmo.thesis.algo;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.Map.Entry;

public class KMeans extends AClusteringAlgorithms{

    public enum KMeansMode {
        CONTINUOUS, ITERATIVE
    }

    private KMeansMode mode;

    public KMeans(String filename, CommonSettings cs, KMeansMode kmMode){
        super(filename, cs);
        mode = kmMode;
    }

    @Override
    public BufferedImage calculateClusters() {
        long start = System.currentTimeMillis();
        clusters = ClusterUtil.getStartClusters(origin, cSettings);
        long endfirst = System.currentTimeMillis();
        System.out.println("Prepared start clusters in " + (endfirst - start) + " ms.");

        // create cluster lookup table
        int h = origin.getHeight(), w = origin.getWidth();
        int[] lut = new int[w * h];
        Arrays.fill(lut, -1);

        boolean pixelChangedCluster = true;
        int loops = 0;
        while (pixelChangedCluster && loops <50) {
            pixelChangedCluster = false;
            loops++;
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int pixel = origin.getRGB(x, y);
                    Cluster cluster = findClosestCluster(pixel);
                    if (lut[w * y + x] != cluster.getId()) {
                        if (mode == KMeansMode.CONTINUOUS) {
                            if (lut[w * y + x] != -1)
                                clusters[lut[w * y + x]].removePixel( pixel);
                            cluster.addPixel(pixel);
                        }
                        // continue looping
                        pixelChangedCluster = true;

                        // update lut
                        lut[w * y + x] = cluster.getId();
                    }
                }
            }
            if (mode == KMeansMode.ITERATIVE) {
                // update clusters
                for (Cluster cluster : clusters)
                    cluster.clear();
                for (int y = 0; y < h; y++) {
                    for (int x = 0; x < w; x++) {
                        int clusterId = lut[w * y + x];
                        clusters[clusterId].addPixel(origin.getRGB(x, y));
                    }
                }
            }
        }
        createClusterizedImage(lut);

        System.out.println("KMeans algorithm:\n"
                         + "  in " + loops + " loops");
        return clustorized;
    }

}
