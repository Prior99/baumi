default: all

all: build-android build-desktop

run: run-desktop

@PHONY: run-android
run-android: assets
	./gradlew android:installDebug android:run

@PHONY: run-desktop
run-desktop: assets
	./gradlew desktop:run

@PHONY: build-android
build-android: assets
	./gradlew android:assembleRelease

@PHONY: build-desktop
build-desktop: assets
	./gradlew desktop:dist

@PHONY: assets
assets:
	mkdir -p assets
	aseprite -b ase/missing-texture.ase --save-as assets/missing-texture.png --data /dev/null

@PHONEY: clean
clean:
	./gradlew clean
	rm -rf assets
