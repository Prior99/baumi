default: all

all: build-android build-desktop

run: run-desktop

ktlint:
	curl -L https://github.com/shyiko/ktlint/releases/download/0.10.0/ktlint > ktlint
	chmod +x ktlint

@PHONY: lint
lint: ktlint
	./ktlint core/src/**/*.kt

@PHONY: run-android
run-android:
	./gradlew android:installDebug android:run

@PHONY: run-desktop
run-desktop:
	./gradlew desktop:run

@PHONY: build-android
build-android:
	./gradlew android:assembleRelease

@PHONY: build-desktop
build-desktop:
	./gradlew desktop:dist

@PHONY: clean
clean:
	./gradlew clean

@PHONY: test
test:
	./gradlew core:test
