package main;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
//package com.chillyfacts.com;
//import java.io.BufferedInputStream;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
//import java.net.HttpUrlConnection


public class Main {

	public static void main(String[] args)
	{
//		String targetURL = "asdas";
//		String urlParameters = "http://54.93.36.163:8080/auth/key";
//	    URL url;
//	    HttpURLConnection connection = null;
//
//		try {
//		    //Create connection
//		    url = new URL(targetURL);
//		    connection = (HttpURLConnection) url.openConnection();
//		    connection.setRequestMethod("POST");
//		    connection.setRequestProperty("Content-Type",
//		        "application/x-www-form-urlencoded");
//
//		    connection.setRequestProperty("Content-Length",
//		        Integer.toString(urlParameters.getBytes().length));
//		    connection.setRequestProperty("Content-Language", "en-US");
//
//		    connection.setUseCaches(false);
//		    connection.setDoOutput(true);
//
//		    //Send request
//		    DataOutputStream wr = new DataOutputStream (
//		        connection.getOutputStream());
//		    wr.writeBytes(urlParameters);
//		    wr.close();
//
//		    //Get Response
//		    InputStream is = connection.getInputStream();
//		    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
//		    StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
//		    String line;
//		    while ((line = rd.readLine()) != null) {
//		      response.append(line);
//		      response.append('\r');
//		    }
//		    rd.close();
//		    //return response.toString();
//		  } catch (Exception e) {
//		    e.printStackTrace();
//		    //return null;
//		  } finally {
//		    if (connection != null) {
//		      connection.disconnect();
//		    }
//		  }

		String response = SendHTTPRequest("GET", "http://54.93.36.163:8080/auth/key", "{'id': 1, 'name': 'Jessa3'}");
		System.out.println(response);
	}


//	public static String SendHTTPRequest2(String aType, String aAddress, String aJson)
//	{
//		JSONObject json = new JSONObject();
//		json.put("someKey", "someValue");
//
//		HttpClient httpClient = HttpClientBuilder.create().build();
//		try {
//		    HttpPost request = new HttpPost("http://yoururl/%22");
//		    StringEntity params = new StringEntity("details={"name":"xyz","age":"20"} ");
//		    request.addHeader("content-type", "application/x-www-form-urlencoded");
//		    request.setEntity(params);
//		    HttpResponse response = httpClient.execute(request);
//		} catch (Exception ex) {
//		} finally {
//		    // @Deprecated httpClient.getConnectionManager().shutdown();
//		}
//	}
	public static String SendHTTPRequest(String aType, String aAddress, String aJson)
	{
		String targetURL = aAddress;
		String urlParameters = aJson;
	    HttpURLConnection connection = null;

		try
		{
		    //Create connection
			URL url = new URL(targetURL);
		    connection = (HttpURLConnection) url.openConnection();
		    connection.setDoOutput(true);
		    connection.setRequestMethod(aType);
		    connection.setRequestProperty("Content-Type", "application/json");
		    connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
		    connection.setRequestProperty("Content-Language", "en-US");
		    connection.setRequestProperty("Accept", "*/*");
		    connection.setRequestProperty("Accept-Encoding", "gzip, deflate");

		    //connection.setUseCaches(false);


		    //Send request
		    if(aType.equals("GET") == false)
		    {
		    	DataOutputStream wr = new DataOutputStream (connection.getOutputStream());
			    wr.writeBytes(urlParameters);
			    wr.close();
		    }

		    //Get Response
		    InputStream is = connection.getInputStream();
		    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		    StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
		    String line;
		    while ((line = rd.readLine()) != null)
		    {
		      response.append(line);
		      response.append('\r');
		    }
		    rd.close();
		    return response.toString();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
		    if (connection != null)
		    {
		      connection.disconnect();
		    }
		  }
	}


}
