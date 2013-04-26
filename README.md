Hesperid Asset Monitoring
=========================

An open source, lightweight and easy to use monitoring system.

## Setup

- Clone the repository
- run `mvn clean install` (you need to have Maven2 installed)
-- in the sub folders *base*, *agentbundle* and *microclient*

## Running the project in debug mode
- Change into the subfolder *server*
- run `MAVEN_OPTS="-Xms256m -Xmx512m -XX:PermSize=128m" mvn jetty:run-exploded`

That's it! Open your browser and you should see the application when opening the URL:

http://localhost:8080/hesperid
