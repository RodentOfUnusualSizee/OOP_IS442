import React,{ReactElement} from 'react';
import Header from '../components/Header';
import Footer from '../components/Footer';
import { useAuth } from '../context/AuthContext';
import Table from '../components/Table';
import { getAllUsers } from '../utils/api';
import { format } from 'date-fns';
import ActivityLogModal from '../components/ActivityLogModal';

function AdminHome() {
    async function handleClick(event: React.MouseEvent<HTMLButtonElement>){
        event.preventDefault();
        const button = event.currentTarget;
        const userId = button.getAttribute('data-id');
        if (userId) {
            setShowModal(true)
            setSelectedId(parseInt(userId))
        }
    }
    const { authUser, isLoggedIn } = useAuth();
    const [selectedId, setSelectedId] = React.useState<number>(0);
    const [showModal, setShowModal] = React.useState<boolean>(false);
    const [hasFetchedData, setHasFetchedData] = React.useState(false);
    const [data, setData] = React.useState<User[]>([]);
    // const userId = authUser.id;
    // const userRole = authUser.role;
    // const userIsLoggedIn = isLoggedIn;
    // const management = userRole === "admin" || userRole === "user";
    const userId = 1;
    const userRole = "admin";
    const userIsLoggedIn = true;
    const management = userRole === "admin" || userRole === "user";
    //get users
    React.useEffect(() => {
        if (!hasFetchedData) {
            const users = getAllUsers();
            users.then((response) => {
                setData(response.data)
                setHasFetchedData(true);
            }).catch((error) => {
                console.log(error);
            });
        }
    }, [userId]);
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

    // create user using User interface
    let user:User = {
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
    for(let i = 0; i < data.length; i++){
        user = data[i]
        let emailVerified:string = "No"
        let lastActivity:string = "None"
        let lastLogin:string = "None"
        let viewFullLog:ReactElement =<button data-id={user["id"]} onClick={handleClick} className="rounded-md text-sm font-medium text-gsgray90 transition hover:bg-slate-400 bg-slate-200 p-2 " >View More</button>
        if(user["emailVerified"]){
            emailVerified = "Yes"
        }
        if(user["lastActivity"] != null){
            let timestampDateObj = new Date(user["lastActivity"]["timestamp"])
            lastActivity = user["lastActivity"]["event"]+" at " + format(timestampDateObj, 'yyyy-MM-dd HH:mm:ss')
        }
        else{
            viewFullLog = <div className="p-2 font-medium text-gsgray90 text-sm">No Activities</div>
        }
        if(user["lastLogin"] != null){
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
    const tableDescription = 'List of user data and activities';
    const tableAction = "Click";
    const tableLink='';

    return (
        <div className="AdminHome">
        <Header management={management} userType={userRole} login={userIsLoggedIn} ></Header>
        <div className="py-8 px-8">
            <Table
                    tableData={userTableData}
                    tableHeaders={tableHeaders}
                    tableTitle={tableTitle}
                    tableDescription={tableDescription}
                    tableAction={tableAction}
                    tableLink={tableLink}
                />
        </div>
        <ActivityLogModal selectedId={selectedId} showModal={showModal} setShowModal={setShowModal}/>
        </div>
    );
}

export default AdminHome;