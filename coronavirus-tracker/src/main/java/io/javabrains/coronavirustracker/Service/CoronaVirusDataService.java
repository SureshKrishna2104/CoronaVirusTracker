package io.javabrains.coronavirustracker.Service;
import java.util.*;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.javabrains.coronavirustracker.models.LocationStats;
@Service
public class CoronaVirusDataService {
	private static String VIRUS_DATA_URL="https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
	
	private List<LocationStats> allStats=new ArrayList<>();

	public List<LocationStats> getAllStats() {
		return allStats;
	}

	@PostConstruct
	@Scheduled(cron = "* * 1 * * *")
	public void fetchVirusData() throws IOException, InterruptedException {
	   List<LocationStats> newStats=new ArrayList<>();
		HttpClient client=HttpClient.newHttpClient();
		HttpRequest request=HttpRequest.newBuilder().uri(URI.create(VIRUS_DATA_URL)).build();
		HttpResponse<String> httpResponse=client.send(request,HttpResponse.BodyHandlers.ofString());
		//System.out.println(httpResponse.body());
		
		StringReader csvBodyReader=new StringReader(httpResponse.body());
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
		for (CSVRecord record : records) {
		   LocationStats locationstat=new LocationStats();
		   locationstat.setState(record.get("Province/State"));
		   locationstat.setCountry(record.get("Country/Region"));
		   int latestCases=Integer.parseInt(record.get(record.size()-1));
		   int prevDayCases=Integer.parseInt(record.get(record.size()-2));
		   locationstat.setLatestTotalCasses(latestCases);
		   locationstat.setDiffFromPrevDay(latestCases-prevDayCases);
		   //System.out.println(locationstat);
		   newStats.add(locationstat);
		   /* this all for 1st 20mins
		    * String state = record.get("Province/State");
		    System.out.println(state);
		    String customerNo = record.get("CustomerNo");
		    String name = record.get("Name");
		    */
		}
		this.allStats=newStats;
	} 

}
