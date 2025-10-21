/*
 * Copyright (c) 2008 Israfil Consulting Services Corporation
 * Copyright (c) 2008 Christian Edward Gruber
 * All Rights Reserved
 * 
 * This software is licensed under the Berkeley Standard Distribution license,
 * (BSD license), as defined below:
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this 
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of Israfil Consulting Services nor the names of its contributors 
 *    may be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
 * OF SUCH DAMAGE.
 * 
 * $Id: Copyright.java 618 2008-04-14 14:03:03Z christianedwardgruber $
 */
package net.israfil.micro.container;

import java.util.Enumeration;
import java.util.Hashtable;

import net.israfil.micro.container.adapters.IndependentAutoWiringAdapter;
import net.israfil.micro.container.error.ComponentAlreadyRegisteredError;
import net.israfil.micro.container.error.CouldNotCreateComponentError;
import net.israfil.micro.container.error.UnsatisfiedDependencyError;
import net.israfil.micro.container.util.CyclicalReferenceDetectionUtil;
import net.israfil.micro.container.util.NonDuplicateStack;


/**
 * A default implementation of AutoWiringAdaptableContainer, a Container
 * that uses an AutoWiringAdapter to ensure that components can be 
 * created appropriately with their dependencies satisfied automatically.
 * 
 * This adapter is intended for constructor injection, but relies on the 
 * adapter to perform such. No reflection is used by this class itself.
 *
 */
public class DefaultContainer extends AbstractContainer implements AutoWiringAdaptableContainer {
	
	private Hashtable registry = new Hashtable();
	
	private final boolean failEarly;
	
	private boolean starting = false;
	
	private final Object CREATION_MUTEX = new Object();
	
	/**
	 * A default constructor that will throw errors regarding circular
	 * dependencies at registration time, throw missing dependency errors
	 * at wire-up (getComponent()) time, and has no parent.
	 * 
	 * @param failEarly A boolean to indicate that this container should detect missing or circular dependencies at start() time.
	 */
	public DefaultContainer() {
		super();
		this.failEarly = false;
	}
	
	/**
	 * Construct this container such that it detects missing dependencies 
	 * upon invocation of start().  Otherwise, it will fail at wire time, 
	 * rather than at registration time.  Circular references will give errors
	 * at registration time.
	 * 
	 * @param failEarly A boolean to indicate that this container should detect missing or circular dependencies at start() time.
	 */
	public DefaultContainer(boolean failEarly) {
		super();
		this.failEarly = failEarly;
	}

	/**
	 * This method constructs a DefaultAutoWiringAdaptableContainer that
	 * has a parent container for backup resolution. The parent container 
	 * can be of any type of Container.
	 * 
	 * @param parent The parent container (optional)
	 */
	public DefaultContainer(Container parent) {
		super(parent);
		this.failEarly = false;
	}
	
	/**
	 * Construct this container such that it detects missing dependencies 
	 * upon invocation of start().  Otherwise, it will fail at wire time, 
	 * rather than at registration time.  Circular references will give errors
	 * at registration time.  This method also provides for a parent container.
	 * The parent container can be of any type of Container.
	 * 
	 * @param failEarly A boolean to indicate that this container should detect missing or circular dependencies at start() time.
	 * @param parent The parent Container (optional)
	 */
	public DefaultContainer(Container parent, boolean failEarly) {
		super(parent);
		this.failEarly = failEarly;
	}
		
	public synchronized void start()  { 
		if (isRunning() || 
			starting) return;
		if (getParent() != null && !getParent().isRunning()) throw new RuntimeException("Parent container is not started.");
		this.starting = true;
		if (failEarly) {
			Enumeration i = registry.keys();
			while (i.hasMoreElements()) {
				// force get each component, forcing all wiring.
				getComponent(i.nextElement());
			}
		}
		super.start();
		starting = false;
	}

	public void registerType(Object key, Class componentType) {
		registerType(key,componentType, 0);
	}
	
	protected void registerType(Object key, Class componentType, long timeout) {
		registerType(key,new IndependentAutoWiringAdapter(componentType), timeout);
	}
	
	public void registerType(Object key, AutoWiringAdapter componentAdapter) {
		registerType(key,componentAdapter,0);
	}
	
	/**
	 * Registers a component for later instantiation and (optionally) startup.
	 * 
	 * @param key the key by which this component will be identified in the system
	 * @param componentAdapter an AutoWiringAdapter for creating this component and listing its dependencies
	 * @param timeout an (optional) timeout applied to any startup lifecycle
	 */
	protected void registerType(Object key, AutoWiringAdapter componentAdapter, long timeout) {
		if (this.isRunning()) throw new RuntimeException("Cannot register when container is started.");
		// FIXME: Figure out whether to support parent registry checking.  Probably can't do it.
		
		if (registry.containsKey(key)) throw new ComponentAlreadyRegisteredError("Component already registered for " + key);
		detectCircularDependencies(key,componentAdapter);
		registry.put(key, componentAdapter);
	}
	
	private void detectCircularDependencies(Object key,AutoWiringAdapter componentAdapter) {
		NonDuplicateStack nds = new NonDuplicateStack();
		nds.push(key);
		CyclicalReferenceDetectionUtil.detectCircularDependencies(this.registry, nds, componentAdapter);
	}
	
	/**
	 * This method wires the object up with its dependencies, providing said
	 * dependencies to the object's adapter. It also starts the object if its
	 * startable
	 * @param originalKey
	 * @param adapter
	 * @return
	 */
	private Object wireObject(Object originalKey, AutoWiringAdapter adapter) {
		Object[] dependencies = adapter.dependencies();
		Object[] parameters = new Object[dependencies.length];
		for (int depn = 0; depn < dependencies.length; depn++ ) {
			Object key = adapter.dependencies()[depn];
			parameters[depn] = getComponent(key);
			if (parameters[depn] == null) throw new UnsatisfiedDependencyError("Could not materialize dependency for key " + key);
		}
		try {
			Object o = adapter.create(parameters);
			if (o == null) throw new InstantiationException("Could not create a non-null object.  Adapter " + adapter.getClass().getName() + " returned null.");
			else {
				try {
					Startable s = ((Startable)o);
					if (!s.isRunning()) s.start();
				} catch (ClassCastException e) {}
				return o;
			}
		} catch (IllegalAccessException e) {
			throw new CouldNotCreateComponentError("Could not create component " + originalKey + " of type " + adapter.getType(),e);
		} catch (InstantiationException e) {
			throw new CouldNotCreateComponentError("Could not create component " + originalKey + " of type " + adapter.getType(),e);			
		} catch (Throwable e) {
			throw new CouldNotCreateComponentError("Could not create component " + originalKey + " of type " + adapter.getType(),e);			
		}
	}

	public Object getComponent(Object key) {
		Object component = super.getStoredComponent(key);
		if (component == null && registry.containsKey(key)) {
			synchronized (CREATION_MUTEX) {
				if (component == null) { // in case following thread gets in just after storage.
					component = wireObject(key,(AutoWiringAdapter)registry.get(key));
					store(key, component);
				}
			}
		}
		return component;
	}
	
} 
