#!/usr/bin/env bash
PROFILE=$1
if [[ -z "$PROFILE" ]]; then
        PROFILE=$SPRING_PROFILES_ACTIVE
fi

JAVA_OPTS="-Xms512m -Xmx512m"
java $JAVA_OPTS -jar -Dspring.profiles.active=$PROFILE /opt/pdi/fil-service.jar

