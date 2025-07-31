#!/usr/bin/env python3
"""
Auto-retrain ML model for Travel Guider
Automatically retrains the travel recommendation model when new place data is available
"""

import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestRegressor, RandomForestClassifier
from sklearn.cluster import KMeans
from sklearn.preprocessing import StandardScaler, LabelEncoder
from sklearn.metrics import mean_squared_error, accuracy_score, silhouette_score
import joblib
import os
import json
import logging
from datetime import datetime

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('ml_training.log'),
        logging.StreamHandler()
    ]
)

class TravelMLAutoTrainer:
    def __init__(self, ml_dir='./'):
        self.ml_dir = ml_dir
        self.models_dir = os.path.join(ml_dir, 'models')
        self.data_dir = os.path.join(ml_dir, 'data')
        
        # Create directories if they don't exist
        os.makedirs(self.models_dir, exist_ok=True)
        os.makedirs(self.data_dir, exist_ok=True)
        
        self.scaler = StandardScaler()
        self.label_encoders = {}
        
    def load_data(self):
        """Load the ML-ready dataset"""
        try:
            csv_path = os.path.join(self.ml_dir, 'places_ml_dataset.csv')
            if not os.path.exists(csv_path):
                logging.error(f"ML dataset not found at {csv_path}")
                return None
                
            df = pd.read_csv(csv_path)
            logging.info(f"Loaded {len(df)} records from ML dataset")
            return df
        except Exception as e:
            logging.error(f"Error loading data: {e}")
            return None
    
    def preprocess_data(self, df):
        """Preprocess data for training"""
        try:
            # Select features for training
            feature_columns = [
                'category_encoded', 'region_encoded', 'district_encoded',
                'popularity_score', 'accessibility_score', 'cost_level',
                'total_activities', 'avg_entry_fee', 'fee_variance'
            ]
            
            # Check if all required columns exist
            missing_cols = [col for col in feature_columns if col not in df.columns]
            if missing_cols:
                logging.warning(f"Missing columns: {missing_cols}")
                feature_columns = [col for col in feature_columns if col in df.columns]
            
            X = df[feature_columns].copy()
            
            # Handle missing values
            X = X.fillna(X.mean())
            
            # Scale features
            X_scaled = self.scaler.fit_transform(X)
            
            # Create target variables
            y_popularity = df['popularity_score'] if 'popularity_score' in df.columns else np.random.rand(len(df))
            y_category = df['category_encoded'] if 'category_encoded' in df.columns else np.random.randint(0, 5, len(df))
            
            logging.info(f"Preprocessed data: {X_scaled.shape[0]} samples, {X_scaled.shape[1]} features")
            return X_scaled, y_popularity, y_category, feature_columns
            
        except Exception as e:
            logging.error(f"Error preprocessing data: {e}")
            return None, None, None, None
    
    def train_popularity_model(self, X, y_popularity):
        """Train popularity prediction model"""
        try:
            X_train, X_test, y_train, y_test = train_test_split(X, y_popularity, test_size=0.2, random_state=42)
            
            model = RandomForestRegressor(n_estimators=100, random_state=42)
            model.fit(X_train, y_train)
            
            # Evaluate
            y_pred = model.predict(X_test)
            mse = mean_squared_error(y_test, y_pred)
            
            logging.info(f"Popularity model trained - MSE: {mse:.4f}")
            
            # Save model
            model_path = os.path.join(self.models_dir, 'popularity_model.pkl')
            joblib.dump(model, model_path)
            
            return model, mse
            
        except Exception as e:
            logging.error(f"Error training popularity model: {e}")
            return None, None
    
    def train_category_model(self, X, y_category):
        """Train category prediction model"""
        try:
            X_train, X_test, y_train, y_test = train_test_split(X, y_category, test_size=0.2, random_state=42)
            
            model = RandomForestClassifier(n_estimators=100, random_state=42)
            model.fit(X_train, y_train)
            
            # Evaluate
            y_pred = model.predict(X_test)
            accuracy = accuracy_score(y_test, y_pred)
            
            logging.info(f"Category model trained - Accuracy: {accuracy:.4f}")
            
            # Save model
            model_path = os.path.join(self.models_dir, 'category_model.pkl')
            joblib.dump(model, model_path)
            
            return model, accuracy
            
        except Exception as e:
            logging.error(f"Error training category model: {e}")
            return None, None
    
    def train_clustering_model(self, X):
        """Train clustering model for place grouping"""
        try:
            # Determine optimal number of clusters (3-8 range)
            best_score = -1
            best_k = 5
            
            for k in range(3, min(9, len(X))):
                kmeans = KMeans(n_clusters=k, random_state=42)
                cluster_labels = kmeans.fit_predict(X)
                score = silhouette_score(X, cluster_labels)
                
                if score > best_score:
                    best_score = score
                    best_k = k
            
            # Train final model
            model = KMeans(n_clusters=best_k, random_state=42)
            model.fit(X)
            
            logging.info(f"Clustering model trained - K: {best_k}, Silhouette Score: {best_score:.4f}")
            
            # Save model
            model_path = os.path.join(self.models_dir, 'clustering_model.pkl')
            joblib.dump(model, model_path)
            
            return model, best_score
            
        except Exception as e:
            logging.error(f"Error training clustering model: {e}")
            return None, None
    
    def save_training_metadata(self, feature_columns, metrics):
        """Save training metadata and metrics"""
        try:
            metadata = {
                'training_date': datetime.now().isoformat(),
                'feature_columns': feature_columns,
                'model_metrics': metrics,
                'scaler_info': {
                    'mean': self.scaler.mean_.tolist() if hasattr(self.scaler, 'mean_') else [],
                    'scale': self.scaler.scale_.tolist() if hasattr(self.scaler, 'scale_') else []
                }
            }
            
            # Save scaler
            scaler_path = os.path.join(self.models_dir, 'scaler.pkl')
            joblib.dump(self.scaler, scaler_path)
            
            # Save metadata
            metadata_path = os.path.join(self.models_dir, 'training_metadata.json')
            with open(metadata_path, 'w') as f:
                json.dump(metadata, f, indent=2)
            
            logging.info("Training metadata saved successfully")
            
        except Exception as e:
            logging.error(f"Error saving metadata: {e}")
    
    def run_auto_training(self):
        """Run complete auto-training pipeline"""
        logging.info("Starting auto-training pipeline...")
        
        # Load data
        df = self.load_data()
        if df is None or len(df) == 0:
            logging.error("No data available for training")
            return False
        
        # Preprocess
        X, y_popularity, y_category, feature_columns = self.preprocess_data(df)
        if X is None:
            logging.error("Data preprocessing failed")
            return False
        
        metrics = {}
        
        # Train models
        popularity_model, pop_mse = self.train_popularity_model(X, y_popularity)
        if popularity_model:
            metrics['popularity_mse'] = pop_mse
        
        category_model, cat_accuracy = self.train_category_model(X, y_category)
        if category_model:
            metrics['category_accuracy'] = cat_accuracy
        
        clustering_model, cluster_score = self.train_clustering_model(X)
        if clustering_model:
            metrics['clustering_silhouette'] = cluster_score
        
        # Save metadata
        self.save_training_metadata(feature_columns, metrics)
        
        logging.info("Auto-training pipeline completed successfully")
        return True

def main():
    """Main function for auto-retraining"""
    trainer = TravelMLAutoTrainer()
    success = trainer.run_auto_training()
    
    if success:
        print("✅ ML models retrained successfully")
        exit(0)
    else:
        print("❌ ML model training failed")
        exit(1)

if __name__ == "__main__":
    main()
