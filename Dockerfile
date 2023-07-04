FROM griefed/baseimage-ubuntu-jdk-8:2.0.22 AS builder

ARG BRANCH_OR_TAG=main
ARG HOSTER=git.griefed.de
ARG VERSION=dev

RUN \
  apt-get update && apt-get upgrade -y && \
  apt-get install -y \
    libatomic1 && \
  git clone \
    -b $BRANCH_OR_TAG \
      https://$HOSTER/Griefed/Monitoring.git \
        /tmp/monitoring && \
  chmod +x /tmp/monitoring/gradlew* && \
  cd /tmp/monitoring && \
  ./gradlew about installQuasar cleanFrontend assembleFrontend copyDist build -Pversion=$VERSION --info -x test && \
  ls -ahl ./build/libs/ && \
  mv \
    ./build/libs/monitoring-$VERSION.jar \
    ./build/libs/monitoring.jar

FROM griefed/baseimage-ubuntu-jdk-8:2.0.22

LABEL maintainer="Griefed <griefed@griefed.de>"
LABEL description="Simple monitoring app."

ENV LOG4J_FORMAT_MSG_NO_LOOKUPS=true

RUN \
  echo "**** Bring system up to date ****" && \
  apt-get update && apt-get upgrade -y && \
  apt-get install -y \
    iputils-ping && \
  echo "**** Creating our folder(s) ****" && \
  mkdir -p \
    /app/monitoring/data && \
  echo "**** Cleanup ****" && \
    rm -rf \
      /root/.cache \
      /tmp/*

COPY --from=builder tmp/monitoring/build/libs/monitoring.jar /app/monitoring/monitoring.jar

COPY root/ /

VOLUME /config

EXPOSE 8080
