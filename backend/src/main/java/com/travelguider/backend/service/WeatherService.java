package com.travelguider.backend.service;

import org.springframework.stereotype.Service;

@Service
public class WeatherService {
    
    // This would integrate with a real weather API like OpenWeatherMap
    // For now, providing mock weather data
    
    public WeatherInfo getWeatherForLocation(double latitude, double longitude, String date) {
        // Mock weather data - in real implementation, this would call an external API
        WeatherInfo weather = new WeatherInfo();
        weather.setCondition(generateMockWeatherCondition());
        weather.setTemperature(generateMockTemperature());
        weather.setHumidity(70);
        weather.setWindSpeed(10.5);
        
        return weather;
    }
    
    private String generateMockWeatherCondition() {
        String[] conditions = {"Sunny", "Partly Cloudy", "Cloudy", "Light Rain", "Clear"};
        return conditions[(int)(Math.random() * conditions.length)];
    }
    
    private double generateMockTemperature() {
        // Generate temperature between 20-35°C for Sri Lanka
        return 20 + (Math.random() * 15);
    }
    
    public static class WeatherInfo {
        private String condition;
        private double temperature;
        private int humidity;
        private double windSpeed;
        
        // Getters and setters
        public String getCondition() { return condition; }
        public void setCondition(String condition) { this.condition = condition; }
        
        public double getTemperature() { return temperature; }
        public void setTemperature(double temperature) { this.temperature = temperature; }
        
        public int getHumidity() { return humidity; }
        public void setHumidity(int humidity) { this.humidity = humidity; }
        
        public double getWindSpeed() { return windSpeed; }
        public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }
    }
}
