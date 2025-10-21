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


import net.israfil.micro.container.adapters.AbstractAutoWiringAdapter;
import net.israfil.micro.container.adapters.IndependentAutoWiringAdapter;

import org.testng.Assert;

public class DefaultContainerTest {
	
	
	public DefaultContainer createContainer() { return new DefaultContainer(); }
	public DefaultContainer createContainer(boolean tf) { return new DefaultContainer(tf); }
	public DefaultContainer createContainer(Container c) { return new DefaultContainer(c); }
	public DefaultContainer createContainer(Container c, boolean tf) { return new DefaultContainer(c,tf); }
	public TestableContainer createTestableContainer() { return new TestableDefaultContainer(); }

	/** @testng.test */
	public void testAutoWiringContainer() {
		TestableContainer container = createTestableContainer();
		container.registerType(A.class, A1.class);
		container.registerType(B.class,B.adapter);
		container.registerType(D.class,D.adapter); // out of order
		container.registerType(C.class, new AbstractAutoWiringAdapter(
				C.class,
				new Object[] {A.class}
			) {
			public Object create(Object[] param) throws IllegalAccessException, InstantiationException {
				return new C((A)param[0]);
			}
		});
		container.start();
		Assert.assertFalse(container.isStored(A.class));
		Assert.assertNotNull(container.getComponent(A.class));
		Assert.assertTrue(container.isStored(A.class));
		Assert.assertFalse(container.isStored(B.class));
		Assert.assertNotNull(container.getComponent(B.class));
		Assert.assertTrue(container.isStored(B.class));
		Assert.assertFalse(container.isStored(C.class));
		Assert.assertFalse(container.isStored(D.class));
		Assert.assertNotNull(container.getComponent(D.class));
		Assert.assertTrue(container.isStored(C.class));
		Assert.assertTrue(container.isStored(D.class));
	}
	
	/** @testng.test 
	 	@testng.expected-exceptions
    	value = "net.israfil.micro.container.error.UnsatisfiedDependencyError" */
	public void testMissingDependenciesFailingLate() {
		AutoWiringAdaptableContainer container = createContainer();
		container.registerType(B.class,B.adapter);
		container.start();
		Assert.assertTrue(container.isRunning());
		container.getComponent(B.class);
	}

	/** @testng.test  */
	public void testDelayedFailureWithMissingDependencies() {
		AutoWiringAdaptableContainer container = createContainer();
		container.registerType(B.class,B.adapter);
		container.start();
	}

	/** @testng.test 
	 	@testng.expected-exceptions
		value = "net.israfil.micro.container.error.UnsatisfiedDependencyError" */
	public void testMissingDependenciesFailingEarly() {
		AutoWiringAdaptableContainer container = createContainer(true);
		container.registerType(B.class,B.adapter);
		container.start();
	}
	
	/** @testng.test */
	public void testMultipleStartups() {
		AutoWiringAdaptableContainer container = createContainer(true);
		container.registerType(A.class,A1.class);
		container.registerType(B.class,B.adapter);
		container.start();
		Assert.assertTrue(container.isRunning());
		container.start(); // no error.
	}

	/** @testng.test 
	 	@testng.expected-exceptions
		value = "java.lang.RuntimeException" */
	public void testStartupWithUnstartedParent() {
		AutoWiringAdaptableContainer parent = createContainer();
		AutoWiringAdaptableContainer child = createContainer(parent);
		parent.registerType(A.class,A1.class);
		child.registerType(B.class,B.adapter);
		child.start(); // failed to start parent.
	}

	/** @testng.test 
	 	@testng.expected-exceptions
		value = "net.israfil.micro.container.error.ComponentAlreadyRegisteredError" */
	public void testDuplicateRegistration() {
		AutoWiringAdaptableContainer container = createContainer();
		container.registerType(A.class,A1.class);
		container.registerType(A.class,new AutoWiringAdapter() {
			public Object create(Object[] parameters) throws IllegalAccessException, InstantiationException {
				return null;
			}
			public Object[] dependencies() { return null; }
			public Class getType() { return null; }
		});
	}
	
	/** @testng.test 	 	
	    @testng.expected-exceptions
		value = "java.lang.RuntimeException" */
	public void testRegistryFailureAfterStart() {
		AutoWiringAdaptableContainer container = createContainer();
		container.registerType(A.class,A1.class);
		container.registerType(B.class,B.adapter);
		container.start();
		container.registerType(D.class,D.adapter);
	}
	
