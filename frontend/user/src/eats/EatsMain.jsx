import React, {useContext, useEffect, useState} from 'react';
import {Link, useNavigate} from 'react-router-dom';

import './EatsMain.css';
import {useDispatch, useSelector} from "react-redux";
import {fetchProfilePicture} from "../state/fetchProfilePicture";


const EatsMain = () => {
  const categories = [
    { name: '피자', icon: '🍕', type: 'pizza'},
    { name: '치킨', icon: '🍗' , type: 'chicken'},
    { name: '햄버거', icon: '🍔' , type: 'BURGER'},
    { name: '아시안', icon: '🍜' , type: 'asian'},
    { name: '멕시코', icon: '🌮' , type: 'mexican'},
    { name: '디저트', icon: '🍰', type: 'desert' },
  ];

  const dispatch = useDispatch();
  const profilePictureUrl = useSelector((state) => state.profilePicture.url);
  const navigate = useNavigate();

  useEffect(() => {
    dispatch(fetchProfilePicture());
  }, [dispatch]);
  const handleCategoryClick = (categoryType) => {
    // 해당 카테고리의 타입을 URL에 포함하여 페이지를 이동합니다.
    navigate(`/eats/restaurants/${categoryType}`, {
       state: {
          type: `${categoryType}`
       }
     });
  };
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
                      // key 존재 필수
                     <div key={index} onClick={() => handleCategoryClick(category.type)}>
                        <div className="category-item">
                           <div className="category-icon">{category.icon}</div>
                           <span className="category-name">{category.name}</span>
                        </div>
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