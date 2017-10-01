default: all

all: build-android build-desktop

run: run-desktop

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
