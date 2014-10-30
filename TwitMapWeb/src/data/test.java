package data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import twitter4j.GeoLocation;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfilesConfigFile;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.BatchPutAttributesRequest;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;
import com.amazonaws.services.simpledb.model.DeleteDomainRequest;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.ReplaceableItem;
import com.amazonaws.services.simpledb.model.SelectRequest;


public class test {
	static String consumerKeyStr = "";
	static String consumerSecretStr = "";
	static String accessTokenStr = "";
	static String accessTokenSecretStr = "";
	
	static String aws_access_key_id = "";
	static String aws_secret_access_key = "";


	static String myDomain;
	static AmazonSimpleDB sdb;
	static AWSCredentials credentials; 
	static Configuration config;

		  public test() throws TwitterException, IOException, JSONException, InterruptedException{
			  
			  initialDB();
			  twitCredential();
			  Thread.sleep(1000);
			  stream();
			 
		  }
		  public void twitCredential(){
			  ConfigurationBuilder cb= new ConfigurationBuilder();
		        
		        cb.setDebugEnabled(true)
		        .setOAuthConsumerKey(consumerKeyStr)
		            .setOAuthConsumerSecret(consumerSecretStr)
		            .setOAuthAccessToken(accessTokenStr)
		            .setOAuthAccessTokenSecret(accessTokenSecretStr);
		        config = cb.build();
		      
		      
		        
			   System.out.println("credetial build for twitter");
		  }
		  
		  public  void stream() throws TwitterException, IOException, JSONException{
			   System.out.println("stream data");
		       StatusListener listener = new StatusListener(){
		    	   int i=0;
		       
		       public void onStatus(Status status) {
		    	    
		    	    String userName = status.getUser().getName();
		    	   	String text = status.getText();
		    	   	String location = null;
		    	   	GeoLocation geoLocation=null;
		    	   	double geoLocationLat = 361 ;
		    	   	double geoLocationLong =361 ;
		    	   	String geoLat = null;
		    	   	String geoLong = null;
		           // System.out.println(userName + " : " + text);
		            
		            if( status.getPlace()!=null&&status.getPlace().getFullName()!=null){
		            	 String place = status.getPlace().getFullName();		            	
		            //	 System.out.println("place: "+ place);
		       
		            }
		            if( status.getUser().getLocation()!=null){
		            	 location = status.getUser().getLocation();
		            //	 System.out.println("Location:  "+ location);
		            }
		            
		            if( status.getGeoLocation()!=null){
		            	 geoLocation = status.getGeoLocation();
		            	 geoLocationLat=status.getGeoLocation().getLatitude();
		            	 geoLat = String.valueOf(geoLocationLat);
		            	 geoLocationLong =status.getGeoLocation().getLongitude();
		            	 geoLong = String.valueOf(geoLocationLong);
		          //  	 System.out.println("geoLocation:  "+ geoLocationLat + "  "+ geoLocationLong);
		            }
		           
		            if(status.getLang()!=null){
		            //	System.out.println("Language:  "+status.getLang());
		         
		            }
		            if(geoLocation!=null){
		            	String itemIndex =((Integer)(i++)).toString();
		            List<ReplaceableItem> sampleData = new ArrayList<ReplaceableItem>();

		            sampleData.add(new ReplaceableItem().withName(itemIndex).withAttributes(
		                    new ReplaceableAttribute().withName("Username").withValue(userName),
		                    new ReplaceableAttribute().withName("content").withValue(text),
		                    new ReplaceableAttribute().withName("Location").withValue(location),
		                    new ReplaceableAttribute().withName("geoLat").withValue(geoLat),
		                    new ReplaceableAttribute().withName("geoLong").withValue(geoLong)
		                    ));
		            sdb.batchPutAttributes(new BatchPutAttributesRequest(myDomain, sampleData) );        
		            }
		            
		           
		        }
		        public void onDeletionNotice1(StatusDeletionNotice statusDeletionNotice) {}
		        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
		        public void onException(Exception ex) {
		            ex.printStackTrace();
		        }
				@Override
				public void onDeletionNotice(StatusDeletionNotice arg0) {}
			
				@Override
				public void onScrubGeo(long arg0, long arg1) {}
				
				@Override
				public void onStallWarning(StallWarning arg0) {}
		    };
		    TwitterStream twitterStream = new TwitterStreamFactory(config).getInstance();
		    twitterStream.addListener(listener);
		    // sample() method internally creates a thread which manipulates TwitterStream and calls these adequate listener methods continuously.
		    twitterStream.sample();
	}
	public  void initialDB(){
		 System.out.println("initial DB");
		
		
         try {
             //credentials =  new ProfileCredentialsProvider("/TwitMapWeb","credentials").getCredentials();
        	 //  credentials = new ProfileCredentialsProvider("").getCredentials();
        	 credentials = new PropertiesCredentials(test.class.getResourceAsStream("credentials"));
         } catch (Exception e) {
             throw new AmazonClientException(
                     "Cannot load the credentials from the credential profiles file. " +
                     "Please make sure that your credentials file is at the correct " +
                     "location (/Users/shijiehu/.aws/credentials), and is in valid format.",
                     e);
         }
          sdb = new AmazonSimpleDBClient(credentials);
        
         
         System.out.println("===========================================");
         System.out.println("Getting Started with Amazon SimpleDB");
         System.out.println("===========================================\n");
         
         try {
        	 String preDomain = null;
        	// System.out.println("Listing all domains in your account:\n");
             for (String domainName : sdb.listDomains().getDomainNames()) {
                 System.out.println("  " + domainName);
                 preDomain = domainName;
             }
             System.out.println();
          //   System.out.println("Deleting " + preDomain + " domain.\n");
          //   sdb.deleteDomain(new DeleteDomainRequest(preDomain));
             // Create a domain
              myDomain = "MyStore";
          //   System.out.println("Creating domain called " + myDomain + ".\n");
             sdb.createDomain(new CreateDomainRequest(myDomain));

             // List domains
         //    System.out.println("Listing all domains in your account:\n");
             for (String domainName : sdb.listDomains().getDomainNames()) {
                 System.out.println("  " + domainName);
             }
             
     /*        System.out.println("Putting data into " + myDomain + " domain.\n");
             sdb.batchPutAttributes(new BatchPutAttributesRequest(myDomain, createSampleData()));
             Thread.sleep(1000);*/
             
         } catch (AmazonServiceException ase) {
             System.out.println("Caught an AmazonServiceException, which means your request made it "
                     + "to Amazon SimpleDB, but was rejected with an error response for some reason.");
             System.out.println("Error Message:    " + ase.getMessage());
             System.out.println("HTTP Status Code: " + ase.getStatusCode());
             System.out.println("AWS Error Code:   " + ase.getErrorCode());
             System.out.println("Error Type:       " + ase.getErrorType());
             System.out.println("Request ID:       " + ase.getRequestId());
         } catch (AmazonClientException ace) {
             System.out.println("Caught an AmazonClientException, which means the client encountered "
                     + "a serious internal problem while trying to communicate with SimpleDB, "
                     + "such as not being able to access the network.");
             System.out.println("Error Message: " + ace.getMessage());
         }
         
	}
	
