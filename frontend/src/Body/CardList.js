import React, { useState } from "react";
import "./CardList.css";

function CardList() {
  const [preferred, setPreferred] = useState([]);

  const togglePreference = (id) => {
    if (preferred.includes(id)) {
      setPreferred(preferred.filter(pid => pid !== id));
    } else {
      setPreferred([...preferred, id]);
    }
  };

  const cards = [
    { id: 1, image: "/images/place1.jpg", description: "Ella Rock – Beautiful hiking destination." },
    { id: 2, image: "/images/place2.jpg", description: "Mirissa Beach – Perfect sunset and surfing spot." },
    { id: 3, image: "/images/place3.jpg", description: "Nuwara Eliya – Tea gardens in the misty hills." },
    { id: 4, image: "/images/place4.jpg", description: "Sigiriya – The ancient rock fortress." },
    { id: 5, image: "/images/place5.jpg", description: "Yala National Park – Wildlife safari adventures." }
  ];

  return (
    <div className="card-list">
      {cards.map((card) => (
        <div className="card" key={card.id}>
          <img src={card.image} alt="Place" className="card-image" />
          <div className="card-content">
            <p className="card-description">{card.description}</p>
            <span
              className={`heart-icon ${preferred.includes(card.id) ? "liked" : ""}`}
              onClick={() => togglePreference(card.id)}
              title={preferred.includes(card.id) ? "Unlike" : "Like"}
            >
              {preferred.includes(card.id) ? "❤️" : "🤍"}
            </span>
          </div>
        </div>
      ))}
    </div>
  );
}

export default CardList;
