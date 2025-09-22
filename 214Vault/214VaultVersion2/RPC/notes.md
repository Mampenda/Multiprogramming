# Remote Procedure Calls (RPC)

- **Goal:** Make communication between processes on different machines look like a normal procedure call.
- **Main ideas:**
    - **Transparency:** The caller does not need to know whether the procedure is local or remote.
    - **Stubs:** Automatically generated code (client stub & server stub) handle marshalling/unmarshalling of
      parameters.
    - **Communication:** The underlying system transmits requests and responses over the network.
    - **Semantics:** Typically "at-most-once" or "at-least-once" execution, depending on how failures are handled.