euroExchange
============

Technologies
- Play 1.2.5
- Had some classpath issues installing cassandra 1.1.12, installed 1.2 and all seems fine (this means the netflix version is slightly higher)
- compiles using Java 1.6
- No need to dump Cassandra.ddl, the application will create the column families on startup, the only thing needed (however)
  is the keyspace needs to be created
- Front end technologies d3 and jquery were used 
- Styling twiter bootstrap

Project Overview

I believe all the user stories have been met. The application lazy loads in an asynchronous manner. Data is displayed to the user on a graph.
The lazy loading part is probably the worst part in terms of efficiency for this project. I would prefer 1 thread only to fetch this data and store to database,
alrough this means the api would be polled.

There are some missing tests e.g. testing some null cases and exception handling but the business logic is unit tested. I have also set up a few integration tests.
These tests are mainly missing due to time constraints. I would have liked to have some time to introduce a BDD framework like cucumber. Also maybe some jasmine to 
test the frontend. Selenium webdriver could have also been used with cucumber to test button interactions.

I decided not to use WS in play but use jersey client to map the XML to java onjects. Looking at it again, this is synchronous and WS can be asynchrous which would be
very useful. Maybe jersey could have been integrated with an Akka actor to perform asynchrously.

Investigated Kundera for an ORM to connect to cassandra, on documentation i did not see this framework supported for play < 2.
I don't like writing raw commands to the DB as there might be some risks with cql injection, this is how github and playstation got 
hacked!!

To Run
1. Create Cassandra keyspace
  ->cqlsh
  ->CREATE KEYSPACE euro_exchange_rate WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };
  
2. Download dependencies
  -> play dependencies
  
3. Run the tests
  -> play auto-test
  
4. Run the application
  -> play run

