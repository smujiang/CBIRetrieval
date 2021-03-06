/*
 * Copyright 2015 ROLLUS Loïc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package retrieval.storage.index;

import org.apache.log4j.Logger;
import retrieval.config.ConfigServer;
import retrieval.dist.RequestPictureVisualWord;
import retrieval.server.globaldatabase.GlobalDatabase;
import retrieval.storage.exception.AlreadyIndexedException;
import retrieval.storage.exception.CloseIndexException;
import retrieval.storage.exception.NoValidPictureException;
import retrieval.storage.exception.PictureTooHomogeneous;
import retrieval.testvector.TestVectorListServer;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is an abstract class for index
 * This implements a hight level index, it means that you can
 * add pictures, delete pictures, get similar pictures...
 * @author Rollus Loic
 */
public abstract class Index {
    
    protected String idStorage;
    
    protected GlobalDatabase database;
    
    private static Logger logger = Logger.getLogger(Index.class);
    protected TestVectorListServer testVectors;
    /**
     * Picture index
     */
    protected PictureIndex pictureIndex;
    
    /**
     * Pictures that must be delete from index (already delete from server)
     */
    protected PicturesToPurge picturesToPurge;
    
    /**
     * Add a picture to index
     * @param image Image to index
     * @param id Image id
     * @param properties Image properties
     * @param N Number of patches
     * @param resizeMethod Method for resizing patches
     * @param sizeOfPatchW Size of patch (w)
     * @param sizeOfPatchH Size of patch (h)
     * @param sync Use sync or async method
     * @throws AlreadyIndexedException Picture is already indexed
     * @throws NoValidPictureException Picture is not valid
     */
    public abstract Long addPicture(
            BufferedImage image,
            Long id,
            Map<String,String> properties,
            int N,
            int resizeMethod,
            int sizeOfPatchW,
            int sizeOfPatchH,
            boolean sync)
            throws AlreadyIndexedException, NoValidPictureException, PictureTooHomogeneous;

    /**
     * Fill structure in argument with nbt an return it
     * Central server will ask NBT for this server during the search process.
     * @param visualWordsByTestVector Map of visual words for each tests vector
     * @return Map of visual words and their NBT for each tests vector
     */
    public abstract List<ConcurrentHashMap<String, Long>> fillNBT(List<ConcurrentHashMap<String, Long>> visualWordsByTestVector);

    /**
     * Compute similarity thanks to structure in argument and Niq
     * @param visualWordsByTestVector Visual words for request picture IQ
     * @param Niq Number of patch generated by Iq
     * @return Ordered lists of similar pictures (ordered by similarities with Iq)
     */
    public abstract List<ResultSim> computeSimilarity(List<ConcurrentHashMap<String, RequestPictureVisualWord>> visualWordsByTestVector,int Niq);
//    public abstract List<ResultSim> computeSimilarity(Map<String,Map<String,ValueStructure>> vws, List<ConcurrentHashMap<String, RequestPictureVisualWord>> visualWordsByTestVector, int Niq);
    /**
     * Get the number of indexed pictures on index
     * @return Size of index
     */
    public synchronized int getSize() {
        return pictureIndex.getSize();
    }

    /**
     * Get the number of tests vectors (T)
     * @return T
     */
    public synchronized int getNumberOfTestsVectors() {
        return getTestVectors().size();
    }

    /**
     * Delete all picture path on index
     * USE ONLY FOR MAINTENANCE
     * VERY BAD PERFORMANCE (must browse all index!)
     * @param uri ids path to delete
     */
    public void deletePicture(List<Long> ids) {
        logger.info("deletePicture " + ids.size() +" resources");
        Map<Long, Integer> picturesID = pictureIndex.delete(ids);
        System.out.println("picturesID="+picturesID);
        picturesToPurge.putToPurge(picturesID); 
    }

    /**
     * Get the number of picture that has been deleted and not yet purge
     * @return 
     */
    public int getPurgeSize() {
        return picturesToPurge.size();
    }

    /**
     * Purge index from deleted pictures
     * @param config Config file
     */
    public synchronized void purge(ConfigServer config) {
        logger.info("purge " + picturesToPurge.size() +" resources");
        getTestVectors().delete(picturesToPurge.getPicturesToPurge());
        logger.info("clear purge index");
        picturesToPurge.clear();
        logger.info("picture to purge = " + getPurgeSize());
    }

    /**
     * Check if pictures is in index entries
     * VERY HEAVY, JUST FOR LITTLE TEST!
     * @param id Pictures id
     * @return True if pictures id is still in index
     */
    public boolean isPicturePresentInIndex(Long id) {
        return getTestVectors().isPicturePresentInIndex(id);
    }

    /**
     * List all pictures in index
     * @return All pictures indexed
     */
    public Map<Long, Map<String,String>> getAllPicturesMap() {
        return pictureIndex.getAllPicturesMap();

    }
    
    /**
     * List all pictures in index
     * @return All pictures indexed
     */
    public List<Long> getAllPicturesList() {
        return pictureIndex.getAllPicturesList();

    }    
    
    /**
     * Check if picture already exists in index
     * @param id Picture id
     * @return True if already exists, else false
     */    
    public boolean isPictureAlreadyIndexed(Long id) {
        return pictureIndex.IsAlreadyIndex(id);
    }

    /**
     * Close index
     * @throws CloseIndexException
     */
    public void close() throws CloseIndexException {
        pictureIndex.close();
        getTestVectors().closeIndex();
    }

    /**
     * Sync database memory and files (not for all database)
     */
    public void sync()  {
        pictureIndex.sync();
        getTestVectors().sync();
    }

    /**
     * Print stats on index (not for all database)
     */
    public void printStat()  {
        getTestVectors().printStat();
    }
    
    public Map<String,String> getProperties(Long id) {
        return pictureIndex.getProperties(id);
    }

    /**
     * Overriding union between l1 and l2
     * If l1 doesn't have the element e of l2, put e in l1
     * If l1 already have the element e of l2, increment e value in l1
     * @param   l1   HashMap l1
     * @param   l2   HashMap l2
     **/
    protected synchronized static void fusion(Map<Long, Entry> l1, Map<Long, Entry> l2) {

        for (Map.Entry<Long, Entry> entree : l2.entrySet()) {

            Entry e2 = entree.getValue();
            Entry e1 = l1.get(new Long(e2.getI()));

            if (e1 != null) {
                //if already exist, increment value
                e1.incrementNIBT(e2.getNIBT());
                e1.incrementSimilarities(e2.getSimilarities());
            } else {
                //if not exist, add it to l1
                l1.put(e2.getI(), (Entry) e2.clone());
            }
        }
    }

    /**
     * List of tests vectors
     * Each test vector contains its own index (for visual words)
     */
    public TestVectorListServer getTestVectors() {
        return testVectors;
    }
}
