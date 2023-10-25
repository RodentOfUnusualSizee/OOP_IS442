import React, { useRef } from 'react';
import emailjs from '@emailjs/browser';
import Header from '../components/Header';
import Footer from '../components/Footer';

function ResetPassword() {

    // user input
    const [email, setEmail] = React.useState<string>("");
    const [password, setPassword] = React.useState<string>("");
    const [error, setError] = React.useState<string>("");

    // email
    const form = useRef<HTMLFormElement | null>(null);
    const sendEmail = (e: { preventDefault: () => void; }) => {
        e.preventDefault();

        if (form.current) {
            emailjs.sendForm('service_b9qji9i', 'template_cxznyah', form.current, 'V5ApWLVix_pK2OOTn')
            .then((result) => {
                console.log(result.text);
            }, (error) => {
                console.log(error.text);
            });
        }
    };

    return (
    <div>
            <Header management={false} userType="admin" login={false}></Header>
            <div className="container mx-auto max-w-screen-xl h-screen rounded-l p-8 grid place-items-center">
                <div>
                    <img src="/images/gs-blue.png" className="w-20 mb-6 mx-auto"></img>
                    <p className="text-4xl text-gray-700 font-bold mb-5 mx-20 px-20">
                        Reset Password
                    </p>
                    <form className="bg-white shadow-md rounded p-8 m-10" ref={form} onSubmit={sendEmail}>
                        <div className="mb-4">
                            <input className="shadow appearance-none border rounded w-full py-2 px-3 my-2 text-gsgray70 leading-tight"
                                id="email"
                                type="text"
                                placeholder="Email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                required
                            >
                            </input>
                        </div>
                        <div className="mb-4">
                            <input className="shadow appearance-none border rounded w-full py-2 px-3 text-gsgray70 leading-tight"
                                id="password"
                                type="password"
                                placeholder="Password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required>
                            </input>
                        </div>
                        <div id="errorMessage" className="text-gsred60 text-sm font-light w-30 pb-2 px-2">
                            {error}
                        </div>
                        <div id="loginButtons">
                            <input className="bg-gsblue60 hover:bg-gsblue70 text-white font-light w-30 py-2 px-2 rounded mx-2" type="submit" value="Reset Password" />
                        </div>
                    </form>
                </div>
            </div>
            <Footer></Footer>
        </div>
    );
}

export default ResetPassword;
