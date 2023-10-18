import React from 'react';
import { Route, Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

interface ProtectedRouteProps {
  path: string;
  element: React.ReactElement;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ path, element }) => {
  const { isLoggedIn } = useAuth();

  return isLoggedIn ? <Route path={path} element={element} /> : <Navigate to="/" replace />;
};

export default ProtectedRoute;
