package com.project.docxtopdf.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

public class Converter {
    public static final String PDF_DIR = "pdfs";

    // Font tiếng Việt
    private static BaseFont vietnameseFont;

    static {
        try {
            vietnameseFont = BaseFont.createFont("/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf", 
                    BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            System.out.println("Successfully loaded DejaVu Sans font");
        } catch (Exception e) {
            System.err.println("Failed to load DejaVu font, trying Liberation Sans: " + e.getMessage());
            try {

                vietnameseFont = BaseFont.createFont("/usr/share/fonts/truetype/liberation/LiberationSans-Regular.ttf",
                        BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                System.out.println("Successfully loaded Liberation Sans font");
            } catch (Exception ex) {
                System.err.println("Failed to load Liberation font, using Helvetica: " + ex.getMessage());
                try {

                    vietnameseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                } catch (Exception ex2) {
                    System.err.println("Critical: Failed to load any font: " + ex2.getMessage());
                }
            }
        }
    }

    public static String convertDocxToPdf(InputStream docxInputStream, String docxFileName, String outputDir)
            throws IOException {

        if (docxInputStream == null) {
            throw new IllegalArgumentException("Input stream cannot be null");
        }
        if (docxFileName == null || docxFileName.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }

        String targetDir = (outputDir != null && !outputDir.trim().isEmpty()) ? outputDir : PDF_DIR;
        File pdfDirFile = new File(targetDir).getAbsoluteFile();

        if (!pdfDirFile.exists()) {
            System.out.println("Creating target directory: " + pdfDirFile.getAbsolutePath());
            if (!pdfDirFile.mkdirs()) {
                throw new IOException("Failed to create output directory: " + targetDir);
            }
        }

        String pdfFileName = docxFileName.replaceAll("(?i)\\.(docx|doc)$", ".pdf");
        String outputPath = targetDir + File.separator + pdfFileName;
        File outputFile = new File(outputPath);

        XWPFDocument docx = null;
        FileOutputStream fos = null;
        Document pdfDocument = null;

        try {
            docx = new XWPFDocument(docxInputStream);
            fos = new FileOutputStream(outputFile);

            pdfDocument = new Document(PageSize.A4);
            pdfDocument.setMargins(50, 50, 50, 50);

            PdfWriter.getInstance(pdfDocument, fos);
            pdfDocument.open();

            Font normalFont = new Font(vietnameseFont, 12, Font.NORMAL);
            Font boldFont = new Font(vietnameseFont, 12, Font.BOLD);
            Font italicFont = new Font(vietnameseFont, 12, Font.ITALIC);
            Font boldItalicFont = new Font(vietnameseFont, 12, Font.BOLDITALIC);

            List<XWPFParagraph> paragraphs = docx.getParagraphs();
            for (XWPFParagraph para : paragraphs) {
                processParagraph(pdfDocument, para, normalFont, boldFont, italicFont, boldItalicFont);
            }

            List<XWPFTable> tables = docx.getTables();
            for (XWPFTable table : tables) {
                pdfDocument.add(new Paragraph(" "));
                processTable(pdfDocument, table, normalFont, boldFont);
                pdfDocument.add(new Paragraph(" "));
            }

            System.out.println("PDF conversion completed: " + outputPath);

        } catch (DocumentException e) {
            throw new IOException("PDF creation failed: " + e.getMessage(), e);
        } finally {
            closeResources(pdfDocument, fos, docx);
        }

        return PDF_DIR + File.separator + pdfFileName;
    }

    private static void processParagraph(Document pdfDocument, XWPFParagraph para,
                                         Font normalFont, Font boldFont,
                                         Font italicFont, Font boldItalicFont)
            throws DocumentException, IOException {

        Paragraph pdfPara = new Paragraph();
        pdfPara.setAlignment(getAlignment(para.getAlignment()));

        String styleId = para.getStyle();
        if (styleId != null && styleId.startsWith("Heading")) {
            int headingLevel = getHeadingLevel(styleId);
            Font headingFont = new Font(vietnameseFont, 18 - headingLevel * 2, Font.BOLD);
            pdfPara.setFont(headingFont);
        }

        List<XWPFRun> runs = para.getRuns();
        if (runs != null && !runs.isEmpty()) {
            for (XWPFRun run : runs) {
                String text = run.getText(0);
                if (text != null && !text.isEmpty()) {
                    Font font = getRunFont(run, normalFont, boldFont, italicFont, boldItalicFont);
                    pdfPara.add(new Chunk(text, font));
                }

                List<XWPFPicture> pictures = run.getEmbeddedPictures();
                if (pictures != null && !pictures.isEmpty()) {
                    for (XWPFPicture picture : pictures) {
                        try {
                            addImageToPdf(pdfDocument, pdfPara, picture);
                        } catch (Exception e) {
                            System.err.println("Failed to add image: " + e.getMessage());
                        }
                    }
                }
            }
        } else {
            String text = para.getText();
            if (text != null && !text.isEmpty()) {
                pdfPara.add(new Chunk(text, normalFont));
            }
        }

        if (pdfPara.isEmpty()) {
            pdfDocument.add(new Paragraph(" "));
        } else {
            pdfDocument.add(pdfPara);
        }
    }

    private static void addImageToPdf(Document pdfDocument, Paragraph pdfPara, XWPFPicture picture)
            throws IOException, DocumentException {

        XWPFPictureData pictureData = picture.getPictureData();
        byte[] imageBytes = pictureData.getData();

        try {
            Image image = Image.getInstance(imageBytes);

            // Scale image để fit trang
            float maxWidth = pdfDocument.getPageSize().getWidth() - 100; // Trừ margins
            float maxHeight = pdfDocument.getPageSize().getHeight() - 100;

            if (image.getWidth() > maxWidth) {
                float ratio = maxWidth / image.getWidth();
                image.scalePercent(ratio * 100);
            }

            if (image.getScaledHeight() > maxHeight) {
                float ratio = maxHeight / image.getScaledHeight();
                image.scalePercent(ratio * 100);
            }

            // Nếu paragraph đã có text, add image sau paragraph
            if (!pdfPara.isEmpty()) {
                pdfDocument.add(pdfPara);
                pdfPara.clear();
            }

            pdfDocument.add(image);
            pdfDocument.add(new Paragraph(" ")); // Spacing sau image

        } catch (BadElementException e) {
            System.err.println("Invalid image data: " + e.getMessage());
        }
    }

    private static Font getRunFont(XWPFRun run, Font normalFont, Font boldFont,
                                   Font italicFont, Font boldItalicFont) {
        boolean isBold = run.isBold();
        boolean isItalic = run.isItalic();

        // Lấy font size nếu có
        int fontSize = run.getFontSize();
        if (fontSize == -1) {
            fontSize = 12; // Default
        }

        Font selectedFont;
        if (isBold && isItalic) {
            selectedFont = new Font(vietnameseFont, fontSize, Font.BOLDITALIC);
        } else if (isBold) {
            selectedFont = new Font(vietnameseFont, fontSize, Font.BOLD);
        } else if (isItalic) {
            selectedFont = new Font(vietnameseFont, fontSize, Font.ITALIC);
        } else {
            selectedFont = new Font(vietnameseFont, fontSize, Font.NORMAL);
        }

        String color = run.getColor();
        if (color != null && !color.equals("auto")) {
            try {
                int r = Integer.parseInt(color.substring(0, 2), 16);
                int g = Integer.parseInt(color.substring(2, 4), 16);
                int b = Integer.parseInt(color.substring(4, 6), 16);
                selectedFont.setColor(new BaseColor(r, g, b));
            } catch (Exception e) {
                // Ignore color parsing errors
            }
        }

        return selectedFont;
    }

    private static int getAlignment(ParagraphAlignment alignment) {
        if (alignment == null) {
            return Element.ALIGN_LEFT;
        }

        switch (alignment) {
            case CENTER:
                return Element.ALIGN_CENTER;
            case RIGHT:
                return Element.ALIGN_RIGHT;
            case BOTH:
                return Element.ALIGN_JUSTIFIED;
            default:
                return Element.ALIGN_LEFT;
        }
    }

    private static int getHeadingLevel(String styleId) {
        try {
            return Integer.parseInt(styleId.replaceAll("\\D+", ""));
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private static void processTable(Document pdfDocument, XWPFTable table, Font normalFont, Font boldFont)
            throws DocumentException {

        int numCols = table.getRow(0).getTableCells().size();
        com.itextpdf.text.pdf.PdfPTable pdfTable = new com.itextpdf.text.pdf.PdfPTable(numCols);
        pdfTable.setWidthPercentage(100);

        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                Paragraph cellPara = new Paragraph();

                // Xử lý paragraphs trong cell
                for (XWPFParagraph para : cell.getParagraphs()) {
                    for (XWPFRun run : para.getRuns()) {
                        String text = run.getText(0);
                        if (text != null) {
                            Font font = run.isBold() ? boldFont : normalFont;
                            cellPara.add(new Chunk(text, font));
                        }
                    }
                }

                com.itextpdf.text.pdf.PdfPCell pdfCell =
                        new com.itextpdf.text.pdf.PdfPCell(cellPara);
                pdfCell.setPadding(5);
                pdfCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                pdfTable.addCell(pdfCell);
            }
        }

        pdfDocument.add(pdfTable);
    }

    private static void closeResources(Document pdfDocument, FileOutputStream fos, XWPFDocument docx) {
        if (pdfDocument != null && pdfDocument.isOpen()) {
            pdfDocument.close();
        }
        if (fos != null) {
            try {
                fos.close();
            } catch (IOException e) {
                System.err.println("Error closing FileOutputStream: " + e.getMessage());
            }
        }
        if (docx != null) {
            try {
                docx.close();
            } catch (IOException e) {
                System.err.println("Error closing XWPFDocument: " + e.getMessage());
            }
        }
    }
}