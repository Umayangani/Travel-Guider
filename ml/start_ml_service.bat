@echo off
echo 🚀 Starting Travel Guider ML Service...

REM Check if Python is installed
python --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Python is not installed or not in PATH
    echo Please install Python 3.8+ and try again
    pause
    exit /b 1
)

REM Navigate to ML directory
cd /d "%~dp0"

REM Check if virtual environment exists
if not exist "venv" (
    echo 📦 Creating virtual environment...
    python -m venv venv
)

REM Activate virtual environment
echo 🔄 Activating virtual environment...
call venv\Scripts\activate.bat

REM Install/upgrade requirements
echo 📥 Installing/updating Python packages...
pip install -r requirements.txt

REM Check if CSV data exists
if not exist "..\backend\uploads\places.csv" (
    echo ⚠️  Warning: places.csv not found in backend/uploads/
    echo The ML service will start but may not have data to work with
    echo Please ensure the backend has imported place data
)

REM Start the ML API service
echo 🎯 Starting ML API service on http://localhost:5000...
echo Press Ctrl+C to stop the service
python ml_api_service.py

pause
