import './HeroSection.css';

function HeroSection() {
  return (
    <section className="hero">
      <div className="animation-container">
        <video
          src="/media/animation.mp4"
          autoPlay
          loop
          muted
          className="hero-video"
        />
      </div>
      <center>
      <button className="get-started-btn">Get Started</button>
      </center>
    </section>
  );
}

export default HeroSection;
