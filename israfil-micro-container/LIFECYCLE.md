# Israfil Micro-Container : Component Lifecycle

> Christian Edward Gruber
>
> May 2, 2007

Since one bad pattern in java is to engage in all sorts of side behaviours
during construction, components can separate construction from initialization
by implementing the Startable interface. Startable components are started
before they are provided to any caller.

## The Startable interface

The Startable interface is quite simple:

```java
package net.israfil.micro.container;

public interface Startable {

    public void start();

    public boolean isRunning();

}
```

Implementors should start whatever they need to in order to be ready to
accept calls, including marshalling any resources necessary, etc.

Care should be taken to indicate the likely startup time, and time-out
appropriately, as the container offers its thread to the component during
this initialization, and faulty initialization can lock the container.
Alternately there are register() methods that provide a timeout parameter
(in milliseconds) which will cause the container to launch a thread to
start the component, then monitor the thread until either the timeout is
reached, or the component is started.

```java
container.registerType(ComponentOne .class, ComponentOneImpl .class, 10);
```
