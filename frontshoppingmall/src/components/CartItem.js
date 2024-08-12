// components/CartItem.js
import React from 'react';
import Image from 'next/image';

export default function CartItem({ item, onQuantityChange, onRemove }) {
    return (
        <div className="cart-item">
            <Image src={item.product.imageUrl} alt={item.product.name} style={{ width: '100px', height: '100px' }} />
            <div className="item-details">
                <h4>{item.product.name}</h4>
                <p>${item.product.price}</p>
                <div>
                    <label>
                        Quantity:
                        <input
                            type="number"
                            value={item.quantity}
                            onChange={(e) => onQuantityChange(item.id, e.target.value)}
                            min="1"
                        />
                    </label>
                </div>
                <button onClick={() => onRemove(item.id)}>Remove</button>
            </div>
        </div>
    );
}
