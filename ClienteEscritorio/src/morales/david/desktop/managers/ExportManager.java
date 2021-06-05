package morales.david.desktop.managers;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.BorderCollapsePropertyValue;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import javafx.stage.FileChooser;
import morales.david.desktop.controllers.schedules.scheduler.SchedulerManager;
import morales.david.desktop.models.Day;
import morales.david.desktop.models.Hour;
import morales.david.desktop.models.SchedulerItem;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class ExportManager {

    private static ExportManager INSTANCE = null;

    public static ExportManager getInstance() {
        if(INSTANCE == null)
            INSTANCE = new ExportManager();
        return INSTANCE;
    }

    public void exportSchedule(List<SchedulerItem> schedules, String searchType, String searchQuery) throws FileNotFoundException {

        if(schedules.size() == 0) return;

        SchedulerManager schedulerManager = new SchedulerManager(schedules, searchType, searchQuery);

        File saveFile = null;
        while (saveFile == null)
            saveFile = getSavePath();

        if(saveFile.exists())
            saveFile.delete();

        PdfWriter pdfWriter = new PdfWriter(saveFile.getAbsolutePath());

        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        pdfDocument.setDefaultPageSize(PageSize.A4);

        Document document = new Document(pdfDocument);

        {

            float columnWidth[] = {75, 105, 105, 105, 105, 105};

            Table schedulerTable = new Table(columnWidth);
            schedulerTable.setHorizontalBorderSpacing(2.5f);
            schedulerTable.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
            schedulerTable.setVerticalBorderSpacing(2.5f);

            // 1º ROW
            {

                schedulerTable.addCell(new Cell().setWidth(25).setMinWidth(25).setBorder(Border.NO_BORDER));

                int i = 1;
                for (Day day : schedulerManager.getCurrentTable().getDays()) {

                    if (i > 5) continue;

                    schedulerTable.addCell(
                        new Cell()
                            .add(new Paragraph(day.getName()))
                            .setPadding(10)
                            .setWidth(70)
                            .setMinWidth(70)
                            .setBackgroundColor(new DeviceRgb(17, 178, 59))
                            .setFontColor(new DeviceRgb(255, 255, 255))
                            .setBorder(Border.NO_BORDER)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    );

                    i++;

                }

            }

            // 2º ROW
            {

                for(Hour hour : schedulerManager.getCurrentTable().getHours()) {

                    String[] parts = hour.getName().split("-");

                    schedulerTable.addCell(
                        new Cell()
                            .add(new Paragraph(parts[0] + "\n" + parts[1]))
                            .setPadding(5)
                            .setWidth(25)
                            .setMinWidth(25)
                            .setBackgroundColor(new DeviceRgb(14, 158, 180))
                            .setFontColor(new DeviceRgb(255, 255, 255))
                            .setBorder(Border.NO_BORDER)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    );

                    for(int i = 0; i < 5; i++) {

                        schedulerTable.addCell(new Cell().setMinWidth(70).setWidth(70).setBorder(Border.NO_BORDER).setPadding(5));

                    }

                }

            }

            document.add(schedulerTable);

        }


        document.close();
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(saveFile.getParentFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private File getSavePath() {

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilter);

        return fileChooser.showSaveDialog(ScreenManager.getInstance().getStage());

    }

}
