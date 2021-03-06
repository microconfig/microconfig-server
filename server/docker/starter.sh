set -e

if [ "$TLS_ENABLED" == "true" ]; then
  export SPRING_PROFILES_ACTIVE="tls"

  #  add root ca certificate to truster, if exists
  if [ -f "/tls/ca.crt" ]; then
      echo "Adding /tls/ca.crt to trusted Root certificates"
      keytool -importcert -noprompt -cacerts -alias microconfig-ca -storepass changeit -file /tls/ca.crt
  fi

  if [ ! -f "/tls/tls.crt" ] || [ ! -f "/tls/tls.key" ]; then
    echo "TLS_ENABLED=true but /tls/tls.crt or /tls/tls.key not found"
    exit 1
  fi

  echo "Importing tls.crt and tls.key into keystore"

  openssl pkcs12 -export \
        -in /tls/tls.crt -inkey /tls/tls.key \
        -name tls -out /tls/tls.p12 -password pass:tls-password

  keytool -importkeystore -noprompt \
        -srckeystore /tls/tls.p12 -srcstoretype PKCS12 -srcstorepass tls-password \
        -destkeystore /tls/microconfig.jks -deststorepass tls-password
fi

java -Dfile.encoding=UTF-8  \
    -XX:+UseShenandoahGC -XX:+UseCompressedOops \
    -Xmx256m \
    -jar /microconfig/server.jar \
    --spring.config.location=/microconfig/