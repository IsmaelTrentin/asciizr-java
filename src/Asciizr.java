import java.nio.file.Paths;
import java.text.ParseException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

/**
 * Simple ascii-art generator from a bmp 24bit image.
 * 
 * @author Ismael Trentin
 * @version 2023.07.12
 */
public class Asciizr {

  // App specific
  public static final int[] SUPPORTED_BITS_PER_PIXEL = new int[] { 24 };
  public static final char[] DEFAULT_CHAR_MAP = new char[] { '@', '#', '%', '/', '-', '\'', '.', ' ' };

  public static final int BYTES_2 = 2;
  public static final int BYTES_4 = 4;

  // BITMAP file header
  public static final int SIGNATURE_OFFSET = 0;
  public static final int FILE_SIZE_OFFSET = SIGNATURE_OFFSET + BYTES_2;
  public static final int RESERVED_1_OFFSET = FILE_SIZE_OFFSET + BYTES_4;
  public static final int RESERVED_2_OFFSET = RESERVED_1_OFFSET + BYTES_2;
  public static final int PIXEL_ARRAY_OFFSET_OFFSET = RESERVED_2_OFFSET + BYTES_2;

  // DIB header
  public static final int DIB_HEADER_SIZE_OFFSET = PIXEL_ARRAY_OFFSET_OFFSET + BYTES_4;
  public static final int IMAGE_WIDTH_OFFSET = DIB_HEADER_SIZE_OFFSET + BYTES_4;
  public static final int IMAGE_HEIGHT_OFFSET = IMAGE_WIDTH_OFFSET + BYTES_4;
  public static final int PLANES_OFFSET = IMAGE_HEIGHT_OFFSET + BYTES_4;
  public static final int BITS_PER_PIXEL_OFFSET = PLANES_OFFSET + BYTES_2;
  public static final int COMPRESSION_OFFSET = BITS_PER_PIXEL_OFFSET + BYTES_2;
  public static final int IMAGE_SIZE_OFFSET = COMPRESSION_OFFSET + BYTES_4;
  // ...

  /**
   * Reads a UInt16 with little endianess.
   * 
   * @param bytes  the bytes from which to read from
   * @param offset the bytes array index offset
   * @return a UInt16
   * @throws Exception if the are not enough bytes to be read
   */
  private static int read2BytesLE(byte[] bytes, int offset) throws Exception {
    if (bytes.length < BYTES_2) {
      throw new Exception("not enough bytes");
    }

    int result = 0;
    return result
        | (0xff & bytes[offset + 1]) << 8
        | (0xff & bytes[offset]);
  }

  /**
   * Reads a UInt32 with little endianess.
   * 
   * @param bytes  the bytes from which to read from
   * @param offset the bytes array index offset
   * @return a UInt32
   * @throws Exception if the are not enough bytes to be read
   */
  private static long read4BytesLE(byte[] bytes, int offset) throws Exception {
    if (bytes.length < BYTES_4) {
      throw new Exception("not enough bytes");
    }

    long result = 0;
    return result
        | (0xff & bytes[offset + 3]) << 24
        | (0xff & bytes[offset + 2]) << 16
        | (0xff & bytes[offset + 1]) << 8
        | (0xff & bytes[offset]);
  }

  /**
   * Parses the arguments `args` to `CliOptions`.
   * 
   * @param args the inline arguments
   * @return a `CliOptions` object
   * @throws ParseException when parsing fails
   */
  private static CliOptions parseArguments(String[] args) throws ParseException {
    if (args.length == 0) {
      throw new ParseException("no image path provided", 0);
    }

    List<String> argsList = Arrays.asList(args);
    CliOptions options = new CliOptions();

    options.bmpPath = args[0];
    options.printHeader = argsList.contains("-H");
    options.inverseBrightness = argsList.contains("-i");

    if (argsList.contains("-c") && argsList.get(argsList.indexOf("-c") + 1) != null) {
      options.useCustomCharMap = true;

      String charMapStr = argsList.get(argsList.indexOf("-c") + 1);

      if (!charMapStr.startsWith("[") || !charMapStr.endsWith("]")) {
        throw new ParseException("invalid custom charmap. must be [char,char,...]", 0);
      }

      int charactersAmount = charMapStr.length() - 2;
      charactersAmount -= charactersAmount / 2;
      char[] customCharMap = new char[charactersAmount];
      int customI = 0;
      for (int i = 0; i < charMapStr.length() - 1; i++) {
        if (i % 2 == 0) {
          customCharMap[customI] = charMapStr.charAt(i + 1);
          customI++;
        }
      }

      options.customCharMap = customCharMap;

    }

    if (argsList.contains("-o") && argsList.get(argsList.indexOf("-o") + 1) != null) {
      options.writeTo = true;
      options.writeToPath = argsList.get(argsList.indexOf("-o") + 1);
    }

    return options;
  }

