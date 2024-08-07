package models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;
import play.data.validation.Constraints;

import java.util.Date;

@Entity
@Table(name="cron_timers")
public class CronTimer extends ModelBase {

    @Getter @Setter
    @Constraints.Required
    public String name;

    @Getter @Setter
    @Constraints.Required
    @Temporal(TemporalType.TIMESTAMP)
    public Date lastRun;

    public CronTimer() {
    }

    public CronTimer(String name, Date lastRun) {
        this.name = name;
        this.lastRun = lastRun;
    }
}
