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

public class CausedRuntimeException extends RuntimeException {
	
	private final Throwable cause;
	
	public CausedRuntimeException() {
		super();
		this.cause = null;
	}
	
	public CausedRuntimeException(Throwable t) {
		super();
		this.cause = t;
	}
	
	public CausedRuntimeException(String message) {
		super(message);
		this.cause = null;
	}
	public CausedRuntimeException(String message, Throwable t) {
		super(message);
		this.cause = t;
	}

	public String getMessage() {
		return super.getMessage();
	}
	
	public Throwable getCause() {
		return cause;
	}

	public void printStackTrace() { printStackTrace(System.err); }

	public void printStackTrace(java.io.PrintStream s) {
		synchronized (s) {
			s.println(this);
			StackTraceElement[] elements = getStackTrace();
			for (int i = 0; i < elements.length; i++)
				s.println("\tat " + elements[i]);
			tryToPrintUnderlyingCause(this, s);
		}
	}
	
	/**
	 * Wraps the attempt to recurse if there's a cause using a try/catch,
	 * to avoid use of instanceof, since Throwable does not contain a cause
	 * or getCause() API in the CLDC.
	 */
	protected static void tryToPrintUnderlyingCause(Throwable t, java.io.PrintStream ps) {
		try {  
			CausedRuntimeException cre = (CausedRuntimeException)t;
			Throwable deeperCause = cre.getCause();
			if (deeperCause != null)
				printCauseStackTrace(cre,deeperCause,ps);
		} catch (ClassCastException e) { /* ignore */ }		
	}
    
	protected static void printCauseStackTrace(CausedRuntimeException original, Throwable cause, PrintStream ps) {
		StackTraceElement[] trace = original.getStackTrace();
		StackTraceElement[] causedTrace = cause.getStackTrace();
		int causeTraceLength = causedTrace.length - 1; 
		int traceLength = trace.length - 1;
		while (traceLength >= 0 && 
			   causeTraceLength >= 0 &&
			   trace[traceLength].equals(causedTrace[causeTraceLength])) 
		{
			traceLength--;
			causeTraceLength--;
		}
		int commonFrames = trace.length - traceLength- 1;

		ps.println("Caused by: " + cause);
		for (int i = 0; i <= traceLength; i++)  ps.println("\tat " + trace[i]);
		if (commonFrames != 0) ps.println("\t... " + commonFrames + " more");

		tryToPrintUnderlyingCause(cause,ps);
	}

	
}
