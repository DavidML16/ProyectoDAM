package morales.david.desktop.models;

import com.google.gson.internal.LinkedTreeMap;

public class ExportableItem {

    private String exportType;
    private String exportQuery;

    private Object item;

    public ExportableItem(String exportType, String exportQuery, Object item) {
        this.exportType = exportType;
        this.exportQuery = exportQuery;
        this.item = item;
    }

    public String getExportType() {
        return exportType;
    }

    public void setExportType(String exportType) {
        this.exportType = exportType;
    }

    public String getExportQuery() {
        return exportQuery;
    }

    public void setExportQuery(String exportQuery) {
        this.exportQuery = exportQuery;
    }

    public Object getItem() {
        return item;
    }

    public void setItem(Object item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return "ExportableItem{" +
                "exportType='" + exportType + '\'' +
                ", exportQuery='" + exportQuery + '\'' +
                ", item=" + item +
                '}';
    }

    public static ExportableItem parse(LinkedTreeMap map) {

        String exportType = (String) map.get("exportType");
        String exportQuery = (String) map.get("exportQuery");

        Object item = null;

        if(exportType.equalsIgnoreCase("TEACHER")) {

            LinkedTreeMap teacherMap = (LinkedTreeMap) map.get("item");

            item = Teacher.parse(teacherMap);

        } else if(exportType.equalsIgnoreCase("GROUP")) {

            LinkedTreeMap groupMap = (LinkedTreeMap) map.get("item");

            item = Group.parse(groupMap);


        } else if(exportType.equalsIgnoreCase("CLASSROOM")) {

            LinkedTreeMap classroomMap = (LinkedTreeMap) map.get("item");

            item = Classroom.parse(classroomMap);
            
        }

        return new ExportableItem(exportType, exportQuery, item);

    }

}
