#!/usr/bin/env bash

mkdir microservices
cd microservices

spring init \
--boot-version=2.3.0.RELEASE \
--build=gradle \
--java-version=1.8 \
--packaging=jar \
--name=program-service \
--package-name=microservices.core.program \
--groupId=microservices.core.program \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
program-service

spring init \
--boot-version=2.3.0.RELEASE \
--build=gradle \
--java-version=1.8 \
--packaging=jar \
--name=employee-service \
--package-name=microservices.core.employee \
--groupId=microservices.core.employee \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
employee-service

spring init \
--boot-version=2.3.0.RELEASE \
--build=gradle \
--java-version=1.8 \
--packaging=jar \
--name=client-service \
--package-name=microservices.core.client \
--groupId=microservices.core.client \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
client-service

spring init \
--boot-version=2.3.0.RELEASE \
--build=gradle \
--java-version=1.8 \
--packaging=jar \
--name=gym-service \
--package-name=microservices.core.gym \
--groupId=microservices.core.gym \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
gym-service

spring init \
--boot-version=2.3.0.RELEASE \
--build=gradle \
--java-version=1.8 \
--packaging=jar \
--name=gym-composite-service \
--package-name=microservices.composite.gym \
--groupId=microservices.composite.gym \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
gym-composite-service

cd ..