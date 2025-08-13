@echo off
set package="engine.jaxb.generated"
set schema="../../resources/S-Emulator-v1.xsd"
xjc-run-win.bat -p %package% %schema%