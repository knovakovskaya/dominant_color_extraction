package ru.ifmo.thesis.algo;

import java.awt.image.BufferedImage;
import java.util.*;

public class ClusterUtil {

    public static void mergeClusters(int minclusternum,int border){}

    private static Cluster[] clustersFromColorCollection(Collection<Map.Entry<Integer, Integer>> colors){
        Cluster[] clusters = new Cluster[colors.size()];
        int id = 0;
        for (Map.Entry<Integer,Integer> entry: colors){
            clusters[id] = new Cluster(id, entry.getKey(), entry.getValue());
            id++;
        }
        return clusters;
    }

    public static Cluster[] getClustersOnDiagonal(BufferedImage image, CommonSettings cs) {
        Cluster[] result = new Cluster[cs.clustersNum];
        int dx = image.getWidth() / cs.clustersNum;
        int dy = image.getHeight() / cs.clustersNum;
        for (int i = 0, x = 0, y = 0; i < cs.clustersNum; i++) {
            result[i] = new Cluster(i, image.getRGB(x, y));
            x += dx;
            y += dy;
        }
        return result;
    }

    private static Cluster[] GetRandomClustersFromCollection(Collection<Map.Entry<Integer, Integer>> colors, CommonSettings cs) {
        ArrayList<Map.Entry<Integer,Integer>> colorsList = new ArrayList<>(colors);
        Collections.shuffle(colorsList);
        return clustersFromColorCollection(colorsList.subList(0, cs.clustersNum));
    }

    private static int findClosestClusterInRange(int rgb, Cluster clusters[], int left, int right) {
        int min = Integer.MAX_VALUE;
        for (int i = left; i < right; ++i) {
            int distance = clusters[i].distance(rgb);
            if (distance < min) {
                min = distance;
            }
        }
        return min;
    }

    public static Cluster[] getPlusPlusClusters(BufferedImage image, CommonSettings cs) {
        ArrayList<Map.Entry<Integer, Integer>> colors = new ArrayList<Map.Entry<Integer, Integer>>(
                                                            PicUtil.parseColorsFromPicture(image).entrySet());
        if (cs.clustersNum < colors.size()){
            Cluster startClusters[] = new Cluster[cs.clustersNum];
            int rand = Math.abs(new Random(System.currentTimeMillis()).nextInt()) % colors.size();
            startClusters[0] = new Cluster(0, colors.get(rand).getKey(), colors.get(rand).getValue());
            int distance[] = new int[colors.size()];

            for (int i = 1; i < cs.clustersNum; ++i){
                long sumDx2 = 0;
                for (int j = 0; j < distance.length; ++j){
                    distance[j] = findClosestClusterInRange(colors.get(j).getKey(), startClusters, 0, i);
                    sumDx2 += distance[j];
                }
                for (int j = 0; j < distance.length; ++j){
                    sumDx2 -= distance[j];
                    if (sumDx2 > 0){
                        continue;
                    }
                    startClusters[i] = new Cluster(i, colors.get(j).getKey(), colors.get(j).getValue());
                }
            }

            return  startClusters;
        }
        return clustersFromColorCollection(colors);
    }

    public static Cluster[] getStartClusters(BufferedImage bi, CommonSettings cs){
        switch (cs.startPoint){
            case DIAG:
                return ClusterUtil.getClustersOnDiagonal(bi, cs);
            case PLUS_PLUS:
                return getPlusPlusClusters(bi, cs);
            case RANDOM:
            default:
                HashMap<Integer,Integer> colors = PicUtil.parseColorsFromPicture(bi);
                return ClusterUtil.GetRandomClustersFromCollection(new ArrayList<Map.Entry<Integer, Integer>>(colors.entrySet()), cs);
        }
    }

}
