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

import static com.google.common.truth.Truth.assertThat;

import net.israfil.micro.container.adapters.AbstractAutoWiringAdapter;
import org.junit.Test;


public class MultiThreadedAutoWiringContainerTest {
	
	@Test
	public void testMissingDependenciesWiringEarly() {
		AutoWiringAdaptableContainer container = new DefaultContainer(true);
		container.registerType(A.class,A1.class);
		container.registerType(B.class,B.adapter);
		container.start();
		assertThat(container.getComponent(B.class)).isNotNull();
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
	
	public static class E {
		protected E() {}
	}
	
	public static class TestableAutoWiringAdaptableContainer extends DefaultContainer {
		public boolean isStored(Object key) {
			return super.isStored(key);
		}
	}
}
