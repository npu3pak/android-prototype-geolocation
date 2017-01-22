package ru.npu3pak.citythroughmyeyes.storage.db;

import android.provider.BaseColumns;

public class MarkersSchema {
    public static final String MARKERS_TABLE_NAME = "MARKERS";

    public enum Field implements BaseColumns {
        MARKER_TYPE             ("MARKER_TYPE",             "VARCHAR",   true),  /*Тип маркера*/
        MARKER_COLOR            ("MARKER_COLOR",            "INTEGER",   true),  /*Цвет картинки*/
        MARKER_COMMENT          ("MARKER_COMMENT",          "VARCHAR",   false), /*Подпись к маркеру*/
        MARKER_TIMESTAMP_MILLIS ("MARKER_TIMESTAMP_MILLIS", "INTEGER",   false), /*Дата создания маркера*/
        ADMIN_AREA              ("ADMIN_AREA",              "VARCHAR",   false), /*Регион, область*/
        SUB_ADMIN_AREA          ("SUB_ADMIN_AREA",          "VARCHAR",   false), /*Район*/
        LOCALITY                ("LOCALITY",                "VARCHAR",   false), /*Населенный пункт*/
        SUB_LOCALITY            ("SUB_LOCALITY",            "VARCHAR",   false), /*Район населенного пункта*/
        THOROUGHFARE            ("THOROUGHFARE",            "VARCHAR",   false), /*Улица*/
        SUB_THOROUGHFARE        ("SUB_THOROUGHFARE",        "VARCHAR",   false), /*Переулок чтоли?*/
        PREMISES                ("PREMISES",                "VARCHAR",   false), /*Номер дома, строение*/
        COUNTRY_NAME            ("COUNTRY_NAME",            "VARCHAR",   false), /*Страна*/
        LATITUDE                ("LATITUDE",                "FLOAT",     false), /*Широта*/
        LONGITUDE               ("LONGITUDE",               "FLOAT",     false); /*Долгота*/


        Field(String columnName, String columnType, boolean notNull) {
            this.columnName = columnName;
            this.columnType = columnType;
            this.notNull = notNull;
        }

        public String getDescriptor() {
            return columnName + " " + columnType + (notNull ? " NOT NULL" : "");
        }

        public String toString() {
            return columnName;
        }

        private String columnName;
        private String columnType;
        private boolean notNull;
    }

    public static String getCommaSeparatedDescriptors() {
        return Field.MARKER_TYPE.getDescriptor() + ",\n" +
               Field.MARKER_COLOR.getDescriptor() + ",\n" +
               Field.MARKER_COMMENT.getDescriptor() + ",\n" +
               Field.MARKER_TIMESTAMP_MILLIS.getDescriptor() + ",\n" +
               Field.ADMIN_AREA.getDescriptor() + ",\n" +
               Field.SUB_ADMIN_AREA.getDescriptor() + ",\n" +
               Field.LOCALITY.getDescriptor() + ",\n" +
               Field.SUB_LOCALITY.getDescriptor() + ",\n" +
               Field.THOROUGHFARE.getDescriptor() + ",\n" +
               Field.SUB_THOROUGHFARE.getDescriptor() + ",\n" +
               Field.PREMISES.getDescriptor() + ",\n" +
               Field.COUNTRY_NAME.getDescriptor() + ",\n" +
               Field.LATITUDE.getDescriptor() + ",\n" +
               Field.LONGITUDE.getDescriptor();
    }

    private MarkersSchema() {
    }
}
