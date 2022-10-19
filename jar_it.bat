@echo off
javac -encoding utf8 code/*.java -d bin\
cd bin
jar cfe EMU.jar EMU *.class
move EMU.jar ..
cd ..