	public ArrayList<message> readDB(String keyword) throws InterruptedException{
		 String result = null;
		  JSONObject dbStream = new JSONObject();
          File twitterFile = new File("db_data");
          
	        System.out.println("===========================================");
	        System.out.println("Getting Started with Amazon SimpleDB");
	        System.out.println("===========================================\n");
	        ArrayList<message> datas = new ArrayList<message>();
	        try {
	            String myDomain = null;
	            for (String domainName : sdb.listDomains().getDomainNames()) {
	                myDomain = domainName;
	            }
	            System.out.println();
	            Thread.sleep(100);

	            // Select data from a domain
	            // Notice the use of backticks around the domain name in our select expression.
	            String selectExpression = "select * from " + myDomain + " where content like '"+keyword+"%'";
	            System.out.println("Selecting: " + selectExpression + "\n");
	            SelectRequest selectRequest = new SelectRequest(selectExpression);
	            
	            for (Item item : sdb.select(selectRequest).getItems()) {
	            	message singlemsg = new message();
	                System.out.println("    itemIndex: " + item.getName());
	                for (Attribute attribute : item.getAttributes()) {
	                    
	                    if(attribute.getName().equals("username" )) singlemsg.username = attribute.getValue();
	                    if(attribute.getName().equals("content") )  singlemsg.content = attribute.getValue();
	                    if(attribute.getName().equals("location" )) singlemsg.location = attribute.getValue();
	                    if(attribute.getName() .equals("geoLat" )) {singlemsg.geoLat = Double.parseDouble(attribute.getValue());
	                    System.out.println("12");
	                    }
	                    
	                    if(attribute.getName() .equals("geoLong") ) {singlemsg.geoLong = Double.parseDouble(attribute.getValue());
	                    }
	                }
	                datas.add(singlemsg);
	            }
	            System.out.println();
	           
	        } catch (AmazonServiceException ase) {
	            System.out.println("Caught an AmazonServiceException, which means your request made it "
	                    + "to Amazon SimpleDB, but was rejected with an error response for some reason.");
	            System.out.println("Error Message:    " + ase.getMessage());
	            System.out.println("HTTP Status Code: " + ase.getStatusCode());
	            System.out.println("AWS Error Code:   " + ase.getErrorCode());
	            System.out.println("Error Type:       " + ase.getErrorType());
	            System.out.println("Request ID:       " + ase.getRequestId());
	        } catch (AmazonClientException ace) {
	            System.out.println("Caught an AmazonClientException, which means the client encountered "
	                    + "a serious internal problem while trying to communicate with SimpleDB, "
	                    + "such as not being able to access the network.");
	            System.out.println("Error Message: " + ace.getMessage());
	        }
	    return datas;
	}

}
