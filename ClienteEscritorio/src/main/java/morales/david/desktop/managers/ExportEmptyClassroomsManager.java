package morales.david.desktop.managers;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.BorderCollapsePropertyValue;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import javafx.stage.FileChooser;
import morales.david.desktop.models.*;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

public class ExportEmptyClassroomsManager {

    private static ExportEmptyClassroomsManager INSTANCE = null;

    public static ExportEmptyClassroomsManager getInstance() {
        if(INSTANCE == null)
            INSTANCE = new ExportEmptyClassroomsManager();
        return INSTANCE;
    }

    public void exportTimeZone(TimeZone timeZone, List<Classroom> classrooms) throws FileNotFoundException, MalformedURLException {

        if(classrooms.size() == 0) return;

        File saveFile = getSavePath();
        if(saveFile == null)
            return;

        if(saveFile.exists())
            saveFile.delete();

        PdfWriter pdfWriter = new PdfWriter(saveFile.getAbsolutePath());

        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        pdfDocument.setDefaultPageSize(PageSize.A4);

        Document document = new Document(pdfDocument);

        // PAGE HEADER
        {

            float[] headerColumnWidth = { 280f, 280f };

            Table headerTable = new Table(headerColumnWidth);

            headerTable.setMargins(0, 2.5f, 0, 2.5f);
            headerTable.setBackgroundColor(new DeviceRgb(230, 5, 71));

            headerTable.addCell(
                    new Cell()
                            .add(new Image(ImageDataFactory.create(getClass().getResource("/images/logo@x1.png"))).setWidth(50).setHeight(16))
                            .setHorizontalAlignment(HorizontalAlignment.LEFT)
                            .setVerticalAlignment(VerticalAlignment.MIDDLE)
                            .setPaddingTop(7f)
                            .setPaddingBottom(7f)
                            .setPaddingLeft(7f)
                            .setBorder(Border.NO_BORDER)

            );

            headerTable.addCell(
                    new Cell()
                            .add(new Paragraph("I.E.S Fernando Aguilar Quignon"))
                            .setTextAlignment(TextAlignment.RIGHT)
                            .setVerticalAlignment(VerticalAlignment.MIDDLE)
                            .setPaddingTop(7f)
                            .setPaddingBottom(7f)
                            .setPaddingRight(15f)
                            .setFontSize(10f)
                            .setFontColor(new DeviceRgb(255, 255, 255))
                            .setBorder(Border.NO_BORDER)

            );

            document.add(headerTable);

        }

        // SCHEDULE INFO PARAGRAPH
        {

            document.add(
                    new Paragraph("Aulas libres para el " + timeZone)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setBackgroundColor(new DeviceRgb(240, 240, 240))
                            .setPadding(5f)
                            .setFontSize(9f)
                            .setMargins(10.5f, 2.5f, 8, 2.5f));

        }

        // SCHEDULE TABLES
        {

            Table classroomsTable = getClassroomsTable(classrooms);
            document.add(classroomsTable);

        }

        document.close();

        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(saveFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private Table getClassroomsTable(List<Classroom> classrooms) {

        float columnWidth[] = {80f, 80f, 80f, 80f, 80f};

        Table classroomsTable = new Table(columnWidth);
        classroomsTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
        classroomsTable.setHorizontalBorderSpacing(15f);
        classroomsTable.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        classroomsTable.setVerticalBorderSpacing(15f);

        {

            for(Classroom classroom : classrooms) {

                classroomsTable.addCell(getClassroomCell(classroom));

            }

        }

        return classroomsTable;

    }

    private Cell getClassroomCell(Classroom classroom) {

        Cell cell = new Cell();
        cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
        cell.setHorizontalAlignment(HorizontalAlignment.CENTER);
        cell.setTextAlignment(TextAlignment.CENTER);
        cell.setWidth(70f);
        cell.setMinWidth(70f);
        cell.setBorder(Border.NO_BORDER);
        cell.setBackgroundColor(new DeviceRgb(194, 255, 239));
        cell.add(new Paragraph(classroom.getName()));
        cell.setPadding(7f);
        cell.setFontSize(9f);

        return cell;

    }

    private File getSavePath() {

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilter);

        return fileChooser.showSaveDialog(ScreenManager.getInstance().getStage());

    }

}
