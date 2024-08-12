import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import checkAdmin from '../../utils/checkAuth';
import Image from 'next/image';
import axios from 'axios';

export default function Products() {
    const router = useRouter();
    const [isAdmin, setIsAdmin] = useState(false);
    const [products, setProducts] = useState([]);
    const [newProduct, setNewProduct] = useState({ name: '', description: '', price: '', stock: '', image: null });
    const [previewImage, setPreviewImage] = useState(null);
    const [loading, setLoading] = useState(false);
    const [editingProduct, setEditingProduct] = useState(null); // 수정할 상품 저장 상태

    const backendUrl = process.env.NEXT_PUBLIC_BACKEND_URL;

    useEffect(() => {
        async function verifyAdmin() {
            const admin = await checkAdmin();
            setIsAdmin(admin);
            if (!admin) {
                alert('You do not have permission to access this page.');
                router.push('/login');
            }
        }
        verifyAdmin();
    }, [router]);

    useEffect(() => {
        if (isAdmin) {
            const fetchProducts = async () => {
                setLoading(true);
                try {
                    const response = await axios.get(`${backendUrl}/api/products`, { withCredentials: true });
                    setProducts(response.data);
                } catch (error) {
                    alert('Failed to fetch products');
                } finally {
                    setLoading(false);
                }
            };
            fetchProducts();
        }
    }, [backendUrl, isAdmin]);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setNewProduct((prevProduct) => ({
            ...prevProduct,
            [name]: value,
        }));
    };

    const handleFileChange = (e) => {
        const file = e.target.files[0];
        setNewProduct((prevProduct) => ({
            ...prevProduct,
            image: file,
        }));
        setPreviewImage(URL.createObjectURL(file));
    };

    const handleAddProduct = async () => {
        const formData = new FormData();
    
        // JSON 데이터 파트 추가
        formData.append('product', new Blob([JSON.stringify({
            name: newProduct.name,
            description: newProduct.description,
            price: newProduct.price,
            stock: newProduct.stock
        })], { type: 'application/json' }));
    
        // 파일 파트 추가
        if (newProduct.image) {
            formData.append('file', newProduct.image, newProduct.image.name);
        }
    
        try {
            setLoading(true);
            const response = await axios.post(`${backendUrl}/api/products`, formData, {
                withCredentials: true,
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });
            alert('Product added successfully');
            setProducts([...products, response.data]);
            setNewProduct({ name: '', description: '', price: '', stock: '', image: null });
            setPreviewImage(null);
        } catch (error) {
            if (error.response && error.response.status === 403) {
                alert('You do not have permission to perform this action.');
                router.push('/login');
            } else {
                alert(`Failed to add product: ${error.message}`);
            }
        } finally {
            setLoading(false);
        }
    };    

    const handleDeleteProduct = async (id) => {
        try {
            setLoading(true);
            await axios.delete(`${backendUrl}/api/products/${id}`, { withCredentials: true });
            setProducts(products.filter(product => product.id !== id));
        } catch (error) {
            alert(`Failed to delete product: ${error.message}`);
        } finally {
            setLoading(false);
        }
    };

    const handleEditProduct = (product) => {
        setEditingProduct(product);
        setNewProduct({
            name: product.name,
            description: product.description,
            price: product.price,
            stock: product.stock,
            image: product.imageUrl ? product.imageUrl : null,
        });
        setPreviewImage(product.imageUrl ? `${backendUrl}/${product.imageUrl}` : null); // 상대 경로를 사용하여 이미지 로드
    };
    

    const handleUpdateProduct = async () => {
        if (!editingProduct) return;
    
        const formData = new FormData();
        formData.append('product', new Blob([JSON.stringify({
            name: newProduct.name,
            description: newProduct.description,
            price: newProduct.price,
            stock: newProduct.stock
        })], { type: 'application/json' }));
    
        // 이미지가 새로 설정된 경우에만 파일 파트를 추가
        if (newProduct.image && typeof newProduct.image !== 'string') {
            formData.append('file', newProduct.image, newProduct.image.name);
        }
    
        try {
            const response = await axios.put(`${backendUrl}/api/products/${editingProduct.id}`, formData, {
                withCredentials: true,
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });
            alert('Product updated successfully');
            setProducts(products.map(product => product.id === editingProduct.id ? response.data : product));
            setEditingProduct(null);
            setNewProduct({ name: '', description: '', price: '', stock: '', image: null });
            setPreviewImage(null);
        } catch (error) {
            console.error('Failed to update product:', error);
            alert(`Failed to update product: ${error.message}`);
        } finally {
            setLoading(false);
        }
    };

    if (!isAdmin) {
        return <p>Loading...</p>;  // 관리자 권한 확인 전까지 로딩 상태 표시
    }

    return (
        <div>
            <h2>Product Management</h2>
            <div>
                <input
                    type="text"
                    name="name"
                    placeholder="Product Name"
                    value={newProduct.name}
                    onChange={handleInputChange}
                />
                <input
                    type="text"
                    name="description"
                    placeholder="Product Description"
                    value={newProduct.description}
                    onChange={handleInputChange}
                />
                <input
                    type="number"
                    name="price"
                    placeholder="Product Price"
                    value={newProduct.price}
                    onChange={handleInputChange}
                />
                <input
                    type="number"
                    name="stock"
                    placeholder="Product Stock"
                    value={newProduct.stock}
                    onChange={handleInputChange}
                />
                <input
                    type="file"
                    name="image"
                    onChange={handleFileChange}
                />
                {previewImage && (
                    <div>
                        <image src={previewImage} alt="Image preview" style={{ maxWidth: '200px', marginTop: '10px' }} />
                    </div>
                )}
                {editingProduct ? (
                    <button onClick={handleUpdateProduct} disabled={loading}>
                        {loading ? 'Updating...' : 'Update Product'}
                    </button>
                ) : (
                    <button onClick={handleAddProduct} disabled={loading}>
                        {loading ? 'Adding...' : 'Add Product'}
                    </button>
                )}
            </div>
            <h3>Product List</h3>
            {loading ? (
                <p>Loading...</p>
            ) : (
                <ul>
                    {products.map((product) => (
                        <li key={product.id}>
                            {product.name} - ${product.price} - {product.stock} in stock
                            {product.imageUrl && <Image src={`${backendUrl}/${product.imageUrl}`} alt={product.name} style={{ maxWidth: '100px' }} />}
                            <button onClick={() => handleEditProduct(product)} disabled={loading}>Edit</button>
                            <button onClick={() => handleDeleteProduct(product.id)} disabled={loading}>Delete</button>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}
