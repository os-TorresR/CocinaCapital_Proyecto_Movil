# Cocina Capital

App móvil Android que conecta usuarios con restaurantes de la Región de Valparaíso mediante un sistema de tickets canjeables por beneficios.

**Asignatura:** DSY1105 Desarrollo de Aplicaciones Móviles — Evaluación Parcial 2

**Integrantes:**
- Oscar Torres
- Gonzalo Urtubia
- Malcolm Ruz

## Funcionalidades

- **Registro e inicio de sesión** con validaciones. La clave se guarda como hash.
- **Locales:** lista de restaurantes con su beneficio. Con el GPS se ordenan por cercanía.
- **Tickets:** compra simulada de 1 a 10 tickets.
- **Canje:** al canjear un ticket en un local se genera un cupón con código alfanumérico y código QR.
- **Cupones:** en el Perfil aparecen los cupones por canjear. Cada cupón dura 1 hora, y al vencer sale de la lista.
- **Perfil:** el usuario puede tomarse una foto de perfil con la cámara.

## Recursos nativos

- **Ubicación (GPS):** pide permiso en tiempo de ejecución.
- **Cámara:** pide permiso en tiempo de ejecución.

## Tecnologías

- Kotlin y Jetpack Compose (Material 3)
- Arquitectura MVVM
- DataStore para guardar los datos en el teléfono
- ZXing para generar el código QR

## Pasos para ejecutar

1. Clonar el repositorio y abrir la carpeta en Android Studio (**File > Open**).
2. Esperar la sincronización de Gradle.
3. Elegir un emulador (API 33 o superior) y presionar **Run ▶**.

## Estado actual

Proyecto con registro, login, locales, tickets, cupones con QR y recursos nativos (GPS y cámara) funcionando. Los datos se guardan solo en el teléfono, sin servidor.
