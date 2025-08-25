import React from "react";
import "./UpcomingEvents.css";

// Example static data for Sri Lankan festivals/events (replace with API or dynamic data as needed)
const events = [
  {
    id: 1,
    name: "Esala Perahera",
    date: "2025-08-10",
    location: "Kandy",
    description: "One of the grandest Buddhist festivals in Sri Lanka, featuring processions with dancers, drummers, and decorated elephants.",
    image: "/media/kandy-perahera.jpg"
  },
  {
    id: 2,
    name: "Sinhala & Tamil New Year",
    date: "2025-04-14",
    location: "Islandwide",
    description: "Traditional New Year celebrated by Sinhalese and Tamil communities with games, rituals, and feasting.",
    image: "/media/avurudu.jpg"
  },
  {
    id: 3,
    name: "Vesak Festival",
    date: "2025-05-12",
    location: "Islandwide",
    description: "Celebration of the birth, enlightenment, and passing of Lord Buddha. Streets are decorated with lanterns and pandals.",
    image: "/media/vesak.jpg"
  },
  {
    id: 4,
    name: "Nallur Festival",
    date: "2025-08-20",
    location: "Jaffna",
    description: "A major Hindu festival at the Nallur Kandaswamy Kovil, featuring processions and traditional music.",
    image: "/media/nallur.jpg"
  }
];

function UpcomingEvents() {
  // Filter for events in the future (from today)
  const today = new Date();
  const upcoming = events.filter(e => new Date(e.date) >= today).sort((a, b) => new Date(a.date) - new Date(b.date));

  return (
    <div className="upcoming-events">
      <h2 className="events-title">Upcoming Festivals & Events</h2>
      <div className="events-list">
        {upcoming.length === 0 ? (
          <div className="no-events">No major upcoming events.</div>
        ) : (
          upcoming.map(event => (
            <div className="event-card" key={event.id}>
              <img src={event.image} alt={event.name} className="event-img" />
              <div className="event-info">
                <h3>{event.name}</h3>
                <div className="event-date">{new Date(event.date).toLocaleDateString('en-GB', { year: 'numeric', month: 'long', day: 'numeric' })}</div>
                <div className="event-location">{event.location}</div>
                <p className="event-desc">{event.description}</p>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
}

export default UpcomingEvents;
