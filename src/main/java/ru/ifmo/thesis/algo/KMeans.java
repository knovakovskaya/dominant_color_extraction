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
    public BufferedImage calculate() {
        long start = System.currentTimeMillis();
        int w = origin.getWidth();
        int h = origin.getHeight();
        clusters = ClusterUtil.getStartClusters(origin, cSettings);
        long endfirst = System.currentTimeMillis();
        System.out.println("Prepared start clusters in " + (endfirst - start) + " ms.");
        // create cluster lookup table
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
        // create result image
        clustorized = new BufferedImage(w, h,
                BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int clusterId = lut[w * y + x];
                clustorized.setRGB(x, y, clusters[clusterId].getRGB());
                int currentColor = clusters[clusterId].getRGB();
                if (colors.containsKey(currentColor)) {
                    colors.put(currentColor, colors.get(currentColor) + 1);
                } else {
                    colors.put(currentColor, 1);
                }
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("KMeans algorithm:\n"
                + "Clustered to " + clusters.length
                + " clusters in " + loops
                + " loops in " + (end - start) + " ms.");
        return clustorized;
    }

    public static Collection<Map.Entry<Color, Integer>> testFoo(String filename, int cnum, KMeansMode mode){
        //calculate
        CommonSettings cs = new CommonSettings(cnum, 0.001, 0.05, CommonSettings.StartPointsAlgo.SMART_TOP_RANDOM);
        KMeans kmeans = new KMeans(filename, cs, mode);
        BufferedImage dstImage = kmeans.calculate();
        return PicUtil.castToColorArray(PicUtil.getSortedColors(kmeans.colors));
    }



}
