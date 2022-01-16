[home](../README.md)

# portland Honeypot Server

## Define some environment variables on your local workstation


    sudo vim /etc/profile


and add the following lines (PDB* on the honeypot server as well):



    export PSVRNM=server name, as defined in your .ssh/config file.
    export PUSRNM=server user name.
    export PDNM=domain name assigned to server.
    export PDBURL=<database URL:port (3306 if default mysql)>
    export PDBUSR=<database user>
    export PDBPWD=<database password>



edit portland.service, replace karl with your user name.



## Deploy



Set up a digitalocean server with this user data:



    #!/bin/bash
    apt-get -y update
    apt-get -y install nginx vim net-tools certbot python3-certbot-nginx openjdk-17-jre-headless default-jdk
    apt-get -y upgrade
    ufw allow OpenSSH
    ufw allow 'Nginx Full'
    ufw enable
    export HOSTNAME=$(curl -s http://169.254.169.254/metadata/v1/hostname)
    export PUBLIC_IPV4=$(curl -s http://169.254.169.254/metadata/v1/interfaces/public/0/ipv4/address)
    echo Droplet: $HOSTNAME, IP Address: $PUBLIC_IPV4 > /usr/share/nginx/html/index.html
    

Add a user,


    scp crtusr.sh $PSVRNM:/root/
    ssh $PSVRNM
    htop


Verify apt-get is done, quit out of there with a q, it might have asked you to reboot so oblige:


    sudo reboot 0


then, create your user:


    ssh $PSVRNM
    ./crtusr.sh (your user name) (your password)



You should see authorizedkeys at the end of the output.  Next, exit out, and on your system update ~/.ssh/config so that your user is not root anymore.


Next, you'll need to set up a domain name and point it to your server.  If you are reading this right now, that's almost certainly going to be trivial for you.

Next, you'll want to create the nginx server block and [secure it with letsencrypt](https://www.digitalocean.com/community/tutorials/how-to-secure-nginx-with-let-s-encrypt-on-ubuntu-20-04).   Skip Step 1, and the firewall setup, because that was already done with your digitalocean user data above.   When you are doing the certbot, select option 2 at the end to redirect all http traffic to https.





Also now might be a good time to do any other nginx config you want to do, like:


 
    ssh $PSVRNM
    sudo vim /etc/nginx/nginx.conf



Add this to the end of the http block:



    add_header Cache-Control "no-cache, no-store, max-age=0, must-revalidate";
    add_header X-Content-Type-Options nosniff;
    add_header X-Frame-Options SAMEORIGIN;


Next, edit your domain nginx config:


    vim /etc/nginx/sites-available/<domain>


Replace the / location section with:


    location /images/ {
     try_files $uri $uri/ =404;
    } 
    location /downloads/ {
    try_files $uri $uri/ =404;
    }
    location /css/ {
     try_files $uri $uri/ =404;
    }
    location /js/ {
     try_files $uri $uri/ =404;
    }
    location / {
     proxy_pass http://127.0.0.1:8082;
    }


Deploy your static content and your honeypot spring boot website:


    ./deploy.sh
    
    

On your server, install the service:

    ssh $PSVRNM
    sudo mv portland.service /etc/systemd/system/
    sudo systemctl enable portland
    sudo reboot 0


Take a short break, then go back in there and make sure you see port 8082 and 443 active:


    ssh $PSVRNM
    sudo netstat -tulpn
    


## Test locally


    ./run.sh
    
    
[localhost:8082](http://localhost:8082)





## Database


[Set up a mysql database](https://www.digitalocean.com/community/tutorials/how-to-install-mysql-on-ubuntu-20-04), somewhere, in these instructions the database will be hosted on the same server as the honeypot.



    sudo apt install mysql-server
    sudo mysql_secure_installation
    sudo mysql
    CREATE USER '<PDBUSR value>'@'%' IDENTIFIED BY '<PDBPWD value>';
    GRANT ALL PRIVILEGES on *.* TO '<PDBUSR value>'@'%' WITH GRANT OPTION;

verify:


    SELECT User, Host FROM mysql.user;
    exit;
    systemctl status mysql.service
    sudo mysqladmin -p -u <PDBUSR value> version
    sudo ufw allow mysql


Edit /etc/mysql/mysql.conf.d/mysqld.cnf, and update the bind address, for example to allow any host to connect:


    bind-address	= ::

