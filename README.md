#  Food Ordering System

A web-based Food Ordering System developed using Spring Boot, Java, HTML, CSS, Thymeleaf, and H2 Database.

## 📌 Project Description

This project is a simple food ordering web application where users can browse available food items, add items to their cart, and place orders.

The application provides a user-friendly interface for viewing food items and managing the shopping cart.

## ✨ Features

- View available food items
- Food categories such as pizza, burger, and dessert
- Add food items to cart
- View shopping cart
- Calculate order total
- Place an order
- Order success page
- Simple admin page
- Responsive web interface

## 🛠️ Technologies Used

- Java
- Spring Boot
- Spring MVC
- Thymeleaf
- HTML5
- CSS3
- Maven
- H2 Database

## 📂 Project Structure

```text
foodorder
│
├── pom.xml
├── .gitignore
├── README.md
│
└── src
    └── main
        ├── java
        │   └── com.example.foodorder
        │       ├── FoodorderApplication.java
        │       ├── controller
        │       └── model
        │
        └── resources
            ├── static
            │   ├── css
            │   └── images
            │
            └── templates
                ├── index.html
                ├── cart.html
                ├── success.html
                ├── order-success.html
                └── admin.html