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
import com.itextpdf.layout.property.*;
import javafx.stage.FileChooser;
import morales.david.desktop.models.*;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExportInspectorManager {

    private static ExportInspectorManager INSTANCE = null;

    public static ExportInspectorManager getInstance() {
        if(INSTANCE == null)
            INSTANCE = new ExportInspectorManager();
        return INSTANCE;
    }

    private static File initialDirectory;

    public void exportSchedule(List<ScheduleTurn> schedules, TimeZone timeZone, boolean openOnFinish, File exportDirectory) throws FileNotFoundException {

        if(schedules.size() == 0) return;

        List<ScheduleTurn> scheduleTurns = sortSchedules(schedules);

        File saveFile;

        if(exportDirectory == null) {

            saveFile = getSavePath(timeZone);
            if (saveFile == null)
                return;

            if(saveFile.getParentFile().isDirectory())
                initialDirectory = saveFile.getParentFile();

        } else {

            saveFile = new File(exportDirectory, "parte_guardia_" + timeZone.getDay().getId() + "_" + timeZone.getHour().getId() + ".pdf");

        }

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

        document.add(new Paragraph("").setTextAlignment(TextAlignment.CENTER).setPadding(5f));

        // SCHEDULE INFO PARAGRAPH
        {

            float[] infoColumnWidth = { 300f, 40f, 200f, 20f };

            Table infoTable = new Table(infoColumnWidth);

            infoTable.setMargins(0, 2.5f, 0, 2.5f);

            Cell emptyCell = new Cell()
                    .add(new Paragraph(""))
                    .setBorder(Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE);

            infoTable.addCell(
                    new Cell()
                            .add(new Paragraph("PARTE DE GUARDIA"))
                            .setTextAlignment(TextAlignment.LEFT)
                            .setVerticalAlignment(VerticalAlignment.MIDDLE)
                            .setPaddingTop(7f)
                            .setPaddingBottom(0f)
                            .setPaddingLeft(15f)
                            .setBold()
                            .setFontSize(14f)
                            .setFontColor(new DeviceRgb(27, 19, 66))
                            .setBorder(Border.NO_BORDER)

            );

            infoTable.addCell(
                    new Cell()
                            .add(new Paragraph("Fecha:"))
                            .setTextAlignment(TextAlignment.RIGHT)
                            .setVerticalAlignment(VerticalAlignment.MIDDLE)
                            .setPaddingTop(7f)
                            .setPaddingBottom(0f)
                            .setPaddingRight(15f)
                            .setFontSize(11f)
                            .setFontColor(new DeviceRgb(27, 19, 66))
                            .setBorder(Border.NO_BORDER)

            );

            infoTable.addCell(
                    new Cell()
                            .add(new Paragraph(""))
                            .setVerticalAlignment(VerticalAlignment.MIDDLE)
                            .setMarginTop(7f)
                            .setMarginBottom(0f)
                            .setFontSize(10f)
                            .setFontColor(new DeviceRgb(27, 19, 66))

            );

            infoTable.addCell(emptyCell);

            infoTable.addCell(
                    new Cell()
                            .add(new Paragraph(getScheduleInfoText(timeZone)))
                            .setTextAlignment(TextAlignment.LEFT)
                            .setVerticalAlignment(VerticalAlignment.TOP)
                            .setPaddingBottom(7f)
                            .setPaddingLeft(15f)
                            .setFontSize(10f)
                            .setFontColor(new DeviceRgb(27, 19, 66))
                            .setBorder(Border.NO_BORDER)

            );

            infoTable.addCell(emptyCell);
            infoTable.addCell(emptyCell);
            infoTable.addCell(emptyCell);

            document.add(infoTable);

        }

        document.add(new Paragraph("").setTextAlignment(TextAlignment.CENTER).setPadding(5f));

        // SCHEDULE TABLE
        {

            Table schedulerTable = getScheduleTable(scheduleTurns);

            document.add(schedulerTable);

        }

        document.close();

        if(openOnFinish) {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(saveFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private Table getScheduleTable(List<ScheduleTurn> scheduleTurns) {

        float columnWidth[] = {30f, 30f, 150f, 10f, 30f, 30f, 150f};

        Table schedulerTable = new Table(columnWidth);
        schedulerTable.setHorizontalBorderSpacing(2.8f);
        schedulerTable.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        schedulerTable.setVerticalBorderSpacing(2.8f);

        TextAlignment textAlignment = TextAlignment.CENTER;
        Float fontSize = 7f;
        Float padding = 2f;

        // 1º ROW
        {

            DeviceRgb deviceRgb = new DeviceRgb(86, 154, 218);

            schedulerTable.addHeaderCell(
                    new Cell()
                            .add(new Paragraph("AULA"))
                                    .setPaddings(5f, 5f, 5f, 5f)
                                    .setWidth(30f)
                                    .setMinWidth(29f)
                                    .setFontSize(8f)
                                    .setBold()
                                    .setBackgroundColor(deviceRgb)
                                    .setFontColor(new DeviceRgb(255, 255, 255))
                                    .setBorder(Border.NO_BORDER)
                                    .setTextAlignment(textAlignment)
                                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        );

            schedulerTable.addHeaderCell(
                    new Cell()
                            .add(new Paragraph("PROF"))
                                    .setPaddings(5f, 5f, 5f, 5f)
                                    .setWidth(30f)
                                    .setMinWidth(29f)
                                    .setFontSize(8f)
                                    .setBold()
                                    .setBackgroundColor(deviceRgb)
                                    .setFontColor(new DeviceRgb(255, 255, 255))
                                    .setBorder(Border.NO_BORDER)
                                    .setTextAlignment(textAlignment)
                                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                            );

            schedulerTable.addHeaderCell(
                    new Cell()
                            .add(new Paragraph("NOMBRE"))
                                    .setPaddings(5f, 5f, 5f, 5f)
                                    .setWidth(150f)
                                    .setMinWidth(149f)
                                    .setFontSize(8f)
                                    .setBold()
                                    .setBackgroundColor(deviceRgb)
                                    .setFontColor(new DeviceRgb(255, 255, 255))
                                    .setBorder(Border.NO_BORDER)
                                    .setTextAlignment(textAlignment)
                                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                            );

            schedulerTable.addHeaderCell(
                    new Cell()
                            .add(new Paragraph("C"))
                                    .setPaddings(5f, 5f, 5f, 5f)
                                    .setWidth(10f)
                                    .setMinWidth(9f)
                                    .setFontSize(8f)
                                    .setBold()
                                    .setBackgroundColor(deviceRgb)
                                    .setFontColor(new DeviceRgb(255, 255, 255))
                                    .setBorder(Border.NO_BORDER)
                                    .setTextAlignment(textAlignment)
                                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                            );

            schedulerTable.addHeaderCell(
                    new Cell()
                            .add(new Paragraph("ETAPA"))
                                    .setPaddings(5f, 5f, 5f, 5f)
                                    .setWidth(30f)
                                    .setMinWidth(29f)
                                    .setFontSize(8f)
                                    .setBold()
                                    .setBackgroundColor(deviceRgb)
                                    .setFontColor(new DeviceRgb(255, 255, 255))
                                    .setBorder(Border.NO_BORDER)
                                    .setTextAlignment(textAlignment)
                                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                            );

            schedulerTable.addHeaderCell(
                    new Cell()
                            .add(new Paragraph("P/A/R"))
                                    .setPaddings(5f, 5f, 5f, 5f)
                                    .setWidth(30f)
                                    .setMinWidth(29f)
                                    .setFontSize(8f)
                                    .setBold()
                                    .setBackgroundColor(deviceRgb)
                                    .setFontColor(new DeviceRgb(255, 255, 255))
                                    .setBorder(Border.NO_BORDER)
                                    .setTextAlignment(textAlignment)
                                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                            );

            schedulerTable.addHeaderCell(
                    new Cell()
                            .add(new Paragraph("OBSERVACIONES / FIRMA"))
                                    .setPaddings(5f, 5f, 5f, 5f)
                                    .setWidth(150f)
                                    .setMinWidth(149f)
                                    .setFontSize(8f)
                                    .setBold()
                                    .setBackgroundColor(deviceRgb)
                                    .setFontColor(new DeviceRgb(255, 255, 255))
                                    .setBorder(Border.NO_BORDER)
                                    .setTextAlignment(textAlignment)
                                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                            );

        }

        // SCHEDULE ROWS
        {

            for(ScheduleTurn scheduleTurn : scheduleTurns) {

                if(scheduleTurn == null)
                    continue;

                schedulerTable.addCell(
                        new Cell()
                                .add(new Paragraph(scheduleTurn.getClassroom() != null ? scheduleTurn.getClassroom().getName() : ""))
                                .setPaddings(padding, 5f, 5f, padding)
                                .setWidth(30f)
                                .setFontSize(fontSize)
                                .setFontColor(new DeviceRgb(0, 0, 0))
                                .setBorder(Border.NO_BORDER)
                                .setTextAlignment(textAlignment)
                                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                );

                schedulerTable.addCell(
                        new Cell()
                                .add(new Paragraph(scheduleTurn.getTeacher() != null ? scheduleTurn.getTeacher().getAbreviation() : ""))
                                .setPaddings(padding, 5f, 5f, padding)
                                .setWidth(30f)
                                .setFontSize(fontSize)
                                .setFontColor(new DeviceRgb(0, 0, 0))
                                .setBorder(Border.NO_BORDER)
                                .setTextAlignment(textAlignment)
                                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                );

                schedulerTable.addCell(
                        new Cell()
                                .add(new Paragraph(scheduleTurn.getTeacher() != null ? scheduleTurn.getTeacher().getName() : ""))
                                .setPaddings(padding, 5f, 5f, padding)
                                .setWidth(150f)
                                .setFontSize(fontSize)
                                .setFontColor(new DeviceRgb(0, 0, 0))
                                .setBorder(Border.NO_BORDER)
                                .setTextAlignment(textAlignment)
                                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                );

                schedulerTable.addCell(
                        new Cell()
                                .add(new Paragraph(scheduleTurn.getCourse() != null ? scheduleTurn.getCourse().getLevel() : ""))
                                .setPaddings(padding, 5f, 5f, padding)
                                .setWidth(10f)
                                .setFontSize(fontSize)
                                .setFontColor(new DeviceRgb(0, 0, 0))
                                .setBorder(Border.NO_BORDER)
                                .setTextAlignment(textAlignment)
                                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                );

                schedulerTable.addCell(
                        new Cell()
                                .add(new Paragraph(scheduleTurn.getCourse() != null ? scheduleTurn.getCourse().getName() : ""))
                                .setPaddings(padding, 5f, 5f, padding)
                                .setWidth(30f)
                                .setFontSize(fontSize)
                                .setFontColor(new DeviceRgb(0, 0, 0))
                                .setBorder(Border.NO_BORDER)
                                .setTextAlignment(textAlignment)
                                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                );

                schedulerTable.addCell(
                        new Cell()
                                .add(new Image(ImageDataFactory.create(getClass().getResource("/images/blue-square.png"))).setWidth(12).setHeight(12))
                                .setPaddings(0f, 0f, 0f, 14f)
                                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                                .setBorder(Border.NO_BORDER)

                );

                schedulerTable.addCell(
                        new Cell()
                                .add(new Paragraph(""))
                                .setPaddings(padding, 5f, 5f, padding)
                                .setWidth(150f)
                                .setTextAlignment(textAlignment)
                                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                );

            }

        }

        return schedulerTable;

    }

    private String getScheduleInfoText(TimeZone timeZone) {

        return capitalizeFirstLetter(timeZone.getDay().getName()) + "   " + timeZone.getHour().getName();

    }

    public String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    public List<ScheduleTurn> sortSchedules(List<ScheduleTurn> schedules) {

        List<ScheduleTurn> finalList = new ArrayList<>();
        List<ScheduleTurn> normalList = new ArrayList<>();
        List<ScheduleTurn> gList = new ArrayList<>();
        List<ScheduleTurn> blankList = new ArrayList<>();

        for(ScheduleTurn schedule : schedules) {

            if(schedule.getClassroom() == null || schedule.getClassroom().getName().equalsIgnoreCase(""))
                blankList.add(schedule);

            else if(schedule.getClassroom().getName().equalsIgnoreCase("G"))
                gList.add(schedule);

            else
                normalList.add(schedule);

        }

        for(ScheduleTurn scheduleTurn : normalList)
            if(!existTeacher(finalList, scheduleTurn.getTeacher()))
                finalList.add(scheduleTurn);

        for(ScheduleTurn scheduleTurn : gList)
            if(!existTeacher(finalList, scheduleTurn.getTeacher()))
                finalList.add(scheduleTurn);

        for(ScheduleTurn scheduleTurn : blankList)
            if(!existTeacher(finalList, scheduleTurn.getTeacher()))
                finalList.add(scheduleTurn);

        return finalList;

    }

    private boolean existTeacher(List<ScheduleTurn> list, Teacher teacher) {

        boolean finded = false;

        for(ScheduleTurn scheduleTurn : list) {

            if(scheduleTurn.getTeacher() == null || teacher == null)
                continue;

            if(scheduleTurn.getTeacher().getAbreviation().equalsIgnoreCase(teacher.getAbreviation()))
                finded = true;

        }

        return finded;

    }

    private File getSavePath(TimeZone timeZone) {

        FileChooser fileChooser = new FileChooser();

        if(initialDirectory != null)
            fileChooser.setInitialDirectory(initialDirectory);

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialFileName("parte_guardia_" + timeZone.getDay().getId() + "_" + timeZone.getHour().getId() + ".pdf");

        return fileChooser.showSaveDialog(ScreenManager.getInstance().getStage());

    }

}
