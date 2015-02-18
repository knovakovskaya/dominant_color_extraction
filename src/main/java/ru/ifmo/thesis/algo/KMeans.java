package ru.ifmo.thesis.algo;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.Map.Entry;

public class KMeans {

    //TODO: move this functionality from testing method to GUI
    /* start point added for KMEANS testing */
    public static void main(String[] args) {
        args = new String[] {"/Users/knovakovskaya/Documents/thesis/pics/sample1.jpg", "/Users/knovakovskaya/clusterized.png", "5", "-c"};

        if (args.length != 4) {
            System.out.println("Usage: java popscan.KMeans"
                    + " [source image filename]"
                    + " [destination image filename]"
                    + " [clustercount 0-255]"
                    + " [mode -i (ITERATIVE)|-c (CONTINUOS)]");
            return;
        }
        // parse arguments
        String src = args[0];
        String dst = args[1];
        int k = Integer.parseInt(args[2]);
        String m = args[3];
        KMeansMode mode = KMeansMode.CONTINUOUS;
        //if (m.equals("-i")) {
            mode = KMeansMode.ITERATIVE;
        //} else if (m.equals("-c")) {
        //    mode = KMeansMode.CONTINUOUS;
        //}

        // create new KMeans object
        KMeans kmeans = new KMeans(mode, 0.001);
        // call the function to actually start the clustering
        BufferedImage dstImage = kmeans.calculate(PicUtil.loadImage(src), MAX_CLUSTERS_NUM);
        // save the resulting image
        PicUtil.saveImage(dst, dstImage);
    }

    /*
     * Separator for test functions and KMeans functionality
     *
     *
     */

    public enum KMeansMode {
        CONTINUOUS, ITERATIVE
    }

    private final static HashMap<Integer, Integer> colors = new HashMap<>();
    private static final double GLOBAL_PART = 0.05;
    private static final int MAX_CLUSTERS_NUM = 20;
    private static final int TOP_COLORS = 500;
    private static final int TOO_MANY_COLORS = 5000;
    private KMeansMode mode;
    private double border;
    private Cluster[] clusters;


    public KMeans() {
        mode = KMeansMode.CONTINUOUS;
        border = -1.0;
    }

    public KMeans(KMeansMode amode, double aborder){
        mode = amode;
        border = aborder;
    }

    /*
     * KMeans algo functions. All functions to creates start clusters
     * and to calculate final clusters.
     */

    public Cluster[] getStartClustersSimple(BufferedImage image, int k) {
        Cluster[] result = new Cluster[k];
        int dx = image.getWidth() / k;
        int dy = image.getHeight() / k;
        for (int i = 0, x = 0, y = 0; i < k; i++) {
            result[i] = new Cluster(i, image.getRGB(x, y));
            x += dx;
            y += dy;
        }
        return result;
    }

    private Cluster[] randomKClustersFromCollection(Collection<Entry<Integer, Integer>> colors, int k) {
        Cluster[] clusters = new Cluster[k];
        ArrayList<Entry<Integer,Integer>> colorsList = new ArrayList<>(colors);
        Collections.shuffle(colorsList);
        return clustersFromColorCollection(colorsList.subList(0,k));
    }

    private Cluster[] clustersFromColorCollection(Collection<Entry<Integer, Integer>> colors){
        Cluster[] clusters = new Cluster[colors.size()];
        int id = 0;
        for (Map.Entry<Integer,Integer> entry: colors){
            clusters[id] = new Cluster(id, entry.getKey());
            clusters[id].pixelCount = entry.getValue();
            clusters[id].reds *= entry.getValue();
            clusters[id].greens *= entry.getValue();
            clusters[id].blues *= entry.getValue();
            id++;
        }
        return clusters;
    }

    private int getMainClustersNum(ArrayList<Entry<Integer, Integer>> sortedColors, int k){
        /* returns number of global(>sum/GLOBAL_PART pixels) colors, but less than k*/
        int sum = 0, counter = 0;
        for (Entry<Integer,Integer> entry: sortedColors)
            sum += entry.getValue();
        for (Entry<Integer,Integer> entry: sortedColors){
            if ((counter == k) || ((double)entry.getValue()/sum < GLOBAL_PART))
                break;
            sum -= entry.getValue();
            counter++;
        }
        return counter;
    }

