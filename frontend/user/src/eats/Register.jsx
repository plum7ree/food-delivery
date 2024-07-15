import React, {useState} from 'react';
import axios from 'axios';
import {useNavigate} from 'react-router-dom';
import {
   Container,
   TextField,
   Button,
   Typography,
   Box,
   Avatar,
   CssBaseline
} from '@mui/material';
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import {login} from "../state/authSlice";
import {useDispatch, useSelector} from "react-redux";

const Register = () => {
   const credential = useSelector((state) => state.auth.credential);
   const [formData, setFormData] = useState({
      email: '',
      username: '',
   });
   const [error, setError] = useState('');
   const navigate = useNavigate();
   const dispatch = useDispatch();

   const handleChange = (e) => {
      setFormData({...formData, [e.target.name]: e.target.value});
   };

   const handleSubmit = async (e) => {
      e.preventDefault();
      setError('');
      try {
         const response = await axios.post('http://localhost:8080/user/api/register', formData, {
            headers: {
               Authorization: `Bearer ${credential}`,
               'Content-Type': 'application/json',
            },
            body: {
               username: formData.username,
            }
         });
         if (response.status === 200) {
            console.log('Registration successful');
            dispatch(login(response));
            navigate('/');
         }
      } catch (error) {
         console.error('Registration failed:', error);
         setError('Registration failed. Please try again.');
      }
   };

   return (
      <Container component="main" maxWidth="xs">
         <CssBaseline/>
         <Box
            sx={{
               marginTop: 8,
               display: 'flex',
               flexDirection: 'column',
               alignItems: 'center',
            }}
         >
            <Avatar sx={{m: 1, bgcolor: 'secondary.main'}}>
               <LockOutlinedIcon/>
            </Avatar>
            <Typography component="h1" variant="h5">
               Register
            </Typography>
            <Box component="form" onSubmit={handleSubmit} noValidate sx={{mt: 1}}>
               <TextField
                  margin="normal"
                  required
                  fullWidth
                  id="username"
                  label="Username"
                  name="username"
                  autoComplete="username"
                  value={formData.username}
                  onChange={handleChange}
               />
               <Button
                  type="submit"
                  fullWidth
                  variant="contained"
                  sx={{mt: 3, mb: 2}}
               >
                  Register
               </Button>
               {error && (
                  <Typography color="error" align="center">
                     {error}
                  </Typography>
               )}
            </Box>
         </Box>
      </Container>
   );
};

export default Register;