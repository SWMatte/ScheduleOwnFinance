package com.finance.services;

import com.finance.entities.DTO.PdfDTO;
import com.finance.entities.DTO.SummaryItDTO;
import com.finance.repositories.PdfRepository;
import lombok.AllArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.IOException;


@Service
@AllArgsConstructor
public class PdfService implements iPdfOperation {

    @Autowired
    private final PdfRepository pdfRepository;


    @Override
    public PDDocument createCustomPdf(String outputPdfPath, PdfDTO fields) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);  // Inizializzazione al di fuori del try-with-resources

        try {
            // Titolo
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("Invoice");
            contentStream.endText();

            // Campi singoli
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.newLineAtOffset(50, 720);
            contentStream.showText("Name: " + fields.getMonth());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Address: " + fields.getTotalAvailable());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Date: " + fields.getTotalSpent());
            contentStream.endText();

            // Impostazioni tabella
            float tableYPosition = 680;
            float margin = 50;
            float pageHeight = page.getMediaBox().getHeight();
            float rowHeight = 20f;
            float[] columnWidths = {80, 90, 90, 80, 80, 80};
            String[] headers = {"Data", "Descrizione", "TipoEvento", "ValoreInserito", "EuroRisparmiati", "EuroDisponibili"};

            // Header tabella
            drawTableRow(contentStream, headers, margin, tableYPosition, rowHeight, columnWidths, true);

            // Controllo overflow e nuove pagine per righe tabella
            float yStart = tableYPosition - rowHeight;
            for (SummaryItDTO item : fields.getListElement()) {
                // Aggiunta nuova pagina se necessario
                if (yStart < margin) {
                    contentStream.close();  // Chiude il contentStream corrente prima di creare la nuova pagina
                    page = new PDPage();
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                    yStart = tableYPosition;

                    // Ridisegna header tabella nella nuova pagina
                    drawTableRow(contentStream, headers, margin, yStart, rowHeight, columnWidths, true);
                    yStart -= rowHeight;
                }

                String[] rowData = {
                        wrapText(item.getData().toString(), columnWidths[0]),
                        wrapText(item.getDescrizione(), columnWidths[1]),
                        wrapText(item.getTipoEvento().toString(), columnWidths[2]),
                        wrapText(item.getValoreInserito().toString(), columnWidths[3]),
                        wrapText(item.getEuroRisparmiati().toString(), columnWidths[4]),
                        wrapText(item.getEuroDisponibili().toString(), columnWidths[5])
                };
                drawTableRow(contentStream, rowData, margin, yStart, rowHeight, columnWidths, false);
                yStart -= rowHeight;
            }
        } finally {
            contentStream.close();  // Assicura la chiusura del contentStream al termine
            document.save(outputPdfPath);
            document.close();
        }

        return document;
    }

    private void drawTableRow(PDPageContentStream contentStream, String[] content, float margin, float y, float rowHeight, float[] columnWidths, boolean isHeader) throws IOException {
        float nextX = margin;
        for (int i = 0; i < content.length; i++) {
            contentStream.setFont(isHeader ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA, 8);  // Riduzione font per adattamento
            contentStream.beginText();
            contentStream.newLineAtOffset(nextX + 5, y - 15);
            contentStream.showText(content[i] != null ? content[i] : "");
            contentStream.endText();
            nextX += columnWidths[i];
        }
        contentStream.moveTo(margin, y);
        contentStream.lineTo(nextX, y);
        contentStream.stroke();
    }

    private String wrapText(String text, float maxWidth) {
        int maxChars = (int) (maxWidth / 6); // Stima adattata per larghezza cella
        return text.length() > maxChars ? text.substring(0, maxChars - 3) + "..." : text;
    }

    private String getPathDesktop() {
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("win")) {
            String userProfile = System.getenv("USERPROFILE");
            return userProfile + "\\Desktop";
        } else if (osName.contains("mac")) {
            String userHome = System.getProperty("user.home");
            return userHome + "/Desktop";
        } else if (osName.contains("nix") || osName.contains("nux")) {
            String userHome = System.getProperty("user.home");
            return userHome + "/Desktop";
        } else {
            throw new UnsupportedOperationException("Operation system not supported");
        }
    }



/*


    private byte[] saveDocumentInByteArray(PDDocument pdDocument) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        pdDocument.save(byteArrayOutputStream);
        pdDocument.close();
        return byteArrayOutputStream.toByteArray();
    }


    private void savePdfToDatabase(byte[] pdfBytes, Order order) {
        Optional<Pdf> pdf1 = Optional.ofNullable(pdfRepository.findByNumberOrder(order.getNumberOrder()));
        if (pdf1.isEmpty()) {
            Pdf pdf = Pdf.builder()
                    .pdfData(pdfBytes)
                    .numberOrder(order.getNumberOrder())
                    .build();
            pdfRepository.save(pdf);
        }else {
            pdf1.get().setPdfData(pdfBytes);
            pdfRepository.save(pdf1.get());
        }

    }

    private byte[] retrieveByteArrayFromDB(String order) {
        Pdf pdf = pdfRepository.findByNumberOrder(order);
        byte[] bytes = pdf.getPdfData();
        return bytes;
    }



    private PDDocument constructPdfFromDatabase(String numberOrder) throws IOException {
        // Recupera il byte array dal database
        byte[] pdfBytes = retrieveByteArrayFromDB(numberOrder);

        // Ricostruisce il documento PDF dall'array di byte
        PDDocument pdfDocument = PDDocument.load(pdfBytes);

        return pdfDocument; // Restituisce il documento PDF ricostruito
    }



    public void processPdfFromDB(String numberOrder) throws IOException {
        String operationSystem = getPathDesktop();
        // Ricrea il documento pdf
        PDDocument pdfDocument = constructPdfFromDatabase(numberOrder);
        // aggiungo una pagina per facs simile elaborazione dal ristorante

        PDPage newPage = new PDPage(); // Aggiungi una nuova pagina
        pdfDocument.addPage(newPage);
        PDPageContentStream contentStream = new PDPageContentStream(pdfDocument, newPage);
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 11);
        contentStream.newLineAtOffset(100, 650);
        contentStream.showText("Grazie per l'acquisto il tuo ordine è stato processato");
        contentStream.endText();
        contentStream.close();
        // Salva il PDF su disco
        pdfDocument.save(operationSystem + "\\output1.pdf");

        byte[] pdfBytes = saveDocumentInByteArray(pdfDocument);

        Pdf pdf = pdfRepository.findByNumberOrder(numberOrder);
        pdf.setPdfData(pdfBytes);
        pdf.setDateSaved(LocalDate.now());
        pdf.setDocumentProcessed(true);
        pdfRepository.save(pdf);

    }
 */


}