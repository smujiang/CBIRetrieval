package retrieval.storage.index.patchs;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import retrieval.server.globaldatabase.GlobalDatabase;
import retrieval.storage.exception.CloseIndexException;
import retrieval.storage.exception.ReadIndexException;
import retrieval.storage.exception.StartIndexException;

/**
 * Created by lrollus on 14/01/15.
 */
public class RedisPatchsIndex implements PicturePatchsIndex{

    /**
     * Store map
     */
    Jedis redis;
    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger(RedisPatchsIndex.class);

    /**
     * Constructor to build a Patchs Map in Memory
     */
    public RedisPatchsIndex(GlobalDatabase global, String idServer)
            throws StartIndexException,ReadIndexException {
        logger.info("JedisPatchsIndex: start");
        try {
            redis = (Jedis)global.getDatabasePatchs();
        }
        catch(Exception e) {
            logger.fatal(e.toString());
            throw new StartIndexException(e.toString());
        }
    }

    /**
     * Delete all image ID key in index
     * @param picturesID Image ID to delete (just look the key)
     */
    public void delete(Map<Long, Integer> picturesID) {
        for (Map.Entry<Long, Integer> entry : picturesID.entrySet()) {
            redis.del(entry.getKey().toString());
        }
    }

    /**
     * Add a new image id and its N value (number of patch extracted to index)
     * @param imageID Image I
     * @param N NI (Number of patch extracted from I to index it)
     */
    public void put(Long imageID, Integer N) {
        redis.set(imageID.toString(), N.toString());
    }

    /**
     * Get the NI value of image I
     * @param imageID I
     * @return Number of patch extracted from I to index it
     */
    public Integer get(Long imageID) {
        String numberOfPatch = redis.get(imageID.toString());
        if (numberOfPatch == null) {
            return -1;

        } else {
            return Integer.parseInt(numberOfPatch);

        }
    }

    /**
     * Check if index contains key
     * @param imagePath Image I
     * @return True if index contains I, else false
     */
    public boolean containsKey(Long imagePath) {
        return get(imagePath)!=-1;
    }

    /**
     * Print index
     */
    public void print() {
        // traverse records
        logger.info("PatchIndex");
        Set<String> keys = redis.keys("*");
        Iterator<String> it = keys.iterator();
        while (it.hasNext()) {
            String key = it.next();
            String value = redis.get(key);
            logger.info(key + "=" + value);

        }
    }

    /**
     * Close index
     * @throws CloseIndexException Error during index close
     */
    public void close() throws CloseIndexException {
        redis.disconnect();

    }

    public void sync() {

    }
}
