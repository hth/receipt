package com.tholix.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.awt.*;

/**
 * User: hitender
 * Date: 10/18/13 10:58 PM
 */
public class ImageSplit {
    public static void main(String[] args) throws IOException {

        File file = new File("/Users/hitender/Downloads/" + "20130429_171952.jpg"); // I have bear.jpg in my working directory
        File image = decreaseResolution(file);
        splitImage(image);
    }

    public static void splitImage(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        BufferedImage image = ImageIO.read(fis); //reading the image file
        System.out.println("W: " + image.getWidth() + ", " + "H: " + image.getHeight());

        splitImage(image);
    }

    public static void splitImage(BufferedImage image) throws IOException {
        int rows = 4; //You should decide the values for rows and cols variables
        int cols = 4;
        int chunks = rows * cols;

        int chunkWidth = image.getWidth() / cols; // determines the chunk width and height
        int chunkHeight = image.getHeight() / rows;
        int count = 0;
        BufferedImage imgs[] = new BufferedImage[chunks]; //Image array to hold image chunks
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                //Initialize the image array with image chunks
                imgs[count] = new BufferedImage(chunkWidth, chunkHeight, image.getType());

                // draws the image chunk
                Graphics2D gr = imgs[count++].createGraphics();
                gr.drawImage(image, 0, 0, chunkWidth, chunkHeight, chunkWidth * y, chunkHeight * x, chunkWidth * y + chunkWidth, chunkHeight * x + chunkHeight, null);
                gr.dispose();
            }
        }
        System.out.println("Splitting done");

        //writing mini images into image files
        for (int i = 0; i < imgs.length; i++) {
            ImageIO.write(imgs[i], "png", File.createTempFile("receiptofi-img-" + i + "-", ".png"));
        }
        System.out.println("Mini images created");
    }

    public static File decreaseResolution(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        BufferedImage image = ImageIO.read(fis); //reading the image file

        System.out.println("W: " + image.getWidth() + ", " + "H: " + image.getHeight());
        double aspectRatio = (double) image.getWidth(null)/(double) image.getHeight(null);

        BufferedImage tempPNG = resizeImage(image, 750, (int) (750/aspectRatio));
        File newFilePNG = File.createTempFile(file.getName() + "-receiptofi-image-", ".png");
        ImageIO.write(tempPNG, "png", newFilePNG);
        return newFilePNG;
    }


    /**
     * This function resize the image file and returns the BufferedImage object that can be saved to file system.
     */
    public static BufferedImage resizeImage(final Image image, int width, int height) {
        final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setComposite(AlphaComposite.Src);
        //below three lines are for RenderingHints for better image quality at cost of higher processing time
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.drawImage(image, 0, 0, width, height, null);
        graphics2D.dispose();
        return bufferedImage;
    }
}
