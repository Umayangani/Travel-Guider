"""
Travel Places ML Training Script
================================
This script trains a machine learning model using the places.csv data
for travel recommendations and itinerary planning.
"""

import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestRegressor, RandomForestClassifier
from sklearn.preprocessing import LabelEncoder, StandardScaler
from sklearn.metrics import accuracy_score, mean_squared_error
import joblib
import os

class TravelPlacesMLTrainer:
    def __init__(self, csv_path="../backend/uploads/places.csv"):
        self.csv_path = csv_path
        self.place_classifier = None
        self.popularity_predictor = None
        self.label_encoders = {}
        self.scaler = StandardScaler()
        
    def load_and_prepare_data(self):
        """Load and prepare the places data for ML training"""
        print("📊 Loading places data...")
        df = pd.read_csv(self.csv_path)
        print(f"✅ Loaded {len(df)} places")
        
        # Clean and prepare data
        df = df.dropna(subset=['Name', 'District', 'Category'])
        
        # Create features
        df['name_length'] = df['Name'].str.len()
        df['description_length'] = df['Description'].fillna('').str.len()
        df['description_word_count'] = df['Description'].fillna('').str.split().str.len()
        
        # Create popularity score based on multiple factors
        df['popularity_score'] = (
            (df['name_length'] < 50).astype(int) * 0.3 +  # Shorter names are more memorable
            (df['description_length'] > 100).astype(int) * 0.4 +  # Good descriptions
            (df['Category'].isin(['Beach', 'Temple', 'Scenic'])).astype(int) * 0.3  # Popular categories
        )
        
        # Encode categorical variables
        categorical_cols = ['District', 'Region', 'Category']
        for col in categorical_cols:
            if col in df.columns:
                le = LabelEncoder()
                df[f'{col.lower()}_encoded'] = le.fit_transform(df[col].fillna('Unknown'))
                self.label_encoders[col] = le
        
        # Create time category
        df['time_category'] = pd.cut(df['Eestimated_time_to_visit'].fillna(2), 
                                   bins=[0, 2, 4, 8, float('inf')], 
                                   labels=['short', 'medium', 'long', 'full_day'])
        
        self.data = df
        return df
    
    def train_place_classifier(self):
        """Train a classifier to predict place categories based on features"""
        print("🤖 Training place category classifier...")
        
        # Features for classification
        feature_cols = ['name_length', 'description_length', 'description_word_count',
                       'district_encoded', 'region_encoded']
        
        X = self.data[feature_cols].fillna(0)
        y = self.data['category_encoded']
        
        # Split data
        X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
        
        # Scale features
        X_train_scaled = self.scaler.fit_transform(X_train)
        X_test_scaled = self.scaler.transform(X_test)
        
        # Train classifier
        self.place_classifier = RandomForestClassifier(n_estimators=100, random_state=42)
        self.place_classifier.fit(X_train_scaled, y_train)
        
        # Evaluate
        y_pred = self.place_classifier.predict(X_test_scaled)
        accuracy = accuracy_score(y_test, y_pred)
        print(f"✅ Place classifier accuracy: {accuracy:.3f}")
        
        return accuracy
    
    def train_popularity_predictor(self):
        """Train a model to predict place popularity scores"""
        print("⭐ Training popularity predictor...")
        
        # Features for popularity prediction
        feature_cols = ['name_length', 'description_length', 'description_word_count',
                       'category_encoded', 'district_encoded', 'region_encoded']
        
        X = self.data[feature_cols].fillna(0)
        y = self.data['popularity_score']
        
        # Split data
        X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
        
        # Train regressor
        self.popularity_predictor = RandomForestRegressor(n_estimators=100, random_state=42)
        self.popularity_predictor.fit(X_train, y_train)
        
        # Evaluate
        y_pred = self.popularity_predictor.predict(X_test)
        mse = mean_squared_error(y_test, y_pred)
        print(f"✅ Popularity predictor MSE: {mse:.3f}")
        
        return mse
    
    def save_models(self):
        """Save trained models and encoders"""
        print("💾 Saving models...")
        
        # Create models directory
        os.makedirs('models', exist_ok=True)
        
        # Save models
        joblib.dump(self.place_classifier, 'models/place_classifier.joblib')
        joblib.dump(self.popularity_predictor, 'models/popularity_predictor.joblib')
        joblib.dump(self.scaler, 'models/scaler.joblib')
        joblib.dump(self.label_encoders, 'models/label_encoders.joblib')
        
        print("✅ Models saved to ./models/ directory")
    
    def train_complete_pipeline(self):
        """Run the complete training pipeline"""
        print("🚀 Starting ML training pipeline...")
        
        # Load and prepare data
        df = self.load_and_prepare_data()
        
        # Train models
        classifier_accuracy = self.train_place_classifier()
        popularity_mse = self.train_popularity_predictor()
        
        # Save models
        self.save_models()
        
        # Export ML-ready data
        ml_ready_path = '../backend/uploads/places_ml_ready.csv'
        df.to_csv(ml_ready_path, index=False)
        print(f"✅ ML-ready data exported to {ml_ready_path}")
        
        print("\n🎉 ML Training Complete!")
        print(f"📊 Trained on {len(df)} places")
        print(f"🎯 Classifier accuracy: {classifier_accuracy:.3f}")
        print(f"⭐ Popularity MSE: {popularity_mse:.3f}")
        
        return {
            'total_places': len(df),
            'classifier_accuracy': classifier_accuracy,
            'popularity_mse': popularity_mse
        }

if __name__ == "__main__":
    trainer = TravelPlacesMLTrainer()
    results = trainer.train_complete_pipeline()
