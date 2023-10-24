import React,{ReactElement} from 'react';
import Header from '../components/Header';
import Footer from '../components/Footer';
import { useAuth } from '../context/AuthContext';
import Table from '../components/Table';
import { getAllEvents, getAllUsers } from '../utils/api';
import { format } from 'date-fns';
import ActivityLogModal from '../components/ActivityLogModal';

function Audit() {
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
    const [data, setData] = React.useState<Event[]>([]);
    const userId = authUser.id;
    const userRole = authUser.role;
    const userIsLoggedIn = isLoggedIn;
    const management = userRole === "admin" || userRole === "user";
    // const userId = 1;
    // const userRole = "admin";
    // const userIsLoggedIn = true;
    // const management = userRole === "admin" || userRole === "user";
    //get users
    React.useEffect(() => {
        if (!hasFetchedData) {
            const users = getAllEvents();
            users.then((response) => {
                setData(response.data)
                setHasFetchedData(true);
            }).catch((error) => {
                console.log(error);
            });
        }
    }, []);
    // create Event interface
    interface Event  {
        userId: number;
        event: string;
        timestamp: string
    }


    // create event using User interface
    let event:Event = {
        userId:0,
        event: "",
        timestamp: ""
    }
    
    let eventTableData: any[] = [];
    for(let i=data.length-1; i >= 0; i--){
        event = data[i]
        eventTableData.push(event)
    }
    
    const tableHeaders = [
            { header: 'Activity', key: 'event' },
            { header: 'Activity Timestamp', key: 'timestamp' },
            { header: 'Performed By', key: 'userId' },
        ];
    const tableTitle = 'Audit Log';
    const tableDescription = 'List of most recent Activity Data';
    const tableAction = "Click";
    const tableLink='';

    return (
        <div className="Audit">
        <Header management={management} userType={userRole} login={userIsLoggedIn} ></Header>
        <div className="py-8 px-8">
            <Table
                    tableData={eventTableData}
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

export default Audit;