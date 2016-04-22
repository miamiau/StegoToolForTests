/**
 *
 */
package main.java.ssim;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

/**
 * @author teodora.cosma
 *
 */
public class SsimHelper {

    /**
     * @param coverImage
     * @param stegoImage
     * @return
     * @throws SsimException
     */
    public static String calculateSsim(BufferedImage coverImage, BufferedImage stegoImage) throws SsimException {
        
        double ssimIndex = 0.0;
        
        if (coverImage.getHeight() != stegoImage.getHeight()) {
            throw new SsimException("The images must be the same height!");
        }
        
        if (coverImage.getWidth() != stegoImage.getWidth()) {
            throw new SsimException("The images must be the same width!");
        }
        
        int pixelSize = coverImage.getColorModel().getPixelSize();
        if (pixelSize != stegoImage.getColorModel().getPixelSize()) {
            throw new SsimException("The images must have the same number of bits per pixel!");
        }
        
        // if (coverImage.getColorModel().getPixelSize() == 24) {
        // throw new SsimException("RGB images are not supported!");
        // }
        
        String title_1, title_2;
        int pointer, filter_length, image_height, image_width, image_dimension, bits_per_pixel_1, bits_per_pixel_2, a,
                b, c;
        float filter_weights[];
        double[] ssim_map;
        double ssim_index;
        
        double sigma_gauss = 1.5;
        int filter_width = 11;
        int filter_scale = 20;
        double K1 = 0.01;
        double K2 = 0.03;
        double downsampled = coverImage.getHeight() / 256;
        double downsampled_backup = downsampled;
        boolean gaussian_window = true;
        String[] window_type = { "Gaussian", "Same weight" };
        
        String window_selection = window_type[0];
        boolean out = false;
        boolean show_downsampled_images = false;
        boolean show_gaussian_filter = false;
        boolean show_ssim_map = false;
        
        while (!out) {
            out = true;
            GenericDialog gd = new GenericDialog("Filter parameters");
            gd.addNumericField("Standard deviation:", sigma_gauss, 1);
            gd.addChoice("Window type:", window_type, window_selection);
            gd.addNumericField("Filter width:", filter_width, 0);
            gd.addNumericField("K1:", K1, 2);
            gd.addNumericField("K2:", K2, 2);
            gd.addNumericField("View scale (downsampled by):", downsampled, 0);
            gd.addNumericField("Filter scale (for viewing gaussian filter):", filter_scale, 0);
            gd.addCheckbox("Show downsampled images", show_downsampled_images);
            gd.addCheckbox("Show SSIM map", show_ssim_map);
            gd.addCheckbox("Show gaussian filter", show_gaussian_filter);
            gd.showDialog();
            if (!gd.wasCanceled()) {
                sigma_gauss = gd.getNextNumber();
                window_selection = gd.getNextChoice();
                filter_width = (int) (gd.getNextNumber());
                K1 = gd.getNextNumber();
                K2 = gd.getNextNumber();
                downsampled = (int) gd.getNextNumber();
                filter_scale = (int) (gd.getNextNumber());
                show_downsampled_images = gd.getNextBoolean();
                show_ssim_map = gd.getNextBoolean();
                show_gaussian_filter = gd.getNextBoolean();
                double d;
                a = filter_width / 2;
                d = filter_width - (a * 2);
                if (window_selection != "Gaussian") {
                    gaussian_window = false;
                }
                if (d == 0) {
                    IJ.error("Filter width and heigth must be odd");
                    out = false;
                }
                if (gaussian_window & (sigma_gauss <= 0)) {
                    IJ.error("Sigma must be greater than 0");
                    out = false;
                }
                if (gaussian_window & (filter_scale <= 0)) {
                    IJ.error("Filter scale must be greater than 0");
                    out = false;
                }
                if (downsampled > downsampled_backup) {
                    IJ.error("Miminum height must be 256 pixels (review Viewing scale)");
                    out = false;
                }
                if (downsampled < 1) {
                    IJ.error("Minimun value of Viewing scale must be 1");
                    out = false;
                }
                gd.dispose();
            }
        }
        
        double C1 = (Math.pow(2, pixelSize) - 1) * K1;
        C1 = C1 * C1;
        double C2 = (Math.pow(2, pixelSize) - 1) * K2;
        C2 = C2 * C2;
        
        //
        // NOW, WE CREATE THE FILTER, GAUSSIAN OR MEDIA FILTER, ACCORDING TO THE
        // VALUE OF boolean "gaussian_window"
        //
        
        filter_length = filter_width * filter_width;
        float window_weights[] = new float[filter_length];
        double[] array_gauss_window = new double[filter_length];
        
        if (gaussian_window) {
            
            double value, distance = 0;
            int center = (filter_width / 2);
            double total = 0;
            double sigma_sq = sigma_gauss * sigma_gauss;
            
            for (int y = 0; y < filter_width; y++) {
                for (int x = 0; x < filter_width; x++) {
                    distance = (Math.abs(x - center) * Math.abs(x - center))
                            + (Math.abs(y - center) * Math.abs(y - center));
                    pointer = (y * filter_width) + x;
                    array_gauss_window[pointer] = Math.exp((-0.5 * distance) / sigma_sq);
                    total = total + array_gauss_window[pointer];
                }
            }
            for (pointer = 0; pointer < filter_length; pointer++) {
                array_gauss_window[pointer] = array_gauss_window[pointer] / total;
                window_weights[pointer] = (float) array_gauss_window[pointer];
            }
        } else { // NO WEIGHTS. ALL THE PIXELS IN THE EVALUATION WINDOW HAVE THE
                 // SAME WEIGHT
            for (pointer = 0; pointer < filter_length; pointer++) {
                array_gauss_window[pointer] = 1.0 / filter_length;
                window_weights[pointer] = (float) array_gauss_window[pointer];
            }
        }
        if (show_gaussian_filter) { // IN CASE OF A GAUSSIAN FILTER, YOU CAN
                                    // SHOW IT IF YOU WANT
            ColorModel cm = null;
            ImageProcessor gauss_window_ip = new FloatProcessor(filter_width, filter_width, window_weights, cm);
            gauss_window_ip = gauss_window_ip.resize(filter_width * filter_scale);
            String title_filtro_1 = "Sigma: " + sigma_gauss + " Width: " + filter_width + " píxeles";
            ImagePlus gauss_window_imp = new ImagePlus(title_filtro_1, gauss_window_ip);
            gauss_window_imp.show();
            gauss_window_imp.updateAndDraw();
        }
        //
        // END OF FILTER SELECTION
        //
        //
        // MAIN ALGORITHM
        //
        int coverImageWidth = coverImage.getWidth();
        int coverImageHeight = coverImage.getHeight();
        int stegoImageWidth = stegoImage.getWidth();
        int stegoImageHeight = stegoImage.getHeight();
        int imageDimension = coverImageHeight * coverImageWidth;
        ImagePlus coverImagePlus = new ImagePlus("Cover Image", coverImage);
        ImagePlus stegoImagePlus = new ImagePlus("Stego Image", stegoImage);
        ImageProcessor image_1_original_p = coverImagePlus.getProcessor();
        ImageProcessor image_2_original_p = stegoImagePlus.getProcessor();
        
        image_width = image_1_original_p.getWidth();
        image_width = (int) (image_width / downsampled);
        image_1_original_p.setInterpolate(true);
        image_2_original_p.setInterpolate(true);
        ImageProcessor image_1_p = image_1_original_p.resize(image_width);
        ImageProcessor image_2_p = image_2_original_p.resize(image_width);
        
        image_height = image_1_p.getHeight();
        image_width = image_1_p.getWidth();
        imageDimension = image_width * image_height;
        
        ImageProcessor mu1_ip = new FloatProcessor(image_width, image_height);
        ImageProcessor mu2_ip = new FloatProcessor(image_width, image_height);
        float[] array_mu1_ip = (float[]) mu1_ip.getPixels();
        float[] array_mu2_ip = (float[]) mu2_ip.getPixels();
        
        float[] array_mu1_ip_copy = new float[imageDimension];
        float[] array_mu2_ip_copy = new float[imageDimension];
        
        bits_per_pixel_1 = pixelSize;
        a = b = 0;
        for (pointer = 0; pointer < imageDimension; pointer++) {
            
            if (bits_per_pixel_1 == 8) {
                a = (0xff & image_1_p.get(pointer));
                b = (0xff & image_2_p.get(pointer));
            }
            if (bits_per_pixel_1 == 16) {
                a = (0xffff & image_1_p.get(pointer));
                b = (0xffff & image_2_p.get(pointer));
            }
            if (bits_per_pixel_1 == 32) {
                a = (image_1_p.get(pointer));
                b = (image_2_p.get(pointer));
            }
            array_mu1_ip[pointer] = array_mu1_ip_copy[pointer] = a; // Float.intBitsToFloat(a);
            array_mu2_ip[pointer] = array_mu2_ip_copy[pointer] = b; // Float.intBitsToFloat(b);
        }
        mu1_ip.convolve(window_weights, filter_width, filter_width);
        mu2_ip.convolve(window_weights, filter_width, filter_width);
        
        double[] mu1_sq = new double[imageDimension];
        double[] mu2_sq = new double[imageDimension];
        double[] mu1_mu2 = new double[imageDimension];
        
        for (pointer = 0; pointer < imageDimension; pointer++) {
            mu1_sq[pointer] = array_mu1_ip[pointer] * array_mu1_ip[pointer];
            mu2_sq[pointer] = array_mu2_ip[pointer] * array_mu2_ip[pointer];
            mu1_mu2[pointer] = array_mu1_ip[pointer] * array_mu2_ip[pointer];
        }
        
        double[] sigma1_sq = new double[imageDimension];
        double[] sigma2_sq = new double[imageDimension];
        double[] sigma12 = new double[imageDimension];
        
        for (pointer = 0; pointer < imageDimension; pointer++) {
            
            sigma1_sq[pointer] = array_mu1_ip_copy[pointer] * array_mu1_ip_copy[pointer];
            sigma2_sq[pointer] = array_mu2_ip_copy[pointer] * array_mu2_ip_copy[pointer];
            sigma12[pointer] = array_mu1_ip_copy[pointer] * array_mu2_ip_copy[pointer];
        }
        //
        // THERE IS A METHOD IN IMAGEJ THAT CONVOLVES ANY ARRAY, BUT IT ONLY
        // WORKS WITH IMAGE PROCESSORS. THIS IS THE REASON BECAUSE I CREATE THE
        // FOLLOWING PROCESSORS
        //
        ImageProcessor soporte_1_ip = new FloatProcessor(image_width, image_height);
        ImageProcessor soporte_2_ip = new FloatProcessor(image_width, image_height);
        ImageProcessor soporte_3_ip = new FloatProcessor(image_width, image_height);
        float[] array_soporte_1 = (float[]) soporte_1_ip.getPixels();
        float[] array_soporte_2 = (float[]) soporte_2_ip.getPixels();
        float[] array_soporte_3 = (float[]) soporte_3_ip.getPixels();
        
        for (pointer = 0; pointer < imageDimension; pointer++) {
            array_soporte_1[pointer] = (float) sigma1_sq[pointer];
            array_soporte_2[pointer] = (float) sigma2_sq[pointer];
            array_soporte_3[pointer] = (float) sigma12[pointer];
        }
        soporte_1_ip.convolve(window_weights, filter_width, filter_width);
        soporte_2_ip.convolve(window_weights, filter_width, filter_width);
        soporte_3_ip.convolve(window_weights, filter_width, filter_width);
        
        for (pointer = 0; pointer < imageDimension; pointer++) {
            sigma1_sq[pointer] = array_soporte_1[pointer] - mu1_sq[pointer];
            sigma2_sq[pointer] = array_soporte_2[pointer] - mu2_sq[pointer];
            sigma12[pointer] = array_soporte_3[pointer] - mu1_mu2[pointer];
        }
        ssim_map = new double[imageDimension];
        double suma = 0;
        for (pointer = 0; pointer < imageDimension; pointer++) {
            ssim_map[pointer] = (((2 * mu1_mu2[pointer]) + C1) * ((2 * sigma12[pointer]) + C2))
                    / ((mu1_sq[pointer] + mu2_sq[pointer] + C1)
                            * (sigma1_sq[pointer] + sigma2_sq[pointer] + C2));
            suma = suma + ssim_map[pointer];
        }
        
        ssimIndex = suma / imageDimension;
        String message_1 = " ";
        if (show_ssim_map) {
            ImageProcessor ssim_map_ip = new FloatProcessor(image_width, image_height, ssim_map);
            message_1 = "SSIM Index:   " + ssimIndex;
            ImagePlus ssim_map_imp = new ImagePlus(message_1, ssim_map_ip);
            ssim_map_imp.show();
            ssim_map_imp.updateAndDraw();
        }
        if (show_downsampled_images) {
            title_1 = coverImagePlus.getTitle();
            title_2 = stegoImagePlus.getTitle();
            title_1 = title_1 + " down scaled " + downsampled + " times";
            title_2 = title_2 + " down scaled " + downsampled + " times";
            ImagePlus image_1_final_imp = new ImagePlus(title_1, image_1_p);
            image_1_final_imp.show();
            image_1_final_imp.updateAndDraw();
            ImagePlus image_2_final_imp = new ImagePlus(title_2, image_2_p);
            image_2_final_imp.show();
            image_2_final_imp.updateAndDraw();
        }
        
        System.out.println(" ++++++++++++++++++++ SSIM index: " + ssimIndex);
        return Double.toString(ssimIndex);
    }
}
