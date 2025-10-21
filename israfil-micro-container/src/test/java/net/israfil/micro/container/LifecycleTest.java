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


public class LifecycleTest {

	/** @testng.test */
	public void testAbstractStartable() {
		Startable s = new AbstractStartable() {};
		Assert.assertFalse(s.isRunning());
		s.start();
		Assert.assertTrue(s.isRunning());
	}

	/** @testng.test */
	public void testStartupOfSingleComponent() {
		AutoWiringAdaptableContainer container = new DefaultContainer();
		final A1 a1 = new A1();
		container.registerType(A.class, new IndependentAutoWiringAdapter(A1.class) {
			public Object create(Object[] args) { return a1; } 
		}); 
		Assert.assertFalse(a1.isRunning());
		container.start();
		container.getComponent(A.class);
		Assert.assertTrue(a1.isRunning());
	}	
	
	/** @testng.test */
	public void testStartupOfStartedComponent() {
		AutoWiringAdaptableContainer container = new DefaultContainer();
		final A2 a2 = new A2();
		a2.start();
		container.registerType(A.class, new IndependentAutoWiringAdapter(A.class) {
			public Object create(Object[] args) { return a2; } 
		}); 
		container.start();
		container.getComponent(A.class);
		Assert.assertEquals(a2.invoked,1);
	}

	/** @testng.test */
	public void testStartupOfMultipleComponents() {
		AutoWiringAdaptableContainer container = new DefaultContainer();
		final A1 a1 = new A1();
		final C c = new C(a1);
		container.registerType(A.class, new IndependentAutoWiringAdapter(A1.class) {
			public Object create(Object[] args) { return a1; } 
		}); 
		container.registerType(B.class, B.adapter);
		container.registerType(C.class, new AbstractAutoWiringAdapter(
				C.class,
				new Object[] {A.class}
			) {
			public Object create(Object[] param) throws IllegalAccessException, InstantiationException {
				return c;
			}
		});
		Assert.assertFalse(a1.isRunning());
		Assert.assertFalse(c.isRunning());
		container.start();
		container.getComponent(A.class);
		Assert.assertFalse(c.isRunning());
		Assert.assertTrue(a1.isRunning());		
		container.getComponent(C.class);
		Assert.assertTrue(c.isRunning());
		Assert.assertTrue(a1.isRunning());
	}

	public static interface A {
	}
	
	public static class A1 extends AbstractStartable implements A {
		public void start() { super.start(); }
	}
	
	public static class A2 extends AbstractStartable implements A {
		public int invoked = 0;
		public void start() {
			super.start();
			invoked++;
		}
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
	public static class C extends AbstractStartable implements Startable{
		private final A a;
		public C(A a) {
			if (a==null) throw new IllegalArgumentException("C cannot support null constructor arguments.");
			this.a = a;
		}
		public void start() { super.start(); }
		public static final AutoWiringAdapter adapter = new AbstractAutoWiringAdapter(
				C.class,
				new Object[] {A.class}
			) {
			public Object create(Object[] param) throws IllegalAccessException, InstantiationException {
				return new C((A)param[0]);
			}
		};
	}
	
}
