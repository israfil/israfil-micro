# Israfil Micro-Container : Usage
## Christian Edward Gruber
## May 2, 2007

The default container is an auto-wiring container which allows for registration
of components quite simply, if they have no dependencies.  Components with 
dependencies require the use of an adapter which explicitly declares the 
dependencies, and provides a builder method which receives the dependencies and
explicitly constructs the component, avoiding any use of reflection, which is
not supported in the CLDC 1.1 profile. 

## Setting up the auto-wiring container.

Setting up the container should be done early in your entry-point code.
The default container auto-wires components on first use of the component,
unless configured to pre-initialize them.  This allows for early creation 
of the container and registration of components without incurring the 
up-front performance cost.

Registration for independent components works quite simply:

```java
AutoWiringAdaptableContainer container = new DefaultAutoWiringAdaptablecContainer();
container.registerType(ComponentOne.class,ComponentOneImpl.class);
container.registerType(ComponentTwo.class,ComponentTwoImpl.class);
```

## Registering components with dependencies

Registration of components with dependencies is a bit more complicated, as it
requires an adapter:

```java
public void registerType(Object key, AutoWiringAdapter componentAdapter);
```

AutoWiringAdapters can be created on the fly, for instance by using an anonymous
inner class that implements AutoWiringAdapter or extending AbstractAutoWiringAdapter:

```java
container.registerType(ComponentThree.class,new AbstractAutoWiringAdapter(
        ComponentThree.class, new Object[] {ComponentOne.class}
    ) {
    public Object create(Object[] params) throws IllegalAccessException, InstantiationException {
        return new ComponentThreeImpl((ComponentOne)params[0]);
    }
});
```

Another option is to create a constant adapter on the component itself

```java
public class ComponentThreeImpl implements ComponentThree {
    public static final AutoWiringAdapter adapter = new AbstractAutoWiringAdapter(
            ComponentThree.class, new Object[] {ComponentOne.class}
        ) {
        public Object create(Object[] params) throws IllegalAccessException, InstantiationException {
            return new ComponentThreeImpl((ComponentOne)params[0]);
        }
    });
    
    private final ComponentOne one;
    
    public ComponentThreeImpl(ComponentOne one) {
        this.one = one;
    }
    
    public void doStuff() { one.whatever(); }
}
```

Having created this adapter constant, you can then more easily register the 
component in the following way:

```java
container.registerType(ComponentThree.class,ComponentThreeImpl.adapter);
```

## Retrieving components

Retrieving components is quite simple, using the getComponent method.  

```java
ComponentThree three = (ComponentThree)container.getComponent(ComponentThree.class);
three.doStuff();
```
