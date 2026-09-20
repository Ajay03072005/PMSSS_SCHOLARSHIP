/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        gov: {
          navy: '#0b2545',
          blue: '#134074',
          teal: '#006466',
          accent: '#f77f00',
          light: '#eef4f8',
          border: '#cbd5e1',
          card: '#ffffff'
        }
      }
    },
  },
  plugins: [],
}
