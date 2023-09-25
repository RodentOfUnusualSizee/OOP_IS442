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
          'gsgray90': '#1C2B36'
        },
      }
    ),
  },
  plugins: [],
}
