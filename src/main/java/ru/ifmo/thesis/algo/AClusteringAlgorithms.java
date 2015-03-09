package ru.ifmo.thesis.algo;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class AClusteringAlgorithms implements IClusteringAlgorithms {
    protected BufferedImage origin;
    protected BufferedImage clustorized;

    protected CommonSettings cSettings;

    protected HashMap<Integer, Integer> colors = new HashMap<>();
    protected Cluster[] clusters;

    public AClusteringAlgorithms(String filename, CommonSettings cs){
        cSettings = new CommonSettings(cs);
        origin = PicUtil.loadImage(filename);
    }

    @Override
    public void setSettings(CommonSettings cs) {
        cSettings = cs;
    }

    @Override
    public CommonSettings getSettings(){
        return cSettings;
    }

    @Override
    public BufferedImage getClustorizedImage() {
        return clustorized;
    }

    @Override
    public BufferedImage getOriginalImage() {
        return origin;
    }


    @Override
    public Cluster findClosestCluster(int rgb) {
        Cluster closest_cluster = null;
        int min = Integer.MAX_VALUE;
        for (Cluster cluster : clusters) {
            int distance = cluster.distance(rgb);
            if (distance < min) {
                min = distance;
                closest_cluster = cluster;
            }
        }
        return closest_cluster;
    }

    @Override
    public Collection<Map.Entry<Color, Integer>> calculateAndGetColors() {
        assert cSettings.clustersNum > 0 && cSettings.clustersNum <= CommonSettings.MAX_CLUSTERS_NUM;
        calculate();
        return PicUtil.castToColorArray(PicUtil.getSortedColors(colors));
    }

    public BufferedImage calculate(){
        long start = System.currentTimeMillis();
        calculateClusters();
        long end = System.currentTimeMillis();
        System.out.println("  Clustered to " + clusters.length + " clusters\n"
                         + "  in " + (end - start) + " ms.");
        return clustorized;
    }

    /* always calculates the clusters and clusterized image*/
    public abstract BufferedImage calculateClusters();

    protected void createClusterizedImage(int pixelToCluster[]){
        // create result image
        int w = origin.getWidth(), h = origin.getHeight();
        clustorized = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int clusterId = pixelToCluster[w * y + x];
                clustorized.setRGB(x, y, clusters[clusterId].getRGB());
                int currentColor = clusters[clusterId].getRGB();
                if (colors.containsKey(currentColor)) {
                    colors.put(currentColor, colors.get(currentColor) + 1);
                } else {
                    colors.put(currentColor, 1);
                }
            }
        }
    }

}
