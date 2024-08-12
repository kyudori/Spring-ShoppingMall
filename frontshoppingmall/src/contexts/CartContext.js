import React, { createContext, useContext, useState } from 'react';
import axios from 'axios';

const CartContext = createContext();

export const useCart = () => useContext(CartContext);

export const CartProvider = ({ children }) => {
    const [cart, setCart] = useState({ items: [] });
    const [loading, setLoading] = useState(false);
    const backendUrl = process.env.NEXT_PUBLIC_BACKEND_URL;

    const fetchCart = async () => {
        setLoading(true);
        try {
            const response = await axios.get(`${backendUrl}/api/cart`, { withCredentials: true });
            setCart(response.data);
        } catch (error) {
            console.error('Failed to fetch cart', error);
        } finally {
            setLoading(false);
        }
    };

    const addItemToCart = async (productId, quantity) => {
        setLoading(true);
        try {
            const response = await axios.post(`${backendUrl}/api/cart/add`, {
                productId,
                quantity
            }, { withCredentials: true });
            setCart(response.data);
        } catch (error) {
            console.error('Failed to add item to cart', error);
        } finally {
            setLoading(false);
        }
    };

    const updateItemInCart = async (cartItemId, quantity) => {
        setLoading(true);
        try {
            const response = await axios.put(`${backendUrl}/api/cart/update/${cartItemId}`, {
                quantity
            }, { withCredentials: true });
            setCart(response.data);
        } catch (error) {
            console.error('Failed to update cart item', error);
        } finally {
            setLoading(false);
        }
    };

    const removeItemFromCart = async (cartItemId) => {
        setLoading(true);
        try {
            const response = await axios.delete(`${backendUrl}/api/cart/remove/${cartItemId}`, { withCredentials: true });
            setCart(response.data);
        } catch (error) {
            console.error('Failed to remove cart item', error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <CartContext.Provider value={{ cart, loading, fetchCart, addItemToCart, updateItemInCart, removeItemFromCart }}>
            {children}
        </CartContext.Provider>
    );
};
