package ru.ifmo.thesis.algo;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

public abstract class AClusteringAlgorithms implements IClusteringAlgorithms {
    protected BufferedImage origin;
    protected BufferedImage clustorized;

    protected CommonSettings cSettings;

    protected HashMap<Integer, Integer> colors = new HashMap<>();
    protected Cluster[] clusters;
    protected int distanceType;

    public AClusteringAlgorithms(String filename, CommonSettings cs){
        cSettings = new CommonSettings(cs);
        origin = PicUtil.loadImage(filename);
        distanceType = 1;
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
        return findClosestClusterInRange(rgb, 0, clusters.length);
    }

    private Cluster findClosestClusterInRange(int rgb, int left, int right){
        Cluster closest_cluster = null;
        int min = Integer.MAX_VALUE;
        for (int i = left; i < right; ++i) {
            int distance = (distanceType == 0) ? clusters[i].distance(rgb): clusters[i].distance2p(rgb);
            if (distance < min) {
                min = distance;
                closest_cluster = clusters[i];
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
        HashMap<Integer,Integer> idToPos = new HashMap<Integer,Integer>();
        for (int i = 0; i < clusters.length; ++i)
            idToPos.put(clusters[i].id, i);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (x==45 && y ==18){
                    System.out.println(Integer.toString(x)+" "+Integer.toString(y));
                }
                int clusterId = pixelToCluster[w * y + x];
                clustorized.setRGB(x, y, clusters[idToPos.get(clusterId)].getRGB());
                int currentColor = clusters[idToPos.get(clusterId)].getRGB();
                if (colors.containsKey(currentColor)) {
                    colors.put(currentColor, colors.get(currentColor) + 1);
                } else {
                    colors.put(currentColor, 1);
                }
            }
        }
    }

    protected void mergeClusters(int[] lut){
        if (cSettings.mergeType != CommonSettings.MergeType.DISABLED){
            ArrayList<Cluster> newClusters =  new ArrayList<>();
            HashSet<Integer> badClusters = new HashSet<>();

            Arrays.sort(clusters, new Comparator<Cluster>() {
                @Override
                public int compare(Cluster o1, Cluster o2) {
                    return Integer.compare(o1.pixelCount, o2.pixelCount);
                }
            });

            for (int i = 0; i < clusters.length-1; ++i){ //at least on cluster is ood
                Cluster cls = findClosestClusterInRange(clusters[i].getRGB(), i + 1, clusters.length);
                int distance = Integer.MAX_VALUE;
                if (cls != null)
                    distance = clusters[i].distance(cls);

                if (cSettings.mergeType == CommonSettings.MergeType.AND){
                    if ( ((double)clusters[i].pixelCount/lut.length < cSettings.border) &&
                            (cls != null && distance < cSettings.minDist) ){
                        badClusters.add(clusters[i].id);
                    }else{
                        newClusters.add(clusters[i]);
                    }
                }else{
                    if ( ((double)clusters[i].pixelCount/lut.length < cSettings.border) ||
                            (cls != null && distance < cSettings.minDist) ){
                        badClusters.add(clusters[i].id);
                    }else{
                        newClusters.add(clusters[i]);
                    }
                }
            }
            newClusters.add(clusters[clusters.length-1]);

            //all clusters to delete id's now in bad clusters, all good clusters in new clusters
            clusters = newClusters.toArray(new Cluster[newClusters.size()]);
            for (int i = 0; i < lut.length; ++i){
                if (badClusters.contains(lut[i])){
                    Cluster newCluster = findClosestCluster(origin.getRGB(i%origin.getWidth(), i/origin.getWidth()));
                    lut[i] = newCluster.id;
                    newCluster.addPixel(origin.getRGB(i%origin.getWidth(), i/origin.getWidth()));
                }
            }
        }
    }


}
