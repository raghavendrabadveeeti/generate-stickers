package com.rgv.print.stickers;

import com.itextpdf.barcodes.Barcode128;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GeneratePrintableStickers {

  public static void main(String[] args) throws Exception {

    String currDir = getCurrDir();
    String fileName = new SimpleDateFormat("'Sticker_'" + "yyyy_MM_dd_HH_mm_ss'.pdf'", Locale.getDefault()).format(new Date());
    String destFileName = currDir + File.separator + fileName;
    File file = new File(destFileName);
    file.getParentFile().mkdirs();
    new GeneratePrintableStickers().generatePDF(destFileName);
  }

  private static String getCurrDir() {
    return Paths.get(".").toAbsolutePath().normalize().toString();
  }

  protected void generatePDF(String destFile) throws Exception {
    List<String> rdAccountNumbers = readAccountNumbers();
    PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destFile));
    Document doc = new Document(pdfDoc, PageSize.A4);
    doc.setMargins(0, 0, 0, 0);

    Table table = new Table(new float[4]).useAllAvailableWidth();
    table.setMarginTop(0);
    table.setMarginBottom(0);
    table.setBorder(Border.NO_BORDER);

    for (int i = 0; i < rdAccountNumbers.size(); i++) {
      String accountNumber = rdAccountNumbers.get(i);
      Image barcodeImage = getBarcodeImage(pdfDoc, accountNumber);
      Cell barcodeCell = new Cell();
      barcodeCell.setHeight(UnitValue.createPointValue(79.61f));
      barcodeCell.setMargins(0, 0, 0, 0);
      barcodeCell.setVerticalAlignment(VerticalAlignment.MIDDLE);
      barcodeCell.setHorizontalAlignment(HorizontalAlignment.RIGHT);
      if (accountNumber.isEmpty() || accountNumber.trim().equals("")) {
        Paragraph paragraph = new Paragraph(" ");
        paragraph.setWidth(UnitValue.createPointValue(148.819f));
        barcodeCell.add(paragraph);
      } else {
        barcodeCell.setPaddingLeft(20);
        barcodeCell.add(barcodeImage);
      }
      barcodeCell.setBorder(Border.NO_BORDER);
      table.addCell(barcodeCell);
    }
    doc.add(table);
    doc.close();
  }

  protected static Image getBarcodeImage(PdfDocument pdfDoc, String code) {
    Barcode128 code128 = new Barcode128(pdfDoc);
    code128.setBaseline(-1);
    code128.setSize(14);
    code128.setCode(code);
    code128.setCodeType(Barcode128.CODE128);
    Image code128Image = new Image(code128.createFormXObject(pdfDoc));
    return code128Image;
  }

  protected static List<String> readAccountNumbers() throws IOException {
    List<String> accountNumbers = new ArrayList<>();
    String sourceFileLocation = getCurrDir() + File.separator + "RD_Account_Numbers.csv";
    try (BufferedReader br = new BufferedReader(new FileReader(sourceFileLocation))) {
      String line;
      while ((line = br.readLine()) != null) {
        accountNumbers.add(line.trim());
      }
    }
    return accountNumbers;
  }

}
