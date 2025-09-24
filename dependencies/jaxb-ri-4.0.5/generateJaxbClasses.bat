@echo off
set package="engine.jaxb.generated"
set schema="../../resources/S-Emulator-v2.xsd"
xjc-run-win.bat -p %package% %schema%