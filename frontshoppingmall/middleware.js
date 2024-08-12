import { NextResponse } from 'next/server';

export function middleware(req) {
  const { cookies } = req;
  const jwt = cookies.get('JWT');

  if (!jwt) {
    return NextResponse.redirect(new URL('/login', req.url));
  }

  const url = req.nextUrl.clone();

  if (url.pathname.startsWith('/admin')) {
    // 관리 페이지에 대한 접근을 제어합니다.
    return fetch(`${process.env.NEXT_PUBLIC_BACKEND_URL}/api/users/check-auth`, {
      headers: {
        'Authorization': `Bearer ${jwt.value}`,
      },
    })
    .then(response => {
      if (response.ok) {
        return response.json();
      }
      return Promise.reject('Unauthorized');
    })
    .then(user => {
      if (user.authorities && user.authorities.some(auth => auth.authority === 'ROLE_ADMIN')) {
        return NextResponse.next();
      } else {
        return NextResponse.redirect(new URL('/', req.url));
      }
    })
    .catch(() => NextResponse.redirect(new URL('/login', req.url)));
  }

  return NextResponse.next();
}
