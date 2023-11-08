import React, { ReactElement } from 'react';
import Header from '../components/Header';
import { useAuth } from '../context/AuthContext';
import Table from '../components/Table';
import { getAllUsers } from '../utils/api';
import { format } from 'date-fns';
import ActivityLogModal from '../components/ActivityLogModal';
// create User interface
interface User {
    id: number;
    email: string;
    firstName: string;
    lastName: string;
    role: string;
    emailVerified: boolean;
    portfolioIds: number[];
    lastLogin: string;
    lastActivity: Record<string, string>;
}
interface UserConverted {
    id: number;
    email: string;
    firstName: string;
    lastName: string;
    role: string;
    emailVerified: string;
    lastLogin: string;
    lastActivity: string;
    viewFullLog: ReactElement;
}
function AdminHome() {
    const { authUser, isLoggedIn } = useAuth();
    const [selectedId, setSelectedId] = React.useState<number>(0);
    const [showModal, setShowModal] = React.useState<boolean>(false);
    const [hasFetchedData, setHasFetchedData] = React.useState(false);
    const [data, setData] = React.useState<User[]>([]);

    const [isLoading, setIsLoading] = React.useState<boolean>(true);
    const [userId, setUserId] = React.useState<number>(1);
    const [userRole, setUserRole] = React.useState<string>("");
    const [userIsLoggedIn, setUserIsLoggedIn] = React.useState<boolean>(false);
    const management = userRole === "admin" || userRole === "user";

    //For Search
    const [searchInput, setSearchInput] = React.useState<string>('');
    const [filteredData, setFilteredData] = React.useState<User[]>([]);

    //For Pagination
    const [currentPage, setCurrentPage] = React.useState(1);
    const [rowsPerPage, setRowsPerPage] = React.useState(10);
    const [isDropdownOpen, setIsDropdownOpen] = React.useState(false);

    async function handleClick(event: React.MouseEvent<HTMLButtonElement>) {
        event.preventDefault();
        const button = event.currentTarget;
        const userId = button.getAttribute('data-id');
        if (userId) {
            setShowModal(true)
            setSelectedId(parseInt(userId))
        }
    }
    const handlePrevPage = () => {
        if (currentPage > 1) {
            setCurrentPage(currentPage - 1);
        }
    };

    const handleNextPage = () => {
        const totalPages = Math.ceil(filteredData.length / rowsPerPage);
        if (currentPage < totalPages) {
            setCurrentPage(currentPage + 1);
        }
    };
    
    const handleRowsPerPageChange = (option:number) => {
        setIsDropdownOpen(false)
        setRowsPerPage(option);
        setCurrentPage(1); // Reset the current page to 1 when the number of rows per page changes
    };

    //get users
    React.useEffect(() => {
        if (authUser) {
            setIsLoading(false);
            setUserId(authUser.id);
            setUserRole(authUser.role);
            setUserIsLoggedIn(true);
            console.log("login part");
            if (!hasFetchedData) {
                const users = getAllUsers();
                users.then((response) => {
                    setData(response.data)
                    setFilteredData(response.data)
                    setHasFetchedData(true);
                }).catch((error) => {
                    console.log(error);
                });
            }
        } else {
            console.log("auth never loaded");
        }
    }, [authUser, isLoggedIn]);

    //Search for user: name/email/id/role
    React.useEffect(() => {
        const filteredUsers = data.filter((user) => {
            const searchTerm = searchInput.toLowerCase();
            return (
                user.firstName.toLowerCase().includes(searchTerm) ||
                user.lastName.toLowerCase().includes(searchTerm) ||
                user.email.toLowerCase().includes(searchTerm) ||
                user.id.toString().includes(searchTerm) ||
                user.role.toString().includes(searchTerm)
            );
        });
        setFilteredData(filteredUsers);
    }, [searchInput]);

    // create user using User interface
    let user: User = {
        id: 0,
        email: "",
        firstName: "",
        lastName: "",
        role: "",
        emailVerified: false,
        portfolioIds: [],
        lastLogin: "",
        lastActivity: {},
    }
    let userConverted: UserConverted = {
        id: 0,
        email: "",
        firstName: "",
        lastName: "",
        role: "",
        emailVerified: "",
        lastLogin: "",
        lastActivity: "",
        viewFullLog: <></>
    }

    let userTableData: any[] = [];
    for (let i = 0; i < filteredData.length; i++) {
        user = filteredData[i]
        let emailVerified: string = "No"
        let lastActivity: string = "None"
        let lastLogin: string = "None"
        let viewFullLog: ReactElement = <button data-id={user["id"]} onClick={handleClick} className="rounded-md text-sm font-medium text-gsgray90 transition hover:bg-slate-400 bg-slate-200 p-2 " >View More</button>
        if (user["emailVerified"]) {
            emailVerified = "Yes"
        }
        if (user["lastActivity"] != null) {
            let timestampDateObj = new Date(user["lastActivity"]["timestamp"])
            lastActivity = user["lastActivity"]["event"] + " at " + format(timestampDateObj, 'yyyy-MM-dd HH:mm:ss')
        }
        else {
            viewFullLog = <div className="p-2 font-medium text-gsgray90 text-sm">No Activities</div>
        }
        if (user["lastLogin"] != null) {
            lastLogin = format(new Date(user["lastLogin"]), 'yyyy-MM-dd HH:mm:ss')
        }
        userConverted = {
            id: user["id"],
            email: user["email"],
            firstName: user["firstName"],
            lastName: user["lastName"],
            role: user["role"],
            emailVerified: emailVerified,
            lastLogin: lastLogin,
            lastActivity: lastActivity,
            viewFullLog: viewFullLog

        }
        userTableData.push(userConverted)
    }
    const tableHeaders = [
        { header: 'User ID', key: 'id' },
        { header: 'First Name', key: 'firstName' },
        { header: 'Last Name', key: 'lastName' },
        { header: 'Email Address', key: 'email' },
        { header: 'Email Verified', key: 'emailVerified' },
        { header: 'Role', key: 'role' },
        { header: 'Last Login', key: 'lastLogin' },
        { header: 'Last Activity', key: 'lastActivity' },
        { header: 'View Full Log', key: 'viewFullLog' },
    ];
    const tableTitle = 'Users';
    const tableDescription = 'List of Users and User Activities';
    const tableAction = "Click";
    const tableLink = '';
    if (isLoading) {
        return (
            <div>Loading...</div>
        )
    }
    const startIndex = (currentPage - 1) * rowsPerPage;
    const endIndex = startIndex + rowsPerPage;
    const visibleData = userTableData.slice(startIndex, endIndex);

    return (
        <div className="AdminHome">
            <Header management={management} userType={userRole} login={userIsLoggedIn} ></Header>
            <div className="relative" style={{ margin: "30px 30px 0px", width: "400px" }}>
                <div className="relative">
                    <label className="sr-only"> Search </label>
                    <input
                        type="text"
                        id="Search"
                        placeholder="Search for Name/Email/ID/Role"
                        className=" p-4 w-full rounded-md border border-gray-400 py-2.5 pe-10 shadow-sm sm:text-sm"
                        value={searchInput}
                        onChange={(e) => setSearchInput(e.target.value)}
                    />

                    <span className="absolute inset-y-0 end-0 grid w-10 place-content-center">
                        <button type="button" className="text-gray-600 hover:text-gray-700">
                            <span className="sr-only">Search</span>

                            <svg
                                xmlns="http://www.w3.org/2000/svg"
                                fill="none"
                                viewBox="0 0 24 24"
                                stroke-width="1.5"
                                stroke="currentColor"
                                className="h-4 w-4"
                            >
                                <path
                                    stroke-linecap="round"
                                    stroke-linejoin="round"
                                    d="M21 21l-5.197-5.197m0 0A7.5 7.5 0 105.196 5.196a7.5 7.5 0 0010.607 10.607z"
                                />
                            </svg>
                        </button>
                    </span>
                </div>
            </div>
            <div className="py-8 px-8">
                <Table
                    tableData={visibleData}
                    tableHeaders={tableHeaders}
                    tableTitle={tableTitle}
                    tableDescription={tableDescription}
                    tableAction={tableAction}
                    tableLink={tableLink}
                />
            </div>
            <div className="inline-flex justify-center gap-1">
                <a href="#" className="inline-flex h-8 w-8 items-center justify-center rounded border border-gray-100 bg-white text-gray-900 rtl:rotate-180" onClick={handlePrevPage}>
                    <span className="sr-only">Prev Page</span>
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-3 w-3" viewBox="0 0 20 20" fill="currentColor">
                        <path fill-rule="evenodd" d="M12.707 5.293a1 1 0 010 1.414L9.414 10l3.293 3.293a1 1 0 01-1.414 1.414l-4-4a1 1 0 010-1.414l4-4a1 1 0 011.414 0z" clip-rule="evenodd" />
                    </svg>
                </a>
                <div>
                    <label htmlFor="PaginationPage" className="sr-only">Page</label>
                    <input
                        type="text"
                        className="h-8 w-12 rounded border border-gray-100 bg-white p-0 text-center text-xs font-medium text-gray-900 [-moz-appearance:_textfield] [&::-webkit-inner-spin-button]:m-0 [&::-webkit-inner-spin-button]:appearance-none [&::-webkit-outer-spin-button]:m-0 [&::-webkit-outer-spin-button]:appearance-none"
                        value={currentPage}
                        id="PaginationPage"
                        onFocus={(e) => e.target.select()}
                        onChange={(e) => {
                            const page = e.target.value;
                            if (page !== "" && parseInt(page) >= 0) {
                                setCurrentPage(parseInt(page));
                            }
                        }}
                    />
                </div>
                <a href="#" className="inline-flex h-8 w-8 items-center justify-center rounded border border-gray-100 bg-white text-gray-900 rtl:rotate-180" onClick={handleNextPage}>
                    <span className="sr-only">Next Page</span>
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-3 w-3" viewBox="0 0 20 20" fill="currentColor">
                        <path fill-rule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clip-rule="evenodd" />
                    </svg>
                </a>
                <div className=" inline-flex h-8 px-2 items-center justify-center rounded border border-gray-100 bg-white text-center text-xs font-medium ml-2">
                    Rows Per Page: {rowsPerPage}
                </div> 
                <button onClick={isDropdownOpen ? () => setIsDropdownOpen(false) : () => setIsDropdownOpen(true)} className="inline-flex h-8 w-8 items-center justify-center rounded border border-gray-100 bg-white text-gray-900 rtl:rotate-180">
                        <span className="sr-only">Menu</span>
                        <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
                            <path fillRule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clipRule="evenodd"/>
                        </svg>
                    </button> 
                <div className="inline-block w-20 justify-center my-auto">
          

                {/* Dropdown menu */}
                <div className={isDropdownOpen ? "block end-0 z-10 rounded-md border border-gray-100 bg-white shadow-lg" : "hidden"}>
                    <div className="p-2">
                        {[10, 25, 50, 100].map((option) => (
                            <button
                                key={option}
                                onClick={() => handleRowsPerPageChange(option)}
                                className={`block rounded-lg px-4 py-2 text-sm text-gray-500 hover:bg-gray-50 hover:text-gray-700 ${option === rowsPerPage ? 'bg-gray-100' : ''
                                    }`}
                                role="menuitem"
                            >
                                {option}
                            </button>
                        ))}
                    </div>
                </div>
            </div>
            </div>
            <ActivityLogModal selectedId={selectedId} showModal={showModal} setShowModal={setShowModal} />
        </div>

    );
}

export default AdminHome;