    private ArrayList<Entry<Integer, Integer>> getDiagColors(ArrayList<Entry<Integer, Integer>> sortedColors, int k){
        int step = sortedColors.size()/k;

        ArrayList<Entry<Integer, Integer>> result = new ArrayList<>();
        for (int i = 0; i < k; ++i)
            result.add(sortedColors.get(i*step));

        return result;
    }

    public Cluster[] getSmartKClusters(BufferedImage image, int k) {
        HashMap<Integer,Integer> colors = PicUtil.parseColorsFromPicture(image);
        if (colors.size() > k) {
            //remove with border
            int size = image.getHeight() * image.getWidth();
            LinkedList<Integer> toRemove = new LinkedList<>();
            for (Entry<Integer,Integer> entry: colors.entrySet()){
                if ( (border > 0) && (colors.size()-toRemove.size() > k) && (((double)entry.getValue())/size < border) )
                    toRemove.push(entry.getKey());
            }
            for (Integer key: toRemove)
                colors.remove(key);

            if (colors.size() > TOO_MANY_COLORS){
                //if still a lot of (>5k colors) let get random colors
                return randomKClustersFromCollection(new ArrayList<Entry<Integer, Integer>>(colors.entrySet()), k);
            }else {
                ArrayList<Entry<Integer, Integer>> sortedColors = new ArrayList<>(PicUtil.getSortedColors(colors));
                int mainClustersNum = getMainClustersNum(sortedColors, k);
                ArrayList<Entry<Integer,Integer>> topColors = new ArrayList<>(sortedColors.subList(0, mainClustersNum));
                if (mainClustersNum < k)
                    topColors.addAll(
                            getDiagColors(
                                    new ArrayList<Entry<Integer,Integer>>(
                                            sortedColors.subList(mainClustersNum, sortedColors.size())
                                    ),
                                    k
                            )
                    );
                return clustersFromColorCollection(topColors);
            }
        }
        return clustersFromColorCollection(new ArrayList<Entry<Integer, Integer>>(colors.entrySet()));
    }

    public BufferedImage calculate(BufferedImage image, int k) {
        long start = System.currentTimeMillis();
        int w = image.getWidth();
        int h = image.getHeight();
        clusters = getSmartKClusters(image, k);
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
                    int pixel = image.getRGB(x, y);
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
                        clusters[clusterId].addPixel(image.getRGB(x, y));
                    }
                }
            }

        }
        // create result image
        BufferedImage result = new BufferedImage(w, h,
                BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int clusterId = lut[w * y + x];
                result.setRGB(x, y, clusters[clusterId].getRGB());
                int currentColor = clusters[clusterId].getRGB();
                if (colors.containsKey(currentColor)) {
                    colors.put(currentColor, colors.get(currentColor) + 1);
                } else {
                    colors.put(currentColor, 1);
                }
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("Clustered to " + k
                + " clusters in " + loops
                + " loops in " + (end - start) + " ms.");
        return result;
    }


    /*
     * some functions, that helps to calculate something for KMeans
     */

    public Cluster findClosestCluster(int rgb) {
        Cluster closest_cluster = null;
        int min = Integer.MAX_VALUE;
        for (Cluster cluster : clusters){
            int distance = cluster.distance(rgb);
            if (distance < min) {
                min = distance;
                closest_cluster = cluster;
            }
        }
        return closest_cluster;
    }


    /*
     * static methods that returns KMeans result
     * TODO: create KMeansResult class and return its object as a result
     */
    public static Collection<Map.Entry<Color, Integer>> calculateAndGetColors(final String filename,
                                                                              int clusterNumber,
                                                                              KMeansMode mode,
                                                                              double border) {
        assert clusterNumber > 0 && clusterNumber <= MAX_CLUSTERS_NUM;
        KMeans kMeans = new KMeans(mode, border);
        colors.clear();
        kMeans.calculate(PicUtil.loadImage(filename), clusterNumber);
        return PicUtil.castToColorArray(PicUtil.getSortedColors(kMeans.colors));
    }


}
