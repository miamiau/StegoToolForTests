/**
 *
 */
package main.java.ssim;

/**
 * @author teodora.cosma
 */
public class SsimException extends Exception {
    
    private static final long serialVersionUID = 1L;

    public SsimException() {
        super("Sssim index could not be computed!");
    }

    public SsimException(String message) {
        super(message);
    }
}
