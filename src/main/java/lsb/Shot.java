package main.java.lsb;

/**
 * A shot on a cover image.
 * <P>
 * A shot is a (x,y) co-ordinate and bit position triplet that is used to define
 * an attempted "shot" onto a steganographic image. They represent the position
 * of the pixel on the image, and the position of the bit that will be changed
 * by this "shot".
 */
class Shot {

	/** The x position of the pixel. */
	private final int xPos;
	/** The y position of the pixel. */
	private final int yPos;
	/** The bit position on the pixel. */
	private final int bitPos;
	/** The layer of the shot (0-based). */
	private final int layer;

	/**
	 * Creates a new shot with a default bit position of 0 and layer of 0.
	 * 
	 * @param xpos
	 *            The x position of the shot.
	 * @param ypos
	 *            The y position of the shot.
	 */
	public Shot(int xpos, int ypos) {
		this(xpos, ypos, 0, 0);
	}

	/**
	 * Creates a new shot.
	 * 
	 * @param xpos
	 *            The x position of the shot.
	 * @param ypos
	 *            The y position of the shot.
	 * @param bitpos
	 *            The bit position of the shot.
	 * @param layer
	 *            The layer for the shot.
	 */
	public Shot(int xpos, int ypos, int bitpos, int imLayer) {
		xPos = xpos;
		yPos = ypos;
		bitPos = bitpos;
		layer = imLayer;
	}

	/**
	 * Gets the x position of the shot.
	 * 
	 * @return The x position of the shot.
	 */
	public int getX() {
		return xPos;
	}

	/**
	 * Gets the y position of the shot.
	 * 
	 * @return The y position of the shot.
	 */
	public int getY() {
		return yPos;
	}

	/**
	 * Gets the bit position for the shot.
	 * 
	 * @return The bit position for the shot.
	 */
	public int getBitPosition() {
		return bitPos;
	}

	/**
	 * Gets the layer for the shot.
	 * 
	 * @return The bit position for the shot.
	 */
	public int getLayer() {
		return layer;
	}

}