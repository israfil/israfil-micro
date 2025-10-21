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

import java.util.Stack;

import org.testng.Assert;


public class NonDuplicateStackTest {
	
	/** @testng.test */
	public void testBasicStackBehaviour() {
		String s1 = "Hi.";
		String s2 = "Hi";
		String s3 = "Bye.";
		Stack stack = new NonDuplicateStack();
		stack.push(s1);
		stack.push(s2);
		stack.push(s3);
		Assert.assertSame(stack.pop(),s3);
		Assert.assertSame(stack.pop(),s2);
		Assert.assertSame(stack.pop(),s1);
	}
	
	/** @testng.test 
	    @testng.expected-exceptions
    	value = "java.lang.IllegalArgumentException" */
	public void testDuplicateHandlingByObjectRef() {
		Object o1 = new Object();
		Object o2 = new Object();
		Object o3 = new Object();
		Stack stack = new NonDuplicateStack();
		stack.push(o1);
		stack.push(o2);
		stack.push(o3);
		stack.push(o2);
	}
		
	/** @testng.test 
	    @testng.expected-exceptions
		value = "java.lang.IllegalArgumentException" */
	public void testDuplicateHandlingByValue() {
		String s1 = "Hi.";
		String s2 = "Hi";
		String s3 = "Bye.";
		String s4 = "Hi";
		Stack stack = new NonDuplicateStack();
		stack.push(s1);
		stack.push(s2);
		stack.push(s3);
		stack.push(s4);
	}

}
