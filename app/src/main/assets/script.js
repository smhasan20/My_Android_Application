gsap.to(".animation-element", {
    duration: 1,
    opacity: 1,
    y: 0,
    ease: "power4.out",
    delay: 0.5,
    onComplete: function() {
      gsap.to(".animation-element img", { duration: 1, rotation: 360 });
    }
  });
  