package demo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.data.mongodb.core.MongoTemplate;

import util.MongodbConnection;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import dao.PollDAO;

public class ControllerOperations {

	private final AtomicInteger modCounter = new AtomicInteger();
	private final AtomicInteger pollCounter = new AtomicInteger();
	Random rand = new Random();

	MongoTemplate m = MongodbConnection.getConnection();
	DBCollection mc = m.getCollection("moderator_details");
	DBCollection pc = m.getCollection("poll_details");
	TimeZone timeZone = TimeZone.getTimeZone("UTC");


	// 1 POST : Create Moderator

	public Moderator createModerator(Moderator mod){

		SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd'T'hh:mm:ss'Z'");
		ft.setTimeZone(timeZone);

		mod.setId(modCounter.incrementAndGet());

		mod.setCreated_at(((ft.format(new Date()))));

		DBObject DBObject = createModDBObject(mod);

		mc.insert(DBObject);

		return mod;	
	}



	// 2 GET : View Moderator

	public Moderator getModerator(int moderator_id){


		DBObject query = new BasicDBObject("moderator_id", moderator_id);

		DBObject dbo =	mc.findOne(query);

		return m.getConverter().read(Moderator.class, dbo);

	}

	// 3 PUT : Update Moderator
	public Moderator updateModerator(int moderator_id, Moderator mod){

		BasicDBObject newDoc = new BasicDBObject();
		newDoc.put("email", mod.getEmail());
		newDoc.put("password",mod.getPassword());

		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("moderator_id", moderator_id);

		BasicDBObject updateObj = new BasicDBObject();
		updateObj.put("$set", newDoc);

		mc.update(searchQuery, updateObj);

		return getModerator(moderator_id);


	}


	// 4 POST : Create a new Poll

	public Poll insertPoll(int moderator_id,Poll poll){


		poll.setId(Integer.toString((rand.nextInt(100000)+37),36));

		DBObject DBObject = createPollDBObject(new PollDAO(poll),moderator_id);

		pc.insert(DBObject);


		return poll;

	}


	// 5 GET : list poll details for particular poll_id without results

	public Poll viewPollWithoutResult(String poll_id){

		DBObject query = new BasicDBObject("poll_id", poll_id);

		DBObject dbo =	pc.findOne(query);

		return m.getConverter().read(Poll.class, dbo);

	}


	// 6 GET  : list poll with result

