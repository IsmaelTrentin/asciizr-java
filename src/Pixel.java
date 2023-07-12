/**
 * Simple representation of a Pixel.
 * 
 * @author Ismael Trentin
 * @version 2023.07.12
 */
public class Pixel {
  /**
   * The pixel red channel value.
   */
  private int r;

  /**
   * The pixel green channel value.
   */
  private int g;

  /**
   * The pixel blue channel value.
   */
  private int b;

  /**
   * Creates a new instance of a Pixel.
   * 
   * @param r pixel red channel value
   * @param g pixel green channel value
   * @param b pixel blue channel value
   */
  public Pixel(int r, int g, int b) {
    this.r = r;
    this.g = g;
    this.b = b;
  }

  /**
   * Returns the red channel value of the pixel.
   * 
   * @return the red channel value of the pixel
   */
  public int getRed() {
    return this.r;
  }

  /**
   * Returns the green channel value of the pixel.
   * 
   * @return the green channel value of the pixel
   */
  public int getGreen() {
    return this.g;
  }

  /**
   * Returns the blue channel value of the pixel.
   * 
   * @return the blue channel value of the pixel
   */
  public int getBlue() {
    return this.b;
  }

  /**
   * Sums all the channels and returns the value.
   * 
   * @return the sum of all the pixel color channels
   */
  public int getSum() {
    return this.r + this.g + this.b;
  }

  @Override
  public String toString() {
    return String.format(
        "(%s, %s, %s)",
        this.r,
        this.g,
        this.b);
  }
}
