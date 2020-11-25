PROFILE=$1
if [[ -z "$PROFILE" ]]; then
        PROFILE=$SPRING_PROFILES_ACTIVE
fi

JAVA_OPTS="-Xms1024m -Xmx1024m"
java $JAVA_OPTS -jar -Djava.net.preferIPv4Stack=true -Dspring.profiles.active=$PROFILE /opt/pdi/push-service.jar

