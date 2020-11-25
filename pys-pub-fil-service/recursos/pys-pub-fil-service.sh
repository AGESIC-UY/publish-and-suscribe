PROFILE=$1
if [[ -z "$PROFILE" ]]; then
        PROFILE=$SPRING_PROFILES_ACTIVE
fi

JAVA_OPTS="-Xms1024m -Xmx1024m"
java $JAVA_OPTS -jar -Dspring.profiles.active=$PROFILE /opt/pdi/pub-fil-service.jar

