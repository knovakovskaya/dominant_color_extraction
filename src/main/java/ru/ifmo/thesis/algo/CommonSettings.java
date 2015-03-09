package ru.ifmo.thesis.algo;

public class CommonSettings{
    public enum StartPointsAlgo {
        DIAG,  //points from images diagonal
        RANDOM, //random color from picture(random from color collection)
        SMART_TOP_RANDOM, //cut unpopular colors, sort by dist and get from diag
        PLUS_PLUS, //plus_plus algo
        SMART_RANKS_COLORS, //rank system to check distance
    }

    public int clustersNum;

    public double border;
    public double globalPart;
    public StartPointsAlgo startPoint;

    public final static double MAX_CLUSTERS_NUM = 50;
    public final static double GSC_COLOR_LIMIT = 5000;

    public CommonSettings(int cnum,
                          double b,
                          double gp,
                          StartPointsAlgo sp){
        clustersNum = cnum;
        border = b;
        globalPart = gp;
        startPoint = sp;
    }

    public CommonSettings(CommonSettings cs){
        copy(cs);
    }

    public void copy(CommonSettings cs){
        clustersNum = cs.clustersNum;
        border = cs.border;
        globalPart = cs.globalPart;
        startPoint = cs.startPoint;
    }
}
