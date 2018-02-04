import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;


import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;

import twitter4j.JSONException;
import twitter4j.JSONObject;
import twitter4j.JSONArray;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;


public class Test {
	public static void main(String[] args){
		String filepath = "C:\\Users\\Sindhura\\Documents\\IRProj4\\christmas1.json";
			try {
				BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(new FileInputStream(filepath), "UTF8"));
				String stemp = "";
				String sfull = ""; 
				while((stemp = bufferedreader.readLine())!=null){
					sfull = sfull + stemp;
				}
				bufferedreader.close();
				JSONObject jsonobjectroot = new JSONObject(sfull);
				JSONArray jsontweets = jsonobjectroot.getJSONArray("tweets");

				File newfile = new File("C:\\Users\\uhith lagisetti\\Documents\\Sukesh\\Academics\\CSE535-IR\\Project4\\christmasOutput2.txt");
				Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newfile), "UTF-8"));
				
				Inner(jsontweets);
				
				

				try{
					out.write(jsonobjectroot.toString());
				} finally{
					out.close();
				}
				
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e){
				e.printStackTrace();
			} catch (JSONException e){
				e.printStackTrace();
			}
		}
	private static void Inner(JSONArray jsontweets) throws JSONException, IOException{
		int length = jsontweets.length();
		String[] apikeys = {"01788a9de84594d53ad886e19edb5a4d839256dd"};
		for(int j=0;j<1;j++){
			for(int i=j*998;i<(j+1)*998;i++){
				if(i>=length){
					break;
				}
				{
				JSONObject jsontweet = jsontweets.getJSONObject(i);
				String tweet = "";
				if(jsontweet.has("text")){
					tweet = jsontweet.getString("text");
				}
				
			
				String request        = "https://gateway-a.watsonplatform.net/calls/text/TextGetRankedNamedEntities";
				URL    url            = new URL( request );
				HttpURLConnection conn= (HttpURLConnection) url.openConnection();
				conn.setDoOutput( true );
				conn.setInstanceFollowRedirects( false );
				conn.setRequestMethod( "POST" );
				conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded"); 
				conn.setRequestProperty( "charset", "utf-8");
				String urlParameters  = "apikey="+apikeys[j]+"&text="+tweet+"&outputMode=json";
				byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
				int    postDataLength = postData.length;
				conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
				conn.setUseCaches( false );
				//JSONArray jsontags = new JSONArray();
				HashMap<String, JSONArray> hashmap = new HashMap<String, JSONArray>();
				List<String> lsstring = new ArrayList<String>();
				try( DataOutputStream wr = new DataOutputStream( conn.getOutputStream())) {
					wr.write( postData );
					
					if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
						
						StringWriter writer = new StringWriter();
						IOUtils.copy(conn.getInputStream(), writer, "UTF-8");
						String theString = writer.toString();
						
						JSONObject jsonobjectresponse = new JSONObject(theString);
						if(!jsonobjectresponse.isNull("entities")){
						JSONArray jsonentities = jsonobjectresponse.getJSONArray("entities");
						int lengthentities = jsonentities.length();
						for(int k=0;k<lengthentities;k++){
							String type = jsonentities.getJSONObject(k).getString("type");
							if(hashmap.containsKey(type)){
								hashmap.get(type).put(jsonentities.getJSONObject(k).getString("text"));
							}
							else{
								JSONArray newarray = new JSONArray();
								newarray.put(jsonentities.getJSONObject(k).getString("text"));
								hashmap.put(type, newarray);
								lsstring.add(type);
							}	
							//jsontags.put(jsonentities.getJSONObject(j).getString("text"));
						}
					}
					}
				}
				for(int h=0;h<lsstring.size();h++){
					jsontweet.put(lsstring.get(h), hashmap.get(lsstring.get(h)));
				}
				conn.disconnect();
			}
			}
		}	
			
		}
		
	}
	

