/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    './src/**/*.{js,jsx,ts,tsx,vue}',
    './src/*.{js,jsx,ts,tsx}'
  ],
  theme: {
    extend:(
      {
        colors: {
          'gsblue': '#749AC7',
          'gswhite': '#FFFFFF', 
          'gsblue40': '#75B1FF', 
          'gsblue50': '#3D8DF5',
          'gsblue60': '#186ADE', //For Primary Calls to Action, Important Information
          'gsblue70': '#0D4EA6',
          'gsblue80': '#103A75',
          'gsgray20': '#DCE3E8', // Tertiary Surface/BG, Minimal Borders
          'gsgray30': '#C1CCD6',
          'gsgray40': '#9FB1BD', //For Tertiary Text, Secondary Icons
          'gsgray70': '#3E5463', //For Secondary Text, Primary Icons
          'gsgray90': '#1C2B36', //For Primary Text, High Contrast Icons
          'gsgreen20': '#EBF7ED',
          'gsgreen40': '#43C478',
          'gsgreen50': '#16A163',
          'gsgreen60': '#077D55', //For Positive Values, Success, Available, Confirmed
          'gsgreen70': '#075E45',
          'gsyellow30': '#F5C518', //For warning, mild alerts
          'gsred20': '#FADCD9',
          'gsred50': '#FA5343',
          'gsred60': '#D91F11', //Critical Alert/ Errors, Negative Values
          'gsred70': '#A1160A',
          'gsorange50': '#E86427', //For Warning, Mild Alerts
        },
      }
    ),
  },
  plugins: [],
}
