FROM maven:3.9.8

RUN mkdir -p /home/.m2 \
    && mkdir -p /home/app \
    && chown -R 1000:1000 /home

WORKDIR /home/app

USER 1000

CMD [ "mvn", "-Duser.home=/home", "quarkus:dev", "-Ddebug", "-DdebugHost=0.0.0.0"]
