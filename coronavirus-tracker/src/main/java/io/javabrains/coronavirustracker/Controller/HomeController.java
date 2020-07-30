package io.javabrains.coronavirustracker.Controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import io.javabrains.coronavirustracker.Service.CoronaVirusDataService;
import io.javabrains.coronavirustracker.models.LocationStats;

@Controller
public class HomeController {
	@Autowired
	CoronaVirusDataService coronaVirusDataService;
	
	@GetMapping("/")
	public String home(Model model) {
		List<LocationStats> allStats=coronaVirusDataService.getAllStats();
		int totalReportedCases=allStats.stream().mapToInt(stat -> stat.getLatestTotalCasses()).sum();
		int totalNewCases=allStats.stream().mapToInt(stat -> stat.getDiffFromPrevDay()).sum();
		model.addAttribute("locationStats",allStats);
		model.addAttribute("totalReportedCases",totalReportedCases);
		model.addAttribute("totalNewCases",totalNewCases);
		return "home";
	}

}
