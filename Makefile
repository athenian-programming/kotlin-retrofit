default: compile

clean:
	./gradlew clean

compile: build

build:
	./gradlew build -xtest

versioncheck:
	./gradlew dependencyUpdates