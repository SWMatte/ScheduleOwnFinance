package com.finance.services;

import com.finance.entities.DTO.PdfDTO;
import com.finance.entities.DTO.SummaryItDTO;
import com.finance.repositories.PdfRepository;
import lombok.AllArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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
            // Dimensioni della pagina
            PDRectangle pageSize = page.getMediaBox();
            // float pageWidth = pageSize.getWidth();
            float pageWidth = page.getMediaBox().getWidth();
            // Calcolo della larghezza del testo
            String text = "Invoice";
            PDFont font = PDType1Font.HELVETICA_BOLD;
            float fontSize = 18;
            float textWidth = font.getStringWidth(text) / 1000 * fontSize;

            // Calcolo della posizione orizzontale per centrare il testo
            float textX = (pageWidth - textWidth) / 2;
            float textY = 750; // Altezza del testo (puoi personalizzarla)

            // Scrittura del testo
            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.newLineAtOffset(textX, textY);
            contentStream.showText(text);
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
            contentStream.newLineAtOffset(0, -20); // riga vuota
            contentStream.endText();


            String[] headers = {"Data", "Descrizione", "TipoEvento", "ValoreInserito", "EuroRisparmiati", "EuroDisponibili", "nuovaColonna"};
            float margin = 50; // Margine laterale
            float tableYPosition = 680 - 50; // Altezza iniziale della tabella
            float rowHeight = 20f; // Altezza standard di ogni riga
            Integer[] columnWidths = {80, 90, 90, 80, 80, 80, 80}; // Specifica le larghezze di ciascuna colonna

            // Calcola la larghezza totale delle colonne
            float totalTableWidth = Arrays.stream(columnWidths).mapToInt(Integer::intValue).sum();


