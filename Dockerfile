FROM oraclelinux:9
LABEL authors="philo" mail="philo.sagas@gmail.com"
LABEL version="1.0" description="On Code Challenge"

ENV REFRESHED_AT=2024-10-27
RUN yum -y upgrade
RUN yum -y install java-17-openjdk java-17-openjdk-devel

WORKDIR /opt
ADD ping/target/ping-0.0.1-SNAPSHOT-bin.tar.gz /opt/
ADD pong/target/pong-0.0.1-SNAPSHOT-bin.tar.gz /opt/

CMD ["/bin/bash", "-c", "/opt/pong-service/start.sh ; /opt/ping-service/start.sh 1 ; /opt/ping-service/start.sh 2 ; /opt/ping-service/start.sh 3 ; /bin/bash"]