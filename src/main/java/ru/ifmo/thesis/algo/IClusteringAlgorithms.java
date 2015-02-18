package ru.ifmo.thesis.algo;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Map;

public interface IClusteringAlgorithms {

    public BufferedImage getOriginalImage();
    public BufferedImage getClustorizedImage();

    public void setSettings(CommonSettings cs);
    public CommonSettings getSettings();

    public BufferedImage calculate();
    public Collection<Map.Entry<Color, Integer>> calculateAndGetColors();

    public Cluster findClosestCluster(int rgb);
}
