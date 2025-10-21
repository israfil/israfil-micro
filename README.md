# Tools and libraries for use in J2ME development

> Archived ancient project, for largely documentary and fooling around purposes.

A set of tools and technologies from Israfil Consulting Services and other contributors
that can be used in the development of J2ME software in a constrained environment (CLDC 1.1 profile).
Much of the technologies here are adaptations of modern best-practices or state-of-the-art
tools into a constrained platform footprint.

Java Micro Edition doesn't really matter anymore, as, for the most part, hardware is capable of running most of
the standard JRE (and modern modules in theory allow for subsets of Java APIs to be composed into smaller runtimes).
Still, this was small enough to run on set-top boxes, and was included in a major set-top box J2ME based operating
system deployed to millions of TV boxes.

Current modules include:

| Sub Project | J2ME Profile | Description                                                                                                                                                           | Version | Docs                                        |
|:------------|:-------------|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------|:--------|:--------------------------------------------|
| container   | CLDC 1.1     | an Inversion of Control Dependency-Injection container (inspired by picocontainer)                                                                                    | 1.1     | [README](israfil-micro-container/README.md) |
| collections | CLDC 1.1     | compliant Java 1.2-style collections (API subset - not actually imported)                                                                                             | None    |                                             |
| concurrent  | CLDC 1.1     | compliant backported concurrent APIs (by Doug Lea and JSR-166). This includes Lock, ReadWriteLock, ReentrantLock and ReadWriteReentrantLock, plus their dependencies. | None    |                                             |
| util        | CLDC 1.1     | useful classes used by other israfil-micro projects                                                                                                                   | 1.1     | [README](israfil-micro-util/README.md)      |

This project was originally a bet, at one of Christian Gruber's clients, that Inversion of Control /
Dependency-Injection could be done on a Java Micro (CLDC 1.1) level API, without access to reflection and other J2SE
conveniences. The project is also, in a sense, a great-grandparent to [Dagger](https://dagger.dev) as its author was
one of the primary original authors of Dagger, and this notion of minimal, no-reflection IoC/DI was part of Dagger's
original DNA.

Lots of credit goes to Tyler Goodwin, who was a great intellectual sparring partner and a great engineer, who
talked me through some of this, while building the smallest SQL-ish engine I've ever seen.

Licensed with BSD-3-clause license (https://opensource.org/license/BSD-3-Clause)
