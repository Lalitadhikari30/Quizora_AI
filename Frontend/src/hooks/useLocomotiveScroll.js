import LocomotiveScroll from 'locomotive-scroll';
import { useEffect, useRef } from 'react';
import { useLocation } from 'react-router-dom';

const useLocomotiveScroll = (options = {}) => {
  const scrollRef = useRef(null);
  const scrollInstance = useRef(null);
  const location = useLocation();

  useEffect(() => {
    if (scrollRef.current) {
      scrollInstance.current = new LocomotiveScroll({
        el: scrollRef.current,
        smooth: true,
        multiplier: 1,
        duration: 1.8,
        easing: (t) => Math.min(1, 1.001 - Math.pow(2, -10 * t)),
        smartphone: {
          smooth: true,
          multiplier: 1,
        },
        tablet: {
          smooth: true,
          multiplier: 1,
        },
        ...options,
      });

      // Clean up on unmount
      return () => {
        if (scrollInstance.current) {
          scrollInstance.current.destroy();
        }
      };
    }
  }, [options]);

  useEffect(() => {
    // Update scroll on route changes
    if (scrollInstance.current) {
      scrollInstance.current.update();
      scrollInstance.current.scrollTo(0, { duration: 0 });
    }
  }, [location.pathname]);

  useEffect(() => {
    // Handle browser back/forward
    const handlePopState = () => {
      if (scrollInstance.current) {
        scrollInstance.current.update();
        scrollInstance.current.scrollTo(0, { duration: 0 });
      }
    };

    window.addEventListener('popstate', handlePopState);
    
    return () => {
      window.removeEventListener('popstate', handlePopState);
    };
  }, []);

  return { scrollRef, scrollInstance };
};

export default useLocomotiveScroll;
