import React, { useEffect } from 'react';
import { useCart } from '../contexts/CartContext';
import { useRouter } from 'next/router';

export default function CartPage() {
  const { cart, loading, fetchCart, updateItemInCart, removeItemFromCart } = useCart();
  const router = useRouter();

  useEffect(() => {
    fetchCart();
  }, [fetchCart]); // `useEffect` 의존성 배열에 `fetchCart` 추가

  if (loading) {
    return <p>Loading...</p>;
  }

  // 방어적인 코드를 추가하여 에러를 방지
  if (!cart || !cart.items || cart.items.length === 0) {
    return <p>Your cart is empty.</p>;
  }

  const handleQuantityChange = (cartItemId, event) => {
    const newQuantity = parseInt(event.target.value, 10);
    if (newQuantity > 0) {
      updateItemInCart(cartItemId, newQuantity);
    }
  };

  const handleRemoveItem = (cartItemId) => {
    removeItemFromCart(cartItemId);
  };

  const handleCheckout = () => {
    router.push('/checkout');
  };

  return (
    <div>
      <h2>Your Cart</h2>
      <ul>
        {cart.items.map((item) => (
          <li key={item.id}>
            <p>{item.product.name}</p>
            <p>Price: ${item.product.price}</p>
            <input
              type="number"
              value={item.quantity}
              onChange={(e) => handleQuantityChange(item.id, e)}
            />
            <button onClick={() => handleRemoveItem(item.id)}>Remove</button>
          </li>
        ))}
      </ul>
      <button onClick={handleCheckout}>Proceed to Checkout</button>
    </div>
  );
}