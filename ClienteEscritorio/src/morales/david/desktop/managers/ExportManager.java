package morales.david.desktop.managers;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.BorderCollapsePropertyValue;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import com.jfoenix.controls.JFXButton;
import javafx.stage.FileChooser;
import morales.david.desktop.controllers.schedules.scheduler.SchedulerManager;
import morales.david.desktop.models.Day;
import morales.david.desktop.models.Hour;
import morales.david.desktop.models.Schedule;
import morales.david.desktop.models.SchedulerItem;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

public class ExportManager {

    private static ExportManager INSTANCE = null;

    public static ExportManager getInstance() {
        if(INSTANCE == null)
            INSTANCE = new ExportManager();
        return INSTANCE;
    }

    public void exportSchedule(List<SchedulerItem> schedules, String searchType, String searchQuery) throws FileNotFoundException, MalformedURLException {

        if(schedules.size() == 0) return;

        SchedulerManager schedulerManager = new SchedulerManager(schedules, searchType, searchQuery);
        schedulerManager.setMorning(true);

        File saveFile = getSavePath();
        if(saveFile == null)
            return;

        if(saveFile.exists())
            saveFile.delete();

        PdfWriter pdfWriter = new PdfWriter(saveFile.getAbsolutePath());

        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        pdfDocument.setDefaultPageSize(PageSize.A4);

        Document document = new Document(pdfDocument);

        {

            float[] headerColumnWidth = { 280f, 280f };

            Table headerTable = new Table(headerColumnWidth);

            headerTable.setMargins(0, 2.5f, 0, 2.5f);
            headerTable.setBackgroundColor(new DeviceRgb(230, 5, 71));

            headerTable.addCell(
                    new Cell()
                            .add(new Image(ImageDataFactory.create("src/resources/images/logo@x1.png")).setWidth(50).setHeight(16))
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

        document.add(
                new Paragraph(getScheduleInfoText(schedulerManager))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBackgroundColor(new DeviceRgb(240, 240, 240))
                        .setPadding(5f)
                        .setFontSize(9f)
                        .setMargins(10.5f, 2.5f, 8, 2.5f));

        {

            Table morningScheduleTable = getScheduleTable(schedulerManager);

            document.add(morningScheduleTable);

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

    private Table getScheduleTable(SchedulerManager schedulerManager) {

        float columnWidth[] = {40f, 71.5f, 71.5f, 71.5f, 71.5f, 71.5f};

        Table schedulerTable = new Table(columnWidth);
        schedulerTable.setHorizontalBorderSpacing(2.5f);
        schedulerTable.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        schedulerTable.setVerticalBorderSpacing(2.5f);

        // 1º ROW
        {

            schedulerTable.addCell(
                    new Cell()
                            .add(new Paragraph(""))
                            .setPadding(5)
                            .setWidth(40)
                            .setMinWidth(40)
                            .setBackgroundColor(new DeviceRgb(17, 178, 59))
                            .setBorder(Border.NO_BORDER)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setVerticalAlignment(VerticalAlignment.MIDDLE)
            );

            int i = 1;
            for (Day day : schedulerManager.getCurrentTable().getDays()) {

                if (i > 5) continue;

                schedulerTable.addCell(
                        new Cell()
                                .add(new Paragraph(day.getName()))
                                .setPaddings(5f, 10f, 5f, 10f)
                                .setWidth(71.5f)
                                .setMinWidth(71f)
                                .setFontSize(8f)
                                .setBackgroundColor(new DeviceRgb(17, 178, 59))
                                .setFontColor(new DeviceRgb(255, 255, 255))
                                .setBorder(Border.NO_BORDER)
                                .setTextAlignment(TextAlignment.CENTER)
                                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                );

                i++;

            }

        }

        {

            int hourindex = 0;
            for(Hour hour : schedulerManager.getCurrentTable().getHours()) {

                String[] parts = hour.getName().split("-");

                schedulerTable.addCell(
                        new Cell()
                                .add(new Paragraph(parts[0] + "\n" + parts[1]))
                                .setPadding(5)
                                .setWidth(40)
                                .setMinWidth(40)
                                .setBackgroundColor(new DeviceRgb(14, 158, 180))
                                .setFontColor(new DeviceRgb(255, 255, 255))
                                .setFontSize(8f)
                                .setBorder(Border.NO_BORDER)
                                .setTextAlignment(TextAlignment.CENTER)
                                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                );

                if(hour.getId() == 13 || hour.getId() == 14) {

                    schedulerTable.addCell(new Cell(1, 5)
                            .add(new Paragraph("R  E  C  R  E  O"))
                            .setPadding(5)
                            .setBackgroundColor(new DeviceRgb(234, 234, 234))
                            .setBorder(Border.NO_BORDER)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setVerticalAlignment(VerticalAlignment.MIDDLE));

                    continue;

                }

                SchedulerItem[] hourSchedules = schedulerManager.getCurrentTable().getScheduleItemsByHour(hourindex);

                for(SchedulerItem schedulerItem : hourSchedules) {

                    if (schedulerItem != null && schedulerItem.getScheduleList() != null && schedulerItem.getScheduleList().size() > 0) {

                        if(schedulerItem.getScheduleList().size() == 1) {

                            schedulerTable.addCell(getCell(schedulerManager, schedulerItem.getScheduleList().get(0), false));

                        } else {

                            float[] schedulerItemTableWidth = {66f};

                            Table schedulerItemTable = new Table(schedulerItemTableWidth);
                            schedulerItemTable.setPadding(0);
                            schedulerItemTable.setMargin(0);
                            schedulerItemTable.setBorder(Border.NO_BORDER);

                            for(Schedule schedule : schedulerItem.getScheduleList()) {

                                schedulerItemTable.addCell(getCell(schedulerManager, schedule, true));

                            }

                            schedulerTable.addCell(
                                    new Cell()
                                        .add(schedulerItemTable)
                                        .setBackgroundColor(new DeviceRgb(247, 247, 247))
                                        .setBorder(Border.NO_BORDER)
                                        .setMargin(0)
                                        .setPadding(0)
                                        .setHorizontalAlignment(HorizontalAlignment.CENTER)
                                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                            );

                        }

                    } else {

                        schedulerTable.addCell(
                                new Cell()
                                        .add(new Paragraph(""))
                                        .setPadding(10)
                                        .setWidth(71.5f)
                                        .setMinWidth(71f)
                                        .setBorder(Border.NO_BORDER)
                                        .setTextAlignment(TextAlignment.CENTER)
                                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        );

                    }

                }

                hourindex++;

            }

        }

        return schedulerTable;

    }

    private Cell getCell(SchedulerManager schedulerManager, Schedule schedule, boolean multiple) {

        Cell cell = new Cell();
        cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
        cell.setHorizontalAlignment(HorizontalAlignment.CENTER);
        cell.setTextAlignment(TextAlignment.CENTER);
        cell.setWidth(65.5f);
        cell.setMinWidth(65.5f);
        cell.setBorder(Border.NO_BORDER);
        cell.setBackgroundColor(new DeviceRgb(java.awt.Color.decode(schedule.getSubject().getColor())));
        cell.add(new Paragraph(schedule.getText(schedulerManager.getSearchTypeNumber(), multiple)));

        cell.setPaddings(5, 7, 5, 7);

        if(multiple) {
            cell.setFontSize(5f);
        } else {
            cell.setFontSize(6f);
        }

        return cell;

    }

    private String getScheduleInfoText(SchedulerManager schedulerManager) {

        String result = "";

        String type = schedulerManager.getSearchType();
        String query = schedulerManager.getSearchQuery();

        if(type.equalsIgnoreCase("TEACHER"))
            result = "Horario del profesor/profesora, " + query;
        else if(type.equalsIgnoreCase("GROUP"))
            result = "Horario del grupo, " + query;
        else if(type.equalsIgnoreCase("CLASSROOM"))
            result = "Horario del aula, " + query;

        return result;

    }

    private File getSavePath() {

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilter);

        return fileChooser.showSaveDialog(ScreenManager.getInstance().getStage());

    }

}
