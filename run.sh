#!/bin/bash

DEVICE_ID=$1
JAR_NAME=TiAndroidFunctionalTests.jar
SLEEP_TIME=$2
FUNCTIONALAREA=$3
YOUR_APP_PACKAGE_NAME=$4

YOUR_APP_ACTIVITY_NAME=$YOUR_APP_PACKAGE_NAME
YOUR_APP_ACTIVITY_NAME+=/.
YOUR_APP_ACTIVITY_NAME+=$5

ant build

CLASSNAME=com.ti.uiautomator.
CLASSNAME+=$FUNCTIONALAREA
CLASSNAME+=Test#runTests

echo $CLASSNAME

adb -s $DEVICE_ID push deviceConfig/$DEVICE_ID/bddConfig.properties /data/local/tmp

#Kill any existing tests lying around on adevice
adb -s $DEVICE_ID shell am force-stop android.accessibilityservice.IAccessibilityServiceClient

sleep 2

adb -s $DEVICE_ID shell am force-stop $YOUR_APP_PACKAGE_NAME

adb -s $DEVICE_ID shell am start -n $YOUR_APP_ACTIVITY_NAME

sleep $SLEEP_TIME

adb -s $DEVICE_ID push tests.properties /data/local/tmp

adb -s $DEVICE_ID push features /data/local/tmp

adb -s $DEVICE_ID push bin/$JAR_NAME /data/local/tmp

#Allow App to begin
sleep $SLEEP_TIME

SCREENSHOT_FOLDER_NAME=$FUNCTIONALAREA
SCREENSHOT_FOLDER_NAME+=Test

adb -s $DEVICE_ID shell rm -r /data/local/tmp/$SCREENSHOT_FOLDER_NAME

adb -s $DEVICE_ID shell uiautomator runtest $JAR_NAME -c $CLASSNAME

mkdir $SCREENSHOT_FOLDER_NAME

adb -s $DEVICE_ID pull /data/local/tmp/$SCREENSHOT_FOLDER_NAME $SCREENSHOT_FOLDER_NAME/$DEVICE_ID

adb -s $DEVICE_ID pull /data/local/tmp/junitreport.xml

zip -r screenshots.zip $SCREENSHOT_FOLDER_NAME
