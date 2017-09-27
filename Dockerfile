FROM tomcat:alpine

MAINTAINER Jo Desmet <jo_desmet@yahoo.com>

ADD ./target/*.war /usr/local/tomcat/webapps/

CMD [ "catalina.sh" , "run" ]
