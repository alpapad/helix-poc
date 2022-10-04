#!/bin/bash

exec ${JAVA_HOME}/bin/java ${JVM_ARGS} ${JVM_MEM} ${JVM_DEBUG} \
     -Djava.security.egd=file:/dev/./urandom \
     -XX:+ExitOnOutOfMemoryError \
     -XX:+CrashOnOutOfMemoryError \
     -Djava.io.tmpdir=/tmp \
     -Dspring.profiles.active=${APP_PROFILES} \
     -Dlogging.config=${LOGBACK_CONFIG} \
     -jar /app/app.jar