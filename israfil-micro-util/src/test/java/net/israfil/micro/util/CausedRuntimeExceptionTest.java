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
package net.israfil.micro.util;

import java.io.PrintStream;

import org.testng.Assert;


public class CausedRuntimeExceptionTest {
	
	private static final String TEST_MESSAGE1 = "Test Message1";
	private static final String TEST_MESSAGE2 = "Test Message2";
	private static final String TEST_MESSAGE3 = "Test Message3";
	
	/**
	 * @testng.test
	 * @testng.expected-exceptions value = "net.israfil.micro.util.CausedRuntimeException"
	 */
	public void testSimpleErrorThrow() {
		CausedRuntimeException error = new CausedRuntimeException();
		throw error;
	}

	/**
	 * @testng.test
	 * @testng.expected-exceptions value = "net.israfil.micro.util.CausedRuntimeException"
	 */
	public void testSimpleErrorThrowWithString() {
		CausedRuntimeException error = new CausedRuntimeException(TEST_MESSAGE1);
		throw error;
	}

	/**
	 * @testng.test
	 * @testng.expected-exceptions value = "net.israfil.micro.util.CausedRuntimeException"
	 */
	public void testSimpleErrorThrowWithThrowable() {
		CausedRuntimeException error = new CausedRuntimeException(
			new RuntimeException(TEST_MESSAGE2)
		);
		throw error;
	}

	/**
	 * @testng.test
	 * @testng.expected-exceptions value = "net.israfil.micro.util.CausedRuntimeException"
	 */
	public void testSimpleErrorThrowWithStringAndThrowable() {
		CausedRuntimeException error = new CausedRuntimeException(
			TEST_MESSAGE1, 
			new RuntimeException(TEST_MESSAGE2)
		);
		throw error;
	}	

	/** @testng.test  */
	public void testErrorPrintStream() {
		Throwable t = new RuntimeException(TEST_MESSAGE2);
		CausedRuntimeException error = new CausedRuntimeException(TEST_MESSAGE1, t);
		StringBufferOutputStream out = new StringBufferOutputStream();
		error.printStackTrace(new PrintStream(out));
		Assert.assertTrue(out.getBuffer().toString().startsWith(error.getClass().getName() + ": " + TEST_MESSAGE1));
		Assert.assertTrue(out.getBuffer().toString().contains("Caused by: " + t.getClass().getName() + ": " + TEST_MESSAGE2));
	}	
	
	/** @testng.test  */
	public void testDeepErrorPrintStream() {
		Throwable root = new CausedRuntimeException(TEST_MESSAGE1);
		CausedRuntimeException intermediate = new CausedRuntimeException(TEST_MESSAGE2, root);
		CausedRuntimeException error = new CausedRuntimeException(TEST_MESSAGE3, intermediate);
		StringBufferOutputStream out = new StringBufferOutputStream();
		error.printStackTrace(new PrintStream(out));
		Assert.assertTrue(out.getBuffer().toString().startsWith(error.getClass().getName() + ": " + TEST_MESSAGE3));
		Assert.assertTrue(out.getBuffer().toString().contains("Caused by: " + intermediate.getClass().getName() + ": " + TEST_MESSAGE2));
		Assert.assertTrue(out.getBuffer().toString().contains("Caused by: " + root.getClass().getName() + ": " + TEST_MESSAGE1));
		/* index a greater than index b, so root is deeper than intermediate*/
		int indexa = out.getBuffer().toString().indexOf("Caused by: " + root.getClass().getName() + ": " + TEST_MESSAGE1);
		int indexb = out.getBuffer().toString().indexOf("Caused by: " + intermediate.getClass().getName() + ": " + TEST_MESSAGE2);		
		Assert.assertTrue(indexa > indexb);
	}	
}
