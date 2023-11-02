import React from 'react';

function NoStock() {
    return (
        <div className="container mx-auto max-w-screen-xl h-screen rounded-l p-8 grid place-items-center">
        <div>
            <img src="/images/Drawkit/search.png" className="w-40 mb-6 mx-auto"></img>
            <p className="text-4xl text-gray-700 font-bold mb-5">
                No information found. 
            </p>
        </div>
    </div>
    );
}

export default NoStock;