# Open Pantry

## Overview
This is a web application that allows users to reserve and post food for people within communities that they may join.
This project was created to accomplish the targets of UN Goal 11

## Features
- Reserve food listed to communities
- Post food to communities
- Join communities in certain locations
- Filter food by tags to fit your needs

## Technologies
### Front End
- React

### Back End
- Java
- Spring Boot

### Database
- H2

## Security
- JWT

## Setup Instructions

- Clone the repository
- Run the Spring Boot application
  - Navigate to ClassChampions/communityPantry
  - Run `./gradlew.bat bootrun`
- Run the React application 
- Navigate to ClassChampions/frontend
  - Run `npm install`
  - Run `npm start`
- Open http://localhost:3000 in your browser

Swagger endpoints are available at http://localhost:8080/swagger-ui/index.html#/

## Usage

- Create an account or log in to your account
- Join a community based on its location
- Browse available food items within the community
- Post food items to the community based on your excess food or preferences
- Reserve food items based on items posted within community