	public PollDAO viewPollWithResult(int moderator_id,String poll_id){

		DBObject query = new BasicDBObject("poll_id", poll_id);

		DBObject dbo =	pc.findOne(query);


		Poll poll= m.getConverter().read(Poll.class, dbo);

		PollDAO polldao= new PollDAO(poll);


		String jsonstring=dbo.toString();

		try {
			JSONObject jobj = (JSONObject) new JSONParser().parse(jsonstring);
			JSONArray ja =(JSONArray) jobj.get("results");

			int a[]=new int [ja.size()];
			for(int i=0;i<ja.size();i++){

				a[i]=Integer.valueOf(String.valueOf(ja.get(i)));

			}
			polldao.setResults(a);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return polldao;

	}

	// 7 GET  : list all polls
	public List<PollDAO> viewAllPolls(int moderator_id){

		List<PollDAO> list= new LinkedList<PollDAO>();

		DBObject query = new BasicDBObject("moderator_id", moderator_id);

		DBCursor cur =	pc.find(query);

		while(cur.hasNext()){

			DBObject dbo=cur.next();
			Poll poll= m.getConverter().read(Poll.class, dbo);

			PollDAO polldao= new PollDAO(poll);


			String jsonstring=dbo.toString();

			try {
				JSONObject jobj = (JSONObject) new JSONParser().parse(jsonstring);
				JSONArray ja =(JSONArray) jobj.get("results");

				int a[]=new int [ja.size()];
				for(int i=0;i<ja.size();i++){

					a[i]=Integer.valueOf(String.valueOf(ja.get(i)));		

				}
				polldao.setResults(a);
				list.add(polldao);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}



		}


		return list;
	}


	// 8 DELETE : delete a poll

	public void deletePoll(String poll_id){

		BasicDBObject query = new BasicDBObject();
		query.put("poll_id", poll_id);	

		pc.remove(query);

	}

	// 9 PUT : Vote a poll

	public void votePoll(String poll_id,int choice_index){

		DBObject query = new BasicDBObject("poll_id", poll_id);

		DBObject dbo =	pc.findOne(query);


		Poll poll= m.getConverter().read(Poll.class, dbo);

		PollDAO polldao= new PollDAO(poll);


		String jsonstring=dbo.toString();

		try {
			JSONObject jobj = (JSONObject) new JSONParser().parse(jsonstring);
			JSONArray ja =(JSONArray) jobj.get("results");

			int a[]=new int [ja.size()];
			for(int i=0;i<ja.size();i++){

				a[i]=Integer.valueOf(String.valueOf(ja.get(i)));


				//polldao.setResults(a);


			}

			System.out.println("before update choice count :  "+ a[choice_index]);
			a[choice_index]=a[choice_index]+1;

			polldao.setResults(a);
			System.out.println("before update choice count :  "+ a[choice_index]);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("poll_id", poll_id);

		//BasicDBObject updateObj = new BasicDBObject();
		//updateObj.put("$set", newDoc);

		DBObject updateObj = createPollDBObject(polldao,Integer.valueOf(String.valueOf(dbo.get("moderator_id"))));

		System.out.println("object after updating  :  "+updateObj.get("results"));

		pc.update(searchQuery, updateObj);


	}




	//10 :  Result Scheduler giving results of expired polls to kafka broker

	public List<String> getResultsOfExpiredPolls(){

		SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd'T'hh:mm:ss.ms'Z'");
		ft.setTimeZone(timeZone);


		List<String> result=new LinkedList<String>();

		DBCursor cur =	pc.find();

		while(cur.hasNext()){

			String msg=null;
			DBObject dbo=cur.next();
 
			try {
				String currentTime=ft.format((new Date()));

				String x=(String) dbo.get("expired_at");

				System.out.println("extracted expiry time from db : "+x);
				Date temp=ft.parse(x);
				String expiryTime=ft.format(temp);

				Date p = ft.parse(expiryTime);
				Date c=ft.parse(currentTime);


				if(c.compareTo(p) <0){                                      // not expired

					continue;

				}else if(p.compareTo(c)==0){                               // both date are same
					if(c.getTime() < p.getTime()){                         // not expired

						continue;  	

					}else if(p.getTime() == c.getTime()){                   //expired


						msg=createProducerMessage(dbo);


					}else{                                                   //expired

						msg=createProducerMessage(dbo);

					}
				}else{                                                      //expired

					msg=createProducerMessage(dbo);

				}

			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 

			if(msg!=null){
				System.out.println(" before adding into list : " + msg);
				result.add(msg);
			}


		}


		return result;

	}

	public String createProducerMessage(DBObject dbo){

		String sjsuId="009606766";
		String msg=null;
		DBObject query = new BasicDBObject("moderator_id", dbo.get("moderator_id"));

		DBObject d =	mc.findOne(query);
		String email=(String) d.get("email");


		msg=email+":"+sjsuId+":Poll Result [";


		try {
			JSONArray jr = (JSONArray) new JSONParser().parse(dbo.get("results").toString());
			JSONArray jc =(JSONArray)new JSONParser().parse( dbo.get("choice").toString());

			int i=0;
			while(i<jr.size()){

				System.out.println(jc.get(i)+"  =  "+jr.get(i));

				msg=msg+jc.get(i)+" = "+jr.get(i)+",";
				i++;
			}
			msg=msg+"]";

			System.out.println(msg);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return msg;


	}



	public DBObject createPollDBObject(PollDAO poll,int moderator_id){

		BasicDBObjectBuilder docBuilder = BasicDBObjectBuilder.start();

		docBuilder.append("poll_id", poll.getId());
		docBuilder.append("moderator_id", moderator_id);
		docBuilder.append("question", poll.getQuestion());
		docBuilder.append("started_at", poll.getStarted_at());
		docBuilder.append("expired_at", poll.getExpired_at());
		docBuilder.append("choice", poll.getChoice());
		docBuilder.append("results",poll.getResults());

		return docBuilder.get();

	}

	public  DBObject createModDBObject(Moderator mod){

		BasicDBObjectBuilder docBuilder = BasicDBObjectBuilder.start();

		docBuilder.append("moderator_id", mod.getId());
		docBuilder.append("name", mod.getName());
		docBuilder.append("email", mod.getEmail());
		docBuilder.append("password", mod.getPassword());
		docBuilder.append("created_at", mod.getCreated_at());

		return docBuilder.get();
	}




}
