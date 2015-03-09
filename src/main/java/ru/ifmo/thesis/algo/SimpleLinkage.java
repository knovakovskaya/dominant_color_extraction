package ru.ifmo.thesis.algo;


import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
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
        HashMap<Integer, LinkedList<Integer>> clustersMap = new HashMap<Integer, LinkedList<Integer>>();
        for (int i = 0; i < w*h; ++i){
            LinkedList<Integer> clusterset = new LinkedList<Integer>();
            clusterset.add(i);
            clustersMap.put(i, clusterset);
            lut[i] = i;
        }

        while (clustersMap.size() > cSettings.clustersNum){
            for (int i = 0; i < w*h-1 && clustersMap.size() > cSettings.clustersNum; ++i){
                int currentMin = Integer.MAX_VALUE, i1 = -1, i2 = -1;
                for (int j = i+1; j < w*h; ++j){
                    if (lut[i] != lut[j] &&
                            PicUtil.Distance(origin.getRGB(i%w, i/w), origin.getRGB(j%w, j/w)) < currentMin){
                        currentMin = PicUtil.Distance(origin.getRGB(i%w, i/w), origin.getRGB(j%w, j/w));
                        i1 = i; i2 = j;
                    }
                }

                int clusterToRemove = lut[i1];
                for (Integer pos: clustersMap.get(clusterToRemove))
                    lut[pos] = lut[i2];
                clustersMap.get(lut[i2]).addAll(clustersMap.get(clusterToRemove));
                clustersMap.remove(clusterToRemove);
            }
            loops++;
        }
        clusters = new Cluster[clustersMap.size()];
        tmp = 0;
        for (Map.Entry<Integer, LinkedList<Integer>> entry : clustersMap.entrySet()) {
            clusters[tmp] =  new Cluster(tmp);
            for (Integer pixelPos: entry.getValue()){
                clusters[tmp].addPixel(origin.getRGB(pixelPos%w, pixelPos/w));
                lut[pixelPos] = tmp;
            }
            tmp++;
        }

        createClusterizedImage(lut);

        System.out.println("Single-Linkage algorithm:\n"
                + "  in " + loops + " loops");
        return clustorized;
    }

}
