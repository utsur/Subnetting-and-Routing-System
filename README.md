# Subnetting and Routing System

This program is a system for routing within and between subnets.
It manages any number of subnets of any size and finds the shortest path 
between any systems based on the topology. This enables the exchange of packets.

## Features

- **Network management**: Create and manage any number of subnets of different sizes.
- **System management**: Add and remove computers and routers.
- **Connection management**: Create and remove connections between systems.
- **Path finding**: Calculate the shortest path between any systems.
  - Intra-subnet routing using Dijkstra's algorithm
  - Inter-subnet routing using BGP tables
- **Packet exchange**: Send packets between systems via the calculated path.

## Components

- **Network**: Central data structure for managing subnets, systems, and connections.
- **Subnet**: Represents a subnet in CIDR notation and contains systems.
- **Systems**: Base class for all network systems.
  - **Computer**: End device inside a subnet.
  - **Router**: Connects subnets and enables inter-subnet routing.
- **Connection**: Connection between two systems with a weight.
- **PathFinder**: Implements algorithms for path finding

## Usage

The program is controlled from the command line. The following commands are available:

- `load network`: Loads a network from a file
- `list`: Lists all subnets
- `list range`: Shows the IP range of a subnet
- `list systems`: Lists all systems in a subnet
- `add computer`: Adds a computer to a subnet
- `remove computer`: Removes a computer from a subnet
- `add connection`: Creates a connection between two systems
- `remove connection`: Removes a connection between two systems
- `send packet`: Sends a packet from one system to another
- `quit`: Exits the program

## Example file

An example network can be found in `example.txt`.
Load it with `load network example.txt`.

## Technical details

The system uses:
- Dijkstra's algorithm for intra-subnet routing
- BGP tables for inter-subnet routing
- Automatic updating of routing tables when the network topology changes
- Error handling for invalid operations

## Possible improvements and extensions

- Add tests for the various components and functionalities
- Add support for other routing protocols
- Add working GUI
