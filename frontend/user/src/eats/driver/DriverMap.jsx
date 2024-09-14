// src/components/DriverMap.js
import React from 'react';
import { useSelector } from 'react-redux';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import { getDriversAsArray } from '../../state/driverSlice';

const DriverMap = () => {
  const drivers = useSelector(getDriversAsArray);

  return (
    <MapContainer center={[37.5665, 126.978]} zoom={13} style={{ height: '100vh', width: '100%' }}>
      <TileLayer
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
      />
      {drivers.map(driver => (
        <Marker key={driver.driverId} position={[driver.lat, driver.lon]}>
          <Popup>
            Driver ID: {driver.driverId}<br />
            Latitude: {driver.lat}<br />
            Longitude: {driver.lon}
          </Popup>
        </Marker>
      ))}
    </MapContainer>
  );
};

export default DriverMap;
