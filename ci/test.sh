#!/bin/bash -ex

boot_oracle() {
  cp ./ci/oracle_setup.sql /docker-entrypoint-initdb.d/
  (./ci/boot_oracle.sh &> /tmp/boot_oracle.log) &
  echo "Waiting for Oracle to finish booting"
  O_SLEEP=0
  while [ ! -f /tmp/oracle.is.ready ]; do
    O_SLEEP=$(expr $(expr ${O_SLEEP} + 20) % 61)
    sleep ${O_SLEEP}
  done
}

prepare_driver() {
  mkdir -p ./lib
  cp /u01/app/oracle-product/12.1.0/xe/jdbc/lib/ojdbc7.jar ./lib
  perl -pi -e 's#(\s+)(testCompile\("org.springframework.boot:spring-boot-starter-test"\))#$1$2\n$1testRuntime(files("lib/ojdbc7.jar"))#' build.gradle.kts
}

setup_dependencies() {
  apt-get update
  apt-get install -y openjdk-7-jre-headless
}

pushd ./trilogy
  setup_dependencies
  boot_oracle
  prepare_driver
  DB_URL=jdbc:oracle:thin:@$(hostname -i):1521:xe ./gradlew clean testAll
popd