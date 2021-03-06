VERSION=21.0.0

curl -sL https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-$VERSION/graalvm-ce-java11-windows-amd64-$VERSION.zip -o graalvm.zip
unzip -q graalvm.zip -d graalvm
rm graalvm.zip
mv graalvm/graalvm-ce-java11-*/* graalvm/
rm -rf graalvm/graalvm-ce-java11-*
graalvm/bin/gu.cmd install -n native-image
graalvm/bin/native-image.cmd --version