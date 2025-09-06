package com.nutritionstack.nutritionstackwebapi.service.nutrition;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ReportFileService {
    
    private static final String REPORTS_DIRECTORY = "reports";
    
    public String saveReportToFile(String fileName, byte[] content) throws IOException {
        Path reportsDir = Paths.get(REPORTS_DIRECTORY);
        if (!Files.exists(reportsDir)) {
            Files.createDirectories(reportsDir);
        }
        
        Path filePath = reportsDir.resolve(fileName);
        Files.write(filePath, content);
        return filePath.toString();
    }
    
    public void deleteReportFile(String filePath) {
        try {
            Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            System.err.println("Failed to delete report file: " + filePath + " - " + e.getMessage());
        }
    }
    
    public String generateFileName(Long userId, String reportType, LocalDateTime startDate, LocalDateTime endDate) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return String.format("nutrition_report_%s_%s_%d_%s.pdf", 
            reportType.toLowerCase(), 
            startDate.toLocalDate().toString().replace("-", ""),
            userId,
            timestamp);
    }
    
    public String generateReportName(String reportType, LocalDateTime startDate, LocalDateTime endDate) {
        return String.format("%s Report - %s to %s", 
            reportType, 
            startDate.toLocalDate().toString(), 
            endDate.toLocalDate().toString());
    }
}
