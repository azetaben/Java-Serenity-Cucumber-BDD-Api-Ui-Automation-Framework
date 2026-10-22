package saucedemo.com.support;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public final class PdfContent {
    private static final Logger log = LoggerFactory.getLogger(PdfContent.class);

    private PdfContent() {
    }

    public static String text(Path file) throws IOException {
        log.debug("Extracting text from downloaded PDF '{}'", file.getFileName());
        try (var document = Loader.loadPDF(file.toFile())) {
            if (document.getNumberOfPages() < 1) throw new IOException("Downloaded PDF has no pages");
            return new PDFTextStripper().getText(document);
        }
    }
}
