package net.es.mp.scheduler.types.validators;

import java.util.Date;

import com.mongodb.BasicDBList;

import net.es.mp.scheduler.types.Schedule;
import net.es.mp.types.validators.BasicDBListValidator;
import net.es.mp.types.validators.MPTypeValidator;
import net.es.mp.types.validators.URIValidator;

public class ScheduleValidator extends MPTypeValidator{
    public ScheduleValidator(){
        super();
        this.addFieldDef(Schedule.START_TIME, Date.class, true, true);
        this.addFieldDef(Schedule.REPEAT, Integer.class, true, true);
        this.addFieldDef(Schedule.INTERVAL, Long.class, true, true);
        this.addFieldDef(Schedule.CALLBACK_URIS, BasicDBList.class, true, true, 
            new BasicDBListValidator(new URIValidator()));
    }
}
