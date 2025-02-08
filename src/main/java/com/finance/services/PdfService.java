package com.finance.services;

import com.finance.entities.Auth.User;
import com.finance.entities.DTO.PdfDTO;
import com.finance.entities.DTO.SummaryItDTO;
import com.finance.entities.Pdf;
import com.finance.repositories.PdfRepository;
import com.finance.utils.BaseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;


import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Service
@AllArgsConstructor
@Slf4j
public class PdfService extends BaseService implements iPdfOperation {

    @Autowired
    private final PdfRepository pdfRepository;


    @Override
    public PDDocument createCustomPdf(String outputPdfPath, PdfDTO fields, User user) throws IOException {
        PDDocument document = new PDDocument(); // creazione del pdf
        PDPage page = new PDPage(); // aggiungiamo la pagina al pdf
        document.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);  // questo permette di aggiungere i contenuti alla pagina linee immagini ecc

        try { /* definiamo il titolo e le dimensioni del titolo*/
            // Dimensioni della pagina
            PDRectangle pageSize = page.getMediaBox();
            float pageWidth = page.getMediaBox().getWidth();
            // Calcolo della larghezza del testo
            String text = "Invoice";
            PDFont font = PDType1Font.HELVETICA_BOLD;
            float fontSize = 18;
            float textWidth = font.getStringWidth(text) / 1000 * fontSize; // textWidth: Calcola la larghezza del testo per centrarlo sulla pagina. La funzione getStringWidth restituisce la larghezza del testo in unità di misura 1000, quindi bisogna fare il calcolo con fontSize per ottenere la larghezza in punti.

            // Calcolo della posizione orizzontale per centrare il testo
            float textX = (pageWidth - textWidth) / 2;
            float textY = 750; // Altezza del testo (puoi personalizzarla)

            // Scrittura del testo
            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.newLineAtOffset(textX, textY);
            contentStream.showText(text);
            contentStream.endText();

            // Campi singoli sempre nella parte alta del testo
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.newLineAtOffset(50, 720);
            contentStream.showText("Mese corrente: " + fields.getMonth());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Totale disponibile per il mese : " + fields.getTotalAvailable());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Totale speso nel mese: " + fields.getTotalSpent());
            contentStream.newLineAtOffset(0, -20); // riga vuota , se aumento questo -20 la nuova riga sara' molto distanziata rispetto a quella sopra
            contentStream.endText();

            /* configurazioni tabella */
            // setto gli header delle celle della tabella
            String[] headers = {"Data", "Descrizione", "TipoEvento", "ValoreInserito", "EuroRisparmiati", "EuroDisponibili", "nuovaColonna"};
            float margin = 50; // Margine laterale
            float tableYPosition = 680 - 50;  // tableYPosition: La posizione verticale di inizio della tabella. Si trova a 680 - 50 (questa è una coordinata Y di partenza per la tabella).
            float rowHeight = 20f; // Altezza standard di ogni riga
            Integer[] columnWidths = {80, 90, 90, 80, 80, 80, 80}; // Specifica le larghezze di ciascuna colonna

            // Calcola la larghezza totale delle colonne
            float totalTableWidth = Arrays.stream(columnWidths).mapToInt(Integer::intValue).sum(); // Calcola la larghezza totale della tabella sommando la larghezza di tutte le colonne.


            // Verifica se la tabella è più larga della pagina, in caso riduci la larghezza delle colonne
            /*Verifica se la larghezza totale della tabella è maggiore della larghezza disponibile sulla pagina (tenendo conto del margine laterale). Se sì, ridimensiona le colonne in modo che la tabella si adatti alla larghezza disponibile della pagina.*/

            if (totalTableWidth > pageWidth - 2 * margin) {
                float scalingFactor = (pageWidth - 2 * margin) / totalTableWidth;
                for (int i = 0; i < columnWidths.length; i++) {
                    columnWidths[i] = (int) (columnWidths[i] * scalingFactor); // Ridimensiona la larghezza delle colonne
                }
            }

