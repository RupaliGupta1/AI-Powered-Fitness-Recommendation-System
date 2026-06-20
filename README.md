🏋️‍♂️ AI-Powered Fitness Recommendation System

An intelligent, microservices-based fitness application designed to help users track their daily activities, monitor calorie intake, and receive personalized fitness recommendations powered by AI.

🚀 Project Overview

This application is built using a Microservices Architecture with Java, Spring Boot, and React, enabling scalability, modularity, and high performance. It leverages AI-driven insights to provide personalized health and fitness recommendations based on user behavior and activity data.

🧩 Key Features 📊 Track user activities, workouts, and calorie intake 🧠 AI-powered personalized fitness recommendations 🔐 Secure authentication and authorization ⚡ Asynchronous event-driven communication 📱 Responsive React-based frontend 🏗️ Architecture & Microservices User Service – Manages user profiles and authentication data Activity Service – Tracks workouts, steps, and calorie consumption AI Recommendation Service – Generates personalized fitness suggestions using AI

Includes:

Service Discovery for dynamic service registration API Gateway for centralized routing and request handling 🤖 AI Integration

Integrated Spring AI with Gemini API to analyze user activity patterns and generate:

Personalized workout suggestions Health and diet recommendations Behavioral fitness insights ⚙️ Tech Stack Backend: Java, Spring Boot, Spring Cloud Frontend: React.js AI: Spring AI, Gemini API Messaging: RabbitMQ Security: Keycloak (OAuth 2.0 PKCE) Architecture: Microservices, API Gateway, Service Discovery 🔄 System Design Highlights Asynchronous communication between services using message queues Centralized configuration using Spring Cloud Config Server Secure token-based authentication with OAuth 2.0 PKCE flow Scalable and loosely coupled microservices architecture 🔒 Security Secured REST APIs using Keycloak OAuth 2.0 PKCE-based authentication for frontend-backend communication Role-based access control for users 🎯 Goal of the Project

To build a scalable, AI-driven fitness platform that not only tracks user activity but also provides intelligent, personalized health recommendations using modern microservices and AI technologies.

