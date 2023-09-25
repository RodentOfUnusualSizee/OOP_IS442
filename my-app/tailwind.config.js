/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.{html,js,ts,tsx}"],
  theme: {
    colors: {
      'gs-blue': '#749AC7',
      'gs-white': '#FFFFFF'
    }
  },
  purge: ['./src/**/*.{js,jsx,ts,tsx}'],
  plugins: [],
}

