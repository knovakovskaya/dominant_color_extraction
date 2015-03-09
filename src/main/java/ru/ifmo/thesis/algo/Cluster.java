package ru.ifmo.thesis.algo;


class Cluster {
    int id;
    int pixelCount;
    int reds;
    int greens;
    int blues;

    public Cluster(int id) {
        this.id = id;
    }

    public Cluster(int id, int rgb) {
        this.id = id;
        addPixel(rgb);
    }

    public void clear() {
        reds = 0;
        greens = 0;
        blues = 0;
        pixelCount = 0;
    }

    int getId() {
        return id;
    }

    int getRGB() {
        int r,g,b;
        r=g=b=0;
        if (pixelCount != 0) {
            r = reds / pixelCount;
            g = greens / pixelCount;
            b = blues / pixelCount;
        }
        return 0xff000000 | r << 16 | g << 8 | b;
    }

    void addPixel(int rgb) {
        reds += PicUtil.getRed(rgb);
        greens += PicUtil.getGreen(rgb);
        blues += PicUtil.getBlue(rgb);
        pixelCount++;
    }

    void removePixel(int rgb) {
        reds -= PicUtil.getRed(rgb);
        greens -= PicUtil.getGreen(rgb);
        blues -= PicUtil.getBlue(rgb);
        pixelCount--;
    }

    int distance(int rgb) {
        int d = Integer.MAX_VALUE;
        if (pixelCount != 0) {
                        int rx = Math.abs(reds / pixelCount - PicUtil.getRed(rgb));
                        int gx = Math.abs(greens / pixelCount - PicUtil.getGreen(rgb));
                        int bx = Math.abs(blues / pixelCount - PicUtil.getBlue(rgb));
                        d = (rx + gx + bx) / 3;
            //d = PicUtil.Distance(PicUtil.toRGB(reds/pixelCount, greens/pixelCount, blues/pixelCount), rgb);
        }
        return d;
    }
}