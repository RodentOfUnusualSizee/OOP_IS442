import React from 'react';

function Loading() {
    return (
        <div className="container mx-auto max-w-screen-xl h-screen rounded-l p-8 grid place-items-center">
            <div>
                <img src="/images/Drawkit/loading.png" className="w-40 mb-6 mx-auto"></img>
                <p className="text-4xl text-gray-700 font-bold mb-5">
                    Loading... please give us a moment.
                </p>
            </div>
        </div>
    );
}

export default Loading;