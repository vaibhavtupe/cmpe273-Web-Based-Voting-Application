
package util;
import java.net.UnknownHostException;
import com.mongodb.MongoClient;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.mongodb.MongoClientURI;

public class MongodbConnection {

	public static MongoTemplate getConnection() {
		MongoClientURI uri=null;
		MongoClient mongoclient=null;
		MongoTemplate mongoConnection=null;
		try {
			uri = new MongoClientURI("mongodb://vaibhav:tupe@ds045907.mongolab.com:45907/cmpe273_assignment2");
			mongoclient = new MongoClient(uri);
			mongoConnection = new MongoTemplate(mongoclient,"cmpe273_assignment2");
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		
		
		System.out.println(mongoConnection.getCollectionNames());
		return mongoConnection;
		
		
	}

}


