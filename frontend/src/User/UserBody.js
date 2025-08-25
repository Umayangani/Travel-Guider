import React, { useState } from "react";
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

  return (
    <div className="user-body">
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
    </div>
  );
}

export default UserBody;
