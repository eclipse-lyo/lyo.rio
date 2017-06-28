# Lyo OSLC 3.0 Reference Implementation

The Lyo OSLC 3.0 Reference Implementation is a simple bug tracker that supports:

-   LDP 1.0 Basic Container
-   OSLC 3.0 Dialogs
-   OSLC 3.0 Resource Preview
-   SPARQL for query
-   OSLC Change Management 3.0 vocabulary

It is hosted at <http://oslc3.mybluemix.net>, but you can build and run it locally.

Building and Running from the Command Line
------------------------------------------

If you have Git and Maven command-line tools installed, simply run these commands:

`$ git clone `[`http://git.eclipse.org/gitroot/lyo/org.eclipse.lyo.rio.git`]
`$ cd org.eclipse.lyo.oslc.v3.sample`
`$ mvn install jetty:run`

The sample is available at <http://localhost:8080/oslc3>.

## Building and Running from Eclipse

You can also build and run in Eclipse.

### Eclipse Prerequisites

See [Lyo/prereqs].

### Runtimes

The reference implementation uses a Jetty app server as the default runtime. It will also work in an Eclipse IDE using a Tomcat runtime. See [the Eclipse documentation] for instructions on creating a Tomcat server.

> '''Note: '''If you're running these examples on Windows, make sure you don't have spaces in your workspace path. The samples might not launch.

### Clone the RIO Git repository

This example assumes EGit is being used

-   Open the Git Repositories view in Eclipse and click the Clone Git Repository icon
-   Use [`git://git.eclipse.org/gitroot/lyo/org.eclipse.lyo.rio.git`] as the URI (browse it also [on line]). User/Password are not required.

<!-- -->

-   On the Branch Selection page, select the master branch
-   On the Local Destination page, specify a location or accept the default and click Finish

The repository should now show up in your Git Repositories view

### Import the project from the git repository

-   In the Git Repositories view, right click org.eclipse.lyo.rio and select Import Projects
-   Select the Import Existing Projects wizard and click next
-   Select org.eclipse.lyo.oslc.v3.sample

### Build the project

To build the project,

-   Expand the org.eclipse.lyo.oslc.v3.sample project
-   Right click pom.xml -&gt; Run As -&gt; Maven install

### Run the application

-   Expand the org.eclipse.lyo.oslc.v3.sample project
-   Right click pom.xml -&gt; Run As -&gt; Maven Build...
-   Set goal jetty:run
-   Click Run

Test that the server is running by going to this URL in the web browser: <http://localhost:8080/oslc3>

  [`http://git.eclipse.org/gitroot/lyo/org.eclipse.lyo.rio.git`]: http://git.eclipse.org/gitroot/lyo/org.eclipse.lyo.rio.git
  [Lyo/prereqs]: Lyo/prereqs "wikilink"
  [the Eclipse documentation]: http://help.eclipse.org/indigo/index.jsp?topic=%2Forg.eclipse.jst.server.ui.doc.user%2Ftopics%2Ftomcat.html
  [`git://git.eclipse.org/gitroot/lyo/org.eclipse.lyo.rio.git`]: git://git.eclipse.org/gitroot/lyo/org.eclipse.lyo.rio.git
  [on line]: http://git.eclipse.org/c/lyo/org.eclipse.lyo.rio.git/
