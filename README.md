# ZIoT-Node-Interface-API

This project is supposed to provide a HTTP/JS friendly way to interact with a [ZoarialIoT Node](https://github.com/Zoarial94/ZoarialIoT-Java-Node).

## Current functionality
> The connected node is currently hard-coded to localhost

> You must also create a username/password and login before using the api

> This API saves a JWT to a HTTP-only cookie for authentication

- List all known nodes
- List all known actions (Local or from another node)
- Get information about a specific action
- Run an action with arguments
- Update information about an action
