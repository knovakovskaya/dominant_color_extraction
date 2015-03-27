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

    public Cluster(int id, int rgb, int size) {
        this.id = id;
        addPixel(rgb);
        pixelCount = size;
        reds *= pixelCount;
        greens *= pixelCount;
        blues *= pixelCount;
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
        if (pixelCount != 0) {
            int rx = reds / pixelCount - PicUtil.getRed(rgb);
            int gx = greens / pixelCount - PicUtil.getGreen(rgb);
            int bx = blues / pixelCount - PicUtil.getBlue(rgb);
            return rx*rx + gx*gx + bx*bx;
        }
        return Integer.MAX_VALUE;
    }
}