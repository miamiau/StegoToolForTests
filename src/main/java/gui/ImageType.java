package main.java.gui;

/**
 * ImageType enum.
 * 
 * @author Teodora C.
 */
public enum ImageType {

	COVER("coverimage"), SECRET("secretimage"), STEGO("stegoimage"), SOURCE(
			"sourceimage"), HIDDEN("hiddenimage");

	private String type;

	/**
	 * The constructor for ImageType.
	 * 
	 * @param type
	 */
	private ImageType(String type) {
		this.type = type;
	}

	/**
	 * Getter for the image type.
	 * 
	 * @return
	 */
	public String getType() {
		return type;
	}

}
