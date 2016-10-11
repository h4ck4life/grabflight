Bootstrap theme:
	https://bootswatch.com/simplex/

Bitbucket repo:
	git clone https://mohdalif@bitbucket.org/mohdalif/grabflight.git
	
Java path:
	/usr/lib/jvm/java-8-oracle/jre/bin/java

Run standalone:
	git pull origin master
	sudo mvn clean compile package -Dmaven.test.skip=true
	java -Dninja.port=9000 -jar target/grabflight-0.0.1-SNAPSHOT.jar
	
java -Dcom.sun.management.jmxremote.port=3030 -Dcom.sun.management.jmxremote.rmi.port=3030 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dninja.port=9000 -jar target/grabflight-0.0.1-SNAPSHOT.jar