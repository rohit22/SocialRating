package main.com.service;
 
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
 
import org.json.JSONObject;
 
/**
 * @author Crunchify.com
 * 
 */
 
public class RESTServiceClient {
	public static void main(String[] args) {
		String string = "";
		try {
 
			// Step1: Let's 1st read file from fileSystem
			// Change CrunchifyJSON.txt path here
			InputStream crunchifyInputStream = new FileInputStream("/home/rohitb/Desktop/object.txt");
			InputStreamReader crunchifyReader = new InputStreamReader(crunchifyInputStream);
			BufferedReader br = new BufferedReader(crunchifyReader);
			String line;
			while ((line = br.readLine()) != null) {
				string += line + "\n";
			}
 
			JSONObject jsonObject = new JSONObject(string);
			System.out.println(jsonObject);
 
			// Step2: Now pass JSON File Data to REST Service
			try {
				//URL url = new URL("http://localhost:8080/SocialRating/api/getContent");
				//URL url = new URL("http://socialratingsearch-env.us-east-1.elasticbeanstalk.com/api/getContent");
				URL url = new URL("http://cloudcomputingproj-sa-env.us-east-1.elasticbeanstalk.com/api/getContent");
				URLConnection connection = url.openConnection();
				connection.setDoOutput(true);
				connection.setRequestProperty("Content-Type", "application/json");
				//connection.setConnectTimeout(5000);
				//connection.setReadTimeout(5000);
				OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
				out.write(jsonObject.toString());
				out.close();
 
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				StringBuffer sb = new StringBuffer();
				while ((line = in.readLine()) != null) {
					sb.append(line);
				}
				
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File("/home/rohitb/Dropbox/Spring16/CloudComputing/PolarityDatasets/rt-polaritydata/output.txt")));
				bw.write(sb.toString());
				bw.flush();
				bw.close();
				System.out.println(sb.toString());
				System.out.println("\nCrunchify REST Service Invoked Successfully..");
				in.close();
			} catch (Exception e) {
				System.out.println("\nError while calling Crunchify REST Service");
				System.out.println(e);
			}
 
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
