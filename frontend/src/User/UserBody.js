import React, { useState } from "react";
import ItineraryModal from "./ItineraryModal";
import "./UserBody.css";

// Example data, replace with real recommendations
const recommendedPlaces = [
  {
    id: 1,
    image: "/media/kandy.jpeg",
    title: "Kandy",
    description: "A beautiful city in the hills, famous for the Temple of the Tooth and scenic lake."
  },
  {
    id: 2,
    image: "/media/nineArch.jpg",
    title: "Nine Arch Bridge",
    description: "Iconic colonial-era railway bridge surrounded by lush greenery in Ella."
  },
  {
    id: 3,
    image: "/media/sigiriya.jpeg",
    title: "Sigiriya",
    description: "Ancient rock fortress with stunning views and beautiful frescoes."
  },
  {
    id: 4,
    image: "/media/kandy.jpeg",
    title: "Temple of the Tooth",
    description: "Sacred Buddhist site in Kandy, a UNESCO World Heritage Site."
  },
  {
    id: 5,
    image: "/media/nineArch.jpg",
    title: "Ella Gap",
    description: "A scenic gap in the hills with breathtaking views."
  },
  {
    id: 6,
    image: "/media/sigiriya.jpeg",
    title: "Pidurangala Rock",
    description: "A popular hike with panoramic views of Sigiriya and the surrounding jungle."
  },
];

function UserBody() {
  const [liked, setLiked] = useState({});
  const [start, setStart] = useState(0);
  const [showItineraryModal, setShowItineraryModal] = useState(false);
  const [sampleItinerary] = useState({
    daily_itinerary: [
      {
        day: 1,
        total_places: 2,
        places: [
          {
            Place: "Temple of the Tooth",
            Category: "Religious",
            Region: "Kandy",
            Description: "Sacred Buddhist temple housing a relic of Buddha's tooth",
            Latitude: 7.2936,
            Longitude: 80.6337,
            Ticket_price: "500",
            Contact_no: "+94812234567",
            Eestimated_time_to_visit: "2",
            start_time: "09:00",
            end_time: "11:00"
          },
          {
            Place: "Kandy Lake",
            Category: "Nature",
            Region: "Kandy", 
            Description: "Beautiful artificial lake in the heart of Kandy",
            Latitude: 7.2906,
            Longitude: 80.6337,
            Ticket_price: "0",
            Contact_no: "N/A",
            Eestimated_time_to_visit: "1",
            start_time: "11:30",
            end_time: "12:30"
          }
        ]
      },
      {
        day: 2,
        total_places: 2,
        places: [
          {
            Place: "Sigiriya Rock Fortress",
            Category: "Historical",
            Region: "Sigiriya",
            Description: "Ancient rock fortress with stunning frescoes and views",
            Latitude: 7.9570,
            Longitude: 80.7603,
            Ticket_price: "3900",
            Contact_no: "+94662285049",
            Eestimated_time_to_visit: "3",
            start_time: "08:00",
            end_time: "11:00"
          },
          {
            Place: "Dambulla Cave Temple",
            Category: "Religious",
            Region: "Dambulla",
            Description: "Ancient cave temple complex with beautiful Buddha statues",
            Latitude: 7.8567,
            Longitude: 80.6490,
            Ticket_price: "1500",
            Contact_no: "+94662284767",
            Eestimated_time_to_visit: "2",
            start_time: "14:00",
            end_time: "16:00"
          }
        ]
      },
      {
        day: 3,
        total_places: 1,
        places: [
          {
            Place: "Galle Fort",
            Category: "Historical",
            Region: "Galle",
            Description: "UNESCO World Heritage site with colonial architecture",
            Latitude: 6.0215,
            Longitude: 80.2168,
            Ticket_price: "0",
            Contact_no: "+94912232423",
            Eestimated_time_to_visit: "3",
            start_time: "09:00",
            end_time: "12:00"
          }
        ]
      }
    ],
    total_days: 3,
    total_places: 5,
    travel_style: "Cultural",
    interests: ["Religious", "Historical", "Nature"]
  });
  const cardsPerPage = 3;
  const end = start + cardsPerPage;

  const toggleLike = (id) => {
    setLiked((prev) => ({ ...prev, [id]: !prev[id] }));
  };

  const handlePrev = () => {
    setStart((prev) => Math.max(0, prev - cardsPerPage));
  };
  const handleNext = () => {
    setStart((prev) => (prev + cardsPerPage < recommendedPlaces.length ? prev + cardsPerPage : prev));
  };

  const handleViewItinerary = () => {
    setShowItineraryModal(true);
  };

  const handleCloseItinerary = () => {
    setShowItineraryModal(false);
  };

  return (
    <div className="user-body">
      {/* Itinerary Quick Access Section */}
      <div className="itinerary-section">
        <h2 className="user-body-title">Your Travel Plans</h2>
        <div className="itinerary-quick-view">
          <div className="itinerary-card-mini">
            <div className="itinerary-preview">
              <h3>Sri Lanka Cultural Tour</h3>
              <div className="preview-details">
                <span>3 days • 5 places • Cultural</span>
              </div>
            </div>
            <button 
              className="view-itinerary-btn"
              onClick={handleViewItinerary}
            >
              View Full Itinerary
            </button>
          </div>
          <div className="create-new-mini">
            <h4>Plan New Trip</h4>
            <button className="create-new-btn">Create Itinerary</button>
          </div>
        </div>
      </div>

      {/* Recommended Places Section */}
      <h2 className="user-body-title">Recommended Places</h2>
      <div className="user-card-list">
        <button className="user-arrow user-arrow-left" onClick={handlePrev} disabled={start === 0}>&lt;</button>
        <div className="user-card-list-inner">
          {recommendedPlaces.slice(start, end).map((place) => (
            <div className="user-card" key={place.id}>
              <div className="user-card-img-wrap">
                <img src={place.image} alt={place.title} className="user-card-img" />
                <button
                  className={`user-card-like${liked[place.id] ? " liked" : ""}`}
                  onClick={() => toggleLike(place.id)}
                  aria-label={liked[place.id] ? "Unlike" : "Like"}
                >
                  {liked[place.id] ? "❤️" : "🤍"}
                </button>
              </div>
              <div className="user-card-content">
                <h3>{place.title}</h3>
                <p>{place.description}</p>
              </div>
            </div>
          ))}
        </div>
        <button className="user-arrow user-arrow-right" onClick={handleNext} disabled={end >= recommendedPlaces.length}>&gt;</button>
      </div>
      
      {/* Itinerary Modal */}
      <ItineraryModal
        isOpen={showItineraryModal}
        itineraryData={sampleItinerary}
        onClose={handleCloseItinerary}
      />
    </div>
  );
}

export default UserBody;