	/** @testng.test 	 	
	    @testng.expected-exceptions
    	value = "net.israfil.micro.container.error.CouldNotCreateComponentError" */
	public void testIllegalAccessDuringConstruction() {
		AutoWiringAdaptableContainer container = createContainer(true);
		container.registerType(ComponentWithProtectedConstructor.class,ComponentWithProtectedConstructor.class);
		container.start();
	}

	/** @testng.test 	 	
	    @testng.expected-exceptions
		value = "net.israfil.micro.container.error.CouldNotCreateComponentError" */
	public void testInstantiationErrorDuringConstruction() {
		AutoWiringAdaptableContainer container = createContainer(true);
		container.registerType(A.class,A.class);
		container.start();
	}

	/** @testng.test 	 	
	    @testng.expected-exceptions
		value = "net.israfil.micro.container.error.CouldNotCreateComponentError" */
	public void testNullComponentCreation() {
		AutoWiringAdaptableContainer container = createContainer(true);
		container.registerType(A.class,new IndependentAutoWiringAdapter(A.class) {
			public Object create(Object[] param) throws IllegalAccessException, InstantiationException {
				return null;
			}
		});
		container.start();
	}
	/** @testng.test 	 	
	    @testng.expected-exceptions
		value = "net.israfil.micro.container.error.CouldNotCreateComponentError" */
	public void testArbitraryErrorInComponentCreation() {
		AutoWiringAdaptableContainer container = createContainer(true);
		container.registerType(A.class,new IndependentAutoWiringAdapter(A.class) {
			public Object create(Object[] param) throws IllegalAccessException, InstantiationException {
				throw new RuntimeException("Random error");
			}
		});
		container.start();
	}


	/** @testng.test */
	public void testAutowiringWithDependencyInParentContainerWithEarlyInstantiation() {
		AutoWiringAdaptableContainer parent = createContainer(true);
		AutoWiringAdaptableContainer child = createContainer(parent,true);
		parent.registerType(A.class,A1.class);
		child.registerType(B.class,B.adapter);
		parent.start();
		child.start();
		Assert.assertNotNull(child.getComponent(B.class));
	}
	/** @testng.test */
	public void testAutowiringWithDependencyInParentContainerWithLateIntantiation() {
		AutoWiringAdaptableContainer parent = createContainer();
		AutoWiringAdaptableContainer child = createContainer(parent);
		parent.registerType(A.class,A1.class);
		child.registerType(B.class,B.adapter);
		parent.start();
		child.start();
		Assert.assertNotNull(child.getComponent(B.class));
	}

	public static abstract class A {
	}
	public static class A1 extends A {
	}
	public static class B {
		private final A a;
		public B(A a) {
			if (a==null) throw new IllegalArgumentException("B cannot support null constructor arguments.");
			this.a = a;
		}
		public static final AutoWiringAdapter adapter = new AbstractAutoWiringAdapter(
				B.class,
				new Object[] {A.class}
			) {
			public Object create(Object[] param) throws IllegalAccessException, InstantiationException {
				return new B((A)param[0]);
			}
		};
	}
	public static class C {
		private final A a;
		public C(A a) {
			if (a==null) throw new IllegalArgumentException("C cannot support null constructor arguments.");
			this.a = a;
		}
	}
	public static class D {
		private final C c;
		public D(C c) {
			if (c==null) throw new IllegalArgumentException("D cannot support null constructor arguments.");
			this.c = c;
		}
		public static final AutoWiringAdapter adapter = new AbstractAutoWiringAdapter(
				D.class,
				new Object[] {C.class}
			) {
			public Object create(Object[] param) throws IllegalAccessException, InstantiationException {
				return new D((C)param[0]);
			}
		};
	}
	
	public static class ComponentWithProtectedConstructor {
		protected ComponentWithProtectedConstructor() {}
	}
	
	public static interface TestableContainer extends AutoWiringAdaptableContainer {
		public boolean isStored(Object key);
	}
	
	public static class TestableDefaultContainer extends DefaultContainer implements TestableContainer {
		public boolean isStored(Object key) {
			return super.isStored(key);
		}
	}


}
