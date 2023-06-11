package utility;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * Temporary data to be used for creating attendance records.
 */
public class TempAttendanceData {
    private Integer fingerprint_id;
    private Date date_now;
    private Time time_now;
    private String event_name;
    private String event_location;
    private String first_name;


    public Integer getFingerprintID() {
        return fingerprint_id;
    }

    public Date getDateNow() {
        return date_now;
    }

    public Time getTimeNow() {
        return time_now;
    }

    public String getEventName() {
        return event_name;
    }

    public String getEventLocation() {
        return event_location;
    }

    public String getFirstName() {
        return first_name;
    }

    public void setFirstName(String first_name) {
        this.first_name = first_name;
    }

    public void buildAttendanceData(String fingerprint_id, String event_name, String event_location) {
        this.fingerprint_id = Integer.parseInt(fingerprint_id);
        this.date_now = Date.valueOf(LocalDate.now());
        this.time_now = Time.valueOf(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        this.event_name = event_name;
        this.event_location = event_location;
    }
}
