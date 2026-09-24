module.exports = {
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        brand: {
          50: '#e0f9ff',
          100: '#b3f2ff',
          500: '#00d2ff', // Primary Cyber Blue
          700: '#0099cc',
          secondary: '#8a2be2',
          accent: '#ff007f',
        },
        surface: {
          canvas: '#0a0c16',
          card: '#12172a',
          border: '#1e293b',
          muted: '#64748b',
          dark: '#0a0c16',
        },
        semantic: {
          success: '#00f0aa',
          warning: '#ffb703',
          danger: '#ff3366',
          info: '#00d2ff',
        }
      },
      backgroundImage: {
        'brand-gradient': 'linear-gradient(90deg, #00d2ff 0%, #8a2be2 50%, #ff007f 100%)',
      }
    },
  },
  plugins: [],
};
