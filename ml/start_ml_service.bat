@echo off
echo 🚀 Starting Travel Guider ML Integration System
echo.

echo 📋 Checking Python installation...
python --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Python not found! Please install Python first.
    pause
    exit /b 1
)

echo ✅ Python found!
echo.

echo 📦 Installing required packages...
pip install flask flask-cors pandas numpy scikit-learn joblib requests >nul 2>&1

echo 📂 Changing to ML directory...
cd /d "%~dp0"

echo 🧠 Starting ML API Service...
echo 🌐 Service will be available at: http://localhost:5000
echo 📊 This connects your Java backend to the ML system
echo.
echo 💡 Keep this window open while using the application
echo 🛑 Press Ctrl+C to stop the service
echo.

python flask_ml_api.py

pause
