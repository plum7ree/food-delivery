// MyPage.js
import React from 'react';
import {Link} from 'react-router-dom';
import './MyPage.css';

const MyPage = () => {
   return (
      <div className="mypage-screen">
         <header className="header">
            <h1 className="logo">FoodDelivery</h1>
         </header>
         <div className="mypage-content">
            <h2>마이페이지</h2>
            <ul className="mypage-menu">
               <li>
                  <Link to="/eats/restaurant-manage">
                     <i className="fas fa-utensils"></i>
                     <span>음식점 등록</span>
                  </Link>
               </li>
               {/* 추가 메뉴 항목 */}
            </ul>
         </div>
      </div>
   );
};

export default MyPage;