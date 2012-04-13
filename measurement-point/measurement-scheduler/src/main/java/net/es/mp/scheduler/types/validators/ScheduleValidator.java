package net.es.mp.scheduler.types.validators;

import java.util.Date;

import net.es.mp.scheduler.types.Schedule;
import net.es.mp.types.validators.MPTypeValidator;

public class ScheduleValidator extends MPTypeValidator{
    public ScheduleValidator(){
        super();
        this.addFieldDef(Schedule.START_TIME, Date.class, true, true);
        this.addFieldDef(Schedule.REPEAT, Integer.class, true, true);
        this.addFieldDef(Schedule.INTERVAL, Long.class, true, true);
    }
}
