PROFILE=$1
if [[ -z "$PROFILE" ]]; then
        PROFILE=$SPRING_PROFILES_ACTIVE
fi

JAVA_OPTS="-Xmx128m"
java $JAVA_OPTS -jar -Dspring.profiles.active=$PROFILE /opt/pdi/pys-backoffice.jar

