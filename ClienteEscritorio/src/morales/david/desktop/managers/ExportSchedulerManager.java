package morales.david.desktop.managers;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.property.*;
import javafx.stage.FileChooser;
import morales.david.desktop.controllers.schedules.scheduler.SchedulerManager;
import morales.david.desktop.models.*;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExportSchedulerManager {

    private static ExportSchedulerManager INSTANCE = null;

    public static ExportSchedulerManager getInstance() {
        if(INSTANCE == null)
            INSTANCE = new ExportSchedulerManager();
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

        // PAGE HEADER
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

        // SCHEDULE INFO PARAGRAPH
        {

            document.add(
                    new Paragraph(getScheduleInfoText(schedulerManager))
                            .setTextAlignment(TextAlignment.CENTER)
                            .setBackgroundColor(new DeviceRgb(240, 240, 240))
                            .setPadding(5f)
                            .setFontSize(9f)
                            .setMargins(10.5f, 2.5f, 8, 2.5f));

        }

        // SCHEDULE TABLES
        {

            if(schedulerManager.getSearchType().equalsIgnoreCase("TEACHER")) {

                Table morningScheduleTable = getScheduleTable(schedulerManager);

                document.add(morningScheduleTable);

                document.add(new Paragraph("\n"));

                schedulerManager.setMorning(false);

                Table afternoonScheduleTable = getScheduleTable(schedulerManager);

                document.add(afternoonScheduleTable);

            } else {

                boolean first = false;

                if(schedulerManager.getCurrentTimetable().getSchedulerMorning().haveSchedules()) {

                    Table morningScheduleTable = getScheduleTable(schedulerManager);

                    document.add(morningScheduleTable);

                    first = true;

                }

                if(schedulerManager.getCurrentTimetable().getSchedulerAfternoon().haveSchedules()) {

                    if(first) {
                        document.add(new Paragraph("\n"));
                    }

                    schedulerManager.setMorning(false);

                    Table afternoonScheduleTable = getScheduleTable(schedulerManager);

                    document.add(afternoonScheduleTable);

                }

            }

        }

        // SCHEDULE INFO TABLE
        {

            document.add(new Paragraph("\n"));

            Table infoTable = getInfoTable(schedulerManager);
            document.add(infoTable);

        }

        // SCHEDULE TEACHERS LIST
        {

            if(!schedulerManager.getSearchType().equalsIgnoreCase("TEACHER")) {

                document.add(new Paragraph("\n"));
                document.add(getTeacherList(schedulerManager));

            }

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

        boolean multiple = false;
        if(!schedulerManager.getSearchType().equalsIgnoreCase("TEACHER")) {
            if (schedulerManager.getCurrentTimetable().getSchedulerMorning().haveSchedules() &&
                    schedulerManager.getCurrentTimetable().getSchedulerAfternoon().haveSchedules())
                multiple = true;
        }

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

            for(Hour hour : schedulerManager.getCurrentTable().getHours()) {

                String[] parts = hour.getName().split("-");

                String name = "";
                if(schedulerManager.getSearchType().equalsIgnoreCase("TEACHER") || multiple)
                    name = parts[0];
                else
                    name = parts[0] + "\n" + parts[1];

                schedulerTable.addCell(
                        new Cell()
                                .add(new Paragraph(name))
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

                if(!schedulerManager.getSearchType().equalsIgnoreCase("TEACHER") && (hour.getId() == 13 || hour.getId() == 14)) {

                    schedulerTable.addCell(new Cell(1, 5)
                            .add(new Paragraph("R  E  C  R  E  O"))
                            .setPadding(5)
                            .setFontSize(10f)
                            .setBackgroundColor(new DeviceRgb(220, 220, 220))
                            .setBorder(Border.NO_BORDER)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setVerticalAlignment(VerticalAlignment.MIDDLE));

                    continue;

                }

                SchedulerItem[] hourSchedules = schedulerManager.getCurrentTable().getScheduleItemsByHour(hour);

                for(SchedulerItem schedulerItem : hourSchedules) {

                    if (schedulerItem != null && schedulerItem.getScheduleList() != null && schedulerItem.getScheduleList().size() > 0) {

                        if(schedulerItem.getScheduleList().size() == 1) {

                            Cell uniqueCell = getScheduleCell(schedulerManager, schedulerItem.getScheduleList().get(0), multiple);
                            schedulerTable.addCell(uniqueCell);

                        } else {

                            float[] schedulerItemTableWidth = {66f};

                            Table schedulerItemTable = new Table(schedulerItemTableWidth);
                            schedulerItemTable.setPadding(0);
                            schedulerItemTable.setMargin(0);
                            schedulerItemTable.setBorder(Border.NO_BORDER);

                            for(Schedule schedule : schedulerItem.getScheduleList()) {

                                schedulerItemTable.addCell(getScheduleCell(schedulerManager, schedule, true));

                            }

                            Cell multipleCell = new Cell()
                                    .add(schedulerItemTable)
                                    .setBorder(Border.NO_BORDER)
                                    .setMargin(0)
                                    .setPadding(0)
                                    .setHorizontalAlignment(HorizontalAlignment.CENTER)
                                    .setVerticalAlignment(VerticalAlignment.MIDDLE);

                            if(hour.getId() == 13 || hour.getId() == 14)
                                multipleCell.setBackgroundColor(new DeviceRgb(220, 220, 220));
                            else
                                multipleCell.setBackgroundColor(new DeviceRgb(247, 247, 247));

                            schedulerTable.addCell(multipleCell);

                        }

                    } else {

                        Cell emptyCell = new Cell()
                                .add(new Paragraph(""))
                                .setPadding(10)
                                .setWidth(71.5f)
                                .setMinWidth(71f)

                                .setBorder(Border.NO_BORDER)
                                .setTextAlignment(TextAlignment.CENTER)
                                .setVerticalAlignment(VerticalAlignment.MIDDLE);

                        if(hour.getId() == 13 || hour.getId() == 14)
                            emptyCell.setBackgroundColor(new DeviceRgb(220, 220, 220));
                        else
                            emptyCell.setBackgroundColor(new DeviceRgb(234, 234, 234));

                        schedulerTable.addCell(emptyCell);

                    }

                }

            }

        }

        return schedulerTable;

    }

    private Paragraph getTeacherList(SchedulerManager schedulerManager) {

        Paragraph paragraph = new Paragraph();
        paragraph.setFontSize(7.5f);
        paragraph.setMargins(0, 0, 0, 15);

        List<Teacher> teachers = new ArrayList<>();
        teachers.addAll(schedulerManager.getCurrentTimetable().getSchedulerMorning().getTeachers());
        teachers.addAll(schedulerManager.getCurrentTimetable().getSchedulerAfternoon().getTeachers());

        Collections.sort(teachers);

        for(Teacher teacher : teachers) {

            paragraph.add(new Text(teacher.getAbreviation() + " - " + teacher.getName() + "\n"));

        }

        return paragraph;

    }

    private Table getInfoTable(SchedulerManager schedulerManager) {

        float columnWidth[] = {150f, 150f};

        Table infoTable = new Table(columnWidth);
        infoTable.setHorizontalBorderSpacing(15f);
        infoTable.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);

        if(schedulerManager.getSearchType().equalsIgnoreCase("TEACHER")) {

            Cell unique = getInfoCell();
            unique.add(new Paragraph("Asignatura   Aula\nGrupo"));
            infoTable.addCell(unique);

            Cell multiple = getInfoCell();
            multiple.add(new Paragraph("Asignatura   Aula   Grupo"));
            infoTable.addCell(multiple);


        } else if(schedulerManager.getSearchType().equalsIgnoreCase("GROUP")) {

            Cell unique = getInfoCell();
            unique.add(new Paragraph("Profesor\nAsignatura   Aula"));
            infoTable.addCell(unique);

            Cell multiple = getInfoCell();
            multiple.add(new Paragraph("Profesor   Asignatura   Aula"));
            infoTable.addCell(multiple);


        } else if(schedulerManager.getSearchType().equalsIgnoreCase("CLASSROOM")) {

            Cell unique = getInfoCell();
            unique.add(new Paragraph("Profesor\nAsignatura   Aula\nGrupo"));
            infoTable.addCell(unique);

            Cell multiple = getInfoCell();
            multiple.add(new Paragraph("Profesor   Asignatura   Aula   Grupo"));
            infoTable.addCell(multiple);


        }

        return infoTable;

    }

    private Cell getScheduleCell(SchedulerManager schedulerManager, Schedule schedule, boolean multiple) {

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

    private Cell getInfoCell() {

        Cell cell = new Cell();
        cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
        cell.setHorizontalAlignment(HorizontalAlignment.CENTER);
        cell.setTextAlignment(TextAlignment.CENTER);
        cell.setWidth(140f);
        cell.setMinWidth(140f);
        cell.setBorder(Border.NO_BORDER);
        cell.setBackgroundColor(new DeviceRgb(235, 235, 235));
        cell.setPaddings(5, 7, 5, 7);
        cell.setFontSize(8.5f);

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
