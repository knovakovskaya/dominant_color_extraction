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

    public abstract BufferedImage calculate();

}
