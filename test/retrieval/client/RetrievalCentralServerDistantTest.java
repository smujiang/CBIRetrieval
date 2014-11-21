package retrieval.client;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import retrieval.client.ListServerInformationSocket;
import retrieval.client.RetrievalClient;
import retrieval.client.ServerInformationSocket;
import retrieval.config.ConfigClient;
import retrieval.config.ConfigServer;
import retrieval.utils.FileUtils;

/**
 *
 * @author lrollus
 */
public class RetrievalCentralServerDistantTest extends RetrievalCentralServerAbstract{
    
    private static Logger logger = Logger.getLogger(RetrievalCentralServerDistantTest.class);
    

    @BeforeClass
    public static void setUpClass() throws Exception {
        enableLog();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
        try {
            config = new ConfigServer("testdata/ConfigServer.prop");
            configCentralServer = new ConfigClient("testdata/ConfigClient.prop");
            config.setStoreName("MEMORY");
            System.out.println("server");
            multiServer1 = createMultiServer(config,MULTISERVERPORT1,0,"MEMORY");      
            multiServer2 = createMultiServer(config,MULTISERVERPORT2,0,"MEMORY");
            multiServer1.createServer(CONTAINER1);
            multiServer1.createServer(CONTAINER2);
            multiServer2.createServer(CONTAINER1);
            
            multiServer1.getServer(CONTAINER1).indexPicture(FileUtils.readPicture(LOCALPICTURE1),1l,LOCALPICTURE1MAP);
            multiServer1.getServer(CONTAINER1).indexPicture(FileUtils.readPicture(LOCALPICTURE2),2l,null);
            multiServer1.getServer(CONTAINER2).indexPicture(FileUtils.readPicture(LOCALPICTURE3),3l,null);
            multiServer1.getServer(CONTAINER2).indexPicture(FileUtils.readPicture(LOCALPICTURE4),4l,null);
            multiServer2.getServer(CONTAINER1).indexPicture(FileUtils.readPicture(LOCALPICTURE5),5l,null);
            multiServer2.getServer(CONTAINER1).indexPicture(FileUtils.readPicture(LOCALPICTURE6),6l,null);
            multiServer2.getServer(CONTAINER1).indexPicture(FileUtils.readPicture(LOCALPICTURE7),7l,null);
            /*
             * multiServer1
             * container 1 = LOCALPICTURE1, LOCALPICTURE2
             * container 2 = LOCALPICTURE3, LOCALPICTURE4
             * 
             * multiserver2
             * container 1 = LOCALPICTURE5, LOCALPICTURE6 , LOCALPICTURE7
             */
            multiCentralWithServer1 = new RetrievalClient(configCentralServer, multiServer1);
            
            List<ServerInformationSocket> servers = new ArrayList<ServerInformationSocket>();
            servers.add(new ServerInformationSocket("localhost", multiServer1.getPort()));
            servers.add(new ServerInformationSocket("localhost", multiServer2.getPort()));
            multiCentralWithAllServer = new RetrievalClient(configCentralServer, new ListServerInformationSocket(servers));
            
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            fail();
        }        
    }
    
    @After
    public void tearDown() {
        try{multiServer1.stop();}catch(Exception e) {}
        try{multiServer2.stop();}catch(Exception e) {}        
    }

}