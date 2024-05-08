# RaoGasStation Program

## Overview

`RaoGasStation` is a Java-based simulation program that models a gas station. This program supports multiple types of fuel and handles related exceptions, such as insufficient fuel or overly expensive fuel prices. It utilizes concurrent programming techniques, ensuring thread safety in a multi-threaded environment by employing a thread pool.

## Features

- **Multiple Fuel Type Support**: Supports various types of fuel, including regular, diesel, and super.
- **Exception Handling**: Capable of handling and throwing `NotEnoughGasException` and `GasTooExpensiveException`.
- **Thread Safety**: Ensures data consistency and safety during thread pool and concurrent access by using `ConcurrentHashMap` and `CopyOnWriteArrayList`.
- **Revenue and Sales Statistics**: Tracks total revenue, number of sales, and transaction cancellations due to insufficient fuel or excessive prices.
- **Thread Pool Usage**: Utilizes a thread pool for managing concurrent tasks efficiently, reducing overhead and enhancing performance.

## Prerequisites

- Java JDK 11 or higher
- Maven (for building and running tests)

## Building and Running

### Compiling the Program

Compile the program using the following command:

``bash
javac RaoGasStation.java
``

### Running Tests

The project uses JUnit 5 for unit testing. Execute the tests with the following Maven command:

``bash
mvn test
``

Ensure your `pom.xml` file includes the dependency for JUnit 5:

``xml
<dependencies>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-engine</artifactId>
        <version>5.10.2</version>
        <scope>test</scope>
    </dependency>
</dependencies>
``

## Testing

The project includes multiple test cases to verify the functionality of the `RaoGasStation`. These tests cover normal purchase scenarios, exception handling, and concurrent access in a multi-threaded environment using a thread pool.

### Test Cases

- **testAddGasPump**: Verifies that gas pumps are added correctly.
- **testBuyGasNormal**: Scenario for a normal fuel purchase.
- **testBuyGasNotEnoughGasException**: Simulates an exception when there is not enough fuel.
- **testBuyGasTooExpensiveException**: Simulates an exception when the fuel is too expensive.
- **testSetAndGetPrice**: Simulates for set and get price by gas type.
- **testConcurrentBuyGas**: Tests that concurrent purchases are handled correctly in a multi-threaded environment with a thread pool.
