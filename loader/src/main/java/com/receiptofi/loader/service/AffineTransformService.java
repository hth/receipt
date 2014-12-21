package com.receiptofi.loader.service;

import org.springframework.stereotype.Service;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 * User: hitender
 * Date: 12/21/14 5:13 AM
 */
@Service
public class AffineTransformService {

    public void affineTransform(BufferedImage src, BufferedImage dst, AffineTransform t) {
        AffineTransformOp op = new AffineTransformOp(t, null);
        op.filter(src, dst);
    }
}
