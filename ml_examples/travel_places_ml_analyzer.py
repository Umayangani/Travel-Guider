# Travel Guide Places ML Analysis
# Example script for using the exported CSV data for Machine Learning

import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier, RandomForestRegressor
from sklearn.preprocessing import LabelEncoder, StandardScaler
from sklearn.metrics import classification_report, mean_squared_error
import matplotlib.pyplot as plt
import seaborn as sns

class TravelPlacesMLAnalyzer:
    def __init__(self, csv_file_path):
        """
        Initialize the ML analyzer with CSV data
        Args:
            csv_file_path: Path to the downloaded CSV file from Travel Guide backend
        """
        self.df = pd.read_csv(csv_file_path)
        self.le = LabelEncoder()
        self.scaler = StandardScaler()
        
    def load_and_explore_data(self):
        """Load and explore the travel places dataset"""
        print("=== Travel Places Dataset Overview ===")
        print(f"Dataset shape: {self.df.shape}")
        print("\nColumn names:")
        print(self.df.columns.tolist())
        print("\nFirst few rows:")
        print(self.df.head())
        print("\nData types:")
        print(self.df.dtypes)
        print("\nMissing values:")
        print(self.df.isnull().sum())
        
        return self.df
    
    def preprocess_data(self):
        """Preprocess the data for ML"""
        print("\n=== Preprocessing Data ===")
        
        # Handle missing values
        self.df['estimated_time_to_visit'].fillna(self.df['estimated_time_to_visit'].median(), inplace=True)
        self.df['latitude'].fillna(0, inplace=True)
        self.df['longitude'].fillna(0, inplace=True)
        
        # If using standard CSV (not ML-ready), create additional features
        if 'name_length' not in self.df.columns:
            self.df['name_length'] = self.df['name'].str.len()
            self.df['description_length'] = self.df['description'].str.len()
            self.df['description_word_count'] = self.df['description'].str.split().str.len()
        
        # Create categorical features if not present
        if 'time_category' not in self.df.columns:
            self.df['time_category'] = pd.cut(self.df['estimated_time_to_visit'], 
                                            bins=[0, 1, 3, 6, float('inf')], 
                                            labels=['short', 'medium', 'long', 'full_day'])
        
        print("Preprocessing completed!")
        return self.df
    
    def analyze_place_categories(self):
        """Analyze travel place categories"""
        print("\n=== Place Category Analysis ===")
        
        # Category distribution
        category_counts = self.df['category'].value_counts()
        print("Category distribution:")
        print(category_counts)
        
        # Visualization
        plt.figure(figsize=(10, 6))
        plt.subplot(1, 2, 1)
        category_counts.plot(kind='bar')
        plt.title('Place Categories Distribution')
        plt.xticks(rotation=45)
        
        # Average time by category
        plt.subplot(1, 2, 2)
        avg_time = self.df.groupby('category')['estimated_time_to_visit'].mean()
        avg_time.plot(kind='bar', color='orange')
        plt.title('Average Visit Time by Category')
        plt.xticks(rotation=45)
        plt.tight_layout()
        plt.show()
        
        return category_counts
    
    def geographic_analysis(self):
        """Analyze geographic distribution of places"""
        print("\n=== Geographic Analysis ===")
        
        # Remove rows with invalid coordinates
        valid_coords = self.df[(self.df['latitude'] != 0) & (self.df['longitude'] != 0)]
        
        if len(valid_coords) > 0:
            plt.figure(figsize=(12, 8))
            
            # Scatter plot of places
            plt.subplot(2, 2, 1)
            plt.scatter(valid_coords['longitude'], valid_coords['latitude'], 
                       c=valid_coords['estimated_time_to_visit'], cmap='viridis', alpha=0.7)
            plt.colorbar(label='Visit Time (hours)')
            plt.xlabel('Longitude')
            plt.ylabel('Latitude')
            plt.title('Places by Location (colored by visit time)')
            
            # Region distribution
            plt.subplot(2, 2, 2)
            self.df['region'].value_counts().plot(kind='pie', autopct='%1.1f%%')
            plt.title('Distribution by Region')
            
            # District analysis
            plt.subplot(2, 2, 3)
            top_districts = self.df['district'].value_counts().head(10)
            top_districts.plot(kind='barh')
            plt.title('Top 10 Districts by Number of Places')
            
            plt.tight_layout()
            plt.show()
        
        return valid_coords
    
    def predict_visit_time(self):
        """Build ML model to predict visit time"""
        print("\n=== Building Visit Time Prediction Model ===")
        
        # Prepare features
        features = ['name_length', 'description_length', 'description_word_count']
        
        # Add encoded categorical features if available
        if 'category_encoded' in self.df.columns:
            features.extend(['category_encoded', 'region_encoded', 'district_encoded'])
        else:
            # Encode categories manually
            self.df['category_encoded'] = self.le.fit_transform(self.df['category'].fillna('unknown'))
            features.append('category_encoded')
        
        # Add coordinates if available
        if self.df['latitude'].sum() != 0:
            features.extend(['latitude', 'longitude'])
        
        X = self.df[features].fillna(0)
        y = self.df['estimated_time_to_visit']
        
        # Remove rows with missing target
        mask = ~y.isna()
        X = X[mask]
        y = y[mask]
        
        # Split data
        X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
        
        # Scale features
        X_train_scaled = self.scaler.fit_transform(X_train)
        X_test_scaled = self.scaler.transform(X_test)
        
        # Train model
        model = RandomForestRegressor(n_estimators=100, random_state=42)
        model.fit(X_train_scaled, y_train)
        
        # Predictions
        y_pred = model.predict(X_test_scaled)
        
        # Evaluate
        mse = mean_squared_error(y_test, y_pred)
        rmse = np.sqrt(mse)
        
        print(f"Model Performance:")
        print(f"RMSE: {rmse:.2f} hours")
        print(f"Mean actual visit time: {y_test.mean():.2f} hours")
        
        # Feature importance
        feature_importance = pd.DataFrame({
            'feature': features,
            'importance': model.feature_importances_
        }).sort_values('importance', ascending=False)
        
        print("\nFeature Importance:")
        print(feature_importance)
        
        # Visualization
        plt.figure(figsize=(12, 5))
        
        plt.subplot(1, 2, 1)
        plt.scatter(y_test, y_pred, alpha=0.7)
        plt.plot([y_test.min(), y_test.max()], [y_test.min(), y_test.max()], 'r--', lw=2)
        plt.xlabel('Actual Visit Time')
        plt.ylabel('Predicted Visit Time')
        plt.title('Actual vs Predicted Visit Time')
        
        plt.subplot(1, 2, 2)
        feature_importance.head(10).plot(x='feature', y='importance', kind='barh')
        plt.title('Top 10 Feature Importance')
        
        plt.tight_layout()
        plt.show()
        
        return model, feature_importance
    
    def classify_place_popularity(self):
        """Classify places by popularity based on features"""
        print("\n=== Place Popularity Classification ===")
        
        # Create popularity labels based on multiple factors
        if 'popularity_score' in self.df.columns:
            # Use pre-computed popularity score
            popularity_threshold = self.df['popularity_score'].median()
            self.df['is_popular'] = (self.df['popularity_score'] > popularity_threshold).astype(int)
        else:
            # Create simple popularity score
            time_score = (self.df['estimated_time_to_visit'] >= 2) & (self.df['estimated_time_to_visit'] <= 4)
            name_score = self.df['name_length'] <= 15
            category_score = self.df['category'].isin(['religious', 'historical', 'natural'])
            
            self.df['is_popular'] = (time_score.astype(int) + name_score.astype(int) + category_score.astype(int)) >= 2
        
        # Prepare features for classification
        features = ['name_length', 'description_length', 'description_word_count', 'estimated_time_to_visit']
        
        if 'category_encoded' not in self.df.columns:
            self.df['category_encoded'] = self.le.fit_transform(self.df['category'].fillna('unknown'))
        features.append('category_encoded')
        
        X = self.df[features].fillna(0)
        y = self.df['is_popular']
        
        # Split data
        X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42, stratify=y)
        
        # Train classifier
        clf = RandomForestClassifier(n_estimators=100, random_state=42)
        clf.fit(X_train, y_train)
        
        # Predictions
        y_pred = clf.predict(X_test)
        
        # Evaluate
        print("Classification Report:")
        print(classification_report(y_test, y_pred))
        
        return clf
    
    def generate_recommendations(self, user_preferences=None):
        """Generate place recommendations based on ML insights"""
        print("\n=== Generating Recommendations ===")
        
        if user_preferences is None:
            user_preferences = {
                'preferred_time': 3.0,  # hours
                'preferred_categories': ['historical', 'religious'],
                'max_distance_from_center': 100  # km from Colombo (approximation)
            }
        
        # Filter places based on preferences
        recommendations = self.df.copy()
        
        # Time preference
        time_diff = abs(recommendations['estimated_time_to_visit'] - user_preferences['preferred_time'])
        recommendations['time_score'] = 1 / (1 + time_diff)
        
        # Category preference
        recommendations['category_score'] = recommendations['category'].apply(
            lambda x: 1.0 if x in user_preferences['preferred_categories'] else 0.5
        )
        
        # Calculate overall score
        recommendations['recommendation_score'] = (
            recommendations['time_score'] * 0.4 +
            recommendations['category_score'] * 0.4 +
            (recommendations.get('popularity_score', 0.5) / 2) * 0.2
        )
        
        # Sort by score
        top_recommendations = recommendations.nlargest(10, 'recommendation_score')
        
        print("Top 10 Recommended Places:")
        for idx, row in top_recommendations.iterrows():
            print(f"{row['name']} ({row['category']}) - Score: {row['recommendation_score']:.2f}")
        
        return top_recommendations


# Example usage
def main():
    # Initialize analyzer with your downloaded CSV file
    analyzer = TravelPlacesMLAnalyzer('places_ml_ready.csv')  # or 'places.csv'
    
    # Explore data
    df = analyzer.load_and_explore_data()
    
    # Preprocess
    df = analyzer.preprocess_data()
    
    # Analyze categories
    analyzer.analyze_place_categories()
    
    # Geographic analysis
    analyzer.geographic_analysis()
    
    # Build ML models
    time_model, importance = analyzer.predict_visit_time()
    popularity_model = analyzer.classify_place_popularity()
    
    # Generate recommendations
    recommendations = analyzer.generate_recommendations()
    
    print("\n=== Analysis Complete! ===")
    print("You can now use the trained models for:")
    print("1. Predicting visit times for new places")
    print("2. Classifying place popularity")
    print("3. Generating personalized recommendations")

if __name__ == "__main__":
    main()
