package net.es.mp.util.publishers;

import java.net.URI;
import java.util.List;

import javax.ws.rs.core.UriBuilder;

import com.mongodb.BasicDBObject;

import net.es.mp.authn.LocalAuthnSubject;
import net.es.mp.measurement.types.Measurement;
import net.es.mp.scheduler.types.Schedule;
import net.es.mp.streaming.MPStreamingService;
import net.es.mp.streaming.StreamManager;
import net.es.mp.streaming.types.Stream;

public class LocalStreamPublisher implements Publisher{

    public URI create(Schedule schedule) {
        StreamManager mgr = MPStreamingService.getInstance().getManager();
        Stream stream = new Stream(new BasicDBObject());
        stream.setType(schedule.getType());
        stream.setScheduleURI(schedule.getURI());
        try {
            mgr.createStream(stream, "/mp/streams/", new LocalAuthnSubject());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        
        return UriBuilder.fromUri(stream.getURI()).build();
    }

    public void publish(List<Measurement> measurements, String streamURI) {
        StreamManager mgr = MPStreamingService.getInstance().getManager();
        String id = null;
        String[] uriParts;
        try {
            uriParts = (new URI(streamURI)).getPath().split("/");
            id = uriParts[uriParts.length-1];
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        try {
            mgr.addMeasurements(id, measurements, new LocalAuthnSubject());
        } catch (Exception e) {
            throw new RuntimeException("Not authorized to use local publisher: " + e.getMessage());
        }
    }

}
