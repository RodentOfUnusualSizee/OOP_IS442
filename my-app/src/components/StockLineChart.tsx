import React from 'react';
import {
    LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer
} from 'recharts';

function StockLineChart(props: { data: any; }) {

    let linechartdata = props.data;

    return (
        <ResponsiveContainer width="100%" height={400}>
            <LineChart
                data={linechartdata}
                margin={{
                    top: 5, right: 30, left: 20, bottom: 5,
                }}
            >
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="date" />
                <YAxis />
                <CartesianGrid stroke="#eee" strokeDasharray="5 5"/>
                <Tooltip />
                <Legend />
                <Line type="monotone" dataKey="open" stroke="#8884d8" name='Opening Price'/>
                <Line type="monotone" dataKey="close" stroke="#82ca9d" name="Closing Price" />
                <Line type="monotone" dataKey="high" stroke="#ffc658" name="High Price" />
                <Line type="monotone" dataKey="low" stroke="#d0ed57" name="Low Price" />
            </LineChart>
        </ResponsiveContainer>
    );
};

export default StockLineChart;