# Old OSLC RIO 

Most likely use is to run some tests againsts these OSLC servers using the Lyo Test Suite. Otherwise, you want to check out Lyo OSLC libraries (aka OSLC4J) or better yet, Lyo Designer because these servers contain bare OSLC implementations without the use of Lyo libraries.

## Getting started

Run the following:

    mvn -f org.eclipse.lyo.rio.core/pom.xml clean install
    mvn -f org.eclipse.lyo.rio.template-webapp/pom.xml clean install

Now you can run the CM OSLC server (provider):

    mvn -f org.eclipse.lyo.rio.cm/pom.xml clean jetty:run-exploded

You can now proceed to http://localhost:8080/rio-cm/