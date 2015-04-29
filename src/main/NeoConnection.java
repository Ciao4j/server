package main;


import java.io.File;
import java.io.IOException;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.DefaultFileSystemAbstraction;
import org.neo4j.kernel.StoreLocker;

public class NeoConnection {
	private GraphDatabaseService graphDb;
//	private static final String DB_PATH = "D:/neo4jdb/neodb";
	private static final String DB_PATH = "D:/SocialWeb/graph.db";
	public NeoConnection(){
//		StoreLocker lock = new StoreLocker(new DefaultFileSystemAbstraction());
//        lock.checkLock(new File(DB_PATH));
//        try {
//            lock.release();
            graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
    		registerShutdownHook( graphDb );
//        } catch (IOException e1) {
//            e1.printStackTrace();
//        }
		
	}
	
	public  GraphDatabaseService getGraphDb() {
		return graphDb;
	}

	public  void setGraphDb(GraphDatabaseService graphDb) {
		this.graphDb = graphDb;
	}

//	public static GraphDatabaseService getInstance() {
//		if(graphDb == null) {
//			graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
//			registerShutdownHook( graphDb );
//		}
//		return graphDb;
//	}
	private static void registerShutdownHook( final GraphDatabaseService graphDb )
	{
	    // Registers a shutdown hook for the Neo4j instance so that it
	    // shuts down nicely when the VM exits (even if you "Ctrl-C" the
	    // running example before it's completed)
	    Runtime.getRuntime().addShutdownHook( new Thread()
	    {
	        @Override
	        public void run()
	        {
	            graphDb.shutdown();
	        }
	    } );
	}
}
