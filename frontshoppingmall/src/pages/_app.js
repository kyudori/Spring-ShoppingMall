import '../styles/globals.css';
import { useEffect } from 'react';
import { useRouter } from 'next/router';
import axios from 'axios';
import { CartProvider } from '../contexts/CartContext'; 

function MyApp({ Component, pageProps }) {
  const router = useRouter();
  const backendUrl = process.env.NEXT_PUBLIC_BACKEND_URL; 

  useEffect(() => {
    const checkAuth = async () => {
      try {
        await axios.get(`${backendUrl}/api/users/check-auth`, { withCredentials: true });
      } catch (error) {
        if (router.pathname !== '/login' && router.pathname !== '/register') {
          router.push('/login');
        }
      }
    };

    checkAuth();
  }, [router, backendUrl]);

  return (
    <CartProvider> 
      <Component {...pageProps} />
    </CartProvider>
  );
}

export default MyApp;
