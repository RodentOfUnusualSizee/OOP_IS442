import React from 'react';
import { getUserById } from '../utils/api';

interface UserInfoModalProps {
    selectedId: number;
    showModal: boolean;
    setShowModal: (show: boolean) => void;
}

const UserInfoModal: React.FC<UserInfoModalProps> = ({ selectedId, showModal, setShowModal }) => {
    console.log("Rendering Modal, Show Modal:", showModal);
    let userId = selectedId
    console.log(userId)
    const handleModalClose = () => {
        setShowModal(false);
    }
    let userObj = {
        id: 0,
        email: "",
        firstName: "",
        lastName: "",
        role: "",
        emailVerified: false,
        lastLogin: "",
        lastActivity: {
            event: "",
            timestamp: "",
        },
    }
    const [userData, setUserData] = React.useState<User>(userObj);
    // get user by id
    React.useEffect(() => {
        getUserById(userId).then((response) => {
            if (response.success) {
                console.log('Data fetched successfully:', response);
                setUserData(response.data);
            } else {
                console.log('No user activities found:', response.message);
                setUserData(userObj);
            }
        }).catch((error) => {
            console.log(error)
            console.error('Error fetching data:', error);
            setUserData(userObj);
        });
    }, [userId]);


    console.log(userData)
    // create User interface
    interface User {
        id: number;
        email: string;
        firstName: string;
        lastName: string;
        role: string;
        emailVerified: boolean;
        lastLogin: string; // Assuming it's in ISO date format, you can use 'Date' type if needed
        lastActivity: {
            event: string;
            timestamp: string; // Assuming it's in ISO date format, you can use 'Date' type if needed
        };
    }

    // Example usage:
    const user: User = {
        id: userData.id,
        email: userData.email,
        firstName: userData.firstName,
        lastName: userData.lastName,
        role: userData.role,
        emailVerified: userData.emailVerified,
        lastLogin: userData.lastLogin,
        lastActivity: {
            event: userData.lastActivity.event,
            timestamp: userData.lastActivity.timestamp,
        },
    };

    return (
        <div className="UserInfoModal">
            {showModal && (
                <div className="fixed inset-0 z-10 overflow-y-auto">
                    <div className="flex items-center justify-center min-h-screen pt-4 pb-20 text-center sm:block sm:p-0">
                        {/* Background overlay */}
                        <div className="fixed inset-0 transition-opacity bg-gsgray20 opacity-75" aria-hidden="true" onClick={handleModalClose}>
                            <div className="absolute inset-0 opacity-75"></div>
                        </div>

                        {/* Modal container */}
                        <div className="inline-block overflow-hidden text-left align-bottom transition-all transform bg-white rounded-lg shadow-xl sm:my-8 sm:align-middle sm:max-w-xl">
                            <div className="px-4 pt-5 pb-4 bg-white sm:p-6 sm:pb-4">
                                <div className="items-center">
                                    <div className="mt-3 text-center sm:mt-0 sm:text-center sm:ml-4">
                                        <h3 className="text-3xl font-semibold leading-6 text-gsgray90 bg-white py-4" id="modal-title">UserId - {selectedId} Info</h3>

                                        {/* Detailed List */}
                                        <div className="overflow-y-hidden overflow-x-hidden mt-4">
                                            <div className="flow-root rounded-lg border border-gray-100 py-3 shadow-sm">
                                                <dl className="-my-3 divide-y divide-gray-100 text-sm">
                                                    <div className="grid grid-cols-1 gap-1 p-3 sm:grid-cols-3 sm:gap-4">
                                                        <dt className="font-medium text-gray-900">User ID</dt>
                                                        <dd className="text-gray-700 sm:col-span-2">{user.id}</dd>
                                                    </div>

                                                    <div className="grid grid-cols-1 gap-1 p-3 sm:grid-cols-3 sm:gap-4">
                                                        <dt className="font-medium text-gray-900">Name</dt>
                                                        <dd className="text-gray-700 sm:col-span-2">{user.firstName + " "+ user.lastName}</dd>
                                                    </div>

                                                    <div className="grid grid-cols-1 gap-1 p-3 sm:grid-cols-3 sm:gap-4">
                                                        <dt className="font-medium text-gray-900">Role</dt>
                                                        <dd className="text-gray-700 sm:col-span-2">{user.role}</dd>
                                                    </div>

                                                    <div className="grid grid-cols-1 gap-1 p-3 sm:grid-cols-3 sm:gap-4">
                                                        <dt className="font-medium text-gray-900">Email Verified</dt>
                                                        <dd className="text-gray-700 sm:col-span-2">{user.emailVerified ? "Yes" : "No"}</dd>
                                                    </div>

                                                    <div className="grid grid-cols-1 gap-1 p-3 sm:grid-cols-3 sm:gap-4">
                                                        <dt className="font-medium text-gray-900">Last Login Timestamp</dt>
                                                        <dd className="text-gray-700 sm:col-span-2">
                                                            {user.lastLogin}
                                                        </dd>
                                                    </div>

                                                    <div className="grid grid-cols-1 gap-1 p-3 sm:grid-cols-3 sm:gap-4">
                                                        <dt className="font-medium text-gray-900">Last Activity</dt>
                                                        <dd className="text-gray-700 sm:col-span-2">
                                                            {user.lastActivity.event}
                                                        </dd>
                                                    </div>

                                                    <div className="grid grid-cols-1 gap-1 p-3 sm:grid-cols-3 sm:gap-4">
                                                        <dt className="font-medium text-gray-900">Last Activity Timestamp</dt>
                                                        <dd className="text-gray-700 sm:col-span-2">
                                                            {user.lastActivity.timestamp}
                                                        </dd>
                                                    </div>
                                                </dl>
                                            </div>
                                        </div>

                                        <div className="mb-3">
                                            <span id="summary"></span>
                                        </div>
                                        <hr></hr>
                                        <div className="px-4 py-3 sm:flex sm:flex-row-reverse sm:px-6">
                                            <button type="button" onClick={handleModalClose} className="inline-flex justify-center w-full px-3 py-2 text-sm font-semibold text-white bg-gsgray70 rounded-md shadow-sm hover:bg-gsgray90 sm:ml-3 sm:w-auto">Close</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default UserInfoModal;