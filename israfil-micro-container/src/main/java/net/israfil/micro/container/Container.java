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

/**
 * A container for components that will satisfy requests if the 
 * component is available.  How these component requests are satisfied
 * are implementation dependent, as well as how inter-component 
 * dependencies are resolved.
 * 
 * @author <a href="mailto:cgruber@israfil.net">Christian Edward Gruber </a>
 * 
 */
public interface Container {

	/**
	 * Returns true if the Component is available and prepared.
	 * It throws a ContainerNotStarted runtime exception if the this method 
	 * is used before the container is started.
	 * 
	 * This method must be implemented by implementors of Container in a 
	 * thread-safe and reentrant way. 
	 */
	public boolean hasComponent(Object key);
	
	/**
	 * Returns the component named by the key if it is available, or null if no
	 * such component is available.  It throws a ContainerNotStarted runtime
	 * exception if this method is used before the container is started.  
	 * 
	 * This method must be implemented by implementors of Container in a 
	 * thread-safe and reentrant way. 
	 */
	public Object getComponent(Object key);
	
	/**
	 * Begin the lifecycle of the container, after which components should be
	 * accessible.  Implementors should stop any component registration as of
	 * the start() call.  Subsequent start() calls should be ignored.
	 */
	public void start();

	/**
	 * Returns true if the container has been started, and false if it has
	 * not been started.
	 */
	public boolean isRunning();
	
}
