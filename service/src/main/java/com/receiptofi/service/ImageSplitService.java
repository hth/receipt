package com.receiptofi.service;

import com.receiptofi.utils.FileUtil;

import org.apache.commons.io.FilenameUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

/**
 * User: hitender
 * Date: 10/18/13 10:58 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public final class ImageSplitService {
    private static final Logger LOG = LoggerFactory.getLogger(ImageSplitService.class);
    public static final String PNG_FORMAT = "png";

    /**
     * Decrease the resolution of the receipt image with PNG file format for better resolution.
     *
     * @param file
     * @return
     * @throws IOException
     */
    public File decreaseResolution(File file) throws IOException {
        BufferedImage image = bufferedImage(file);

        LOG.debug("W={} H={}", image.getWidth(), image.getHeight());
        double aspectRatio = (double) image.getWidth(null) / (double) image.getHeight(null);

        BufferedImage bufferedImage = resizeImage(image, 750, (int) (750 / aspectRatio));
        File scaledFile = FileUtil.createTempFile(FilenameUtils.getBaseName(file.getName()) + "_Scaled", FilenameUtils.getExtension(file.getName()));
        ImageIO.write(bufferedImage, PNG_FORMAT, scaledFile);
        return scaledFile;
    }

    /**
     * Decrease the resolution of the receipt image with PNG file format for better resolution.
     *
     * @return
     * @throws IOException
     */
    public void decreaseResolution(InputStream inputStream, OutputStream outputStream) throws IOException {
        BufferedImage image = bufferedImage(inputStream);

        LOG.debug("W={} H={}", image.getWidth(), image.getHeight());
        double aspectRatio = (double) image.getWidth(null) / (double) image.getHeight(null);

        BufferedImage bufferedImage = resizeImage(image, 750, (int) (750 / aspectRatio));
        ImageIO.write(bufferedImage, PNG_FORMAT, outputStream);
    }

    /**
     * Can be used for calculating height and width of an image.
     *
     * @param file
     * @return
     * @throws IOException
     */
    public BufferedImage bufferedImage(File file) throws IOException {
        return bufferedImage(new FileInputStream(file));
    }

    public BufferedImage bufferedImage(InputStream is) throws IOException {
        return ImageIO.read(is);
    }

    /**
     * This function resize the image file and returns the BufferedImage object that can be saved to file system.
     *
     * @param image
     * @param width
     * @param height
     * @return
     */
    private static BufferedImage resizeImage(final Image image, int width, int height) {
        final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setComposite(AlphaComposite.Src);
        //below three lines are for RenderingHints for better image quality at cost of higher processing time
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.drawImage(image, 0, 0, width, height, null);
        graphics2D.dispose();
        return bufferedImage;
    }
}