// Verifica se la tabella è più larga della pagina, in caso riduci la larghezza delle colonne
            if (totalTableWidth > pageWidth - 2 * margin) {
                float scalingFactor = (pageWidth - 2 * margin) / totalTableWidth;
                for (int i = 0; i < columnWidths.length; i++) {
                    columnWidths[i] = (int) (columnWidths[i] * scalingFactor); // Ridimensiona la larghezza delle colonne
                }
            }

        // Calcola la posizione orizzontale per centrare la tabella
             float tableXPosition = (pageWidth - totalTableWidth) / 2;



            // Disegno della tabella
            drawTable(contentStream, headers, fields.getListElement(), margin, tableYPosition, rowHeight, columnWidths, document);

        } finally {
            // Chiudi il ContentStream e salva il documento
            contentStream.close();
            document.save(outputPdfPath);
            document.close();
        }

        return document;
    }

    private void drawTable(PDPageContentStream contentStream, String[] headers, List<SummaryItDTO> rows, float margin,
                           float yStart, float rowHeight, Integer[] columnWidths, PDDocument document) throws IOException {
        float nextY = yStart;

        // Disegna header
        drawTableRow(contentStream, headers, margin, nextY, rowHeight, columnWidths, true);
        nextY -= rowHeight;

        for (SummaryItDTO item : rows) {
            if (nextY < margin) {
                contentStream.close(); // Chiudi il contentStream corrente prima di creare una nuova pagina
                PDPage page = new PDPage();
                document.addPage(page);
                contentStream = new PDPageContentStream(document, page);
                nextY = yStart;

                // Ridisegna l'header nella nuova pagina
                drawTableRow(contentStream, headers, margin, nextY, rowHeight, columnWidths, true);
                nextY -= rowHeight;
            }

            String[] rowData = {
                    wrapText(item.getData().toString(), columnWidths[0]),
                    wrapText(item.getDescrizione(), columnWidths[1]),
                    wrapText(item.getTipoEvento().toString(), columnWidths[2]),
                    wrapText(item.getValoreInserito().toString(), columnWidths[3]),
                    wrapText(item.getEuroRisparmiati().toString(), columnWidths[4]),
                    wrapText(item.getEuroDisponibili().toString(), columnWidths[5]),
                    wrapText("PIPPO", columnWidths[6]),
            };

            nextY = drawWrappedRow(contentStream, rowData, margin, nextY, rowHeight, columnWidths);
        }

        // Disegna una linea inferiore per chiudere la tabella
        contentStream.moveTo(margin, nextY);
        contentStream.lineTo(margin + Arrays.stream(columnWidths).mapToInt(f -> f).sum(), nextY);
        contentStream.stroke();
    }

    private void drawTableRow(PDPageContentStream contentStream, String[] content, float tableXPosition, float y,
                              float rowHeight, Integer[] columnWidths, boolean isHeader) throws IOException {
        float nextX = tableXPosition; // Usa tableXPosition al posto di margin

        // Aggiungi la linea sopra l'intestazione
        if (isHeader) {
            contentStream.setLineWidth(1f);  // Imposta la larghezza della linea
            contentStream.moveTo(tableXPosition, y);  // Inizio della linea
            contentStream.lineTo(tableXPosition + Arrays.stream(columnWidths).mapToInt(Integer::intValue).sum(), y);  // Fine della linea
            contentStream.stroke();  // Disegna la linea
            contentStream.moveTo(tableXPosition, y);
            contentStream.lineTo(tableXPosition, y - rowHeight);
            contentStream.stroke(); // disegna il bordo a sinistra
        }



        for (int i = 0; i < content.length; i++) {
            // Imposta il font e il colore per l'intestazione o il corpo della tabella
            contentStream.setFont(isHeader ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA, 8);
            contentStream.beginText();

            // Allinea il testo con un margine interno di 5 punti
            contentStream.newLineAtOffset(nextX + 5, y - 15);

            // Mostra il testo della cella (se nullo, mostra una stringa vuota)
            contentStream.showText(content[i] != null ? content[i] : "");

            contentStream.endText();

            // Sposta il puntatore X per disegnare il bordo della cella successiva
            nextX += columnWidths[i];

            // Disegna il bordo della cella
            contentStream.moveTo(nextX, y);
            contentStream.lineTo(nextX, y - rowHeight);
            contentStream.stroke();

            // Se siamo alla colonna "Data" (i == 0), disegna una linea verticale a destra della cella
            if (i == 0) {
                contentStream.moveTo(nextX, y);  // Muoviti alla fine della colonna "Data"
                contentStream.lineTo(nextX, y - rowHeight);  // Disegna il bordo a fianco della cella "Data"
                contentStream.stroke();
            }
        }

        // Disegna una linea sotto la riga
        contentStream.moveTo(tableXPosition, y - rowHeight);
        contentStream.lineTo(nextX, y - rowHeight);
        contentStream.stroke();
    }

    private float drawWrappedRow(PDPageContentStream contentStream, String[] content, float margin, float y, float rowHeight, Integer[] columnWidths) throws IOException {
        float maxHeight = rowHeight;
        float nextX = margin;

        for (int i = 0; i < content.length; i++) {
            // Suddividi il testo in più righe in base alla larghezza della colonna
            List<String> wrappedLines = splitText(content[i], columnWidths[i] - 10);

            // Calcola l'altezza della cella
            float cellHeight = wrappedLines.size() * 10 + 5;
            maxHeight = Math.max(maxHeight, cellHeight);

            // Disegna il testo riga per riga
            float textY = y - 15;
            for (String line : wrappedLines) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 8);
                contentStream.newLineAtOffset(nextX + 5, textY);
                contentStream.showText(line);
                contentStream.endText();
                textY -= 10;
            }

            // Disegna bordi della cella
            contentStream.moveTo(nextX, y);
            contentStream.lineTo(nextX, y - maxHeight);
            contentStream.stroke();

            nextX += columnWidths[i];
        }

        // Chiudi la riga
        contentStream.moveTo(nextX, y);
        contentStream.lineTo(nextX, y - maxHeight);
        contentStream.stroke();

        return y - maxHeight; // Restituisce la nuova posizione Y
    }
    private List<String> splitText(String text, float maxWidth) throws IOException {
        PDFont font = PDType1Font.HELVETICA;
        float fontSize = 8;
        List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();

        for (String word : text.split(" ")) {
            String tempLine = line.length() == 0 ? word : line + " " + word;
            float textWidth = font.getStringWidth(tempLine) / 1000 * fontSize;

            if (textWidth > maxWidth) {
                lines.add(line.toString());
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(tempLine);
            }
        }

        if (line.length() > 0) {
            lines.add(line.toString());
        }

        return lines;
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