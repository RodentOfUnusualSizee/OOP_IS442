import React,{ReactElement} from 'react';
import Header from '../components/Header';
import Footer from '../components/Footer';
import { useAuth } from '../context/AuthContext';
import Table from '../components/Table';
import { getActivityLogById } from '../utils/api';
import { format } from 'date-fns';
interface ActivityLogModalProps {
    selectedId:number;
    showModal: boolean;
    setShowModal: (show: boolean) => void;
  }

const ActivityLogModal: React.FC<ActivityLogModalProps> = ({ selectedId, showModal, setShowModal }) => {
    console.log("Rendering Modal, Show Modal:", showModal);
    let userId = selectedId
    console.log(userId)
    const handleModalClose = () => {
        setShowModal(false);
    }
    const [eventData, setEventData] = React.useState<Event[]>([]);
    // get events
    React.useEffect(() => {
    getActivityLogById(userId).then((response) => {
        if (response.success) {
          console.log('Data fetched successfully:', response);
          setEventData(response["data"]["allEvents"]);
        } else {
          console.log('No user activities found:', response.message);
          setEventData([]);
        }
      }).catch((error) => {
        console.error('Error fetching data:', error);
        setEventData([]);
      });
}, [userId]);


    console.log(eventData)
    // create User interface
    interface Event {
        event:string;
        timestamp:string;
        }

    // create user using User interface
    let event:Event = {
        event:"",
        timestamp:"",
    }
  
    
    let eventTableData: any[] = [];
    for(let i = 0; i < eventData.length; i++){
        event = eventData[i]
        let timestamp = format(new Date(event["timestamp"]), 'yyyy-MM-dd HH:mm:ss');
        eventTableData.push({event:event['event'], timestamp:timestamp})
    }

    const tableHeaders = [
        { header: 'Event', key: 'event' },
        { header: 'Timestamp', key: 'timestamp' },

        ];
    const tableTitle = '';
    const tableDescription = '';
    const tableAction = "View Events";
    const tableLink='localhost:8080/api/user/1/activity-log';

    return (
        <div className="ActivityLogModal">
    {showModal && (
        <div className="fixed inset-0 z-10 overflow-y-auto">
            <div className="flex items-center justify-center min-h-screen pt-4 pb-20 text-center sm:block sm:p-0">
                {/* Background overlay */}
                <div className="fixed inset-0 transition-opacity bg-gsgray20 opacity-75" aria-hidden="true" onClick={handleModalClose}>
                    <div className="absolute inset-0 opacity-75"></div>
                </div>

                {/* Modal container */}
                <div className="inline-block overflow-hidden text-left align-bottom transition-all transform bg-white rounded-lg shadow-xl sm:my-8 sm:align-middle sm:max-w-lg sm:w-full">
                    <div className="px-4 pt-5 pb-4 bg-white sm:p-6 sm:pb-4">
                        <div className="items-center">
                            <div className="mt-3 text-center sm:mt-0 sm:text-center sm:ml-4">
                                <h3 className="text-3xl font-semibold leading-6 text-gsgray90 bg-white py-4" id="modal-title">User Activity log</h3>

                                {/* Scrollable table container */}
                                <div className="overflow-y-scroll overflow-x-hidden" style={{ maxHeight: '550px' }}>
                                    <Table
                                        tableData={eventTableData}
                                        tableHeaders={tableHeaders}
                                        tableTitle={tableTitle}
                                        tableDescription={tableDescription}
                                        tableAction={tableAction}
                                        tableLink={tableLink}
                                    />
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

export default ActivityLogModal;