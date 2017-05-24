package de.tinf15b4.kino.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageLoader {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public byte[] getImage(String url, String pictureFormat) {
        try {
            return getImage(new URL(url), pictureFormat);
        } catch (IOException e) {
            logger.warn(String.format("Unable to convert the given String [%s] into an URL", url), e);
            return new byte[0];
        }
    }

    public byte[] getImage(URL url, String pictureFormat) {
        try {
            byte[] imageInByte;
            BufferedImage originalImage = ImageIO.read(url);

            // convert BufferedImage to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(originalImage, pictureFormat, baos);
            baos.flush();
            imageInByte = baos.toByteArray();
            baos.close();

            return imageInByte;
        } catch (IOException e) {
            logger.warn(String.format("Unable to load Picture from given URL [%s]", url), e);
            return new byte[0];
        }
    }
}
