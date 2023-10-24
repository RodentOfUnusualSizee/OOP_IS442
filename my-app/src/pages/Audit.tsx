import React, { ReactElement } from 'react';
import Header from '../components/Header';
import Footer from '../components/Footer';
import { useAuth } from '../context/AuthContext';
import Table from '../components/Table';
import { getAllEvents } from '../utils/api';
import { format } from 'date-fns';
import UserInfoModal from '../components/UserInfoModal';
// create Event interface
interface Event {
    userId: number;
    event: string;
    timestamp: string
}
interface EventTableRow {
    userId: number;
    event: string;
    timestamp: string;
    viewUser: ReactElement
}
function Audit() {
    const { authUser, isLoggedIn } = useAuth();
    const [selectedId, setSelectedId] = React.useState<number>(0);
    const [showModal, setShowModal] = React.useState<boolean>(false);
    const [hasFetchedData, setHasFetchedData] = React.useState(false);
    const [data, setData] = React.useState<Event[]>([]);

    const [isLoading, setIsLoading] = React.useState<boolean>(true);
    const [userId, setUserId] = React.useState<number>(1);
    const [userRole, setUserRole] = React.useState<string>("");
    const [userIsLoggedIn, setUserIsLoggedIn] = React.useState<boolean>(false);
    const management = userRole === "admin" || userRole === "user";

    //For Search
    const [searchInput, setSearchInput] = React.useState<string>('');
    const [filteredData, setFilteredData] = React.useState<Event[]>([]);

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

    //get events
    React.useEffect(() => {
        if (authUser) {
            setIsLoading(false);
            setUserId(authUser.id);
            setUserRole(authUser.role);
            setUserIsLoggedIn(true);
            console.log("login part");
            if (!hasFetchedData) {
                const users = getAllEvents();
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
    //Search for event: activity/userid/timestamp
    React.useEffect(() => {
        const filteredUsers = data.filter((event) => {
            const searchTerm = searchInput.toLowerCase();
            return (
                event.event.toLowerCase().includes(searchTerm) ||
                event.userId.toString().toLowerCase().includes(searchTerm) ||
                event.timestamp.toLowerCase().includes(searchTerm)
            );
        });
        setFilteredData(filteredUsers);
    }, [searchInput]);

    // create event using User interface
    let event: Event = {
        userId: 0,
        event: "",
        timestamp: ""
    }

    let eventTableRow: EventTableRow = {
        userId: 0,
        event: "",
        timestamp: "",
        viewUser: <div></div>
    }

    let eventTableData: any[] = [];
    for (let i = filteredData.length - 1; i >= 0; i--) {
        event = filteredData[i]
        eventTableRow = {
            userId: event.userId,
            event: event.event,
            timestamp: event.timestamp,
            viewUser: <button data-id={event["userId"]} onClick={handleClick} className="rounded-md text-sm font-medium text-gsgray90 transition hover:bg-slate-400 bg-slate-200 p-2 " >View More</button>
        }
        eventTableData.push(eventTableRow)
    }

    const tableHeaders = [
        { header: 'Activity', key: 'event' },
        { header: 'Activity Timestamp', key: 'timestamp' },
        { header: 'Performed By', key: 'userId' },
        { header: 'View User', key: 'viewUser' },
    ];
    const tableTitle = 'Audit Log';
    const tableDescription = 'List of most recent Activity Data';
    const tableAction = "Click";
    const tableLink = '';
    if (isLoading) {
        return (
            <div>Loading...</div>
        )
    }

    const startIndex = (currentPage - 1) * rowsPerPage;
    const endIndex = startIndex + rowsPerPage;
    const visibleData = eventTableData.slice(startIndex, endIndex);

    return (
        <div className="Audit">
            <Header management={management} userType={userRole} login={userIsLoggedIn} ></Header>
            <div className="relative" style={{ margin: "30px 30px 0px", width: "400px" }}>
                <div className="relative">
                    <label className="sr-only"> Search </label>
                    <input
                        type="text"
                        id="Search"
                        placeholder="Search for Activity/Timestamp/UserID"
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
            <div className="inline-flex justify-center gap-1 mb-8">
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
            
           

            <UserInfoModal selectedId={selectedId} showModal={showModal} setShowModal={setShowModal} />
        </div>
    );
}

export default Audit;