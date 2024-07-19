FROM tomcat:10.1.26-jre21

RUN rm -rf /usr/local/tomcat/webapps/*


COPY build/libs/chuck_berry_rest_webapp-1.0.war /usr/local/tomcat/webapps/

EXPOSE 8080

CMD ["catalina.sh", "run"]