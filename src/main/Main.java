package main;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.math.BigInteger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import model.RSAPublicKey;
import model.RSAKeyPair;
import model.RSAPrivateKey;
import encryption.Encoder;
import encryption.Decoder;
import encryption.RSAKeyPairGenerator;


public class Main {

	public static void main(String[] args)
	{
		String serverAddress = "http://54.93.36.163:8080";
		String outLoginToken = null;
		RSAPublicKey rsaServerPublicKey = null;
		RSAKeyPair ourKeyPair;
		String login = "Jessa3";
		String password = "1234";
		String receiver = "receiverName";

		ourKeyPair = RSAKeyPairGenerator.generateKeyPair(256);

		Scanner scan = new Scanner(System.in);
        String wybor;
        while (true)
        {
            writeToConsole(" ");
            writeToConsole("--------------------------------------------------");
            writeToConsole("1. - Downlaod public key");
            writeToConsole("2. - ChangePublicKey");
            writeToConsole("3. - Login");
            writeToConsole("4. - Logout");
            writeToConsole("5. - getAllUSers");
            writeToConsole("6. - getUsersPublicKey");
            writeToConsole("7. - register");
            writeToConsole("7. - sendMessage");
            writeToConsole("0. - Exit");
            wybor = scan.nextLine();
            switch (wybor) {
                case "1":
                	if(serverAddress != null)
                		rsaServerPublicKey = GetServerPublicKey(serverAddress);
                    break;
                case "2":

                    break;
                case "3":
                	if(serverAddress != null && rsaServerPublicKey != null && ourKeyPair != null && login != null && password != null)
                		outLoginToken = Login(serverAddress, rsaServerPublicKey, ourKeyPair, login, password);
                    break;
                case "4":
                	if(serverAddress != null && outLoginToken != null && rsaServerPublicKey != null)
                		Logout(serverAddress, outLoginToken, rsaServerPublicKey);
                    break;
                case "5":

                    break;
                case "6":

                    break;
                case "7":
                	if(serverAddress != null && rsaServerPublicKey != null && ourKeyPair != null && login != null && password != null)
                		Register(serverAddress, rsaServerPublicKey, ourKeyPair, login, password);
                    break;
                case "8":
                	if(serverAddress != null && rsaServerPublicKey != null && outLoginToken != null && receiver != null)
                		SendMessage(serverAddress, rsaServerPublicKey, outLoginToken, receiver, " This is my message");
                    break;
                case "0":

                    return;
                default:
                    writeToConsole("Error, please try again");
            }
        }
	}

	private static void SendMessage(String aAddress, RSAPublicKey aRsaSeverPublicKey, String aToken, String aReceiver, String aMesssage)
	{
		Encoder encoder = new Encoder(aRsaSeverPublicKey);

		String json = 	 "{'token' : '" 	+ aToken + "',"
						+"'messsage': '"  	+ aMesssage + "',"
						+"'receiver': '"  	+ aReceiver + "'}";
		String encodedJson = encoder.encryptMessage(json);

		SendHTTPRequest("POST", aAddress + "/auth/register", encodedJson);
	}

	private static void Register(String aAddress, RSAPublicKey aRsaSeverPublicKey, RSAKeyPair aOurKeyPair, String aLogin, String aPasssword)
	{
		Encoder encoder = new Encoder(aRsaSeverPublicKey);

		String json = 	 "{'login' : '" 	+ aLogin + "',"
						+"'password': '"  	+ aPasssword + "',"
						+"'e': '"  			+ aOurKeyPair.getPublicKey().getE().toString() + "',"
						+"'n': '" 			+ aOurKeyPair.getPublicKey().getN().toString()+"'}";
		String encodedJson = encoder.encryptMessage(json);

		SendHTTPRequest("POST", aAddress + "/auth/register", encodedJson);
	}

	private static void Logout(String aAddress, String aToken, RSAPublicKey aRsaSeverPublicKey)
	{
		Encoder encoder = new Encoder(aRsaSeverPublicKey);

		String json = "{'token': '"  + aToken + "'}";
		String encodedJson = encoder.encryptMessage(json);

		SendHTTPRequest("GET", aAddress + "/auth/logout", encodedJson);
	}

	private static String Login(String aAddress, RSAPublicKey aRsaSeverPublicKey, RSAKeyPair aOurKeyPair, String aLogin, String aPasssword)
	{
		RSAPrivateKey rsaPrivateKey= aOurKeyPair.getPrivateKey();
		Decoder decoder = new Decoder(rsaPrivateKey);
		Encoder encoder = new Encoder(aRsaSeverPublicKey);

		String json = "{'login': '"+ aLogin +"', 'password': '" + aPasssword +"'}";
		String encodedJson = encoder.encryptMessage(json);

		String encodedResponse = SendHTTPRequest("POST", aAddress + "/auth/login", encodedJson);
		String response = decoder.decodeMessage(encodedResponse);
		System.out.println(response);

		Map<String, String[]> map = JsonToMapStringString(response);

		return map.get("token")[0];
	}

	private static RSAPublicKey GetServerPublicKey(String aAddress)
	{
		String response = SendHTTPRequest("GET", aAddress + "/auth/key", null);
		System.out.println(response);

		Map<String, String[]> map = JsonToMapStringString(response);

		return new RSAPublicKey(new BigInteger(map.get("e")[0]), new BigInteger(map.get("n")[0]));
	}

	private static String SendHTTPRequest(String aType, String aAddress, String aJson)
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
		    if(aJson != null)
		    	connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
		    connection.setRequestProperty("Content-Language", "en-US");
		    connection.setRequestProperty("Accept", "*/*");
		    connection.setRequestProperty("Accept-Encoding", "gzip, deflate");

		    //Send request
		    if(aJson != null && aType.equals("GET") == false)
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

	private static void writeToConsole(String string) {
        synchronized (System.out) {
            System.out.println(string);
        }
    }

	private static Map<String, String[]> JsonToMapStringString(String json)
	{

		Map<String, String[]> map = new HashMap<String, String[]>();
		try {
			map = new ObjectMapper().readValue(json, HashMap.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return map;
	}

}
