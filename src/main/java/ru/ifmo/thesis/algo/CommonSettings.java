package ru.ifmo.thesis.algo;

public class CommonSettings{
    public enum StartPointsAlgo {
        DIAG,  //points from images diagonal
        RANDOM, //random color from picture(random from color collection)
        PLUS_PLUS, //plus_plus algo
    }

    public int clustersNum;

    public double border;
    public double epsilon;
    public StartPointsAlgo startPoint;

    public final static double MAX_CLUSTERS_NUM = 50;

    public CommonSettings(int cnum,
                          double b,
                          double eps,
                          StartPointsAlgo sp){
        clustersNum = cnum;
        border = b;
        epsilon = eps;
        startPoint = sp;
    }

    public CommonSettings(CommonSettings cs){
        copy(cs);
    }

    public void copy(CommonSettings cs){
        clustersNum = cs.clustersNum;
        border = cs.border;
        epsilon = cs.epsilon;
        startPoint = cs.startPoint;
    }
}
