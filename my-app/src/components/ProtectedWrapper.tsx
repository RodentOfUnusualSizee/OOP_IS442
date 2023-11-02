import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

interface ProtectedWrapperProps {
  element: React.ReactElement;
}

const ProtectedWrapper: React.FC<ProtectedWrapperProps> = ({ element }) => {
  const { authUser } = useAuth();

  if (authUser) {
    return element;
  }

  return <Navigate to="/" replace />;
};

export default ProtectedWrapper;
