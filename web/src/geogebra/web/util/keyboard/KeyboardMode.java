package geogebra.web.util.keyboard;

/**
 * Keyboard modes
 * 
 * @author bencze
 */
public enum KeyboardMode {
	/**
	 * Text input mode.
	 */
	TEXT("ABC"),
	/**
	 * Number input mode.
	 */
	NUMBER("123");

	private String internalName;

	KeyboardMode(String internalName) {
		this.internalName = internalName;
	}

	/**
	 * @return the internal name of this mode
	 */
	public String getInternalName() {
		return internalName;
	}
}
