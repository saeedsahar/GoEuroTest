package com.mycompany.goeuro;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonReader {

	public static void main(String[] args) throws IOException {
		String urlString = "http://api.goeuro.com/api/v2/position/suggest/en/";

		Scanner inputScanner = new Scanner(System.in);
		while (true) {
			System.out.println("======================");
			System.out.println("1. Get Data");
			System.out.println("2. Exit Program");
			System.out.println("======================");
			String input = inputScanner.next();

			switch (input){
			case "1":
				System.out.print("Input your location: ");
				String location = inputScanner.next();
				try {
					readDataFromURL(urlString, location);
				} catch (Exception e) {
					if (e instanceof RuntimeException)
						System.out.println("Data not found! Please input valid location. ");
					else
						e.printStackTrace();
				}
				break;
			case "2":
				System.exit(0);
				break;
			default:
				System.out.println("Please input valid choice i.e 1 or 2");
			}

		}
	}

	private static void readDataFromURL(String urlString, String location)
			throws MalformedURLException, IOException, JSONException {

		FileWriter fileWriter = null;
		PrintWriter printWriter = null;
		System.out.println("URL to be connected == " + urlString+location);
		InputStream inputStream = new URL(urlString+location).openStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
		// Reading response from URL as text
		String jsonText = readResponseFromURLAsText(reader);
		// Making an JSONArray from text received
		JSONArray jsonArray = new JSONArray(jsonText);
		System.out.println("Array Received == "+jsonArray);

		if (jsonArray != null && jsonArray.length() > 0) {
			String id, name, type, longitude, latitude;
			// Output CSV file - Separate file for each location / overwriting existing
			File outputCSV = new File("d:\\"+location+".csv");
			fileWriter = new FileWriter(outputCSV);
			printWriter = new PrintWriter(fileWriter);

			// Writing Columns in CSV file
			printWriter.write("_id,");
			printWriter.write("name,");
			printWriter.write("type,");
			printWriter.write("longitude,");
			printWriter.write("latitude\n");

			for (int index = 0; index < jsonArray.length(); index++) {
				JSONObject jsonObj = jsonArray.getJSONObject(index);
				System.out.println("JSON Object at index >> " + index + " == "+ jsonObj);

				id = jsonObj.get("_id").toString();
				name = jsonObj.get("name").toString();
				type = jsonObj.get("type").toString();

				JSONObject jsonObjLocation = jsonObj.getJSONObject("geo_position");

				longitude = jsonObjLocation.get("longitude").toString();
				latitude = jsonObjLocation.get("latitude").toString();

				System.out.println("ID == " + id);
				System.out.println("name == " + name);
				System.out.println("type == " + type);
				System.out.println("longitude == " + longitude);
				System.out.println("lattitude == " + latitude);

				// Writing Rows in CSV file
				printWriter.write(id + ",");
				printWriter.write(name + ",");
				printWriter.write(type + ",");
				printWriter.write(longitude + ",");
				printWriter.write(latitude + "\n");
			}
			fileWriter.close();
			printWriter.close();
			reader.close();
			inputStream.close();
		} else {
			throw new RuntimeException("");
		}

	}

	private static String readResponseFromURLAsText(BufferedReader reader) throws IOException {
		StringBuilder sb = new StringBuilder();
		int count;
		while ((count = reader.read()) != -1) {
			sb.append((char) count);
		}
		return sb.toString();
	}

}

