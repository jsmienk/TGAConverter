package nl.jeroensmienk;

import nl.jeroensmienk.tga.TGAImageReaderSpi;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main {

    private static final String OUTPUT_FORMAT = "PNG";
    private static final String OUTPUT_PREFIX = "ic_";
    private static final String INPUT_EXTENSION = ".tga";
    private static final String OUTPUT_EXTENSION = ".png";

    /*

    Convert .tga images to .png from a given folder and put them in a folder named 'converted' inside that folder

     */

    public static void main(String[] args) {
        final IIORegistry registry = IIORegistry.getDefaultInstance();
        registry.registerServiceProvider(new TGAImageReaderSpi());

        // Source folder argument
        if (args.length > 0) {
            final String path = args[0];
            // If source folder exists
            final File sourceFolder = new File(path);
            if (sourceFolder.exists()) {
                // If source folder has files
                final File[] sourceFilesArray = sourceFolder.listFiles();
                if (sourceFilesArray != null && sourceFilesArray.length > 0) {
                    // If target folder exists
                    final File targetFolder = new File(sourceFolder.getAbsolutePath() + "/converted");
                    if (!targetFolder.exists()) {
                        // Else create it
                        final boolean targetFolderCreation = targetFolder.mkdir();
                        if (targetFolderCreation) {
                            System.out.println("Target folder created: " + targetFolder.getAbsolutePath());
                        } else {
                            System.err.println("Target folder could not be created! " + targetFolder.getAbsolutePath());
                        }
                    }

                    // Find all files in the source folder
                    final List<File> sourceFiles = Arrays.asList(sourceFilesArray);

                    // For every source file
                    double count = 0.0;
                    final double max = sourceFiles.size();
                    for (final File file : sourceFiles) {
                        System.out.println("Progress: " + (count / max * 100));

                        // If it is not a directory
                        if (file.isFile()) {
                            final String fileName = file.getName().substring(0, file.getName().length() - INPUT_EXTENSION.length());
                            final String targetFileName = OUTPUT_PREFIX + fileName + OUTPUT_EXTENSION;
                            final String targetPath = targetFolder.getAbsolutePath() + "/" + targetFileName;
                            try {
                                boolean result = convertFormat(file.getAbsolutePath(), targetPath, OUTPUT_FORMAT);
                                if (!result) {
                                    System.err.println("Could not convert image!");
                                }
                            } catch (IOException ex) {
                                System.err.println("Error during converting image!");
                                ex.printStackTrace();
                            }
                        }

                        count++;
                    }
                } else {
                    System.err.println("No source files found!");
                }
            } else {
                System.err.println("Source folder does not exist!");
            }
        } else {
            System.err.println("No source folder provided!");
        }
    }

    /**
     * Converts an image to another format
     *
     * @param inputImagePath  Path of the source image
     * @param outputImagePath Path of the destination image
     * @param formatName      the format to be converted to, one of: jpeg, png,
     *                        bmp, wbmp, and gif
     * @return true if successful, false otherwise
     * @throws IOException if errors occur during writing
     */
    private static boolean convertFormat(String inputImagePath, String outputImagePath, String formatName) throws IOException {
        boolean result = false;
        try (final FileInputStream inputStream = new FileInputStream(inputImagePath)) {
            try (final FileOutputStream outputStream = new FileOutputStream(outputImagePath)) {

                // reads input image from file
                final BufferedImage inputImage = ImageIO.read(inputStream);
                if (inputImage != null) {

                    // writes to the output image in specified format
                    result = ImageIO.write(inputImage, formatName, outputStream);
                } else {
                    System.err.println("InputImage == null!");
                }
            }
        }

        return result;
    }
}
