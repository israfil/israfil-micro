# Israfil Micro CLDC 1.1 Utility Library

> Christian Edward Gruber
>
> June 20, 2008

## Overview

The micro utils are a set of classes that help address some of the limitations
of the CLDC 1.1 APIs. This includes, for example, Throwable not having an
underlying cause, and other missing conveniences.

The utilities library will be kept quite compressed, in order to be useable
in constrained environments intended for CLDC devices, and will provide only
the most commonly used classes. This project may, in future, be split into
smaller pieces should it acquire too many classes. In that sense, it should
be regarded as a placeholder project.

## Changes and version numbers

This project, as with other Israfil Micro projects will iterate on a major/minor
version cycle. Patches will result in a new minor release. Minor changes are
likely to be backward compatible, but are not guaranteed to be. Major changes
will likely not be backward compatible. Therefore, dependent code should
target a particular version of this project, and not adopt a version range
strategy, to avoid incompatible changes.
