package ua.petproject.annotations;

public enum DataTypeForFields {
    STRING("VARCHAR"),
    CHAR("VARCHAR"),
    CHARACTER("VARCHAR"),
    BOOLEAN("BIT"),
    INT("INT"),
    INTEGER("INT"),
    LONG("INT"),
    BIGINT("INT"),
    SHORT("SMALLINT"),
    DOUBLE("REAL"),
    FLOAT("REAL"),
    BYTE("BINARY");

    private String correspondingSqlType;

    DataTypeForFields(String correspondingSqlType){
        this.correspondingSqlType = correspondingSqlType.toUpperCase();
    }

    public String getValue() {
        return correspondingSqlType;
    }
}
