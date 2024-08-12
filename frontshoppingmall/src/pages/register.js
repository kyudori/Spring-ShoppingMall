import { useState } from 'react';
import axios from 'axios';
import { useRouter } from 'next/router';

export default function Register() {
  const [formData, setFormData] = useState({
    userid: '',
    email: '',
    nickname: '',
    fullName: '',
    password: '',
    confirmPassword: '',
    birthDate: '',
    phoneNumber: ''
  });

  const router = useRouter();
  const backendUrl = process.env.NEXT_PUBLIC_BACKEND_URL;

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleRegister = async () => {
    if (formData.password !== formData.confirmPassword) {
      alert('Passwords do not match');
      return;
    }

    try {
      const response = await axios.post(`${backendUrl}/api/users/register`, formData);
      alert(response.data);
      router.push('/login');
    } catch (error) {
      alert('Registration failed');
    }
  };

  return (
    <div>
      <h2>Register</h2>
      <input type="text" name="userid" placeholder="UserID" value={formData.userid} onChange={handleChange} />
      <input type="email" name="email" placeholder="Email" value={formData.email} onChange={handleChange} />
      <input type="text" name="nickname" placeholder="Nickname" value={formData.nickname} onChange={handleChange} />
      <input type="text" name="fullName" placeholder="Full Name" value={formData.fullName} onChange={handleChange} />
      <input type="password" name="password" placeholder="Password" value={formData.password} onChange={handleChange} />
      <input type="password" name="confirmPassword" placeholder="Confirm Password" value={formData.confirmPassword} onChange={handleChange} />
      <input type="date" name="birthDate" value={formData.birthDate} onChange={handleChange} />
      <input type="text" name="phoneNumber" placeholder="Phone Number" value={formData.phoneNumber} onChange={handleChange} />
      <button onClick={handleRegister}>Register</button>
    </div>
  );
}
