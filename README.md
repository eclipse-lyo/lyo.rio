# Lyo Reference Implementation for OSLC

[![CI](https://github.com/eclipse/lyo.rio/workflows/CI/badge.svg)](https://github.com/eclipse/lyo.rio/actions?query=workflow%3ACI)
[![](https://img.shields.io/badge/misc-discourse-lightgrey.svg)](https://forum.open-services.net/)
[![](https://img.shields.io/badge/misc-gitter-lightgrey.svg)](https://gitter.im/eclipse/lyo)

> RIO is a simple, bare-bones reference implementation of the OSLC specifications. It is written in Java as a standard Java EE web applications with minimal dependencies. It is intended to help those who are adopting OSLC by providing a functioning system that can be explored via a simple UI and REST services, or by taking a look at the source code.

## Getting started

1. Read the rest of this README to understand the goals of this repository better.
2. Run the `oslc.v3.sample` application by following its [README](org.eclipse.lyo.oslc.v3.sample/README.md)

## Introduction

This document gives you a quick overview of the original Reference Implementation for OSLC (RIO), explain how RIO is organized and how to build and run the code. There are newer RIOs that leverage OSLC4J to which some of the information below applies, but not all.

It is recommended to use the RIOs based on OSLC4J. For example the [running OSLC4J-based RIO for Chanage Management] is discussed on the [Lyo/BuildingOSLC4J] page.

RIO is a simple, bare-bones reference implementation of the OSLC specifications. It is written in Java as a standard Java EE web applications with minimal dependencies. It is intended to help those who are adopting OSLC by providing a functioning system that can be explored via a simple UI and REST services, or by taking a look at the source code.

## Goals of RIO

The goals of RIO are:

-   Provide minimal reference implementation of the OSLC specifications
-   Provide a tool for provider and consumer implementations to reference and experiment with
-   Provide a framework to prototype proposed additions to the OSLC specifications

RIO is *not* intended to be:

-   A full implementation of OSLC
-   A full featured ALM tool
-   A performance benchmark
-   A framework or SDK

## RIO architecture

RIO is a standard Java EE web applications with minimal dependencies and it organized into four modules.

### Modules and Dependencies

RIO is organized into the following components:

-   RIO Core JAR - base classes for services, RDF triple store and query syntax parser
-   RIO Core Webapp - common JSP pages and static resources used by RIO web applications
-   RIO CM Webapp WAR - the RIO Change Management web application
-   RIO AM Webapp WAR - the RIO Architecture Management web application
-   RIO RM Webapp WAR - the RIO Requirements Management web application

The major dependencies of RIO are:

-   Java Servlet API
-   Java Server Pages (JSP)
-   Open RDF / Sesame RDF parser and triple-store
-   ANTLR parser generator
-   Maven build system

We choose to use a very minimal set of dependencies for RIO because we want it to be really simple. The web parts of RIO are implemented with only the Servlet API and JSP pages. For simplicity's sake, there is no webapp framework, no Dojo and no OSGI.

- For RDF, we choose to use !OpenRDF / Sesame over Jena because it seemed easier to work with.
- For the build, we chose Maven for these reasons:
    - Allows developers to very easily \*build and run RIO with any IDE or no IDE at all\*, i.e. via command-line
    - Allows us to pull in depend

## CONTRIBUTING

See [the instructions](CONTRIBUTING.md) for details.

## LICENSE

Open Source under the [Eclipse Distribution License 1.0] and the [Eclipse Public License 1.0].

[running OSLC4J-based RIO for Chanage Management]: http://wiki.eclipse.org/Lyo/BuildingOSLC4J#Run_the_Change_Management_sample_provider
[Lyo/BuildingOSLC4J]: Lyo/BuildingOSLC4J "wikilink"
[Eclipse Distribution License 1.0]: http://www.eclipse.org/org/documents/edl-v10.php
[Eclipse Public License 1.0]: http://www.eclipse.org/legal/epl-v10.html