  public static void main(String[] args) throws Exception {
    // bitmap file is necessary
    if (args.length == 0) {
      System.err.println("provide bitmap path as first argument");
      return;
    }

    // cli args
    CliOptions options = parseArguments(args);
    Path imagePath = Paths.get(options.bmpPath);
    Path writeToPath;

    // check if image is accessible
    File imageFile = imagePath.toFile();
    if (!imageFile.exists()) {
      System.err.printf("path %s not found\n", imagePath.toString());
      return;
    }
    if (!imageFile.canRead()) {
      System.err.printf("cannot read %s\n", imagePath.toString());
      return;
    }

    // check if a file with the name of the output file already exists
    if (options.writeTo) {
      writeToPath = Paths.get(options.writeToPath);
      File writeToFile = writeToPath.toFile();
      if (writeToFile.exists()) {
        System.err.printf("file %s already exists\n", writeToPath.toString());
        return;
      }
    }

    // read image
    byte[] imageBytes;
    try {
      imageBytes = Files.readAllBytes(imagePath);
    } catch (IOException e) {
      System.err.printf("io error: %s\n", e.getMessage());
      e.printStackTrace(System.err);
      return;
    }

    // read necessary header entries
    String sig = new String(new char[] { (char) imageBytes[0], (char) imageBytes[1] });
    long fileSize = read4BytesLE(imageBytes, FILE_SIZE_OFFSET);
    long pixelArrayOffset = read4BytesLE(imageBytes, PIXEL_ARRAY_OFFSET_OFFSET);
    long imageWidth = read4BytesLE(imageBytes, IMAGE_WIDTH_OFFSET);
    long imageHeight = read4BytesLE(imageBytes, IMAGE_HEIGHT_OFFSET);
    int bitsPerPixel = read2BytesLE(imageBytes, BITS_PER_PIXEL_OFFSET);
    long imageSize = read4BytesLE(imageBytes, IMAGE_SIZE_OFFSET);
    imageSize = imageSize == 0 ? imageBytes.length - (int) pixelArrayOffset : imageSize;

    if (options.printHeader) {
      System.out.println("signature:          " + sig);
      System.out.println("file size:          " + fileSize);
      System.out.println("pixel array offset: " + pixelArrayOffset);
      System.out.println("width:              " + imageWidth);
      System.out.println("height:             " + imageHeight);
      System.out.println("bits per pixel:     " + bitsPerPixel);
      System.out.println("image size:         " + imageSize);
    }

    // Search supported bits per pixel and exit if a supported one is not found
    if (Arrays.binarySearch(
        SUPPORTED_BITS_PER_PIXEL,
        0,
        SUPPORTED_BITS_PER_PIXEL.length,
        bitsPerPixel) == -1) {
      System.err.printf(
          "image bits per pixel (%d) not supported. supported: %s",
          bitsPerPixel,
          Arrays.toString(SUPPORTED_BITS_PER_PIXEL));
      return;
    }

    Pixel[][] rawPixels = new Pixel[(int) imageHeight][(int) imageWidth];
    int bytesPerPixel = bitsPerPixel / 8;
    // calculate the bytes used to pad rows if necessary
    int paddingBytes = (int) imageWidth * (bytesPerPixel) % 4;
    int bytesInRow = bytesPerPixel * (int) imageWidth + paddingBytes;
    char[][] asciiArt = new char[(int) imageHeight][(int) imageWidth];

    // read pixel array data
    int paddingOffset = 0;
    for (int i = 0; i < (int) imageSize - paddingOffset; i += bytesPerPixel) {
      int bytesOffset = (int) pixelArrayOffset + paddingOffset + i;

      int b = 0xff & imageBytes[bytesOffset];
      int g = 0xff & imageBytes[bytesOffset + 1];
      int r = 0xff & imageBytes[bytesOffset + 2];
      Pixel p = new Pixel(r, g, b);

      // map pixel coords since bmp height is reversed
      int pixelY = (int) imageHeight - (i + paddingOffset) / bytesInRow - 1;
      int pixelX = (i / bytesPerPixel) % (int) imageWidth;
      rawPixels[pixelY][pixelX] = p;

      // add padding if we are at the end of the pixel row
      if (i != 0 && i * bytesPerPixel % (bytesInRow - paddingBytes) == bytesPerPixel) {
        paddingOffset += paddingBytes;
      }
    }

    // generate ascii art
    char[] charMap = options.useCustomCharMap ? options.customCharMap : DEFAULT_CHAR_MAP;
    for (int y = 0; y < rawPixels.length; y++) {
      for (int x = 0; x < rawPixels[y].length; x++) {
        // calculate brightness adding the color channels together
        double brightness = rawPixels[y][x].getSum() / (255.0 * 3); // [0.0;1.0]
        // map brightness to a charmap index
        int charMapIdx = (int) Math.round((charMap.length - 1) * brightness);
        asciiArt[y][x] = charMap[options.inverseBrightness ? (charMap.length - 1) - charMapIdx : charMapIdx];
      }
    }

    if (options.writeTo) {
      // why java why ?!?!?
      writeToPath = Paths.get(options.writeToPath);

      byte[] data = new byte[(int) imageHeight * ((int) imageWidth + 1)];
      for (int i = 0; i < data.length - imageWidth; i++) {
        // add new line
        if (i % (int) imageWidth == (int) imageWidth - 1) {
          data[i] = '\n';
          continue;
        }

        int y = i / (int) imageWidth;
        int x = i % (int) imageWidth;
        data[i] = (byte) asciiArt[y][x];
      }

      try {
        Files.write(writeToPath, data);
      } catch (IOException e) {
        System.err.println("io error: could not write file " + e.getMessage());
        e.printStackTrace(System.err);
        return;
      }

      System.out.printf("wrote file to %s", writeToPath.toString());
      return;
    }

    // print art if no out file is specified
    for (int y = 0; y < asciiArt.length; y++) {
      for (int x = 0; x < asciiArt[y].length; x++) {
        System.out.print(asciiArt[y][x]);
      }
      System.out.println();
    }
  }
}
