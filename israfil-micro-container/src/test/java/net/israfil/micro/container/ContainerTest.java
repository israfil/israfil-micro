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

import org.testng.Assert;

public class ContainerTest {
	
	AbstractContainer container = null;
	AbstractContainer child = null;
	

	/** @testng.before-method alwaysRun = "true" */
	public void setUp() {
		container = new AbstractContainer() {
			public void start() { }
			public Object getComponent(Object key) { return getStoredComponent(key); }
		};
		child = new AbstractContainer(container) {
			public void start() { }
			public Object getComponent(Object key) { return getStoredComponent(key); }
		};
	}

	/** @testng.after-method alwaysRun = "true" */
	public void tearDown() {
		child = null;
		container = null;
	}

	/** @testng.test */
	public void testContainerWithSingleComponent() {
		FakeComponent component = new FakeComponent1();
		container.store(FakeComponent.class, component);
		Assert.assertSame(container.getComponent(FakeComponent.class), component);
	}
	
	/** @testng.test */
	public void testContainerWithMultipleComponents() {
		FakeComponent component = new FakeComponent1();
		FakeDependentComponent component2 = new FakeDependentComponent(component);
		container.store(FakeComponent.class, component);
		container.store(FakeDependentComponent.class, component2);
		Assert.assertSame(container.getComponent(FakeComponent.class), component);
		Assert.assertSame(container.getComponent(FakeDependentComponent.class), component2);
	}
	
	/** @testng.test
	    @testng.expected-exceptions
        value = "net.israfil.micro.container.error.ComponentAlreadyRegisteredError" */
	public void testContainerWithDuplicateComponents() {
		FakeComponent component1 = new FakeComponent1();
		FakeComponent component2 = new FakeComponent2();
		container.store(FakeComponent.class,component1);
		container.store(FakeComponent.class,component2);
	}

	/** @testng.test */
	public void testContainerWithRepeatedRegistrationsOfTheSameComponent() {
		FakeComponent component1 = new FakeComponent1();
		container.store(FakeComponent.class,component1);
		container.store(FakeComponent.class,component1);
	}

	/** @testng.test */
	public void testContainerWithParent() {
		FakeComponent component1 = new FakeComponent1();
		container.store(FakeComponent.class,component1);
		Assert.assertTrue(child.hasComponent(FakeComponent.class));
		Assert.assertSame(child.getComponent(FakeComponent.class), component1);
	}

	/** @testng.test
	    @testng.expected-exceptions
	    value = "net.israfil.micro.container.error.ComponentAlreadyRegisteredError" */
	public void testDuplicateRegistrationWithFirstObjectInParent() {
		FakeComponent component1 = new FakeComponent1();
		FakeComponent component2 = new FakeComponent2();
		container.store(FakeComponent.class,component1);
		child.store(FakeComponent.class,component2);
	}

	/** @testng.test */
	public void testDuplicateComponentsInPeerContainers() {
		AbstractContainer child2 = new AbstractContainer(container) {
			public void start() { }
			public Object getComponent(Object key) { return getStoredComponent(key); }
		};
		FakeComponent component1 = new FakeComponent1();
		FakeComponent component2 = new FakeComponent2();
		child.store(FakeComponent.class,component1);
		child2.store(FakeComponent.class,component2);
		Assert.assertTrue(child.hasComponent(FakeComponent.class));
		Assert.assertSame(child.getComponent(FakeComponent.class), component1);
		Assert.assertNotSame(child2.getComponent(FakeComponent.class), component1);
		Assert.assertTrue(child2.hasComponent(FakeComponent.class));
		Assert.assertSame(child2.getComponent(FakeComponent.class), component2);
		Assert.assertNotSame(child2.getComponent(FakeComponent.class), component1);
		Assert.assertFalse(container.hasComponent(FakeComponent.class));
	}

	public abstract class FakeComponent {
		public abstract boolean isTrue();
	}
	public class FakeComponent1 extends FakeComponent {
		public boolean isTrue() { return false; }
	}
	public class FakeComponent2 extends FakeComponent {
		public boolean isTrue() { return false; }
	}
	public class FakeDependentComponent {
		private final FakeComponent fake;
		public FakeDependentComponent(FakeComponent component) {
			this.fake = component;
		}
		public boolean isTrue() { return fake.isTrue(); }
	}
}
