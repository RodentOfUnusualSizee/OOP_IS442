import React from 'react';
import { PieChart, Pie, Cell, Legend, Tooltip } from 'recharts';


function PieChartComponent() {
    const data = [
        { name: 'Category A', value: Math.floor(Math.random() * 50) + 10 },
        { name: 'Category B', value: Math.floor(Math.random() * 50) + 10 },
        { name: 'Category C', value: Math.floor(Math.random() * 50) + 10 },
        { name: 'Category D', value: Math.floor(Math.random() * 50) + 10 },
    ];

    const colors = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042'];

    return (
        <div className="flex-1" style={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
            <div>
                <h5>Investment Division</h5>
                <PieChart width={400} height={300}>
                    <Pie
                        data={data}
                        dataKey="value"
                        nameKey="name"
                        cx="50%"
                        cy="50%"
                        outerRadius={80}
                        fill="#8884d8"
                        label
                    >
                        {data.map((entry, index) => (
                            <Cell key={`cell-${index}`} fill={colors[index % colors.length]} />
                        ))}
                    </Pie>
                    <Legend verticalAlign="bottom" height={36} />
                    <Tooltip />
                </PieChart>
            </div>
        </div>
    );
}

export default PieChartComponent;
