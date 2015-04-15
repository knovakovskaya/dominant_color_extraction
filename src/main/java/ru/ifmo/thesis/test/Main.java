package ru.ifmo.thesis.test;

import ru.ifmo.thesis.algo.KMeans;
import ru.ifmo.thesis.algo.CommonSettings;
import ru.ifmo.thesis.algo.PicUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        args = new String[] {"~/tmp/dominant_color_extraction/pics/sl5.jpg", "~/tmp/dominant_color_extraction/pics/sl5_new.jpg", "3", "-c"};

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
        KMeans.KMeansMode mode = KMeans.KMeansMode.CONTINUOUS;
        if (m.equals("-i")) {
            mode = KMeans.KMeansMode.ITERATIVE;
        } else if (m.equals("-c")) {
            mode = KMeans.KMeansMode.CONTINUOUS;
        }

        //calculate
        CommonSettings cs = new CommonSettings(4,
                                               0.001, //
                                               10, //
                                               CommonSettings.MergeType.DISABLED,
                                               CommonSettings.StartPointsAlgo.MAX_DIST);
        KMeans kmeans = new KMeans(src, cs, mode);
        ArrayList<Map.Entry<Color, Integer>> colors = new ArrayList<>(kmeans.calculateAndGetColors());
        PicUtil.saveImage(dst, kmeans.getClustorizedImage());
    }
}
