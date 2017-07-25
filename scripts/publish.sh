#!/bin/bash

cd $TRAVIS_BUILD_DIR

echo "Branch: "$TRAVIS_BRANCH" pull request: "$TRAVIS_PULL_REQUEST

if [[ $TRAVIS_BRANCH == 'master' && $TRAVIS_PULL_REQUEST == 'false' ]]
then
  echo "Start sonar analysis"
  ./gradlew sonarqube -Dsonar.host.url=https://sonarqube.com -Dsonar.organization=cyganki -Dsonar.login=$SONAR_TOKEN
  # NOTHING TO DEPLOY FOR NOW FOR FRONT
  #  cd e-arbiter-web
  #  angular-cli-ghpages --no-silent --repo=https://GH_TOKEN@github.com/arturczopek/e-arbiter.git
else
  echo "No analysis"
fi