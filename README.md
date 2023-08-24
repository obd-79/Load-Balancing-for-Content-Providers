# Load Balancing for Content Providers

A network application project that implements load balancing for content providers. The project simulates a load balancer that efficiently distributes user requests among multiple servers.

## Objective

1. **Team Work and Remote Pair Programming**: Experience collaborative development and pair programming techniques.
2. **Self-Directed Learning of Socket Programming**: Enhance self-directed learning skills by implementing a network application using socket programming.

## Project Description

The project aims to solve the problem of server overload by creating a load balancer that acts as an intermediate machine between clients and multiple servers. It efficiently distributes the load (user requests) among different servers using both static and dynamic load balancing algorithms.


### Requirements

The servers must support the following requests:
- Directory listing
- File transfer
- Computation (simulated)
- Video streaming

The load balancer handles the addition or removal of servers and the communication between servers and clients.

## Implementation

The project consists of three main components:
- **Client**: Connects to the load balancer with requests.
- **Server**: Registers with the load balancer and handles client requests.
- **Load Balancer**: Estimates service time, selects servers, and manages server communication.

### Communication Flow

1. Server registers with load balancer (static or dynamic method).
2. Client connects to load balancer.
3. Load balancer estimates time and selects a server.
4. Server and client set up a direct connection.
5. Server termination is handled by the load balancer.

## Usage

1. Compile and run the `client.java`, `server.java`, and `loadBalancer.java` files.
2. Follow the instructions in the command line for client and server interactions.

## Dependencies

- Java
- TCP/UDP (as per your implementation)

## README Content

- **Load Balancing Strategies**: Implemented one static and one dynamic strategy.
- **High-Level Approach**: Protocols used (TCP/UDP), application layer mechanisms, design properties/features.
- **Challenges**: Challenges faced during development.
- **Testing**: Overview of testing methods.
- **How to Run Project**: Instructions for running the project.

## Author

Obada Kalo
