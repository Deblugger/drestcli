mkdir -p ~/.drestcli/collection
mkdir -p ~/.drestcli/configuration

./gradlew build
cp ./build/libs/drestclient-1.0-SNAPSHOT.jar ~/.drestcli/drestcli.jar
echo 'alias drestcli="java -jar ~/.drestcli/drestcli.jar"' >> ~/.bashrc
source ~/.bashrc