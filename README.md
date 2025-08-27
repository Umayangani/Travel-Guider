# Travel Guider

A comprehensive travel guide application for Sri Lanka with machine learning-powered recommendations.

## Project Structure

```
Travel-Guider/
├── backend/                 # Spring Boot backend application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/       # Java source code
│   │   │   └── resources/  # Configuration and static resources
│   │   └── test/           # Test classes
│   ├── pom.xml            # Maven configuration
│   └── uploads/           # File upload directory
├── frontend/              # React frontend application
│   ├── src/              # React source code
│   ├── public/           # Static assets
│   └── package.json      # Node.js dependencies
├── ml/                   # Machine learning components
│   ├── auto_retrain_model.py
│   ├── places_ml_data.csv
│   └── test_ml_integration.py
└── README.md            # This file
```

## Features

- Place management with detailed information
- Entry fee management system
- CSV import/export functionality
- Machine learning recommendations
- User authentication and authorization
- Responsive web interface

## Technology Stack

### Backend
- Spring Boot 3.x
- Java 17+
- Maven
- MySQL/H2 Database
- Spring Security
- OpenCSV

### Frontend
- React 18
- JavaScript/TypeScript
- CSS3
- Responsive Design

### Machine Learning
- Python
- Pandas
- Scikit-learn

## Getting Started

### Prerequisites
- Java 17 or higher
- Node.js 16 or higher
- Maven 3.6+
- MySQL (optional, H2 for development)

### Backend Setup
1. Navigate to backend directory
```bash
cd backend
```

2. Install dependencies and run
```bash
./mvnw spring-boot:run
```

### Frontend Setup
1. Navigate to frontend directory
```bash
cd frontend
```

2. Install dependencies
```bash
npm install
```

3. Start development server
```bash
npm start
```

## API Endpoints

### Places Management
- `GET /api/places` - Get all places
- `POST /api/places` - Create new place
- `PUT /api/places/{id}` - Update place
- `DELETE /api/places/{id}` - Delete place

### CSV Operations
- `POST /api/csv/export` - Export places to CSV
- `POST /api/csv/upload/{type}` - Upload CSV file
- `POST /api/csv/import/{type}` - Import data from CSV
- `GET /api/csv/download/{type}` - Download CSV file

### Entry Fees
- `GET /api/entry-fees` - Get all entry fees
- `POST /api/entry-fees` - Create entry fee
- `PUT /api/entry-fees/{id}` - Update entry fee

## Database Schema

### Places Table
- place_id (Primary Key)
- name
- district
- description
- region
- category
- estimated_time_to_visit
- latitude
- longitude

### Entry Fees Table
- fee_id (Primary Key)
- place_id (Foreign Key)
- foreign_adult
- foreign_child
- local_adult
- local_child
- student
- free_entry

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License.
AI powered Customize travel guider
