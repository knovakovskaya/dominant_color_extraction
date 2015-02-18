package ru.ifmo.thesis.algo;

import java.awt.image.BufferedImage;
import java.util.*;

public class ClusterUtil {

    public static void mergeClusters(int minclusternum,int border){}

    private static Cluster[] clustersFromColorCollection(Collection<Map.Entry<Integer, Integer>> colors){
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

    private static int getMainClustersNum(Collection<Map.Entry<Integer, Integer>> sortedColors, int k, CommonSettings cs){
        int sum = 0, counter = 0;
        for (Map.Entry<Integer,Integer> entry: sortedColors)
            sum += entry.getValue();
        for (Map.Entry<Integer,Integer> entry: sortedColors){
            if ((counter == k) || ((double)entry.getValue()/sum < cs.globalPart))
                break;
            sum -= entry.getValue();
            counter++;
        }
        return counter;
    }

    private static ArrayList<Map.Entry<Integer, Integer>> getDiagColors(ArrayList<Map.Entry<Integer, Integer>> sortedColors, int k){
        int step = sortedColors.size()/k;

        ArrayList<Map.Entry<Integer, Integer>> result = new ArrayList<>();
        for (int i = 0; i < k; ++i)
            result.add(sortedColors.get(i*step));

        return result;
    }

    public static Cluster[] getSmartClusters(BufferedImage image, CommonSettings cs) {
        HashMap<Integer,Integer> colors = PicUtil.parseColorsFromPicture(image);
        if (colors.size() > cs.clustersNum) {
            //remove with border
            int size = image.getHeight() * image.getWidth();
            LinkedList<Integer> toRemove = new LinkedList<>();
            for (Map.Entry<Integer,Integer> entry: colors.entrySet()){
                if ( (cs.border > 0) && (colors.size()-toRemove.size() > cs.clustersNum) && (((double)entry.getValue())/size < cs.border) )
                    toRemove.push(entry.getKey());
            }
            for (Integer key: toRemove)
                colors.remove(key);

            if (colors.size() > cs.GSC_COLOR_LIMIT){
                //if still a lot of (>5k colors) let get random colors
                return GetRandomClustersFromCollection(new ArrayList<Map.Entry<Integer, Integer>>(colors.entrySet()), cs);
            }else {
                ArrayList<Map.Entry<Integer, Integer>> sortedColors = new ArrayList<>(PicUtil.getSortedColors(colors));
                int mainClustersNum = getMainClustersNum(sortedColors, cs.clustersNum, cs);
                ArrayList<Map.Entry<Integer,Integer>> topColors = new ArrayList<>(sortedColors.subList(0, mainClustersNum));
                if (mainClustersNum < cs.clustersNum)
                    topColors.addAll(
                            getDiagColors(
                                    new ArrayList<Map.Entry<Integer,Integer>>(
                                            sortedColors.subList(mainClustersNum, sortedColors.size())
                                    ),
                                    cs.clustersNum
                            )
                    );
                return clustersFromColorCollection(topColors);
            }
        }
        return clustersFromColorCollection(new ArrayList<Map.Entry<Integer, Integer>>(colors.entrySet()));
    }

    public static Cluster[] getStartClusters(BufferedImage bi, CommonSettings cs){
        switch (cs.startPoint){
            case DIAG:
                return ClusterUtil.getClustersOnDiagonal(bi, cs);
            case SMART_TOP_RANDOM:
                return getSmartClusters(bi, cs);
            case RANDOM:
            default:
                HashMap<Integer,Integer> colors = PicUtil.parseColorsFromPicture(bi);
                return ClusterUtil.GetRandomClustersFromCollection(new ArrayList<Map.Entry<Integer, Integer>>(colors.entrySet()), cs);
        }
    }

}
