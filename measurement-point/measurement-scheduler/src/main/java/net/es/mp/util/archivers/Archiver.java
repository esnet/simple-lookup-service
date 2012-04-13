package net.es.mp.util.archivers;

import java.net.URI;

import net.es.mp.measurement.types.Measurement;

public interface Archiver {
    public URI archive(Measurement measurement);
}
