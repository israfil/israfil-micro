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
import static org.junit.Assert.fail;

import net.israfil.micro.container.adapters.AbstractAutoWiringAdapter;
import net.israfil.micro.container.adapters.IndependentAutoWiringAdapter;
import net.israfil.micro.container.error.CouldNotCreateComponentError;
import net.israfil.micro.container.error.UnsatisfiedDependencyError;
import org.junit.Test;

public abstract class AbstractContainerTest<T extends AutoWiringAdaptableContainer> {


    protected abstract T createContainer();

    protected abstract T createContainer(boolean tf);

    protected abstract T createContainer(Container c);

    protected abstract T createContainer(Container c, boolean tf);

    protected abstract TestableContainer createTestableContainer();

    @Test
    public void testAutoWiringContainer() {
        TestableContainer container = createTestableContainer();
        container.registerType(A.class, A1.class);
        container.registerType(B.class, B.adapter);
        container.registerType(D.class, D.adapter); // out of order
        container.registerType(C.class, new AbstractAutoWiringAdapter(
                C.class,
                new Object[]{A.class}
        ) {
            public Object create(Object[] param) throws IllegalAccessException, InstantiationException {
                return new C((A) param[0]);
            }
        });
        container.start();
        assertThat(container.isStored(A.class)).isFalse();
        assertThat(container.getComponent(A.class)).isNotNull();
        assertThat(container.isStored(A.class)).isTrue();
        assertThat(container.isStored(B.class)).isFalse();
        assertThat(container.getComponent(B.class)).isNotNull();
        assertThat(container.isStored(B.class)).isTrue();
        assertThat(container.isStored(C.class)).isFalse();
        assertThat(container.isStored(D.class)).isFalse();
        assertThat(container.getComponent(D.class)).isNotNull();
        assertThat(container.isStored(C.class)).isTrue();
        assertThat(container.isStored(D.class)).isTrue();
    }

    @Test(expected = UnsatisfiedDependencyError.class)
    public void testMissingDependenciesFailingLate() {
        AutoWiringAdaptableContainer container = createContainer();
        container.registerType(B.class, B.adapter);
        container.start();
        assertThat(container.isRunning()).isTrue();
        container.getComponent(B.class);
    }

    @Test
    public void testDelayedFailureWithMissingDependencies() {
        AutoWiringAdaptableContainer container = createContainer();
        container.registerType(B.class, B.adapter);
        container.start();
    }

    @Test(expected = UnsatisfiedDependencyError.class)
    public void testMissingDependenciesFailingEarly() {
        AutoWiringAdaptableContainer container = createContainer(true);
        container.registerType(B.class, B.adapter);
        container.start();
    }

    @Test
    public void testMultipleStartups() {
        AutoWiringAdaptableContainer container = createContainer(true);
        container.registerType(A.class, A1.class);
        container.registerType(B.class, B.adapter);
        container.start();
        assertThat(container.isRunning()).isTrue();
        container.start(); // no error.
    }

    @Test(expected = RuntimeException.class)
    public void testStartupWithUnstartedParent() {
        AutoWiringAdaptableContainer parent = createContainer();
        AutoWiringAdaptableContainer child = createContainer(parent);
        parent.registerType(A.class, A1.class);
        child.registerType(B.class, B.adapter);
        child.start(); // failed to start parent.
    }

    @Test(expected = net.israfil.micro.container.error.ComponentAlreadyRegisteredError.class)
    public void testDuplicateRegistration() {
        AutoWiringAdaptableContainer container = createContainer();
        container.registerType(A.class, A1.class);
        container.registerType(A.class, new AutoWiringAdapter() {
            public Object create(Object[] parameters) throws IllegalAccessException, InstantiationException {
                return null;
            }

            public Object[] dependencies() {
                return null;
            }

            public Class getType() {
                return null;
            }
        });
    }

    @Test(expected = RuntimeException.class)
    public void testRegistryFailureAfterStart() {
        AutoWiringAdaptableContainer container = createContainer();
        container.registerType(A.class, A1.class);
        container.registerType(B.class, B.adapter);
        container.start();
        container.registerType(D.class, D.adapter);
    }

    @Test(expected = CouldNotCreateComponentError.class)
    public void testIllegalAccessDuringConstruction() {
        AutoWiringAdaptableContainer container = createContainer(true);
        container.registerType(ComponentWithProtectedConstructor.class, ComponentWithProtectedConstructor.class);
        container.start();
    }

    @Test(expected = CouldNotCreateComponentError.class)
    public void testInstantiationErrorDuringConstruction() {
        AutoWiringAdaptableContainer container = createContainer(true);
        container.registerType(A.class, A.class);
        container.start();
    }

    @Test(expected = CouldNotCreateComponentError.class)
    public void testNullComponentCreation() {
        AutoWiringAdaptableContainer container = createContainer(true);
        container.registerType(A.class, new IndependentAutoWiringAdapter(A.class) {
            public Object create(Object[] param) throws IllegalAccessException, InstantiationException {
                return null;
            }
        });
        container.start();
    }

    @Test(expected = CouldNotCreateComponentError.class)
    public void testArbitraryErrorInComponentCreation() {
        AutoWiringAdaptableContainer container = createContainer(true);
        container.registerType(A.class, new IndependentAutoWiringAdapter(A.class) {
            public Object create(Object[] param) throws IllegalAccessException, InstantiationException {
                throw new RuntimeException("Random error");
            }
        });
        container.start();
    }


    @Test
    public void testAutowiringWithDependencyInParentContainerWithEarlyInstantiation() {
        AutoWiringAdaptableContainer parent = createContainer(true);
        AutoWiringAdaptableContainer child = createContainer(parent, true);
        parent.registerType(A.class, A1.class);
        child.registerType(B.class, B.adapter);
        parent.start();
        child.start();
        assertThat(child.getComponent(B.class)).isNotNull();
    }

    @Test
    public void testAutowiringWithDependencyInParentContainerWithLateIntantiation() {
        AutoWiringAdaptableContainer parent = createContainer();
        AutoWiringAdaptableContainer child = createContainer(parent);
        parent.registerType(A.class, A1.class);
        child.registerType(B.class, B.adapter);
        parent.start();
        child.start();
        assertThat(child.getComponent(B.class)).isNotNull();
    }

    public static abstract class A {
    }

    public static class A1 extends A {
    }

    public static class B {
        private final A a;

        public B(A a) {
            if (a == null) throw new IllegalArgumentException("B cannot support null constructor arguments.");
            this.a = a;
        }

        public static final AutoWiringAdapter adapter = new AbstractAutoWiringAdapter(
                B.class,
                new Object[]{A.class}
        ) {
            public Object create(Object[] param) throws IllegalAccessException, InstantiationException {
                return new B((A) param[0]);
            }
        };
    }

    public static class C {
        private final A a;

        public C(A a) {
            if (a == null) throw new IllegalArgumentException("C cannot support null constructor arguments.");
            this.a = a;
        }
    }

    public static class D {
        private final C c;

        public D(C c) {
            if (c == null) throw new IllegalArgumentException("D cannot support null constructor arguments.");
            this.c = c;
        }

        public static final AutoWiringAdapter adapter = new AbstractAutoWiringAdapter(
                D.class,
                new Object[]{C.class}
        ) {
            public Object create(Object[] param) throws IllegalAccessException, InstantiationException {
                return new D((C) param[0]);
            }
        };
    }

    public static class ComponentWithProtectedConstructor {
        protected ComponentWithProtectedConstructor() {
        }
    }

    public interface TestableContainer extends AutoWiringAdaptableContainer {
        public boolean isStored(Object key);
    }
}
