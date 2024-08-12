// pages/checkout.js
import React, { useEffect } from 'react';
import { useCart } from '../contexts/CartContext';
import { useRouter } from 'next/router';

export default function Checkout() {
    const { cart, loading } = useCart();
    const router = useRouter();

    useEffect(() => {
        if (!cart || cart.items.length === 0) {
            router.push('/cart');
        }
    }, [cart, router]);

    if (loading || !cart) {
        return <p>Loading...</p>;
    }

    const handlePayment = () => {
        // 결제 처리 로직 추가 (예: 카카오페이 연동)
        alert('Payment process would go here.');
    };

    return (
        <div>
            <h2>Checkout</h2>
            <ul>
                {cart.items.map((item) => (
                    <li key={item.id}>
                        <p>{item.product.name} - {item.quantity} x ${item.product.price}</p>
                    </li>
                ))}
            </ul>
            <p>Total: ${cart.items.reduce((acc, item) => acc + item.quantity * item.product.price, 0)}</p>
            <button onClick={handlePayment}>Proceed to Payment</button>
        </div>
    );
}
