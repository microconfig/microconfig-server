curl -sL https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-20.2.0/graalvm-ce-java11-linux-amd64-20.2.0.tar.gz -o graalvm.tar.gz
mkdir .graalvm
tar -xf graalvm.tar.gz -C .graalvm
rm graalvm.tar.gz
mv .graalvm/graalvm-ce-java11-20.2.0/* .graalvm/
rm -rf .graalvm/graalvm-ce-java11-20.2.0
chmod +x .graalvm/bin/gu
.graalvm/bin/gu install -n native-image
.graalvm/bin/native-image --version

#Install musl
mkdir musl
curl -sL https://musl.libc.org/releases/musl-1.2.1.tar.gz | tar xz
cd musl-1.2.1
./configure --disable-shared --prefix="../musl"
make && make install
cd ..
export PATH="$PWD/musl/bin:$PATH"
musl-gcc -v

# Install zlib
curl -sL https://zlib.net/zlib-1.2.11.tar.gz | tar xz
cd zlib-1.2.11
export CC=musl-gcc
./configure --static --prefix="../musl"
make && make install
cd ..
