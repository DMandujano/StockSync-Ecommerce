import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vuetify from 'vite-plugin-vuetify'

export default defineConfig({
    plugins: [vue(), vuetify()],
    // 1. ELIMINADO 'base': La app debe servirse desde la raíz '/' para evitar conflictos
    server: {
        port: 5173,
        proxy: {
            // Esto solo se usa en desarrollo
            '/api': {
                target: 'http://localhost:8080',
                changeOrigin: true
            }
        },
    },
    build: {
        // 2. CORREGIDO: Esto envía los archivos compilados DIRECTO al static del backend
        // Ajusta la ruta '..' si tu frontend no está al mismo nivel que backend
        outDir: '../backend/src/main/resources/static',
        emptyOutDir: true, // Limpia la carpeta antes de copiar los nuevos archivos
    },
})