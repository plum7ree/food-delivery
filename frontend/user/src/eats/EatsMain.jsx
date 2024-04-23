import React, {useContext, useEffect, useState} from 'react';
import { Link } from 'react-router-dom';

import './EatsMain.css';
import {useDispatch, useSelector} from "react-redux";
import {fetchProfilePicture} from "../state/profilePictureSlice";


const EatsMain = () => {
  const categories = [
    { name: '피자', icon: '🍕' },
    { name: '치킨', icon: '🍗' },
    { name: '햄버거', icon: '🍔' },
    { name: '아시안', icon: '🍜' },
    { name: '멕시코', icon: '🌮' },
    { name: '디저트', icon: '🍰' },
  ];

  const dispatch = useDispatch();
  const profilePictureUrl = useSelector((state) => state.profilePicture.url);

  useEffect(() => {
    dispatch(fetchProfilePicture());
  }, [dispatch]);

  return (
      <div className="eats-main-screen">
          <header className="header">
              <h1 className="logo">FoodDelivery</h1>
              <div className="user-info">
                  <Link to="/eats/mypage">
                      <div className="profile-picture">
                          {profilePictureUrl ? (
                              <img src={profilePictureUrl} alt="Profile"/>
                          ) : (
                              <i className="fas fa-user-circle"></i>
                          )}
                      </div>
                  </Link>
              </div>
          </header>
          <div className="search-bar">
              <i className="fas fa-search"></i>
              <input type="text" placeholder="음식점 또는 음식 검색"/>
          </div>
          <div className="categories">
              <h2>인기 카테고리</h2>
              <div className="category-list">
                  {categories.map((category, index) => (
                      <div key={index} className="category-item">
                          <div className="category-icon">{category.icon}</div>
                          <span className="category-name">{category.name}</span>
                      </div>
                  ))}
              </div>
          </div>
          <div className="featured-restaurants">
              <h2>인기 음식점</h2>
              {/* 인기 음식점 목록 */}
          </div>
      </div>
  );
};

export default EatsMain;