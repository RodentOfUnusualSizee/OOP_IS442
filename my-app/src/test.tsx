import { Routes, Route } from "react-router-dom"
import Home from "./Home";
import About from "./About";
import Contact from "./Contact";
function App() { return (
<div className="App">      
 <Routes>         
    <Route path="/" element={<Dashboard />} />         
    <Route path="services" element={<Services />} />         
    <Route path="contact" element={<Contact />} />       
    </Routes>     
    </div>) 
}
export default App
