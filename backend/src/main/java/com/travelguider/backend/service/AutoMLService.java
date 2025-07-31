package com.travelguider.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

@Service
public class AutoMLService {

    @Autowired
    private CsvService csvService;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.ml.dir:ml}")
    private String mlDir;

    /**
     * Automatically update ML datasets when place data changes
     */
    public void updateMLDatasets() {
        CompletableFuture.runAsync(() -> {
            try {
                System.out.println("🤖 Auto-updating ML datasets...");
                
                // Create ML directory if it doesn't exist
                Path mlPath = Paths.get(mlDir);
                if (!Files.exists(mlPath)) {
                    Files.createDirectories(mlPath);
                }

                // Export ML-ready CSV to ML folder
                String mlCsvPath = exportMLDataToMLFolder();
                
                // Trigger Python ML model retraining (if python script exists)
                triggerMLModelUpdate(mlCsvPath);
                
                System.out.println("✅ ML datasets updated successfully");
                
            } catch (Exception e) {
                System.err.println("❌ Error updating ML datasets: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Export ML-ready data directly to ML folder
     */
    private String exportMLDataToMLFolder() throws IOException {
        Path mlPath = Paths.get(mlDir);
        String fileName = "places_ml_data.csv";
        Path csvFilePath = mlPath.resolve(fileName);
        
        // Use the enhanced CSV service to generate ML data
        csvService.exportPlacesToCsvForML();
        
        // Copy from uploads to ML folder
        Path sourcePath = Paths.get(uploadDir, "places_ml_ready.csv");
        if (Files.exists(sourcePath)) {
            Files.copy(sourcePath, csvFilePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            System.out.println("📊 ML dataset exported to: " + csvFilePath);
        }
        
        return csvFilePath.toString();
    }

    /**
     * Trigger Python ML model retraining
     */
    private void triggerMLModelUpdate(String csvPath) {
        try {
            Path pythonScript = Paths.get(mlDir, "auto_retrain_model.py");
            
            if (Files.exists(pythonScript)) {
                System.out.println("🐍 Triggering Python ML model retraining...");
                
                ProcessBuilder pb = new ProcessBuilder("python", pythonScript.toString(), csvPath);
                pb.directory(Paths.get(mlDir).toFile());
                pb.redirectErrorStream(true);
                
                Process process = pb.start();
                
                // Don't wait for completion - let it run in background
                CompletableFuture.runAsync(() -> {
                    try {
                        int exitCode = process.waitFor();
                        if (exitCode == 0) {
                            System.out.println("✅ Python ML model retraining completed successfully");
                        } else {
                            System.err.println("❌ Python ML model retraining failed with exit code: " + exitCode);
                        }
                    } catch (InterruptedException e) {
                        System.err.println("❌ ML model retraining interrupted: " + e.getMessage());
                    }
                });
                
            } else {
                System.out.println("ℹ️ Python auto-retrain script not found at: " + pythonScript);
                System.out.println("ℹ️ ML dataset updated, manual model retraining required");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error triggering ML model update: " + e.getMessage());
        }
    }

    /**
     * Check if ML model exists and is up to date
     */
    public boolean isMLModelUpToDate() {
        try {
            Path mlModelPath = Paths.get(mlDir, "travel_recommendation_model.pkl");
            Path mlDataPath = Paths.get(mlDir, "places_ml_data.csv");
            
            if (!Files.exists(mlModelPath) || !Files.exists(mlDataPath)) {
                return false;
            }
            
            long modelTime = Files.getLastModifiedTime(mlModelPath).toMillis();
            long dataTime = Files.getLastModifiedTime(mlDataPath).toMillis();
            
            // Model should be newer than data
            return modelTime >= dataTime;
            
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Get ML model status for health checks
     */
    public String getMLStatus() {
        try {
            Path mlDir = Paths.get(this.mlDir);
            Path dataFile = mlDir.resolve("places_ml_data.csv");
            Path modelFile = mlDir.resolve("travel_recommendation_model.pkl");
            
            StringBuilder status = new StringBuilder();
            status.append("ML Directory: ").append(Files.exists(mlDir) ? "✅" : "❌").append("\n");
            status.append("ML Dataset: ").append(Files.exists(dataFile) ? "✅" : "❌").append("\n");
            status.append("ML Model: ").append(Files.exists(modelFile) ? "✅" : "❌").append("\n");
            
            if (Files.exists(dataFile)) {
                long records = Files.lines(dataFile).count() - 1; // Subtract header
                status.append("Records: ").append(records).append("\n");
            }
            
            if (Files.exists(modelFile)) {
                long modelTime = Files.getLastModifiedTime(modelFile).toMillis();
                status.append("Model Updated: ").append(new java.util.Date(modelTime)).append("\n");
            }
            
            status.append("Model Up-to-date: ").append(isMLModelUpToDate() ? "✅" : "❌");
            
            return status.toString();
            
        } catch (IOException e) {
            return "Error checking ML status: " + e.getMessage();
        }
    }
}
