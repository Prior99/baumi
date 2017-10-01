default: all

all: android desktop

run: run-desktop

@PHONY: run-android
run-android:
	./gradlew android:installDebug android:run:

@PHONY: run-desktop
run-desktop:
	./gradlew desktop:run

@PHONY: android
android:
	./gradlew android:assembleRelease

@PHONY: desktop
desktop:
	./gradlew desktop:dist
