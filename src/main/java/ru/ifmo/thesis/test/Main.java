package ru.ifmo.thesis.test;

import ru.ifmo.thesis.algo.KMeans;
import ru.ifmo.thesis.algo.CommonSettings;
import ru.ifmo.thesis.algo.PicUtil;

import java.awt.image.BufferedImage;

public class Main {

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
        KMeans.KMeansMode mode = KMeans.KMeansMode.CONTINUOUS;
        if (m.equals("-i")) {
            mode = KMeans.KMeansMode.ITERATIVE;
        } else if (m.equals("-c")) {
            mode = KMeans.KMeansMode.CONTINUOUS;
        }

        //calculate
        CommonSettings cs = new CommonSettings(k, 0.001, 0.05, CommonSettings.StartPointsAlgo.SMART_TOP_RANDOM);
        KMeans kmeans = new KMeans(src, cs, mode);
        BufferedImage dstImage = kmeans.calculate();
        PicUtil.saveImage(dst, dstImage);
    }
}
