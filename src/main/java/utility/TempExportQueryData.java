package utility;

import java.sql.Date;

public class TempExportQueryData {
    private Date date_query;


    public Date getDateQuery() {
        return date_query;
    }


    public boolean buildDate(String date_string) {
        // TODO: test regex string @https://regexr.com
        boolean validFormat = date_string.matches("^[1-9]{4}-((1[0-2])|(0?[1-9]))-\\d{1,2}$");
        if (validFormat) {
            this.date_query = Date.valueOf(date_string);
        }
        return  validFormat;
    }
}
