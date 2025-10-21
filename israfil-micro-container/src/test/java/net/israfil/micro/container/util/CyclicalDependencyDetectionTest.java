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
package net.israfil.micro.container.util;

import java.util.Enumeration;
import java.util.Hashtable;

import net.israfil.micro.container.AutoWiringAdapter;
import net.israfil.micro.container.adapters.AbstractAutoWiringAdapter;
import net.israfil.micro.container.adapters.IndependentAutoWiringAdapter;
import net.israfil.micro.container.error.CyclicalDependencyError;
import org.junit.Test;


public class CyclicalDependencyDetectionTest {
	
	@Test
	public void testCircularDependencyCheckWithNonCyclicGraph() {
		// setup non-circular graph.
		Hashtable registry = new Hashtable();
		registry.put("a", new FakeAutoWireAdapter(new Object[]{"b","e","d"}));
		registry.put("b", new FakeAutoWireAdapter(new Object[]{"f","c"}));
		registry.put("c", new IndependentAutoWiringAdapter(Object.class));
		registry.put("d", new FakeAutoWireAdapter(new Object[]{"e"}));
		registry.put("e", new FakeAutoWireAdapter(new Object[]{"c"}));
		registry.put("f", new IndependentAutoWiringAdapter(Object.class));
		Enumeration i = registry.keys();
		NonDuplicateStack nds = new NonDuplicateStack();
		while (i.hasMoreElements()) {
			Object key = i.nextElement();
			nds.push(key);
			CyclicalReferenceDetectionUtil.detectCircularDependencies(registry, nds, (AutoWiringAdapter)registry.get(key));
			nds.pop();
		}
	}
	
	@Test
	public void testToSatisfyCoberturasSillyRule() {
		CyclicalReferenceDetectionUtil util = new CyclicalReferenceDetectionUtil(){};
	}
	
	@Test(expected = CyclicalDependencyError.class)
	public void testCircularDependencyCheckWithCyclicalGraph() {
		// setup circular graph.
		Hashtable registry = new Hashtable();
		registry.put("a", new FakeAutoWireAdapter(new Object[]{"b","e","d"}));
		registry.put("b", new FakeAutoWireAdapter(new Object[]{"f","c"}));
		registry.put("c", new FakeAutoWireAdapter(new Object[]{"d"}));
		registry.put("d", new FakeAutoWireAdapter(new Object[]{"e"}));
		registry.put("e", new FakeAutoWireAdapter(new Object[]{"c"}));
		registry.put("f", new IndependentAutoWiringAdapter(Object.class));
		Enumeration i = registry.keys();
		NonDuplicateStack nds = new NonDuplicateStack();
		while (i.hasMoreElements()) {
			Object key = i.nextElement();
			nds.push(key);
			CyclicalReferenceDetectionUtil.detectCircularDependencies(registry, nds, (AutoWiringAdapter)registry.get(key));
			nds.pop();
		}
	}
	
	
	public static class FakeAutoWireAdapter extends AbstractAutoWiringAdapter {
		public FakeAutoWireAdapter(Object[] dependencies) {
			super(Object.class, dependencies);
		}
		public Object create(Object[] parameters) throws IllegalAccessException, InstantiationException {
			return null;
		}
	}
}
