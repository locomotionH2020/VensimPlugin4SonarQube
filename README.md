# VensimPlugin4SonarQube
SonarQube plugin for the .mdl files of the modeling language Vensim

# Build requirements

- Java 11
- Maven 3.5.4
- ANTLR4 4.7.2

# Build

- download this repo
- mvn -Dmaven.test.skip=true clean package

Compiling with -Dmaven.test.skip=true avoid automatic tests and, hence, having a Sonarqube instance and Sonar scanner installed

# Deployment requirements
- A Sonarqube 7.9 LTS instance (some tests have been done with latest versions)
- Sonar Scanner 4.0 or another application having an embeded Sonarqube scanner

# Deployment
- Build project avoiding tests (as mentioned before)
- After build a file sonar-vensim-X.jar is generated in target directory. X is referring the current version of the plugin (defined at the pom.xml file)
- $SONARQUBE_PATH is used to point the root path of the Sonrqube instance
- Stop current Sonarqube instance: Depends on the OS. If Linux: run  sh sonar.sh stop at $SONARQUBE_PATH/bin/linux-x86-64/
- Copy the previously generated jar (sonar-vensim-X.jar) to $SONARQUBE_PATH/extensions/plugins
- Check there are no different versions ofthe plugin (two different $SONARQUBE_PATH files), remove older if any
- Start Sonarqube instance: similar to stoping. If Linux: run sh sonar.sh start at $SONARQUBE_PATH/bin/linux-x86-64/
- Check the plugin is ready at Sonarqube instance: sign in Sonarqube instance and click at Rules to check if Vensim appears in the language list


