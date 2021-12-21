if [ -z "$PSVRNM" ]; then
  echo 'please define PSVRNM system environment variable.'
  exit 2;
fi
if [ -z "$PUSRNM" ]; then
  echo 'please define PUSRNM system environment variable.'
  exit 2;
fi
if [ -z "$PDNM" ]; then
  echo 'please define PDNM system environment variable.'
  exit 2;
fi


# Create the service file

echo '[Unit]' > portland.service
echo 'Description=portland' >> portland.service
echo '' >> portland.service
echo '[Service]' >> portland.service
echo "User=${PUSRNM}" >> portland.service
echo 'Type=simple' >> portland.service
echo "ExecStart=/opt/java/jdk1.8.0_112/bin/java -jar /home/${PUSRNM}/portland.jar" >> portland.service
echo 'SuccesExitStatus=143' >> portland.service
echo '' >> portland.service
echo '[Install]' >> portland.service
echo 'WantedBy=multi-user.target' >> portland.service
echo '' >> portland.service


# Copy over the static content.


scp -r raw/* $PSVRNM:/var/www/$PDNM/html/


# Build the spring boot jar
mvn package


# Copy over the spring boot jar and the service script.
scp target/portland.jar $PSVRNM:/home/$PUSRNM/
scp portland.service $PSVRNM:/home/$PUSRNM/


