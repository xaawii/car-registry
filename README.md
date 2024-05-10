# car-registry

This is a CRUD API RESTful service that allows users to perform CRUD actions on users, brands, and cars.

Users must be registered and logged in to obtain a valid token. Users with the role 'USER' are only allowed to perform GET requests, such as querying brands and cars by a specific ID or retrieving the entire list. Additionally, they can download brands and cars as CSV files.

Users with the role 'ADMIN' have access to all endpoints. They can delete users, perform CRUD actions on brands and cars, and upload a CSV file to populate the database with brands and cars.

# Docker Compose Configuration
Below is the docker-compose.yaml file that facilitates running this API on Docker:
```yaml
services:
  mysql:
    container_name: mysql-car-registry
    image: mysql:latest
    ports:
      - "8002:3306"
    environment:
      MYSQL_ROOT_PASSWORD: password
      MYSQL_DATABASE: CarRegistry
    volumes:
      - data-mysql:/var/lib/mysql
    restart: always
    networks:
      - spring

  car-registry:
    container_name: car-registry
    image: xaawii/car-registry:latest
    ports:
      - "8000:8000"
    environment:
      DB_USER: root
      DB_PASSWORD: password
      DB_NAME: CarRegistry
      DB_HOST: mysql-car-registry
      DB_PORT: 3306
    restart: always
    networks:
      - spring
    depends_on:
      - mysql


volumes:
  data-mysql:
    name: data-mysql

networks:
  spring:
    name: spring
    driver: bridge
```
# Populating the Database with CSV Files
Utilize these CSV files to populate the brand and car tables within the database by employing the designated endpoint:

brands.csv
```csv
name,warranty,country
Ford,7,United States
Toyota,10,Japan
Lexus,10,Japan
MG,7,United Kingdom/China
Mazda,6,Japan
Hyundai,5,South Korea
Mitsubishi,5,Japan
SsangYong,5,South Korea
Suzuki,5,Japan
Alfa Romeo,5,Italy
BYD,5,China
DS Automobiles,2,France
Fiat,2,Italy
Honda,3,Japan
Jeep,2,United States
Kia,7,South Korea
Land Rover,3,United Kingdom
Mercedes-Benz,2,Germany
Nissan,3,Japan
Opel,2,Germany
Peugeot,2,France
Renault,2,France
Skoda,2,Czech Republic
Volvo,2,Sweden
Volkswagen,2,Germany
```
cars.csv
```csv
brand,model,description,colour,fuel_type,mileage,num_doors,price,year
Ford,Mondeo,Very old,Grey,Diesel,1400,4,1400,1990
Ford,Focus,Compact,Red,Gasoline,80000,5,15000,2017
Ford,Fiesta,Small,Blue,Gasoline,50000,3,8000,2014

Toyota,M10,Very cool,Blue,Diesel,21000,5,21000,2015
Toyota,Yaris,Practical,White,Gasoline,100000,3,10000,2010
Toyota,RAV4,SUV,Silver,Hybrid,45000,5,35000,2022

Kia,Sorento,Spacious,Black,Diesel,40000,5,25000,2018
Kia,Rio,Urban,Gray,Gasoline,60000,5,12000,2019

MG,ZS,Family,Red,Gasoline,30000,5,18000,2020
MG,5,Sporty,Blue,Gasoline,80000,3,10000,2016

Mazda,3,Modern,White,Gasoline,50000,5,18000,2021
Mazda,MX-5,Convertible,Yellow,Gasoline,20000,2,25000,2015

Hyundai,Tucson,Family,Silver,Diesel,60000,5,22000,2017
Hyundai,i10,Urban,Red,Gasoline,40000,5,8000,2019

Mitsubishi,ASX,SUV,Black,Gasoline,70000,5,15000,2018
Mitsubishi,Lancer,Sporty,Blue,Gasoline,90000,4,6000,2012

SsangYong,Tivoli,SUV,Gray,Diesel,50000,5,20000,2019
SsangYong,Korando,Family,Silver,Gasoline,80000,5,12000,2015

Suzuki,Swift,Small,Red,Gasoline,45000,3,9000,2020
Suzuki,Vitara,SUV,Blue,Gasoline,65000,5,16000,2018

Alfa Romeo,Giulietta,Sporty,Black,Gasoline,30000,4,28000,2016
Alfa Romeo,Stelvio,SUV,Gray,Diesel,20000,5,40000,2021

BYD,Tang,SUV,White,Electric,15000,5,45000,2023
BYD,Han,Sporty,Blue,Electric,10000,4,50000,2022
```
# Creating a User with ADMIN Role
To accomplish this, you must manually adjust the role of the users whom you wish to designate as administrators.

# Documentation for Endpoints
The documentation for endpoints is available on Swagger. You can explore and test them at /swagger-ui/index.html.
