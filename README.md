# Ospedale - Parcial 3 POO

## Universidad
Universidad del Norte  
Departamento de Ingeniería de Sistemas y Computación  
Programación Orientada a Objetos  

## Integrantes

DANIEL ARTURO FORERO (200101082)
GABRIELA TORRES (200050555)
SEBASTIAN RIVERA VALBUENA (200048523)

## Descripción del proyecto

Ospedale es un sistema de gestión hospitalaria desarrollado en Java Swing.  
El sistema permite registrar pacientes, registrar doctores, solicitar citas, aceptar citas, completar citas, cancelar citas, reprogramar citas, prescribir medicamentos, solicitar hospitalizaciones, aprobar hospitalizaciones, denegar hospitalizaciones y visualizar la información en tablas.

El proyecto fue refactorizado a una arquitectura MVC, separando las responsabilidades en modelos, vistas, controladores, servicios, repositorios, validadores, DTO, request, response y utilidades.

## Arquitectura utilizada

El proyecto implementa el patrón MVC:

- *Model:* contiene las entidades principales del sistema, como Patient, Doctor, Appointment, Hospitalization y Prescription.
- *View:* contiene las interfaces gráficas LoginView, AdminView, PatientView y DoctorView.
- *Controller:* recibe las solicitudes de las vistas y devuelve respuestas estandarizadas.
- *Service:* contiene la lógica de negocio del sistema.
- *Repository:* simula el almacenamiento de información en memoria.
- *DTO:* permite enviar información serializada hacia las vistas sin exponer directamente los modelos.
- *Request:* transporta los datos capturados por las vistas hacia los controladores.
- *Validation:* contiene las reglas de validación exigidas en el parcial.
- *Util:* contiene clases auxiliares para carga JSON, serialización, generación de identificadores y manejo de fechas.
- *Observer:* implementa el patrón observador para actualizar tablas automáticamente.

## Estructura principal del src

```text
src/packagee/
├── app/
├── model/
├── model/enums/
├── repository/
├── service/
├── controller/
├── dto/
├── request/
├── response/
├── validation/
├── util/
├── observer/
├── test/
├── LoginView.java
├── AdminView.java
├── PatientView.java
├── DoctorView.java
└── PanelRound.java
