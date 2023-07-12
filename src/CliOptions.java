/**
 * Defines the available options for the Asciizr application.
 * 
 * @author Ismael Trentin
 * @version 2023.07.12
 */
public class CliOptions {

  /**
   * The bmp image file path.
   */
  public String bmpPath;

  /**
   * Defines if the header values should be printed, `true` will print, `false no.
   */
  public boolean printHeader;

  /**
   * Defines if the output should be written to a file, `true` will write to
   * output, `false` no.
   */
  public boolean writeTo;

  /**
   * The path to the output file.
   */
  public String writeToPath;

  /**
   * Defines if a custom characters map should be used, `true` yes, `false` no.
   */
  public boolean useCustomCharMap;

  /**
   * The custom characters map to be used for the ascii art generation.
   */
  public char[] customCharMap;

  /**
   * Defines if the brightness should be inversed, `true` is inversed, `false` is
   * normal.
   */
  public boolean inverseBrightness;

  /**
   * Creates a new instance of `CliOptions`.
   */
  public CliOptions() {
  }
}
