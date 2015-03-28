package ru.ifmo.thesis.algo;


import java.awt.image.BufferedImage;
import java.util.*;

public class SimpleLinkage extends AClusteringAlgorithms {

    protected int distanceType;

    public SimpleLinkage(String filename, CommonSettings cs){
        super(filename, cs);
        distanceType = 2;
    }

    private int[] rangeX(int X){
        int[] arr = new int[X];
        for (int i = 0; i < X; ++i){
            arr[i] = i;
        }
        return arr;
    }

    private ArrayList<Integer> getPoints(){
        ArrayList<Integer> points = new ArrayList<>();
        for (int i = 0; i< origin.getHeight()*origin.getWidth(); ++i){
            points.add(i);
        }
        Collections.shuffle(points);
        return points;
    }

    private int[] getDistance(ArrayList<Integer> points){
        int w = origin.getWidth(), h = origin.getHeight();
        int n = points.size();
        int[] distance = new int[n*n];

        for (int i = 0; i < n; ++i){
            for (int j = 0; j < n; ++j){
                int pos1 = points.get(i);
                int pos2 = points.get(j);
                distance[j+i*n] =  PicUtil.getDistance(origin.getRGB(pos1%w, pos1/w), origin.getRGB(pos2%w, pos2/w));
            }
        }

        return distance;
    }

    private class Pair{
        public int a, b;
        public Pair(int a_, int b_){ a = a_; b = b_; }
    }

    private int[] algSort(int toSort[]){
        ArrayList<Pair> pairs = new ArrayList<>();
        int[] sorted_idx = new int[toSort.length];
        for (int i=0; i < toSort.length; ++i){
            pairs.add(new Pair(i, toSort[i]));
        }
        Collections.sort(pairs, new Comparator<Pair>() {
            public int compare(Pair p1, Pair p2) {
                return p1.b < p2.b ? -1 : p1.b == p2.b ? 0 : 1;
            }
        });
        for (int i=0; i < pairs.size(); ++i){
            sorted_idx[i] = pairs.get(i).a;
        }
         
        return sorted_idx;
    }

    private  int[] getLutAndCalculateClusters(int[][] Z, int n, int k, ArrayList<Integer> points){
        int w = origin.getWidth();
        int[] lut = new int[n];
        LinkedList<Integer>[] clustersLists = new LinkedList[n];
        for (int i = 0; i < n; ++i){
            clustersLists[i] = new LinkedList<>();
            clustersLists[i].add(i);
        }
        int[] clusters_ids = rangeX(n);

        for (int i = 0; n-i > k; ++i){
            int ind1 = Z[i][0]%n;
            int ind2 = Z[i][1]%n;

            clustersLists[ind1].addAll(clustersLists[ind2]);
            for (Integer p: clustersLists[ind1]){
                clustersLists[p] = clustersLists[ind1];
                clusters_ids[p] = clusters_ids[ind1];
            }
        }

        TreeSet<Integer> processedClusters = new TreeSet<Integer>();
        clusters = new Cluster[k];
        for (int i = 0, kk = 0; i < n; ++i){
            if (!processedClusters.contains(clusters_ids[i])){
                processedClusters.add(clusters_ids[i]);
                clusters[kk] = new Cluster(kk);
                for (Integer p: clustersLists[i]){
                    lut[points.get(i)] = kk;
                    int x = points.get(i)%w;
                    int y = points.get(i)/w;
                    clusters[kk].addPixel(origin.getRGB(x,y));
                }
            }
        }

        return lut;
    }

    private int[] fromPointerRepresentation(int[] L, int[] P, ArrayList<Integer> points){
        int n = L.length;
        int[] sorted_idx = algSort(L);
        int[] node_ids = rangeX(n);
        int[][] Z =  new int[n - 1][3];

        for (int i = 0; i < n-1; ++i){
            int cl = sorted_idx[i];
            int pi = P[cl];

            if (node_ids[cl] < node_ids[pi]){
                Z[i][0] = node_ids[cl];
                Z[i][1] = node_ids[pi];
            }else{
                Z[i][0] = node_ids[pi];
                Z[i][1] = node_ids[cl];
            }
            Z[i][2] = L[cl];
            node_ids[pi] = n+i;
        }

        return getLutAndCalculateClusters(Z, n, cSettings.clustersNum, points);
    }

    @Override
    public BufferedImage calculateClusters() {
        int loops = 0, tmp;
        int h = origin.getHeight(), w = origin.getWidth();
        int n = h*w;
        ArrayList<Integer> points = getPoints();
        int[] distance = getDistance(points);

        int[] M = new int[n];
        int[] L = new int[n];
        int[] P = new int[n];

        for (int i = 0; i < n; ++i){
            P[i] = i;
            L[i] = Integer.MAX_VALUE;

            for (int j = 0; j < n; ++j){
                M[j] = distance[i+j*n];
            }

            for (int j = 0; j < n; ++j){
                if (L[j] >= M[j]){
                    M[P[j]] = Math.min(M[P[j]], M[j]);
                    L[j] = M[j];
                    P[j] = i;
                }else{
                    M[P[j]] = Math.min(M[P[j]], M[j]);
                }
            }

            for (int j = 0; j < n; ++j){
                if (L[j] >= L[P[j]])
                    P[j] = i;
            }
        }

        int[] lut = fromPointerRepresentation(L,P, points);
        createClusterizedImage(lut);


        System.out.println("Single-Linkage algorithm:\n"
                + "  in " + loops + " loops");
        return clustorized;
    }

}
