#/bin/sh
#CLASSPATH=
export set CLASSPATH=$PWD/mysql-connector-java-5.1.14-bin.jar:$CLASSPATH
export set CLASSPATH=$PWD/servlet.jar:$CLASSPATH
export set CLASSPATH=$PWD/Primatives.class:$CLASSPATH
#echo $CLASSPATH
#javac -Xlint:unchecked $1
javac $1
#java mainprogram
