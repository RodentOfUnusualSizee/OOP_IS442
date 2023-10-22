import React from 'react';
import { PieChart, Pie, Cell, Legend, Tooltip } from 'recharts';

function PieChartComponent(props: { data: any; }) {

    let piechartdata = props.data

    const colors = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042'];

    return (
        <div className="flex-1" style={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
            <div>
                <h5>Investment Division</h5>
                <PieChart width={400} height={300}>
                    <Pie
                        data={piechartdata}
                        dataKey="value"
                        nameKey="name"
                        cx="50%"
                        cy="50%"
                        outerRadius={80}
                        fill="#8884d8"
                        label
                    >
                        {piechartdata.map((entry : any, index : number) => (
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
