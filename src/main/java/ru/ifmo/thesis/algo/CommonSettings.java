package ru.ifmo.thesis.algo;

public class CommonSettings{
    public enum StartPointsAlgo {
        DIAG,  //points from images diagonal
        RANDOM, //random color from picture(random from color collection)
        PLUS_PLUS, //plus_plus algo
        MAX_DIST, //clusters with max dist between each other
    }

    public enum MergeType{
        OR,
        AND,
        DISABLED
    }

    public int clustersNum;

    public double border;
    public int minDist;
    public MergeType mergeType;
    public StartPointsAlgo startPoint;

    public final static double MAX_CLUSTERS_NUM = 50;

    public CommonSettings(int cnum,
                          double b,
                          int md,
                          MergeType mt,
                          StartPointsAlgo sp){
        clustersNum = cnum;
        border = b;
        minDist = md;
        mergeType = mt;
        startPoint = sp;
    }

    public CommonSettings(CommonSettings cs){
        copy(cs);
    }

    public void copy(CommonSettings cs){
        clustersNum = cs.clustersNum;
        border = cs.border;
        minDist = cs.minDist;
        mergeType = cs.mergeType;
        startPoint = cs.startPoint;
    }
}