            /**
             * Calcola la posizione orizzontale per centrare la tabella sulla pagina. Questo viene fatto sottraendo la larghezza totale della tabella dalla larghezza della pagina e dividendo per 2, in modo che la tabella sia centrata.
             */
            // Calcola la posizione orizzontale per centrare la tabella
            float tableXPosition = (pageWidth - totalTableWidth) / 2;


            // Disegno della tabella
            drawTable(contentStream, headers, fields.getListElement(), margin, tableYPosition, rowHeight, columnWidths, document);

        } catch (Exception e) {

        }
        // Chiudi il ContentStream e salva il documento
        //String fullPath = outputPdfPath + "/output.pdf";
        //document.save(fullPath);


        contentStream.close();
        byte[] pdfBytes = saveDocumentInByteArray(document);
        pdfRepository.save(Pdf.builder()
                .documentProcessed(true)
                .pdfData(pdfBytes)
                .referenceMonth(fields.getMonth())
                .dateSavedPdf(Date.from(Instant.now()))
                .user(user)
                .build());
        document.close();
        return document;

    }


    private void drawTable(PDPageContentStream contentStream, String[] headers, List<SummaryItDTO> rows, float margin,
                           float yStart, float rowHeight, Integer[] columnWidths, PDDocument document) throws IOException {
        float nextY = yStart; // Imposta la coordinata verticale iniziale per la tabella.

        // Disegna header
        drawTableRow(contentStream, headers, margin, nextY, rowHeight, columnWidths, true); // chiama metodo che gestisce ogni singola riga dell'header
        nextY -= rowHeight;  // Aggiorna la posizione Y per la riga successiva, spostandosi verso il basso di un'altezza di riga.

        /**
         * Controlla se la posizione Y è inferiore al margine inferiore della pagina. Se sì, non c'è abbastanza spazio per una nuova riga, quindi:
         * contentStream.close();: Chiude il contentStream per la pagina corrente.
         * Creazione di una nuova pagina:
         * PDPage page = new PDPage();: Crea una nuova pagina.
         * document.addPage(page);: Aggiunge la nuova pagina al documento.
         * Apertura di un nuovo flusso di contenuto:
         * contentStream = new PDPageContentStream(document, page);
         * Reimposta la coordinata nextY: Riporta la posizione Y all'altezza iniziale.
         * Ridisegna l'header sulla nuova pagina: Ogni pagina della tabella inizia con l'intestazione.
         */
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

            String[] rowData = { // crea i dati all'interno della riga
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

        // Configurazione per margini interni e spaziatura
        final float cellPadding = 5; // Margine interno
        final float lineSpacing = 10; // Spazio tra le righe di testo

        for (int i = 0; i < content.length; i++) {
            // Suddividi il testo in più righe in base alla larghezza della colonna
            List<String> wrappedLines = splitText(content[i], columnWidths[i] - 2 * cellPadding);

            // Calcola l'altezza della cella basata sul numero di righe
            float cellHeight = wrappedLines.size() * lineSpacing + cellPadding;
            maxHeight = Math.max(maxHeight, cellHeight);

            // Disegna il testo, riga per riga
            float textY = y - cellPadding - lineSpacing; // Posizione iniziale del testo
            for (String line : wrappedLines) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 8);
                contentStream.newLineAtOffset(nextX + cellPadding, textY);
                contentStream.showText(line);
                contentStream.endText();
                textY -= lineSpacing; // Passa alla riga successiva
            }

            // Disegna i bordi della cella
            contentStream.moveTo(nextX, y); // Bordo sinistro
            contentStream.lineTo(nextX, y - maxHeight);
            contentStream.stroke();

            nextX += columnWidths[i]; // Passa alla colonna successiva
        }

        // Disegna il bordo destro della riga
        contentStream.moveTo(nextX, y);
        contentStream.lineTo(nextX, y - maxHeight);
        contentStream.stroke();

        // Disegna il bordo inferiore della riga
        contentStream.moveTo(margin, y - maxHeight);
        contentStream.lineTo(nextX, y - maxHeight);
        contentStream.stroke();

        return y - maxHeight; // Restituisce la nuova posizione Y
    }

    /**
     * passo in input il testo e la grandezza massima
     */
    private List<String> splitText(String text, float maxWidth) throws IOException {
        PDFont font = PDType1Font.HELVETICA;
        float fontSize = 8;
        List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();

        for (String word : text.split(" ")) {
            // Prova ad aggiungere la parola corrente alla riga
            String tempLine = line.length() == 0 ? word : line + " " + word;
            float textWidth = font.getStringWidth(tempLine) / 1000 * fontSize;

            if (textWidth > maxWidth) {
                // Se la lunghezza eccede maxWidth, aggiungi la riga corrente
                lines.add(line.toString());
                line = new StringBuilder(word);

                // Gestione per parole lunghe
                while (font.getStringWidth(line.toString()) / 1000 * fontSize > maxWidth) {
                    int cutoffIndex = findCutoffIndex(line.toString(), maxWidth, font, fontSize);
                    lines.add(line.substring(0, cutoffIndex));
                    line = new StringBuilder(line.substring(cutoffIndex));
                }
            } else {
                line = new StringBuilder(tempLine);
            }
        }

        // Aggiungi l'ultima riga residua
        if (line.length() > 0) {
            lines.add(line.toString());
        }

        return lines;
    }

    // Metodo migliorato per parole lunghe
    private int findCutoffIndex(String text, float maxWidth, PDFont font, float fontSize) throws IOException {
        int cutoff = text.length(); // Di default, assume che l'intero testo rientri
        for (int i = 1; i <= text.length(); i++) {
            float width = font.getStringWidth(text.substring(0, i)) / 1000 * fontSize;
            if (width > maxWidth) {
                cutoff = i - 1; // Trova il punto di taglio massimo
                break;
            }
        }
        return cutoff > 0 ? cutoff : 1; // Assicura che ci sia almeno 1 carattere per evitare loop infiniti
    }


    private String wrapText(String text, float maxWidth) {
        int maxChars = (int) (maxWidth / 6); // Stima adattata per larghezza cella
        return text.length() > maxChars ? text.substring(0, maxChars - 3) + "..." : text;
    }

    static String getPathDesktop() {
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


    private byte[] saveDocumentInByteArray(PDDocument pdDocument) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        pdDocument.save(byteArrayOutputStream);
        pdDocument.close();
        return byteArrayOutputStream.toByteArray();
    }


    private byte[] retrieveByteArrayFromDB(User user, String month) {
        log.info("Enter into: " + getCurrentClassName() + " start method: " + getCurrentMethodName());
        log.info("Start to retrieve pdf from database");
        try {
            Pdf pdf = pdfRepository.findByUserAndReferenceMonth(user, month);
            return pdf.getPdfData();

        } catch (RuntimeException e) {
            log.error("Error to retrieve dataPdf from database, will be returned a null value");
            return null;
        }
    }


    /**
     * This method retrieve the PDF from the database based of the array of bytes and build again the document
     */
    public PDDocument constructPdfFromDatabase(User user, String month) {
        log.info("Enter into: " + getCurrentClassName() + " start method: " + getCurrentMethodName());
        // Ricostruisce il documento PDF dall'array di byte
        try (PDDocument pdfDocument = PDDocument.load(retrieveByteArrayFromDB(user, month))) {
            log.info("End method: " + getCurrentMethodName());
            return pdfDocument; // Restituisce il documento PDF ricostruito
        } catch (IOException | NullPointerException e){
            log.error("Error to generate Pdf");
            return null;
        }
    }


}