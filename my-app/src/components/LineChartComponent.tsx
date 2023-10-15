import React from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';

function generateRandomData() {
    const data = [];

    for (let i = 1; i <= 30; i++) {
        const date = new Date();
        date.setDate(date.getDate() - i);
        const price = Math.floor(Math.random() * 20 + 60);

        data.push({ date, price });
    }

    return data;
}

function LineChartComponent() {
    const data = generateRandomData();

    return (
        <div className='flex-1' style={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
            <div>
                <h5>Portfolio Capital</h5>
                <LineChart width={600} height={300} data={data}>
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