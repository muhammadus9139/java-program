package Services;

// PDFPrintService.java - NEW FILE for Print & Download

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class PDFPrintService {

    /**
     * Print PDF directly to default printer
     */
    public static boolean printPDF(String pdfPath) {
        try {
            File pdfFile = new File(pdfPath);
            if (!pdfFile.exists()) {
                System.err.println("PDF file not found: " + pdfPath);
                return false;
            }

            // Get default print service
            PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();

            if (defaultPrintService == null) {
                System.err.println("No default printer found");
                return false;
            }

            // Open with default PDF viewer for printing
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.PRINT)) {
                    desktop.print(pdfFile);
                    return true;
                } else {
                    // Open file for manual printing
                    desktop.open(pdfFile);
                    return true;
                }
            }

            return false;

        } catch (IOException e) {
            System.err.println("Print error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Show print dialog with options
     */
    public static boolean printWithDialog(String pdfPath) {
        try {
            File pdfFile = new File(pdfPath);
            if (!pdfFile.exists()) {
                return false;
            }

            // Use Desktop API to print
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().print(pdfFile);
                return true;
            }

            return false;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Download PDF to specific location with custom name
     */
    public static boolean downloadPDF(String sourcePath, String destinationFolder, String fileName) {
        try {
            Path source = Paths.get(sourcePath);
            Path destination = Paths.get(destinationFolder, fileName);

            // Create destination folder if not exists
            Files.createDirectories(destination.getParent());

            // Copy file
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);

            return true;

        } catch (IOException e) {
            System.err.println("Download error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Quick download to Downloads folder
     */
    public static String downloadToDownloads(String sourcePath, String fileName) {
        try {
            // Get user's Downloads folder
            String userHome = System.getProperty("user.home");
            String downloadsPath = userHome + File.separator + "Downloads";

            Path source = Paths.get(sourcePath);
            Path destination = Paths.get(downloadsPath, fileName);

            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);

            return destination.toString();

        } catch (IOException e) {
            System.err.println("Download to Downloads error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Open PDF in default viewer
     */
    public static boolean openPDF(String pdfPath) {
        try {
            File pdfFile = new File(pdfPath);
            if (!pdfFile.exists()) {
                return false;
            }

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdfFile);
                return true;
            }

            return false;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get available printers
     */
    public static String[] getAvailablePrinters() {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        String[] printerNames = new String[printServices.length];

        for (int i = 0; i < printServices.length; i++) {
            printerNames[i] = printServices[i].getName();
        }

        return printerNames;
    }

    /**
     * Print to specific printer
     */
    public static boolean printToSpecificPrinter(String pdfPath, String printerName) {
        try {
            // Find printer by name
            PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
            PrintService selectedPrinter = null;

            for (PrintService service : printServices) {
                if (service.getName().equals(printerName)) {
                    selectedPrinter = service;
                    break;
                }
            }

            if (selectedPrinter == null) {
                System.err.println("Printer not found: " + printerName);
                return false;
            }

            // For PDF, just open with system default viewer for now
            return openPDF(pdfPath);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}