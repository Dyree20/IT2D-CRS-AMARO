package vrs;

import java.awt.*;
import java.awt.print.*;
import javax.swing.JPanel;
import javax.swing.JOptionPane;

public class PanelPrinter implements Printable {
    private JPanel panelToPrint;
    private boolean showPrintDialog;
    private PrintQuality printQuality;
    
    public enum PrintQuality {
        DRAFT(1),
        NORMAL(2),
        HIGH(3);
        
        private final int value;
        
        PrintQuality(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
    }
    
    public PanelPrinter(JPanel panelToPrint) {
        this(panelToPrint, true, PrintQuality.NORMAL);
    }
    
    public PanelPrinter(JPanel panelToPrint, boolean showPrintDialog, PrintQuality printQuality) {
        this.panelToPrint = panelToPrint;
        this.showPrintDialog = showPrintDialog;
        this.printQuality = printQuality;
    }
    
    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return Printable.NO_SUCH_PAGE;
        }
        
        try {
            Graphics2D g2d = (Graphics2D) graphics;
            
            // Enable anti-aliasing for better quality
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            // Set print quality
            switch (printQuality) {
                case HIGH:
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    break;
                case DRAFT:
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
                    break;
                default:
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);
            }
            
            // Set page format to bond paper (8.5 x 11 inches)
            pageFormat.setOrientation(PageFormat.PORTRAIT);
            Paper paper = new Paper();
            double width = 8.5 * 72; // 8.5 inches converted to points
            double height = 11 * 72; // 11 inches converted to points
            paper.setSize(width, height);
            
            // Set margins (0.5 inches on all sides)
            double margin = 0.5 * 72;
            paper.setImageableArea(margin, margin, width - (2 * margin), height - (2 * margin));
            pageFormat.setPaper(paper);
            
            // Calculate scaling to fit the panel on the page
            double scaleX = pageFormat.getImageableWidth() / panelToPrint.getWidth();
            double scaleY = pageFormat.getImageableHeight() / panelToPrint.getHeight();
            double scale = Math.min(scaleX, scaleY);
            
            // Center the panel on the page
            double xOffset = (pageFormat.getImageableWidth() - (panelToPrint.getWidth() * scale)) / 2;
            double yOffset = (pageFormat.getImageableHeight() - (panelToPrint.getHeight() * scale)) / 2;
            
            // Apply transformations
            g2d.translate(pageFormat.getImageableX() + xOffset, pageFormat.getImageableY() + yOffset);
            g2d.scale(scale, scale);
            
            // Print the panel
            panelToPrint.printAll(graphics);
            
            return Printable.PAGE_EXISTS;
        } catch (Exception e) {
            throw new PrinterException("Error printing panel: " + e.getMessage());
        }
    }
    
    public void printPanel() {
        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable(this);
            
            if (!showPrintDialog || job.printDialog()) {
                job.print();
            }
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(null,
                "Error printing: " + ex.getMessage(),
                "Print Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void setPrintQuality(PrintQuality quality) {
        this.printQuality = quality;
    }
    
    public PrintQuality getPrintQuality() {
        return printQuality;
    }
} 