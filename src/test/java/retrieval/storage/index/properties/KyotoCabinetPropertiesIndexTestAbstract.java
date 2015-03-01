/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval.storage.index.properties;


import org.junit.*;
import retrieval.TestUtils;
import retrieval.config.ConfigServer;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author lrollus
 */
public abstract class KyotoCabinetPropertiesIndexTestAbstract extends TestUtils{
    
    KyotoCabinetPropertiesIndexSingleFile mainIndex;
    ConfigServer config;    
    
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getCountValue method, of class KyotoCabinetPathIndexAbstract.
     */
    @Test
    public void testGetCountValue() {
        System.out.println("getCountValue");
        assertEquals(0,mainIndex.getCountValue());
        mainIndex.addPicture(1l,null);
        assertEquals(1,mainIndex.getCountValue());
    }

    /**
     * Test of setCountValue method, of class KyotoCabinetPathIndexAbstract.
     */
    @Test
    public void testSetCountValue() {
        System.out.println("setCountValue");
        mainIndex.setCountValue(99);
        assertEquals(99,mainIndex.getCountValue());
    }

    /**
     * Test of incrCountSize method, of class KyotoCabinetPathIndexAbstract.
     */
    @Test
    public void testIncrCountSize() {
        System.out.println("incrCountSize");
        mainIndex.setCountValue(2);
        mainIndex.incrCountSize();
        assertEquals(3,mainIndex.getCountValue());
    }

    /**
     * Test of decrCountSize method, of class KyotoCabinetPathIndexAbstract.
     */
    @Test
    public void testDecrCountSize() {
        System.out.println("decrCountSize");
        mainIndex.setCountValue(3);
        mainIndex.decrCountSize();
        assertEquals(2,mainIndex.getCountValue());
    }
    
    @Test
    public void testConvertStringToMap() {
        System.out.println("testConvertStringToMap");
        Map<String,String> map = new HashMap<String,String>();
        map.put("test1", "123");
        map.put("test2", "456");
        
        String result = KyotoCabinetPropertiesIndexSingleFile.convertMapToString(map);
        assertEquals(true,"test1;@;123;@;test2;@;456".equals(result) || "test2;@;456;@;test1;@;123".equals(result));
        assertEquals("NULL",KyotoCabinetPropertiesIndexSingleFile.convertMapToString(null));
        assertEquals("",KyotoCabinetPropertiesIndexSingleFile.convertMapToString(new HashMap<String,String>()));
    }    

    
    @Test
    public void testConvertMapToString() {
        System.out.println("testConvertMapToString");

        String test = "test1;@;123;@;test2;@;456";
        Map<String,String> map = KyotoCabinetPropertiesIndexSingleFile.convertStringToMap(test);
        
        assertEquals(2,map.size());
        assertEquals("123",map.get("test1"));
        assertEquals("456",map.get("test2"));
    }        
}
