import React, {useState, useEffect, useRef, useContext} from "react";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import axios from "axios";
import "./UberLikeApp.css";
import {useSelector} from "react-redux";

const axiosInstance = axios.create({
  baseURL: 'http://localhost:8072'
});

const UberLikeApp = () => {
  const [activeTab, setActiveTab] = useState('home');
  const [address, setAddress] = useState("");
  const [serviceType, setServiceType] = useState("regular");
  const [isSearchingDriver, setIsSearchingDriver] = useState(false);
  const [searchResults, setSearchResults] = useState(null);
  const [currentPosition, setCurrentPosition] = useState(null);
  const [routeData, setRouteData] = useState(null);
  const mapRef = useRef(null);
  const markerRef = useRef(null);
  const currentPositionMarkerRef = useRef(null);
  const polylineRef = useRef(null);

  const profilePictureUrl = useSelector((state) => state.profilePicture.url);
    // update profile
  useEffect(() => {
    const fetchProfilePicture = async () => {
      try {
        const response = await axiosInstance.get("/user/api/profile-picture");
        console.log("profile picture : ", response.data);
        // 프로필 사진을 받아와서 상태에 설정
        setProfilePictureUrl(response.data);
      } catch (error) {
        console.error("Error fetching profile picture:", error);
      }
    };
    fetchProfilePicture(); // 프로필 사진 가져오기 함수 호출
  }, []);

  useEffect(() => {
    if (mapRef.isInitialized) return;
    mapRef.isInitialized = true
    const map = L.map(mapRef.current).setView([37.5665, 126.978], 13);
    // mapRef 에 leaflet map 을 add 함.
    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
      attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors',
      maxZoom: 18,
    }).addTo(map);

    mapRef.current = map;

    console.log("mapRef.current: ", mapRef.current)
    
    const layerGroup = L.layerGroup().addTo(map);
  
    // 현재 위치 권한 획득
    map.locate({ watch: true, enableHighAccuracy: true });
  
    // 현재 위치 변경 이벤트 리스너
    map.on("locationfound", (e) => {
      console.log("location found!")
      const { lat, lng } = e.latlng;
      setCurrentPosition({ lat, lng });
  
      if (!currentPositionMarkerRef.current) {
        currentPositionMarkerRef.current = L.marker([lat, lng]).addTo(layerGroup);
      } else {
        currentPositionMarkerRef.current.setLatLng([lat, lng]);
      }
    });
  
    return () => {
      console.log("before map remove: " , mapRef.current)

      // map.remove();
      console.log("after map remove: " , mapRef.current)
    };
  }, []);

  useEffect(() => {
    if (searchResults) {
      const { lat, lon } = searchResults;
      const newMarker = L.marker([lat, lon]);

      // 이전 마커가 있으면 제거
      if (markerRef.current) {
        mapRef.current.removeLayer(markerRef.current);
      }

      // 새로운 마커를 지도에 추가하고 markerRef.current 업데이트
      newMarker.addTo(mapRef.current);
      markerRef.current = newMarker;

      // 새로운 위치로 지도 이동
      mapRef.current.setView([lat, lon], 16);
    }
  }, [searchResults]);

  useEffect(() => {
    if (routeData && mapRef.current) {
      console.log("route data useEffect");
      const { pointList } = routeData;
      const pathCoordinates = pointList.map((point) => [point.lat, point.lon]);
  
      if (polylineRef.current) {
        mapRef.current.removeLayer(polylineRef.current);
      }
      
      const polyline = L.polyline(pathCoordinates, { color: "red" });
      console.log("map: ", mapRef, polyline)
      polyline.addTo(mapRef.current); // Leaflet 지도 객체에 직접 추가
  
      console.log("new polyline");
      console.log(polyline);
      polylineRef.current = polyline;
    }
  }, [routeData]);
  


  const handleSearch = (e) => {
    e.preventDefault();
    // 주소 검색 로직 구현
    const params = {
      text: address, // 검색어
      // 다른 파라미터들도 추가 가능
    };
  
    axiosInstance
      .get("/route/api/address/search", { params })
      .then((response) => {
        // response.data :
        // {
        //   "housenumber": null,
        //   "street": "",
        //   "city": "",
        //   "postalCode": null,
        //   "name": "경복궁(정부서울청사)",
        //   "buildingType": null,
        //   "lat": 37.57576,
        //   "lon": 126.973564,
        //   "osmid": 355173059
        // }
        setSearchResults({lat:response.data.lat, lon:response.data.lon})
        console.log(response)
        console.log(currentPosition)
        const params = {
          startLat: currentPosition.lat,
          startLon: currentPosition.lng, //TODO change name from startLon -> startLng
          destLat: response.data.lat,
          destLon: response.data.lon,
        }
        axiosInstance.get("/route/api/query", {params})
        .then((response) => {
          console.log(response.data)
          // response.data:
          // { pointList: Array 
          //   instructionList: Array
          // }
          //TODO draw path into a map here
          setRouteData(response.data);


        })
      })
      .catch((error) => {
        console.error(error);
      });
  };

  const handleCall = (e) => {
    e.preventDefault();
    setIsSearchingDriver(true);
    // 호출 로직 구현
    axiosInstance
      .post("/call", { serviceType })
      .then((response) => {
        // 호출 결과 처리
        
      })
      .catch((error) => {
        console.error(error);
      });
  };

  const handleCancel = () => {
    setIsSearchingDriver(false);
  };

  return (
    <div className="app-container">
      <header className="header">
        <div className="tabs">
          <div
            className={`tab ${activeTab === 'home' ? 'active' : ''}`}
            onClick={() => setActiveTab('home')}
          >
            홈
          </div>
          <div
            className={`tab ${activeTab === 'orders' ? 'active' : ''}`}
            onClick={() => setActiveTab('orders')}
          >
            주문내역
          </div>
          {/* 추가 탭 */}
        </div>
        <div className="profile-picture">
        {profilePictureUrl && <img src={profilePictureUrl} alt="Profile"/>}
        </div>
      </header>
  <div className="top-container">
        <div className="map-container">
          <div className="search-bar">
            <form onSubmit={handleSearch}>
              <input
                type="text"
                name="address"
                placeholder="주소 검색"
                value={address}
                onChange={(e) => setAddress(e.target.value)}
              />
            </form>
          </div>
          <div ref={mapRef} style={{ height: "100%" }}></div>
          {isSearchingDriver && (
            <div id="searchingDriver">운전자 검색 중...</div>
          )}
        </div>
      </div>
      <div className="controls-container">
        {!isSearchingDriver && (
          <form onSubmit={handleCall}>
            <button type="submit" id="callButton">
              호출하기
            </button>
          </form>
        )}
        {isSearchingDriver && (
          <button type="button" id="cancelButton" onClick={handleCancel}>
            호출 취소
          </button>
        )}
      </div>
    </div>
  );
};

export default UberLikeApp;