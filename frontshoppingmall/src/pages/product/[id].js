import { useRouter } from 'next/router';
import React, { useState, useEffect } from 'react';
import { useCart } from '../../contexts/CartContext';
import axios from 'axios';

export default function ProductPage() {
    const router = useRouter();
    const { id } = router.query;
    const [product, setProduct] = useState(null);
    const [quantity, setQuantity] = useState(1);
    const { addItemToCart, loading } = useCart();
    const backendUrl = process.env.NEXT_PUBLIC_BACKEND_URL;

    useEffect(() => {
        if (id) {
            const fetchProduct = async () => {
                try {
                    const response = await axios.get(`${backendUrl}/api/products/${id}`);
                    setProduct(response.data);
                } catch (error) {
                    console.error("Failed to fetch product:", error);
                }
            };
            fetchProduct();
        }
    }, [id, backendUrl]);

    if (!product) {
        return <p>Loading...</p>;
    }

    const handleAddToCart = async () => {
        try {
            await addItemToCart(product.id, quantity);
            console.log("Item added to cart successfully");
            router.push('/cart');
        } catch (error) {
            console.error("Failed to add item to cart:", error);
        }
    };

    return (
        <div>
            <h2>{product.name}</h2>
            <p>{product.description}</p>
            <p>Price: ${product.price}</p>
            <input
                type="number"
                min="1"
                value={quantity}
                onChange={(e) => setQuantity(parseInt(e.target.value, 10))}
            />
            <button onClick={handleAddToCart} disabled={loading}>
                {loading ? 'Adding...' : 'Add to Cart'}
            </button>
        </div>
    );
}
