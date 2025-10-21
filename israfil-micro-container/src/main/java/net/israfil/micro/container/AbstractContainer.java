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

import java.util.Hashtable;

import net.israfil.micro.container.error.ComponentAlreadyRegisteredError;

/**
 * A convenience abstract class that implements basic component storage and
 * lookup, as well as basic component instance assignment.
 * 
 * @author <a href="mailto:cgruber@israfil.net">Christian Edward Gruber </a>
 *
 */
public abstract class AbstractContainer implements Container {
	
	private final Hashtable components = new Hashtable();
	
	private final Container parent;
	
	private boolean running = false;

	public AbstractContainer() {
		parent = null;
	}
	
	public AbstractContainer(Container parent) {
		this.parent = parent;
	}
	
	protected boolean isStored(Object key) {
		return components.containsKey(key);
	}

	public boolean hasComponent(Object key) {
		return components.containsKey(key) || (parent != null && parent.hasComponent(key));
	}
	
	public boolean isRunning() {
		return this.running;
	}
	
	public void start() {
		this.running = true;
	}
	
	protected Container getParent() { return parent; }

	protected Object getStoredComponent(Object key) {
		Object result = components.get(key);
		if (result == null && parent != null) 
			result = parent.getComponent(key);
		return result;
	}
	
	protected void store(Object key, Object component) {
		if (hasComponent(key)) {
			if (getComponent(key) == component) return;
			throw new ComponentAlreadyRegisteredError("Object already registered for key: " + key);
		}
		components.put(key, component);
	}
	
}
