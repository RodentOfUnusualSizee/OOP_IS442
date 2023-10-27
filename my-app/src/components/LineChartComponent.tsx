import React from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';

function LineChartComponent(props: { data: any; width: number; height: number; }) {
    let linechartdata = props.data

    return (
        <div className='flex-1' style={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
            <div>
                <h5>Portfolio Capital</h5>
                <LineChart width={props.width} height={props.height} data={linechartdata}>
                    <XAxis dataKey="date" />
                    <YAxis />
                    <CartesianGrid stroke="#ccc" />
                    <Tooltip />
                    <Legend />
                    <Line type="monotone" dataKey="price" stroke="#8884d8" />
                </LineChart>
            </div>
        </div>
    );
}

export default LineChartComponent;