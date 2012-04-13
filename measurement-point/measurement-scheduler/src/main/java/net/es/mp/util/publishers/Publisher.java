package net.es.mp.util.publishers;

import java.net.URI;
import java.util.List;

import net.es.mp.measurement.types.Measurement;
import net.es.mp.scheduler.types.Schedule;

public interface Publisher {
    public URI create(Schedule schedule);
    public void publish(List<Measurement> measurements, String streamURI);
}
