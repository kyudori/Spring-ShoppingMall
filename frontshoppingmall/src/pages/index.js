import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import axios from 'axios';
import Link from 'next/link';  // Link 컴포넌트를 추가

export default function Home() {
  const router = useRouter();
  const [isAdmin, setIsAdmin] = useState(false);

  useEffect(() => {
    const checkAuth = async () => {
      try {
        const response = await axios.get(`${process.env.NEXT_PUBLIC_BACKEND_URL}/api/users/check-auth`, { withCredentials: true });
        if (response.data && response.data.authorities) {
          const roles = response.data.authorities.map(authority => authority.authority);
          setIsAdmin(roles.includes('ROLE_ADMIN'));
        }
      } catch (error) {
        router.push('/login');
      }
    };

    checkAuth();
  }, [router]); // `router` 의존성 배열에 추가

  return (
    <div>
      <h2>Welcome to the Home Page!</h2>
      {isAdmin && (
        <div>
          <Link href="/admin/products"> 
            <a>Manage Products</a>
          </Link>
        </div>
      )}
    </div>
  );
}