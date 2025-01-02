package dns;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class representing a cache of stored DNS records.
 *
 * @version 1.0
 */
public class DNSCache {

    // TODO: fill me in!

    private ArrayList<DNSRecord> records;
    
    public DNSCache() {
        records = new ArrayList<>();
    }

    public void storeRecords(ArrayList<DNSRecord> new_records) {
        for (var record : new_records) {
            if (record.getTypeStr().equals("A")) {
                records.add(record);
            }
        }
    }

    public ArrayList<DNSRecord> getRecords(String name, String type, String rclass) {
        var matches = new ArrayList<DNSRecord>();
        var iterator = records.iterator();
        
        while (iterator.hasNext()) {
            var record = iterator.next();
            if (record.getName().equals(name) &&
                record.getClassStr().equals(rclass) &&
                record.getTypeStr().equals(type)) {
                matches.add(record);
            }
        }
        return matches;
    }

    public void cleanCache() {
        var iterator = records.iterator();
        
        while (iterator.hasNext()) {
            var record = iterator.next();
            var record_age = Duration.between(record.getTimestamp(), Instant.now()).getSeconds();
            if (record_age > record.getTTL()) {
                iterator.remove();
            }
        }
    }

}
